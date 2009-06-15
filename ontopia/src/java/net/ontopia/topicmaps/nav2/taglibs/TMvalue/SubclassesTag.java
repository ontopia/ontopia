// $Id: SubclassesTag.java,v 1.11 2003/02/04 10:59:29 grove Exp $

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

import org.apache.log4j.Logger;

/**
 * INTERNAL: Value Producing Tag for finding all the subclasses
 * of the topics in a collection.
 */
public class SubclassesTag extends BaseValueProducingAndAcceptingTag {

  // initialization of logging facility
  private static Logger log = Logger
    .getLogger(SubclassesTag.class.getName());

  private final static TypeHierarchyUtils hierUtils = new TypeHierarchyUtils();
  
  // tag attributes
  private Integer levelNumber = null;
  
  public Collection process(Collection topics) throws JspTagException {
    // find all subclasses of all topics in collection
    if (topics == null || topics.isEmpty())
      return Collections.EMPTY_SET;
    else {
      HashSet subclasses = new HashSet();
      Iterator iter = topics.iterator();
      TopicIF topic = null;
      Object obj = null;
      while (iter.hasNext()) {
        obj = iter.next();
        try {
          topic = (TopicIF) obj;
        } catch (ClassCastException e) {
          throw new NavigatorRuntimeException("SubclassesTag expected to get a input collection of " +
            "topic instances, but got instance of class " + obj.getClass().getName());
        }

        // the topic casting succeeded, now find the subclasses
        if (levelNumber == null)
          subclasses.addAll( hierUtils.getSubclasses(topic) );
        else
          subclasses.addAll( hierUtils.getSubclasses(topic,
                             levelNumber.intValue()));
        
      } // while
      return subclasses;
    }
  }


  // -----------------------------------------------------------------
  // set methods for additional tag attributes
  // -----------------------------------------------------------------

  /**
   * Set maximum number of levels to traverse downwards.
   *
   * @param levelString try to convert to valid integer,
   * otherwise fallback to default value (null).
   */
  public void setLevel(String levelString) {
    try {
      levelNumber = new Integer(levelString);
    } catch (NumberFormatException e) {
      log.warn("Reset invalid level value to null; was '" + levelString + "'.");
      levelNumber = null;
    }

    if (levelNumber.intValue() < 1)
      throw new OntopiaRuntimeException("Only positive levels are allowed.");
  }
  
}
