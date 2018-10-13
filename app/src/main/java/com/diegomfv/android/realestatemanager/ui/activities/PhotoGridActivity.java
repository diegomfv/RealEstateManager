package com.diegomfv.android.realestatemanager.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;

import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.adapters.RVAdapterMediaGrid;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.data.entities.ImageRealEstate;
import com.diegomfv.android.realestatemanager.ui.base.BaseActivity;
import com.diegomfv.android.realestatemanager.ui.dialogfragments.InsertDescriptionDialogFragment;
import com.diegomfv.android.realestatemanager.util.FirebasePushIdGenerator;
import com.diegomfv.android.realestatemanager.util.GridSpaceItemDecoration;
import com.diegomfv.android.realestatemanager.util.ItemClickSupport;
import com.diegomfv.android.realestatemanager.util.ToastHelper;
import com.diegomfv.android.realestatemanager.util.Utils;

import java.io.FileNotFoundException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.diegomfv.android.realestatemanager.util.Utils.setOverflowButtonColor;

/**
 * Created by Diego Fajardo on 02/09/2018.
 */

public class PhotoGridActivity extends BaseActivity implements InsertDescriptionDialogFragment.InsertDescriptionDialogListener {

    private static final String TAG = PhotoGridActivity.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @BindView(R.id.toolbar_id)
    Toolbar toolbar;

    @BindView(R.id.card_view_recyclerView_media_id)
    RecyclerView recyclerView;

    private RVAdapterMediaGrid adapter;

    @BindView(R.id.button_add_photo_id)
    Button buttonAddPhoto;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Unbinder unbinder;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called!");

        ////////////////////////////////////////////////////////////////////////////////////////////
        setContentView(R.layout.activity_photo_grid);
        unbinder = ButterKnife.bind(this);

        this.configureToolBar();

