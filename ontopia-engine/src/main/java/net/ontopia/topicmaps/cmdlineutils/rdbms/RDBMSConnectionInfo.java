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

package net.ontopia.topicmaps.cmdlineutils.rdbms;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.Map;
import net.ontopia.persistence.proxy.DataSourceConnectionFactory;
import net.ontopia.persistence.proxy.DriverDataSource;
import net.ontopia.utils.BeanUtils;
import net.ontopia.utils.CmdlineOptions;
import net.ontopia.utils.CmdlineUtils;
import net.ontopia.utils.PropertyUtils;

/**
 * EXPERIMENTAL: A tool that generates a report of the database
 * metadata as reported by the JDBC driver<p>
 */
public class RDBMSConnectionInfo {


  public static void main(String[] argv) throws Exception {

    // Initialize logging
    CmdlineUtils.initializeLogging();
      
    // Register logging options
    CmdlineOptions options = new CmdlineOptions("RDBMSConnectionInfo", argv);
    CmdlineUtils.registerLoggingOptions(options);
      
    // Parse command line options
    try {
      options.parse();
    } catch (CmdlineOptions.OptionsException e) {
      System.err.println("Error: " + e.getMessage());
      System.exit(1);      
    }

    // Get command line arguments
    String[] args = options.getArguments();

    if (args.length != 1) {
      usage();
      System.exit(3);
    }

    // open database connection
    String propfile = args[0];
    DataSourceConnectionFactory cf = new DataSourceConnectionFactory(new DriverDataSource(PropertyUtils.toMap(PropertyUtils.loadProperties(new File(propfile)))), true);

    Connection conn = cf.requestConnection();
    try {
      DatabaseMetaData dbm = conn.getMetaData();

      System.out.println("--- properties ------------------------------------------");
      for (Map.Entry<String, String> entry : BeanUtils.beanMap(dbm, true).entrySet()) {
        System.out.println(entry.getKey() + ": " + entry.getValue());
      }

      System.out.println("--- tables and views ------------------------------------");
      ResultSet rs = dbm.getTables(null, null, null, new String[] { "TABLE", "VIEW" });
      while (rs.next()) {
        String schema_name = rs.getString(2);
        String table_name = rs.getString(3);
        System.out.println((schema_name == null ? table_name : schema_name + "." + table_name));
      }
      
    } finally {
      conn.rollback();
      conn.close();
    }

  }
    
  private static void usage() {
    System.out.println("java net.ontopia.topicmaps.cmdlineutils.rdbms.RDBMSConnectionInfo [options] <dbprops>");
    System.out.println("");
    System.out.println("  Generates database metadata report as given by JDBC driver.");
    System.out.println("");
    System.out.println("  Options:");
    CmdlineUtils.printLoggingOptionsUsage(System.out);
    System.out.println("");
    System.out.println("  <dbprops>:   the database configuration file");
    System.out.println("");
  }

}
