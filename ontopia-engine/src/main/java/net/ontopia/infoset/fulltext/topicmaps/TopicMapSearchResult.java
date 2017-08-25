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

package net.ontopia.infoset.fulltext.topicmaps;

import java.io.IOException;
import java.util.AbstractList;

import net.ontopia.infoset.fulltext.core.DocumentIF;
import net.ontopia.infoset.fulltext.core.FieldIF;
import net.ontopia.infoset.fulltext.core.SearchResultIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: A List implementation that wraps a SearchResultIF to
 * present the actual topic map objects referenced in the search
 * result. The 'object_id' document field is used by default to look
 * up topic map objects by their object ids. This field can be changed
 * by setting the objectIdField property.<p>
 *
 * <b>Example:</b><p>
 *
 * <pre>
 * TopicMapSearchResult tmobjects = new TopicMapSearchResult(topicmap, searcher.search("verdi"));
 * Iterator iter = tmobjects.iterator();
 * while (iter.hasNext()) {
 *   System.out.println(iter.next());
 * }
 * </pre>
 *
 * <b>Output:</b><p>
 *
 * <pre>
 * [basic.TopicName, 790, 'Verdi, Giuseppe']
 * [basic.TopicName, 791, 'Giuseppe Verdi']
 * [basic.TopicName, 705, 'Land of Verdi']]
 * </pre>
 */

public class TopicMapSearchResult extends AbstractList<TMObjectIF> {

  protected TopicMapIF topicmap;
  protected SearchResultIF result;
  protected String id_field = "object_id";
  
  public TopicMapSearchResult(TopicMapIF topicmap, SearchResultIF result) {
    this.topicmap = topicmap;
    this.result = result;    
  }

  /**
   * INTERNAL: Returns the name of the field that contains the topic
   * map object id.
   */
  public String getObjectIdField() {
    return id_field;
  }

  /**
   * INTERNAL: Sets the name of the field that contains the topic map
   * object id.
   */
  public void setObjectIdField(String id_field) {
    this.id_field = id_field;
  }

  /**
   * INTERNAL: The score of the result row.
   */
  public float getScore(int index) {
    try {
      return result.getScore(index);
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  /**
   * INTERNAL: This is the java.util.List.get(int) method.
   */
  @Override
  public TMObjectIF get(int index) {
    try {
      // Get document and extract object id
      DocumentIF document = result.getDocument(index);
      FieldIF field = document.getField(id_field);
      
      // Lookup object in topic map and return it.
      return topicmap.getObjectById(field.getValue());
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  /**
   * INTERNAL: This is the java.util.List.size() method.
   */
  @Override
  public int size() {
    try {
      return result.hits();
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
}
