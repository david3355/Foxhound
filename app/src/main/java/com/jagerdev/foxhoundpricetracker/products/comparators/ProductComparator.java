package com.jagerdev.foxhoundpricetracker.products.comparators;

import java.util.Comparator;

import model.Product;

public abstract class ProductComparator implements Comparator<Product>
{
       public ProductComparator(boolean ascending)
       {
              super();
              this.ascending = ascending;
       }

       private boolean ascending;

       protected int getSign(){
              return ascending ? 1 : -1;
       }

       public static ProductComparator buildProductComparator(SortBy sortBy, boolean ascending)
       {
              switch (sortBy)
              {
                     case LAST_UPDATED:
                            return new LastUpdatedComparator(ascending);
                     case ACTUAL_PRICE:
                            return new ActualPriceComparator(ascending);
                     case PRODUCT_NAME:
                            return new ProductNameComparator(ascending);
                     default:
                     case LAST_ADDED: return new LastAddedComparator(ascending);

              }
       }
}
