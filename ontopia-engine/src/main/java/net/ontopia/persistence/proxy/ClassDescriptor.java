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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Class used for holding object relational mapping class
 * declarations. It is used by the ObjectRelationalMapping class.<p>
 *
 * A class descriptor contains information about object classes
 * defined in object relational mappings. Class descriptors consists
 * of a set of field descriptors.<p>
 */

public class ClassDescriptor {
    
  protected ObjectRelationalMapping mapping;
  protected Class<?> klass;
  protected Class<?> klass_immutable;
  
  protected String[] identity_fields = new String[0];
  protected boolean isabstract;
  protected int type;
  protected int structure;
  
  protected String master_table;
  protected Class<?>[] extends_classes = new Class<?>[0];
  protected Class<?>[] interfaces = new Class<?>[0];

  protected List<FieldDescriptor> fdescs_list;
  protected Map<String, FieldDescriptor> fdescs;
    
  public ClassDescriptor(Class<?> klass, Class<?> klass_immutable, ObjectRelationalMapping mapping) {
    this.klass = klass;
    this.klass_immutable = klass_immutable;
    this.mapping = mapping;

    fdescs_list = new ArrayList<FieldDescriptor>();
    fdescs = new HashMap<String, FieldDescriptor>();
  }

  /**
   * INTERNAL: Gets the object relational mapping that the class
   * descriptor belongs to.
   */
  public ObjectRelationalMapping getMapping() {
    return mapping;
  }

  /**
   * INTERNAL: Returns the name of the descriptor class (the mapped class).
   */
  public String getName() {
    return klass.getName();
  }
  
  /**
   * INTERNAL: Returns the descriptor class.
   */
  public Class<?> getDescriptorClass() {
    return klass;
  }
  
  /**
   * INTERNAL: Returns the immutable descriptor class.
   */
  public Class<?> getImmutableDescriptorClass() {
    return klass_immutable;
  }
  
  /**
   * INTERNAL: Returns an array containing the fields that are
   * identity fields.<p>
   *
   * An identity field is the field that together represents the
   * identity, or primary key, of instances of the class.<p>
   */
  public FieldDescriptor[] getIdentityFields() {
    FieldDescriptor[] result = new FieldDescriptor[identity_fields.length];
    for (int i=0; i < identity_fields.length; i++) {
      result[i] = getFieldByName(identity_fields[i]);
      if (result[i] == null) {
        throw new OntopiaRuntimeException("Unknown identity field: " + identity_fields[i]);
      }
    }
    return result;
  }
  
  /**
   * INTERNAL: Returns an array containing the fields that are value
   * fields, i.e. not identity fields.
   */
  public FieldDescriptor[] getValueFields() {
    List<FieldDescriptor> id_fields = Arrays.asList(getIdentityFields());
    Collection<FieldDescriptor> result = new ArrayList<FieldDescriptor>();
    for (FieldDescriptor fdesc : getFieldDescriptors()) {
      if (!id_fields.contains(fdesc)) {
        result.add(fdesc);
      }
    }
    return FieldUtils.toFieldDescriptorArray(result);
  }
  
  /**
   * INTERNAL: Returns an array containing the fields that are of
   * primitive types.
   */
  public FieldDescriptor[] getPrimitiveFields() {
    Collection<FieldDescriptor> result = new ArrayList<FieldDescriptor>();
    for (FieldDescriptor fdesc : getFieldDescriptors()) {
      if (fdesc.isPrimitiveField()) {
        result.add(fdesc);
      }
    }
    return FieldUtils.toFieldDescriptorArray(result);
  }
  
  /**
   * INTERNAL: Returns an array containing the fields that references
   * other mapped objects.
   */
  public FieldDescriptor[] getReferenceFields() {
    Collection<FieldDescriptor> result = new ArrayList<FieldDescriptor>();
    for (FieldDescriptor fdesc : getFieldDescriptors()) {
      if (fdesc.isReferenceField()) {
        result.add(fdesc);
      }
    }
    return FieldUtils.toFieldDescriptorArray(result);
  }
  
  /**
   * INTERNAL: Returns an array containing the fields that are
   * aggregate objects. Aggregate objects are composite objects that
   * don't have explicit identity.
   */
  public FieldDescriptor[] getAggregateFields() {
    if (isAggregate()) {
      return getFieldDescriptors();
    }
    Collection<FieldDescriptor> result = new ArrayList<FieldDescriptor>();
    for (FieldDescriptor fdesc : getFieldDescriptors()) {
      if (fdesc.isAggregateField()) {
        result.add(fdesc);
      }
    }
    return FieldUtils.toFieldDescriptorArray(result);
  }
  
  /**
   * INTERNAL: Returns the field names of the identity fields.
   */
  protected String[] getIdentityFieldNames() {
    return identity_fields;
  }
  
  /**
   * INTERNAL: Sets the field names of the identity fields.
   */
  protected void setIdentityFieldNames(String[] identity_fields) {
    this.identity_fields = identity_fields;
  }

  /**
   * INTERNAL: Returns the class type.
   */
  public int getType() {
    return type;
  }

