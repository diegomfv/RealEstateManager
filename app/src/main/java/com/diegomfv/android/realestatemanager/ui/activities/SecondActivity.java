package com.diegomfv.android.realestatemanager.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.utils.ToastHelper;

public class SecondActivity extends AppCompatActivity {

    private static final String TAG = SecondActivity.class.getSimpleName();

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called!");

        context = SecondActivity.this;

        ////////////////////////////////////////////////////////////////////////////////////////////

        setContentView(R.layout.fragment_item_description);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: called!");
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: called!");

        switch (item.getItemId()) {

            case R.id.menu_add_button: {

                ToastHelper.toastMenuItemClicked(context, item);

            } break;

            case R.id.menu_change_currency_button: {

                ToastHelper.toastMenuItemClicked(context, item);

            } break;

            case R.id.menu_edit_listing_button: {

                ToastHelper.toastMenuItemClicked(context, item);

            } break;

            case R.id.menu_search_button: {

                ToastHelper.toastMenuItemClicked(context, item);

            } break;

        }

        return super.onOptionsItemSelected(item);
    }
}
