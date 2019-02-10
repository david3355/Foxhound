package com.jagerdev.foxhoundpricetracker.products;

import java.util.HashMap;
import java.util.Map;

public class UniversalPriceParser
{
       public UniversalPriceParser()
       {
              priceTemplates = new HashMap<>();
       }

       private Map<String, Character> priceTemplates;

       public double getPrice(String rawPrice, String productId)
       {
              Character decimalPointCharacter = priceTemplates.containsKey(productId) ? priceTemplates.get(productId) : '.';
              String cleanedPrice = removeAllNonNumberCharacters(rawPrice, decimalPointCharacter);
              return parseCleanPrice(cleanedPrice);
       }

       private String removeAllNonNumberCharacters(String rawPrice, char decimalPointCharacter)
       {
              StringBuilder filtered = new StringBuilder();
//              char[] allowedDelimiters = {',', '.'};
              for (int i = 0; i < rawPrice.length(); i++)
              {
                     char c = rawPrice.charAt(i);
//                     if (Arrays.asList(allowedDelimiters).contains(c))
                     if (c == decimalPointCharacter)
                            filtered.append(c);
                     else
                            for (int numchar = 0; numchar <= 9; numchar++)
                            {
                                   if (c == String.valueOf(numchar).charAt(0))
                                   {
                                          filtered.append(c);
                                          break;
                                   }
                            }
              }
              return filtered.toString();
       }

       private double parseCleanPrice(String price)
       {
              try
              {
                     return Double.parseDouble(price);
              }
              catch (Exception e)
              {
                     return 0;
              }
       }
}
