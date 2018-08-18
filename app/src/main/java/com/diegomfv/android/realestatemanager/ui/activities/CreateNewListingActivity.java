package com.diegomfv.android.realestatemanager.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.utils.TextInputAutoCompleteTextView;
import com.diegomfv.android.realestatemanager.utils.ToastHelper;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Diego Fajardo on 18/08/2018.
 */

// TODO: 18/08/2018 Add a notification insertion completes!
public class CreateNewListingActivity extends AppCompatActivity {

    private static final String TAG = CreateNewListingActivity.class.getSimpleName();

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

    private Unbinder unbinder;

    private HashMap<String,String> mapOfDescriptions;

    private Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called!");

        ////////////////////////////////////////////////////////////////////////////////////////////
        setContentView(R.layout.activity_create_new_listing);
        setTitle("Create a new Listing");
        unbinder = ButterKnife.bind(this);

        mapOfDescriptions = new HashMap<>();

        getDescriptionsMap();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called!");
        unbinder.unbind();
    }

    @OnClick ({R.id.button_add_photo_id, R.id.button_insert_listing_id})
    public void buttonClicked (View view) {
        Log.d(TAG, "buttonClicked: " + ((Button)view).getText().toString() + " clicked!");

        switch (view.getId()) {

            case R.id.button_add_photo_id: {

                launchActivityWithIntentFilledWithMap();


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


    /** Android - How to pass HashMap<String,String> between activities?
     * Use putExtra(String key, Serializable obj) to insert the HashMap and
     * on the other Activity use getIntent().getSerializableExtra(String key).
     * You will need to Cast the return value as a HashMap though.*/

    private void getDescriptionsMap() {
        Log.d(TAG, "getDescriptionsMap: called!");

        intent = getIntent();

        if (intent != null && intent.getSerializableExtra(Constants.DESCRIPTIONS_SERIALIZABLE) != null) {
            mapOfDescriptions = (HashMap<String, String>) intent.getSerializableExtra(Constants.DESCRIPTIONS_SERIALIZABLE);

            ToastHelper.toastShort(this, mapOfDescriptions.toString());

        }


    }

    private void launchActivityWithIntentFilledWithMap () {
        Log.d(TAG, "launchActivityWithIntentFilledWithMap: called!");

        Intent intent = new Intent(this, AddPhotoActivity.class);
        intent.putExtra(Constants.DESCRIPTIONS_SERIALIZABLE, mapOfDescriptions);
        startActivity (intent);

    }


}
