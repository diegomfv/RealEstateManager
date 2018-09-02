package com.diegomfv.android.realestatemanager.adapters;

import android.content.Context;
import android.graphics.Bitmap;
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
import com.diegomfv.android.realestatemanager.data.entities.RealEstate;
import com.diegomfv.android.realestatemanager.util.Utils;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Diego Fajardo on 16/08/2018.
 */
public class RVAdapterListings extends RecyclerView.Adapter<RVAdapterListings.MyViewHolder> {

    private static final String TAG = RVAdapterListings.class.getSimpleName();

    //////////////////////

    private Context context;

    private List<RealEstate> listRealEstates;

    private Map<String,Bitmap> mapOfBitmaps;

    private RequestManager glide;

    private int currency;

    //////////////////////

    public RVAdapterListings(Context context, List<RealEstate> listRealEstates,
                             Map<String,Bitmap> mapOfBitmaps,
                             RequestManager glide, int currency) {

        Log.d(TAG, "RVAdapterListings: called!");
        this.context = context;
        this.listRealEstates = listRealEstates;
        this.mapOfBitmaps = mapOfBitmaps;
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

    public void setDataBitmaps(Map<String,Bitmap> newData) {
        Log.d(TAG, "setDataBitmaps: called!");
        this.mapOfBitmaps = newData;
        notifyDataSetChanged();
    }

    public void setCurrency (int newData) {
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

        private void updateItem(int position) {
            Log.d(TAG, "updateItem: called!");
            loadImage(position);
            textViewBuilding.setText(getType(position));
            textViewSurfaceArea.setText(getSurfaceArea(position));
            textViewPrice.setText(getPriceOfBuilding(position));
            setVisibilityOfSoldTextView(position);
        }

        private void loadImage (int position) {
            Log.d(TAG, "loadImage: called!");

            for (Map.Entry<String, Bitmap> entry : mapOfBitmaps.entrySet())
            {
                Log.i(TAG, "loadImage: entry --->" + entry);
            }
            glide.load(mapOfBitmaps.get(listRealEstates.get(position).getId())).into(imageView);
        }

        private String getType(int position) {
            Log.d(TAG, "getType: called!");
            return Utils.capitalize(listRealEstates.get(position).getType());
        }

        private String getSurfaceArea(int position) {
            Log.d(TAG, "getSurfaceArea: called!");
            return String.valueOf(listRealEstates.get(position).getSurfaceArea()) + " sqm";
        }

        private String getPriceOfBuilding(int position) {
            Log.d(TAG, "getPriceOfBuilding: called!");
            int price = (int) Utils.getPriceAccordingToCurrency(currency, listRealEstates.get(position).getPrice());
            return Utils.getCurrencySymbol(currency) + " " + Utils.formatToDecimals(price, currency);
        }

        private void setVisibilityOfSoldTextView (int position) {
            Log.d(TAG, "setVisibilityOfSoldTextView: called!");
            if (listRealEstates.get(position).getDateSale() != null) {
                textViewSold.setVisibility(View.VISIBLE);
            } else {
                textViewSold.setVisibility(View.INVISIBLE);
            }
        }
    }
}
