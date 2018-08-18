package com.diegomfv.android.realestatemanager.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.diegomfv.android.realestatemanager.R;

import butterknife.BindView;

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

}
