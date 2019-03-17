package com.jagerdev.foxhoundpricetracker.products;

import java.util.ArrayList;
import java.util.List;

public class UniqueSelector<T>
{
       public List<T> getUniqueList(List<T> items)
       {
              List<T> unique = new ArrayList<>();
              T item, lastItem = null;
              for (int i = 0; i < items.size(); i++)
              {
                     item = items.get(i);
                     if (i == items.size() - 1 || !item.equals(lastItem))
                            unique.add(item);
                     else if (item.equals(lastItem) && i < items.size() - 1 && !item.equals(items.get(i + 1)))
                            unique.add(item);
                     lastItem = item;
              }
              return unique;
       }
}
