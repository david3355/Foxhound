package com.jagerdev.foxhoundpricetracker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jagerdev.foxhoundpricetracker.utils.ExternalStorageHelper;

/**
 * Created by Jager on 2016.02.08..
 */

//public class PriceTrackerDatabaseManager
//{
//       private PriceTrackerDatabaseManager(Context AppContext)
//       {
//              appContext = AppContext;
//       }
//
//       private static PriceTrackerDatabaseManager self;
//
//       public static PriceTrackerDatabaseManager getInstance(Context AppContext)
//       {
//              if (self == null)
//                     self = new PriceTrackerDatabaseManager(AppContext);
//              return self;
//       }
//
//       private Context appContext;
//       private PriceTrackerDatabaseHelper dbHelper;
//       private SQLiteDatabase mDB;
//
//       private boolean fileExist(String FileName)
//       {
//              return new File(FileName).exists();
//       }
//
//       private void open()
//       {
//              dbHelper = new PriceTrackerDatabaseHelper(appContext, DBConstants.getDatabasePath(appContext));
//              mDB = dbHelper.getWritableDatabase();
//              if (mDB != null && !fileExist(getAbsuluteDBPath()))
//                     dbHelper.onCreate(mDB);
//       }
//
//       private void close()
//       {
//              dbHelper.close();
//       }
//
//       public String getAbsuluteDBPath()
//       {
//              return mDB.getPath();
//       }
//
//       public long insertData(PositionsTableDef data)
//       {
//              open();
//              ContentValues values = new ContentValues();
//              values.put(PositionsTableDef.COLUMN_LATITUDE, data.getLatitude());
//              values.put(PositionsTableDef.COLUMN_LONGITUDE, data.getLongitude());
//              values.put(PositionsTableDef.COLUMN_ACCURACY, data.getAccuracy());
//              values.put(PositionsTableDef.COLUMN_TIMESTAMP, data.getTimestamp());
//              long rowid = mDB.insert(PositionsTableDef.DB_TABLE, null, values);
//
//              close();
//              return rowid;
//       }
//
//       public int deleteData(String DatabaseField, String KeyValue)
//       {
//              open();
//              int deletedRows = mDB.delete(PositionsTableDef.DB_TABLE,
//                      String.format("%s='%s'", DatabaseField, KeyValue), null);
//              close();
//              return deletedRows;
//       }
//
//       public void clearAllData()
//       {
//              open();
//              mDB.execSQL(String.format("DELETE FROM %s", PositionsTableDef.DB_TABLE));
//              close();
//       }
//
//       public int updateValue(String DatabaseField, String KeyValue, String ColumnToModify, Object newValue)
//       {
//              open();
//              ContentValues values = new ContentValues();
//              values.put(ColumnToModify, newValue.toString());
//              int updatedRows = mDB.update(PositionsTableDef.DB_TABLE, values,
//                      String.format("%s='%s'", DatabaseField, KeyValue), null);
//              close();
//              return updatedRows;
//       }
//
//       public List<PositionsTableDef> getAllRecords()
//       {
//              List<PositionsTableDef> records = new ArrayList<PositionsTableDef>();
//              Cursor c = selectAll();
//              if (!c.moveToFirst())
//                     return records;
//              do
//              {
//                     records.add(getDataByCursor(c));
//              } while (c.moveToNext());
//              return records;
//       }
//
//       public Cursor selectAll()
//       {
//              open();
//              String query = String.format("select * from %s", PositionsTableDef.DB_TABLE);
//              Cursor c = mDB.rawQuery(query, null);
//              c.moveToFirst(); // Mielőtt lezárjuk a kapcsolatot, az első elemre kell lépni, különben nem lesz adat!
//              close();
//              return c;
//       }
//
//       public PositionsTableDef selectData(String ColumnName, String KeyValue)
//       {
//              open();
//              String query = String.format("select * from %s where %s='%s'", PositionsTableDef.DB_TABLE, ColumnName, KeyValue);
//              Cursor c = mDB.rawQuery(query, null);
//              boolean hasData = c.moveToFirst();
//              close();
//              if (hasData)
//                     return getDataByCursor(c);
//              return null;
//       }
//
//       public PositionsTableDef selectMax(String ColumnName)
//       {
//              open();
//              String query = String.format("SELECT * FROM %s WHERE %s=(SELECT MAX(%s) FROM %s)", PositionsTableDef.DB_TABLE, ColumnName, ColumnName, PositionsTableDef.DB_TABLE);
//              Cursor c = mDB.rawQuery(query, null);
//              boolean hasData = c.moveToFirst();
//              close();
//              if (hasData)
//                     return getDataByCursor(c);
//              return null;
//       }
//
//       public List<PositionsTableDef> fetchAll()
//       {
//              open();
//              String[] fields =
//                      {PositionsTableDef.COLUMN_ID, PositionsTableDef.COLUMN_LATITUDE, PositionsTableDef.COLUMN_LONGITUDE, PositionsTableDef.COLUMN_ACCURACY, PositionsTableDef.COLUMN_TIMESTAMP};
//              Cursor c = mDB.query(PositionsTableDef.DB_TABLE, fields, null, null, null, null, null);
//              List<PositionsTableDef> data = new ArrayList<>();
//              boolean hasData = c.moveToFirst();
//              if (hasData) do
//              {
//                     PositionsTableDef item = getDataByCursor(c);
//                     data.add(item);
//                     hasData = c.moveToNext();
//              } while (hasData);
//              close();
//              return data;
//       }
//
//       public PositionsTableDef fetchData(String DatabaseField, String KeyValue)
//       {
//              open();
//              String[] fields =
//                      {PositionsTableDef.COLUMN_ID, PositionsTableDef.COLUMN_LATITUDE, PositionsTableDef.COLUMN_LONGITUDE, PositionsTableDef.COLUMN_ACCURACY, PositionsTableDef.COLUMN_TIMESTAMP};
//              Cursor c = mDB.query(PositionsTableDef.DB_TABLE, fields,
//                      String.format("%s='%s'", DatabaseField, KeyValue), null, null, null, null);
//              boolean hasData = c.moveToFirst();
//              close();
//              if (hasData)
//                     return getDataByCursor(c);
//              return null;
//       }
//
//       public  List<PositionsTableDef> fetchDataBetween(String DatabaseField, String FromValue, String ToValue)
//       {
//              open();
//              String[] fields =
//                      {PositionsTableDef.COLUMN_ID, PositionsTableDef.COLUMN_LATITUDE, PositionsTableDef.COLUMN_LONGITUDE, PositionsTableDef.COLUMN_ACCURACY, PositionsTableDef.COLUMN_TIMESTAMP};
//              Cursor c = mDB.query(PositionsTableDef.DB_TABLE, fields,
//                      String.format("%s BETWEEN %s AND %s", DatabaseField, FromValue, ToValue), null, null, null, null);
//              List<PositionsTableDef> data = new ArrayList<>();
//              boolean hasData = c.moveToFirst();
//              if (hasData) do
//              {
//                     PositionsTableDef item = getDataByCursor(c);
//                     data.add(item);
//                     hasData = c.moveToNext();
//              } while (hasData);
//              close();
//              return data;
//       }
//
//       private PositionsTableDef getDataByCursor(Cursor c)
//       {
//              return new PositionsTableDef(
//                      Double.parseDouble(c.getString(c.getColumnIndex(PositionsTableDef.COLUMN_LATITUDE))),
//                      Double.parseDouble(c.getString(c.getColumnIndex(PositionsTableDef.COLUMN_LONGITUDE))),
//                      Integer.parseInt(c.getString(c.getColumnIndex(PositionsTableDef.COLUMN_ACCURACY))),
//                      Long.parseLong(c.getString(c.getColumnIndex(PositionsTableDef.COLUMN_TIMESTAMP)))
//              );
//       }
//}

class PriceTrackerDatabaseHelper extends SQLiteOpenHelper
{

       public PriceTrackerDatabaseHelper(Context context, String name)
       {
              super(context, name, null, DBConstants.DATABASE_VERSION);
       }

       @Override
       public void onCreate(SQLiteDatabase db)
       {
              //db.execSQL(DBConstants.DATABASE_CREATE_ALL);
       }

       @Override
       public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
       {
              // db.execSQL(DBConstants.DATABASE_DROP_ALL);
              // db.execSQL(DBConstants.DATABASE_CREATE_ALL);
       }
}


class DBConstants
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