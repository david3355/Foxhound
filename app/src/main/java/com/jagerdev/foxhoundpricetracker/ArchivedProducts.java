package com.jagerdev.foxhoundpricetracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jagerdev.foxhoundpricetracker.products.ProductAdapter;
import com.jagerdev.foxhoundpricetracker.products.ProductComparator;
import com.jagerdev.foxhoundpricetracker.utils.ServiceRunHandler;

import java.util.Map;

import database.DatabaseException;
import model.Product;
import tracker.PriceTrackerService;

public class ArchivedProducts extends AppCompatActivity implements AdapterView.OnItemClickListener
{
       private ListView list_archived_products;
       private ProductAdapter productAdapter;
       private ServiceRunHandler svcRunHandler;
       private PriceTrackerService priceTrackerService;

       @Override
       protected void onCreate(Bundle savedInstanceState)
       {
              super.onCreate(savedInstanceState);
              setContentView(R.layout.activity_archived_products);
              svcRunHandler = ServiceRunHandler.getInstance();

              try
              {
                     priceTrackerService = PriceTrackerService.getInstance();
              } catch (DatabaseException e)
              {
                     e.printStackTrace();
              }

              list_archived_products = findViewById(R.id.list_archived_products);
              productAdapter = new ProductAdapter(this);
              list_archived_products.setAdapter(productAdapter);
              list_archived_products.setOnItemClickListener(this);
       }

       @Override
       protected void onResume()
       {
              svcRunHandler.uiActivated(this.getClass().getName());
              if (productAdapter != null )
              {
                     setItemsToList();
                     productAdapter.notifyDataSetChanged();
              }

              super.onResume();
       }

       @Override
       protected void onStop()
       {
              super.onStop();
              svcRunHandler.uiDeactivated(this.getClass().getName());
       }

       private void setItemsToList()
       {
              final Map<String, Product> products = priceTrackerService.getArchivedProducts();
              runOnUiThread(new Runnable()
              {
                     @Override
                     public void run()
                     {
                            productAdapter.clear();
                            productAdapter.addAll(products.values());
                            productAdapter.sort(new ProductComparator());
                     }
              });
       }

       @Override
       public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
       {
              Product product = (Product) adapterView.getItemAtPosition(i);
              Intent editProductActivity = new Intent(this, ProductInfoActivity.class);
              editProductActivity.putExtra("product_id", product.getId());
              editProductActivity.putExtra("is_archived", true);
              startActivity(editProductActivity);
       }
}
