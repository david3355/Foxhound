package com.jagerdev.foxhoundpricetracker;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.jagerdev.foxhoundpricetracker.products.NotificationConfirmer;
import com.jagerdev.foxhoundpricetracker.products.UniversalPriceParser;
import com.jagerdev.foxhoundpricetracker.products.selector.PriceParseException;
import com.jagerdev.foxhoundpricetracker.utils.NotificationHelper;
import com.jagerdev.foxhoundpricetracker.utils.TrackerInitializer;

import model.Product;
import tracker.PriceTrackerService;
import tracker.ProductAvailability;
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
       private UniversalPriceParser priceParser = UniversalPriceParser.getInstance();

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
              if (intent != null)  // Is this necessary? Should be started as foreground service instead
              {
                     boolean onSystemStartup = intent.getBooleanExtra(TrackerService.START_IN_FOREGROUND, false);
                     if (onSystemStartup) foreground();
              }
              TrackerInitializer.initialize(this);
              logger.info("Starting PriceTracker Android service");
              Thread starter = new Thread(this);
              starter.start();
              running = true;

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
                            priceTrackerSvc.stop(false);
                     } catch (Exception e)
                     {
                            Log.w(this.getClass().getName(), String.format("Error while stopping PriceTracker service: %s", e.getMessage()));
                     }
              }
              running = false;

              if (!forceStopped)
              {
                     logger.info("PriceTracker service is being destroyed by OS! Sending last scream broadcast");
                     Intent broadcastIntent = new Intent(this, TrackerLastScreamReceiver.class);
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
              if (NotificationConfirmer.shouldSendPriceUpdateNotification(product, oldPrice, newPrice))
              {
                     int notifIconRes;
                     String extraNotifInfo = "Price changed";
                     try {
                            double parsedOldPrice = priceParser.getPrice(oldPrice, product.getDecimalSeparator());
                            double parsedNewPrice = priceParser.getPrice(newPrice, product.getDecimalSeparator());
                            if (parsedNewPrice > parsedOldPrice)
                            {
                                   notifIconRes = R.drawable.up;
                                   extraNotifInfo = "Price increased";
                            } else
                            {
                                   notifIconRes = R.drawable.down;
                                   extraNotifInfo = "Price decreased";
                            }


                     } catch (PriceParseException e) {
                            notifIconRes = R.drawable.price_change;
                     }

                     notificationHelper.sendNotification(this, product.getId(), product.getName(), String.format("%s. New price: %s", extraNotifInfo, newPrice), notifIconRes, false);
              }
       }

       @Override
       public void availabilityChecked(boolean previouslyAvailable, ProductAvailability availability, Product product, Exception error)
       {
              ProductInfoActivity.saveStateDetailsToPrefs(ProductInfoActivity.STATE_DETAILS_PREF_PREFIX_KEY, this, product, error != null ? error.getMessage() : "");
              if (availability != ProductAvailability.NO_INTERNET &&
                      previouslyAvailable != availability.getValue() &&
                      NotificationConfirmer.shouldSendAvailabilityUpdateNotification(product, availability))
              {
                     int iconResource = availability.getValue() ? R.drawable.available : R.drawable.not_available;
                     notificationHelper.sendNotification(this, product.getId(), product.getName(), availability.getValue() ? "Available" : "Not available", iconResource, availability.getValue());
              }
       }

       @Override
       public void productChecked(Product product)
       {

       }

       @Override
       public void productAdded(Product product)
       {
              notificationHelper.sendNotification(this, product.getId(), product.getName(),  "New product added.", R.drawable.name, false);
       }

       @Override
       public void productRemoved(Product product)
       {
              notificationHelper.sendNotification(this, null, product.getName(),  "Product removed.", R.drawable.name, false);
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
              startForeground(NOTIFICATION_ID, new NotificationHelper(this).createForegroundServiceNotification(this));
       }

       /**
        * Return the service to the background
        */
       public void background() {
              stopForeground(true);
       }
}
