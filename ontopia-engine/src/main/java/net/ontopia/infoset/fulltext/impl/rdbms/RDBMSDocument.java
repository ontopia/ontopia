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

package net.ontopia.infoset.fulltext.impl.rdbms;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import net.ontopia.infoset.fulltext.core.DocumentIF;
import net.ontopia.infoset.fulltext.core.FieldIF;
  
/**
 * INTERNAL: RDBMS DocumentIF class implementation.<p>
 */

public class RDBMSDocument implements DocumentIF {

  protected Map<String, FieldIF> fields;
  protected float score;
  
  RDBMSDocument(Map<String, FieldIF> fields, float score) {
    this.fields = fields;
    this.score = score;
  }

  public float getScore() {
    return score;
  }
  
  public FieldIF getField(String name) {
    return fields.get(name);
  }
  
  public Collection<FieldIF> getFields() {
    return fields.values();
  }

  public void addField(FieldIF field) {
    throw new UnsupportedOperationException("Cannot modify RDBMS document object.");
  }

  public void removeField(FieldIF field) {
    throw new UnsupportedOperationException("Cannot modify RDBMS document object.");
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("<rdbms.Document ");
    Iterator<FieldIF> iter = getFields().iterator();
    while (iter.hasNext()) {
      FieldIF field = iter.next();
      sb.append(field.toString());
    }
    sb.append(">");
    return sb.toString();
  }
  
}
