// $Id: DifferenceTag.java,v 1.8 2003/02/04 10:59:29 grove Exp $

package net.ontopia.topicmaps.nav2.taglibs.value;

import java.util.HashSet;
import java.util.Collection;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.topicmaps.nav2.core.ValueProducingTagIF;
import net.ontopia.topicmaps.nav2.core.ValueAcceptingTagIF;

/**
 * INTERNAL: Value Producing Tag for computing
 * the set difference of two collections.
 */
public class DifferenceTag extends TagSupport implements ValueAcceptingTagIF {

  // members
  private Collection resultCollection;

  /**
   * Process the start tag for this instance.
   */
  public int doStartTag() {
    // evaluate body
    return EVAL_BODY_INCLUDE;
  }

  /**
   * Process the end tag.
   */
  public int doEndTag() {
    // retrieve parent tag which accepts the produced collection by this tag 
    ValueAcceptingTagIF acceptingTag = (ValueAcceptingTagIF)
      findAncestorWithClass(this, ValueAcceptingTagIF.class);

    // kick it over to the accepting tag
    acceptingTag.accept( resultCollection );

    // reset member collection
    resultCollection = null;
    
    return EVAL_PAGE;
  }

  /**
   * reset the state of the Tag.
   */
  public void release() {
    // overwrite default behaviour
    // do not set parent to null!!!
  }

  // -----------------------------------------------------------------
  // Implementation of ValueAcceptingTagIF
  // -----------------------------------------------------------------

  public void accept(Collection inputCollection) {
    if (resultCollection == null) {
      // setup first collection
      resultCollection = new HashSet(inputCollection);
    } else {
      // calculate difference between first and second collection
      resultCollection.removeAll(inputCollection);
    }
  }

}





