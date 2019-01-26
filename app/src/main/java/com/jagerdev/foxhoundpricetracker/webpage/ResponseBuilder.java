package com.jagerdev.foxhoundpricetracker.webpage;

import com.google.gson.Gson;

import org.restlet.data.Status;

import fi.iki.elonen.NanoHTTPD;

public class ResponseBuilder
{
       private static Gson jsonTransformer;

       static
       {
              jsonTransformer = new Gson();
       }

       public static NanoHTTPD.Response buildResponse(String data, final int responseCode, final String statusDescription)
       {
              NanoHTTPD.Response resp = NanoHTTPD.newFixedLengthResponse(jsonTransformer.toJson(data));
              resp.addHeader("Access-Control-Allow-Origin", "*");
              resp.setStatus(ResponseBuilder.getStatus(responseCode, statusDescription));
              return resp;
       }

       public static NanoHTTPD.Response buildResponse(String data, Status status )
       {
              return buildResponse(data, status.getCode(), status.getDescription());
       }

       public static NanoHTTPD.Response.IStatus getStatus(final int statusCode, final String description)
       {
              return new NanoHTTPD.Response.IStatus()
              {
                     @Override
                     public String getDescription()
                     {
                            return description;
                     }

                     @Override
                     public int getRequestStatus()
                     {
                            return statusCode;
                     }
              };
       }
}
