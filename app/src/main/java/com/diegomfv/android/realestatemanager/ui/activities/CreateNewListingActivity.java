package com.diegomfv.android.realestatemanager.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.utils.ToastHelper;

import java.io.FileNotFoundException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Diego Fajardo on 18/08/2018.
 */

// TODO: 18/08/2018 Add a notification insertion completes!
public class CreateNewListingActivity extends AppCompatActivity {

    private static final String TAG = CreateNewListingActivity.class.getSimpleName();

    /////////////////////////////////

    @BindView(R.id.text_input_ac_text_view_type_of_bulding_id)
    TextView tvTypeOfBuilding;

    @BindView(R.id.text_input_ac_text_view_price_id)
    TextView tvPrice;

    @BindView(R.id.text_input_ac_text_view_surface_area_id)
    TextView tvSurfaceArea;

    @BindView(R.id.text_input_ac_text_view_number_of_rooms_id)
    TextView tvNumberOfRooms;

    @BindView(R.id.text_input_ac_text_view_description_id)
    TextView tvDescription;

    @BindView(R.id.text_input_layout_address_id)
    TextView tvAddress;

    @BindView(R.id.recyclerView_media_id)
    RecyclerView recyclerView;

    @BindView(R.id.button_add_photo_id)
    Button buttonAddPhoto;

    @BindView(R.id.button_insert_listing_id)
    Button buttonInsertListing;

    /////////////////////////////////

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called!");

        //////////////////////////////////////////////////////////
        setContentView(R.layout.activity_create_new_listing);
        setTitle("Create a new Listing");


    }

    @OnClick ({R.id.button_add_photo_id, R.id.button_insert_listing_id})
    public void buttonClicked (View view) {
        Log.d(TAG, "buttonClicked: " + ((Button)view).getText().toString() + " clicked!");

        switch (view.getId()) {

            case R.id.button_add_photo_id: {

                ToastHelper.toastShort(this, "Adding photo");


            } break;

            case R.id.button_insert_listing_id: {

                ToastHelper.toastShort(this, "Inserting Listing");

                // TODO: 18/08/2018 Do all the checks

            } break;

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called!");

//        if (resultCode == RESULT_CODE) {
//
//            /* We get the image data and update inputStreamSelectedImage variable which will be
//             * used later if the user decides to save this image in his/her profile
//             * */
//            final Uri imageUri = data.getData();
//            InputStream inputStreamSelectedImage = null;
//            try {
//                inputStreamSelectedImage = getContentResolver().openInputStream(imageUri);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//
//            final Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStreamSelectedImage);
//
//
//        }




    }
}
