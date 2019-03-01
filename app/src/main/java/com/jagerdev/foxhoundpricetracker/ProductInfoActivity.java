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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.jagerdev.foxhoundpricetracker.products.Frequency;
import com.jagerdev.foxhoundpricetracker.products.ProductSnapshotComparator;
import com.jagerdev.foxhoundpricetracker.products.UniqueSelector;
import com.jagerdev.foxhoundpricetracker.products.UniversalPriceParser;
import com.jagerdev.foxhoundpricetracker.utils.AndroidUtil;
import com.jagerdev.foxhoundpricetracker.utils.ServiceRunHandler;
import com.jagerdev.foxhoundpricetracker.utils.chart.HistoryChartMarkerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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

public class ProductInfoActivity extends AppCompatActivity implements View.OnClickListener, OnInvalidInput, PriceTrackEvent, OnChartValueSelectedListener
{

       private LinearLayout list_history;
       private PriceTrackerService priceTrackerService;
       private PriceTrackerManager priceTrackerManager;
       private ServiceRunHandler svcRunHandler;
       private Product respectiveProduct;

       private EditText edit_product_name, edit_product_path, edit_product_inspect_freq;
       private TextView txt_product_url, label_product_path, label_product_price, label_product_name;
       private TextView txt_last_check, txt_product_status, txt_record_datetime, txt_alarm_count;
       private Spinner edit_product_inspect_unit;
       private EditText edit_product_price;
       private TextView txt_product_actual_price;
       private LinearLayout panel_actual_price;
       private ImageButton btn_edit_price;
       private Button btn_retrack_product;
       private ImageButton btn_ack_alarms;
       private ProgressBar progress_product_refresh;
       private ImageButton btn_edit_time_plus, btn_edit_time_minus;
       private ImageView img_notif_settings_expand;

       private LineChart chart;

       private LinearLayout notification_settings_header, notification_settings_panel;

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

              notification_settings_header = findViewById(R.id.notification_settings_header);
              notification_settings_header.setOnClickListener(this);
              notification_settings_panel = findViewById(R.id.notification_settings_panel);
              img_notif_settings_expand = findViewById(R.id.img_notif_settings_expand);

              chart = findViewById(R.id.price_history_chart);

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

              // TODO set notificationSettings properties from loading database

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

       @Override
       public void onValueSelected(Entry e, Highlight h)
       {

       }

       @Override
       public void onNothingSelected()
       {

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

              initChart();
              drawChart(uniqueHistory);

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

       private void initChart()
       {
              // chart
              // no description text
              chart.getDescription().setEnabled(false);

              HistoryChartMarkerView mv = new HistoryChartMarkerView(this, R.layout.history_chart_marker);

              // Set the marker to the chart
              mv.setChartView(chart);
              chart.setMarker(mv);

              // enable touch gestures
              chart.setTouchEnabled(true);

              chart.setDragDecelerationFrictionCoef(0.9f);

              // enable scaling and dragging
              chart.setDragEnabled(true);
              chart.setScaleEnabled(false);
              chart.setDrawGridBackground(false);
              chart.setHighlightPerDragEnabled(true);

              // set an alternative background color
              //chart.setBackgroundColor(Color.WHITE);
              chart.setViewPortOffsets(0f, 0f, 0f, 0f);

              // get the legend (only possible after setting data)
              Legend l = chart.getLegend();
              l.setEnabled(false);

              XAxis xAxis = chart.getXAxis();
              xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
//              xAxis.setTypeface(tfLight);
              xAxis.setTextSize(10f);
              xAxis.setTextColor(Color.WHITE);
              xAxis.setDrawAxisLine(false);
              xAxis.setDrawGridLines(true);
              xAxis.setTextColor(Color.rgb(51, 174, 98));
//              xAxis.setTextColor(R.color.colorSpringGreen);
              xAxis.setCenterAxisLabels(true);
              xAxis.setGranularity(1f); // one hour
              xAxis.setValueFormatter(new IAxisValueFormatter()
              {
                     private final SimpleDateFormat mFormat = new SimpleDateFormat("MMM dd", Locale.ENGLISH);
                     @Override
                     public String getFormattedValue(float value, AxisBase axis)
                     {
                            long millis = TimeUnit.HOURS.toMillis((long) value);
                            millis = (long) value;
                            return mFormat.format(new Date(millis));
                     }
              });

              YAxis leftAxis = chart.getAxisLeft();
              leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
//              leftAxis.setTypeface(tfLight);
              leftAxis.setTextColor(ColorTemplate.getHoloBlue());
              leftAxis.setDrawGridLines(true);
              leftAxis.setGranularityEnabled(true);
              leftAxis.setAxisMinimum(0f);
              leftAxis.setAxisMaximum(170f);
              leftAxis.setYOffset(-9f);
              leftAxis.setTextColor(Color.rgb(51, 174, 98));
//              leftAxis.setTextColor(R.color.colorSpringGreen);

              YAxis rightAxis = chart.getAxisRight();
              rightAxis.setEnabled(false);
       }

       private void drawChart(List<ProductSnapshot> uniqueHistory)
       {
              ArrayList<Entry> values = new ArrayList<>();

              UniversalPriceParser priceParser = new UniversalPriceParser();
              for (ProductSnapshot snapshot : uniqueHistory)
              {
                     float parsedPrice = priceParser.getPrice(snapshot.getPrice(), respectiveProduct.getId());
                     Entry entry = new Entry(snapshot.getDateOfSnapshot().getMillis(), parsedPrice);
                     values.add(entry);
              }

              // create a dataset and give it a type
              List<ILineDataSet> dataSets = new ArrayList<>();

              LineDataSet set1 = new LineDataSet(values, "DataSet 1");
              set1.setAxisDependency(YAxis.AxisDependency.RIGHT);

              set1.setColor(ColorTemplate.getHoloBlue());
              set1.setValueTextColor(ColorTemplate.getHoloBlue());
              set1.setLineWidth(1.5f);
              set1.setDrawCircles(true);
              set1.setDrawValues(false);
              set1.setFillAlpha(65);
              set1.setFillColor(ColorTemplate.getHoloBlue());
              set1.setHighLightColor(Color.rgb(244, 117, 117));
              set1.setDrawCircleHole(false);

              dataSets.add(set1);
              // create a data object with the data sets
              LineData data = new LineData(dataSets);
              data.setValueTextColor(Color.WHITE);
              data.setValueTextSize(9f);

              // set data
              chart.setData(data);

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

       private void reTrackProduct()
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
                            reTrackProduct();
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
                     case R.id.notification_settings_header:
                            toggleNotificationSettingsPanel();
                            break;
              }
       }

       private void toggleNotificationSettingsPanel()
       {
              if (notification_settings_panel.getVisibility() == View.GONE)
              {
                     img_notif_settings_expand.setRotation(180);
                     notification_settings_panel.setVisibility(View.VISIBLE);
              }
              else
              {
                     img_notif_settings_expand.setRotation(0);
                     notification_settings_panel.setVisibility(View.GONE);
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
