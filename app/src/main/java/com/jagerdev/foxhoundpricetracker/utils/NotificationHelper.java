package com.jagerdev.foxhoundpricetracker.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.jagerdev.foxhoundpricetracker.MainActivity;
import com.jagerdev.foxhoundpricetracker.ProductInfoActivity;
import com.jagerdev.foxhoundpricetracker.R;

public class NotificationHelper
{
       final static String GROUP_KEY_PRICETRACKER = "price_tracker";
       final static String CHANNEL_ID = "price_tracker_notifications";

       public NotificationHelper(Context context)
       {
              this.ctx = context;
       }

       private Context ctx;

       public void sendNotification(Context context, String productId, String notificationTitle, String notificationText, int notificationIconSmall)
       {
              NotificationManager notificationManager =
                      (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

              Intent resultIntent = new Intent(context, ProductInfoActivity.class);
              resultIntent.putExtra("product_id", productId);
              PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent,
                      PendingIntent.FLAG_UPDATE_CURRENT);

              NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context.getApplicationContext(), CHANNEL_ID);
              notifBuilder.setSmallIcon(notificationIconSmall)
                      .setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), notificationIconSmall))
                      .setContentTitle(notificationTitle)
                      .setContentText(notificationText)
                      .setStyle(new NotificationCompat.BigTextStyle())
                      .setGroup(GROUP_KEY_PRICETRACKER)
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

              notificationManager.notify(productId.hashCode(), notif);
       }

       public Notification createForegroundServiceNotification(Context context, String notificationTitle, String notificationText)
       {
              Intent resultIntent = new Intent(context, MainActivity.class);
              PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent,
                      PendingIntent.FLAG_UPDATE_CURRENT);

              NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(ctx)
                      .setSmallIcon(R.drawable.foxhound_small)
                      .setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), R.drawable.foxhound))
                      .setContentTitle(notificationTitle)
                      .setContentText(notificationText)
                      .setStyle(new NotificationCompat.BigTextStyle())
                      .setContentIntent(resultPendingIntent);

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
