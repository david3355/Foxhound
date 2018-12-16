package com.jagerdev.foxhoundpricetracker.products;

import java.util.Comparator;

import model.ProductSnapshot;

public class ProductSnapshotComparator implements Comparator<ProductSnapshot>
{
       @Override
       public int compare(ProductSnapshot a, ProductSnapshot b)
       {
              return a.getDateOfSnapshot().getMillis() < b.getDateOfSnapshot().getMillis() ? 1 : a.getDateOfSnapshot().getMillis() == b.getDateOfSnapshot().getMillis() ? 0 : -1;
       }
}
