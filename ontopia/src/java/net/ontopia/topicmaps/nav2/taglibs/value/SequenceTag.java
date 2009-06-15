
// $Id: SequenceTag.java,v 1.1 2003/08/17 13:18:29 larsga Exp $

package net.ontopia.topicmaps.nav2.taglibs.value;

import java.util.ArrayList;
import java.util.Collection;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.topicmaps.nav2.core.ValueProducingTagIF;
import net.ontopia.topicmaps.nav2.core.ValueAcceptingTagIF;

/**
 * INTERNAL: Tag which gathers all the values it receives into a
 * sequence. All the collections received are flattened into a single
 * sequence.
 */
public class SequenceTag extends TagSupport implements ValueAcceptingTagIF {
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
    // retrieve parent tag which accepts the collection produced by this tag 
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
    if (resultCollection == null)
      resultCollection = new ArrayList();

    // we accept duplicates, and the list will ensure that we respect order
    resultCollection.addAll( inputCollection );
  }
}
