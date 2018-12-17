package com.jagerdev.foxhoundpricetracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jagerdev.foxhoundpricetracker.products.Frequency;
import com.jagerdev.foxhoundpricetracker.products.ProductSnapshotComparator;
import com.jagerdev.foxhoundpricetracker.utils.AndroidUtil;
import com.jagerdev.foxhoundpricetracker.utils.ServiceRunHandler;

import java.util.Collections;
import java.util.List;

import controllers.exceptions.ImproperPathSelectorException;
import controllers.exceptions.PathForProductNotFoundException;
import controllers.exceptions.SourcePageNotAvailableException;
import controllers.validators.OnInvalidInput;
import database.DatabaseException;
import model.Product;
import model.ProductSnapshot;
import tracker.PriceTrackerManager;
import tracker.PriceTrackerService;
import tracker.clientnotifier.PriceTrackEvent;

public class ProductInfoActivity extends AppCompatActivity implements View.OnClickListener, OnInvalidInput, PriceTrackEvent
{

       private LinearLayout list_history;
       private PriceTrackerService priceTrackerService;
       private PriceTrackerManager priceTrackerManager;
       private ServiceRunHandler svcRunHandler;
       private Product respectiveProduct;

       private EditText edit_product_name, edit_product_path, edit_product_inspect_freq;
       private TextView txt_last_check, txt_product_status, txt_record_datetime, txt_alarm_count;
       private Spinner edit_product_inspect_unit;
       private TextView txt_product_name;
       private EditText edit_product_price;
       private TextView txt_product_actual_price;
       private LinearLayout panel_actual_price;
       private ImageButton btn_edit_price;
       private Button btn_retrack_product;
       private ImageButton btn_ack_alarms;

