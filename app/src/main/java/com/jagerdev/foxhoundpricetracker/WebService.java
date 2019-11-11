package com.jagerdev.foxhoundpricetracker;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.jagerdev.foxhoundpricetracker.webpage.MiniGUIServer;
import com.jagerdev.foxhoundpricetracker.webpage.MiniRESTServer;

import java.io.IOException;

import controllers.validators.OnInvalidInput;
import database.DatabaseException;

public class WebService extends Service implements OnInvalidInput
{
       private MiniGUIServer guiServer;
       private MiniRESTServer restServer;

       public static final int GUI_PORT = 8585;
       public static final int REST_PORT = 25501;

       public WebService()
       {
              guiServer = new MiniGUIServer("0.0.0.0", GUI_PORT, this);
              try
              {
                     restServer = new MiniRESTServer("0.0.0.0", REST_PORT, this);
              } catch (DatabaseException e)
              {
                     e.printStackTrace();
              }
       }

       @Override
       public IBinder onBind(Intent intent)
       {
              // TODO: Return the communication channel to the service.
              throw new UnsupportedOperationException("Not yet implemented");
       }

       @Override
       public int onStartCommand(Intent intent, int flags, int startId)
       {
              startGUIServer();
              startRESTServer();
              Toast.makeText(this, "Webservice started.", Toast.LENGTH_SHORT).show();
              return super.onStartCommand(intent, flags, startId);
       }

       @Override
       public void onDestroy()
       {
              try
              {
                     if (guiServer != null) guiServer.stop();
                     if (restServer != null) restServer.stop();
              } catch (Exception e)
              {
              }
              Toast.makeText(this, "Webservice stopped.", Toast.LENGTH_SHORT).show();
              super.onDestroy();
       }

       public void startGUIServer()
       {
              try
              {
                     guiServer.start();
              } catch (IOException e)
              {
                     e.printStackTrace();
              }
       }

       public void startRESTServer()
       {
              try
              {
                     restServer.start();
              } catch (IOException e)
              {
                     e.printStackTrace();
              }
       }

       @Override
       public void invalidInput(Object o, String s)
       {
              // TODO message over Websocket?
       }
}
