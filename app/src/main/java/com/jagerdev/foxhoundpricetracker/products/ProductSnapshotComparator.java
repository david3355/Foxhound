package com.jagerdev.foxhoundpricetracker.products;

import java.util.Comparator;

import model.ProductSnapshot;

public class ProductSnapshotComparator implements Comparator<ProductSnapshot>
{
       public ProductSnapshotComparator(boolean ascending)
       {
              this.ascending = ascending;
       }

       public ProductSnapshotComparator()
       {
              this(true);
       }

       private boolean ascending;

       @Override
       public int compare(ProductSnapshot a, ProductSnapshot b)
       {
              int compareSign = ascending ? -1 : 1;
              return a.getDateOfSnapshot().getMillis() < b.getDateOfSnapshot().getMillis() ? compareSign : a.getDateOfSnapshot().getMillis() == b.getDateOfSnapshot().getMillis() ? 0 : -compareSign;
       }
}
