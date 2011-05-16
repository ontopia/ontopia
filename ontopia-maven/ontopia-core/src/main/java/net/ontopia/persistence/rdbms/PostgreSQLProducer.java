
// $Id: PostgreSQLProducer.java,v 1.5 2005/10/05 18:01:47 grove Exp $

package net.ontopia.persistence.rdbms;

import java.io.IOException;
import java.util.List;

/** 
 * INTERNAL: DDL statement generator for the PostgreSQL database
 * platform.
 */
public class PostgreSQLProducer extends GenericSQLProducer {
                                               
  public PostgreSQLProducer(Project project) {
    super(project);
  }
  
  public PostgreSQLProducer(Project project, String[] platforms) {
    super(project, platforms);
  }

  protected List dropStatement(Table table, List statements) throws IOException {
    StringBuffer sb = new StringBuffer();
    sb.append("drop table ");
    sb.append(table.getName());
    sb.append(" cascade");
    statements.add(sb.toString());
    return statements;
  }
  
  // -- flags
  
  protected boolean supportsForeignKeys() {
    return true;
  }
  
}
