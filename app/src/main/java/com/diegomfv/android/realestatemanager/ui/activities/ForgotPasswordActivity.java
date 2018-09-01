package com.diegomfv.android.realestatemanager.ui.activities;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.ui.base.BaseActivity;
import com.google.android.gms.stats.internal.G;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Diego Fajardo on 29/08/2018.
 */
public class ForgotPasswordActivity extends BaseActivity {

    private static final String TAG = ForgotPasswordActivity.class.getSimpleName();

    private Bitmap bitmap;

    private ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        imageView = findViewById(R.id.imageView_id);

        // TODO: 31/08/2018 Probably this way is an easier way to download images
        bitmap = BitmapFactory.decodeFile(getImagesDir() + )

        Glide.with(this).load(bitmap).into(imageView);

        Glide


    }

    @SuppressLint("CheckResult")
    private void getBitmapImagesFromImagesFiles() {
        Log.d(TAG, "getBitmapImagesFromImagesFiles: called!");

        if (accessInternalStorageGranted) {

            counter = getListOfImagesRealEstateCache().size();

            for (int i = 0; i < getListOfImagesRealEstateCache().size(); i++) {


                Single.just(getInternalStorage().readFile(getTemporaryDir() + getListOfImagesRealEstateCache().get(i).getId()))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<byte[]>() {
                            @Override
                            public void onSuccess(byte[] data) {
                                Log.i(TAG, "onSuccess: called!");

                                getListOfBitmaps().add(BitmapFactory.decodeByteArray(data, 0, data.length));

                                counter--;
                                if (counter == 0) {
                                    configureRecyclerView();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());

                            }
                        });
            }
        }
    }

}
