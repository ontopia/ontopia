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
  
/**
 * INTERNAL: Interface for use by classes that retrieve field values
 * from result sets and bind values in prepared statements. This
 * interface is JDBC specific.
 */

public interface FieldHandlerIF {
  
  /**
   * INTERNAL: Returns the number of columns that the field spans.
   */
  public int getColumnCount();

  /**
   * INTERNAL: Returns true if the field handler references an object
   * identity field.
   */
  public boolean isIdentityField();
  
  /**
   * INTERNAL: Reads the value beginning at the given offset in the
   * current ResultSet row. The number of columns actually read
   * depends on the type of object field.
   */    
  public Object load(AccessRegistrarIF registrar, TicketIF ticket, ResultSet rs, int rsindex, boolean direct) throws SQLException;
  
  /**
   * INTERNAL: Binds the object field value starting from the given
   * offset in the prepared statement. The number of columns actually
   * bound depends on the type of object field.
   */    
  public void bind(Object value, PreparedStatement stm, int stmt_index) throws SQLException;

  public void retrieveFieldValues(Object value, List<Object> field_values);
  
  public void retrieveSQLValues(Object value, List<SQLValueIF> sql_values);
  
}






