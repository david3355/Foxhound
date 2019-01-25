package com.jagerdev.foxhoundpricetracker.utils;

import android.widget.TextView;

public class Common
{
       public static void updateTextValue(boolean increase, TextView textView)
       {
              try
              {
                     int value = Integer.parseInt(textView.getText().toString());
                     value = increase ? value + 1 : value - 1;
                     textView.setText(String.valueOf(value));
              } catch (Exception e)
              {
              }
       }
}
