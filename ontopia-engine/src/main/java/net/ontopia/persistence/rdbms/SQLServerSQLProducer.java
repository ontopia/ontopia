
package net.ontopia.persistence.rdbms;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import net.ontopia.utils.StringUtils;

/** 
 * INTERNAL: Class that generates DDL statements for the sqlserver
 * database platform.
 */

public class SQLServerSQLProducer extends GenericSQLProducer {

  public SQLServerSQLProducer(Project project) {
    super(project);
  }
  
  public SQLServerSQLProducer(Project project, String[] platforms) {
    super(project, platforms);
  }

  // -- flags

  protected boolean supportsForeignKeys() {
    return false;
  }

  protected boolean supportsNullInColumnDefinition() {
    return true;
  }
  
}
