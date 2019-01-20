package com.jagerdev.foxhoundpricetracker.products;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.jagerdev.foxhoundpricetracker.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

       private ProductFilter productFilter = new ProductFilter();
       private List<Product> originalProducts, tempProducts;

       @Override
       public View getView(int i, View view, ViewGroup parent)
       {
              LayoutInflater inflater = LayoutInflater.from(getContext());
              view = inflater.inflate(R.layout.product_layout, parent, false);
              TextView text_product_name = view.findViewById(R.id.product_name);
              TextView text_product_price = view.findViewById(R.id.product_price);
              TextView text_product_alarm = view.findViewById(R.id.product_alarm);
              Product p = getItem(i);
              int textColor = p.isAvailableNow() ? ContextCompat.getColor(getContext(), R.color.colorSpringGreen) : Color.RED;
              text_product_name.setText(p.getName());
              text_product_price.setTextColor(textColor);
              text_product_price.setText(p.getActualPrice());
              if (p.getActiveAlarms() == 0) text_product_alarm.setBackgroundColor(Color.WHITE);
              else
              {
                     //text_product_alarm.setText(String.valueOf(p.getActiveAlarms()));
                     text_product_alarm.setBackgroundColor(Color.rgb(255, 155, 0));
              }

              Linkify.addLinks(text_product_name, Linkify.WEB_URLS);
              return view;
       }

       @Override
       public int getCount()
       {
              if (originalProducts == null) return 0;
              return originalProducts.size();
       }

       @Override
       public long getItemId(int position)
       {
              return position;
       }

       @Nullable
       @Override
       public Product getItem(int position)
       {
              return originalProducts.get(position);
       }

       @Override
       public void clear()
       {
              if (originalProducts != null) originalProducts.clear();
       }

       @Override
       public void addAll(@NonNull Collection<? extends Product> collection)
       {
              originalProducts = new ArrayList<>(collection);
              tempProducts = originalProducts;
       }

       @Override
       public void sort(@NonNull Comparator<? super Product> comparator)
       {
              Collections.sort(originalProducts, comparator);
       }

       @NonNull
       @Override
       public Filter getFilter()
       {
              return productFilter;
       }

       private class ProductFilter extends Filter
       {
              @Override
              protected FilterResults performFiltering(CharSequence constraint)
              {
                     FilterResults result = new FilterResults();

                     if (constraint != null && constraint.length() > 0)
                     {
                            constraint = constraint.toString().toLowerCase();

                            ArrayList<Product> filteredProducts = new ArrayList<>();

                            Product p;
                            for (int i = 0; i < tempProducts.size(); i++)
                            {
                                   p = tempProducts.get(i);
                                   if (p.getName().toLowerCase().contains(constraint))
                                   {
                                          filteredProducts.add(p);
                                   }
                            }

                            result.count = filteredProducts.size();
                            result.values = filteredProducts;
                     } else
                     {
                            result.count = tempProducts.size();
                            result.values = tempProducts;
                     }

                     return result;
              }

              @Override
              protected void publishResults(CharSequence charSequence, FilterResults filterResults)
              {
                     originalProducts = (ArrayList<Product>)filterResults.values;
                     ProductAdapter.this.notifyDataSetChanged();
              }
       }
}
