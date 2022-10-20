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
import java.util.Set;
import java.util.HashSet;

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Class used for holding object relational mapping field
 * declarations. It is used by the ObjectRelationalMapping class.<p>
 *
 * A field descriptor contains information about fields of object
 * classes defined in object relational mappings. A field descriptor
 * belongs to a single class descriptor.<p>
 */

public class FieldDescriptor {

  private final static Set<Class<?>> PRIMITIVE_TYPES;

  static {
    Set<Class<?>> pt = new HashSet<Class<?>>();
    pt.add(String.class);
    pt.add(java.io.Reader.class);
    pt.add(java.io.InputStream.class);
    pt.add(Long.class);
    pt.add(Integer.class);
    pt.add(Boolean.class);
    pt.add(Character.class);
    pt.add(Short.class);
    pt.add(Double.class);
    pt.add(Float.class);
    PRIMITIVE_TYPES = pt;
  }
  // -----------------------------------------------------------------------------
  // Cardinality
  // -----------------------------------------------------------------------------

  /**
   * Flag indicating that the descriptor field represents a 1:1 relationship.
   */
  public static final int ONE_TO_ONE = 1;

  /**
   * Flag indicating that the descriptor field represents a 1:M relationship.
   */

  public static final int ONE_TO_MANY = 2;
  /**
   * Flag indicating that the descriptor field represents a M:M relationship.
   */
  public static final int MANY_TO_MANY = 3;
  
  protected ObjectRelationalMapping mapping;
  protected ClassDescriptor cdesc;

  protected String name;
  protected int cardinality;
  protected Class value_class;
  protected boolean required;
  protected boolean readonly;

  protected String getter;
  protected String setter;
  protected Method getter_method;
  protected Method setter_method;
  
  protected String[] columns; //! = new String[0];
  protected String jointable;
  protected String[] joinkeys; //! = new String[0];
  protected String[] manykeys; //! = new String[0];
  protected Class collection_class;
  
  public FieldDescriptor(String name, ClassDescriptor cdesc) {
    this.name = name;
    this.cdesc = cdesc;
  }

  /**
   * INTERNAL: Gets the class descriptor that the field descriptor
   * belongs to.
   */
  public ClassDescriptor getClassDescriptor() {
    return cdesc;
  }
  
  /**
   * INTERNAL: Returns the name of the descriptor field (the mapped
   * field).
   */
  public String getName() {
    return name;
  }

  /**
   * INTERNAL: Gets the field cardinality.
   */
  public int getCardinality() {
    return cardinality;
  }

  /**
   * INTERNAL: Sets the field cardinality. This can be either
   * ONE_TO_ONE, ONE_TO_MANY or MANY_TO_MANY.
   */
  public void setCardinality(int cardinality) {
    if (cardinality != ONE_TO_ONE &&
        cardinality != ONE_TO_MANY &&
        cardinality != MANY_TO_MANY) {
      throw new IllegalArgumentException("Invalid argument: " + cardinality);
    }
    this.cardinality = cardinality;
  }

  /**
   * INTERNAL: Returns true if the field cardinality is 1:1.
   */
  public boolean isOneToOne() {
    return (cardinality == ONE_TO_ONE);
  }

  /**
   * INTERNAL: Returns true if the field cardinality is 1:M.
   */
  public boolean isOneToMany() {
    return (cardinality == ONE_TO_MANY);
  }

  /**
   * INTERNAL: Returns true if the field cardinality is M:M.
   */
  public boolean isManyToMany() {
    return (cardinality == MANY_TO_MANY);
  }
  
  /**
   * INTERNAL: Returns true if the field is a collection field (a
   * cardinality of 1:1 or 1:M).
   */
  public boolean isCollectionField() {
    if (cardinality == ONE_TO_ONE) {
      return false;
    } else {
      return true;
    }
  }

  /**
   * INTERNAL: Returns true if the field is a required field.
   */
  public boolean isRequired() {
    return required;
  }

  /**
   * INTERNAL: Sets whether the field is a required field or not.
   */
  public void setRequired(boolean required) {
    this.required = required;
  }

  /**
   * INTERNAL: Returns true if this field is read-only field.
   */
  public boolean isReadOnly() {
    return readonly;
  }

  /**
   * INTERNAL: Sets whether the field is a read-only field or not.
   */
  public void setReadOnly(boolean readonly) {
    this.readonly = readonly;
  }
  
  /**
   * INTERNAL: Gets the class descriptor of the field value
   * class. Note that primitive value classes don't have a class info.
   */
  public ClassDescriptor getValueClassDescriptor() {
    return getClassDescriptor().getMapping().getDescriptorByClass(getValueClass());
  }

  /**
   * INTERNAL: Gets the field value class. For primitive fields the
   * primitive wrapper class is returned.
   */
  public Class getValueClass() {
    return value_class;
  }
  
  /**
   * INTERNAL: Sets the field value class. For primitive fields the
   * primitive wrapper class should be used.
   */
  public void setValueClass(Class value_class) {
    this.value_class = value_class;
  }
  
