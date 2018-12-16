package com.jagerdev.foxhoundpricetracker;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.jagerdev.foxhoundpricetracker.products.Frequency;
import com.jagerdev.foxhoundpricetracker.utils.ServiceRunHandler;

import controllers.exceptions.ImproperPathSelectorException;
import controllers.exceptions.PathForProductNotFoundException;
import controllers.exceptions.SourcePageNotAvailableException;
import controllers.validators.OnInvalidInput;
import database.DatabaseException;
import tracker.PriceTrackerManager;

public class NewProductActivity extends AppCompatActivity implements View.OnClickListener, OnInvalidInput
{
       private ServiceRunHandler svcRunHandler;
       private PriceTrackerManager priceTrackerManager;
       private EditText new_product_name;
       private EditText new_product_path;
       private EditText new_product_price;
       private EditText new_product_inspect_freq;
       private Spinner new_product_inspect_unit;

       public final static String COPY_NAME = "copy_name";
       public final static String COPY_URL = "copy_url";
       public final static String COPY_PRICE = "copy_price";

       private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;

       @Override
       protected void onCreate(Bundle savedInstanceState)
       {
              super.onCreate(savedInstanceState);
              setContentView(R.layout.activity_new_product);

              Toolbar new_product_toolbar = findViewById(R.id.new_product_toolbar);
              setSupportActionBar(new_product_toolbar);

              svcRunHandler = ServiceRunHandler.getInstance();

              Button btn_track_product = findViewById(R.id.btn_track_product);
              new_product_name = findViewById(R.id.new_product_name);
              new_product_path = findViewById(R.id.new_product_path);
              new_product_price = findViewById(R.id.new_product_price);
              new_product_inspect_freq = findViewById(R.id.new_product_inspect_freq);
              new_product_inspect_unit = findViewById(R.id.new_product_inspect_unit);

              String name = getIntent().getStringExtra(COPY_NAME);
              String url = getIntent().getStringExtra(COPY_URL);
              String price = getIntent().getStringExtra(COPY_PRICE);

              new_product_name.setText(name);
              new_product_path.setText(url);
              new_product_price.setText(price);

              ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Frequency.UNITS_NAMES);
              adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
              new_product_inspect_unit.setAdapter(adapter);
              setDefaultInspectTime();

              try
              {
                     priceTrackerManager = new PriceTrackerManager(this);
              } catch (DatabaseException e)
              {
              }

              btn_track_product.setOnClickListener(this);
       }

       @Override
       protected void onResume()
       {
              svcRunHandler.uiActivated(this.getClass().getName());
              super.onResume();
       }

       @Override
       protected void onStop()
       {
              super.onStop();
              svcRunHandler.uiDeactivated(this.getClass().getName());
       }

       @Override
       public void onClick(View view)
       {
              switch (view.getId())
              {
                     case R.id.btn_track_product:
                            trackNewItem();
                            break;
              }

       }

       @Override
       public boolean onCreateOptionsMenu(Menu menu)
       {
              // Inflate the menu; this adds items to the action bar if it is present.
              getMenuInflater().inflate(R.menu.menu_new_product, menu);
              return true;
       }

       @Override
       public boolean onOptionsItemSelected(MenuItem item)
       {
              int id = item.getItemId();

              switch (id)
              {
                     case R.id.item_clean_fields:
                            clearFields();
                            break;
                     case R.id.item_copy_productinfo:
                            startFloatingCopyWidget();
                            break;
              }

              return super.onOptionsItemSelected(item);
       }

       private void startFloatingCopyWidget()
       {
              //Check if the application has draw over other apps permission or not?
              //This permission is by default available for API<23. But for API > 23
              //you have to ask for the permission in runtime.
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                     //If the draw over permission is not available open the settings screen to grant the permission.
                     Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                             Uri.parse("package:" + getPackageName()));
                     startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
              } else {
                     initializeFloatingCopyView();
              }
       }

       /**
        * Set and initialize the view elements.
        */
       private void initializeFloatingCopyView() {
              startService(new Intent(this, FloatingCopyService.class));
//              finish();
       }

       private void clearFields()
       {
              new_product_name.setText("");
              new_product_path.setText("");
              new_product_price.setText("");
              new_product_inspect_freq.setText("");
              setDefaultInspectTime();
              new_product_name.requestFocus();
       }

       private void setDefaultInspectTime()
       {
              new_product_inspect_freq.setText("12");
              new_product_inspect_unit.setSelection(2);
       }

       private void trackNewItem()
       {
              final String productName = new_product_name.getText().toString();
              final String productWebPath = new_product_path.getText().toString();
              final String productPrice = new_product_price.getText().toString();
              final String productInspectFreq = new_product_inspect_freq.getText().toString();
              final String productIFUnit = Frequency.UNITS[new_product_inspect_unit.getSelectedItemPosition()];
              Thread t = new Thread(new Runnable()
              {
                     @Override
                     public void run()
                     {
                            try
                            {
                                   priceTrackerManager.trackNewItem(productPrice, productWebPath, productName, productInspectFreq, productIFUnit);
                                   showInfo(String.format("%s is added to tracked items: %s", productName, productPrice));
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

       private void showInfo(final String message)
       {
              runOnUiThread(new Runnable()
              {
                     @Override
                     public void run()
                     {
                            Toast.makeText(NewProductActivity.this, message, Toast.LENGTH_LONG).show();
                     }
              });
       }

       @Override
       public void invalidInput(Object o, String s)
       {
              showInfo(String.format("INVALID INPUT for %s. Details: %s", o.toString(), s));
       }
}
