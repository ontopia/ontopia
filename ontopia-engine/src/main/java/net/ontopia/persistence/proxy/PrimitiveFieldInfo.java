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

package net.ontopia.persistence.proxy;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import net.ontopia.persistence.query.sql.SQLNull;
import net.ontopia.persistence.query.sql.SQLPrimitive;
import net.ontopia.persistence.query.sql.SQLValueIF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: A field that references a primitive value class.
 */

public class PrimitiveFieldInfo extends AbstractFieldInfo {

  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(PrimitiveFieldInfo.class.getName());

  protected String colname;
  protected int sqltype;
  
  PrimitiveFieldInfo(ClassInfoIF parent_cinfo, FieldDescriptor field, int index) {
    super(parent_cinfo, field, index);
    
    // Compile column information
    colname = field.getColumns()[0];
    sqltype = SQLTypes.getType(field.getValueClass());
  }

  public int getSQLType() {
    return sqltype;
  }
  
  @Override
  public ClassInfoIF getValueClassInfo() {
    return null;
  }

  @Override
  public int getColumnCount() {
    return 1;
  }
  
  @Override
  public boolean isIdentityField() {
    return false;
  }
  
  @Override
  public String[] getValueColumns() {
    return new String[] {colname};
  }

  /// --- FieldHandlerIF implementation
  
  /**
   * INTERNAL: Loads a single primitive value.
   */
  @Override
  public Object load(AccessRegistrarIF registrar, TicketIF ticket, ResultSet rs, int rsindex, boolean direct) throws SQLException {
    // Read primitive value
    Object value = SQLTypes.getObject(rs, rsindex, sqltype, direct);
    if (log.isDebugEnabled()) {
      log.debug("PF: Loading column " + colname + "=" + value);
    }
    return value;
  }
  
  @Override
  public void bind(Object value, PreparedStatement stm, int stmt_index) throws SQLException {
    // value is a primitive object
    if (log.isDebugEnabled()) {
      log.debug("PF: Binding [" + stmt_index + "] " + colname + "=" + value);
    }
    SQLTypes.setObject(stm, stmt_index, value, sqltype);
  }

  @Override
  public void retrieveFieldValues(Object value, List<Object> field_values) {
    field_values.add(value);
  }

  @Override
  public void retrieveSQLValues(Object value, List<SQLValueIF> sql_values) {
    if (value == null) {      
      sql_values.add(new SQLNull()); // TODO: Use SQLNull.getInstance() / SQLNull.INSTANCE
    } else {
      sql_values.add(new SQLPrimitive(value, sqltype));
    }      
  }

  @Override
  public String toString() {
    return "<PrimitiveFieldInfo " + field.getName() + ">";
  }
  
}
