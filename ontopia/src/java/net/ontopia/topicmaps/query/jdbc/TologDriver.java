
// $Id: TologDriver.java,v 1.2 2007/11/13 13:31:40 geir.gronmo Exp $

package net.ontopia.topicmaps.query.jdbc;

import java.sql.*;
import java.util.Properties;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.StringUtils;

import org.apache.log4j.Logger;

/**
 * INTERNAL: JDBC driver for Tolog.<br>
 *
 * Example connection uris:<br>
 *
 * tolog:opera.ltm<br>
 * tolog:opera.ltm:file:/tmp/tm-sources.xml<br>
 *
 */

public class TologDriver implements Driver {

  // Define a logging category.
  static Logger log = Logger.getLogger(TologDriver.class.getName());
  
  public TologDriver() {
  }

  public int getMajorVersion() {
    return 0;
  }

  public int getMinorVersion() {
    return 1;
  }

  public boolean jdbcCompliant() {
    return false;
  }

  public boolean acceptsURL(String url) 
    throws SQLException {
    return url.startsWith("tolog:");
  }

  public Connection connect(String url, Properties info) 
    throws SQLException {
    return new TologConnection(url);
  }

  public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) 
    throws SQLException {
    return new DriverPropertyInfo[] { };
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
    TologDriver driver = new TologDriver();
    DriverManager.registerDriver(driver);

    // set initialization flag to true
    TologDriver.initialized = true;
  }

  static void initDriver(String driverClass) {
    try {
      Class.forName(driverClass);
    } catch (ClassNotFoundException e) {
      // ignore if not exists
    }
  }

}
