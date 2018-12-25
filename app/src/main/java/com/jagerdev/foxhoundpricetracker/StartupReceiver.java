package com.jagerdev.foxhoundpricetracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class StartupReceiver extends BroadcastReceiver
{
       private static final String TAG_BOOT_BROADCAST_RECEIVER = "PRICETRACKER_BOOT_BROADCAST_RECEIVER";

       @Override
       public void onReceive(Context context, Intent intent)
       {
              Toast.makeText(context, "Starting PriceTracker service", Toast.LENGTH_SHORT).show();

              String action = intent.getAction();
              if(Intent.ACTION_BOOT_COMPLETED.equals(action))
              {
                     Intent svcIntent = new Intent(context, TrackerService.class);
                     svcIntent.putExtra(TrackerService.START_IN_FOREGROUND, true);
                     context.startService(svcIntent);
              }
       }


}
