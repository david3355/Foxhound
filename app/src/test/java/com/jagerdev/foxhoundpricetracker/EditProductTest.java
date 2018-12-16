package com.jagerdev.foxhoundpricetracker;

import com.jagerdev.foxhoundpricetracker.products.Frequency;

import org.junit.Assert;
import org.junit.Test;

public class EditProductTest
{
       @Test
       public void testConvertInspectFrequencyDay()
       {
              Frequency result = ProductInfoActivity.convertInspectFrequency(86400);
              Assert.assertEquals(1, result.frequency);
              Assert.assertEquals("day", result.unit);
       }

       @Test
       public void testConvertInspectFrequencyHour()
       {
              Frequency result = ProductInfoActivity.convertInspectFrequency(3600);
              Assert.assertEquals(1, result.frequency);
              Assert.assertEquals("hour", result.unit);
       }

       @Test
       public void testConvertInspectFrequencyMin()
       {
              Frequency result = ProductInfoActivity.convertInspectFrequency(60);
              Assert.assertEquals(1, result.frequency);
              Assert.assertEquals("min", result.unit);
       }

       @Test
       public void testConvertInspectFrequencySec()
       {
              Frequency result = ProductInfoActivity.convertInspectFrequency(30);
              Assert.assertEquals(30, result.frequency);
              Assert.assertEquals("sec", result.unit);
       }
}
