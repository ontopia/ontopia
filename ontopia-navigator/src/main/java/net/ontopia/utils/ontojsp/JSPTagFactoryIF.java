
package net.ontopia.utils.ontojsp;

import java.util.Map;

import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;

/**
 * INTERNAL: An interface for a custom JSP tag generating factory.
 */
public interface JSPTagFactoryIF {

  /**
   * Creates the correct tag for this JSPTreeNode, depending on the
   * tagname.
   */
  public TagSupport getTagInstance(String tagname, Map attrVals,
                                   TagSupport parentTag) 
    throws NavigatorRuntimeException;
  
}




