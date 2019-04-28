package com.jagerdev.foxhoundpricetracker.settings;

import com.jagerdev.foxhoundpricetracker.products.Frequency;

import java.util.HashMap;
import java.util.Map;

public class GlobalSettings
{
       public GlobalSettings()
       {
              config = new HashMap<>();
              initDefaultConfig();
       }

       public static GlobalSettings getInstance()
       {
              if (instance == null) instance = new GlobalSettings();
              return instance;
       }

       private Map<ConfigKey, Object> config;
       private static GlobalSettings instance;

       public enum ConfigKey
       {
              DEFAULT_INSPECTION_UNIT,
              DEFAULT_INSPECTION_PERIOD
       }

       private void initDefaultConfig()
       {
              setStringConfig(ConfigKey.DEFAULT_INSPECTION_PERIOD, "12");
              setStringConfig(ConfigKey.DEFAULT_INSPECTION_UNIT, Frequency.UnitValues.HOURS.getValue());
       }

       public String getStringConfig(ConfigKey configKey)
       {
              return (String)config.get(configKey);
       }

       public Integer getIntConfig(ConfigKey configKey)
       {
              return (Integer)config.get(configKey);
       }

       public void setStringConfig(ConfigKey configKey, String configValue)
       {
              config.put(configKey, configValue);
       }

       public void setIntConfig(ConfigKey configKey, int configValue)
       {
              config.put(configKey, configValue);
       }
}
