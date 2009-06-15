
// $Id: SpyDriver.java,v 1.11 2008/12/04 11:25:15 lars.garshol Exp $

package net.ontopia.persistence.jdbcspy;

import java.sql.*;
import java.util.Properties;
import net.ontopia.utils.OntopiaRuntimeException;

import org.apache.log4j.Logger;

/**
 * INTERNAL: 
 */

public class SpyDriver implements Driver {

  // Define a logging category.
  static Logger log = Logger.getLogger(SpyDriver.class.getName());

  static SpyStats stats = new SpyStats();
  
  Driver driver;
  
  public SpyDriver() {
  }

  public int getMajorVersion() {
    return driver.getMajorVersion();
  }

  public int getMinorVersion() {
    return driver.getMinorVersion();
  }

  public boolean jdbcCompliant() {
    return driver.jdbcCompliant();
  }

  public boolean acceptsURL(String url) 
    throws SQLException {
    return url.startsWith("jdbcspy:");
  }

  public Connection connect(String url, Properties info) 
    throws SQLException {
    String realURL = getRealURL(url);
    if (realURL == null) 
      // stop here as this url was not meant for jdbcspy
      return null;

    if (realURL.startsWith("jdbc:postgresql:")) {
      initDriver("org.postgresql.Driver");
    } else if (realURL.startsWith("jdbc:oracle:")) {
      initDriver("oracle.jdbc.driver.OracleDriver");
    }

    log.debug("Translating connect url: " + url + " -> " + realURL);
    this.driver = DriverManager.getDriver(realURL);
    log.debug("Real driver: " + driver);

    return new SpyConnection(driver.connect(realURL, info), stats);
  }

  public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) 
    throws SQLException {
    return driver.getPropertyInfo(url, info);
  }

  // -- helper

  protected String getRealURL(String url) {
    return (url.startsWith("jdbcspy:") ? url.substring("jdbcspy:".length()) : null);
  }

  // -- init
  
  static {
    try {
      initialize();
    } catch (Exception e) {
      e.printStackTrace();      
    }
  }

  static boolean initialized;

  static void initialize() throws SQLException {
    if (initialized) return;

    // register driver
    SpyDriver driver = new SpyDriver();
    DriverManager.registerDriver(driver);

    // set initialization flag to true
    SpyDriver.initialized = true;
  }

  static void initDriver(String driverClass) {
    try {
      Class.forName(driverClass);
    } catch (ClassNotFoundException e) {
      // ignore if not exists
    }
  }


  // -- report generation

  public static void clearStats() {
    synchronized (stats) {
      stats.profiler.clear();
    }
    log.debug("JDBCSpy stats cleared.");
  }

  public static void writeReport(String filename) throws java.io.IOException {
    java.io.FileWriter out = new java.io.FileWriter(filename);
    try {
      stats.generateReport(out);
    } finally {
      out.close();
    }
    log.debug("JDBCSpy report written to " + filename);
  }

  public static void writeReport(java.io.Writer out) throws java.io.IOException {
    try {
      stats.generateReport(out);
    } catch (java.io.IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

}
