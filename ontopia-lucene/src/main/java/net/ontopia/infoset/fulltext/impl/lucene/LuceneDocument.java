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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import net.ontopia.infoset.fulltext.core.DocumentIF;
import net.ontopia.infoset.fulltext.core.FieldIF;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
  
/**
 * INTERNAL: DocumentIF wrapper for Lucene's own internal document class.<p>
 */

public class LuceneDocument implements DocumentIF {

  protected Document document;

  LuceneDocument(Document document) {
    this.document = document;
  }
  
  @Override
  public FieldIF getField(String name) {
    IndexableField field = document.getField(name);
    if (field == null) {
      return null;
    }
    return new LuceneField(field);
  }
  
  @Override
  public Collection<FieldIF> getFields() {
    Collection<FieldIF> result = new ArrayList<FieldIF>();
    for (IndexableField field : document.getFields()) {
      result.add(new LuceneField(field));
    }
    return result;
  }

  @Override
  public void addField(FieldIF field) {
    throw new UnsupportedOperationException("Cannot modify wrapped document object.");
  }

  @Override
  public void removeField(FieldIF field) {
    throw new UnsupportedOperationException("Cannot modify wrapped document object.");
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("<lucene.Document ");
    Iterator<FieldIF> iter = getFields().iterator();
    while (iter.hasNext()) {
      FieldIF field = iter.next();
      sb.append(field.toString());
    }
    sb.append('>');
    return sb.toString();
  }
}
