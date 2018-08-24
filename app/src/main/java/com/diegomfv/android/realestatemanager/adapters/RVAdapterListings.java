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
import com.diegomfv.android.realestatemanager.data.entities.RealEstate;
import com.diegomfv.android.realestatemanager.utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Diego Fajardo on 16/08/2018.
 */
public class RVAdapterListings extends RecyclerView.Adapter<RVAdapterListings.MyViewHolder>{

    private static final String TAG = RVAdapterListings.class.getSimpleName();

    //////////////////////

    private Context context;
    private List<RealEstate> realEstates;
    private RequestManager glide;

    //////////////////////

    public RVAdapterListings (Context context, RequestManager glide) {
        Log.d(TAG, "RVAdapterListings: called!");
        this.context = context;
        this.glide = glide;

    }

    ///////////////////////

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: called!");

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(
                R.layout.fragment_list_listings_item,
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

        if (realEstates == null) {
            return 0;
        }

        return realEstates.size();
    }

    /** Method to update the data
     * */
    public void setData(List<RealEstate> newData) {
        this.realEstates = newData;
        notifyDataSetChanged();
    }

    /** Method that retrieves a real estate in Fragment when clicked
     * */
    public RealEstate getRealEstate (int position) {
        return this.realEstates.get(position);
    }


    //////////////////////////

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private final String TAG = MyViewHolder.class.getSimpleName();

        @BindView(R.id.image_view_id)
        ImageView imageView;

        @BindView(R.id.type_of_building_id)
        TextView textViewBuilding;

        @BindView(R.id.surface_area_of_building_id)
        TextView textViewSurfaceArea;

        @BindView(R.id.price_of_building_id)
        TextView textViewPrice;

        //////////////////////////

        public MyViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "MyViewHolder: called!");
            ButterKnife.bind(this, itemView);
        }

        private void updateItem (int position) {
            Log.d(TAG, "updateItem: called!");
            textViewBuilding.setText(getType(position));
            textViewSurfaceArea.setText(getSurfaceArea(position));
            textViewPrice.setText(getPriceOfBuilding(position));
        }

        private String getType (int position) {
            Log.d(TAG, "getType: called!");
            return Utils.capitalize(realEstates.get(position).getType());
        }

        private String getSurfaceArea (int position) {
            Log.d(TAG, "getSurfaceArea: called!");
            return String.valueOf(realEstates.get(position).getSurfaceArea()) + " sqm";
        }

        private String getPriceOfBuilding (int position) {
            Log.d(TAG, "getPriceOfBuilding: called!");

            // TODO: 24/08/2018 Change the currency value to something modificable
            return "$ " + Utils.formatToDecimals(realEstates.get(position).getPrice(), 0);
        }

    }
}
