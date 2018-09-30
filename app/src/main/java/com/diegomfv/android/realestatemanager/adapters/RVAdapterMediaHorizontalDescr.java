package com.diegomfv.android.realestatemanager.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.RequestManager;
import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.data.DataRepository;
import com.diegomfv.android.realestatemanager.data.entities.RealEstate;
import com.snatik.storage.Storage;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Diego Fajardo on 19/08/2018.
 */
public class RVAdapterMediaHorizontalDescr extends RecyclerView.Adapter<RVAdapterMediaHorizontalDescr.MyViewHolder>{

    private static final String TAG = RVAdapterListings.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Context context;

    private DataRepository dataRepository;

    private Storage internalStorage;

    private String imagesDir;

    private RealEstate realEstate;

    private RequestManager glide;

    private int currency;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public RVAdapterMediaHorizontalDescr (Context context, DataRepository dataRepository,
                                     Storage storage, String imageDir,
                                     RealEstate realEstate,
                                     RequestManager glide, int currency) {

        Log.d(TAG, "RVAdapterMediaHorizontal: called!");
        this.context = context;
        this.dataRepository = dataRepository;
        this.internalStorage = storage;
        this.imagesDir = imageDir;
        this.realEstate = realEstate;
        this.glide = glide;
        this.currency = currency;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @NonNull
    @Override
    public RVAdapterMediaHorizontalDescr.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: called!");

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(
                R.layout.rv_media_item,
                parent,
                false);

        return new RVAdapterMediaHorizontalDescr.MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RVAdapterMediaHorizontalDescr.MyViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called!");

        holder.updateItem(position);

    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: called!");

        if (realEstate.getListOfImagesIds() == null) {
            return 0;
        }

        return realEstate.getListOfImagesIds().size();
    }

    /** Method that retrieves the key when an item is clicked
     * */
    public String getKey (int position) {
        Log.d(TAG, "getKey: called!");
        return realEstate.getListOfImagesIds().get(position);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private final String TAG = RVAdapterMediaHorizontalDescr.MyViewHolder.class.getSimpleName();

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
        private void updateItem (int position) {
            Log.d(TAG, "updateItem: called!");
            loadBitmap(position, imageView);
        }

        /**
         * Method to load the image of the item.
         */
        private void loadBitmap (int position, ImageView imageView) {
            Log.d(TAG, "loadBitmap: called!");

            glide.load(dataRepository.getBitmap(
                    internalStorage,
                    imagesDir,
                    realEstate.getListOfImagesIds().get(position)))
                    .into(imageView);
        }
    }
}