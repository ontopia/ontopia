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

package net.ontopia.topicmaps.impl.basic;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.impl.utils.AbstractSubjectIdentityCache;
import net.ontopia.topicmaps.impl.utils.TopicMapTransactionIF;
import net.ontopia.utils.CollectionFactoryIF;

/**
 * INTERNAL: Class that maintains indexes for use with the TopicMapIF
 * locator lookup methods. This is especially useful in the cases
 * where the topic map object cannot use queries to do the
 * lookups.</p>
 *
 * This class uses the event model to maintain its indexes.</p>
 */
public class SubjectIdentityCache extends AbstractSubjectIdentityCache
  implements java.io.Serializable {
  private static final long serialVersionUID = -2503061122276743151L;

  protected TopicMapTransactionIF txn;  
  protected CollectionFactoryIF cfactory;

  protected long counter;

  protected Map<String, TMObject> id_objects;
  protected Map<LocatorIF, TopicIF> subjects;
  protected Map<LocatorIF, TopicIF> subject_indicators;
  protected Map<LocatorIF, TMObjectIF> source_locators;
  
  public SubjectIdentityCache(TopicMapTransactionIF txn, CollectionFactoryIF cfactory) {
    super(null);
    this.handlers = cfactory.makeLargeMap();
    this.txn = txn;
    this.cfactory = cfactory;
    
    // initialize state
    refresh();
  }

  public void refresh() {
    // Initialize maps
    counter = 0;

    // Note: Cannot drop entries since we're not able to reregister
    // them without traversing the entire topic map. So we should
    // always keep them around.
    id_objects = cfactory.makeLargeMap();
    subjects = cfactory.makeLargeMap();
    subject_indicators = cfactory.makeLargeMap();
    source_locators = cfactory.makeLargeMap();
  }

  // --------------------------------------------------------------------------
  // TopicMapIF locator lookup methods
  // --------------------------------------------------------------------------
  
  @Override
  public TMObjectIF getObjectById(String object_id) {
    TMObject o = id_objects.get(object_id);
    if (o == null || o.parent == null) {
      return null;
    } else {
      return o;
    }
  }
  
  @Override
  public TMObjectIF getObjectByItemIdentifier(LocatorIF locator) {
    return source_locators.get(locator);
  }
  
  @Override
  public TopicIF getTopicBySubjectLocator(LocatorIF locator) {
    return subjects.get(locator);
  }
  
  @Override
  public TopicIF getTopicBySubjectIdentifier(LocatorIF locator) {
    return subject_indicators.get(locator);    
  }

  // --------------------------------------------------------------------------
  // IdentifierIndexIF lookup methods
  // --------------------------------------------------------------------------

  public Collection<LocatorIF> getItemIdentifiers() {
    return source_locators.keySet();
  }

  public Collection<LocatorIF> getSubjectIdentifiers() {
    return subject_indicators.keySet();
  }

  // --------------------------------------------------------------------------
  // Event handler methods
  // --------------------------------------------------------------------------

  @Override
  protected TMObjectIF _getObjectByItemIdentifier(LocatorIF source_locator) {
    return source_locators.get(source_locator);
  }

  @Override
  protected void registerSourceLocator(LocatorIF source_locator, TMObjectIF object) {
    source_locators.put(source_locator, object);
  }
  
  @Override
  protected void unregisterSourceLocator(LocatorIF source_locator) {
    source_locators.remove(source_locator);
  }

  @Override
  protected TopicIF _getTopicBySubjectIdentifier(LocatorIF subject_indicator) {
    return subject_indicators.get(subject_indicator);
  }

  @Override
  protected void registerSubjectIndicator(LocatorIF subject_indicator, TopicIF object) {
    subject_indicators.put(subject_indicator, object);
  }
  
  @Override
  protected void unregisterSubjectIndicator(LocatorIF subject_indicator) {
    subject_indicators.remove(subject_indicator);
  }

  @Override
  protected TopicIF _getTopicBySubjectLocator(LocatorIF subject) {
    return subjects.get(subject);
  }

  @Override
  protected void registerSubject(LocatorIF subject, TopicIF object) {
    subjects.put(subject, object);
  }

  @Override
  protected void unregisterSubject(LocatorIF subject) {
    subjects.remove(subject);
  }

  // --------------------------------------------------------------------------
  // Object registration methods
  // --------------------------------------------------------------------------

  /**
   * INTERNAL: Register the object with the identity map.
   */
  @Override
  protected void registerObject(TMObjectIF o) {
    // Add object and its id from the identity maps.
    Objects.requireNonNull(o, "Cannot register a null object with the identity map.");
    // Add new map entries
    TMObject object = (TMObject)o;
    // Create new id if not already created
    if (object.oid == null) {
      object.oid = ("" + counter++);
    }
    if (!id_objects.containsKey(object.oid)) {
      id_objects.put(object.oid, object);
    }
  }

  /**
   * INTERNAL: Unregister the object with the identity map.
   */
  @Override
  protected void unregisterObject(TMObjectIF o) {
    // Clear object and its id from the identity maps.
    Objects.requireNonNull(o, "Cannot unregister a null object with the identity map.");

    // Remove map entries
    id_objects.remove(((TMObject)o).oid);
  }
}
