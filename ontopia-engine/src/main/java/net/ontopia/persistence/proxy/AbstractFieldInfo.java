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
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: An abstract field info class containing the common
 * behaviour for FieldInfoIFs.
 */

public abstract class AbstractFieldInfo implements FieldInfoIF {

  private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
  
  protected String name;
  protected int index;
  protected int cardinality;
  protected boolean readonly;
  protected boolean is_collection;
  protected ClassInfoIF parent_cinfo;
  protected Class<?> value_class;
  protected FieldDescriptor field;
  protected Method getter;
  protected Method setter;

  AbstractFieldInfo(ClassInfoIF parent_cinfo, FieldDescriptor field, int index) {
    // WARN: cardinality flags are at this time the same as those in
    // FieldDescriptor.
    this(parent_cinfo, field.getName(), index, field.getValueClass(),
         field.isCollectionField(), field.getCardinality(), field.isReadOnly());
    this.field = field;
  }

  protected AbstractFieldInfo(ClassInfoIF parent_cinfo, String name,
                              int index, Class<?> value_class,
                              boolean is_collection, int cardinality, boolean readonly) {
    // WARN: These properties are not enough to support all methods,
    // since some of the methods still rely on the field instance.
    this.parent_cinfo = parent_cinfo;
    this.name = name;
    this.index = index;
    this.value_class = value_class;
    this.is_collection = is_collection;
    this.cardinality = cardinality;
    this.readonly = readonly;
  }
  
  @Override
  public String getName() {
    return name;
  }

  @Override
  public int getIndex() {
    return index;
  }

  @Override
  public boolean isReadOnly() {
    return readonly;
  }
  
  @Override
  public int getCardinality() {
    return cardinality;
  }

  @Override
  public boolean isCollectionField() {
    return is_collection;
  }

  @Override
  public boolean isPrimitiveField() {
    return field.isPrimitiveField();
  }
  
  @Override
  public boolean isReferenceField() {
    return field.isReferenceField();
  }

  @Override
  public boolean isAggregateField() {
    return field.isAggregateField();
  }
  
  @Override
  public ClassInfoIF getParentClassInfo() {
    return parent_cinfo;
  }

  @Override
  public Class<?> getValueClass() {
    return value_class;
  }

  @Override
  public String getTable() {
    return field.getTable();
  }

  @Override
  public Object getValue(Object object) throws Exception {
    //! System.out.println("=> " + getName() + " " + field.getGetter() + " " + object.getClass() + " " + field.getValueClass());
    return getGetterMethod().invoke(object, EMPTY_OBJECT_ARRAY);
  }

  @Override
  public void setValue(Object object, Object value) throws Exception {
    //! System.out.println("=> " + getName() + " " + field.getSetter() + " " + object.getClass() + " " + field.getValueClass());
    getSetterMethod().invoke(object, new Object[] {value});
  }

  protected Method getGetterMethod() {
    if (getter == null) {
      getter = field.getGetterMethod();
      if (getter == null) {
        throw new OntopiaRuntimeException("Could not find getter method for field " + field.getName());
      }
    }
    return getter;
  }

  protected Method getSetterMethod() {
    if (setter == null) {
      setter = field.getSetterMethod();
      if (setter == null) {
        throw new OntopiaRuntimeException("Could not find setter method for field " + field.getName());
      }
    }
    return setter;
  }

  @Override
  public String getJoinTable() {
    return field.getJoinTable();
  }

  @Override
  public String[] getJoinKeys() {
    return field.getJoinKeys();
  }

  @Override
  public String[] getManyKeys() {
    return field.getManyKeys();
  }

  /// -- Misc

  /**
   * INTERNAL: Returns the underlying FieldDescriptor instance if one
   * exists.
   */
  public FieldDescriptor getDescriptor() {
    return field;
  }
  
}
