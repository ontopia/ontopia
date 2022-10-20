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

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.ontopia.persistence.query.sql.SQLValueIF;

/**
 * INTERNAL: A field that represents the identity of instances of a
 * class. An identity field is a composite of one or more fields that
 * together represent the identity of objects.<p>
 */

public class IdentityFieldInfo implements FieldInfoIF {
  private static final String UNSUPPORTED_METHOD_MESSAGE = "This method should not be called for IdentityFieldInfo.";
  
  protected ClassInfoIF parent_cinfo;
  protected Class<?> parent_class;
  
  protected FieldInfoIF[] fields;
  protected int fields_length;
  
  protected String[] value_columns;
  protected int column_count;

  protected Method getter;
  protected Method setter;
  
  protected int sqlType = -1;
  
  public IdentityFieldInfo(ClassInfoIF parent_cinfo, FieldInfoIF[] identity_fields) {
    // Class information
    this.parent_cinfo = parent_cinfo;
    this.parent_class = parent_cinfo.getDescriptorClass();
    
    // Field information
    this.fields = identity_fields;
    this.fields_length = fields.length;
    
    // Compute value columns
    this.value_columns = computeValueColumns();
    this.column_count = value_columns.length;
    
    // Optimization: Hack to figure out if the field is a long field
    if (fields_length == 1 && fields[0] instanceof PrimitiveFieldInfo) {
      this.sqlType = ((PrimitiveFieldInfo)fields[0]).getSQLType();
    } 
  }

  /**
   * INTERNAL: Returns the underlying FieldInfoIFs that the identity
   * field spans.
   */  
  public FieldInfoIF[] getFields() {
    return fields;
  }
  
  @Override
  public String getName() {
    throw new UnsupportedOperationException(UNSUPPORTED_METHOD_MESSAGE);
  }

  @Override
  public int getIndex() {
    throw new UnsupportedOperationException(UNSUPPORTED_METHOD_MESSAGE);
  }
  
  @Override
  public int getCardinality() {
    return FieldInfoIF.ONE_TO_ONE;
  }

  @Override
  public boolean isReadOnly() {
    return true;
  }
  
  @Override
  public boolean isIdentityField() {
    return true;
  }

  @Override
  public boolean isCollectionField() {
    return false;
  }

  @Override
  public boolean isPrimitiveField() {
    return false;
  }
  
  @Override
  public boolean isReferenceField() {
    return false;
  }

  @Override
  public boolean isAggregateField() {
    return false;
  }
  
  @Override
  public ClassInfoIF getParentClassInfo() {
    return parent_cinfo;
  }

  @Override
  public ClassInfoIF getValueClassInfo() {
    throw new UnsupportedOperationException(UNSUPPORTED_METHOD_MESSAGE);
  }

  @Override
  public Class<?> getValueClass() {
    throw new UnsupportedOperationException(UNSUPPORTED_METHOD_MESSAGE);
  }

  @Override
  public String getTable() {
    throw new UnsupportedOperationException(UNSUPPORTED_METHOD_MESSAGE);
  }

  @Override
  public int getColumnCount() {
    return column_count;
  }

  @Override
  public String[] getValueColumns() {
    return value_columns;
  }
  
  protected String[] computeValueColumns() {
    // Collect column names from children
    List<String> names = new ArrayList<String>();
    aggregateColumnNames(names);
    // Morph into a string array
    String[] _names = new String[names.size()];
    names.toArray(_names);
    return _names;
  }
  
  protected void aggregateColumnNames(List<String> columns) {
    for (int i=0; i < fields_length; i++) {
      columns.addAll(Arrays.asList(fields[i].getValueColumns()));
    }    
  }

  private IdentityIF getIdentity(Object value) {
    if (value instanceof PersistentIF) {
      return ((PersistentIF)value)._p_getIdentity();
    } else {
      return (IdentityIF)value;
    }
  }

  //! // WARNING: This is actually incorrect, since we should be getting
  //! // the identity of the value object, not that parent object as we
  //! // do here.
  //! 
  //! public Object getValue(Object object) throws Exception {
  //!   return ((PersistentIF)object)._p_getIdentity();
  //! }
  //! 
  //! public void setValue(Object object, Object value) throws Exception {
  //!   ((PersistentIF)object)._p_setIdentity((IdentityIF)value);
  //! }

