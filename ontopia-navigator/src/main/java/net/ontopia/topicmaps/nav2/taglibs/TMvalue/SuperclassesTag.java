/*
 * #!
 * Ontopia Navigator
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

package net.ontopia.topicmaps.nav2.taglibs.TMvalue;

import java.util.HashSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import javax.servlet.jsp.JspTagException;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.utils.TypeHierarchyUtils;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.taglibs.value.BaseValueProducingAndAcceptingTag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Value Producing Tag for finding all the superclasses
 * of the topics in a collection.
 */
public class SuperclassesTag extends BaseValueProducingAndAcceptingTag {

  // initialization of logging facility
  private static final Logger log = LoggerFactory
    .getLogger(SuperclassesTag.class.getName());

  private static TypeHierarchyUtils hierUtils = new TypeHierarchyUtils();

  // tag attributes
  private Integer levelNumber = null;
  
  @Override
  public Collection process(Collection topics) throws JspTagException {
    // find all superclasses of all topics in collection
    if (topics == null || topics.isEmpty()) {
      return Collections.EMPTY_SET;
    } else {
      HashSet superclasses = new HashSet();
      Iterator iter = topics.iterator();
      TopicIF topic = null;
      Object obj = null;

      while (iter.hasNext()) {
        obj = iter.next();
        try {
          topic = (TopicIF)obj;
        } catch (ClassCastException e) {
          String msg = "SubclassesTag expected to get a input collection of " +
            "topic instances, " +
            "but got instance of class " + obj.getClass().getName();
          throw new NavigatorRuntimeException(msg);
        }

        // ok, the topic cast succeeded, now continue
        if (levelNumber == null) {
          // if no level is specified get all of them.
          superclasses.addAll(hierUtils.getSuperclasses(topic));
        } else { 
          superclasses.addAll(hierUtils.getSuperclasses(topic, levelNumber.intValue()));
        }

      } // while
      return superclasses;
    }
  }


  // -----------------------------------------------------------------
  // set methods for additional tag attributes
  // -----------------------------------------------------------------

  /**
   * Set maximum number of levels to traverse upwards.
   *
   * @param levelString try to convert to valid integer,
   * otherwise fallback to default value (null).
   */
  public void setLevel(String levelString) {
    try {
      levelNumber = Integer.valueOf(levelString);        
    } catch (NumberFormatException e) {
      log.warn("Reset invalid level value to null; was '" + levelString + "'.");
      levelNumber = null;
    }

    if (levelNumber.intValue() < 1) {
      throw new OntopiaRuntimeException("Only positive levels are allowed.");
    }
  }
  
}





