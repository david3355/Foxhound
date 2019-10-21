package com.jagerdev.foxhoundpricetracker.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.jagerdev.foxhoundpricetracker.MainActivity.PICKFILE_REQUEST_CODE;

public class AndroidUtil
{
       private static final String PREFS_NAME = "com.jagerdev.foxhoundpricetracker.FoxhoundPriceTracker";

       public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
              ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
              for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                     if (serviceClass.getName().equals(service.service.getClassName())) {
                            Log.i ("isMyServiceRunning?", true+"");
                            return true;
                     }
              }
              Log.i ("isMyServiceRunning?", false+"");
              return false;
       }

       public static void toastOnThread(final Activity context, final String message)
       {
              toastOnThread(context, message, Toast.LENGTH_SHORT);
       }

       public static void toastOnThread(final Activity context, final String message, final int toastLength)
       {
              context.runOnUiThread(new Runnable()
              {
                     @Override
                     public void run()
                     {
                            Toast.makeText(context, message, toastLength).show();
                     }
              });
       }

       public static String getClipboardText(Context context)
       {
              ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
              if (clipboard != null)
              {
                     ClipData data = clipboard.getPrimaryClip();
                     if (data != null && data.getItemCount() > 0)
                     {
                            return clipboard.getPrimaryClip().getItemAt(0).getText().toString();
                     }
              }
              return "";
       }

       public static void setClipboardText(Context context, String text)
       {
              ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
              if (clipboard != null)
              {
                     ClipData data = ClipData.newPlainText("PriceTracker", text);
                     clipboard.setPrimaryClip(data);
              }
       }

       public static void openInDefaultBrowser(Context context, String url)
       {
              Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
              context.startActivity(browserIntent);
       }

       public static void shareFile(Context context, Uri fileURI, String shareMessage)
       {
              Intent intent = new Intent(Intent.ACTION_SEND);
              intent.setType("application/octet-stream");
              intent.putExtra(Intent.EXTRA_STREAM, fileURI);

              context.startActivity(Intent.createChooser(intent, shareMessage));
       }

       public static  void selectFile(Activity context)
       {
              Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
              intent.setType("file/*.db");
              context.startActivityForResult(intent, PICKFILE_REQUEST_CODE);
       }

       public static void saveValueToPrefs(String prefKey, Context context, String value)
       {
              SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
              prefs.putString(prefKey, value);
              prefs.apply();
       }

       public static String readValueFromPrefs(String prefKey, Context context, String defaultValue)
       {
              SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
              return prefs.getString(prefKey, defaultValue);
       }

       public static void saveListToPrefs(String prefKey, Context context, List<String> value)
       {
              SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
              prefs.putStringSet(prefKey, new HashSet<String>(value));
              prefs.apply();
       }

       public static List<String> readListFromPrefs(String prefKey, Context context, List<String> defaultValue)
       {
              SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
              return new ArrayList<>(prefs.getStringSet(prefKey, new HashSet<>(defaultValue)));
       }
}
