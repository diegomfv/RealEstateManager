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

import com.bumptech.glide.RequestManager;
import com.diegomfv.android.realestatemanager.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Diego Fajardo on 19/08/2018.
 */
public class RVAdapterMediaHorizontal extends RecyclerView.Adapter<RVAdapterMediaHorizontal.MyViewHolder>{

    private static final String TAG = RVAdapterListings.class.getSimpleName();

    //////////////////////

    private Context context;
    private List<Bitmap> bitmapLists;
    private RequestManager glide;

    //////////////////////

    public RVAdapterMediaHorizontal (Context context, List<Bitmap> bitmapLists, RequestManager glide) {
        Log.d(TAG, "RVAdapterListings: called!");

        this.context = context;
        this.bitmapLists = bitmapLists;
        this.glide = glide;

    }

    ///////////////////////

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: called!");

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(
                R.layout.rv_media_item,
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

        if (bitmapLists == null) {
            return 0;
        }

        return bitmapLists.size();
    }

    /** Method to update the data
     * */
    public void setData(List<Bitmap> newData) {
        this.bitmapLists = newData;
        notifyDataSetChanged();
    }

    /** Method that retrieves a real estate in Fragment when clicked
     * */
    public Bitmap getRealEstate (int position) {
        return this.bitmapLists.get(position);
    }


    //////////////////////////

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private final String TAG = MyViewHolder.class.getSimpleName();

        @BindView(R.id.image_view_id)
        ImageView imageView;

        ////////////////////////////////////////////////////////////////////////////////////////////

        public MyViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "MyViewHolder: called!");
            ButterKnife.bind(this, itemView);
        }

        private void updateItem (int position) {
            Log.d(TAG, "updateItem: called!");
            loadImage(position);
        }

        private void loadImage (int position) {
            glide.load(bitmapLists.get(position)).into(imageView);
        }

    }
}