  /**
   * INTERNAL: Sets the class type. This is can either be
   * ClassInfoIF.TYPE_IDENTIFIABLE or ClassInfoIF.TYPE_AGGREGATE.
   */
  public void setType(int type) {
    // TODO: PRIMITIVE not yet supported.
    if (type != ClassInfoIF.TYPE_IDENTIFIABLE &&
        type != ClassInfoIF.TYPE_AGGREGATE) {
      throw new IllegalArgumentException("Invalid argument: " + type);
    }
    this.type = type;
  }

  public boolean isAggregate() {
    return (type == ClassInfoIF.TYPE_AGGREGATE);
  }

  /**
   * INTERNAL: Returns the class structure.
   */
  public int getStructure() {
    return structure;
  }

  /**
   * INTERNAL: Sets the class structure. This is can either be
   * ClassInfoIF.STRUCTURE_OBJECT or ClassInfoIF.STRUCTURE_COLLECTION.
   */
  public void setStructure(int structure) {
    // TODO: MAP not yet supported.
    if (structure != ClassInfoIF.STRUCTURE_OBJECT &&
        structure != ClassInfoIF.STRUCTURE_COLLECTION) {
      throw new IllegalArgumentException("Invalid argument: " + structure);
    }
    this.structure = structure;
  }

  /**
   * INTERNAL: Sets the abstract flag. The default is that the
   * descriptor class is non-abstract (i.e. concrete).
   */
  public void setAbstract(boolean isabstract) {
    this.isabstract = isabstract;
  }

  /**
   * INTERNAL: Returns true if the descriptor class is mapped as an
   * abstract class.
   */
  public boolean isAbstract() {
    return isabstract;
  }
  
  /**
   * INTERNAL: Returns the name of the master table in which the class
   * is stored.
   */
  public String getMasterTable() {
    return master_table;
  }

  /**
   * INTERNAL: Sets the name of the master table in which the class is
   * stored.
   */
  public void setMasterTable(String master_table) {
    this.master_table = master_table;
  }

  /**
   * INTERNAL: Returns the descriptor classes that this descriptor
   * class extends. (Not implemented)
   */
  public Class[] getExtends() {
    return extends_classes;
  }

  /**
   * INTERNAL: Sets the descriptor classes that this descriptor class
   * extends. (Not implemented)
   */
  public void setExtends(Class[] extends_classes) {
    this.extends_classes = extends_classes;
  }

  protected Map<String, FieldDescriptor> getFieldDescriptorMap() {
    Map<String, FieldDescriptor> all_fields = new HashMap<String, FieldDescriptor>();
    populateExtendsMap(all_fields);
    return all_fields;
  }

  protected void populateExtendsMap(Map<String, FieldDescriptor> _fdescs) {
    for (Class<?> extends_classe : extends_classes) {
      getMapping().getDescriptorByClass(extends_classe).populateExtendsMap(_fdescs);
    }
    // Add my own
    _fdescs.putAll(this.fdescs);
  }

  /**
   * INTERNAL: Gets the interfaces that this descriptor class
   * implement.
   */
  public Class[] getInterfaces() {
    return interfaces;
  }

  /**
   * INTERNAL: Sets the interfaces that this descriptor class
   * implement.
   */
  public void setInterfaces(Class[] interfaces) {
    this.interfaces = interfaces;
  }
  
  /**
   * INTERNAL: Returns all the field descriptors of this class
   * descriptor.
   */
  public FieldDescriptor[] getFieldDescriptors() {
    // Return local field definitions
    return FieldUtils.toFieldDescriptorArray(fdescs_list);

    // FIXME: add real support for inheritance
    // Note: the ordering of fields is important
    
    // Class[] _extends = getExtends();
    // if (_extends != null && _extends.length > 0) {
    //   // Populate field definitions from parents
    //   return FieldUtils.toFieldDescriptorArray(getFieldDescriptorMap().values());
    // }
    // else {
    //   // Return local field definitions
    //   return FieldUtils.toFieldDescriptorArray(fdescs.values());
    // }
  }

  /**
   * INTERNAL: Gets all the field names of this class descriptor.
   */
  public String[] getFieldNames() {
    return FieldUtils.toStringArray(getFieldDescriptorMap().keySet());
  }

  /**
   * INTERNAL: Gets the field descriptor representing the field with
   * the given name.
   */
  public FieldDescriptor getFieldByName(String field_name) {
    return getFieldDescriptorMap().get(field_name);
  }
  
  /**
   * INTERNAL: Adds the field descriptor to the class descriptor.
   */
  public void addField(FieldDescriptor fdesc) {
    // Put on ordered list
    fdescs_list.add(fdesc);
    // Add to lookup map
    fdescs.put(fdesc.getName(), fdesc);
  }

  // /**
  //  * INTERNAL: Utility method that returns the field descriptors
  //  * representing the fields in the input array.
  //  */
  // public FieldDescriptor[] getFieldDescriptors(String[] fields) {
  //   FieldDescriptor[] fdescs = new FieldDescriptor[fields.length];
  //   for (int i=0; i < fields.length; i++) {
  //     FieldDescriptor fdesc = getFieldByName(fields[i]);
  //     if (fdesc == null)
  //       throw new OntopiaRuntimeException("No field descriptor with the name " + fields[i]);
  //     fdescs[i] = fdesc;
  //   }
  //   return fdescs;
  // }

  @Override
  public String toString() {
    return "<ClassDescriptor " + getName() + ">";
  }
  
}
