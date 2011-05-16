
// $Id: ValueProducingTagIF.java,v 1.8 2003/09/03 16:22:15 larsga Exp $

package net.ontopia.topicmaps.nav2.core;

import java.util.Collection;
import javax.servlet.jsp.JspException;

/**
 * INTERNAL: Implemented by tags whose functionality is such that they
 * process a single input collection to produce their output value.
 */
public interface ValueProducingTagIF {

  /**
   * INTERNAL: Process the input collection and return the output
   * collection.  This collection will afterwards typically be passed
   * up to the parent tag, which should be a value-accepting tag.
   *
   * @see net.ontopia.topicmaps.nav2.taglibs.value.BaseValueProducingTag
   */
  public Collection process(Collection inputCollection) throws JspException;
  
}
