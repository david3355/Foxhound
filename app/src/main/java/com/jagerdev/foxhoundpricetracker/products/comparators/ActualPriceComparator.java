package com.jagerdev.foxhoundpricetracker.products.comparators;

import com.jagerdev.foxhoundpricetracker.products.UniversalPriceParser;

import model.Product;

public class ActualPriceComparator extends ProductComparator {
    public ActualPriceComparator(boolean ascending) {
        super(ascending);
        this.parser = new UniversalPriceParser();
    }

    private UniversalPriceParser parser;

    @Override
    public int compare(Product a, Product b) {
        double price1 = parser.getPrice(a.getActualPrice(), a.getDecimalSeparator());
        double price2 = parser.getPrice(b.getActualPrice(), b.getDecimalSeparator());
        return Double.compare(price1, price2) * getSign();
    }
}
