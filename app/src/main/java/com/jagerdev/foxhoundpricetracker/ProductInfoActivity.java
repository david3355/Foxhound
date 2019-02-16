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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jagerdev.foxhoundpricetracker.products.Frequency;
import com.jagerdev.foxhoundpricetracker.products.ProductSnapshotComparator;
import com.jagerdev.foxhoundpricetracker.products.ProductSource;
import com.jagerdev.foxhoundpricetracker.products.UniqueSelector;
import com.jagerdev.foxhoundpricetracker.products.UniversalPriceParser;
import com.jagerdev.foxhoundpricetracker.utils.AndroidUtil;
import com.jagerdev.foxhoundpricetracker.utils.ServiceRunHandler;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
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

import static com.jagerdev.foxhoundpricetracker.utils.Common.updateTextValue;

public class ProductInfoActivity extends AppCompatActivity implements View.OnClickListener, OnInvalidInput, PriceTrackEvent
{

       private LinearLayout list_history;
       private PriceTrackerService priceTrackerService;
       private PriceTrackerManager priceTrackerManager;
       private ServiceRunHandler svcRunHandler;
       private Product respectiveProduct;

       private EditText edit_product_name, edit_product_path, edit_product_inspect_freq;
       private TextView txt_product_url, label_product_path, label_product_price, label_product_name;
       private TextView txt_last_check, txt_product_status, txt_record_datetime, txt_alarm_count;
       private TextView txt_selected_data;
       private Spinner edit_product_inspect_unit;
       private EditText edit_product_price;
       private TextView txt_product_actual_price;
       private LinearLayout panel_actual_price;
       private ImageButton btn_edit_price;
       private Button btn_retrack_product;
       private ImageButton btn_ack_alarms;
       private ProgressBar progress_product_refresh;
       private ImageButton btn_edit_time_plus, btn_edit_time_minus;

       private GraphView priceHistoryGraph;

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

              label_product_name = findViewById(R.id.label_product_name);
              label_product_path = findViewById(R.id.label_product_path);
              label_product_price = findViewById(R.id.label_product_price);

              txt_product_url = findViewById(R.id.txt_product_url);
              txt_alarm_count = findViewById(R.id.txt_alarm_count);
              txt_last_check = findViewById(R.id.txt_last_check);
              txt_product_status = findViewById(R.id.txt_product_status);
              txt_record_datetime = findViewById(R.id.txt_record_datetime);
              edit_product_price = findViewById(R.id.edit_product_price);
              txt_product_actual_price = findViewById(R.id.txt_product_actual_price);
              panel_actual_price = findViewById(R.id.panel_actual_price);
              btn_ack_alarms = findViewById(R.id.btn_ack_alarms);
              btn_retrack_product = findViewById(R.id.btn_retrack_product);
              btn_edit_price = findViewById(R.id.btn_edit_price);
              progress_product_refresh = findViewById(R.id.progress_product_refresh);

              priceHistoryGraph = findViewById(R.id.price_history_graph);
              txt_selected_data = findViewById(R.id.txt_selected_data);
              txt_selected_data.setOnClickListener(this);

              btn_ack_alarms.setOnClickListener(this);
              btn_retrack_product.setOnClickListener(this);
              btn_edit_price.setOnClickListener(this);
              edit_product_inspect_unit = findViewById(R.id.edit_product_inspect_unit);

              btn_edit_time_plus = findViewById(R.id.btn_edit_time_plus);
              btn_edit_time_minus = findViewById(R.id.btn_edit_time_minus);

              btn_edit_time_plus.setOnClickListener(this);
              btn_edit_time_minus.setOnClickListener(this);

              txt_product_url.setOnClickListener(this);
              label_product_path.setOnClickListener(this);
              label_product_price.setOnClickListener(this);
              label_product_name.setOnClickListener(this);


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
              txt_product_url.setText(respectiveProduct.getWebPath());
              edit_product_price.setText(respectiveProduct.getActualPrice());
              txt_product_actual_price.setText(respectiveProduct.getActualPrice());
              edit_product_inspect_freq.setText(String.valueOf(frequency.frequency));
              setAvailability(respectiveProduct.isAvailableNow() ? ProductStatus.AVAILABLE : ProductStatus.NOT_AVAILABLE);

              checkAlarms();

