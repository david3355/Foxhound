package com.jagerdev.foxhoundpricetracker.utils.chart;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.jagerdev.foxhoundpricetracker.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HistoryChartMarkerView extends MarkerView
{
       private final TextView txtMarkerValue, txtMarkerDate;

       public HistoryChartMarkerView(Context context, int layoutResource)
       {
              super(context, layoutResource);

              txtMarkerValue = findViewById(R.id.txt_marker_value);
              txtMarkerDate = findViewById(R.id.txt_marker_date);
       }

       private final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd HH:mm", Locale.ENGLISH);
       // runs every time the MarkerView is redrawn, can be used to update the
       // content (user-interface)
       @Override
       public void refreshContent(Entry e, Highlight highlight)
       {

              if (e instanceof CandleEntry)
              {

                     CandleEntry ce = (CandleEntry) e;

                     txtMarkerValue.setText(Utils.formatNumber(ce.getHigh(), 0, false));
              } else
              {
                     String value = Utils.formatNumber(e.getY(), 0, false);
                     String date = dateFormatter.format(new Date((long)e.getX()));
                     txtMarkerDate.setText(String.format("%s ", date));
                     txtMarkerValue.setText(value);
              }

              super.refreshContent(e, highlight);
       }

       @Override
       public MPPointF getOffset()
       {
              return new MPPointF(-(getWidth() / 2), -getHeight());
       }
}
