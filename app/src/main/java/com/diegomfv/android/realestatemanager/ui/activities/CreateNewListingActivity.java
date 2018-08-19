package com.diegomfv.android.realestatemanager.ui.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.RealEstateManagerApp;
import com.diegomfv.android.realestatemanager.adapters.RVAdapterMediaHorizontal;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.utils.ItemClickSupport;
import com.diegomfv.android.realestatemanager.utils.TextInputAutoCompleteTextView;
import com.diegomfv.android.realestatemanager.utils.ToastHelper;
import com.diegomfv.android.realestatemanager.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

// TODO: 18/08/2018 Add a notification insertion completes!
public class CreateNewListingActivity extends AppCompatActivity {

    private static final String TAG = CreateNewListingActivity.class.getSimpleName();

    private RealEstateManagerApp app;

    /////////////////////////////////

    @BindView(R.id.text_input_ac_text_view_type_of_bulding_id)
    TextInputAutoCompleteTextView tvTypeOfBuilding;

    @BindView(R.id.text_input_ac_text_view_price_id)
    TextInputAutoCompleteTextView tvPrice;

    @BindView(R.id.text_input_ac_text_view_surface_area_id)
    TextInputAutoCompleteTextView tvSurfaceArea;

    @BindView(R.id.text_input_ac_text_view_number_of_rooms_id)
    TextInputAutoCompleteTextView tvNumberOfRooms;

    @BindView(R.id.text_input_ac_text_view_description_id)
    TextInputAutoCompleteTextView tvDescription;

    @BindView(R.id.text_input_ac_text_view_address_id)
    TextInputAutoCompleteTextView tvAddress;

    @BindView(R.id.recyclerView_media_id)
    RecyclerView recyclerView;

    @BindView(R.id.button_add_photo_id)
    Button buttonAddPhoto;

    @BindView(R.id.button_insert_listing_id)
    Button buttonInsertListing;

    /////////////////////////////////

    //RecyclerView Adapter
    private RVAdapterMediaHorizontal adapter;

    private RequestManager glide;

    private boolean accessInternalStorageGranted;

    int counter;

    private Unbinder unbinder;

    private List<Bitmap> listOfBitmaps;

    private HashMap<String,String> mapOfDescriptions;

    private Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called!");

        app = (RealEstateManagerApp) getApplication();

        this.mapOfDescriptions = new HashMap<>();
        this.listOfBitmaps = new ArrayList<>();

        accessInternalStorageGranted = false;

        counter = 0;

        ////////////////////////////////////////////////////////////////////////////////////////////
        setContentView(R.layout.activity_create_new_listing);
        setTitle("Create a new Listing");
        this.unbinder = ButterKnife.bind(this);

        glide = Glide.with(CreateNewListingActivity.this);

        this.checkInternalStoragePermissionGranted();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called!");
        unbinder.unbind();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: called!");
        // TODO: 19/08/2018 Might need to change this

        // TODO: 19/08/2018 Add a fragment saying, would you like to delete the media?
        // TODO: 19/08/2018 If yes, clean the list and the HashMap 
        //do nothing
    }

    @OnClick ({R.id.button_add_photo_id, R.id.button_go_back_id, R.id.button_insert_listing_id})
    public void buttonClicked (View view) {
        Log.d(TAG, "buttonClicked: " + ((Button)view).getText().toString() + " clicked!");

        switch (view.getId()) {

            case R.id.button_add_photo_id: {

                launchActivityWithIntentFilledWithMap();

            } break;

            case R.id.button_go_back_id: {

                Utils.launchActivity(this, MainActivity.class);

            }

            case R.id.button_insert_listing_id: {
                ToastHelper.toastShort(this, "Inserting Listing");
                // TODO: 18/08/2018 Do all the checks
                // TODO: 19/08/2018 REMEMBER NOTIFICATION!

            } break;
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
                    getBitmapsListAndDescriptionsMap();
                }
            }
            break;
        }

    }

    private void checkInternalStoragePermissionGranted() {
        Log.d(TAG, "checkInternalStoragePermissionGranted: called!");

        if (Utils.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            accessInternalStorageGranted = true;
            getBitmapsListAndDescriptionsMap();
        } else {
            Utils.requestPermission(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
        }
    }

    /** Android - How to pass HashMap<String,String> between activities?
     * Use putExtra(String key, Serializable obj) to insert the HashMap and
     * on the other Activity use getIntent().getSerializableExtra(String key).
     * You will need to Cast the return value as a HashMap though.*/

    private void getBitmapsListAndDescriptionsMap() {
        Log.d(TAG, "getBitmapsListAndDescriptionsMap: called!");

        intent = getIntent();

        if (intent != null && intent.getSerializableExtra(Constants.DESCRIPTIONS_SERIALIZABLE) != null) {
            mapOfDescriptions = (HashMap<String, String>) intent.getSerializableExtra(Constants.DESCRIPTIONS_SERIALIZABLE);

            // TODO: 19/08/2018 Delete!
            ToastHelper.toastShort(this, mapOfDescriptions.toString());

            getBitmapImagesFromTemporaryFiles();

        }
    }

    @SuppressLint("CheckResult")
    private void getBitmapImagesFromTemporaryFiles() {
        Log.d(TAG, "getBitmapImagesFromTemporaryFiles: called!");

        if (accessInternalStorageGranted) {

            String mainPath = app.getInternalStorage().getInternalFilesDirectory() + File.separator;
            String temporaryDir = mainPath + File.separator + Constants.TEMPORARY_DIRECTORY + File.separator;

            for (Map.Entry<String,String> entry:
                    mapOfDescriptions.entrySet()) {

                counter = mapOfDescriptions.size();

                Single.just(app.getInternalStorage().readFile(temporaryDir + entry.getKey()))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<byte[]>() {
                            @Override
                            public void onSuccess(byte[] data) {
                                Log.i(TAG, "onSuccess: called!");

                                listOfBitmaps.add(BitmapFactory.decodeByteArray(data, 0 , data.length));
                                counter--;

                                if (counter == 0 && listOfBitmaps != null) {
                                    configureRecyclerView();
                                }

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.i(TAG, "getBitmapImagesFromTemporaryFiles: called!");

                            }
                        });

            }
        }
    }

    private void configureRecyclerView() {
        Log.d(TAG, "configureRecyclerView: called!");

        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false));
        this.adapter = new RVAdapterMediaHorizontal(
                this,
                listOfBitmaps,
                glide);
        this.recyclerView.setAdapter(this.adapter);

        this.configureOnClickRecyclerView();


    }

    private void configureOnClickRecyclerView () {
        Log.d(TAG, "configureOnClickRecyclerView: called!");

        final List<String> listOfDescriptions = new ArrayList<>(mapOfDescriptions.values());

        ItemClickSupport.addTo(recyclerView)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Log.d(TAG, "onItemClicked: item(" + position + ") clicked!");
                        ToastHelper.toastShort(CreateNewListingActivity.this, listOfDescriptions.get(position));
                    }
                });
    }

    private void launchActivityWithIntentFilledWithMap () {
        Log.d(TAG, "launchActivityWithIntentFilledWithMap: called!");

        Intent intent = new Intent(this, AddPhotoActivity.class);
        intent.putExtra(Constants.DESCRIPTIONS_SERIALIZABLE, mapOfDescriptions);
        startActivity (intent);

    }

}
