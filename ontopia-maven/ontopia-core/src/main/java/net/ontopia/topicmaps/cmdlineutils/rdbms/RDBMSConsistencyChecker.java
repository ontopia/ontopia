
// $Id: RDBMSConsistencyChecker.java,v 1.19 2008/05/22 05:43:10 geir.gronmo Exp $

package net.ontopia.topicmaps.cmdlineutils.rdbms;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.ontopia.persistence.proxy.ClassDescriptor;
import net.ontopia.persistence.proxy.ClassInfoIF;
import net.ontopia.persistence.proxy.FieldInfoIF;
import net.ontopia.persistence.proxy.RDBMSMapping;
import net.ontopia.persistence.proxy.RDBMSStorage;
import net.ontopia.topicmaps.utils.TopicStringifiers;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
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

  protected Connection conn;
  protected Writer out;

  public RDBMSConsistencyChecker(String dbProperties, Writer writer)
          throws SQLException, IOException {
    out = writer;

      RDBMSStorage storage = new RDBMSStorage(dbProperties);

      conn = storage.getConnectionFactory(true).requestConnection();

      boolean consistent = true;

      // Consistency checks for foreign keys in TM_ASSOCIATION
      consistent &= referentialIntegrityCheck("TM_ASSOCIATION", "topicmap_id",
              "TM_TOPIC_MAP", "id");
      consistent &= referentialIntegrityCheck("TM_ASSOCIATION", "type_id",
              "TM_TOPIC", "id");

      // Consistency checks for foreign keys in TM_ASSOCIATION_ROLE
      consistent &= referentialIntegrityCheck("TM_ASSOCIATION_ROLE",
              "assoc_id", "TM_ASSOCIATION", "id");
      consistent &= referentialIntegrityCheck("TM_ASSOCIATION_ROLE",
              "topicmap_id", "TM_TOPIC_MAP", "id");
      consistent &= referentialIntegrityCheck("TM_ASSOCIATION_ROLE",
              "type_id", "TM_TOPIC", "id");
      consistent &= referentialIntegrityCheck("TM_ASSOCIATION_ROLE",
              "player_id", "TM_TOPIC", "id");

      // Consistency checks for foreign keys in TM_ASSOCIATION_SCOPE
      consistent &= referentialIntegrityCheck("TM_ASSOCIATION_SCOPE",
              "scoped_id", "TM_ASSOCIATION", "id");
      consistent &= referentialIntegrityCheck("TM_ASSOCIATION_SCOPE",
              "theme_id", "TM_TOPIC", "id");

      // Consistency checks for foreign keys in TM_BASE_NAME
      consistent &= referentialIntegrityCheck("TM_BASE_NAME", "topic_id",
              "TM_TOPIC", "id");
      consistent &= referentialIntegrityCheck("TM_BASE_NAME", "topicmap_id",
              "TM_TOPIC_MAP", "id");

      // Consistency checks for foreign keys in TM_BASE_NAME_SCOPE
      consistent &=  referentialIntegrityCheck("TM_BASE_NAME_SCOPE",
              "theme_id", "TM_TOPIC", "id");
      consistent &=  referentialIntegrityCheck("TM_BASE_NAME_SCOPE",
              "scoped_id", "TM_BASE_NAME", "id");

      // Consistency checks for foreign keys in TM_OCCURRENCE
      consistent &= referentialIntegrityCheck("TM_OCCURRENCE", "topic_id",
              "TM_TOPIC", "id");
      consistent &= referentialIntegrityCheck("TM_OCCURRENCE", "topicmap_id",
              "TM_TOPIC_MAP", "id");
      consistent &= referentialIntegrityCheck("TM_OCCURRENCE", "type_id",
              "TM_TOPIC", "id");

      // Consistency checks for foreign keys in TM_OCCURRENCE_SCOPE
      consistent &=  referentialIntegrityCheck("TM_OCCURRENCE_SCOPE",
              "theme_id", "TM_TOPIC", "id");
      consistent &=  referentialIntegrityCheck("TM_OCCURRENCE_SCOPE",
              "scoped_id", "TM_OCCURRENCE", "id");

      // Consistency checks for foreign keys in TM_ITEM_IDENTIFIERS
      consistent &= referentialIntegrityCheck("TM_ITEM_IDENTIFIERS",
              "topicmap_id", "TM_TOPIC_MAP", "id");

      // Consistency checks for foreign keys in TM_SUBJECT_IDENTIFIERS
      consistent &= referentialIntegrityCheck("TM_SUBJECT_IDENTIFIERS",
              "topic_id", "TM_TOPIC", "id");

      // Consistency checks for foreign keys in TM_SUBJECT_LOCATORS
      consistent &= referentialIntegrityCheck("TM_SUBJECT_LOCATORS",
              "topic_id", "TM_TOPIC", "id");

      // Consistency checks for foreign keys in TM_TOPIC
      consistent &= referentialIntegrityCheck("TM_TOPIC", "topicmap_id",
              "TM_TOPIC_MAP", "id");

      // Consistency checks for foreign keys in TM_TOPIC_TYPES
      consistent &= referentialIntegrityCheck("TM_TOPIC_TYPES", "topic_id",
              "TM_TOPIC", "id");
      consistent &=  referentialIntegrityCheck("TM_TOPIC_TYPES", "type_id",
              "TM_TOPIC", "id");

      // Consistency checks for foreign keys in
      consistent &= referentialIntegrityCheck("TM_VARIANT_NAME",
              "basename_id", "TM_BASE_NAME", "id");
      consistent &= referentialIntegrityCheck("TM_VARIANT_NAME",
              "topicmap_id", "TM_TOPIC_MAP", "id");

      // Consistency checks for foreign keys in TM_VARIANT_NAME_SCOPE
      consistent &= referentialIntegrityCheck("TM_VARIANT_NAME_SCOPE",
              "theme_id", "TM_TOPIC", "id");
      consistent &=  referentialIntegrityCheck("TM_VARIANT_NAME_SCOPE",
              "scoped_id", "TM_VARIANT_NAME", "id");

      out.write(consistent
              ? "There were no missing foreign keys.\n"
              : "Some foreign keys were missing. Details are given above.\n");

      out.flush();

      uniquenessCheck(new String[]{"topicmap_id", "notation", "address"},
              "tm_item_identifiers");
      uniquenessCheck(new String[]{"topic_id", "notation", "address"},
              "tm_subject_identifiers");
      uniquenessCheck(new String[]{"topic_id", "notation", "address"},
              "tm_subject_locators");

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
    } else
      throw new SQLException();

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

    if (allUnique)
      out.write("... There are no duplicate values for " + uniqueTable + "."
              + uniqueFields[0] + ".\n");
    else
      out.write("\n...There are duplicate values for " + uniqueTable + "."
              + uniqueFields[0] + ". Details are given above.\n");

    return allUnique;
  }

  /**
    * Check the database given by dbProperties for topic map consistency.
    * In particular, checks for referential integrity and uniqueness.
    */
  private final static void checkConsistency(String dbProperties,
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
