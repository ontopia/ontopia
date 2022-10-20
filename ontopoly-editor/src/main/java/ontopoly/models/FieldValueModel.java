/*
 * #!
 * Ontopoly Editor
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
package ontopoly.models;


import java.util.Objects;
import net.ontopia.topicmaps.core.TMObjectIF;

import ontopoly.model.RoleField;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

public class FieldValueModel extends LoadableDetachableModel<Object> {

  private FieldInstanceModel fieldInstanceModel;
  private Object value;
  private boolean isExistingValue;
  
  private FieldValueModel(FieldInstanceModel fieldInstanceModel, Object value, boolean isExistingValue) {
    super(value);
    Objects.requireNonNull(fieldInstanceModel, "fieldInstanceModel parameter cannot be null.");
    this.fieldInstanceModel = fieldInstanceModel;

    setValueInternal(value);
    this.isExistingValue = isExistingValue;
  }
  
  private void setValueInternal(Object value) {
    // turn non-serializable objects into model objects
    if (value instanceof RoleField.ValueIF) {
      this.value = new AssociationFieldValueModel((RoleField.ValueIF)value);
    } else if (value instanceof TMObjectIF) {
      this.value = new TMObjectModel(fieldInstanceModel.getFieldInstance().getInstance().getTopicMap().getId(), (TMObjectIF)value);
    } else {
      this.value = value;
    }
  }    
  
  public static FieldValueModel createModel(FieldInstanceModel fieldInstanceModel, Object value, boolean isExistingValue) {
    return new FieldValueModel(fieldInstanceModel, value, isExistingValue);  
  }
  
  public FieldInstanceModel getFieldInstanceModel() {
    return fieldInstanceModel;
  }

  public Object getFieldValue() {
    return getObject();
  }
  
  public boolean isExistingValue() {
    return isExistingValue;
  }
  
  public void setExistingValue(Object value) {
    setValueInternal(value);
    this.isExistingValue = true;
    detach();
  }
  
  @Override
  public Object load() {
    if (value instanceof IModel) {
      return ((IModel)value).getObject();
    } else {
      return value;
    }
  }
  
  @Override
  public void detach() {
    if (value instanceof IModel) {
      ((IModel)value).detach(); 
    }
    super.detach();
  }
  
}
