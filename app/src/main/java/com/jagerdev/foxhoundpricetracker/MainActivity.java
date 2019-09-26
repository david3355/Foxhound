package com.jagerdev.foxhoundpricetracker;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.jagerdev.foxhoundpricetracker.products.ProductAdapter;
import com.jagerdev.foxhoundpricetracker.products.comparators.ProductComparator;
import com.jagerdev.foxhoundpricetracker.products.comparators.SortBy;
import com.jagerdev.foxhoundpricetracker.utils.AndroidUtil;
import com.jagerdev.foxhoundpricetracker.utils.DbFileProvider;
import com.jagerdev.foxhoundpricetracker.utils.NetworkUtil;
import com.jagerdev.foxhoundpricetracker.utils.ServiceRunHandler;
import com.jagerdev.foxhoundpricetracker.utils.TrackerInitializer;

import java.util.Map;

import controllers.validators.OnInvalidInput;
import database.DatabaseException;
import model.Product;
import tracker.PriceTrackerService;
import tracker.clientnotifier.PriceTrackEvent;

import static com.jagerdev.foxhoundpricetracker.database.DBConstants.DATABASE_NAME;

public class MainActivity extends AppCompatActivity
        implements OnInvalidInput, PriceTrackEvent, AdapterView.OnItemLongClickListener,
        AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, View.OnLongClickListener, NavigationView.OnNavigationItemSelectedListener {

    private ListView list_products;
       private TextView txt_webpage_address, txt_no_products;
       private LinearLayout panel_webpage_address;
       private PriceTrackerService priceTrackerService;
       private ProductAdapter productAdapter;
       private ServiceRunHandler svcRunHandler;

       private DrawerLayout navigationDrawerLayout;
       private MenuItem nav_pricetracker_service, nav_webpage_service;

       public static int PICKFILE_REQUEST_CODE = 25000;

//       private SwipeRefreshLayout product_swipe_refresh;

       private SearchView search_bar_products;
       private boolean trackerSvcBound = false;

       private Dialog sortDialog;
       private Button btn_sort_ascending, btn_sort_descending;
       private RadioButton sort_last_added, sort_product_name, sort_last_updated, sort_actual_price;

       public static final String PREFS_SORTBY = "SORT_BY";
       public static final String PREFS_ASCENDING = "ASCENDING";

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
              setSupportActionBar(toolbar);

              navigationDrawerLayout = findViewById(R.id.main_drawer_layout);
              ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, navigationDrawerLayout, toolbar, R.string.open_navigation_drawer, R.string.close_navigation_drawer);
              navigationDrawerLayout.addDrawerListener(toggle);
              toggle.syncState();

              NavigationView navigationView = findViewById(R.id.navigation_view);
              navigationView.setNavigationItemSelectedListener(this);
              Menu navigationMenu = navigationView.getMenu();
              nav_pricetracker_service = navigationMenu.findItem(R.id.nav_pricetracker_service);
              nav_webpage_service = navigationMenu.findItem(R.id.nav_webpage_service);

              sortDialog = new Dialog(this);
              sortDialog.setContentView(R.layout.sort_popup);
              btn_sort_ascending = sortDialog.findViewById(R.id.btn_sort_ascending);
              btn_sort_descending = sortDialog.findViewById(R.id.btn_sort_descending);
              sort_last_added = sortDialog.findViewById(R.id.sort_last_added);
              sort_last_updated = sortDialog.findViewById(R.id.sort_last_updated);
              sort_product_name = sortDialog.findViewById(R.id.sort_product_name);
              sort_actual_price = sortDialog.findViewById(R.id.sort_actual_price);

              btn_sort_ascending.setOnClickListener(this);
              btn_sort_descending.setOnClickListener(this);
              svcRunHandler = ServiceRunHandler.getInstance();
              svcRunHandler.setDelayMsec(0);

              txt_no_products = findViewById(R.id.txt_no_products);
              panel_webpage_address = findViewById(R.id.panel_webpage_address);
              txt_webpage_address = findViewById(R.id.txt_webpage_address);
              search_bar_products = findViewById(R.id.search_bar_products);
