package com.jagerdev.foxhoundpricetracker.products;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import model.Product;

public class NotificationConfirmerTest
{
       private Product product;

       @Before
       public void setUp()
       {
              product = new Product("id", "name", "http://test", "100.0", "selector", true, 0, DateTime.now(), DateTime.now(), 0,
                      false, null, null, null, null, false, false, false, null);
       }

       @Test
       public void shouldSendPriceUpdateNotificationGlobally()
       {
              product.setNotifyWhenPriceChanges(true);
              String oldPrice = "100.0";
              String newPrice = "100.1";
              boolean shouldSendNotification = NotificationConfirmer.shouldSendPriceUpdateNotification(product, oldPrice, newPrice);
              Assert.assertTrue(shouldSendNotification);
       }

       @Test
       public void shouldNotSendPriceUpdateNotificationGlobally()
       {
              product.setNotifyWhenPriceChanges(false);
              String oldPrice = "100.0";
              String newPrice = "121.0";
              boolean shouldSendNotification = NotificationConfirmer.shouldSendPriceUpdateNotification(product, oldPrice, newPrice);
              Assert.assertFalse(shouldSendNotification);
       }

       @Test
       public void shouldSendPriceUpdateNotificationPriceIncreases()
       {
              product.setNotifyWhenPriceIncreasesWith(20.0);
              String oldPrice = "100.0";
              String newPrice = "121.0";
              boolean shouldSendNotification = NotificationConfirmer.shouldSendPriceUpdateNotification(product, oldPrice, newPrice);
              Assert.assertTrue(shouldSendNotification);
       }

       @Test
       public void shouldSendPriceUpdateNotificationPriceIncreasesEqual()
       {
              product.setNotifyWhenPriceIncreasesWith(20.0);
              String oldPrice = "100.0";
              String newPrice = "120.0";
              boolean shouldSendNotification = NotificationConfirmer.shouldSendPriceUpdateNotification(product, oldPrice, newPrice);
              Assert.assertTrue(shouldSendNotification);
       }

       @Test
       public void shouldNotSendPriceUpdateNotificationPriceIncreases()
       {
              product.setNotifyWhenPriceIncreasesWith(20.0);
              String oldPrice = "100.0";
              String newPrice = "119.0";
              boolean shouldSendNotification = NotificationConfirmer.shouldSendPriceUpdateNotification(product, oldPrice, newPrice);
              Assert.assertFalse(shouldSendNotification);
       }

       @Test
       public void shouldSendPriceUpdateNotificationPriceDecreases()
       {
              product.setNotifyWhenPriceDecreasesWith(20.0);
              String oldPrice = "100.0";
              String newPrice = "79.0";
              boolean shouldSendNotification = NotificationConfirmer.shouldSendPriceUpdateNotification(product, oldPrice, newPrice);
              Assert.assertTrue(shouldSendNotification);
       }

       @Test
       public void shouldSendPriceUpdateNotificationPriceDecreasesEqual()
       {
              product.setNotifyWhenPriceDecreasesWith(20.0);
              String oldPrice = "100.0";
              String newPrice = "80.0";
              boolean shouldSendNotification = NotificationConfirmer.shouldSendPriceUpdateNotification(product, oldPrice, newPrice);
              Assert.assertTrue(shouldSendNotification);
       }

       @Test
       public void shouldNotSendPriceUpdateNotificationPriceDecreases()
       {
              product.setNotifyWhenPriceDecreasesWith(20.0);
              String oldPrice = "100.0";
              String newPrice = "81.0";
              boolean shouldSendNotification = NotificationConfirmer.shouldSendPriceUpdateNotification(product, oldPrice, newPrice);
              Assert.assertFalse(shouldSendNotification);
       }

       @Test
       public void shouldNotSendPriceUpdateNotificationPriceDecreasesWithIncreaseSetting()
       {
              product.setNotifyWhenPriceIncreasesWith(20.0);
              String oldPrice = "100.0";
              String newPrice = "79.0";
              boolean shouldSendNotification = NotificationConfirmer.shouldSendPriceUpdateNotification(product, oldPrice, newPrice);
              Assert.assertFalse(shouldSendNotification);
       }

       @Test
       public void shouldSendPriceUpdateNotificationPriceGoesAbove()
       {
              product.setNotifyWhenPriceGoesAbove(150.0);
              String oldPrice = "100.0";
              String newPrice = "151.0";
              boolean shouldSendNotification = NotificationConfirmer.shouldSendPriceUpdateNotification(product, oldPrice, newPrice);
              Assert.assertTrue(shouldSendNotification);
       }

       @Test
       public void shouldSendPriceUpdateNotificationPriceGoesAboveEqual()
       {
              product.setNotifyWhenPriceGoesAbove(150.0);
              String oldPrice = "100.0";
              String newPrice = "150.0";
              boolean shouldSendNotification = NotificationConfirmer.shouldSendPriceUpdateNotification(product, oldPrice, newPrice);
              Assert.assertTrue(shouldSendNotification);
       }

       @Test
       public void shouldNotSendPriceUpdateNotificationPriceGoesAbove()
       {
              product.setNotifyWhenPriceGoesAbove(150.0);
              String oldPrice = "100.0";
              String newPrice = "149.0";
              boolean shouldSendNotification = NotificationConfirmer.shouldSendPriceUpdateNotification(product, oldPrice, newPrice);
              Assert.assertFalse(shouldSendNotification);
       }

       @Test
public void shouldSendAvailabilityUpdateNotificationProductAvailable()
{
       product.setNotifyWhenAvailable(true);
       boolean newAvailability = true;
       boolean shouldSendNotification = NotificationConfirmer.shouldSendAvailabilityUpdateNotification(product, newAvailability);
       Assert.assertTrue(shouldSendNotification);
}

       @Test
       public void shouldNotSendAvailabilityUpdateNotificationProductAvailable()
       {
              product.setNotifyWhenAvailable(true);
              boolean newAvailability = false;
              boolean shouldSendNotification = NotificationConfirmer.shouldSendAvailabilityUpdateNotification(product, newAvailability);
              Assert.assertFalse(shouldSendNotification);
       }

       @Test
       public void shouldSendAvailabilityUpdateNotificationProductUnavailable()
       {
              product.setNotifyWhenUnavailable(true);
              boolean newAvailability = false;
              boolean shouldSendNotification = NotificationConfirmer.shouldSendAvailabilityUpdateNotification(product, newAvailability);
              Assert.assertTrue(shouldSendNotification);
       }

       @Test
       public void shouldNotSendAvailabilityUpdateNotificationProductUnavailable()
       {
              product.setNotifyWhenUnavailable(true);
              boolean newAvailability = true;
              boolean shouldSendNotification = NotificationConfirmer.shouldSendAvailabilityUpdateNotification(product, newAvailability);
              Assert.assertFalse(shouldSendNotification);
       }

       @Test
       public void shouldSendAvailabilityUpdateNotificationGlobally()
       {
              product.setNotifyWhenAvailabilityChanges(true);
              boolean newAvailability = true;
              boolean shouldSendNotification = NotificationConfirmer.shouldSendAvailabilityUpdateNotification(product, newAvailability);
              Assert.assertTrue(shouldSendNotification);
       }

       @Test
       public void shouldNotSendAvailabilityUpdateNotificationGlobally()
       {
              product.setNotifyWhenAvailabilityChanges(false);
              boolean newAvailability = true;
              boolean shouldSendNotification = NotificationConfirmer.shouldSendAvailabilityUpdateNotification(product, newAvailability);
              Assert.assertFalse(shouldSendNotification);
       }
}