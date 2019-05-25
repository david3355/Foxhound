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
              delayMsec = DEFAULT_DELAY_MSEC;
       }

       private static ServiceRunHandler instance;

       private TrackerService trackerSvc;
       final private List<String> activeUis;
       private boolean svcInForeground;

       private final int DEFAULT_DELAY_MSEC = 4000;
       private int delayMsec;

       public static ServiceRunHandler getInstance()
       {
              if (instance == null) instance = new ServiceRunHandler();
              return instance;
       }

       /**
        * Sets the delay before sending PriceTracker service to foreground when no UI is active
        * @param delayMsec Delay value in milliseconds
        */
       public void setDelayMsec(int delayMsec)
       {
              this.delayMsec = delayMsec;
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
              if (delayMsec > 0)
              {
                     Timer t = new Timer();
                     TimerTask foregrounder = new TimerTask()
                     {
                            @Override
                            public void run()
                            {
                                   setServiceToForeground();
                            }
                     };
                     t.schedule(foregrounder, delayMsec);
              }
              else setServiceToForeground();
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
