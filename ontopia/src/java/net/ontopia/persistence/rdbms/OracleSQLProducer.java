
// $Id: OracleSQLProducer.java,v 1.16 2006/12/18 12:27:23 grove Exp $

package net.ontopia.persistence.rdbms;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import net.ontopia.utils.StringUtils;

/** 
 * INTERNAL: Class that generates DDL statements for the oracle
 * database platform.
 */

public class OracleSQLProducer extends GenericSQLProducer {

  public OracleSQLProducer(Project project) {
    super(project);
  }
  
  public OracleSQLProducer(Project project, String[] platforms) {
    super(project, platforms);
  }
  
  protected List dropStatement(Table table, List statements) throws IOException {
    StringBuffer sb = new StringBuffer();
    sb.append("drop table ");
    sb.append(table.getName());
    sb.append(" cascade constraints");
    statements.add(sb.toString());
    return statements;
  }

  // -- flags

  protected boolean supportsForeignKeys() {
    return true;
  }

  protected boolean supportsNullInColumnDefinition() {
    return false;
  }
  
}
