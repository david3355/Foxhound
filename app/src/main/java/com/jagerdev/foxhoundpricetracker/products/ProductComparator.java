package com.jagerdev.foxhoundpricetracker.products;

import java.util.Comparator;

import model.Product;

public class ProductComparator implements Comparator<Product>
{
       @Override
       public int compare(Product a, Product b)
       {
              return a.getDateOfRecord().getMillis() < b.getDateOfRecord().getMillis() ? 1 : a.getDateOfRecord().getMillis() == b.getDateOfRecord().getMillis() ? 0 : -1;
       }
}
