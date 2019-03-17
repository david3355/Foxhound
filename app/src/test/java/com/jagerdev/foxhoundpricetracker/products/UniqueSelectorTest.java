package com.jagerdev.foxhoundpricetracker.products;

import com.google.common.collect.Lists;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class UniqueSelectorTest
{
       private UniqueSelector<Integer> selector;

       @Before
       public void setUp() throws Exception
       {
              selector = new UniqueSelector<>();
       }

       @Test
       public void getUniqueList()
       {
              List<Integer> items = Lists.newArrayList(1, 2, 2, 2, 4, 3, 9, 4, 4, 4, 34);

              List<Integer> actual = selector.getUniqueList(new ArrayList<Integer>(items));

              List<Integer> expected = Lists.newArrayList(1, 2, 2, 4, 3, 9, 4, 4, 34);
              Assert.assertEquals(expected, actual);
       }

       @Test
       public void getUniqueListEmpty()
       {
              List<Integer> items = Lists.newArrayList();

              List<Integer> actual = selector.getUniqueList(new ArrayList<Integer>(items));

              List<Integer> expected = Lists.newArrayList();
              Assert.assertEquals(expected, actual);
       }

       @Test
       public void getUniqueListOneItem()
       {
              List<Integer> items = Lists.newArrayList(1);

              List<Integer> actual = selector.getUniqueList(new ArrayList<Integer>(items));

              List<Integer> expected = Lists.newArrayList(1);
              Assert.assertEquals(expected, actual);
       }

       @Test
       public void getUniqueListTwoItems()
       {
              List<Integer> items = Lists.newArrayList(1,2);

              List<Integer> actual = selector.getUniqueList(new ArrayList<Integer>(items));

              List<Integer> expected = Lists.newArrayList(1,2);
              Assert.assertEquals(expected, actual);
       }

       @Test
       public void getUniqueListRepeatingItem()
       {
              List<Integer> items = Lists.newArrayList(3, 3 ,3 ,3 ,3);

              List<Integer> actual = selector.getUniqueList(new ArrayList<Integer>(items));

              List<Integer> expected = Lists.newArrayList(3, 3);
              Assert.assertEquals(expected, actual);
       }
}