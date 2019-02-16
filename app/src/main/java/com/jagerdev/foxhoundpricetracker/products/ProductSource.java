package com.jagerdev.foxhoundpricetracker.products;

import android.graphics.Color;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

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

       public ProductSource(String productName, String name, int color, LineGraphSeries<DataPoint> dataPoints)
       {
              this.name = name;
              this.color = color;
              this.dataPoints = dataPoints;
       }

       public ProductSource(String productName, String name, LineGraphSeries<DataPoint> dataPoints)
       {
              this(productName, name, generateUniqueColor(productName), dataPoints);
       }

       private String name;
       private int color;
       private LineGraphSeries<DataPoint> dataPoints;
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

       public LineGraphSeries<DataPoint> getDataPoints()
       {
              return dataPoints;
       }

       public void setDataPoints(LineGraphSeries<DataPoint> dataPoints)
       {
              this.dataPoints = dataPoints;
       }
}
