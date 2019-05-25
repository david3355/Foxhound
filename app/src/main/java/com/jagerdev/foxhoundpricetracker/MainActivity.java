package com.jagerdev.foxhoundpricetracker;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.jagerdev.foxhoundpricetracker.products.ProductAdapter;
import com.jagerdev.foxhoundpricetracker.products.ProductComparator;
import com.jagerdev.foxhoundpricetracker.utils.AndroidUtil;
import com.jagerdev.foxhoundpricetracker.utils.NetworkUtil;
import com.jagerdev.foxhoundpricetracker.utils.ServiceRunHandler;
import com.jagerdev.foxhoundpricetracker.utils.TrackerInitializer;

import java.util.Map;

import controllers.validators.OnInvalidInput;
import database.DatabaseException;
import model.Product;
import tracker.PriceTrackerService;
import tracker.clientnotifier.PriceTrackEvent;

public class MainActivity extends AppCompatActivity
        implements OnInvalidInput, PriceTrackEvent, AdapterView.OnItemLongClickListener,
        AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, View.OnLongClickListener
{
       private ListView list_products;
       private TextView txt_webpage_address;
       private LinearLayout panel_webpage_address;
       private PriceTrackerService priceTrackerService;
       private ProductAdapter productAdapter;
       private ServiceRunHandler svcRunHandler;

//       private SwipeRefreshLayout product_swipe_refresh;

       private SearchView search_bar_products;
       private boolean trackerSvcBound = false;

       private ServiceConnection trackerSvcConnection = new ServiceConnection()
       {
              @Override
              public void onServiceConnected(ComponentName componentName, IBinder iBinder)
              {
                     TrackerService.TrackerServiceBinder binder = (TrackerService.TrackerServiceBinder) iBinder;
                     TrackerService trackerSvc = binder.getService();
                     trackerSvcBound = true;
                     svcRunHandler.registerService(trackerSvc);
//                     svcRunHandler.uiActivated();
//                     trackerSvc.background();
              }

              @Override
              public void onServiceDisconnected(ComponentName componentName)
              {
                     trackerSvcBound = false;
              }
       };

       private SearchView.OnQueryTextListener searchQueryListener = new SearchView.OnQueryTextListener()
       {
              @Override
              public boolean onQueryTextSubmit(String s)
              {
                     return false;
              }

              @Override
              public boolean onQueryTextChange(String newText)
              {
                     productAdapter.getFilter().filter(newText);
                     return false;
              }
       };

       private SearchView.OnCloseListener searchCloseListener = new SearchView.OnCloseListener()
       {
              @Override
              public boolean onClose()
              {
                     productAdapter.getFilter().filter(null);
                     return false;
              }
       };

       @Override
       protected void onCreate(Bundle savedInstanceState)
       {
              super.onCreate(savedInstanceState);
              setContentView(R.layout.activity_main);
              Toolbar toolbar = findViewById(R.id.toolbar);
              svcRunHandler = ServiceRunHandler.getInstance();
              svcRunHandler.setDelayMsec(0);

              panel_webpage_address = findViewById(R.id.panel_webpage_address);
              txt_webpage_address = findViewById(R.id.txt_webpage_address);
              search_bar_products = findViewById(R.id.search_bar_products);
//              product_swipe_refresh = findViewById(R.id.product_swipe_refresh);
//              product_swipe_refresh.setOnRefreshListener(this);
              setSupportActionBar(toolbar);

              FloatingActionButton fab = findViewById(R.id.fab);
              fab.setOnClickListener(new View.OnClickListener()
              {
                     @Override
                     public void onClick(View view)
                     {
                            Intent newProductActivity = new Intent(MainActivity.this, NewProductActivity.class);
                            startActivity(newProductActivity);
//                            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                                    .setAction("Action", null).show();
                     }
              });

              list_products = findViewById(R.id.list_products);
              list_products.setOnItemLongClickListener(this);
              list_products.setOnItemClickListener(this);

              txt_webpage_address.setOnClickListener(this);
              txt_webpage_address.setOnLongClickListener(this);

              TrackerInitializer.initialize(this);

              productAdapter = new ProductAdapter(this);

              try
              {
                     priceTrackerService = PriceTrackerService.getInstance();
              } catch (DatabaseException e)
              {
                     e.printStackTrace();
              }

              list_products.setAdapter(productAdapter);

//              ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET},1);

//              if (!AndroidUtil.isServiceRunning(this, TrackerService.class)) startTrackerService();
       }

       private void checkWebserver()
       {
              if (!AndroidUtil.isServiceRunning(this, WebService.class))
              {
                     Intent webService = new Intent(this, WebService.class);
                     startService(webService);
              }

              String hostAddress = NetworkUtil.getWifiIp(this);
              if (hostAddress != null )   // TODO check if gui service is running
              {
                     txt_webpage_address.setText(String.format("http://%s:%s", hostAddress, WebService.GUI_PORT));
                     panel_webpage_address.setVisibility(View.VISIBLE);
              }
              else
              {
                     panel_webpage_address.setVisibility(View.GONE);
              }
       }

       @Override
       protected void onStart()
       {
              super.onStart();
              TrackerService.forceStopped = false;
              startTrackerService();
       }

       @Override
       protected void onResume()
       {
              svcRunHandler.uiActivated(this.getClass().getName());
              if (productAdapter != null && priceTrackerService != null)
              {
                     setItemsToList();
                     priceTrackerService.addEventListener(this);
                     productAdapter.notifyDataSetChanged();
              }

              checkWebserver();    // TODO user can turn it on and off manually later

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
              if (trackerSvcBound)
              {
//                     svcRunHandler.uiDeactivated();
//                     trackerSvc.foreground();
                     unbindService(trackerSvcConnection);
                     trackerSvcBound = false;
              }
       }

       private void setItemsToList()
       {
              final Map<String, Product> products = priceTrackerService.getAllProducts();
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

       private void startTrackerService()
       {
              Intent svc = new Intent(this, TrackerService.class);
              if (!AndroidUtil.isServiceRunning(this, TrackerService.class))
              {
                     startService(svc);
                     Toast.makeText(this, "Tracking service is started.", Toast.LENGTH_SHORT).show();
              }
              bindService(svc, trackerSvcConnection, 0);
       }

       private void stopTrackerService()
       {
              TrackerService.forceStopped = true;
              Intent svc = new Intent(this, TrackerService.class);
              stopService(svc);
              Toast.makeText(this, "Tracking service is stopped.", Toast.LENGTH_SHORT).show();
       }

       private void forceRefreshAllProducts()
       {
              Thread refresher = new Thread(new Runnable()
              {
                     @Override
                     public void run()
                     {
                            for (Product product : priceTrackerService.getAllProducts().values())
                            {
                                   try
                                   {
                                          priceTrackerService.forceProductPriceCheck(product.getId());

                                   } catch (DatabaseException e)
                                   {
                                   }
                            }
                            AndroidUtil.toastOnThread(MainActivity.this, "All products have been refreshed");
                            runOnUiThread(new Runnable()
                            {
                                   @Override
                                   public void run()
                                   {
//                                          product_swipe_refresh.setRefreshing(false);
                                   }
                            });
                     }
              });
              refresher.start();
       }

       @Override
       public boolean onCreateOptionsMenu(Menu menu)
       {
              // Inflate the menu; this adds items to the action bar if it is present.
              getMenuInflater().inflate(R.menu.menu_main, menu);
              MenuItem searchItem = menu.findItem(R.id.app_bar_search);
              SearchView searchInterface = (SearchView) searchItem.getActionView();
              searchInterface.setOnQueryTextListener(searchQueryListener);
              searchInterface.setOnCloseListener(searchCloseListener);
              return true;
       }

       @Override
       public boolean onOptionsItemSelected(MenuItem item)
       {
              // Handle action bar item clicks here. The action bar will
              // automatically handle clicks on the Home/Up button, so long
              // as you specify a parent activity in AndroidManifest.xml.
              int id = item.getItemId();

              //noinspection SimplifiableIfStatement
              switch (id)
              {
                     case R.id.action_settings:
                            Intent settingsIntent = new Intent(this, GlobalSettingsActivity.class);
                            startActivity(settingsIntent);
                            return true;
                     case R.id.stop_tracking_prices:
                            stopTrackerService();
                            return true;
                     case R.id.search_bar_products:
                            search_bar_products.setVisibility(View.VISIBLE);
                            break;
              }

              return super.onOptionsItemSelected(item);
       }

       @Override
       public void invalidInput(Object o, String s)
       {
              Toast.makeText(this, String.format("INVALID INPUT for %s. Details: %s", o.toString(), s), Toast.LENGTH_LONG).show();
       }

       @Override
       public void priceChanges(final String oldPrice, final String newPrice, final Product product)
       {
              runOnUiThread(new Runnable()
              {
                     @Override
                     public void run()
                     {
                            productAdapter.notifyDataSetChanged();
                            Toast.makeText(MainActivity.this, String.format("%s: price changed from %s to %s", product.getName(), oldPrice, newPrice), Toast.LENGTH_SHORT).show();
                     }
              });
       }

       @Override
       public void availabilityChanges(final boolean available, final Product product, Exception error)
       {
              runOnUiThread(new Runnable()
              {
                     @Override
                     public void run()
                     {
                            productAdapter.notifyDataSetChanged();
                            Toast.makeText(MainActivity.this, String.format("%s became %s", product.getName(), available ? "available" : "not available"), Toast.LENGTH_SHORT).show();
                     }
              });
       }

       @Override
       public void productChecked(final Product product)
       {

       }

       @Override
       public void productAdded(Product product)
       {
              setItemsToList();
              AndroidUtil.toastOnThread(this, String.format("New product added: %s", product.getName()));
       }

       @Override
       public void productRemoved(Product product)
       {
              setItemsToList();
              AndroidUtil.toastOnThread(this, String.format("Product removed: %s", product.getName()));
       }


       @Override
       public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
       {
              Product product = (Product) adapterView.getItemAtPosition(i);
              AndroidUtil.openInDefaultBrowser(this, product.getWebPath());
              return true;
       }

       @Override
       public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
       {
              Product product = (Product) adapterView.getItemAtPosition(i);
              Intent editProductActivity = new Intent(this, ProductInfoActivity.class);
              editProductActivity.putExtra("product_id", product.getId());
              startActivity(editProductActivity);
       }


       private void copyWebserviceAddress()
       {
              String address = txt_webpage_address.getText().toString();
              AndroidUtil.setClipboardText(this, address);
              Toast.makeText(this, "Copied to clipboard: " + address, Toast.LENGTH_SHORT).show();
       }

       @Override
       public void onRefresh()
       {
//              forceRefreshAllProducts();
//              product_swipe_refresh.setRefreshing(false);
       }

       @Override
       public void onClick(View view)
       {
              switch (view.getId())
              {
                     case R.id.txt_webpage_address:
                            copyWebserviceAddress();
                            break;
              }
       }

       @Override
       public boolean onLongClick(View view)
       {
              switch (view.getId())
              {
                     case R.id.txt_webpage_address:
                            AndroidUtil.openInDefaultBrowser(this, txt_webpage_address.getText().toString());
                            return false;
              }
              return false;
       }
}
