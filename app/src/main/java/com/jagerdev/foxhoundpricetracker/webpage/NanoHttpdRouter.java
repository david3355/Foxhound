package com.jagerdev.foxhoundpricetracker.webpage;

import org.restlet.data.Status;

import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

import static com.jagerdev.foxhoundpricetracker.webpage.ResponseBuilder.buildResponse;

public class NanoHttpdRouter
{
       public NanoHttpdRouter()
       {
              routes = new HashMap<>();
       }

       private Map<String, RouteMatchHandler> routes;

       public void addRoute(String route, RouteMatchHandler handler)
       {
              routes.put(route, handler);
       }

       public NanoHTTPD.Response route(String path, NanoHTTPD.IHTTPSession session)
       {
              if (routes.containsKey(path))
              {
                     RouteMatchHandler handler = routes.get(path);
                     switch (session.getMethod())
                     {
                            case POST:
                                   return handler.post(session);
                            case PUT:
                                   return handler.put(session);
                            case PATCH:
                                   return handler.patch(session);
                            case DELETE:
                                   return handler.delete(session);
                            case GET:
                            default:
                                   return handler.get(session);
                     }
              }
              return buildResponse(String.format("%s not found", path), Status.CLIENT_ERROR_NOT_FOUND);
       }
}
