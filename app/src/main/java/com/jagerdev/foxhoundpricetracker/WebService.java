package com.jagerdev.foxhoundpricetracker;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.jagerdev.foxhoundpricetracker.webpage.MiniGUIServer;

import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.engine.Engine;
import org.restlet.engine.converter.ConverterHelper;
import org.restlet.service.CorsService;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import controllers.PriceCheckerApp;
import model.serializer.CustomJacksonConverter;

public class WebService extends Service
{
       public WebService()
       {
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
              //startComponentServer();
              startGUIServer();
              return super.onStartCommand(intent, flags, startId);
       }

       @Override
       public void onDestroy()
       {
              super.onDestroy();
       }

       public void startGUIServer()
       {
              MiniGUIServer s = new MiniGUIServer("localhost", 8585, this);
              try
              {
                     s.start();
              } catch (IOException e)
              {
                     e.printStackTrace();
              }
       }

       public void startComponentServer()
       {
              Component component = new Component();
              org.restlet.Application pricecheckerApp = new PriceCheckerApp();
              CorsService corsService = new CorsService();
              corsService.setAllowedOrigins(new HashSet<>(Arrays.asList("*")));
              corsService.setAllowedCredentials(true);
              pricecheckerApp.getServices().add(corsService);
              component.getDefaultHost().attach("/pricetracker", pricecheckerApp);
              Server server = new Server(Protocol.HTTP, "0.0.0.0", 25501, component);
              try {
                     registerCustomJsonConverter();
                     server.start();
                     System.out.println("REST API server started");
              } catch (Exception e) {
                     e.printStackTrace();
              }
       }

       private void registerCustomJsonConverter()
       {
              List<ConverterHelper> converters = Engine.getInstance().getRegisteredConverters();
              converters.clear();
              ConverterHelper converter = new CustomJacksonConverter();
              converters.add(converter);
       }
}