              ArrayAdapter<String> unitsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Frequency.UNITS_NAMES);
              unitsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
              edit_product_inspect_unit.setAdapter(unitsAdapter);
              edit_product_inspect_unit.setSelection(frequency.getIndex());

              list_history = findViewById(R.id.list_history);
              edit_product_name.requestFocus();
       }

       private void checkAlarms()
       {
              if (respectiveProduct.getActiveAlarms() > 0)
              {
                     txt_alarm_count.setText(String.valueOf(respectiveProduct.getActiveAlarms()));
                     changeAlarmsVisibility(View.VISIBLE);
              } else changeAlarmsVisibility(View.GONE);
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
              if (!respectiveProduct.getId().equals(product.getId())) return;
              runOnUiThread(new Runnable()
              {
                     @Override
                     public void run()
                     {
                            checkAlarms();
                            txt_product_actual_price.setText(newPrice);
                            edit_product_price.setText(newPrice);
                     }
              });
       }

       @Override
       public void availabilityChanges(final boolean available, final Product product, Exception error)
       {
              if (!respectiveProduct.getId().equals(product.getId())) return;
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
              if (!respectiveProduct.getId().equals(product.getId())) return;
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

       @Override
       public void productAdded(Product product)
       {
              AndroidUtil.toastOnThread(this, String.format("New product added: %s", product.getName()));
       }

       @Override
       public void productRemoved(Product product)
       {
              AndroidUtil.toastOnThread(this, String.format("Product removed: %s", product.getName()));
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
                     Thread.sleep(200);
              } catch (InterruptedException e)
              {
                     e.printStackTrace();
              }
              List<ProductSnapshot> history = priceTrackerService.getProductHistory(product.getId());
              UniqueSelector<ProductSnapshot> selector = new UniqueSelector<>();
              final List<ProductSnapshot> uniqueHistory = selector.getUniqueList(history);

              drawGraph(uniqueHistory);

              Collections.sort(uniqueHistory, new ProductSnapshotComparator(false));

              runOnUiThread(new Runnable()
              {
                     @Override
                     public void run()
                     {
                            list_history.removeAllViews();
//                            adapter.addAll(history);
                            for (ProductSnapshot ps : uniqueHistory)
                                   list_history.addView(createViewForListItem(list_history, ps));
                     }
              });
       }

       private void drawGraph(List<ProductSnapshot> uniqueHistory)
       {
              priceHistoryGraph.removeAllSeries();

              DateFormat format = new SimpleDateFormat("MM/dd");
              DateAsXAxisLabelFormatter dateFormatter = new DateAsXAxisLabelFormatter(getApplicationContext(), format);

              priceHistoryGraph.getGridLabelRenderer().setLabelFormatter(dateFormatter);
              priceHistoryGraph.getGridLabelRenderer().setNumVerticalLabels(3);
//              priceHistoryGraph.getGridLabelRenderer().setNumHorizontalLabels(3);
              priceHistoryGraph.getGridLabelRenderer().setHorizontalLabelsVisible(false);

              if (uniqueHistory.size() >= 1)
              {
                     priceHistoryGraph.getViewport().setXAxisBoundsManual(true);
                     priceHistoryGraph.getViewport().setMinX(uniqueHistory.get(0).getDateOfSnapshot().getMillis());
                     priceHistoryGraph.getViewport().setMaxX(uniqueHistory.get(uniqueHistory.size() - 1).getDateOfSnapshot().minusDays(10).getMillis());
//                     priceHistoryGraph.getViewport().setScalable(true);
//                     priceHistoryGraph.getViewport().setScrollable(true);
              }

              LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
              series.setDrawDataPoints(true);


              UniversalPriceParser priceParser = new UniversalPriceParser();



              for (ProductSnapshot snapshot : uniqueHistory)
              {
                     double parsedPrice = priceParser.getPrice(snapshot.getPrice(), respectiveProduct.getId());
                     DataPoint point = new DataPoint(new Date(snapshot.getDateOfSnapshot().getMillis()), parsedPrice);

                     series.appendData(point, true, uniqueHistory.size());
              }

              final ProductSource source1 = new ProductSource(respectiveProduct.getName(), respectiveProduct.getWebPath(), series);
              series.setColor(source1.getColor());

              OnDataPointTapListener listener = new OnDataPointTapListener()
              {
                     @Override
                     public void onTap(Series series, DataPointInterface dataPoint)
                     {
                            String time = new DateTime((long)dataPoint.getX()).toString("yyyy-MM-dd HH:mm:ss");
                            String text = String.format("%s (%s)", dataPoint.getY(), time);
                            txt_selected_data.setText(text);
                            if (txt_selected_data.getVisibility() != View.VISIBLE) txt_selected_data.setVisibility(View.VISIBLE);
                     }
              };

              series.setOnDataPointTapListener(listener);


              priceHistoryGraph.addSeries(series);

       }

       private void changeEditProductKeyProperties()
       {
              panel_actual_price.setVisibility(View.GONE);
              txt_product_url.setVisibility(View.GONE);
              edit_product_path.setVisibility(View.VISIBLE);
              edit_product_price.setVisibility(View.VISIBLE);
              edit_product_price.requestFocus();
       }

       private void consolidateProductKeyProperties()
       {
              edit_product_path.setVisibility(View.GONE);
              edit_product_price.setVisibility(View.GONE);
              panel_actual_price.setVisibility(View.VISIBLE);
              txt_product_url.setVisibility(View.VISIBLE);
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
              String productId = respectiveProduct.getId();
              priceTrackerManager.removeProduct(productId);
              Toast.makeText(this, "Product removed.", Toast.LENGTH_SHORT).show();
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
                                   runOnUiThread(new Runnable()
                                   {
                                          @Override
                                          public void run()
                                          {
                                                 txt_product_url.setText(productUrl);
                                                 txt_product_actual_price.setText(newPrice);
                                                 consolidateProductKeyProperties();
                                          }
                                   });
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
                                   runOnUiThread(new Runnable()
                                   {
                                          @Override
                                          public void run()
                                          {
                                                 progress_product_refresh.setVisibility(View.GONE);
                                          }
                                   });
                            } catch (DatabaseException e)
                            {
                            }
                            AndroidUtil.toastOnThread(ProductInfoActivity.this, String.format("%s have been refreshed", respectiveProduct.getName()));
                     }
              });
              progress_product_refresh.setVisibility(View.VISIBLE);
              refresher.start();
       }

       private void changeAlarmsVisibility(int visibility)
       {
              txt_alarm_count.setVisibility(visibility);
              btn_ack_alarms.setVisibility(visibility);
       }

       private void shareProduct()
       {
              Intent share = new Intent(Intent.ACTION_SEND);
              share.setType("text/plain");
              String shareBody = respectiveProduct.getWebPath();
              String shareSubject = respectiveProduct.getName();
              share.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
              share.putExtra(Intent.EXTRA_TEXT, shareBody);
              startActivity(Intent.createChooser(share, "Share product via"));
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
                     case R.id.menu_open_link:
                            AndroidUtil.openInDefaultBrowser(this, respectiveProduct.getWebPath());
                            break;
                     case R.id.item_share_product:
                            shareProduct();
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
                            changeEditProductKeyProperties();
                            break;
                     case R.id.btn_retrack_product:
                            retrackProduct();
                            break;
                     case R.id.btn_ack_alarms:
                            acknowledgeAlarms();
                            break;
                     case R.id.label_product_name:
                            AndroidUtil.setClipboardText(this, respectiveProduct.getName());
                            showClipboardCopyMsg(respectiveProduct.getName());
                            break;
                     case R.id.label_product_path:
                            AndroidUtil.setClipboardText(this, respectiveProduct.getWebPath());
                            showClipboardCopyMsg(respectiveProduct.getWebPath());
                            break;
                     case R.id.label_product_price:
                            AndroidUtil.setClipboardText(this, respectiveProduct.getActualPrice());
                            showClipboardCopyMsg(respectiveProduct.getActualPrice());
                            break;
                     case R.id.btn_edit_time_plus:
                            updateTextValue(true, edit_product_inspect_freq);
                            break;
                     case R.id.btn_edit_time_minus:
                            updateTextValue(false, edit_product_inspect_freq);
                            break;
                     case R.id.txt_selected_data:
                            txt_selected_data.setVisibility(View.GONE);
                            break;
              }
       }

       private void showClipboardCopyMsg(String content)
       {
              Toast.makeText(this, "Copied to clipboard: " + content, Toast.LENGTH_SHORT).show();
       }

       @Override
       public void invalidInput(Object o, String s)
       {
              showInfo(String.format("INVALID INPUT for %s. Details: %s", o.toString(), s));
       }
}
