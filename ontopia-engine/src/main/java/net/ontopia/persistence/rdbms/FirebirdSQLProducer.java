
package net.ontopia.persistence.rdbms;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/** 
 * INTERNAL: Class that generates DDL statements for the firebird /
 * interbase database platform.
 */

public class FirebirdSQLProducer extends GenericSQLProducer {

  public FirebirdSQLProducer(Project project) {
    super(project);
  }
  
  public FirebirdSQLProducer(Project project, String[] platforms) {
    super(project, platforms);
  }

  protected boolean supportsNullInColumnDefinition() {
    return false;
  }
  
}
