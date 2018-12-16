package com.jagerdev.foxhoundpricetracker.products;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jagerdev.foxhoundpricetracker.R;

import model.ProductSnapshot;


/**
 * Created by Jager on 2018.02.08..
 */

// TODO DELETE: Not used anymore!!!
public class ProductSnapshotAdapter extends ArrayAdapter<ProductSnapshot>
{
       public ProductSnapshotAdapter(Context context)
       {
              super(context, R.layout.history_item_layout);
       }

       @Override
       public View getView(int i, View view, ViewGroup parent)
       {
              LayoutInflater inflater = LayoutInflater.from(getContext());
              view = inflater.inflate(R.layout.history_item_layout, parent, false);
              TextView txt_record_date = view.findViewById(R.id.txt_record_date);
              TextView txt_product_price =  view.findViewById(R.id.txt_product_price);
              ProductSnapshot p = getItem(i);
              txt_record_date.setText(p.getDateOfSnapshot().toString("yyyy-MM-dd HH:mm:ss"));
              txt_product_price.setText(p.getPrice());
              return view;
       }


}
