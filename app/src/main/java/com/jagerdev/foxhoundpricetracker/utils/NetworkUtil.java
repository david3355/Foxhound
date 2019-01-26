package com.jagerdev.foxhoundpricetracker.utils;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

public class NetworkUtil
{
       public static String getWifiIp(Context context)
       {
              WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
              if (wifiManager == null) return null;

              int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
              String ipAddressString = convertIpAddress(ipAddress);
              // TODO check hotspot network address
              // 192.168.43.1

              return ipAddressString;
       }

       private static String convertIpAddress(int rawAddress)
       {
              // Convert little-endian to big-endianif needed
              if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
                     rawAddress = Integer.reverseBytes(rawAddress);
              }

              byte[] ipByteArray = BigInteger.valueOf(rawAddress).toByteArray();

              String ipAddressString;
              try {
                     ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
              } catch (UnknownHostException ex) {
                     Log.e("WIFIIP", "Unable to get host address.");
                     ipAddressString = null;
              }
              return ipAddressString;
       }
}
