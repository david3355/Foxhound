package com.jagerdev.foxhoundpricetracker.database;

import android.content.Context;

import com.jagerdev.foxhoundpricetracker.utils.ExternalStorageHelper;

public class DBConstants
{
       public static String getDatabasePath(Context context)
       {
              if (ExternalStorageHelper.isStorageAccessible())
              {
                     if (DATABASE_PATH == null)
                            DATABASE_PATH = ExternalStorageHelper.getPublicDatabasePath(context, DATABASE_NAME);
                     return DATABASE_PATH;
              }
              return DATABASE_NAME;
       }

       public static final String DATABASE_NAME = "pricetracker.db";
       public static String DATABASE_PATH = null;
       public static final int DATABASE_VERSION = 1;

//       public static String DATABASE_CREATE_ALL = PositionsTableDef.DATABASE_CREATE;
//       public static String DATABASE_DROP_ALL = PositionsTableDef.DATABASE_DROP;
}