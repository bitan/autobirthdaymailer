/*
 * Amadeus Confidential Information:
 * Unauthorized use and disclosure strictly forbidden.
 * @1998-2015 - Amadeus s.a.s - All Rights Reserved.
 */
package com.amadeus.fun;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 
 * 
 * @author bmallick
 */
public class ConfigReader {
  public String getPropValue(String key) {
    String value = null;
    try {
      Properties prop = new Properties();
      String propFileName = "config.properties";
      InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

      if (inputStream != null) {
        prop.load(inputStream);
        value = prop.getProperty(key);
      }
      else {
        throw new FileNotFoundException("Config file '" + propFileName + "' not found in the classpath");
      }
    }
    catch (Exception e) {
      MailerService.logger.error("Error reading/parsing config.properties! ERROR : " + e.getMessage());
    }
    return value;
  }
}
