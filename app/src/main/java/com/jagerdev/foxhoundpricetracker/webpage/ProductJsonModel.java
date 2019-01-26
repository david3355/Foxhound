package com.jagerdev.foxhoundpricetracker.webpage;

import org.joda.time.DateTime;

public class ProductJsonModel
{
       private String uuid;
       private String productName;
       private String webPath;
       private String actualPrice;
       private String priceHtmlPathSelector;
       private boolean availableNow;
       private int inspectFrequencySeconds;
       private DateTime dateOfRecord;
       private DateTime dateOfLastCheck;
       private int activeAlarms;

       public ProductJsonModel(String uuid, String name, String webPath, String actualPrice, String priceHtmlPathSelector, boolean availableNow, int inspectFrequencySeconds, DateTime dateOfRecord, DateTime dateOfLastCheck, int activeAlarms)
       {
              this.uuid = uuid;
              this.productName = name;
              this.webPath = webPath;
              this.actualPrice = actualPrice;
              this.priceHtmlPathSelector = priceHtmlPathSelector;
              this.availableNow = availableNow;
              this.inspectFrequencySeconds = inspectFrequencySeconds;
              this.dateOfRecord = dateOfRecord;
              this.dateOfLastCheck = dateOfLastCheck;
              this.activeAlarms = activeAlarms;
       }
}
