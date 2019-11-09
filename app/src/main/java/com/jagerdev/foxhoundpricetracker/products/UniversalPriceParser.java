package com.jagerdev.foxhoundpricetracker.products;

import com.jagerdev.foxhoundpricetracker.products.selector.PriceParseException;

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

    /**
     * Tries to parse a given price string and returns it, or the given default value in case of parsing error
     * @param rawPrice Price to be parsed
     * @param decimalPointSeparatorCharacter Decimal separator character
     * @param defaultValue Default value to be returned in case of parsing error
     * @return Parsed price
     */
       public double getPrice(String rawPrice, Character decimalPointSeparatorCharacter, double defaultValue)
       {
              try
              {
                     return parsePrice(rawPrice, decimalPointSeparatorCharacter);
              } catch (Exception e)
              {
                     return defaultValue;
              }

       }

    /**
     * Tries to parse a given price string and returns it or throws parsing error
     * @param rawPrice Price to be parsed
     * @param decimalPointSeparatorCharacter Decimal separator character
     * @return Parsed price
     * @throws PriceParseException
     */
       public double getPrice(String rawPrice, Character decimalPointSeparatorCharacter) throws PriceParseException {
              return parsePrice(rawPrice, decimalPointSeparatorCharacter);
       }

       private double parsePrice(String rawPrice, Character decimalPointSeparatorCharacter) throws PriceParseException {
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

       private double parseCleanPrice(String price) throws PriceParseException {
              try
              {
                     return Double.parseDouble(price);
              } catch (Exception e)
              {
                     throw new PriceParseException(String.format("Cannot parse price: %s. Reason: %s", price, e.getMessage()));
              }
       }
}