//              product_swipe_refresh = findViewById(R.id.product_swipe_refresh);
//              product_swipe_refresh.setOnRefreshListener(this);

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

              startTrackerService();

//              ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET},1);

//              if (!AndroidUtil.isServiceRunning(this, TrackerService.class)) startTrackerService();
       }

    @Override
    public void onBackPressed() {
           if (navigationDrawerLayout.isDrawerOpen(GravityCompat.START))
               navigationDrawerLayout.closeDrawer(GravityCompat.START);
           else
               super.onBackPressed();
    }

    private void toggleProductListVisibility()
       {
              if (productAdapter != null && productAdapter.getCount() > 0) {
                     txt_no_products.setVisibility(View.GONE);
                     list_products.setVisibility(View.VISIBLE);
              }
              else {
                     txt_no_products.setVisibility(View.VISIBLE);
                     list_products.setVisibility(View.GONE);
              }
       }

       private boolean isWebserviceRunning()
       {
               return AndroidUtil.isServiceRunning(this, WebService.class);
       }

       private boolean isTrackerServiceRunning()
       {
               return AndroidUtil.isServiceRunning(this, TrackerService.class);
       }

       private void toggleWebServer()
       {
              if (!isWebserviceRunning())
              {
                  Intent webService = new Intent(this, WebService.class);
                  startService(webService);

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
                  nav_webpage_service.setTitle(getResources().getString(R.string.stop_webservice));
              }
              else
              {
                  stopService(new Intent(this, WebService.class));
                  panel_webpage_address.setVisibility(View.GONE);
                  nav_webpage_service.setTitle(getResources().getString(R.string.start_webservice));
              }
       }

       @Override
       protected void onStart()
       {
              super.onStart();
              TrackerService.forceStopped = false;
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

              nav_webpage_service.setTitle(isWebserviceRunning() ? getResources().getString(R.string.stop_webservice) : getResources().getString(R.string.start_webservice));
              nav_pricetracker_service.setTitle(isTrackerServiceRunning() ? getResources().getString(R.string.stop_pricetracker_service) : getResources().getString(R.string.start_pricetracker_service));
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
              String sortByPref = AndroidUtil.readValueFromPrefs(PREFS_SORTBY, this, SortBy.LAST_ADDED.toString());
              boolean ascending = Boolean.valueOf(AndroidUtil.readValueFromPrefs(PREFS_ASCENDING, this, "false"));

              final ProductComparator comparator = ProductComparator.buildProductComparator(SortBy.valueOf(sortByPref), ascending);

              final Map<String, Product> products = priceTrackerService.getAllProducts();
              runOnUiThread(new Runnable()
              {
                     @Override
                     public void run()
                     {
                            productAdapter.clear();
                            productAdapter.addAll(products.values());
                            productAdapter.sort(comparator);
                            toggleProductListVisibility();
                     }
              });
       }

       private void startTrackerService()
       {
              Intent svc = new Intent(this, TrackerService.class);
              if (!isTrackerServiceRunning())
              {
                     startService(svc);
                     Toast.makeText(this, "Tracking service is started.", Toast.LENGTH_SHORT).show();
              }
              bindService(svc, trackerSvcConnection, 0);
              nav_pricetracker_service.setTitle(getResources().getString(R.string.stop_pricetracker_service));
       }

       private void stopTrackerService()
       {
              TrackerService.forceStopped = true;
              if (trackerSvcBound)
              {
                  unbindService(trackerSvcConnection);
                  trackerSvcBound = false;
              }
              Intent svc = new Intent(this, TrackerService.class);
              stopService(svc);
              Toast.makeText(this, "Tracking service is stopped.", Toast.LENGTH_SHORT).show();
              nav_pricetracker_service.setTitle(getResources().getString(R.string.start_pricetracker_service));
       }

       private void toggleTrackerService()
       {
           if (isTrackerServiceRunning()) stopTrackerService();
           else startTrackerService();
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

       private void showSortDialog()
       {
           sortDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
           SortBy sortBy = SortBy.valueOf(AndroidUtil.readValueFromPrefs(PREFS_SORTBY, this, SortBy.LAST_ADDED.toString()));
           switch (sortBy)
           {
               case LAST_ADDED:
                   sort_last_added.setChecked(true);
                   break;
               case LAST_UPDATED:
                   sort_last_updated.setChecked(true);
                   break;
               case PRODUCT_NAME:
                   sort_product_name.setChecked(true);
                   break;
               case ACTUAL_PRICE:
                   sort_actual_price.setChecked(true);
                   break;
           }
           sortDialog.show();
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
                     case R.id.menu_refresh_products:
                            forceRefreshAllProducts();
                            return true;
                     case R.id.menu_sort:
                            showSortDialog();
                            return true;
                     case R.id.search_bar_products:
                            search_bar_products.setVisibility(View.VISIBLE);
                            break;
              }

              return super.onOptionsItemSelected(item);
       }

       private void shareDatabaseFile()
       {
              Uri uri = new DbFileProvider().getDatabaseURI(this);
              String shareMessage = String.format("Backup %s via:", DATABASE_NAME);
              AndroidUtil.shareFile(this, uri, shareMessage);
       }

       private void importProducts(String filePath)
       {
              // TODO
       }

       private void selectFileToImport()
       {
              AndroidUtil.selectFile(this);
       }

       private boolean validateDatabaseFile(String filePath)
       {
              return true; // TODO
       }

       @Override
       protected void onActivityResult(int requestCode, int resultCode, Intent data)
       {
              if (requestCode == PICKFILE_REQUEST_CODE && resultCode == RESULT_OK)
              {
                     Uri chosenFile= data.getData();
                     String filePath = chosenFile.getPath();
                     if (validateDatabaseFile(filePath))
                     {
                            Toast.makeText(this, String.format("Importing products from: %s", filePath), Toast.LENGTH_SHORT).show();
                            importProducts(filePath);
                     }
                     else Toast.makeText(this, String.format("Database file is not valid: %s", filePath), Toast.LENGTH_SHORT).show();
              }
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
       public void availabilityChecked(final boolean previouslyAvailable, final boolean available, final Product product, Exception error)
       {
              if (previouslyAvailable != available)
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
       }

       @Override
       public void productChecked(final Product product)
       {

       }

       @Override
       public void productAdded(final Product product)
       {
              setItemsToList();
              runOnUiThread(new Runnable()
              {
                     @Override
                     public void run()
                     {
                            productAdapter.notifyDataSetChanged();
                            Toast.makeText(MainActivity.this, String.format("New product added: %s", product.getName()), Toast.LENGTH_SHORT).show();
                     }
              });
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
                     case R.id.btn_sort_ascending:
                            sortProducts(true);
                            break;
                     case R.id.btn_sort_descending:
                            sortProducts(false);
                            break;
              }
       }

       private SortBy getSortBy()
       {
           if (sort_last_updated.isChecked()) return SortBy.LAST_UPDATED;
           if (sort_product_name.isChecked()) return SortBy.PRODUCT_NAME;
           if (sort_actual_price.isChecked()) return SortBy.ACTUAL_PRICE;
           return SortBy.LAST_ADDED;
       }

       private void sortProducts(boolean ascending)
       {
            SortBy sortBy = getSortBy();
            AndroidUtil.saveValueToPrefs(PREFS_SORTBY, this, sortBy.toString());
            AndroidUtil.saveValueToPrefs(PREFS_ASCENDING, this, String.valueOf(ascending));
            setItemsToList();
            if (productAdapter != null) productAdapter.notifyDataSetChanged();
            sortDialog.dismiss();
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.nav_archived_products:
                startActivity(new Intent(this, ArchivedProducts.class));
                break;
            case R.id.nav_settings:
                Intent settingsIntent = new Intent(this, GlobalSettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.nav_export:
                shareDatabaseFile();
                break;
            case R.id.nav_import:
                selectFileToImport();
                break;
            case R.id.nav_webpage_service:
                toggleWebServer();
                break;
            case R.id.nav_pricetracker_service:
                toggleTrackerService();
                break;
        }
        return true;
    }
}
