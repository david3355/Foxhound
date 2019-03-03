package com.jagerdev.foxhoundpricetracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import utility.logger.CommonLogger;
import utility.logger.PriceTrackerLogger;

public class StartupReceiver extends BroadcastReceiver
{
       private static final String TAG_BOOT_BROADCAST_RECEIVER = "PRICETRACKER_BOOT_BROADCAST_RECEIVER";
       private static CommonLogger logger;

       public StartupReceiver()
       {
              logger = PriceTrackerLogger.getNewLogger(this.getClass().getName());
       }

       @Override
       public void onReceive(Context context, Intent intent)
       {
              String action = intent.getAction();
              if(Intent.ACTION_BOOT_COMPLETED.equals(action))
              {
                     Toast.makeText(context, "Starting PriceTracker service", Toast.LENGTH_SHORT).show();
                     logger.info("Starting Foxhound after system start");
                     Intent svcIntent = new Intent(context, TrackerService.class);
                     svcIntent.putExtra(TrackerService.START_IN_FOREGROUND, true);
                     context.startService(svcIntent);
                     logger.info("Foxhound started after system start");
              }
       }


}
