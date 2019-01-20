package com.jagerdev.foxhoundpricetracker.webpage;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

import fi.iki.elonen.NanoHTTPD;

public class MiniGUIServer extends NanoHTTPD
{
       private Context context;

       public MiniGUIServer(int port)
       {
              super(port);
       }

       public MiniGUIServer(String hostname, int port, Context context)
       {
              super(hostname, port);
              this.context = context;
       }

       public Response serve(IHTTPSession session)
       {
              String content = "<html><body>alma</body></html>";
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
}
