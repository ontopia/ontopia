/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.persistence.jdbcspy;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import net.ontopia.utils.OntopiaRuntimeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: 
 */

public class SpyDriver implements Driver {

  // Define a logging category.
  private static Logger log = LoggerFactory.getLogger(SpyDriver.class.getName());

  protected static SpyStats stats = new SpyStats();
  
  protected Driver driver;
  
  // -- init
  
  private static boolean initialized;

  static {
    try {
      initialize();
    } catch (Exception e) {
      e.printStackTrace();      
    }
  }

  @Override
  public int getMajorVersion() {
    return driver.getMajorVersion();
  }

  @Override
  public int getMinorVersion() {
    return driver.getMinorVersion();
  }

  @Override
  public boolean jdbcCompliant() {
    return driver.jdbcCompliant();
  }

  @Override
  public boolean acceptsURL(String url) 
    throws SQLException {
    return url.startsWith("jdbcspy:");
  }

  @Override
  public Connection connect(String url, Properties info) 
    throws SQLException {
    String realURL = getRealURL(url);
    if (realURL == null) { 
      // stop here as this url was not meant for jdbcspy
      return null;
    }

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

  @Override
  public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) 
    throws SQLException {
    return driver.getPropertyInfo(url, info);
  }

  // -- helper

  protected String getRealURL(String url) {
    return (url.startsWith("jdbcspy:") ? url.substring("jdbcspy:".length()) : null);
  }

  private static void initialize() throws SQLException {
    if (initialized) {
      return;
    }

    // register driver
    SpyDriver driver = new SpyDriver();
    DriverManager.registerDriver(driver);

    // set initialization flag to true
    SpyDriver.initialized = true;
  }

  private static void initDriver(String driverClass) {
    try {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      Class.forName(driverClass, true, classLoader);
    } catch (ClassNotFoundException e) {
      // ignore if not exists
    }
  }

  public static void unregister() {
    try {
      DriverManager.deregisterDriver(DriverManager.getDriver("jdbcspy:"));
    } catch (SQLException sqle) {
      // ignore if it doesn't exist
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

  // J2EE 1.7 specifics - comment out remainder of methods if you have to use java 1.6 or lower

  @Override
  public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
    throw new SQLFeatureNotSupportedException("JDBC Spy does not use JUL logging");
  }
}
