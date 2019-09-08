package com.jagerdev.foxhoundpricetracker.products.comparators;

import model.Product;

public class ProductNameComparator extends ProductComparator{
    public ProductNameComparator(boolean ascending) {
        super(ascending);
    }

    @Override
    public int compare(Product a, Product b) {
        return a.getName().compareTo(b.getName()) * getSign();
    }
}
