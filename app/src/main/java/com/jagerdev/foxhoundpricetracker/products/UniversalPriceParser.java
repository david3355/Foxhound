package com.jagerdev.foxhoundpricetracker.products;

public class UniversalPriceParser
{
       public UniversalPriceParser()
       {
       }

       private static final char MAIN_DECIMAL_POINT = '.';
       private static UniversalPriceParser instance;

       public static UniversalPriceParser getInstance()
       {
              if (instance == null)
                     instance = new UniversalPriceParser();
              return instance;
       }

       public double getPrice(String rawPrice, Character decimalPointSeparatorCharacter)
       {
              if (decimalPointSeparatorCharacter == null)
                     decimalPointSeparatorCharacter = findOutDecimalCharacter(rawPrice);
              String cleanedPrice = removeAllNonNumberCharacters(rawPrice, decimalPointSeparatorCharacter);
              return parseCleanPrice(cleanedPrice);
       }

       /**
        * Rules: If only either , or . character is present in price, then it will
        * be chosen as decimal point If both , and . characters are present, then
        * the character with the greatest index will be chosen, because decimal
        * point is the closest to the end of the price If none of the separator
        * characters is present, the returns null.
        *
        * @param rawPrice
        * @return
        */
       private Character findOutDecimalCharacter(String rawPrice)
       {
              char[] allowedDelimiters =
                      { ',', MAIN_DECIMAL_POINT };
              int index;
              int maxDelimiterIndex = -1;

              for (int i = 0; i < allowedDelimiters.length; i++)
              {
                     index = rawPrice.indexOf(allowedDelimiters[i]);
                     if (index > maxDelimiterIndex)
                            maxDelimiterIndex = index;
              }
              if (maxDelimiterIndex == -1)
                     return null;
              return rawPrice.charAt(maxDelimiterIndex);
       }

       private String removeAllNonNumberCharacters(String rawPrice, Character decimalPointCharacter)
       {
              StringBuilder filtered = new StringBuilder();
              for (int i = 0; i < rawPrice.length(); i++)
              {
                     char c = rawPrice.charAt(i);
                     if (decimalPointCharacter != null && c == decimalPointCharacter)
                            filtered.append(MAIN_DECIMAL_POINT);
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
              } catch (Exception e)
              {
                     return 0;
              }
       }
}
