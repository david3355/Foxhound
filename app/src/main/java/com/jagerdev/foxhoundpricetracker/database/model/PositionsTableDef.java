package com.jagerdev.foxhoundpricetracker.database.model;

import java.util.ArrayList;
import java.util.List;

import database.DBUtil;

//public class PositionsTableDef
//{
//       public static String DATABASE_NAME = "locations.db";
//
//       public static final String DB_TABLE = "positions";
//       public static final String COLUMN_ID = "id";
//       public static final String COLUMN_LATITUDE = "latitude";
//       public static final String COLUMN_LONGITUDE = "longitude";
//       public static final String COLUMN_ACCURACY = "accuracy";
//       public static final String COLUMN_TIMESTAMP = "timestamp";
//
//       public static final String DATABASE_CREATE = String
//               .format("CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s REAL, %s REAL, %s INTEGER, %s INTEGER)",
//                       DB_TABLE, COLUMN_ID, COLUMN_LATITUDE, COLUMN_LONGITUDE, COLUMN_ACCURACY, COLUMN_TIMESTAMP);
//       public static final String DATABASE_DROP = String.format("drop table if exists %s", DB_TABLE);
//
//       public PositionsTableDef(double latitude, double longitude, int accuracy, long timestamp)
//       {
//              this.latitude = latitude;
//              this.longitude = longitude;
//              this.accuracy = accuracy;
//              this.timestamp = timestamp;
//       }
//
//       private double latitude;
//       private double longitude;
//       private int accuracy;
//       private long timestamp;
//
//       public double getLatitude()
//       {
//              return latitude;
//       }
//
//       public void setLatitude(double latitude)
//       {
//              this.latitude = latitude;
//       }
//
//       public double getLongitude()
//       {
//              return longitude;
//       }
//
//       public void setLongitude(double longitude)
//       {
//              this.longitude = longitude;
//       }
//
//       public int getAccuracy()
//       {
//              return accuracy;
//       }
//
//       public void setAccuracy(int accuracy)
//       {
//              this.accuracy = accuracy;
//       }
//
//       public long getTimestamp()
//       {
//              return timestamp;
//       }
//
//       public void setTimestamp(long timestamp)
//       {
//              this.timestamp = timestamp;
//       }
//}

class ProductTableDef
{
       public static final String TABLE_NAME = "product";
       public static final String COLUMN_ID = "id";
       public static final String COLUMN_NAME = "name";
       public static final String COLUMN_WEBPATH = "webpath";
       public static final String COLUMN_PRICEPATHSELECTOR = "price_path_selector";
       public static final String COLUMN_RECORDTIMESTAMP = "record_timestamp";
       public static final String COLUMN_ACTUALPRICE = "actual_price";
       public static final String COLUMN_AVAILABLE = "available";
       public static final String COLUMN_INSPECTFREQUENCY = "inspect_frequency";
       public static final String COLUMN_LASTCHECKTIMESTAMP = "lastcheck_timestamp";
       public static final String COLUMN_ACTIVEALARMS = "active_alarms";

       private long id;
       private String name;
       private String webpath;
       private String price_path_selector;
       private long record_timestamp;
       private String actual_price;
       private boolean available;
       private int inspect_frequency;
       private long lastcheck_timestamp;
       private int active_alarms;

       public ProductTableDef(long id, String name, String webpath, String price_path_selector, long record_timestamp, String actual_price, boolean available, int inspect_frequency, long lastcheck_timestamp, int active_alarms)
       {
              this.id = id;
              this.name = name;
              this.webpath = webpath;
              this.price_path_selector = price_path_selector;
              this.record_timestamp = record_timestamp;
              this.actual_price = actual_price;
              this.available = available;
              this.inspect_frequency = inspect_frequency;
              this.lastcheck_timestamp = lastcheck_timestamp;
              this.active_alarms = active_alarms;
       }

       public long getId()
       {
              return id;
       }

       public void setId(long id)
       {
              this.id = id;
       }

       public String getName()
       {
              return name;
       }

       public void setName(String name)
       {
              this.name = name;
       }

       public String getWebpath()
       {
              return webpath;
       }

