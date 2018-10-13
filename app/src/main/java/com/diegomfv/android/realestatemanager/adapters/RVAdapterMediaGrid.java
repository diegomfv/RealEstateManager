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
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Diego Fajardo on 02/09/2018.
 */
public class RVAdapterMediaGrid extends RecyclerView.Adapter<RVAdapterMediaGrid.MyViewHolder> {

    private static final String TAG = RVAdapterListings.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Context context;
    private List<String> listOfKeys;
    private Map<String, Bitmap> bitmapCache;
    private RequestManager glide;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public RVAdapterMediaGrid(Context context, List<String> listOfKeys, Map<String, Bitmap> bitmapCache, RequestManager glide) {
        Log.d(TAG, "RVAdapterListings: called!");
        this.context = context;
        this.listOfKeys = listOfKeys;
        this.bitmapCache = bitmapCache;
        this.glide = glide;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

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
        if (listOfKeys == null) {
            return 0;
        }
        return listOfKeys.size();
    }

    /**
     * Method to update the data.
     */
    public void setDataKeys(List<String> newData) {
        this.listOfKeys = newData;
        notifyDataSetChanged();
    }

    /**
     * Method to update the Bitmap data.
     */
    public void setDataBitmapCache(Map<String, Bitmap> newData) {
        this.bitmapCache = newData;
        notifyDataSetChanged();
    }


    /**
     * Method that retrieves the key when an item is clicked
     */
    public String getKey(int position) {
        Log.d(TAG, "getKey: called!");
        return listOfKeys.get(position);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final String TAG = RVAdapterMediaHorizontalCreate.MyViewHolder.class.getSimpleName();

        @BindView(R.id.image_view_id)
        ImageView imageView;

        ////////////////////////////////////////////////////////////////////////////////////////////

        public MyViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "MyViewHolder: called!");
            ButterKnife.bind(this, itemView);
        }

        /**
         * Method that updates the item of the holder.
         */
        private void updateItem(int position) {
            Log.d(TAG, "updateItem: called!");
            loadBitmap(position, imageView);
        }

        /**
         * Method to load the image of the item.
         */
        private void loadBitmap(int position, ImageView imageView) {
            Log.d(TAG, "loadBitmap: called!");
            Log.i(TAG, "loadBitmap: bitmapCacheSize = " + bitmapCache.size());
            Log.i(TAG, "loadBitmap: listOfKeysSize = " + listOfKeys.size());

            glide.load(bitmapCache.get(listOfKeys.get(position)))
                    .into(imageView);
        }
    }
}
