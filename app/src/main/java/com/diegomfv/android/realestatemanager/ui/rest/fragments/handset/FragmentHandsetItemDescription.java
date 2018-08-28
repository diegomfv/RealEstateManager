package com.diegomfv.android.realestatemanager.ui.rest.fragments.handset;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.adapters.RVAdapterMediaHorizontal;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.data.entities.RealEstate;
import com.diegomfv.android.realestatemanager.ui.activities.MainActivity;
import com.diegomfv.android.realestatemanager.ui.base.BaseFragment;
import com.diegomfv.android.realestatemanager.utils.ItemClickSupport;
import com.diegomfv.android.realestatemanager.utils.ToastHelper;
import com.diegomfv.android.realestatemanager.utils.Utils;
import com.diegomfv.android.realestatemanager.viewmodel.ListingsSharedViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Diego Fajardo on 16/08/2018.
 */

// TODO: 23/08/2018 Retain the fragment!
public class FragmentHandsetItemDescription extends BaseFragment {

    private static final String TAG = FragmentHandsetItemDescription.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @BindView(R.id.recyclerView_media_id)
    RecyclerView recyclerViewMedia;

    @BindView(R.id.textView_description_id)
    TextView tvDescription;

    @BindView(R.id.textView_surface_area_id)
    TextView tvSurfaceArea;

    @BindView(R.id.textView_numberOfRooms_id)
    TextView tvNumberRooms;

    @BindView(R.id.textView_numberOfBathrooms_id)
    TextView tvNumberBathrooms;

    @BindView(R.id.textView_numberOfBedrooms_id)
    TextView tvNumberBedrooms;

    @BindView(R.id.textView_street_id)
    TextView tvStreet;

    @BindView(R.id.textView_locality_id)
    TextView tvLocality;

    @BindView(R.id.textView_city_id)
    TextView tvCity;

    @BindView(R.id.textView_postcode_id)
    TextView tvPostCode;

    @BindView(R.id.textView_price_id)
    TextView tvPrice;

    @BindView(R.id.textView_sold_id)
    TextView tvSold;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private int currency;

    private List<Bitmap> listOfBitmaps;

    private int imagesCounter;

    //RecyclerView Adapter
    private RVAdapterMediaHorizontal adapter;

    //Glide
    private RequestManager glide;

    private RealEstate realEstate;

    private Unbinder unbinder;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static FragmentHandsetItemDescription newInstance() {
        Log.d(TAG, "newInstance: called!");
        return new FragmentHandsetItemDescription();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: called!");

        if (getActivity() != null) {
            this.currency = Utils.readCurrentCurrencyShPref(getActivity());
        }

        this.imagesCounter = 0;

        ////////////////////////////////////////////////////////////////////////////////////////////
        View view = inflater.inflate(R.layout.fragment_item_description, container, false);
        this.unbinder = ButterKnife.bind(this, view);

        /* Glide configuration*/
        if (getActivity() != null) {
            this.glide = Glide.with(getActivity());
        }

        Bundle bundle = getArguments();
        if (bundle != null) {
            realEstate = bundle.getParcelable(Constants.GET_PARCELABLE);
            Log.i(TAG, "onCreateView: bundle = " + bundle);
        }

        this.configureRecyclerView();

        if (realEstate != null) {
            fillLayoutWithRealEstateInfo(realEstate);
        }

        Log.i(TAG, "onCreateView: tvSurfaceArea = " + tvSurfaceArea);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: called!");
        this.unbinder.unbind();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void fillLayoutWithRealEstateInfo(RealEstate realEstate) {
        Log.d(TAG, "fillLayoutWithRealEstateInfo: called!");
        setDescription(realEstate);
        setSurfaceArea(realEstate);
        setAddress(realEstate);
        setPrice(realEstate);
        fillMapOfBitmaps(realEstate);
    }

    private void setDescription (RealEstate realEstate) {
        Log.d(TAG, "setDescription: called!");
        tvDescription.setText(realEstate.getDescription());
    }

    private void setSurfaceArea (RealEstate realEstate) {
        Log.d(TAG, "setSurfaceArea: called!");
        tvSurfaceArea.setText(String.valueOf(realEstate.getSurfaceArea()));
    }

    private void setAddress (RealEstate realEstate) {
        Log.d(TAG, "setLocation: called!");
        tvStreet.setText(realEstate.getAddress().getStreet());
        tvLocality.setText(realEstate.getAddress().getLocality());
        tvCity.setText(realEstate.getAddress().getCity());
        tvPostCode.setText(realEstate.getAddress().getPostcode());
    }

    // TODO: 28/08/2018 Use Placeholders
    @SuppressLint("SetTextI18n")
    private void setPrice (RealEstate realEstate) {
        Log.d(TAG, "setPrice: called!");
        tvPrice.setText(
                Utils.getCurrencySymbol(currency)
                        + " "
                        + (Utils.formatToDecimals((int) Utils.getPriceAccordingToCurrency(
                                currency, realEstate.getPrice()), currency)));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private List<Bitmap> getListOfBitmaps () {
        Log.d(TAG, "getMapOfBitmaps: called!");
        if (listOfBitmaps == null) {
            return listOfBitmaps = new ArrayList<>();
        }
        return listOfBitmaps;
    }

    @SuppressLint("CheckResult")
    private void fillMapOfBitmaps(RealEstate realEstate) {
        Log.d(TAG, "fillMapOfBitmaps: called!");

        if (getActivity() != null) {

            imagesCounter = realEstate.getListOfImagesIds().size();

            for (int i = 0; i < realEstate.getListOfImagesIds().size(); i++) {

                Single.just(getInternalStorage().readFile(getImagesDir() + realEstate.getListOfImagesIds().get(i)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<byte[]>() {
                            @Override
                            public void onSuccess(byte[] data) {
                                Log.i(TAG, "onSuccess: called!");
                                Log.i(TAG, "onSuccess: data = " + data);

                                getListOfBitmaps().add(
                                        BitmapFactory.decodeByteArray(data, 0, data.length));

                                if (imagesCounter == 0) {
                                    adapter.setData(getListOfBitmaps());
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());

                            }
                        });
            }

        } else {
            Log.i(TAG, "fillMapOfBitmaps: We are here!");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void configureRecyclerView() {
        Log.d(TAG, "configureRecyclerView: called!");
        this.recyclerViewMedia.setHasFixedSize(true);
        this.recyclerViewMedia.setLayoutManager(new LinearLayoutManager(
              getActivity(), LinearLayoutManager.HORIZONTAL, false));
        this.adapter = new RVAdapterMediaHorizontal(
                getActivity(),
                getListOfBitmaps(),
                glide);
        this.recyclerViewMedia.setAdapter(this.adapter);

        this.configureOnClickRecyclerView();
    }

    private void configureOnClickRecyclerView () {
        Log.d(TAG, "configureOnClickRecyclerView: called!");

        ItemClickSupport.addTo(recyclerViewMedia)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerViewMedia, int position, View v) {
                        Log.d(TAG, "onItemClicked: item(" + position + ") clicked!");
                        // TODO: 27/08/2018
                        ToastHelper.toastShort(getActivity(), "Delete this!");
                    }
                });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////


}
