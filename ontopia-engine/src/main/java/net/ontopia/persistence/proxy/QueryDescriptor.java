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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ontopia.persistence.query.sql.DefaultParameterProcessor;
import net.ontopia.persistence.query.sql.DetachedQueryIF;
import net.ontopia.persistence.query.sql.RDBMSCollectionQuery;
import net.ontopia.persistence.query.sql.RDBMSMapQuery;
import net.ontopia.persistence.query.sql.RDBMSMatrixQuery;
import net.ontopia.persistence.query.sql.RDBMSObjectQuery;
import net.ontopia.persistence.query.sql.RDBMSQuery;
import net.ontopia.persistence.query.sql.SQLStatement;
import net.ontopia.persistence.query.sql.SQLStatementIF;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Class used for loading and managing SQL query
 * declarations. It is used by the rdbms proxy implementation, but
 * should implement a query factory interface in the future.
 */

public class QueryDescriptor {
  
  protected String name;
  protected String type;
  protected int fetchSize;
  protected SelectField[] selects;
  protected Class[] params;
  protected Map<String, String> statements;
  protected boolean lookup_identities;
  
  public QueryDescriptor(String name, String type, boolean lookup_identities) {
    this.name = name;
    this.type = type;
    this.statements = new HashMap<String, String>();
    this.lookup_identities = lookup_identities;
  }

  /**
   * INTERNAL: Returns the name of the query. This field is used
   * together with named query execution.
   */
  public String getName() {
    return name;
  }

  /**
   * INTERNAL: Gets the query result type name.
   */
  public String getType() {
    return type;
  }

  /**
   * INTERNAL: Gets the default fetch size.
   */
  public int getFetchSize() {
    return fetchSize;
  }
  
  /**
   * INTERNAL: Sets the default fetch size.
   */
  public void setFetchSize(int fetchSize) {
    this.fetchSize = fetchSize;
  }
  
  /**
   * INTERNAL: Sets the selected fields.
   */
  public void setSelects(List<SelectField> selects) {
    this.selects = new SelectField[selects.size()];
    selects.toArray(this.selects);
  }
  
  /**
   * INTERNAL: Sets the class type of the query parameters.
   */
  public void setParameters(List<Class<?>> params) {
    this.params = new Class[params.size()];
    params.toArray(this.params);
  }

  /**
   * INTERNAL: Gets the class type of the query parameters.
   */
  public String getStatement(String[] platforms) {
    for (int i=0; i < platforms.length; i++) {
      if (statements.containsKey(platforms[i])) {
        return statements.get(platforms[i]);
      }
    }
    throw new OntopiaRuntimeException("No statement available for query '" + getName() +
                                      "' (platforms: " + Arrays.asList(platforms) + ")");
  }
  
  /**
   * INTERNAL: Registers the query statement for the specified
   * platforms.
   */
  public void addStatement(String[] platforms, String statement) {
    for (int i=0; i < platforms.length; i++) {
      if (statements.containsKey(platforms[i])) {
        throw new OntopiaRuntimeException("Duplicate statements for '" + platforms[i] + "' (query: " + getName() + ")");
      }
      statements.put(platforms[i], statement);
    }
  }

  /**
   * INTERNAL: Creates a QueryIF instance that uses the specified
   * storage access and platform settings.
   */
  public DetachedQueryIF createSharedQuery(StorageIF storage, AccessRegistrarIF registrar, String[] platforms) {
    SQLStatement stm = createSQLStatement(storage, platforms);
    stm.setAccessRegistrar(registrar);
    return createDetachedQuery(stm, getType(), false);
  }

  /**
   * INTERNAL: Creates a QueryIF instance that uses the specified
   * storage access, object access, access registrar and platform
   * settings for this query descriptor.
   */
  public QueryIF createQuery(RDBMSAccess access, ObjectAccessIF oaccess, 
                             AccessRegistrarIF registrar, String[] platforms) {
    SQLStatement stm = createSQLStatement(access.getStorage(), platforms);
    stm.setObjectAccess(oaccess);
    stm.setAccessRegistrar(registrar);
    DetachedQueryIF query = createDetachedQuery(stm, getType(), this.lookup_identities);
    return new RDBMSQuery(access, query);
  }

