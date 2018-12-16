package com.jagerdev.foxhoundpricetracker;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jagerdev.foxhoundpricetracker.utils.AndroidUtil;

public class FloatingCopyService extends Service implements View.OnClickListener, View.OnTouchListener
{
       private WindowManager mWindowManager;
       private View mFloatingView;
       private Intent newProductIntent;

       private int initialX;
       private int initialY;
       private float initialTouchX;
       private float initialTouchY;

       private WindowManager.LayoutParams params;
       private View collapsedView, expandedView;
       private TextView floating_copy_product_name, floating_copy_product_url, floating_copy_product_price;

       public FloatingCopyService()
       {
       }

       @Override
       public int onStartCommand(Intent intent, int flags, int startId)
       {
              newProductIntent = new Intent(this, NewProductActivity.class);
              return super.onStartCommand(intent, flags, startId);
       }

       @Override
       public IBinder onBind(Intent intent)
       {
              // TODO: Return the communication channel to the service.
              throw new UnsupportedOperationException("Not yet implemented");
       }

       @Override
       public void onCreate()
       {
              super.onCreate();

              //Inflate the floating view layout we created
              mFloatingView = LayoutInflater.from(this).inflate(R.layout.pricetracker_floating_widget, null);
              mFloatingView.setOnTouchListener(this);
              mFloatingView.setOnClickListener(this);

              LinearLayout floatingmenu_get_name = mFloatingView.findViewById(R.id.floatingmenu_get_name);
              LinearLayout floatingmenu_get_url = mFloatingView.findViewById(R.id.floatingmenu_get_url);
              LinearLayout floatingmenu_get_price = mFloatingView.findViewById(R.id.floatingmenu_get_price);
              LinearLayout floatingmenu_go_to_app = mFloatingView.findViewById(R.id.floatingmenu_go_to_app);
              View floating_close_btn = mFloatingView.findViewById(R.id.floating_close_btn);
              View floating_back_btn = mFloatingView.findViewById(R.id.floating_back_btn);


              floating_copy_product_name = mFloatingView.findViewById(R.id.floating_copy_product_name);
              floating_copy_product_url = mFloatingView.findViewById(R.id.floating_copy_product_url);
              floating_copy_product_price = mFloatingView.findViewById(R.id.floating_copy_product_price);

              floatingmenu_get_name.setOnClickListener(this);
              floatingmenu_get_url.setOnClickListener(this);
              floatingmenu_get_price.setOnClickListener(this);
              floatingmenu_go_to_app.setOnClickListener(this);
              floating_close_btn.setOnClickListener(this);
              floating_back_btn.setOnClickListener(this);


              //The root element of the collapsed view layout
              collapsedView = mFloatingView.findViewById(R.id.collapse_view);
              //The root element of the expanded view layout
              expandedView = mFloatingView.findViewById(R.id.expanded_property_copier);

              //Add the view to the window.

              int windowFlags = WindowManager.LayoutParams.TYPE_PHONE;
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                     windowFlags = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

              params = new WindowManager.LayoutParams(
                      WindowManager.LayoutParams.WRAP_CONTENT,
                      WindowManager.LayoutParams.WRAP_CONTENT,
                      windowFlags,
                      WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                      PixelFormat.TRANSLUCENT);

              //Specify the view position
              params.gravity = Gravity.TOP | Gravity.START;        //Initially view will be added to top-left corner
              params.x = 0;
              params.y = 100;

              //Add the view to the window

              mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
              if (mWindowManager != null)        // TODO error handling
                     mWindowManager.addView(mFloatingView, params);
       }

       @Override
       public void onDestroy()
       {
              super.onDestroy();
              if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
       }

       @Override
       public boolean onTouch(View v, MotionEvent event)
       {
              switch (event.getAction())
              {
                     case MotionEvent.ACTION_DOWN:
                            //remember the initial position.
                            initialX = params.x;
                            initialY = params.y;

                            //get the touch location
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            return true;
                     case MotionEvent.ACTION_MOVE:
                            //Calculate the X and Y coordinates of the view.
                            params.x = initialX + (int) (event.getRawX() - initialTouchX);
                            params.y = initialY + (int) (event.getRawY() - initialTouchY);


                            //Update the layout with new X & Y coordinate
                            mWindowManager.updateViewLayout(mFloatingView, params);
                            return true;
                     case MotionEvent.ACTION_UP:
                            int Xdiff = (int) (event.getRawX() - initialTouchX);
                            int Ydiff = (int) (event.getRawY() - initialTouchY);

                            //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                            //So that is click event.
                            if (Xdiff < 30 && Ydiff < 30)
                            {
                                   v.performClick();
                            }
                            return true;
              }
              return false;
       }

       @Override
       public void onClick(View view)
       {
              switch (view.getId())
              {
                     case R.id.floating_close_btn:
                            closeCopyService();
                            break;
                     case R.id.floating_back_btn:
                            switchCopyWidgetVisibility();
                            break;
                     case R.id.floatingmenu_get_name:
                            copy(NewProductActivity.COPY_NAME, floating_copy_product_name);
                            break;
                     case R.id.floatingmenu_get_url:
                            copy(NewProductActivity.COPY_URL, floating_copy_product_url);
                            break;
                     case R.id.floatingmenu_get_price:
                            copy(NewProductActivity.COPY_PRICE, floating_copy_product_price);
                            break;
                     case R.id.floatingmenu_go_to_app:
                            goToNewProductActivity();
                            break;
                     case R.id.floating_copy_widget:
                            switchCopyWidgetVisibility();
                            break;
              }
       }

       private void switchCopyWidgetVisibility()
       {
              if (isViewCollapsed())
              {
                     //When user clicks on the image view of the collapsed layout,
                     //visibility of the collapsed layout will be changed to "View.GONE"
                     //and expanded view will become visible.
                     collapsedView.setVisibility(View.GONE);
                     expandedView.setVisibility(View.VISIBLE);
              }
              else
              {
                     collapsedView.setVisibility(View.VISIBLE);
                     expandedView.setVisibility(View.GONE);
              }
       }

       /**
        * Detect if the floating view is collapsed or expanded.
        *
        * @return true if the floating view is collapsed.
        */
       private boolean isViewCollapsed()
       {
              return mFloatingView == null || mFloatingView.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
       }

       private void goToNewProductActivity()
       {
              newProductIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
              startActivity(newProductIntent);
              closeCopyService();
       }

       private void closeCopyService()
       {
              stopSelf();
       }

       private void copy(String key, TextView textHolder)
       {
              String clipboardText = AndroidUtil.getClipboardText(this);
              textHolder.setText(clipboardText);
              newProductIntent.putExtra(key, clipboardText);
       }
}
