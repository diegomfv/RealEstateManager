package com.diegomfv.android.realestatemanager.ui.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.RealEstateManagerApp;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.data.AppDatabase;
import com.diegomfv.android.realestatemanager.data.entities.ImageRealEstate;
import com.diegomfv.android.realestatemanager.data.entities.RealEstate;
import com.diegomfv.android.realestatemanager.ui.base.BaseActivity;
import com.diegomfv.android.realestatemanager.utils.FirebasePushIdGenerator;
import com.diegomfv.android.realestatemanager.utils.ToastHelper;
import com.diegomfv.android.realestatemanager.utils.Utils;
import com.snatik.storage.Storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Diego Fajardo on 18/08/2018.
 */
public class AddPhotoActivity extends BaseActivity {

    private static final String TAG = AddPhotoActivity.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @BindView(R.id.image_view_id)
    ImageView imageView;

    @BindView(R.id.edit_text_description_id)
    EditText editTextDescription;

    @BindView(R.id.button_go_back_id)
    Button buttonGoBack;

    @BindView(R.id.button_add_photo_id)
    Button buttonAddPhoto;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private boolean accessInternalStorageGranted;

    private Unbinder unbinder;

    private ImageRealEstate imageRealEstateCache;

    private RequestManager glide;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called!");

        this.accessInternalStorageGranted = false;

        this.glide = Glide.with(this);

        ////////////////////////////////////////////////////////////////////////////////////////////
        setContentView(R.layout.activity_add_photo);
        unbinder = ButterKnife.bind(this);

        this.configureImageViewOnClickListener();

        this.checkInternalStoragePermissionGranted();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called!");
        imageView.setOnClickListener(null);
        unbinder.unbind();

    }

    @OnClick ({R.id.image_view_id, R.id.button_go_back_id, R.id.button_add_photo_id})
    public void buttonClicked (View view) {
        Log.d(TAG, "buttonClicked: " + ((Button)view).getText().toString() + " clicked!");

        switch (view.getId()) {

            case R.id.button_go_back_id: {
                Utils.launchActivity(this, CreateNewListingActivity.class);

            } break;

            case R.id.button_add_photo_id: {
                if (editTextDescription.getText().toString().trim().length() < 10) {
                    ToastHelper.toastShort(this, "The description is too short");

                } else {
                    addPhoto();
                }

            } break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called!");

        if (requestCode == Constants.REQUEST_CODE_GALLERY) {

            try {
                final Uri imageUri = data.getData();
                if (imageUri != null) {
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    imageView.setImageBitmap(selectedImage);
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                ToastHelper.toastShort(AddPhotoActivity.this, getResources().getString(R.string.there_was_an_error));
            }

        } else {
            ToastHelper.toastShort(AddPhotoActivity.this, getResources().getString(R.string.no_image_was_picked));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: called!");

        switch (requestCode) {

            case Constants.REQUEST_CODE_WRITE_EXTERNAL_STORAGE: {

                if (grantResults.length > 0 && grantResults[0] != -1) {
                    accessInternalStorageGranted = true;
                    launchGallery();
                }
            }
            break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void checkInternalStoragePermissionGranted() {
        Log.d(TAG, "checkInternalStoragePermissionGranted: called!");

        if (Utils.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            accessInternalStorageGranted = true;
            launchGallery();

        } else {
            Utils.requestPermission(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void configureImageViewOnClickListener () {
        Log.d(TAG, "configureImageViewOnClickListener: called!");
        imageView.setOnClickListener(imageViewOnClickListener);

    }

    private View.OnClickListener imageViewOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick: called!");
            launchGallery();

        }
    };

    private void launchGallery () {
        Log.d(TAG, "launchGallery: called!");

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,Constants.REQUEST_CODE_GALLERY);

    }

    private void addPhoto () {
        Log.d(TAG, "addPhoto: called!");

        /* Generate push key and add to the image
        * */
        imageRealEstateCache = new ImageRealEstate(
                FirebasePushIdGenerator.generate(),
                editTextDescription.getText().toString().trim());

        /* Insert the image in the temporary folder linked to the key.
         * At the end of this process, launch CreateNewListingActivity
         * */
        configureInternalStorage();

    }

    private void configureInternalStorage () {
        Log.d(TAG, "configureInternalStorage: called!");

        if (accessInternalStorageGranted) {

            String mainPath = getInternalStorage().getInternalFilesDirectory() + File.separator;
            String temporaryDir = mainPath + File.separator + Constants.TEMPORARY_DIRECTORY + File.separator;

            if (getInternalStorage().isDirectoryExists(temporaryDir)) {
                saveImageInInternalStorage(temporaryDir, imageRealEstateCache.getId());

            } else {
                getInternalStorage().createDirectory(temporaryDir);
                saveImageInInternalStorage(temporaryDir, imageRealEstateCache.getId());
            }

        } else {
            // TODO: 18/08/2018 Create a dialog asking for permissions! It should load configure internal storage again!
            ToastHelper.toastInternalStorageAccessNotGranted(this);

        }
    }

    /** This method saves the fetched image in the internal storage asynchronously
     * */
    @SuppressLint("CheckResult")
    private void saveImageInInternalStorage (String temporaryDir, String imageId) {
        Log.d(TAG, "saveImageInInternalStorage: called!");

        if (getInternalStorage().isDirectoryExists(temporaryDir)) {

            try {

                final String filePath = temporaryDir + imageId;

                imageView.setDrawingCacheEnabled(true);
                imageView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                imageView.layout(0, 0, imageView.getMeasuredWidth(), imageView.getMeasuredHeight());
                imageView.buildDrawingCache();
                final Bitmap bitmap = Bitmap.createBitmap(imageView.getDrawingCache());

                if (bitmap != null) {
                    Single.just(getInternalStorage().createFile(filePath, bitmap))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<Boolean>() {
                                @Override
                                public void onSuccess(Boolean fileIsCreated) {
                                    Log.i(TAG, "onSuccess: called!");
                                    addImageToListOfImagesInCache();
                                    Utils.launchActivity(AddPhotoActivity.this, CreateNewListingActivity.class);
                                }
                                @Override
                                public void onError(Throwable e) {
                                    Log.i(TAG, "onError: called!");
                                    ToastHelper.toastThereWasAnError(AddPhotoActivity.this);
                                }
                            });

                } else {
                    ToastHelper.toastShort(this, "Bitmap was not got properly");
                    Utils.launchActivity(this, CreateNewListingActivity.class);

                }

            } catch (NullPointerException e) {
                ToastHelper.toastShort(this, "Sorry, that image cannot be added");
                glide.load(R.drawable.flat_example).into(imageView);
            }

        } else {
            Log.i(TAG, "saveImageInInternalStorage: accessInternalStorageGrantes = false");
            ToastHelper.toastShort(this, "Directory does not exist");
            Utils.launchActivity(this, CreateNewListingActivity.class);
        }
    }

    private void addImageToListOfImagesInCache() {
        Log.d(TAG, "addImageToListOfImagesInCache: called!");
        getListOfImagesRealEstateCache().add(imageRealEstateCache);
    }

}