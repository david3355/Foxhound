package com.jagerdev.foxhoundpricetracker;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jagerdev.foxhoundpricetracker.products.ProductSnapshotComparator;
import com.jagerdev.foxhoundpricetracker.products.UniqueSelector;
import com.jagerdev.foxhoundpricetracker.utils.ServiceRunHandler;

import java.util.Collections;
import java.util.List;

import database.DatabaseException;
import model.Product;
import model.ProductSnapshot;
import tracker.PriceTrackerService;
import tracker.ProductAvailability;
import tracker.clientnotifier.PriceTrackEvent;

public class PriceHistoryActivity extends AppCompatActivity implements PriceTrackEvent
{
       private LinearLayout list_history, progress_loading_history;
       private TextView txt_history_product_name;

       private PriceTrackerService priceTrackerService;
       private Product respectiveProduct;
       private ServiceRunHandler svcRunHandler;

       @Override
       protected void onCreate(Bundle savedInstanceState)
       {
              super.onCreate(savedInstanceState);
              setContentView(R.layout.activity_price_history);
              svcRunHandler = ServiceRunHandler.getInstance();
              list_history = findViewById(R.id.list_history);
              txt_history_product_name = findViewById(R.id.txt_history_product_name);
              progress_loading_history = findViewById(R.id.progress_loading_history);

              try
              {
                     priceTrackerService = PriceTrackerService.getInstance();
              } catch (DatabaseException e)
              {
                     e.printStackTrace();
              }

              String productId = getIntent().getStringExtra("product_id");
              boolean archived = getIntent().getBooleanExtra("is_archived", false);
              respectiveProduct = archived ? priceTrackerService.getArchivedProduct(productId) : priceTrackerService.getProduct(productId); // what if product archived

              txt_history_product_name.setText(respectiveProduct.getName());
       }

       private void setItemsToList(final Product product, boolean runOnThread)
       {
              progress_loading_history.setVisibility(View.VISIBLE);

              if (!runOnThread) setItems(product);
              else
              {
                     Thread historySetter = new Thread(new Runnable()
                     {
                            @Override
                            public void run()
                            {

                                   setItems(product);
                            }
                     });
                     historySetter.start();
              }
       }

       private void setItems(Product product)
       {
              List<ProductSnapshot> history = priceTrackerService.getProductHistory(product.getId());
              UniqueSelector<ProductSnapshot> selector = new UniqueSelector<>();
              final List<ProductSnapshot> uniqueHistory = selector.getUniqueList(history);
              Collections.sort(uniqueHistory, new ProductSnapshotComparator(false));

              runOnUiThread(new Runnable()
              {
                     @Override
                     public void run()
                     {
                            list_history.removeAllViews();
                            for (ProductSnapshot ps : uniqueHistory)
                                   list_history.addView(createViewForListItem(list_history, ps));
                            progress_loading_history.setVisibility(View.GONE);
                     }
              });
       }

       public View createViewForListItem(ViewGroup parent, ProductSnapshot product)
       {
              LayoutInflater inflater = LayoutInflater.from(this);
              View view = inflater.inflate(R.layout.history_item_layout, parent, false);
              TextView txt_record_date = view.findViewById(R.id.txt_record_date);
              TextView txt_product_price = view.findViewById(R.id.txt_product_price);
              txt_record_date.setText(product.getDateOfSnapshot().toString("yyyy-MM-dd HH:mm:ss"));
              txt_product_price.setText(product.getPrice());
              return view;
       }

       @Override
       // This method runs on a separate thread
       public void priceChanges(String s, String s1, Product product)
       {
              if (!respectiveProduct.getId().equals(product.getId())) return;
              Log.d(this.getClass().getName(), String.format("Price of %s is changed. Adding new entry to history.", product.getName()));
              runOnUiThread(new Runnable()
              {
                     @Override
                     public void run()
                     {
                            setItemsToList(respectiveProduct, false);
                     }
              });
       }

       @Override
       public void productChecked(final Product product)
       {
       }

       @Override
       public void availabilityChecked(boolean b, ProductAvailability b1, Product product, Exception e)
       {

       }

       @Override
       public void productAdded(Product product)
       {

       }

       @Override
       public void productRemoved(Product product)
       {

       }

       @Override
       protected void onResume()
       {
              svcRunHandler.uiActivated(this.getClass().getName());
              if (priceTrackerService != null)
              {
                     priceTrackerService.addEventListener(this);
              }

              setItemsToList(respectiveProduct, true);
              super.onResume();
       }

       @Override
       protected void onPause()
       {
              if (priceTrackerService != null)
              {
                     priceTrackerService.removeEventListener(this);
              }
              super.onPause();
       }

       @Override
       protected void onStop()
       {
              super.onStop();
              svcRunHandler.uiDeactivated(this.getClass().getName());
       }
}
