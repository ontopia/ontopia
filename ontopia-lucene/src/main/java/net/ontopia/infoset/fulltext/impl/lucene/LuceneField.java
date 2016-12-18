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

package net.ontopia.infoset.fulltext.impl.lucene;

import java.io.Reader;
import net.ontopia.infoset.fulltext.core.FieldIF;
import org.apache.lucene.document.Field;
  
/**
 * INTERNAL: FieldIF wrapper for Lucene's own internal field class.<p>
 */

public class LuceneField implements FieldIF {

  protected Field field;
  
  LuceneField(Field field) {
    this.field = field;
  }
  
  public String getName() {
    return field.name();
  }
  
  public String getValue() {
    return field.stringValue();
  }

  public Reader getReader() {
    return field.readerValue();
  }
  
  public boolean isStored() {
    return field.isStored();
  }

  public boolean isIndexed() {
    return field.isIndexed();
  }

  public boolean isTokenized() {
    return field.isTokenized();
  }

  @Override
  public String toString() {
    if (getReader() == null)
      return getName() + "=" + getValue() + " ";
    else
      return getName() + "=" + getReader() + " ";
  }
}
