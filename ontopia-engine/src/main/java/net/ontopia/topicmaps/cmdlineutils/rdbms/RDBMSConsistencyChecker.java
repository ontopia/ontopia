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

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.ontopia.persistence.proxy.RDBMSStorage;
import net.ontopia.utils.CmdlineOptions;
import net.ontopia.utils.CmdlineUtils;

/**
 * PUBLIC: Checks an RDBMS database holding topic map data for referential
 * integrity and for uniqueness of source-locators, subject-indicators
 * and subject-locators.
 * Outputs tables/fields, where the field of one table references
 * a field of another table with no corresponding value.
 * Outputs field-combinations that fail the uniqueness test.
 */
public class RDBMSConsistencyChecker {
  private static final String TM_ASSOCIATION = "TM_ASSOCIATION";
  private static final String TM_ASSOCIATION_ROLE = "TM_ASSOCIATION_ROLE";
  private static final String TM_ASSOCIATION_SCOPE = "TM_ASSOCIATION_SCOPE";
  private static final String TM_BASE_NAME = "TM_BASE_NAME";
  private static final String TM_BASE_NAME_SCOPE = "TM_BASE_NAME_SCOPE";
  private static final String TM_OCCURRENCE = "TM_OCCURRENCE";
  private static final String TM_OCCURRENCE_SCOPE = "TM_OCCURRENCE_SCOPE";
  private static final String TM_TOPIC_MAP = "TM_TOPIC_MAP";
  private static final String TM_TOPIC = "TM_TOPIC";
  private static final String TM_TOPIC_TYPES = "TM_TOPIC_TYPES";
  private static final String TM_SUBJECT_LOCATORS = "TM_SUBJECT_LOCATORS";
  private static final String TM_SUBJECT_IDENTIFIERS = "TM_SUBJECT_IDENTIFIERS";
  private static final String TM_ITEM_IDENTIFIERS = "TM_ITEM_IDENTIFIERS";
  private static final String TM_VARIANT_NAME = "TM_VARIANT_NAME";
  private static final String TM_VARIANT_NAME_SCOPE = "TM_VARIANT_NAME_SCOPE";
  private static final String ID = "id";
  private static final String TYPE_ID = "type_id";
  private static final String TOPIC_ID = "topic_id";
  private static final String TOPICMAP_ID = "topicmap_id";
  private static final String BASENAME_ID = "basename_id";
  private static final String THEME_ID = "theme_id";
  private static final String SCOPED_ID = "scoped_id";
  private static final String PLAYER_ID = "player_id";
  private static final String ASSOC_ID = "assoc_id";
  private static final String ADDRESS = "address";
  private static final String NOTATION = "notation";

  protected Connection conn;
  protected Writer out;

