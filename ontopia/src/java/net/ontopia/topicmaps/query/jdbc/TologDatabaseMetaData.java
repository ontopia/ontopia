
// $Id: TologDatabaseMetaData.java,v 1.2 2007/11/14 08:06:14 geir.gronmo Exp $

package net.ontopia.topicmaps.query.jdbc;

import java.sql.*;

public class TologDatabaseMetaData implements DatabaseMetaData {

  TologConnection conn;
  
  public TologDatabaseMetaData(TologConnection conn) {
    this.conn = conn;
  }
  
  public boolean allProceduresAreCallable() throws SQLException {
    return false;
  }
  
  public boolean allTablesAreSelectable() throws SQLException {
    return true;
  }
  
  public String getURL() throws SQLException {
    return conn.getURL();
  }
  
  public String getUserName() throws SQLException {
    return conn.getUserName();
  }
  
  public boolean isReadOnly() throws SQLException {
    return conn.isReadOnly();
  }

  public boolean nullsAreSortedHigh() throws SQLException {
    return nullsAreSortedAtStart();
  }

  public boolean nullsAreSortedLow() throws SQLException {
    return nullsAreSortedAtEnd();
  }

  public boolean nullsAreSortedAtStart() throws SQLException {
    return false;
  }

  public boolean nullsAreSortedAtEnd() throws SQLException {
    return true;
  }

  public String getDatabaseProductName() throws SQLException {
    return "Ontopia Knowledge Suite JDBC Driver";
  }

  public String getDatabaseProductVersion() throws SQLException {
    return net.ontopia.products.TopicMapEngine.getInstance().getVersion();
  }

  public String getDriverName() throws SQLException {
    return "net.ontopia.topicmaps.query.jdbc.TologDriver";
  }

  public String getDriverVersion() throws SQLException {
    return "0.1";
  }

  public int getDriverMajorVersion() {
    return 0;
  }

  public int getDriverMinorVersion() {
    return 1;
  }

  public boolean usesLocalFiles() throws SQLException {
    return false;
  }

  public boolean usesLocalFilePerTable() throws SQLException {
    return false;
  }

  public boolean supportsMixedCaseIdentifiers() throws SQLException {
    return true;
  }

  public boolean storesUpperCaseIdentifiers() throws SQLException {
    return false;
  }

  public boolean storesLowerCaseIdentifiers() throws SQLException {
    return false;
  }

  public boolean storesMixedCaseIdentifiers() throws SQLException {
    return true;
  }

