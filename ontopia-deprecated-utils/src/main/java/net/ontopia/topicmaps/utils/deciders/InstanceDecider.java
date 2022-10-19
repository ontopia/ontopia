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

package net.ontopia.topicmaps.utils.deciders;

import java.util.Collection;
import java.util.Iterator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TypedIF;
import net.ontopia.utils.DeciderIF;
import net.ontopia.topicmaps.utils.ClassInstanceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Decider that takes either TypedIF or TopicIF and
 * returns ok if it is an instance of one of the types it was
 * constructed with.
 */
@Deprecated
public class InstanceDecider implements DeciderIF<Object> {

  // Define a logging category.
  private static final Logger log = LoggerFactory
    .getLogger(InstanceDecider.class.getName());

  protected Collection<TopicIF> types;

  /**
   * Constructor which takes a Collection of types.
   */
  public InstanceDecider(Collection<TopicIF> types) {
    this.types = types;
  } 

  /**
   * Returns true if the type of a TypedIF object matches and returns
   * true if one of the types of a MultiTyped match .
   */
  @Override
  public boolean ok(Object object) {
    if (object instanceof TypedIF) {
      // TypedIF can only have one type
      // Used for Associations, Roles, Occurrences.
      TypedIF typed = (TypedIF) object;
      if (types!=null && !types.isEmpty()) {
        Iterator<TopicIF> it=types.iterator();
        while(it.hasNext()){
          TopicIF thisTopic = it.next();
          if (ClassInstanceUtils.isInstanceOf(typed, thisTopic)) {
            return true;
          } 
        }
      }   
      return false;
    } else if (object instanceof TopicIF) {
      TopicIF topic = (TopicIF) object;
      if (!types.isEmpty()) {
        Iterator<TopicIF> it = types.iterator();
        while(it.hasNext()) {
          TopicIF thisTopic = it.next();
          if (ClassInstanceUtils.isInstanceOf(topic, thisTopic)) {
            return true;
          } 
        }   
      } 
      return false;      
    } else {
      log.warn("Object not suitable type: " + object.getClass().getName());
      return false;
    }
  }
  
}





