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

import java.util.Collection;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.core.ValueAcceptingTagIF;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;

/**
 * INTERNAL: Value Producing Tag for finding all topics that define classes,
 * either in general, or of a specific kind of object.
 */
public class ClassesTag extends TagSupport { // implements ValueProducingTagIF

  // constants
  public static final String TYPE_TOPIC = "topic";
  public static final String TYPE_ASSOC = "association";
  public static final String TYPE_ROLE  = "role";
  public static final String TYPE_OCC   = "occurrence";
  
  // tag attributes
  private String typeName;

  /**
   * Process the start tag for this instance.
   */
  @Override
  public int doStartTag() throws JspTagException {

    // retrieve parent tag which accepts the result of this value producing operation
    ValueAcceptingTagIF acceptingTag = (ValueAcceptingTagIF) findAncestorWithClass(this, ValueAcceptingTagIF.class);

    // try to retrieve default value from ContextManager
    ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);

    // get topicmap object on which we should compute 
    TopicMapIF topicmap = contextTag.getTopicMap();
    if (topicmap == null) {
      throw new NavigatorRuntimeException("ClassesTag found no topic map.");
    }
    
    // get class instance index
    ClassInstanceIndexIF index = (ClassInstanceIndexIF) topicmap.getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");

    // find all topics used to define classes of objects of the type named.
    Collection resColl = null;
    if (typeName == null) {
      resColl = new java.util.HashSet();
      resColl.addAll(index.getTopicTypes());
      resColl.addAll(index.getAssociationTypes());
      resColl.addAll(index.getAssociationRoleTypes());
      resColl.addAll(index.getOccurrenceTypes());
    } else if (typeName.equals(TYPE_TOPIC)) {
      resColl = index.getTopicTypes();
    } else if (typeName.equals(TYPE_ASSOC)) {
      resColl = index.getAssociationTypes();
    } else if (typeName.equals(TYPE_ROLE)) {
      resColl = index.getAssociationRoleTypes();
    } else if (typeName.equals(TYPE_OCC)) {
      resColl = index.getOccurrenceTypes();
    }
    
    // kick it over to the accepting tag
    acceptingTag.accept(resColl);
    
    return SKIP_BODY;
  }

  // -----------------------------------------------------------------
  // set methods for tag attributes
  // -----------------------------------------------------------------

  /**
   * Sets the class-type for topics that should looked up.
   *
   * @param typeName String which should contain one of the following
   *        values: topic | association | occurrence | role
   */
  public void setOf(String typeName) {
    if (typeName.equals(TYPE_TOPIC)
        || typeName.equals(TYPE_ASSOC)
        || typeName.equals(TYPE_ROLE)
        || typeName.equals(TYPE_OCC)) {
        this.typeName = typeName;
    } else {
      throw new IllegalArgumentException("Invalid type name <" + typeName + "> in attribute 'of' " +
                                         " of element 'classes'.");
    }
  }

}





