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

package net.ontopia.topicmaps.utils;

import java.util.Iterator;
import java.util.function.Predicate;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TypedIF;

/**
 * INTERNAL: Decider that decides whether the object is an instance of a
 * topic with the given subject identifier.</p>
 *
 * The decider returns true when the object has the given subject
 * identifier, or it is an instance of a topic with the given subject
 * identifier.</p>
 */

public class SubjectIdentityDecider<T extends TMObjectIF> implements Predicate<T> {
  /**
   * PROTECTED: the given subject identifier.
   */ 
  protected LocatorIF subject_identifier;

  /**
   * INTERNAL: Creates a decider which uses the given subject identifier.
   *
   * @param subject_identifier locatorIF which is the given subject identifier
   */ 
  
  public SubjectIdentityDecider(LocatorIF subject_identifier) {
    this.subject_identifier = subject_identifier;
  }

  /**
   * INTERNAL: Decides whether an object (directly or indirectly) has a
   * given subject identifier.
   *
   * @param object an object which must be a TypedIF or TopicIF
   * @return boolean; true iff the given object has the given subject
   * identifier (directly or indirectly)
   */ 

  @Override
  public boolean test(T object) {
    if (object instanceof TopicIF) {
      TopicIF topic = (TopicIF) object;
      if (topic.getSubjectIdentifiers().contains(subject_identifier)) {
        return true;
      }
    }
      
    if (object instanceof TypedIF) {
      TopicIF topic = ((TypedIF) object).getType();
      if (topic == null) {
        return false;
      }
      return topic.getSubjectIdentifiers().contains(subject_identifier);
      
    } else if (object instanceof TopicIF) {
      Iterator<TopicIF> it = ((TopicIF) object).getTypes().iterator();
      while (it.hasNext()) {
        TopicIF topic = it.next();
        if (topic.getSubjectIdentifiers().contains(subject_identifier)) {
          return true;
        }
      }
    } 

    return false;
  }

}
