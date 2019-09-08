package com.jagerdev.foxhoundpricetracker.products.comparators;

import model.Product;

public class LastAddedComparator extends ProductComparator {
    public LastAddedComparator(boolean ascending) {
        super(ascending);
    }

    @Override
    public int compare(Product a, Product b)
    {
        return Long.compare(a.getDateOfRecord().getMillis(), b.getDateOfRecord().getMillis()) * getSign();
    }
}
