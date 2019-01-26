package com.jagerdev.foxhoundpricetracker.webpage;

import org.restlet.data.Status;

import fi.iki.elonen.NanoHTTPD;

import static com.jagerdev.foxhoundpricetracker.webpage.ResponseBuilder.buildResponse;

public class HTTPMethodHandler implements RouteMatchHandler
{
       @Override
       public NanoHTTPD.Response get(NanoHTTPD.IHTTPSession session)
       {
              return buildResponse("", Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
       }

       @Override
       public NanoHTTPD.Response post(NanoHTTPD.IHTTPSession session)
       {
              return buildResponse("", Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
       }

       @Override
       public NanoHTTPD.Response put(NanoHTTPD.IHTTPSession session)
       {
              return buildResponse("", Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
       }

       @Override
       public NanoHTTPD.Response delete(NanoHTTPD.IHTTPSession session)
       {
              return buildResponse("", Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
       }

       @Override
       public NanoHTTPD.Response patch(NanoHTTPD.IHTTPSession session)
       {
              return buildResponse("", Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
       }
}
