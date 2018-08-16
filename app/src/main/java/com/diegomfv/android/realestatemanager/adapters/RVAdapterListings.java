package com.diegomfv.android.realestatemanager.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Diego Fajardo on 16/08/2018.
 */
public class RVAdapterListings extends RecyclerView.Adapter<RVAdapterListings.MyViewHolder>{

    private static final String TAG = RVAdapterListings.class.getSimpleName();

    //////////////////////

    public RVAdapterListings () {



    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
