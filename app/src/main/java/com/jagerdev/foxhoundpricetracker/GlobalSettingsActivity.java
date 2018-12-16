package com.jagerdev.foxhoundpricetracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jagerdev.foxhoundpricetracker.utils.ServiceRunHandler;

public class GlobalSettingsActivity extends AppCompatActivity
{
       private ServiceRunHandler svcRunHandler;

       @Override
       protected void onCreate(Bundle savedInstanceState)
       {
              super.onCreate(savedInstanceState);
              setContentView(R.layout.activity_global_settings);

              svcRunHandler = ServiceRunHandler.getInstance();
       }

       @Override
       protected void onResume()
       {
              svcRunHandler.uiActivated(this.getClass().getName());
              super.onResume();
       }

       @Override
       protected void onStop()
       {
              super.onStop();
              svcRunHandler.uiDeactivated(this.getClass().getName());
       }
}
