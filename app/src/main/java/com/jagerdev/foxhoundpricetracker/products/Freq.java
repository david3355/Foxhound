package com.jagerdev.foxhoundpricetracker.products;

public enum Freq
{
       SECONDS(0, "sec", "seconds"),
       MINUTES(0, "sec", "seconds"),
       HOURS(0, "sec", "seconds"),
       DAYS(0, "sec", "seconds");

       Freq(int id, String unit, String unit_name)
       {
              this.unit = unit;
              this.unit_name = unit_name;
       }

       public String unit;
       public String unit_name;
}