  @Override
  public Object getValue(Object object) throws Exception {
    throw new UnsupportedOperationException(UNSUPPORTED_METHOD_MESSAGE);
  }

  @Override
  public void setValue(Object object, Object value) throws Exception {
    throw new UnsupportedOperationException(UNSUPPORTED_METHOD_MESSAGE);
  }

  @Override
  public String getJoinTable() {
    throw new UnsupportedOperationException(UNSUPPORTED_METHOD_MESSAGE);
  }

  @Override
  public String[] getJoinKeys() {
    throw new UnsupportedOperationException(UNSUPPORTED_METHOD_MESSAGE);
  }

  @Override
  public String[] getManyKeys() {
    throw new UnsupportedOperationException(UNSUPPORTED_METHOD_MESSAGE);
  }

  /// --- FieldHandlerIF implementation

  /**
   * INTERNAL: Loads from its containing fields an IdentityIF with the
   * field values as key.
   */
  @Override
  public Object load(AccessRegistrarIF registrar, TicketIF ticket, ResultSet rs, int rsindex, boolean direct) throws SQLException {
    // FIXME: Should we open up for the possibility of identity fields
    // should being null? Then reference field handlers can just
    // delegate to identity field handlers.

    // Optimization: primitive long field
    if (sqlType == Types.BIGINT) {
      long value = rs.getLong(rsindex);
      if (rs.wasNull()) { 
        return null;
      } else {
        return registrar.createIdentity(parent_class, value);
      }
      
    } else {
      // If first key is null, no object is referenced.
      Object first_key = fields[0].load(registrar, ticket, rs, rsindex, direct);
      if (first_key == null) {
        return null;
      }
      
      if (fields_length == 1) {
        // If the identity only consists of a single key component return
        // an atomic identity instance.
        //! return new AtomicIdentity(parent_class, first_key);
        return registrar.createIdentity(parent_class, first_key);
        
      } else {    
        // Initialize key array
        Object[] keys = new Object[fields_length];
        
        // Insert first key
        keys[0] = first_key;
        
        // Loop over key fields and collect field values
        for (int i=1; i < fields_length; i++) {
          rsindex += fields[i-1].getColumnCount();
          keys[i] = fields[i].load(registrar, ticket, rs, rsindex, direct);
        }
        
        // Return identity
        return registrar.createIdentity(parent_class, keys);
      }
    }
  }

  /**
   * INTERNAL: Binds the identity keys to the containing fields.
   */
  @Override
  public void bind(Object value, PreparedStatement stm, int stmt_index) throws SQLException {
    // Get the identity
    IdentityIF identity = getIdentity(value);

    if (identity == null) {
      // All keys bind null
      for (int i=0; i < fields_length; i++) {
        fields[i].bind(null, stm, stmt_index);   
      }
    } else {
      // Bind key value
      for (int i=0; i < fields_length; i++) {
        fields[i].bind(identity.getKey(i), stm, stmt_index);   
      }
    }
  }

  @Override
  public void retrieveFieldValues(Object value, List<Object> field_values) {
    // Get the identity keys
    IdentityIF identity = getIdentity(value);

    if (identity == null) {
      // Use null
      for (int i=0; i < fields_length; i++) {
        fields[i].retrieveFieldValues(null, field_values);   
      }

    } else {        
      // Use key value
      for (int i=0; i < fields_length; i++) {
        fields[i].retrieveFieldValues(identity.getKey(i), field_values);   
      }
    }
  }
  
  @Override
  public void retrieveSQLValues(Object value, List<SQLValueIF> sql_values) {
    // Get the identity keys
    IdentityIF identity = getIdentity(value);

    if (identity == null) {
      // Use null
      for (int i=0; i < fields_length; i++) {
        fields[i].retrieveSQLValues(null, sql_values);   
      }
    } else {
      // Use key value
      for (int i=0; i < fields_length; i++) {
        fields[i].retrieveSQLValues(identity.getKey(i), sql_values);   
      }
    }
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("<IdentityFieldInfo [");
    for (int i=0; i < fields_length; i++) {
      sb.append(fields[i].toString());
    }
    sb.append("]>");
    return sb.toString();
  }

  /// -- Misc

  /**
   * INTERNAL: Returns the underlying FieldInfoIF instances.
   */
  
  public FieldInfoIF[] getFieldInfos() {
    return fields;
  }
  
}
