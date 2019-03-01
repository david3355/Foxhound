package com.jagerdev.foxhoundpricetracker.products;

import android.graphics.Color;

import com.github.mikephil.charting.data.LineDataSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class ProductSource
{
       static
       {
              colorStore = new HashMap<>();
       }

       public ProductSource(String productName, String name, int color, LineDataSet dataPoints)
       {
              this.name = name;
              this.color = color;
              this.dataPoints = dataPoints;
       }

       public ProductSource(String productName, String name, LineDataSet dataSet)
       {
              this(productName, name, generateUniqueColor(productName), dataSet);
       }

       private String name;
       private int color;
       private LineDataSet dataPoints;
       private static Map<String, Set<Integer>> colorStore;

       private static int generateUniqueColor(String id)
       {
              if (!colorStore.containsKey(id)) colorStore.put(id, new HashSet<Integer>());
              int genereatedColor;
              do
              {
                     genereatedColor = generateColor();
              }while(colorStore.get(id).contains(genereatedColor));
              return genereatedColor;
       }

       public static int generateColor()
       {
              Random rnd = new Random();
              return Color.rgb(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255));
       }

       public String getName()
       {
              return name;
       }

       public void setName(String name)
       {
              this.name = name;
       }

       public int getColor()
       {
              return color;
       }

       public void setColor(int color)
       {
              this.color = color;
       }

       public LineDataSet getDataSet()
       {
              return dataPoints;
       }

       public void setDataSet(LineDataSet dataPoints)
       {
              this.dataPoints = dataPoints;
       }
}