  protected SQLStatement createSQLStatement(StorageIF storage, String[] platforms) {

    
    ObjectRelationalMappingIF mapping = storage.getMapping();
    
    FieldHandlerIF[] select_fields = getSelectFieldHandlers(mapping, selects);
    FieldHandlerIF[] param_fields = getParameterHandlers(mapping, params);

    String[] param_names = null; // TODO: Add support for parameter names
    DefaultParameterProcessor param_proc = 
      new DefaultParameterProcessor(param_fields, param_names);
    if (fetchSize > 0) {
      param_proc.setFetchSize(fetchSize);
    }

    SQLStatement stm = new SQLStatement(getStatement(platforms), select_fields, param_proc);
   
    if (fetchSize > 0) {
      stm.setFetchSize(fetchSize);
    }

    return stm;
  }

  protected DetachedQueryIF createDetachedQuery(SQLStatementIF stm, String type, boolean lookup_identities) {
    switch (type) {
      case "object": return new RDBMSObjectQuery(stm, lookup_identities);
      case "collection": return new RDBMSCollectionQuery(stm, lookup_identities);
      case "matrix": return new RDBMSMatrixQuery(stm, lookup_identities);
      case "map": return new RDBMSMapQuery(stm, lookup_identities);
      default: throw new OntopiaRuntimeException("Invalid query type: " + type);
    }
  }
  
  protected FieldHandlerIF[] getSelectFieldHandlers(ObjectRelationalMappingIF mapping, SelectField[] selects) {
    FieldHandlerIF[] handlers = new FieldHandlerIF[selects.length];
    for (int i=0; i < selects.length; i++) {
      handlers[i] = getSelectFieldHandler(mapping, selects[i]);
    }
    return handlers;
    
  }
  
  protected FieldHandlerIF getSelectFieldHandler(ObjectRelationalMappingIF mapping, SelectField select) {
    switch (select.getType()) {
    case SelectField.SELECT_CLASS:
      return getFieldHandler(mapping, (Class)select.getValue());
    case SelectField.SELECT_INDICATOR:
      return new IndicatorFieldHandler(mapping, (Map)select.getValue());
    default:
      throw new OntopiaRuntimeException("Select field is invalid: " + select);
    }
  }
  
  protected FieldHandlerIF[] getParameterHandlers(ObjectRelationalMappingIF mapping, Class[] params) {    
    FieldHandlerIF[] handlers = new FieldHandlerIF[params.length];
    for (int i=0; i < params.length; i++) {
      handlers[i] = getFieldHandler(mapping, params[i]);
    }
    return handlers;
  }

  protected FieldHandlerIF getFieldHandler(ObjectRelationalMappingIF mapping, Class klass) {
    if (mapping.isDeclared(klass)) {
      ClassInfoIF cinfo = mapping.getClassInfo(klass);
      if (cinfo.isIdentifiable()) {
        return cinfo.getIdentityFieldInfo();
        // FIXME: Also handle aggregate types
        //! else if (cinfo.isAggregate()) {
        //!   handlers[i] = cinfo.getAggregateF();
      }
      else {
        throw new OntopiaRuntimeException("Parameter class has invalid descriptor: " + klass); 
      }
    } else {
      return new DefaultFieldHandler(SQLTypes.getType(klass));
    }
  }
        
  static class SelectField {

    public static final int SELECT_CLASS = 1;
    public static final int SELECT_INDICATOR = 2;
    public static final int SELECT_FACTORY = 3;

    protected Object value;
    protected int type;
    
    public SelectField(Object value, int type) {
      this.value = value;
      this.type = type;
    }

    public int getType() {
      return type;
    }

    public Object getValue() {
      return value;
    }

    @Override
    public String toString() {
      return "<SelectField " + type + " " + value + ">";
    }
  }
  
}