        this.configureRecyclerView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called!");
        unbinder.unbind();

    }

    @OnClick(R.id.button_add_photo_id)
    public void buttonClicked(View view) {
        Log.d(TAG, "buttonClicked: " + ((Button) view).getText().toString() + " clicked!");

        switch (view.getId()) {
            case R.id.button_add_photo_id: {
                launchGallery();
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called!");

        if (data == null) {
            ToastHelper.toastShort(PhotoGridActivity.this, getResources().getString(R.string.no_image_was_picked));

        } else {
            if (requestCode == Constants.REQUEST_CODE_GALLERY) {

                try {
                    final Uri imageUri = data.getData();
                    if (imageUri != null) {

                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                        /* We generate an id (key) for the bitmap
                         * */
                        String key = FirebasePushIdGenerator.generate();

                        /* The Bitmap itself anf its information is added to the cache
                         * */
                        getListOfImagesRealEstateCache().add(new ImageRealEstate(key, ""));
                        getListOfBitmapKeys().add(key);
                        getRepository().addBitmapToBitmapCache(key, Utils.getResizedBitmap(selectedImage, 840));

                        /* We could resize the image according to the view size
                         * TODO */
                        Log.i(TAG, "onActivityResult: " + getRepository().getCurrentSizeOfBitmapCache());
                        Log.i(TAG, "onActivityResult: " + getBitmapCache().size());

                        /* The recycler view is notified in order to display the new data
                         * */
                        updateAdapterData();

                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    ToastHelper.toastShort(PhotoGridActivity.this, getResources().getString(R.string.there_was_an_error));
                }

            } else {
                ToastHelper.toastShort(PhotoGridActivity.this, getResources().getString(R.string.no_image_was_picked));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: called!");

        switch (item.getItemId()) {

            case android.R.id.home: {
                checkActivityLaunched();
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: called!");
        checkActivityLaunched();
    }

    /**
     * This callback gets triggered when the user inputs information in the dialogFragment and
     * presses the positive button.
     */
    @Override
    public void onDialogPositiveClick(ImageRealEstate imageRealEstate) {
        Log.d(TAG, "onDatePickerDialogPositiveClick: called!");
        ToastHelper.toastLong(this, imageRealEstate.getDescription());

    }

    /**
     * This callback gets triggered when the user
     * presses the negative button.
     */
    @Override
    public void onDialogNegativeClick() {
        Log.d(TAG, "onDatePickerDialogNegativeClick: called!");

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to configure the toolbar.
     */
    private void configureToolBar() {
        Log.d(TAG, "configureToolBar: called!");

        setSupportActionBar(toolbar);
        setTitle("Create a New Listing");
        setOverflowButtonColor(toolbar, Color.WHITE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: called!");
                onBackPressed();
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to determine which activity we launch. It depends on the activity we came from
     * (CreateNewListingActivity or EditListingActivity).
     */
    private void checkActivityLaunched() {
        Log.d(TAG, "checkActivityLaunched: called!");

        if (getIntent() != null && getIntent().getExtras() != null) {

            String fromIntent = getIntent().getStringExtra(Constants.INTENT_FROM_ACTIVITY);

            if (fromIntent.equals(Constants.INTENT_FROM_CREATE)) {
                Intent intent = new Intent(this, CreateNewListingActivity.class);
                intent.putExtra(Constants.INTENT_FROM_PHOTO_GRID_ACTIVITY, Constants.STRING_FROM_PHOTO_GRID_ACTIVITY);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, EditListingActivity.class);
                startActivity(intent);
            }
        }
    }

    /**
     * Method to configure the RecyclerView.
     */
    private void configureRecyclerView() {
        Log.d(TAG, "configureRecyclerView: called!");

        int spanCount = 2; // 2 columns
        int spacing = 50; // 50px
        boolean includeEdge = true; //Includes spaces on the sides

        /* We are using a GridRecyclerView (custom view). See layout
         * */
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new GridLayoutManager(
                this, spanCount));
        this.adapter = new RVAdapterMediaGrid(
                this,
                getListOfBitmapKeys(),
                getRepository().getBitmapCache(),
                getGlide());

        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, R.anim.grid_layout_animation_from_bottom);
        recyclerView.setLayoutAnimation(animation);

        recyclerView.addItemDecoration(new GridSpaceItemDecoration(spanCount, spacing, includeEdge));

        this.recyclerView.setAdapter(this.adapter);

        this.configureOnClickRecyclerView();

    }

    /**
     * Method to configure the onClick listeners of the RecyclerView.
     */
    private void configureOnClickRecyclerView() {
        Log.d(TAG, "configureOnClickRecyclerView: called!");
        ItemClickSupport.addTo(recyclerView)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Log.d(TAG, "onItemClicked: item(" + position + ") clicked!");

                        String key = getListOfBitmapKeys().get(position);

                        for (int i = 0; i < getListOfImagesRealEstateCache().size(); i++) {

                            if (getListOfImagesRealEstateCache().get(i).getId().equals(key)) {
                                launchAddDescriptionDialog(getListOfImagesRealEstateCache().get(i));
                                break;
                            }
                        }
                    }
                });
    }

    /**
     * Method to update the data in the RecyclerView.
     */
    private void updateAdapterData() {
        Log.d(TAG, "updateAdapterData: called!");
        adapter.setDataKeys(getListOfBitmapKeys());
        adapter.setDataBitmapCache(getBitmapCache());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to launch the gallery so the user can choose a picture
     */
    private void launchGallery() {
        Log.d(TAG, "launchGallery: called!");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, Constants.REQUEST_CODE_GALLERY);

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method that launches AddDescriptionDialog which allows the user to enter information
     * about the picture
     */
    private void launchAddDescriptionDialog(ImageRealEstate imageRealEstate) {
        Log.d(TAG, "launchAddDescriptionDialog: called!");
        InsertDescriptionDialogFragment.newInstance(imageRealEstate)
                .show(getSupportFragmentManager(), "InsertDescriptionDialogFragment");
    }
}