package com.jagerdev.foxhoundpricetracker.products;

import junit.framework.Assert;

import org.junit.Test;

@SuppressWarnings("deprecation")
public class UniversalPriceParserTest
{
       @Test
       public void parsePlainPriceText()
       {
              UniversalPriceParser parser = UniversalPriceParser.getInstance();
              double parsedPrice = parser.getPrice("5000", null, 0);
              Assert.assertEquals(5000.0, parsedPrice);
       }

       @Test
       public void parseDotDelimiterPriceText()
       {
              UniversalPriceParser parser = UniversalPriceParser.getInstance();
              double parsedPrice = parser.getPrice("5000.32", null, 0);
              Assert.assertEquals(5000.32, parsedPrice);
       }

       @Test
       public void parseCommaDelimiterPriceText()
       {
              UniversalPriceParser parser = UniversalPriceParser.getInstance();
              double parsedPrice = parser.getPrice("5000,32", null, 0);
              Assert.assertEquals(5000,32, parsedPrice);
       }

       @Test
       public void parseBothDelimitersPriceText()
       {
              UniversalPriceParser parser = UniversalPriceParser.getInstance();
              double parsedPrice = parser.getPrice("500,000.32", null, 0);
              Assert.assertEquals(500000.32, parsedPrice);
       }

       @Test
       public void parseFaultyPriceText()
       {
              UniversalPriceParser parser = UniversalPriceParser.getInstance();
              double parsedPrice = parser.getPrice("alma", null, 10);
              Assert.assertEquals(10.0, parsedPrice);
       }
}
