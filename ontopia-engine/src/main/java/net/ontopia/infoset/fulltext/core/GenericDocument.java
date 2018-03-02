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

package net.ontopia.infoset.fulltext.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
  
/**
 * INTERNAL: The default document implementation. This class contains a
 * single straightforward implementation of the DocumentIF
 * interfaces. The class uses a map internally to hold its FieldIF
 * elements.<p>
 */

public class GenericDocument implements DocumentIF, java.io.Serializable {

  protected Map<String, FieldIF> fields;

  public GenericDocument() {
    this.fields = new HashMap<String, FieldIF>();
  }
  
  @Override
  public FieldIF getField(String name) {
    return (FieldIF)fields.get(name);
  }
  
  @Override
  public Collection<FieldIF> getFields() {
    return fields.values();
  }

  @Override
  public void addField(FieldIF field) {
    fields.put(field.getName(), field);
  }

  @Override
  public void removeField(FieldIF field) {
    fields.remove(field.getName());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("<Document");
    Iterator<FieldIF> iter = getFields().iterator();
    while (iter.hasNext()) {
      FieldIF field = iter.next();
      sb.append(" " + field.getName() + "='" + field.getValue() + "'");
    }
    sb.append('>');
    return sb.toString();
  }
  
}
