package com.jagerdev.foxhoundpricetracker.products;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jagerdev.foxhoundpricetracker.R;

import model.Product;


/**
 * Created by Jager on 2018.02.08..
 */

public class ProductAdapter extends ArrayAdapter<Product>
{
       public ProductAdapter(Context context)
       {
              super(context, R.layout.product_layout);
       }

       @Override
       public View getView(int i, View view, ViewGroup parent)
       {
              LayoutInflater inflater = LayoutInflater.from(getContext());
              view = inflater.inflate(R.layout.product_layout, parent, false);
              TextView text_product_name = view.findViewById(R.id.product_name);
              TextView text_product_price =  view.findViewById(R.id.product_price);
              TextView text_product_alarm =  view.findViewById(R.id.product_alarm);
              Product p = getItem(i);
              int textColor = p.isAvailableNow() ? ContextCompat.getColor(getContext(), R.color.colorSpringGreen): Color.RED;
              text_product_name.setText(p.getName());
              text_product_price.setTextColor(textColor);
              text_product_price.setText(p.getActualPrice());
              if (p.getActiveAlarms() == 0) text_product_alarm.setBackgroundColor(Color.WHITE);
              else
              {
                     text_product_alarm.setText(String.valueOf(p.getActiveAlarms()));
                     text_product_alarm.setBackgroundColor(Color.rgb(255, 155, 0));
              }

              Linkify.addLinks(text_product_name, Linkify.WEB_URLS);
              // TODO set texts, set background
              return view;
       }


}
