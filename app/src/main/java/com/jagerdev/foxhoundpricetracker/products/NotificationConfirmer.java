package com.jagerdev.foxhoundpricetracker.products;

import model.Product;
import tracker.ProductAvailability;

public class NotificationConfirmer
{
       private NotificationConfirmer()
       {
       }

       private static UniversalPriceParser priceParser = UniversalPriceParser.getInstance();

       public static boolean shouldSendPriceUpdateNotification(Product product, String oldPrice, String newQueriedPrice)
       {
              double parsedNewPrice = priceParser.getPrice(newQueriedPrice, product.getDecimalSeparator());
              double parsedActualPrice = priceParser.getPrice(oldPrice, product.getDecimalSeparator());
              if (product.notifyWhenPriceChanges() == null || product.notifyWhenPriceChanges())
                     return true;
              if (product.getNotifyWhenPriceGoesAbove() != null && parsedNewPrice >= product.getNotifyWhenPriceGoesAbove())
                     return true;
              if (product.getNotifyWhenPriceGoesBelow() != null && parsedNewPrice <= product.getNotifyWhenPriceGoesBelow())
                     return true;
              if (product.getNotifyWhenPriceIncreasesWith() != null && parsedActualPrice
                      + parsedActualPrice * (product.getNotifyWhenPriceIncreasesWith() / 100f) <= parsedNewPrice)
                     return true;
              if (product.getNotifyWhenPriceDecreasesWith() != null && parsedActualPrice
                      - parsedActualPrice * (product.getNotifyWhenPriceDecreasesWith() / 100f) >= parsedNewPrice)
                     return true;

              return false;
       }

       public static boolean shouldSendAvailabilityUpdateNotification(Product product, ProductAvailability newAvailability)
       {
              if (product.notifyWhenAvailabilityChanges() == null || product.notifyWhenAvailabilityChanges())
                     return true;
              if (product.notifyWhenAvailable() != null  && product.notifyWhenAvailable() && newAvailability.getValue())
                     return true;
              if (product.notifyWhenUnavailable() != null && product.notifyWhenUnavailable() && !newAvailability.getValue())
                     return true;

              return false;
       }
}
