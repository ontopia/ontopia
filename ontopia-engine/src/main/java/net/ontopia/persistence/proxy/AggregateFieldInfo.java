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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.ontopia.persistence.query.sql.SQLValueIF;

/**
 * INTERNAL: A field that references an aggregate class.<p>
 *
 * An aggregate field is a composite of one or more fields that
 * together represent the fields of instances of the aggregate value
 * class.<p>
 */

public class AggregateFieldInfo extends AbstractFieldInfo {

  protected ClassInfoIF value_cinfo;
  protected FieldInfoIF[] fields;
  protected String[] value_columns;
  protected int column_count;
  
  public AggregateFieldInfo(ClassInfoIF parent_cinfo, FieldDescriptor field, int index) {
    super(parent_cinfo, field, index);
    
    // Class information
    this.value_cinfo = parent_cinfo.getMapping().getClassInfo(field.getValueClass());

    // Compile field information
    // fields = FieldUtils.compileFieldInfo(field.getValueClassDescriptor().getValueFields());
    fields = value_cinfo.getOne2OneFieldInfos();

    // Compute value columns
    value_columns = computeValueColumns();
    column_count = value_columns.length;
  }

  @Override
  public ClassInfoIF getValueClassInfo() {
    return value_cinfo;
  }

  @Override
  public int getColumnCount() {
    return column_count;
  }
  
  @Override
  public boolean isIdentityField() {
    return false;
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
    for (FieldInfoIF _field : fields) {
      columns.addAll(Arrays.asList(_field.getValueColumns()));
    }    
  }

  protected Object readAggregateObject(AccessRegistrarIF registrar, TicketIF ticket, ResultSet rs, int rsindex, boolean direct) throws SQLException {
    // Instanciate new aggregate object
    Object aggregate_object = null;
    
    // Loop over aggregate fields and collect field values
    int width = getColumnCount();    
    for (int i=0; i < width; i++) {
      // Load field value
      FieldInfoIF finfo = fields[i];      
      Object value = finfo.load(registrar, ticket, rs, rsindex, direct);

      // FIXME: If all aggregate columns contains nulls the aggregate
      // object will not be created. Other policies might also be
      // useful. It could perhaps be specified in the mapping file.
      
      // Only set value if field is not null
      if (value != null) {
        try {
          if (aggregate_object == null) {
            // FIXME: Should use a factory to create instances instead
            // e.g. AggregateFactoryIF.create(FieldInfoIF)
            aggregate_object = field.getValueClass().newInstance();
          }
          // Set value
          finfo.setValue(aggregate_object, value);
        } catch (Exception e) {
          throw new PersistenceRuntimeException(e);
        }
      }

      // Increment column index
      rsindex += finfo.getColumnCount();
    }

    return aggregate_object;
  }

  /// --- FieldHandlerIF implementation
  
  /**
   * INTERNAL: Loads from its containing fields an aggregate object.
   */
  @Override
  public Object load(AccessRegistrarIF registrar, TicketIF ticket, ResultSet rs, int rsindex, boolean direct) throws SQLException {
    // Read aggregate object
    return readAggregateObject(registrar, ticket, rs, rsindex, direct);
  }
  
  @Override
  public void bind(Object value, PreparedStatement stm, int stmt_index) throws SQLException {
    // value is an aggregate object

    // Let each aggregate field bind each key value
    int offset = stmt_index;
    for (int i=0; i < fields.length; i++) {
      FieldInfoIF finfo = fields[i];

      // FIXME: Could do the getValue and bind in one operation:
      // FieldInfoIF.bindObject(o,stm,offset); Alternatively the
      // getValue method should be moved somewhere else, or at least
      // out of the FieldInfoIF interface.
      Object field_value;
      if (value == null) {
        field_value = null;
      } else {
        try {
          field_value = finfo.getValue(value);
        } catch (Exception e) {
          throw new PersistenceRuntimeException(e);
        }
      }
      
      finfo.bind(field_value, stm, offset);
      offset += finfo.getColumnCount();
    }
    
  }

  @Override
  public void retrieveFieldValues(Object value, List<Object> field_values) {
    for (int i=0; i < fields.length; i++) {      
      try {
        if (value == null) { 
          fields[i].retrieveFieldValues(null, field_values);
        } else {
          fields[i].retrieveFieldValues(fields[i].getValue(value), field_values);
        }
      } catch (Exception e) {
        throw new PersistenceRuntimeException(e);
      }
    }
  }

  @Override
  public void retrieveSQLValues(Object value, List<SQLValueIF> sql_values) {
    for (int i=0; i < fields.length; i++) {      
      try {
        if (value == null) { 
          fields[i].retrieveSQLValues(null, sql_values);
        } else {
          fields[i].retrieveSQLValues(fields[i].getValue(value), sql_values);
        }
      } catch (Exception e) {
        throw new PersistenceRuntimeException(e);
      }
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("<AggregateFieldInfo " + field.getName() + " [");
    for (int i=0; i < fields.length; i++) {
      sb.append(fields[i].toString());
    }
    sb.append("]>");
    return sb.toString();
  }
  
}
