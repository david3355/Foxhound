package com.jagerdev.foxhoundpricetracker.webpage;

import fi.iki.elonen.NanoHTTPD;

public interface RouteMatchHandler
{
       NanoHTTPD.Response get(NanoHTTPD.IHTTPSession session);
       NanoHTTPD.Response post(NanoHTTPD.IHTTPSession session);
       NanoHTTPD.Response put(NanoHTTPD.IHTTPSession session);
       NanoHTTPD.Response delete(NanoHTTPD.IHTTPSession session);
       NanoHTTPD.Response patch(NanoHTTPD.IHTTPSession session);
}
