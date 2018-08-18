package com.diegomfv.android.realestatemanager.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.RealEstateManagerApp;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.data.AppExecutors;
import com.diegomfv.android.realestatemanager.utils.FirebasePushIdGenerator;
import com.diegomfv.android.realestatemanager.utils.ToastHelper;

import java.io.File;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Diego Fajardo on 18/08/2018.
 */
public class AddPhotoActivity extends AppCompatActivity {

    private static final String TAG = AddPhotoActivity.class.getSimpleName();

    /////////////////////////////////

    @BindView(R.id.image_view_id)
    ImageView imageView;

    @BindView(R.id.edit_text_description_id)
    EditText editTextDescription;

    @BindView(R.id.button_go_back_id)
    Button buttonGoBack;

    @BindView(R.id.button_add_photo_id)
    Button buttonAddPhoto;

    /////////////////////////////////

    private RealEstateManagerApp app;

    private boolean accessInternalStorageGranted;

    private Unbinder unbinder;

    private HashMap<String,String> mapOfDescriptions;

    private Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called!");

        app = (RealEstateManagerApp) getApplication();

        accessInternalStorageGranted = false;

        mapOfDescriptions = new HashMap<>();

        ////////////////////////////////////////////////////////////////////////////////////////////
        setContentView(R.layout.activity_add_photo);
        unbinder = ButterKnife.bind(this);

        getDescriptionsMap();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called!");
        unbinder.unbind();
    }

    @OnClick ({R.id.button_go_back_id, R.id.button_add_photo_id})
    public void buttonclicked (View view) {
        Log.d(TAG, "buttonClicked: " + ((Button)view).getText().toString() + " clicked!");

        switch (view.getId()) {

            case R.id.button_go_back_id: {

                launchActivityWithIntentFilledWithMap();


            } break;

            case R.id.button_add_photo_id: {

                /* Generate push key */
                String pushKey = FirebasePushIdGenerator.generate();

                /* Insert the description into the map liked to the push key */
                mapOfDescriptions.put(pushKey, editTextDescription.getText().toString());

                /* Insert the image in the temporary folder linked to the key */
                configureInternalStorage(pushKey);

                /* Launch the other activity
                * */
                launchActivityWithIntentFilledWithMap();

            } break;
        }
    }


    private void getDescriptionsMap() {
        Log.d(TAG, "getDescriptionsMap: called!");

        intent = getIntent();

        if (intent != null && intent.getSerializableExtra(Constants.DESCRIPTIONS_SERIALIZABLE) != null) {
            mapOfDescriptions = (HashMap<String, String>) intent.getSerializableExtra(Constants.DESCRIPTIONS_SERIALIZABLE);
            ToastHelper.toastShort(this, mapOfDescriptions.toString());
        }
    }

    private void configureInternalStorage (String pushKey) {
        Log.d(TAG, "configureInternalStorage: called!");

        if (accessInternalStorageGranted) {

            String mainPath = app.getInternalStorage().getInternalFilesDirectory() + File.separator;
            String temporaryDir = mainPath + File.separator + Constants.TEMPORARY_DIRECTORY + File.separator;

            if (app.getInternalStorage().isDirectoryExists(temporaryDir)) {
                saveImageInInternalStorage(temporaryDir, pushKey);

            } else {
                app.getInternalStorage().createDirectory(temporaryDir);
                saveImageInInternalStorage(temporaryDir, pushKey);
            }

        } else {
            // TODO: 18/08/2018 Create a dialog asking for permissions! It should load configure internal storage again!
            ToastHelper.toastInternalStorageAccessNotGranted(this);

        }
    }

    /** This method saves the fetched image in the internal storage asynchronously
     * */
    @SuppressLint("CheckResult")
    private void saveImageInInternalStorage (String temporaryDir, String pushKey) {
        Log.d(TAG, "saveImageInInternalStorage: called!");

        if (app.getInternalStorage().isDirectoryExists(temporaryDir)) {

            final String filePath = temporaryDir + pushKey;

            imageView.setDrawingCacheEnabled(true);
            imageView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            imageView.layout(0, 0, imageView.getMeasuredWidth(), imageView.getMeasuredHeight());
            imageView.buildDrawingCache();
            final Bitmap bitmap = Bitmap.createBitmap(imageView.getDrawingCache());

            if (bitmap != null) {

                Single.just(app.getInternalStorage().createFile(filePath, bitmap))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Boolean>() {
                            @Override
                            public void onSuccess(Boolean fileIsCreated) {
                                Log.i(TAG, "onSuccess: called!");
                                launchActivityWithIntentFilledWithMap();
                            }
                            @Override
                            public void onError(Throwable e) {
                                Log.i(TAG, "onError: called!");
                                ToastHelper.toastThereWasAnError(AddPhotoActivity.this);
                            }
                        });
            }

        } else {
            Log.i(TAG, "saveImageInInternalStorage: accessInternalStorageGrantes = false");
            ToastHelper.toastShort(this, "Directory does not exist");
        }
    }

    private void launchActivityWithIntentFilledWithMap () {
        Log.d(TAG, "launchActivityWithIntentFilledWithMap: called!");
        Intent intent = new Intent(this, CreateNewListingActivity.class);
        intent.putExtra(Constants.DESCRIPTIONS_SERIALIZABLE, mapOfDescriptions);
        startActivity (intent);
    }

}