  public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
    return false;
  }

  public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
    return false;    
  }

  public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
    return false;
  }

  public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
    return false;
  }

  public String getIdentifierQuoteString() throws SQLException {
    return " ";
  }

  public String getSQLKeywords() throws SQLException {
    return "";
  }

  public String getNumericFunctions() throws SQLException {
    return "";
  }

  public String getStringFunctions() throws SQLException {
    return "";
  }

  public String getSystemFunctions() throws SQLException {
    return "";
  }

  public String getTimeDateFunctions() throws SQLException {
    return "";
  }

  public String getSearchStringEscape() throws SQLException {
    return "%";
  }

  public String getExtraNameCharacters() throws SQLException {
    return "$%";
  }

  public boolean supportsAlterTableWithAddColumn() throws SQLException {
    return false;
  }

  public boolean supportsAlterTableWithDropColumn() throws SQLException {
    return false;
  }

  public boolean supportsColumnAliasing() throws SQLException {
    return false;
  }

  public boolean nullPlusNonNullIsNull() throws SQLException {
    return true;
  }

  public boolean supportsConvert() throws SQLException {
    return false;
  }

  public boolean supportsConvert(int fromType, int toType) throws SQLException {
    return false;
  }

  public boolean supportsTableCorrelationNames() throws SQLException {
    return false;
  }

  public boolean supportsDifferentTableCorrelationNames() throws SQLException {
    return false;
  }

  public boolean supportsExpressionsInOrderBy() throws SQLException {
    return false;
  }

  public boolean supportsOrderByUnrelated() throws SQLException {
    return false;
  }

  public boolean supportsGroupBy() throws SQLException {
    return false;
  }

  public boolean supportsGroupByUnrelated() throws SQLException {
    return false;
  }

  public boolean supportsGroupByBeyondSelect() throws SQLException {
    return false;
  }

  public boolean supportsLikeEscapeClause() throws SQLException {
    return false;
  }

  public boolean supportsMultipleResultSets() throws SQLException {
    return false;
  }

  public boolean supportsMultipleTransactions() throws SQLException {
    return false;
  }

  public boolean supportsNonNullableColumns() throws SQLException {
    return true;
  }

  public boolean supportsMinimumSQLGrammar() throws SQLException {
    return false;
  }

  public boolean supportsCoreSQLGrammar() throws SQLException {
    return false;
  }

  public boolean supportsExtendedSQLGrammar() throws SQLException {
    return false;
  }

  public boolean supportsANSI92EntryLevelSQL() throws SQLException {
    return false;
  }

  public boolean supportsANSI92IntermediateSQL() throws SQLException {
    return false;
  }

  public boolean supportsANSI92FullSQL() throws SQLException {
    return false;
  }

  public boolean supportsIntegrityEnhancementFacility() throws SQLException {
    return false;
  }

  public boolean supportsOuterJoins() throws SQLException {
    return false;
  }

  public boolean supportsFullOuterJoins() throws SQLException {
    return false;
  }

  public boolean supportsLimitedOuterJoins() throws SQLException {
    return false;
  }

  public String getSchemaTerm() throws SQLException {
    return "ontology";
  }

  public String getProcedureTerm() throws SQLException {
    return "procedure";
  }

  public String getCatalogTerm() throws SQLException {
    return "catalog";
  }

  public boolean isCatalogAtStart() throws SQLException {
    return true;
  }

  public String getCatalogSeparator() throws SQLException {
    return ".";
  }

  public boolean supportsSchemasInDataManipulation() throws SQLException {
    return false;
  }

  public boolean supportsSchemasInProcedureCalls() throws SQLException {
    return false;
  }

  public boolean supportsSchemasInTableDefinitions() throws SQLException {
    return false;
  }

  public boolean supportsSchemasInIndexDefinitions() throws SQLException {
    return false;
  }

  public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
    return false;
  }

  public boolean supportsCatalogsInDataManipulation() throws SQLException {
    return false;
  }

  public boolean supportsCatalogsInProcedureCalls() throws SQLException {
    return false;
  }

  public boolean supportsCatalogsInTableDefinitions() throws SQLException {
    return false;
  }

  public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
    return false;
  }

  public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
    return false;
  }

  public boolean supportsPositionedDelete() throws SQLException {
    return false;
  }

  public boolean supportsPositionedUpdate() throws SQLException {
    return false;
  }

  public boolean supportsSelectForUpdate() throws SQLException {
    return false;
  }

  public boolean supportsStoredProcedures() throws SQLException {
    return false;
  }

  public boolean supportsSubqueriesInComparisons() throws SQLException {
    return false;
  }

  public boolean supportsSubqueriesInExists() throws SQLException {
    return false;
  }

  public boolean supportsSubqueriesInIns() throws SQLException {
    return false;
  }

  public boolean supportsSubqueriesInQuantifieds() throws SQLException {
    return false;
  }

  public boolean supportsCorrelatedSubqueries() throws SQLException {
    return false;
  }

  public boolean supportsUnion() throws SQLException {
    return false;
  }

  public boolean supportsUnionAll() throws SQLException {
    return false;
  }

  public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
    return false;
  }

  public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
    return false;
  }

  public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
    return false;
  }

  public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
    return false;
  }

  public int getMaxBinaryLiteralLength() throws SQLException {
    return 0;
  }

  public int getMaxCharLiteralLength() throws SQLException {
    return 0;
  }

  public int getMaxColumnNameLength() throws SQLException {
    return 0;
  }

  public int getMaxColumnsInGroupBy() throws SQLException {
    return 0;
  }

  public int getMaxColumnsInIndex() throws SQLException {
    return 0;
  }

  public int getMaxColumnsInOrderBy() throws SQLException {
    return 0;
  }

  public int getMaxColumnsInSelect() throws SQLException {
    return 0;
  }

  public int getMaxColumnsInTable() throws SQLException {
    return 0;
  }

  public int getMaxConnections() throws SQLException {
    return 0;
  }

  public int getMaxCursorNameLength() throws SQLException {
    return 0;
  }

  public int getMaxIndexLength() throws SQLException {
    return 0;
  }

  public int getMaxSchemaNameLength() throws SQLException {
    return 0;
  }

  public int getMaxProcedureNameLength() throws SQLException {
    return 0;
  }

  public int getMaxCatalogNameLength() throws SQLException {
    return 0;
  }

  public int getMaxRowSize() throws SQLException {
    return 0;
  }

  public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
    return true;
  }

  public int getMaxStatementLength() throws SQLException {
    return 0;
  }

  public int getMaxStatements() throws SQLException {
    return 0;
  }

  public int getMaxTableNameLength() throws SQLException {
    return 0;
  }

  public int getMaxTablesInSelect() throws SQLException {
    return 0;
  }

  public int getMaxUserNameLength() throws SQLException {
    return 0;
  }

  public int getDefaultTransactionIsolation() throws SQLException {
    return Connection.TRANSACTION_READ_COMMITTED;
  }

  public boolean supportsTransactions() throws SQLException {
    return true;
  }

  public boolean supportsTransactionIsolationLevel(int level) throws SQLException {
    return Connection.TRANSACTION_READ_COMMITTED == level;
  }

  public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
    return false;
  }

  public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
    return false;
  }

  public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
    return false;
  }

  public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
    return false;
  }

  public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {
    return null;
  }

  public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern) throws SQLException {
    return null;
  }

  public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
    return null;
  }

  public ResultSet getSchemas() throws SQLException {
    return null;
  }

  public ResultSet getCatalogs() throws SQLException {
    return null;
  }

  public ResultSet getTableTypes() throws SQLException {
    return null;
  }

  public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
    return null;
  }

  public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException {
    return null;
  }

  public ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
    return null;
  }

  public ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable) throws SQLException {
    return null;
  }

  public ResultSet getVersionColumns(String catalog, String schema, String table) throws SQLException {
    return null;
  }

  public ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException {
    return null;
  }

  public ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException {
    return null;
  }

  public ResultSet getExportedKeys(String catalog, String schema, String table) throws SQLException {
    return null;
  }

  public ResultSet getCrossReference(String primaryCatalog, String primarySchema, String primaryTable, String foreignCatalog, String foreignSchema, String foreignTable) throws SQLException {
    return null;
  }

  public ResultSet getTypeInfo() throws SQLException {
    return null;
  }

  public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate) throws SQLException {
    return null;
  }

  public boolean supportsResultSetType(int type) throws SQLException {
    return true;
  }

  public boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException {
    return true;
  }

  public boolean ownUpdatesAreVisible(int type) throws SQLException {
    return true;
  }

  public boolean ownDeletesAreVisible(int type) throws SQLException {
    return true;
  }

  public boolean ownInsertsAreVisible(int type) throws SQLException {
    return true;
  }

  public boolean othersUpdatesAreVisible(int type) throws SQLException {
    return false;
  }

  public boolean othersDeletesAreVisible(int type) throws SQLException {
    return false;
  }

  public boolean othersInsertsAreVisible(int type) throws SQLException {
    return false;
  }

  public boolean updatesAreDetected(int type) throws SQLException {
    return true;
  }

  public boolean deletesAreDetected(int type) throws SQLException {
    return true;
  }

  public boolean insertsAreDetected(int type) throws SQLException {
    return true;
  }

  public boolean supportsBatchUpdates() throws SQLException {
    return false;
  }

  public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types) throws SQLException {
    return null;
  }

  public Connection getConnection() throws SQLException {
    return conn;
  }

}
