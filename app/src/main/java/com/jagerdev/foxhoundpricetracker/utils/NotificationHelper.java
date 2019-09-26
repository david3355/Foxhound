package com.jagerdev.foxhoundpricetracker.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import androidx.core.app.NotificationCompat;
import android.widget.RemoteViews;

import com.jagerdev.foxhoundpricetracker.FloatingCopyService;
import com.jagerdev.foxhoundpricetracker.MainActivity;
import com.jagerdev.foxhoundpricetracker.ProductInfoActivity;
import com.jagerdev.foxhoundpricetracker.R;

import java.util.Random;

public class NotificationHelper
{
       private static Random rnd = new Random();
       //       final static String GROUP_KEY_PRICETRACKER = "price_tracker";
       private final static String CHANNEL_ID = "price_tracker_notifications";

       public NotificationHelper(Context context)
       {
              this.ctx = context;
       }

       private Context ctx;

       public void sendNotification(Context context, String productId, String notificationTitle, String notificationText, int notificationIconSmall, boolean cancel)
       {
              NotificationManager notificationManager =
                      (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
              if (notificationManager == null) return;

              int notificationId = productId != null ? productId.hashCode() : rnd.nextInt(10000);

              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
              {
                     for (StatusBarNotification notification : notificationManager.getActiveNotifications())
                            if (notification.getId() == notificationId && cancel)
                            {
                                   notificationManager.cancel(notificationId);
                                   return;
                            }
              }

              Intent resultIntent = new Intent(context, productId != null ? ProductInfoActivity.class : MainActivity.class);
              resultIntent.putExtra("product_id", productId);
              PendingIntent resultPendingIntent = PendingIntent.getActivity(context, notificationId, resultIntent,
                      PendingIntent.FLAG_UPDATE_CURRENT);

              NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context.getApplicationContext(), CHANNEL_ID);
              notifBuilder.setSmallIcon(notificationIconSmall)
                      .setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), notificationIconSmall))
                      .setContentTitle(notificationTitle)
                      .setContentText(notificationText)
                      .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                      .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                      .setContentIntent(resultPendingIntent);

              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
              {
                     NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                             "FoxHound PriceTracker channel",
                             NotificationManager.IMPORTANCE_HIGH);
                     notificationManager.createNotificationChannel(channel);
                     notifBuilder.setChannelId(CHANNEL_ID);
              }

              Notification notif = notifBuilder.build();
              notif.flags |= Notification.FLAG_AUTO_CANCEL;

              notificationManager.notify(notificationId, notif);
       }

       public Notification createForegroundServiceNotification(Context context)
       {
              int serviceNotificationRequestCode = "FoxHoundServiceNotification".hashCode();
              Intent resultIntent = new Intent(context, MainActivity.class);
              PendingIntent goToApplicationPendingIntent = PendingIntent.getActivity(context, serviceNotificationRequestCode, resultIntent,
                      PendingIntent.FLAG_UPDATE_CURRENT);

              // TODO handle overlay permission depending on Android version!
              Intent copyServiceIntent = new Intent(context, FloatingCopyService.class);
              PendingIntent copyNewProductPendingIntent = PendingIntent.getService(context, serviceNotificationRequestCode, copyServiceIntent,
                      PendingIntent.FLAG_UPDATE_CURRENT);

              RemoteViews notificationView = new RemoteViews(context.getPackageName(), R.layout.pricetracker_notification);
              notificationView.setOnClickPendingIntent(R.id.panel_notification, goToApplicationPendingIntent);
              notificationView.setOnClickPendingIntent(R.id.btn_go_to_application, goToApplicationPendingIntent);
              notificationView.setOnClickPendingIntent(R.id.btn_copy_new_product, copyNewProductPendingIntent);

              NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(ctx)
                      .setSmallIcon(R.drawable.foxhound_small)
                      .setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), R.drawable.foxhound))
                      .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                      .setCustomContentView(notificationView)
                      .setCustomBigContentView(notificationView)
                      .setStyle(new NotificationCompat.BigTextStyle());

              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
              {
                     NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                     NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                             "FoxHound PriceTracker channel",
                             NotificationManager.IMPORTANCE_NONE);
                     notificationManager.createNotificationChannel(channel);
                     notifBuilder.setChannelId(CHANNEL_ID);
              }

              return notifBuilder.build();
       }

}