  /**
   * INTERNAL: Returns true if the field is an identity field.
   */
  public boolean isIdentityField() {
    FieldDescriptor[] fdescs = getClassDescriptor().getIdentityFields();
    for (int i=0; i < fdescs.length; i++) {
      if (fdescs[i] == this) {
        return true;
      }
    }
    return false;
  }
  
  //! /**
  //!  * INTERNAL: Returns true if the field is a value field.
  //!  */
  //! public boolean isValueField() {
  //!   return !isIdentityField();
  //! }
  
  /**
   * INTERNAL: Returns true if the field is a primitive field.
   */
  public boolean isPrimitiveField() {
    return PRIMITIVE_TYPES.contains(value_class);
  }
  
  /**
   * INTERNAL: Returns true if the field is a reference field.
   */
  public boolean isReferenceField() {
    return !(isPrimitiveField() || isAggregateField());
  }

  /**
   * INTERNAL: Returns true if the field is an aggregate field.
   */
  public boolean isAggregateField() {
    ClassDescriptor cdesc = getValueClassDescriptor();
    if (cdesc != null && cdesc.isAggregate()) {
      return true;
    } else {
      return false;
    }
  }
  
  /**
   * INTERNAL: Returns the method name for getting the field value
   * from class instances.
   */
  public String getGetter() {
    return getter;
  }

  /**
   * INTERNAL: Sets the method name for getting the field value from
   * class instances.
   */
  public void setGetter(String getter) {
    this.getter = getter;
  }

  /**
   * INTERNAL: Returns the method name for setting the field value of
   * class instances.
   */
  public String getSetter() {
    return setter;
  }

  /**
   * INTERNAL: Sets the method name for setting the field value of
   * class instances.
   */
  public void setSetter(String setter) {
    this.setter = setter;
  }
  
  /**
   * INTERNAL: Returns the method for getting the field value from
   * class instances.
   */
  public Method getGetterMethod() {
    if (getter_method == null)
      try {
        getter_method = FieldUtils.getGetterMethod(this);
      } catch (Exception e) {
        throw new OntopiaRuntimeException(e);
      }
    return getter_method;
  }

  /**
   * INTERNAL: Sets the method for getting the field value from class
   * instances.
   */
  public void setGetterMethod(Method getter_method) {
    this.getter_method = getter_method;
  }

  /**
   * INTERNAL: Returns the method for setting the field value of class
   * instances.
   */
  public Method getSetterMethod() {
    if (setter_method == null)
      try {
        setter_method = FieldUtils.getSetterMethod(this);
      } catch (Exception e) {
        throw new OntopiaRuntimeException(e);
      }
    return setter_method;
  }

  /**
   * INTERNAL: Sets the method for setting the field value of class
   * instances.
   */
  public void setSetterMethod(Method setter_method) {
    this.setter_method = setter_method;
  }

  /**
   * INTERNAL: Returns the name of the table in which the field is
   * stored.
   */
  public String getTable() {
    if (jointable != null) {
      return jointable;
    } else {
      return getClassDescriptor().getMasterTable();
    }
  }

  /**
   * INTERNAL: Returns true if the field is stored in a different
   * table from the master table.
   */
  public boolean isExternal() {
    // Not external in the case where the join table is the same as
    // the class descriptor's master table.
    return (jointable != null &&
            !(jointable.equals(getClassDescriptor().getMasterTable())));
  }
  
  /**
   * INTERNAL: Returns the names of the columns referenced by the field.
   */
  public String[] getColumns() {
    return columns;
  }

  /**
   * INTERNAL: Sets the names of the columns referenced by the field.
   */
  public void setColumns(String[] columns) {
    this.columns = columns;
  }
  
  /**
   * INTERNAL: Gets the name of the table which needs to be joined to
   * order to access the field via the master table.
   */
  public String getJoinTable() {
    return jointable;
  }

  /**
   * INTERNAL: Sets the name of the table which needs to be joined to
   * order to access the field via the master table.
   */
  public void setJoinTable(String jointable) {
    this.jointable = jointable;
  }

  /**
   * INTERNAL: Gets the columns that contains the keys referencing the
   * master table.
   */
  public String[] getJoinKeys() {
    return joinkeys;
  }

  /**
   * INTERNAL: Sets the columns that contains the keys referencing the
   * master table.
   */
  public void setJoinKeys(String[] joinkeys) {
    this.joinkeys = joinkeys;
  }

  /**
   * INTERNAL: Gets the columns that contains the keys in the
   * jointable that references the field table.
   */
  public String[] getManyKeys() {
    return manykeys;
  }

  /**
   * INTERNAL: Sets the columns that contains the keys in the
   * jointable that references the field table.
   */
  public void setManyKeys(String[] manykeys) {
    this.manykeys = manykeys;
  }

  /**
   * INTERNAL: Returns the collection class to store the field values
   * in if the field is a 1:M and M:M field.
   */
  public Class getCollectionClass() {
    return collection_class;
  }

  /**
   * INTERNAL: Sets the collection class to store the field values in
   * if the field is a 1:M and M:M field.
   */
  public void setCollectionClass(Class collection_class) {
    this.collection_class = collection_class;
  }

  @Override
  public String toString() {
    return "<FieldDescriptor " + getName() + ">";
  }
  
}
