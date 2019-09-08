package com.jagerdev.foxhoundpricetracker.products.comparators;

import model.Product;

public class LastUpdatedComparator extends ProductComparator {
    public LastUpdatedComparator(boolean ascending) {
        super(ascending);
    }

    @Override
    public int compare(Product a, Product b) {
        return Long.compare(a.getDateOfLastCheck().getMillis(), b.getDateOfLastCheck().getMillis()) * getSign();
    }
}
