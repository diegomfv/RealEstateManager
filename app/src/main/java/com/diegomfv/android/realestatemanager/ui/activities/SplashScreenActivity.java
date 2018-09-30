package com.diegomfv.android.realestatemanager.ui.activities;

import android.content.Intent;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;

import com.diegomfv.android.realestatemanager.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.diegomfv.android.realestatemanager.util.Utils.calculationRectOnScreen;

/**
 * Created by Diego Fajardo (https://github.com/diegomfv) on 08/09/2018.
 */
public class SplashScreenActivity extends AppCompatActivity {

    private static final String TAG = SplashScreenActivity.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @BindView(R.id.main_layout_id)
    ViewGroup mainLayout;

    @BindView(R.id.textView_title1_id)
    View textView1;

    @BindView(R.id.textView_title2_id)
    View textView2;

    @BindView(R.id.textView_title3_id)
    View textView3;

    @BindView(R.id.frame_layout_center_id)
    View frameCenter;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Unbinder unbinder;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called!");

        ////////////////////////////////////////////////////////////////////////////////////////////
        setContentView(R.layout.activity_splash_screen);
        unbinder = ButterKnife.bind(this);

        /* We hide the status bar
         * */
        this.hideStatusBar();

        /* We start the animation
         * */
        this.initSplashScreenAnimation();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called!");
        unbinder.unbind();
    }

    /**
     * Method to hide the status bar
     */
    private void hideStatusBar() {
        Log.d(TAG, "hideStatusBar: called!");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    /**
     * Method that launches AuthLoginActivity
     */
    private void launchAuthLoginActivity() {
        Log.d(TAG, "launchAuthLoginActivity: called!");

        Intent intent = new Intent(this, AuthLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * Method that starts the intro animation
     */
    private void initSplashScreenAnimation() {
        Log.d(TAG, "initSplashScreenAnimation: called!");

        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d(TAG, "onGlobalLayout: called!");

                RectF frameRect = calculationRectOnScreen(frameCenter);
                RectF tv1 = calculationRectOnScreen(textView1);
                RectF tv2 = calculationRectOnScreen(textView2);
                RectF tv3 = calculationRectOnScreen(textView3);

                float distance1 = Math.abs(tv1.bottom - frameRect.top);
                float distance2 = Math.abs(tv3.top - tv2.bottom);

                textView1.animate().alpha(1).translationY(distance1 + 100).setDuration(2000).start();
                textView3.animate().alpha(1).translationY(-distance2).setDuration(2000).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "run: called!");
                        textView2.animate().alpha(1).setDuration(1500).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "run: called!");

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        launchAuthLoginActivity();
                                    }
                                }, 2000);
                            }
                        }).start();
                    }
                }).start();
            }
        });
    }
}