  public RDBMSConsistencyChecker(String dbProperties, Writer writer)
          throws SQLException, IOException {
    out = writer;

      RDBMSStorage storage = new RDBMSStorage(dbProperties);

      conn = storage.getConnectionFactory(true).requestConnection();

      boolean consistent = true;

      // Consistency checks for foreign keys in TM_ASSOCIATION
      consistent &= referentialIntegrityCheck(TM_ASSOCIATION, TOPICMAP_ID, TM_TOPIC_MAP, ID);
      consistent &= referentialIntegrityCheck(TM_ASSOCIATION, TYPE_ID, TM_TOPIC, ID);

      // Consistency checks for foreign keys in TM_ASSOCIATION_ROLE
      consistent &= referentialIntegrityCheck(TM_ASSOCIATION_ROLE, ASSOC_ID, TM_ASSOCIATION, ID);
      consistent &= referentialIntegrityCheck(TM_ASSOCIATION_ROLE, TOPICMAP_ID, TM_TOPIC_MAP, ID);
      consistent &= referentialIntegrityCheck(TM_ASSOCIATION_ROLE, TYPE_ID, TM_TOPIC, ID);
      consistent &= referentialIntegrityCheck(TM_ASSOCIATION_ROLE, PLAYER_ID, TM_TOPIC, ID);

      // Consistency checks for foreign keys in TM_ASSOCIATION_SCOPE
      consistent &= referentialIntegrityCheck(TM_ASSOCIATION_SCOPE, SCOPED_ID, TM_ASSOCIATION, ID);
      consistent &= referentialIntegrityCheck(TM_ASSOCIATION_SCOPE, THEME_ID, TM_TOPIC, ID);

      // Consistency checks for foreign keys in TM_BASE_NAME
      consistent &= referentialIntegrityCheck(TM_BASE_NAME, TOPIC_ID, TM_TOPIC, ID);
      consistent &= referentialIntegrityCheck(TM_BASE_NAME, TOPICMAP_ID, TM_TOPIC_MAP, ID);

      // Consistency checks for foreign keys in TM_BASE_NAME_SCOPE
      consistent &=  referentialIntegrityCheck(TM_BASE_NAME_SCOPE, THEME_ID, TM_TOPIC, ID);
      consistent &=  referentialIntegrityCheck(TM_BASE_NAME_SCOPE, SCOPED_ID, TM_BASE_NAME, ID);

      // Consistency checks for foreign keys in TM_OCCURRENCE
      consistent &= referentialIntegrityCheck(TM_OCCURRENCE, TOPIC_ID, TM_TOPIC, ID);
      consistent &= referentialIntegrityCheck(TM_OCCURRENCE, TOPICMAP_ID, TM_TOPIC_MAP, ID);
      consistent &= referentialIntegrityCheck(TM_OCCURRENCE, TYPE_ID, TM_TOPIC, ID);

      // Consistency checks for foreign keys in TM_OCCURRENCE_SCOPE
      consistent &=  referentialIntegrityCheck(TM_OCCURRENCE_SCOPE, THEME_ID, TM_TOPIC, ID);
      consistent &=  referentialIntegrityCheck(TM_OCCURRENCE_SCOPE, SCOPED_ID, TM_OCCURRENCE, ID);

      // Consistency checks for foreign keys in TM_ITEM_IDENTIFIERS
      consistent &= referentialIntegrityCheck(TM_ITEM_IDENTIFIERS, TOPICMAP_ID, TM_TOPIC_MAP, ID);

      // Consistency checks for foreign keys in TM_SUBJECT_IDENTIFIERS
      consistent &= referentialIntegrityCheck(TM_SUBJECT_IDENTIFIERS, TOPIC_ID, TM_TOPIC, ID);

      // Consistency checks for foreign keys in TM_SUBJECT_LOCATORS
      consistent &= referentialIntegrityCheck(TM_SUBJECT_LOCATORS, TOPIC_ID, TM_TOPIC, ID);

      // Consistency checks for foreign keys in TM_TOPIC
      consistent &= referentialIntegrityCheck(TM_TOPIC, TOPICMAP_ID, TM_TOPIC_MAP, ID);

      // Consistency checks for foreign keys in TM_TOPIC_TYPES
      consistent &= referentialIntegrityCheck(TM_TOPIC_TYPES, TOPIC_ID, TM_TOPIC, ID);
      consistent &=  referentialIntegrityCheck(TM_TOPIC_TYPES, TYPE_ID, TM_TOPIC, ID);

      // Consistency checks for foreign keys in
      consistent &= referentialIntegrityCheck(TM_VARIANT_NAME, BASENAME_ID, TM_BASE_NAME, ID);
      consistent &= referentialIntegrityCheck(TM_VARIANT_NAME, TOPICMAP_ID, TM_TOPIC_MAP, ID);

      // Consistency checks for foreign keys in TM_VARIANT_NAME_SCOPE
      consistent &= referentialIntegrityCheck(TM_VARIANT_NAME_SCOPE, THEME_ID, TM_TOPIC, ID);
      consistent &=  referentialIntegrityCheck(TM_VARIANT_NAME_SCOPE, SCOPED_ID, TM_VARIANT_NAME, ID);

      out.write(consistent
              ? "There were no missing foreign keys.\n"
              : "Some foreign keys were missing. Details are given above.\n");

      out.flush();

      uniquenessCheck(new String[]{TOPICMAP_ID, NOTATION, ADDRESS}, TM_ITEM_IDENTIFIERS);
      uniquenessCheck(new String[]{TOPIC_ID, NOTATION, ADDRESS}, TM_SUBJECT_IDENTIFIERS);
      uniquenessCheck(new String[]{TOPIC_ID, NOTATION, ADDRESS}, TM_SUBJECT_LOCATORS);

      storage.close();
      out.flush();
  }

