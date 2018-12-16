package com.jagerdev.foxhoundpricetracker.database.model;

class PriceHistoryTableDef
{
       public static final String TABLE_PREFIX = "product_";

       public PriceHistoryTableDef(String productID)
       {
              this.table_name = TABLE_PREFIX + productID; // TODO history_
       }

       public String table_name;
       public static final String COLUMN_ID = "id";
       public static final String COLUMN_MEASURE_TIME = "measure_time";
       public static final String COLUMN_PRICE = "actual_price";

       private long id;
       private long measure_time;
       private String actual_price;

       public PriceHistoryTableDef(long id, long measure_time, String actual_price)
       {
              this.id = id;
              this.measure_time = measure_time;
              this.actual_price = actual_price;
       }

       public long getId()
       {
              return id;
       }

       public void setId(long id)
       {
              this.id = id;
       }

       public long getMeasure_time()
       {
              return measure_time;
       }

       public void setMeasure_time(long measure_time)
       {
              this.measure_time = measure_time;
       }

       public String getActual_price()
       {
              return actual_price;
       }

       public void setActual_price(String actual_price)
       {
              this.actual_price = actual_price;
       }

       public String getCreateTableCommand()
       {
              return String.format("CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT)",
                      table_name, COLUMN_ID, COLUMN_MEASURE_TIME, COLUMN_PRICE);
       }

       public String getDropTableCommand()
       {
              return String.format("DROP TABLE IF EXISTS %s", table_name);
       }

       public String getInsertCommand(long snapshotTimestamp, String actualPrice)
       {
              return String.format("INSERT INTO %s (%s, %s) VALUES ('%s', '%s')", table_name, COLUMN_MEASURE_TIME,
                      COLUMN_PRICE, snapshotTimestamp, actualPrice);
       }
}