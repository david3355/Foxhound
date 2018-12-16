package com.jagerdev.foxhoundpricetracker.products;

public  class Frequency
{
       public static final String[] UNITS = {"sec", "min", "hour", "day"};
       public static final String[] UNITS_NAMES= {"seconds", "minutes", "hours", "days"};

       public Frequency(String unit, int frequency)
       {
              this.unit = unit;
              this.frequency = frequency;
              this.unit_name = Frequency.UNITS_NAMES[getIndex()];
       }

       public int frequency;
       public String unit;
       public String unit_name;

       public int getIndex()
       {
              for (int i = 0; i < UNITS.length; i++)
              {
                     if (unit == UNITS[i]) return i;
              }
              return 0;
       }
}

