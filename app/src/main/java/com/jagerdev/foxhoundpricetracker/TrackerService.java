package com.jagerdev.foxhoundpricetracker;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.jagerdev.foxhoundpricetracker.utils.NotificationHelper;
import com.jagerdev.foxhoundpricetracker.utils.TrackerInitializer;

import model.Product;
import tracker.PriceTrackerService;
import tracker.clientnotifier.PriceTrackEvent;
import utility.logger.CommonLogger;
import utility.logger.PriceTrackerLogger;

public class TrackerService extends Service implements PriceTrackEvent, Runnable
{
       public TrackerService()
       {
              logger = PriceTrackerLogger.getNewLogger(this.getClass().getName());
              notificationHelper = new NotificationHelper(this);
       }

       private CommonLogger logger;
       private NotificationHelper notificationHelper;
       private PriceTrackerService priceTrackerSvc;
       private static boolean running = false;
       public static boolean forceStopped = false;

       private final IBinder serviceBinder = new TrackerServiceBinder();

       private static final int NOTIFICATION_ID = 10;
       public static final String START_IN_FOREGROUND = "start_in_foreground";

       public class TrackerServiceBinder extends Binder
       {
              TrackerService getService()
              {
                     return TrackerService.this;
              }
       }

       public static boolean isRunning()
       {
              return running;
       }

       @Override
       public int onStartCommand(Intent intent, int flags, int startId)
       {
              TrackerInitializer.initialize(this);
              logger.info("Starting PriceTracker Android service");
              Thread starter = new Thread(this);
              starter.start();
              running = true;
              if (intent != null)
              {
                     boolean onSystemStartup = intent.getBooleanExtra(TrackerService.START_IN_FOREGROUND, false);
                     if (onSystemStartup) foreground();
              }
              return Service.START_STICKY;
       }

       @Override
       public void onCreate()
       {
              super.onCreate();
       }

       @Override
       public void onDestroy()
       {
              super.onDestroy();
              if (priceTrackerSvc != null)
              {
                     try
                     {
                            priceTrackerSvc.removeEventListener(this);
                            priceTrackerSvc.stop();
                     } catch (Exception e)
                     {
                     }
              }
              running = false;

              if (!forceStopped)
              {
                     logger.info("PriceTracker service is being destroyed by OS! Sending last scream broadcast");
                     Intent broadcastIntent = new Intent("com.jager.foxhoundpricetracker.ActivityRevive.RestartPriceTrackerService");
                     sendBroadcast(broadcastIntent);
                     logger.info("Last scream broadcast sent!");
              }
              else logger.info("PriceTracker service is stopped manually.");
       }

       @Override
       public IBinder onBind(Intent intent)
       {
              return serviceBinder;
       }

       @Override
       public void priceChanges(String oldPrice, String newPrice, Product product)
       {
              notificationHelper.sendNotification(this, product.getId(), product.getName(), String.format("New price: %s", newPrice), R.drawable.price_change);
       }

       @Override
       public void availabilityChanges(boolean available, Product product, Exception error)
       {
              notificationHelper.sendNotification(this, product.getId(), product.getName(),  available ? "Available" : "Not available", R.drawable.availability_changes);
       }

       @Override
       public void productChecked(Product product)
       {

       }

       @Override
       public void productAdded(Product product)
       {
              notificationHelper.sendNotification(this, product.getId(), product.getName(),  "New product added.", R.drawable.name);
       }

       @Override
       public void productRemoved(Product product)
       {
              notificationHelper.sendNotification(this, null, product.getName(),  "Product removed.", R.drawable.name);
       }


       @Override
       public void run()
       {
              try
              {
                     priceTrackerSvc = PriceTrackerService.getInstance();
                     priceTrackerSvc.setGlobalProductRequestTimeout(20000);
                     priceTrackerSvc.addEventListener(this);
                     priceTrackerSvc.start();

              } catch (Exception e)
              {
                     logger.error("Cannot start PriceTracker service. Details: %s", e.getMessage());
                     e.printStackTrace();
              }
       }

       /**
        * Place the service into the foreground
        */
       public void foreground() {
              startForeground(NOTIFICATION_ID, new NotificationHelper(this).createForegroundServiceNotification(this,"Tracker service active", "Tap to return to PriceTracker"));
       }

       /**
        * Return the service to the background
        */
       public void background() {
              stopForeground(true);
       }
}
