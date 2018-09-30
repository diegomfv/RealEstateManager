package com.diegomfv.android.realestatemanager.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.data.DataRepository;
import com.diegomfv.android.realestatemanager.data.entities.RealEstate;
import com.diegomfv.android.realestatemanager.util.Utils;
import com.snatik.storage.Storage;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Diego Fajardo on 16/08/2018.
 */
public class RVAdapterListings extends RecyclerView.Adapter<RVAdapterListings.MyViewHolder> {

    private static final String TAG = RVAdapterListings.class.getSimpleName();

    //////////////////////

    private Context context;

    private DataRepository dataRepository;

    private Storage internalStorage;

    private String imagesDir;

    private List<RealEstate> listRealEstates;

    private RequestManager glide;

    private int currency;

    //////////////////////

    public RVAdapterListings(Context context, DataRepository dataRepository,
                             Storage storage, String imageDir,
                             List<RealEstate> listRealEstates,
                             RequestManager glide, int currency) {

        Log.d(TAG, "RVAdapterListings: called!");
        this.context = context;
        this.dataRepository = dataRepository;
        this.internalStorage = storage;
        this.imagesDir = imageDir;
        this.listRealEstates = listRealEstates;
        this.glide = glide;
        this.currency = currency;
    }

    ///////////////////////

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: called!");

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(
                R.layout.rv_list_listings_item,
                parent,
                false);

        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called!");

        holder.updateItem(position);
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: called!");

        if (listRealEstates == null) {
            return 0;
        }
        return listRealEstates.size();
    }

    /**
     * Method to update the data
     */
    public void setData(List<RealEstate> newData) {
        Log.d(TAG, "setDataKeys: called!");
        this.listRealEstates = newData;
        notifyDataSetChanged();
    }

    /**
     * Method to set the currency.
     */
    public void setCurrency(int newData) {
        Log.d(TAG, "setCurrency: called!");
        this.currency = newData;
        notifyDataSetChanged();
    }

    /**
     * Method that retrieves a real estate in Fragment when clicked
     */
    public RealEstate getRealEstate(int position) {
        return this.listRealEstates.get(position);
    }


    //////////////////////////

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final String TAG = MyViewHolder.class.getSimpleName();

        @BindView(R.id.image_view_id)
        ImageView imageView;

        @BindView(R.id.textView_type_of_building_id)
        TextView textViewBuilding;

        @BindView(R.id.surface_area_of_building_id)
        TextView textViewSurfaceArea;

        @BindView(R.id.price_of_building_id)
        TextView textViewPrice;

        @BindView(R.id.textView_sold_id)
        TextView textViewSold;

        //////////////////////////

        public MyViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "MyViewHolder: called!");
            ButterKnife.bind(this, itemView);
        }

        /**
         * Method that updates the item of the holder
         */
        private void updateItem(int position) {
            Log.d(TAG, "updateItem: called!");
            loadImage(position);
            textViewBuilding.setText(getType(position));
            textViewSurfaceArea.setText(getSurfaceArea(position));
            textViewPrice.setText(getPriceOfBuilding(position));
            setVisibilityOfSoldTextView(position);
        }

        /**
         * Method to load the image of the item.
         */
        private void loadImage(int position) {
            Log.d(TAG, "loadImage: called!");

            if (listRealEstates.get(position).getListOfImagesIds() != null
                    && listRealEstates.get(position).getListOfImagesIds().size() > 0) {
                glide.load(dataRepository.getBitmap(
                        internalStorage,
                        imagesDir,
                        listRealEstates.get(position).getListOfImagesIds().get(0)))
                        .into(imageView);

            } else {
                glide.load(R.drawable.image_not_available)
                        .into(imageView);
            }
        }

        /**
         * Method to get the Type of the real estate
         */
        private String getType(int position) {
            Log.d(TAG, "getType: called!");
            if (Utils.capitalize(listRealEstates.get(position).getType()).isEmpty()) {
                return "Type not Available";
            }
            return Utils.capitalize(listRealEstates.get(position).getType());
        }

        /**
         * Method to get the Surface Area of the real estate
         */
        private String getSurfaceArea(int position) {
            Log.d(TAG, "getSurfaceArea: called!");
            if (listRealEstates.get(position).getSurfaceArea() == 0.0f) {
                return "Sqm not available";
            }
            return String.valueOf(listRealEstates.get(position).getSurfaceArea()) + " sqm";
        }

        /**
         * Method to get the Price of the real estate
         */
        private String getPriceOfBuilding(int position) {
            Log.d(TAG, "getPriceOfBuilding: called!");
            float price = Utils.getValueAccordingToCurrency(currency, listRealEstates.get(position).getPrice());
            if (price == 0.0f) {
                return "Price not available";
            }
            return Utils.getCurrencySymbol(currency) + " " + Utils.getValueFormattedAccordingToCurrency(listRealEstates.get(position).getPrice(), currency);
        }

        /**
         * Method to set the visibility of the TextViewSold
         */
        private void setVisibilityOfSoldTextView(int position) {
            Log.d(TAG, "setVisibilityOfSoldTextView: called!");
            if (listRealEstates.get(position).getDateSale() != null) {
                textViewSold.setVisibility(View.VISIBLE);
            } else {
                textViewSold.setVisibility(View.INVISIBLE);
            }
        }
    }
}
