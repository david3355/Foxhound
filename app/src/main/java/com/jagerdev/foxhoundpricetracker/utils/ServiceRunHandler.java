package com.jagerdev.foxhoundpricetracker.utils;

import com.jagerdev.foxhoundpricetracker.TrackerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ServiceRunHandler
{
       private ServiceRunHandler()
       {
              activeUis = new ArrayList<>();
              svcInForeground = false;
       }

       private static ServiceRunHandler instance;

       private TrackerService trackerSvc;
       final private List<String> activeUis;
       private boolean svcInForeground;

       private final int DELAY_MSEC = 5000;

       public static ServiceRunHandler getInstance()
       {
              if (instance == null) instance = new ServiceRunHandler();
              return instance;
       }

       public boolean hasActiveUI()
       {
              synchronized (activeUis)
              {
                     return activeUis.size() > 0;
              }
       }

       /**
        * Should be called in onResume()
        */
       public void uiActivated(String activityName)
       {
              synchronized (activeUis)
              {
                     activeUis.add(activityName);
              }
              sendServiceToBackground();
       }

       /**
        * Should be called in onStop()
        */
       public void uiDeactivated(String activityName)
       {
              synchronized (activeUis)
              {
                     activeUis.remove(activityName);
              }
              Timer t = new Timer();
              TimerTask foregrounder = new TimerTask()
              {
                     @Override
                     public void run()
                     {
                            setServiceToForeground();
                     }
              };
              t.schedule(foregrounder, DELAY_MSEC);
       }

       public void registerService(TrackerService svc)
       {
              this.trackerSvc = svc;
       }

       private void sendServiceToBackground()
       {
              if (hasActiveUI() && svcInForeground)
              {
                     trackerSvc.background();
                     svcInForeground = false;
              }
       }

       private void setServiceToForeground()
       {
              if (!hasActiveUI() && !svcInForeground)
              {
                     trackerSvc.foreground();
                     svcInForeground = true;
              }
       }
}
