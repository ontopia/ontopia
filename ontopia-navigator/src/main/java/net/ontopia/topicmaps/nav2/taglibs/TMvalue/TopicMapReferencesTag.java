
package net.ontopia.topicmaps.nav2.taglibs.TMvalue;

import java.util.Collection;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.core.ValueProducingTagIF;
import net.ontopia.topicmaps.nav2.core.ValueAcceptingTagIF;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;

/**
 * INTERNAL: Value Producing Tag for generating a collection of
 * TopicMapReferenceIF objects containing information about topicmaps
 * available to this application.
 * <p>
 * (Note: this is somewhat special because this Tag does not need to
 *  manipulate an input collection and so it is not implementing
 * the interface ValueProducingTagIF).
 */
public class TopicMapReferencesTag extends TagSupport {

  /**
   * Process the start tag for this instance.
   */
  public int doStartTag() throws JspTagException {
    // retrieve parent tag which accepts the produced collection by this tag 
    ValueAcceptingTagIF acceptingTag = (ValueAcceptingTagIF)
      findAncestorWithClass(this, ValueAcceptingTagIF.class);

    // try to retrieve root ContextTag
    ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);

    // get collection of TM Reference entries from Configuration
    Collection refs =
      contextTag.getNavigatorApplication().getTopicMapRepository().getReferences();
    
    // kick it up to the accepting tag
    acceptingTag.accept( refs );

    // ignore body, because this is an empty tag
    return SKIP_BODY;
  }
  
}