       public void setWebpath(String webpath)
       {
              this.webpath = webpath;
       }

       public String getPrice_path_selector()
       {
              return price_path_selector;
       }

       public void setPrice_path_selector(String price_path_selector)
       {
              this.price_path_selector = price_path_selector;
       }

       public long getRecord_timestamp()
       {
              return record_timestamp;
       }

       public void setRecord_timestamp(long record_timestamp)
       {
              this.record_timestamp = record_timestamp;
       }

       public String getActual_price()
       {
              return actual_price;
       }

       public void setActual_price(String actual_price)
       {
              this.actual_price = actual_price;
       }

       public boolean isAvailable()
       {
              return available;
       }

       public void setAvailable(boolean available)
       {
              this.available = available;
       }

       public int getInspect_frequency()
       {
              return inspect_frequency;
       }

       public void setInspect_frequency(int inspect_frequency)
       {
              this.inspect_frequency = inspect_frequency;
       }

       public long getLastcheck_timestamp()
       {
              return lastcheck_timestamp;
       }

       public void setLastcheck_timestamp(long lastcheck_timestamp)
       {
              this.lastcheck_timestamp = lastcheck_timestamp;
       }

       public int getActive_alarms()
       {
              return active_alarms;
       }

       public void setActive_alarms(int active_alarms)
       {
              this.active_alarms = active_alarms;
       }

       public static final String CREATE_TABLE = String.format(
               "CREATE TABLE IF NOT EXISTS %s (%s TEXT PRIMARY KEY, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s INTEGER, %s TEXT, %s INTEGER)",
               TABLE_NAME, COLUMN_ID, COLUMN_NAME, COLUMN_WEBPATH, COLUMN_RECORDTIMESTAMP, COLUMN_ACTUALPRICE,
               COLUMN_AVAILABLE, COLUMN_PRICEPATHSELECTOR, COLUMN_INSPECTFREQUENCY, COLUMN_LASTCHECKTIMESTAMP,
               COLUMN_ACTIVEALARMS);

       public static final String DROP_TABLE = String.format("DROP TABLE IF EXISTS %s", TABLE_NAME);

       public static String getInsertCommand(String uuid, String productName, String webPath, String pricePathSelector,
                                             long recordTimestamp, String actualPrice, boolean available, int inspectFrequency, long lastCheckTimestamp,
                                             int activeAlarms)
       {
              productName = DBUtil.replaceSpecialCharacters(productName);
              pricePathSelector = DBUtil.replaceSpecialCharacters(pricePathSelector);
              actualPrice = DBUtil.replaceSpecialCharacters(actualPrice);

              return String.format(
                      "INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', %s, '%s', %s)",
                      TABLE_NAME, COLUMN_ID, COLUMN_NAME, COLUMN_WEBPATH, COLUMN_RECORDTIMESTAMP, COLUMN_ACTUALPRICE,
                      COLUMN_AVAILABLE, COLUMN_PRICEPATHSELECTOR, COLUMN_INSPECTFREQUENCY, COLUMN_LASTCHECKTIMESTAMP,
                      COLUMN_ACTIVEALARMS, uuid, productName, webPath, recordTimestamp, actualPrice, available,
                      pricePathSelector, inspectFrequency, lastCheckTimestamp, activeAlarms);
       }

       public static String getDeleteCommand(String uuid)
       {
              return String.format("DELETE FROM %s WHERE %s='%s'", TABLE_NAME, COLUMN_ID, uuid);
       }

       public static String getUpdateCommand(String id, String databaseField, String newValue)
       {
              return String.format("UPDATE %s SET %s='%s' WHERE id='%s'", TABLE_NAME, databaseField, newValue, id);
       }

       public static String getMultipleUpdateCommand(String id, String[] fields, Object[] values)
       {
              List<String> updateParts = new ArrayList<String>();

              for(int i = 0; i < fields.length; i++)
              {
                     updateParts.add(String.format("%s='%s'", fields[i], values[i]));
              }
              return String.format("UPDATE %s SET %s WHERE id='%s'", TABLE_NAME, String.join(",", updateParts), id);
       }
}