       @Override
       protected void onResume()
       {
              svcRunHandler.uiActivated(this.getClass().getName());
              if (priceTrackerService != null)
              {
                     priceTrackerService.addEventListener(this);
              }
              Thread historySetter = new Thread(new Runnable()
              {
                     @Override
                     public void run()
                     {
                            setItemsToList(respectiveProduct);
                     }
              });
              historySetter.start();
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

       @Override
       protected void onCreate(Bundle savedInstanceState)
       {
              super.onCreate(savedInstanceState);
              setContentView(R.layout.activity_product_info);

              Toolbar product_info_toolbar = findViewById(R.id.product_info_toolbar);
              setSupportActionBar(product_info_toolbar);

              svcRunHandler = ServiceRunHandler.getInstance();

              edit_product_name = findViewById(R.id.edit_product_name);
              edit_product_path = findViewById(R.id.edit_product_path);
              edit_product_inspect_freq = findViewById(R.id.edit_product_inspect_freq);

              txt_alarm_count = findViewById(R.id.txt_alarm_count);
              txt_last_check = findViewById(R.id.txt_last_check);
              txt_product_status = findViewById(R.id.txt_product_status);
              txt_record_datetime = findViewById(R.id.txt_record_datetime);
              txt_product_name = findViewById(R.id.txt_product_name);
              edit_product_price = findViewById(R.id.edit_product_price);
              txt_product_actual_price = findViewById(R.id.txt_product_actual_price);
              panel_actual_price = findViewById(R.id.panel_actual_price);
              btn_ack_alarms = findViewById(R.id.btn_ack_alarms);
              btn_retrack_product = findViewById(R.id.btn_retrack_product);
              btn_edit_price = findViewById(R.id.btn_edit_price);

              btn_ack_alarms.setOnClickListener(this);
              btn_retrack_product.setOnClickListener(this);
              btn_edit_price.setOnClickListener(this);
              edit_product_inspect_unit = findViewById(R.id.edit_product_inspect_unit);

              try
              {
                     priceTrackerService = PriceTrackerService.getInstance();
                     priceTrackerManager = new PriceTrackerManager(this);
              } catch (DatabaseException e)
              {
                     e.printStackTrace();
              }

              String productId = getIntent().getStringExtra("product_id");
              respectiveProduct = priceTrackerService.getProduct(productId);

              txt_last_check.setText(respectiveProduct.getDateOfLastCheck().toString("yyyy-MM-dd HH:mm:ss"));
              txt_record_datetime.setText(respectiveProduct.getDateOfRecord().toString("yyyy-MM-dd HH:mm:ss"));

              Frequency frequency = convertInspectFrequency(respectiveProduct.getInspectFrequency());
              edit_product_name.setText(respectiveProduct.getName());
              edit_product_path.setText(respectiveProduct.getWebPath());
              edit_product_price.setText(respectiveProduct.getActualPrice());
              txt_product_actual_price.setText(respectiveProduct.getActualPrice());
              edit_product_inspect_freq.setText(String.valueOf(frequency.frequency));
              setAvailability(respectiveProduct.isAvailableNow() ? ProductStatus.AVAILABLE : ProductStatus.NOT_AVAILABLE);

              if (respectiveProduct.getActiveAlarms() > 0)
              {
                     txt_alarm_count.setText(String.valueOf(respectiveProduct.getActiveAlarms()));
                     changeAlarmsVisibility(View.VISIBLE);
              } else changeAlarmsVisibility(View.GONE);

              ArrayAdapter<String> unitsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Frequency.UNITS_NAMES);
              unitsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
              edit_product_inspect_unit.setAdapter(unitsAdapter);
              edit_product_inspect_unit.setSelection(frequency.getIndex());

              list_history = findViewById(R.id.list_history);
              txt_product_name.requestFocus();
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
       public void priceChanges(final String oldPrice, final String newPrice, final Product product)
       {
              runOnUiThread(new Runnable()
              {
                     @Override
                     public void run()
                     {
                            txt_product_actual_price.setText(newPrice);
                     }
              });
       }

       @Override
       public void availabilityChanges(final boolean available, final Product product)
       {
              runOnUiThread(new Runnable()
              {
                     @Override
                     public void run()
                     {
                            setAvailability(available ? ProductStatus.AVAILABLE : ProductStatus.NOT_AVAILABLE);
                     }
              });
       }

       @Override
       public void productChecked(final Product product)
       {
              runOnUiThread(new Runnable()
              {
                     @Override
                     public void run()
                     {
                            txt_last_check.setText(product.getDateOfLastCheck().toString("yyyy-MM-dd HH:mm:ss"));
                            Thread historySetter = new Thread(new Runnable()
                            {
                                   @Override
                                   public void run()
                                   {
                                          setItemsToList(respectiveProduct);
                                   }
                            });
                            historySetter.start();
                     }
              });
       }

       enum ProductStatus
       {
              AVAILABLE,
              NOT_AVAILABLE
       }

       private void setAvailability(ProductStatus status)
       {
              switch (status)
              {
                     case AVAILABLE:
                            txt_product_status.setText("Available");
                            txt_product_status.setTextColor(getResources().getColor(R.color.colorSpringGreen));
                            break;
                     case NOT_AVAILABLE:
                            txt_product_status.setText("Not available");
                            txt_product_status.setTextColor(Color.RED);
                            break;
              }
       }

       public static Frequency convertInspectFrequency(int inspectFrequencySeconds)
       {
              int divider = 86400;
              int res;
              String[] units = {"day", "hour", "min", "sec"};
              int[] dividers = {24, 60, 60};

              for (int i = 0; i < units.length; i++)
              {
                     String unit = units[i];
                     int result = inspectFrequencySeconds / divider;
                     res = inspectFrequencySeconds % divider;
                     if (res == 0)
                     {
                            return new Frequency(unit, result);
                     }
                     divider = divider / dividers[i];
              }
              return new Frequency("sec", inspectFrequencySeconds);
       }

       private void setItemsToList(Product product)
       {
              try
              {
                     Thread.sleep(50);
              } catch (InterruptedException e)
              {
                     e.printStackTrace();
              }
              final List<ProductSnapshot> history = priceTrackerService.getProductHistory(product.getId());
              Collections.sort(history, new ProductSnapshotComparator());
              runOnUiThread(new Runnable()
              {
                     @Override
                     public void run()
                     {
                            list_history.removeAllViews();
//                            adapter.addAll(history);
                            for (ProductSnapshot ps : history)
                                   list_history.addView(createViewForListItem(list_history, ps));
                     }
              });
       }

       private void changeEditPrice()
       {
              panel_actual_price.setVisibility(View.GONE);
              edit_product_price.setVisibility(View.VISIBLE);
              edit_product_price.requestFocus();
       }

       private void saveProduct()
       {
              String productName = edit_product_name.getText().toString();
              String inspectFreq = edit_product_inspect_freq.getText().toString();
              String ifUnit = Frequency.UNITS[edit_product_inspect_unit.getSelectedItemPosition()];
              try
              {
                     priceTrackerManager.saveProduct(respectiveProduct.getId(), productName, inspectFreq, ifUnit);
                     showInfo(String.format("Product saved: %s", productName));
              } catch (DatabaseException e)
              {
                     showInfo(e.getMessage());
              }
       }

       private void removeDialog()
       {
              DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
              {
                     @Override
                     public void onClick(DialogInterface dialog, int which)
                     {
                            switch (which)
                            {
                                   case DialogInterface.BUTTON_POSITIVE:
                                          removeProduct();
                                          Intent main = new Intent(ProductInfoActivity.this, MainActivity.class);
                                          main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                          startActivity(main);
                                          break;

                                   case DialogInterface.BUTTON_NEGATIVE:
                                          //No button clicked
                                          break;
                            }
                     }
              };

              AlertDialog.Builder builder = new AlertDialog.Builder(this);
              builder.setMessage("Are you sure you want to remove the product?").setPositiveButton("Yes", dialogClickListener)
                      .setNegativeButton("No", dialogClickListener).show();
       }

       private void showInfo(final String message)
       {
              runOnUiThread(new Runnable()
              {
                     @Override
                     public void run()
                     {
                            Toast.makeText(ProductInfoActivity.this, message, Toast.LENGTH_LONG).show();
                     }
              });
       }

       private void removeProduct()
       {
              // TODO popup
              String productId = respectiveProduct.getId();
              priceTrackerService.deleteProduct(productId);
              Toast.makeText(this, "Product removed.", Toast.LENGTH_SHORT).show();
              // TODO redirect to list
       }

       private void retrackProduct()
       {
              final String productId = respectiveProduct.getId();
              final String productName = edit_product_name.getText().toString();
              final String productUrl = edit_product_path.getText().toString();
              final String newPrice = edit_product_price.getText().toString();
              final String inspectFreq = edit_product_inspect_freq.getText().toString();
              final String ifUnit = Frequency.UNITS[edit_product_inspect_unit.getSelectedItemPosition()];

              Thread t = new Thread(new Runnable()
              {
                     @Override
                     public void run()
                     {
                            try
                            {
                                   priceTrackerManager.updateProduct(productId, newPrice, productUrl, productName, inspectFreq, ifUnit);
                                   showInfo(String.format("Product updated: %s", productName));
                            } catch (ImproperPathSelectorException e)
                            {
                                   showInfo(e.getMessage());
                                   e.printStackTrace();
                            } catch (SourcePageNotAvailableException e)
                            {
                                   showInfo(e.getMessage());
                                   e.printStackTrace();
                            } catch (PathForProductNotFoundException e)
                            {
                                   showInfo(e.getMessage());
                                   e.printStackTrace();
                            } catch (DatabaseException e)
                            {
                                   showInfo(e.getMessage());
                                   e.printStackTrace();
                            }
                     }
              });
              t.start();
       }

       private void acknowledgeAlarms()
       {
              String productId = respectiveProduct.getId();
              int alarmCount = respectiveProduct.getActiveAlarms();
              priceTrackerService.acknowledgeAlarms(productId);
              Toast.makeText(this, String.format("%s alarms were acknowledged.", alarmCount), Toast.LENGTH_SHORT).show();
              changeAlarmsVisibility(View.GONE);
       }

       private void forceRefreshProduct()
       {
              Thread refresher = new Thread(new Runnable()
              {
                     @Override
                     public void run()
                     {
                            try
                            {
                                   priceTrackerService.forceProductPriceCheck(respectiveProduct.getId());

                            } catch (DatabaseException e)
                            {
                            }
                            AndroidUtil.toastOnThread(ProductInfoActivity.this, String.format("%s have been refreshed", respectiveProduct.getName()));
                     }
              });
              refresher.start();
       }

       private void changeAlarmsVisibility(int visibility)
       {
              txt_alarm_count.setVisibility(visibility);
              btn_ack_alarms.setVisibility(visibility);
       }

       @Override
       public boolean onCreateOptionsMenu(Menu menu)
       {
              // Inflate the menu; this adds items to the action bar if it is present.
              getMenuInflater().inflate(R.menu.menu_product_info, menu);
              return true;
       }

       @Override
       public boolean onOptionsItemSelected(MenuItem item)
       {
              int id = item.getItemId();

              switch (id)
              {
                     case R.id.action_settings:
                            return true;
                     case R.id.item_remove_product:
                            removeDialog();
                            break;
                     case R.id.item_save_product:
                            saveProduct();
                            break;
                     case R.id.menu_refresh_product:
                            forceRefreshProduct();
                            break;
              }

              return super.onOptionsItemSelected(item);
       }

       @Override
       public void onClick(View view)
       {
              switch (view.getId())
              {
                     case R.id.btn_edit_price:
                            changeEditPrice();
                            break;
                     case R.id.btn_retrack_product:
                            retrackProduct();
                            break;
                     case R.id.btn_ack_alarms:
                            acknowledgeAlarms();
                            break;
              }
       }

       @Override
       public void invalidInput(Object o, String s)
       {
              showInfo(String.format("INVALID INPUT for %s. Details: %s", o.toString(), s));
       }
}
