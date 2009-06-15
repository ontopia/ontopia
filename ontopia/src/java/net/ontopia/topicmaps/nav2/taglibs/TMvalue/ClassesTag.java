// $Id: ClassesTag.java,v 1.16 2008/06/11 16:56:00 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.taglibs.TMvalue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.topicmaps.core.*;
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
  public int doStartTag() throws JspTagException {

    // retrieve parent tag which accepts the result of this value producing operation
    ValueAcceptingTagIF acceptingTag = (ValueAcceptingTagIF) findAncestorWithClass(this, ValueAcceptingTagIF.class);

    // try to retrieve default value from ContextManager
    ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);

    // get topicmap object on which we should compute 
    TopicMapIF topicmap = contextTag.getTopicMap();
    if (topicmap == null)
      throw new NavigatorRuntimeException("ClassesTag found no topic map.");
    
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
    } else if (typeName.equals(TYPE_TOPIC))
      resColl = index.getTopicTypes();
    else if (typeName.equals(TYPE_ASSOC))
      resColl = index.getAssociationTypes();
    else if (typeName.equals(TYPE_ROLE))
      resColl = index.getAssociationRoleTypes();
    else if (typeName.equals(TYPE_OCC))
      resColl = index.getOccurrenceTypes();
    
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
        || typeName.equals(TYPE_OCC))
        this.typeName = typeName;
    else
      throw new IllegalArgumentException("Invalid type name <" + typeName + "> in attribute 'of' " +
                                         " of element 'classes'.");
  }

}





