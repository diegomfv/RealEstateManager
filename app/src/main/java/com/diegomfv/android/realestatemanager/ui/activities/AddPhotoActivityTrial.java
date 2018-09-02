package com.diegomfv.android.realestatemanager.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.adapters.RVAdapterMediaGrid;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.data.entities.ImageRealEstate;
import com.diegomfv.android.realestatemanager.ui.base.BaseActivity;
import com.diegomfv.android.realestatemanager.util.FirebasePushIdGenerator;
import com.diegomfv.android.realestatemanager.util.GlideApp;
import com.diegomfv.android.realestatemanager.util.GlideRequests;
import com.diegomfv.android.realestatemanager.util.ItemClickSupport;
import com.diegomfv.android.realestatemanager.util.ToastHelper;
import com.diegomfv.android.realestatemanager.util.Utils;

import java.io.FileNotFoundException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Diego Fajardo on 02/09/2018.
 */
public class AddPhotoActivityTrial extends BaseActivity {

    private static final String TAG = AddPhotoActivityTrial.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @BindView(R.id.recyclerView_media_id)
    RecyclerView recyclerView;

    private RVAdapterMediaGrid adapter;

    @BindView(R.id.button_add_photo_id)
    Button buttonAddPhoto;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private ActionBar actionBar;

    private Unbinder unbinder;

    private ImageRealEstate imageRealEstateCache;

    private GlideRequests glide;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called!");

        this.glide = GlideApp.with(this);

        ////////////////////////////////////////////////////////////////////////////////////////////
        setContentView(R.layout.activity_add_photo_trial);
        unbinder = ButterKnife.bind(this);

        this.configureActionBar();

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
            ToastHelper.toastShort(AddPhotoActivityTrial.this, getResources().getString(R.string.no_image_was_picked));

        } else {
            if (requestCode == Constants.REQUEST_CODE_GALLERY) {

                // TODO: 02/09/2018 Decode correctly the image!

                try {
                    final Uri imageUri = data.getData();
                    if (imageUri != null) {
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        String key = FirebasePushIdGenerator.generate();
                        getListOfBitmapKeys().add(key);
                        getRepository().addBitmapToBitmapCache(key, Utils.getResizedBitmap(selectedImage, 840));
                        Log.w(TAG, "onActivityResult: " + getRepository().getCurrentSizeOfBitmapCache());
                        Log.w(TAG, "onActivityResult: " + getBitmapCache().size());
                        adapter.setDataKeys(getListOfBitmapKeys());
                        adapter.setDataBitmapCache(getBitmapCache());
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    ToastHelper.toastShort(AddPhotoActivityTrial.this, getResources().getString(R.string.there_was_an_error));
                }

            } else {
                ToastHelper.toastShort(AddPhotoActivityTrial.this, getResources().getString(R.string.no_image_was_picked));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: called!");

        switch (item.getItemId()) {

            case android.R.id.home: {
                ToastHelper.toastShort(this, "No picture added");
                Utils.launchActivity(this, CreateNewListingActivity.class);
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void configureActionBar() {
        Log.d(TAG, "configureActionBar: called!");

        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeActionContentDescription(getResources().getString(R.string.go_back));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void configureRecyclerView() {
        Log.d(TAG, "configureRecyclerView: called!");

        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new GridLayoutManager(
                this, 2));
        this.adapter = new RVAdapterMediaGrid(
                this,
                getListOfBitmapKeys(),
                getRepository().getBitmapCache(),
                getImagesDir(),
                glide);
        this.recyclerView.setAdapter(this.adapter);

        this.configureOnClickRecyclerView();

    }

    private void configureOnClickRecyclerView() {
        Log.d(TAG, "configureOnClickRecyclerView: called!");

        ItemClickSupport.addTo(recyclerView)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Log.d(TAG, "onItemClicked: item(" + position + ") clicked!");
                        ToastHelper.toastLong(AddPhotoActivityTrial.this, adapter.getKey(position));
                    }
                });
    }

    private void launchGallery() {
        Log.d(TAG, "launchGallery: called!");

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, Constants.REQUEST_CODE_GALLERY);

    }
    private void addImageToListOfImagesInCache() {
        Log.d(TAG, "addImageToListOfImagesInCache: called!");
        getListOfImagesRealEstateCache().add(imageRealEstateCache);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /** This method uses inSampleSize to reduce the size of the image in memory
     * */
    private Bitmap decodeSampleBitmapFromInputStream (InputStream stream, int reqHeight, int reqWidth) {
        Log.d(TAG, "decodeSampleBitmapFromFile: called!");

        //Height and Width in pixels
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(stream, null, options);

        Log.i(TAG, "readBitmapDimensionsAndType: height = " + options.outHeight);
        Log.i(TAG, "readBitmapDimensionsAndType: width = " + options.outWidth);
        Log.i(TAG, "readBitmapDimensionsAndType: memeType= " + options.outMimeType);
        Log.i(TAG, "readBitmapDimensionsAndType: sizeInMemory = " + options.outHeight * options.outWidth * 4);

        options.inSampleSize = calculateInSampleSize(options, reqHeight, reqWidth);

        options.inJustDecodeBounds = false;

        Bitmap bitmap = BitmapFactory.decodeStream(stream, null, options);
        Log.i(TAG, "readBitmapDimensionsAndType: height = " + options.outHeight);
        Log.i(TAG, "readBitmapDimensionsAndType: width = " + options.outWidth);
        Log.i(TAG, "readBitmapDimensionsAndType: memeType= " + options.outMimeType);
        Log.i(TAG, "readBitmapDimensionsAndType: sizeInMemory = " + options.outHeight * options.outWidth * 4);
        Log.i(TAG, "readBitmapDimensionsAndType: bitmap size() = " + bitmap.getByteCount());

        return bitmap;

    }

    //Can be static
    /** This method checks the current size of the image and, if the required size is still
     * higher, it continues reducing the image
     * */
    private int calculateInSampleSize(BitmapFactory.Options options, int reqHeight, int reqWidth) {
        Log.d(TAG, "calculateInSampleSize: called!");

        //Height and Width in pixels
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        Log.d(TAG, "calculateInSampleSize: height = " + height);
        Log.d(TAG, "calculateInSampleSize: reqHeight = " + reqHeight);
        Log.d(TAG, "calculateInSampleSize: width = " + width);
        Log.d(TAG, "calculateInSampleSize: reqWidth = " + reqWidth);

        if (height > reqHeight || width > reqWidth) {

            int halfHeight = height / 2;
            int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {

                Log.i(TAG, "calculateInSampleSize: in while loop...");

                // 2 --> check inSampleSize docs
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

}