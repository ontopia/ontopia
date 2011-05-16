
// $Id: CopyTag.java,v 1.5 2005/12/19 20:57:23 larsga Exp $

package net.ontopia.topicmaps.nav2.taglibs.value;

import java.util.ArrayList;
import java.util.Collection;
import javax.servlet.jsp.JspTagException;

import net.ontopia.topicmaps.nav2.core.ValueProducingTagIF;
import net.ontopia.topicmaps.nav2.core.ValueAcceptingTagIF;

/**
 * INTERNAL: Value Producing Tag for copying another collection.
 */
public class CopyTag extends BaseValueProducingTag {

  public Collection process(Collection inputCollection)
    throws JspTagException {

    return inputCollection;
  }

}
