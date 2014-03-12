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

package net.ontopia.topicmaps.query.spi;

import java.util.Map;
import net.ontopia.topicmaps.core.TopicMapIF;

/**
 * PUBLIC: Simple searcher interface that one can implement and
 * refer to by name through a java module import declaration.<p>
 */
public interface SearcherIF {

  /**
   * PUBLIC: Value type indicating that the result value should be
   * represented as a string.
   */
  public static final int STRING_VALUE = 1;

  /**
   * PUBLIC: Value type indicating that the result value should be
   * represented as-is (as an object).
   */
  public static final int OBJECT_VALUE = 2;

  /**
   * PUBLIC: Value type indicating that the result value is an object
   * id and should be used to look up the corresponding topic map
   * object.
   */
  public static final int OBJECT_ID = 4;

  /**
   * PUBLIC: Value type indicating that the result value is a subject
   * locator and should be used to look up the topic that has that
   * subject locator.
   */
  public static final int SUBJECT_LOCATOR = 8;

  /**
   * PUBLIC: Value type indicating that the result value is a subject
   * identifier and should be used to look up the topic that has that
   * subject identifier.
   */
  public static final int SUBJECT_IDENTIFIER = 16;

  /**
   * PUBLIC: Value type indicating that the result value is an item
   * identifier and should be used to look up the corresponding topic
   * map object.
   */
  public static final int ITEM_IDENTIFIER = 32;
  
  /**
   * PUBLIC: Value type indicating that the result value is an
   * external occurrence value identifier and should be used to look
   * up the corresponding occurrence objects.
   */
  public static final int OCCURRENCE_URI = 64;
  
  /**
   * PUBLIC: Returns type of values returned by the search result. See
   * constants declared in this class.
   */
  public int getValueType();

  /**
   * PUBLIC: Returns the String value of the field. Note that null is
   * returned if the field has a reader set.
   */
  public SearchResultIF getResult(String query);
  
  /**
   * PUBLIC: Called by the query engine before using the instance to
   * pass the module URI to the searcher. No specific behaviour is
   * required from the searcher.
   */
  public void setModuleURI(String moduleURI);
  
  /**
   * PUBLIC: Called by the query engine before using the instance to
   * pass the name of the predicate (the part after the colon in the
   * QName) to the searcher. No specific behaviour is required from
   * the searcher.
   */
  public void setPredicateName(String predicateName);

  /**
   * PUBLIC: Called by the query engine before using the instance to
   * pass the topic map being queried to the predicate. No specific
   * behaviour is required from the searcher.
   */
  public void setTopicMap(TopicMapIF topicmap);
  
  /**
   * PUBLIC: Called by the query engine before using the instance to
   * pass the parameters in the module URI to the searcher. The map
   * will contain {"foo" : "bar"} if the URI ends in "?foo=bar". No
   * specific behaviour is required from the searcher.
   */
  public void setParameters(Map parameters);
  
}
