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
import net.ontopia.persistence.query.sql.SQLValueIF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: The default field handler implementation that is able to
 * read values from result sets and bind values in prepared statements
 * without any particular knowledge about fields. The field handler
 * only works with single columns of the type specified in the class
 * constructor.<p>
 */

public class DefaultFieldHandler implements FieldHandlerIF {

  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(DefaultFieldHandler.class.getName());

  protected int sql_type;

  /**
   * Creates a instance of the default field handler by passing the
   * SQL type to used for reading and storing column values.
   */
  public DefaultFieldHandler(int sql_type) {
    this.sql_type = sql_type;
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
  public Object load(AccessRegistrarIF registrar, TicketIF ticket, ResultSet rs, int rsindex, boolean direct) throws SQLException {
    // Read primitive value
    Object value = SQLTypes.getObject(rs, rsindex, sql_type, direct);
    if (log.isDebugEnabled()) {
      log.debug("DF: Loading index " + rsindex + "=" + value);
    }
    // Set value
    return value;
  }
  
  @Override
  public void bind(Object value, PreparedStatement stm, int stmt_index) throws SQLException {
    // value is a primitive object
    if (log.isDebugEnabled()) {
      log.debug("DF: Binding index " + stmt_index + "=" + value);
    }
    SQLTypes.setObject(stm, stmt_index, value, sql_type);
  }

  @Override
  public void retrieveFieldValues(Object value, List<Object> field_values) {
    throw new UnsupportedOperationException("Default field handler cannot retrieve field values.");
  }

  @Override
  public void retrieveSQLValues(Object value, List<SQLValueIF> sql_values) {
    throw new UnsupportedOperationException("Default field handler cannot retrieve sql values.");
    //! if (value == null)      
    //!   field_values.add(new SQLNull());
    //! else
    //!   field_values.add(new SQLPrimitive(value, sql_type));      
  }

  @Override
  public String toString() {
    return "<DefaultFieldHandler " + sql_type + ">";
  }
  
}





