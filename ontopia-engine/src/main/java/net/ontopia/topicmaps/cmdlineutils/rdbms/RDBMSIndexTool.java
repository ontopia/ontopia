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
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import net.ontopia.persistence.proxy.ConnectionFactoryIF;
import net.ontopia.persistence.proxy.DefaultConnectionFactory;
import net.ontopia.persistence.rdbms.DatabaseProjectReader;
import net.ontopia.persistence.rdbms.Index;
import net.ontopia.persistence.rdbms.Project;
import net.ontopia.persistence.rdbms.Table;
import net.ontopia.utils.CmdlineOptions;
import net.ontopia.utils.CmdlineUtils;
import net.ontopia.utils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * EXPERIMENTAL: A tool that inspects a database to see if the proper
 * indexes has been created.<p>
 */
public class RDBMSIndexTool {


  public static void main(String[] argv) throws Exception {

    // Initialize logging
    CmdlineUtils.initializeLogging();
      
    // Register logging options
    CmdlineOptions options = new CmdlineOptions("RDBMSIndexTool", argv);
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

    // load database schema project
    ClassLoader cloader = RDBMSIndexTool.class.getClassLoader();
    InputStream istream = cloader.getResourceAsStream("net/ontopia/topicmaps/impl/rdbms/config/schema.xml");
    Project dbp = DatabaseProjectReader.loadProject(istream);

    // open database connection
    String propfile = args[0];
    ConnectionFactoryIF cf = new DefaultConnectionFactory(PropertyUtils.loadProperties(new File(propfile)), true);

    Connection conn = cf.requestConnection();
    try {
      DatabaseMetaData dbm = conn.getMetaData();
      boolean downcase = dbm.storesLowerCaseIdentifiers();

      Map extra_indexes = new TreeMap();
      Map missing_indexes = new TreeMap();

      Iterator tables = dbp.getTables().iterator();
      while (tables.hasNext()) {
        Table table = (Table)tables.next();
        String table_name = (downcase ? table.getName().toLowerCase() : table.getName());
        //! System.out.println("T :"  + table_name);

        // get primary keys from database
        Map pkeys = getPrimaryKeys(table_name, dbm);

        // get indexes from database
        Map indexes = getIndexes(table_name, dbm);

        Map dindexes = new HashMap();
        if (table.getPrimaryKeys() != null) {
          String pkey = table_name + '(' + StringUtils.join(table.getPrimaryKeys(), ',') + ')';
          if (!pkeys.containsKey(pkey)) {
            System.out.println("PKM: " + pkey);
          }
        }

        Iterator iter = table.getIndexes().iterator();
        while (iter.hasNext()) {
          Index index = (Index)iter.next();
          String i = table_name + '(' + StringUtils.join(index.getColumns(), ',') + ')';
          String index_name = (downcase ? index.getName().toLowerCase() : index.getName());
          dindexes.put(i, index_name);
        }
        
        Set extra = new HashSet(indexes.keySet());
        extra.removeAll(dindexes.keySet());
        extra.removeAll(pkeys.keySet());
        if (!extra.isEmpty()) {
          Iterator i = extra.iterator();
          while (i.hasNext()) {
            Object k = i.next();
            extra_indexes.put(k, indexes.get(k));
          }
        }

        Set missing = new HashSet(dindexes.keySet());
        missing.addAll(pkeys.keySet());
        missing.removeAll(indexes.keySet());
        if (!missing.isEmpty()) {
          Iterator i = missing.iterator();
          while (i.hasNext()) {
            Object k = i.next();
            missing_indexes.put(k, dindexes.get(k));
          }
        }

      }
      if (!extra_indexes.isEmpty()) {
        System.out.println("/* --- Extra indexes ----------------------------------------- */");
      }
      Iterator eiter = extra_indexes.keySet().iterator();
      while (eiter.hasNext()) {
        Object k = eiter.next();
        System.out.println("drop index " + extra_indexes.get(k) + "; /* " + k + " */");
      }

      if (!missing_indexes.isEmpty()) {
        System.out.println("/* --- Missing indexes---------------------------------------- */");
      }
      Iterator miter = missing_indexes.keySet().iterator();
      while (miter.hasNext()) {
        Object k = miter.next();
        System.out.println("create index " + missing_indexes.get(k) + " on " + k + ";");
      }

    } finally {
      conn.rollback();
      conn.close();
    }

  }

  protected static void print(String prefix, Collection c) {
    Iterator iter = c.iterator();
    while (iter.hasNext()) {
      Object k = iter.next();
      System.out.println(prefix + k);
    }
  }

  protected static void print(String prefix, Map m) {
    Iterator iter = m.keySet().iterator();
    while (iter.hasNext()) {
      Object k = iter.next();
      System.out.println(prefix + k + " " + m.get(k));
    }
  }

  protected static Map getIndexes(String table_name, DatabaseMetaData dbm) throws SQLException {
    // returns { table_name(colname,...) : index_name }
    Map result = new HashMap(5);
    ResultSet rs = dbm.getIndexInfo(null, null, table_name, false, false);
    String prev_index_name = null;
    String columns = null;

    while (rs.next()) {
      String index_name = rs.getString(6);

      if (prev_index_name != null && !prev_index_name.equals(index_name)) {
        result.put(table_name + '(' + columns + ')', prev_index_name);
        columns = null;
      }
      // column_name might be quoted, so unquote it before proceeding
      String column_name = unquote(rs.getString(9), dbm.getIdentifierQuoteString());

      if (columns == null) { 
        columns = column_name;
      } else {
        columns = columns + "," + column_name;
      }

      prev_index_name = index_name;
    }
    rs.close();

    if (prev_index_name != null) {
      result.put(table_name + '(' + columns + ')', prev_index_name);
    }

    return result;
  }

  protected static Map getPrimaryKeys(String table_name, DatabaseMetaData dbm) throws SQLException {
    // returns { table_name(colname,...) : index_name }
    Map result = new HashMap(5);
    ResultSet rs = dbm.getPrimaryKeys(null, null, table_name);
    String prev_index_name = null;
    String columns = null;

    while (rs.next()) {
      String index_name = rs.getString(6);
      if (prev_index_name != null && !prev_index_name.equals(index_name)) {
        result.put(table_name + '(' + columns + ')', prev_index_name);
        columns = null;
      }
      // column_name might be quoted, so unquote it before proceeding
      String column_name = unquote(rs.getString(4), dbm.getIdentifierQuoteString());
      if (columns == null) { 
        columns = column_name;
      } else {
        columns = columns + "," + column_name;
      }

      prev_index_name = index_name;
    }
    rs.close();

    if (prev_index_name != null) {
      result.put(table_name + '(' + columns + ')', prev_index_name);
    }

    return result;
  }

  protected static String unquote(String column_name, String quote) {
    // column_name might be quoted, so unquote it before proceeding
    if (column_name != null && column_name.startsWith(quote) && column_name.endsWith(quote)) {
      return column_name.substring(quote.length(), column_name.length()-quote.length());
    } else {
      return column_name;
    }
  }
    
  private static void usage() {
    System.out.println("java net.ontopia.topicmaps.cmdlineutils.rdbms.RDBMSIndexTool [options] <dbprops>");
    System.out.println("");
    System.out.println("  Analyzes database indexes.");
    System.out.println("");
    System.out.println("  Options:");
    CmdlineUtils.printLoggingOptionsUsage(System.out);
    System.out.println("");
    System.out.println("  <dbprops>:   the database configuration file");
    System.out.println("");
  }

}
