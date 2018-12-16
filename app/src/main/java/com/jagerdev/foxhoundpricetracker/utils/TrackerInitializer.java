package com.jagerdev.foxhoundpricetracker.utils;

import android.content.Context;

import com.jagerdev.foxhoundpricetracker.database.AndroidDatabaseManager;

import org.apache.log4j.Level;

import database.DataBaseManager;
import database.PriceTrackerDatabaseManager;
import utility.logger.PriceTrackerLogger;

public class TrackerInitializer
{
       private final static String LOGFILE_PATH = "pricetracker.log";
       private final static String DATABASE_PATH = "pricetracker.db";

       public static void initialize(Context context)
       {
              initDatabase(context);
              initLogger(context);
       }

       private static void initLogger(Context context)
       {
              String logFilePath = ExternalStorageHelper.getPublicDatabasePath(context, TrackerInitializer.LOGFILE_PATH);
              PriceTrackerLogger.initLogger(logFilePath, Level.INFO);
       }

       private static void initDatabase(Context context)
       {
              if (!PriceTrackerDatabaseManager.isDatabaseManagerSet())
              {
                     String dbPath = context.getDatabasePath(TrackerInitializer.DATABASE_PATH).getAbsolutePath();
                     DataBaseManager databaseManager = new AndroidDatabaseManager(dbPath, context);
                     PriceTrackerDatabaseManager.setDatabaseManager(databaseManager);
              }
       }

}
