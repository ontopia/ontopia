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

import java.util.HashMap;
import java.util.Map;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: A class descriptor-like class that is used by the RDBMS
 * proxy implementation to access the information it needs about the
 * object relational class descriptor in an optimized manner.<p>
 */
public class ClassInfo implements ClassInfoIF {

  protected RDBMSMapping mapping;
  protected ClassDescriptor cdesc;
  protected Class<?> klass;
  protected Class<?> klass_immutable;
  
  protected Map<String, FieldInfoIF> fields;

  protected FieldInfoIF identity_field;
  protected FieldInfoIF[] value_fields;
  
  protected FieldInfoIF[] o2o_fields;
  protected FieldInfoIF[] o2m_fields;
  protected FieldInfoIF[] m2m_fields;

  //! // FIXME: should extend these to support other tables
  //! protected String[] inline_columns;
  //! protected FieldInfoIF[] inline_fields;
  
  public ClassInfo(RDBMSMapping mapping, ClassDescriptor cdesc) {
    this.mapping = mapping;
    this.cdesc = cdesc;
    this.klass = cdesc.getDescriptorClass();
    this.klass_immutable = cdesc.getImmutableDescriptorClass();
    fields = new HashMap<String, FieldInfoIF>();
  }

  /**
   * INTERNAL: Compile the information in the class descriptor to an
   * optimized form. Called from RDBSMapping, because calling it in
   * the constructor leads to never-ending recursion.
   */
  protected void compile() {
    // compute identity fields
    identity_field = new IdentityFieldInfo(this, compileFieldInfos(this, cdesc.getIdentityFields()));
    
    // compute values fields
    value_fields = compileFieldInfos(this, cdesc.getValueFields());
      
    // compute 1:1 value fields
    o2o_fields = FieldUtils.filterByCardinality(value_fields, FieldInfoIF.ONE_TO_ONE);    
    
    // compute 1:M value fields
    o2m_fields = FieldUtils.filterByCardinality(value_fields, FieldInfoIF.ONE_TO_MANY);    
    
    // compute M:M value fields
    m2m_fields = FieldUtils.filterByCardinality(value_fields, FieldInfoIF.MANY_TO_MANY);    

    // compute lookup map
    FieldInfoIF finfo;
    for (int i=0; i < o2o_fields.length; i++) {
      finfo = o2o_fields[i];
      fields.put(finfo.getName(), finfo);
    }
    for (int i=0; i < o2m_fields.length; i++) {
      finfo = o2m_fields[i];
      fields.put(finfo.getName(), finfo);
    }
    for (int i=0; i < m2m_fields.length; i++) {
      finfo = m2m_fields[i];
      fields.put(finfo.getName(), finfo);
    }
  }

  /**
   * INTERNAL: Returns the RDBMS specific object relational mapping
   * instance.
   */
  @Override
  public ObjectRelationalMappingIF getMapping() {
    return mapping;
  }

  @Override
  public String getName() {
    return klass.getName();
  }

  /**
   * INTERNAL: Return the descriptor class described by the
   * descriptor.
   */
  @Override
  public Class<?> getDescriptorClass() {
    return klass;
  }
  
  @Override
  public Object createInstance(boolean immutable) throws Exception {
    if (immutable) {
      return klass_immutable.newInstance();
    } else {
      return klass.newInstance();
    }
  }

  /**
   * INTERNAL: Get the field info by name.
   */
  @Override
  public FieldInfoIF getFieldInfoByName(String name) {
    // System.out.println("WARN: should deprecate getFieldInfoByName or rename to getValueFieldInfoByName.");
    return fields.get(name);
  }
  
  /**
   * INTERNAL: Get the identity field infos.
   */
  @Override
  public FieldInfoIF getIdentityFieldInfo() {
    return identity_field;
  }
  /**
   * INTERNAL: Get the value field infos.
   */
  @Override
  public FieldInfoIF[] getValueFieldInfos() {
    return value_fields;
  }
  
  /**
   * INTERNAL: Get the 1:1 field infos.
   */
  @Override
  public FieldInfoIF[] getOne2OneFieldInfos() {
    return o2o_fields;
  }
  
  /**
   * INTERNAL: Get the 1:M field infos.
   */
  @Override
  public FieldInfoIF[] getOne2ManyFieldInfos() {
    return o2m_fields;
  }
  
  /**
   * INTERNAL: Get the M:M field infos.
   */
  @Override
  public FieldInfoIF[] getMany2ManyFieldInfos() {
    return m2m_fields;
  }

  @Override
  public boolean isAbstract() {
    return cdesc.isAbstract();
  }

  @Override
  public boolean isIdentifiable() {
    return (cdesc.getType() == ClassInfoIF.TYPE_IDENTIFIABLE);
  }

  @Override
  public boolean isAggregate() {
    return (cdesc.getType() == ClassInfoIF.TYPE_AGGREGATE);
  }

  @Override
  public int getStructure() {
    return cdesc.getStructure();
  }
  
  @Override
  public String getMasterTable() {
    return cdesc.getMasterTable();
  }
  
  //! /**
  //!  * INTERNAL: Returns the fields stored in the specified table.
  //!  */
  //! public FieldInfoIF[] getFieldsInTable(String table) {
  //!   return inline_fields;
  //!   // return (FieldInfoIF[])fields_in_table.get(table);
  //! }
  //! 
  //! /**
  //!  * INTERNAL: Returns the columns stored in the specified table.
  //!  */
  //! public String[] getColumnsInTable(String table) {
  //!   return inline_columns;
  //!   // return (String[])columns_in_table.get(table);
  //! }
  
  /**
   * INTERNAL: Wraps the field descriptors in the appropriate field
   * info implementations.
   */
  protected static FieldInfoIF[] compileFieldInfos(ClassInfoIF cinfo, FieldDescriptor[] fdescs) {
    FieldInfoIF[] finfos = new FieldInfoIF[fdescs.length];
    
    // Loop over field descriptors and collect field info instances
    for (int i=0; i < fdescs.length; i++) {
      // log.info("Compiling field info: " + fdescs[i].getName());
      finfos[i] = getFieldInfo(cinfo, fdescs[i], i);
    }    
    return finfos;
  }

  /**
   * INTERNAL: Wraps the field descriptor in the appropriate field
   * info implementation.
   */
  protected static FieldInfoIF getFieldInfo(ClassInfoIF cinfo, FieldDescriptor fdesc, int index) {
    // Loop over all subfield descriptors of the aggregate descriptor
    // log.debug("VC: " + fdesc.getName() + fdesc.isAggregateField());
    if (fdesc.isPrimitiveField()) {
      return new PrimitiveFieldInfo(cinfo, fdesc, index);
    } else if (fdesc.isReferenceField()) {
      return new ReferenceFieldInfo(cinfo, fdesc, index);
    } else if (fdesc.isAggregateField()) {
      return new AggregateFieldInfo(cinfo, fdesc, index);
    } else {
      throw new OntopiaRuntimeException("Unknown field type: " + fdesc);
    }
  }
  
  @Override
  public String toString() {
    return "<ClassInfo " + cdesc.getName() + ">";
  }
}
