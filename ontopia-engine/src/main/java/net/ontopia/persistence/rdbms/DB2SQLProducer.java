
package net.ontopia.persistence.rdbms;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import net.ontopia.utils.StringUtils;

/** 
 * INTERNAL: Class that generates DDL statements for the IBM db2
 * universal database platform.
 */

public class DB2SQLProducer extends GenericSQLProducer {
                                               
  public DB2SQLProducer(Project project) {
    super(project);
  }
                                               
  public DB2SQLProducer(Project project, String[] platforms) {
    super(project, platforms);
  }

  protected boolean supportsNullInColumnDefinition() {
    return false;
  }

  /* Limits: constraint and index names limited to 18 bytes */

  protected String getPrimaryKeyName(Table table) {
    String sname = table.getShortName();
    return (sname != null ? sname : table.getName()) + "_pkey";
  }

  protected String getIndexName(Index index) {
    String sname = index.getShortName();
    return (sname != null ? sname : index.getName());
  }
  
}