  /**
   * Checks the tables table columns 'dependentTable.dependentKey and
   * foreignTable.foreignKey for referential integrity.
   * I.e. for each cell in dependentTable.dependentKey checks if there
   * exists a cell with the same value in foreignTable.foreignKey.
   * If no such cell exists, then this is reported to 'out'.
   */
  private boolean referentialIntegrityCheck(String dependentTable,
          String dependentKey, String foreignTable, String foreignKey)
          throws IOException, SQLException {
    String referenceQuery =
            "select distinct " + dependentTable + "." + dependentKey
            + " from " + dependentTable + " where "
            + dependentTable + "." + dependentKey + " is not null"
            + " and not exists ("
            + " select * from " + foreignTable + " where "
            + dependentTable + "." + dependentKey + " = "
            + foreignTable + "." + foreignKey + ") order by "
            + dependentTable + "." + dependentKey;

    ResultSet resultSet = conn.createStatement()
            .executeQuery(referenceQuery);

    if (resultSet.next()) {
      out.write("The following values for the field "
              + dependentTable + "." + dependentKey
              + " have no corresponding values in "
              + foreignTable + "." + foreignKey + ".\n");
      do {
        out.write("" + resultSet.getObject(dependentKey) + "\n");
      } while (resultSet.next());
      out.write("\n");
      return false;
    }
    return true;
  }

  /**
    * Checks if all combinations of the 'uniqueFields' are unique in the
    * table 'uniqueTable'.
    * Writes feedback to 'out', and returns true iff all combinations of
    * 'uniqueTable.uniqueFields[0]', ..., 'uniqueTable.uniqueFields[n]' are
    * unique (i.e. no combination of the fields occurring twice).
    */
  private boolean uniquenessCheck(String uniqueFields[], String uniqueTable)
          throws IOException, SQLException {
    boolean allUnique = true;

    String fieldsString = "";
    String notNullFieldsString = "not (";
    if (uniqueFields.length > 0) {
      fieldsString += uniqueFields[0];
      notNullFieldsString += uniqueFields[0] + " is null";

      for (int i = 1; i < uniqueFields.length; i++) {
        fieldsString += ", " + uniqueFields[i];
        notNullFieldsString += " or " + uniqueFields[i]
                + " is null";
      }
      notNullFieldsString += ")";
    } else {
      throw new SQLException();
    }

    out.write("\n\nChecking for duplicate values for the combination "
            + fieldsString + " ...\n");

    // Find all subject indicators.
    String query = "select " + fieldsString + ", count("
            + uniqueFields[0] + ") as count from " + uniqueTable
            + " where " + notNullFieldsString + " group by " + fieldsString;

    out.write(query);
    out.flush();

    ResultSet resultSet = conn.createStatement().executeQuery(query);

    if (resultSet.next()) {
      do {
        int count = resultSet.getInt("count");

        if (count > 1) {
          Object currentValue = resultSet.getObject(uniqueFields[0]);

          String rowFeedback = "The value combination '"
                  + currentValue.toString();

          for (int i = 1; i < uniqueFields.length; i++) {
            currentValue = resultSet.getObject(uniqueFields[i]);
            rowFeedback += "', '" + (currentValue == null
                    ? "null"
                    : currentValue.toString());
          }

          rowFeedback += "' occurred " + count + " times.\n";
          out.write(rowFeedback);

          allUnique = false;
        }
      } while (resultSet.next());
    }

    if (allUnique) {
      out.write("... There are no duplicate values for " + uniqueTable + "."
              + uniqueFields[0] + ".\n");
    } else {
      out.write("\n...There are duplicate values for " + uniqueTable + "."
              + uniqueFields[0] + ". Details are given above.\n");
    }

    return allUnique;
  }

  /**
    * Check the database given by dbProperties for topic map consistency.
    * In particular, checks for referential integrity and uniqueness.
    */
  private static void checkConsistency(String dbProperties,
          Writer writer) throws SQLException, IOException {
    new RDBMSConsistencyChecker(dbProperties, writer);
  }

  private static void usage() {
    System.out.println("java net.ontopia.topicmaps.cmdlineutils"
            + ".RDBMSConsistencyChecker [options] <dbprops>");
    System.out.println("");
    System.out.println("  Checks a database (holding topicmap data) for"
            + " referential integrity between topicmap objects.");
    System.out.println("");
    System.out.println("  Options:");
    CmdlineUtils.printLoggingOptionsUsage(System.out);
    System.out.println("");
    System.out.println("  <dbprops>:   the database configuration file");
  }

  /**
    * Check a given database for topic map consistency..
    * In particular, checks for referential integrity and uniqueness.
    */
  public static void main(String [] argv) throws Exception {

    // Initialize logging
    CmdlineUtils.initializeLogging();

    // Initialize command line option parser and listeners
    CmdlineOptions options = new CmdlineOptions("RDBMSConsistencyChecker",
            argv);

    // Register logging options
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

    String dbProperties = null;
    if (args.length != 1) {
      System.err.println("Error: Illegal number of arguments.");
      usage();
      System.exit(1);
    }

    dbProperties = args[0];
    checkConsistency(dbProperties, new OutputStreamWriter(System.out));
  }

}
