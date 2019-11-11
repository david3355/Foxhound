package com.jagerdev.foxhoundpricetracker.products;

import java.util.List;

public interface ProductRegisterEvents
{
       void onRegisteredSuccessfully();
       void onFinally();
       void pricePathPossibilitiesReceived(List<String> possiblePaths);
}
