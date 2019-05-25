package com.jagerdev.foxhoundpricetracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.jagerdev.foxhoundpricetracker.utils.AndroidUtil;

import utility.logger.CommonLogger;
import utility.logger.PriceTrackerLogger;

public class TrackerLastScreamReceiver extends BroadcastReceiver
{

       public TrackerLastScreamReceiver()
       {
              logger = PriceTrackerLogger.getNewLogger(this.getClass().getName());
       }

       private CommonLogger logger;

       @Override
       public void onReceive(Context context, Intent intent)
       {
             logger.info("Received that PriceTracker Service is stopped! Restarting service...");
             Log.i(TrackerService.class.getSimpleName(), "PriceTracker Service is stopped! Restarting service...");
             if (!AndroidUtil.isServiceRunning(context, TrackerService.class))
             {
                    Intent svcIntent = new Intent(context, TrackerService.class);
                    svcIntent.putExtra(TrackerService.START_IN_FOREGROUND, true);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    {
                           context.startForegroundService(svcIntent);
                    } else context.startService(svcIntent);
                    logger.info("Foxhound started due to receiving last scream");
             }
       }
}
