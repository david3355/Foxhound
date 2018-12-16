package com.jagerdev.foxhoundpricetracker.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.DataBaseManager;

public class AndroidDatabaseManager implements DataBaseManager
{
       public AndroidDatabaseManager(String databaseFilePath, Context AppContext)
       {
              setDatabasePath(databaseFilePath);
              appContext = AppContext;
       }

       private String databasePath;
       private Context appContext;
       private PriceTrackerDatabaseHelper dbHelper;
       private SQLiteDatabase mDB;

       @Override
       public String getDatabasePath()
       {
              return databasePath;
       }

       @Override
       public void setDatabasePath(String s)
       {
              databasePath = s;
       }

       @Override
       public boolean fileExist(String s)
       {
              return new File(s).exists();
       }

       @Override
       public Connection open()
       {
              dbHelper = new PriceTrackerDatabaseHelper(appContext, DBConstants.getDatabasePath(appContext));
              mDB = dbHelper.getWritableDatabase();
              if (mDB != null && !fileExist(getAbsuluteDBPath()))
                     dbHelper.onCreate(mDB);
              return null;
       }

       private void close()
       {
              dbHelper.close();
       }

       public String getAbsuluteDBPath()
       {
              return mDB.getPath();
       }

       @Override
       public void executeNonQuery(String s)
       {
              synchronized (this)
              {
                     open();
                     mDB.execSQL(s);
              }
       }

       @Override
       public List<Map<String, String>> executeQuery(String s)
       {
              synchronized (this)
              {
                     open();
                     Cursor c = mDB.rawQuery(s, null);
                     List<Map<String, String>> result = new ArrayList<>();
                     boolean hasData = c.moveToFirst();
                     int n = c.getColumnCount();
                     if (hasData) do
                     {
                            Map<String, String> row = new HashMap<>();
                            for (int i = 0; i < n; i++)
                            {
                                   String fieldName = c.getColumnName(i);
                                   row.put(fieldName, c.getString(i));
                            }
                            result.add(row);
                            hasData = c.moveToNext();
                     } while (hasData);
                     c.close();
                     return result;
              }
       }
}
