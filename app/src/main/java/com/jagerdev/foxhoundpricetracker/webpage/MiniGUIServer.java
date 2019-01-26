package com.jagerdev.foxhoundpricetracker.webpage;

import android.content.Context;

import org.restlet.data.Status;

import java.io.IOException;
import java.io.InputStream;

import fi.iki.elonen.NanoHTTPD;

import static com.jagerdev.foxhoundpricetracker.webpage.ResponseBuilder.buildResponse;
import static com.jagerdev.foxhoundpricetracker.webpage.ResponseBuilder.getStatus;

public class MiniGUIServer extends NanoHTTPD
{
       private Context context;
       private NanoHttpdRouter router;

       public MiniGUIServer(int port)
       {
              super(port);
       }

       public MiniGUIServer(String hostname, int port, Context context)
       {
              super(hostname, port);
              this.context = context;
              this.router = new NanoHttpdRouter();
              router.addRoute("/", mainGui);
              router.addRoute("/img/favicon.ico", favicon);
       }

       private RouteMatchHandler mainGui = new HTTPMethodHandler()
       {
              @Override
              public Response get(IHTTPSession session)
              {
                     String content = "<html><body></body></html>";
                     try
                     {
                            InputStream stream = context.getAssets().open("newproduct.html");
                            int size = stream.available();

                            byte[] buffer = new byte[size];
                            stream.read(buffer);
                            stream.close();

                            content = new String(buffer);
                     } catch (IOException e)
                     {
                            e.printStackTrace();
                     }
                     return newFixedLengthResponse(content);
              }
       };

       private RouteMatchHandler favicon = new HTTPMethodHandler()
       {
              @Override
              public Response get(IHTTPSession session)
              {
                     try
                     {
                            InputStream stream = context.getAssets().open("favicon.ico");
                            return newFixedLengthResponse(getStatus(200, "OK"), "image/x-icon", stream, stream.available());
                     } catch (IOException e)
                     {
                            e.printStackTrace();
                     }
                     return buildResponse("cannot get icon", Status.SERVER_ERROR_INTERNAL);
              }
       };

       public Response serve(IHTTPSession session)
       {
              return router.route(session.getUri(), session);
       }
}
