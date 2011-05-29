
package net.ontopia.topicmaps.nav2.taglibs.value;

import java.util.ArrayList;
import java.util.Collections;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.BodyContent;

import net.ontopia.topicmaps.nav2.core.ValueProducingTagIF;
import net.ontopia.topicmaps.nav2.core.ValueAcceptingTagIF;

/**
 * INTERNAL: Value Producing Tag for generating a string value.
 * (somewhat special because this Tag does not need to
 *  manipulate an input collection)
 */
public class StringTag extends BodyTagSupport {

  /**
   * Actions after some body has been evaluated.
   */
  public int doAfterBody() throws JspTagException {

    // retrieve parent tag which accepts the result of this operation
    ValueAcceptingTagIF acceptingTag = (ValueAcceptingTagIF)
      findAncestorWithClass(this, ValueAcceptingTagIF.class);

    // get body content which should contain the string we want to set.
    BodyContent body = getBodyContent();
    String content = body.getString();

    // construct new collection which consists of one String entry
    // kick it over to the nearest accepting tag
    acceptingTag.accept( Collections.singleton(content) );

    // reset body contents
    body.clearBody();
    
    return SKIP_BODY;
  }

  /**
   * reset the state of the Tag.
   */
  public void release() {
    // overwrite default behaviour
    // do not set parent to null!!!
  }
  
}





