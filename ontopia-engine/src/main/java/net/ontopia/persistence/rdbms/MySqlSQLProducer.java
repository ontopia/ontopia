
package net.ontopia.persistence.rdbms;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import net.ontopia.utils.StringUtils;

/** 
 * INTERNAL: Class that generates DDL statements for the mysql
 * database platform.
 */

public class MySqlSQLProducer extends GenericSQLProducer {
    
  //! protected String[] platforms = new String[] { "mysql", "generic" };
                                               
  public MySqlSQLProducer(Project project) {
    super(project);
  }
                                               
  public MySqlSQLProducer(Project project, String[] platforms) {
    super(project, platforms);
  }
  
  protected List createStatement(Table table, List statements) throws IOException {
    StringBuffer sb = new StringBuffer();
    String[] pkeys = table.getPrimaryKeys();
    // Create table
    sb.append("create table ");
    sb.append(table.getName());
    sb.append(" (\n");
    Iterator iter = table.getColumns().iterator();
    while (iter.hasNext()) {
      Column col = (Column)iter.next();
      DataType type = project.getDataTypeByName(col.getType(), platforms);
      sb.append("  ");
      sb.append(col.getName());
      sb.append("  ");
      if (type.getType().equals("varchar") && Integer.parseInt(type.getSize()) > 255) {
        sb.append("TEXT");
      } else {        
        sb.append(type.getType());
        sb.append((type.isVariable() ? "(" + type.getSize() + ")" : ""));
      }
      sb.append((!col.isNullable() ? " not null" : ""));
      if (pkeys != null || iter.hasNext())
        sb.append(",");
      sb.append("\n");
      
    }
    // Primary keys
    if (pkeys != null) {
      sb.append("  constraint " + table.getName() +  "_pkey primary key (");
      sb.append(StringUtils.join(pkeys, ", "));
      sb.append(")");
      sb.append("\n");
    }
    
    sb.append(") TYPE = InnoDB\n");    
    statements.add(sb.toString());
    return statements;
  }
  
  /**
   * INTERNAL: Generate the DDL statement(s) to create indexes for the
   * specified table.
   */
  protected List createIndexes(Table table, List statements) throws IOException {
    List indexes = table.getIndexes();
    for (int i=0; i < indexes.size(); i++) {
      Index index = (Index)indexes.get(i);
      StringBuffer sb = new StringBuffer();
      sb.append("create index ");
      sb.append(index.getName());
      sb.append(" on ");
      sb.append(table.getName());
      sb.append("(");      
      String[] cols = index.getColumns();
      for (int x=0; x < cols.length; x++) {
        if (x > 0) sb.append(", ");
        sb.append(cols[x]);
        Column col = table.getColumnByName(cols[x]);
        if (col.getType().equals("Blob") || col.getType().equals("Clob") || col.getType().equals("Text"))
          sb.append("(255)");
      }
      sb.append(")");
      statements.add(sb.toString());
    }
    return statements;
  }
  
}
