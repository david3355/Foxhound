package com.jagerdev.foxhoundpricetracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
              Log.i(TrackerService.class.getSimpleName(), "PriceTracker Service Stops! Restarting service...");
             if (!AndroidUtil.isServiceRunning(context, TrackerService.class)) context.startService(new Intent(context, TrackerService.class));
             Intent svcIntent = new Intent(context, TrackerService.class);
              svcIntent.putExtra(TrackerService.START_IN_FOREGROUND, true);
              context.startService(svcIntent);
       }
}
