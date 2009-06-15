
// $Id: LinkTag.java,v 1.19 2005/10/14 16:08:40 larsga Exp $

package net.ontopia.topicmaps.nav2.taglibs.output;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Collection;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;

import net.ontopia.topicmaps.nav2.core.LinkGeneratorIF;
import net.ontopia.topicmaps.nav2.core.NavigatorApplicationIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.impl.basic.DefaultUniversalLinkGenerator;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.nav2.utils.NavigatorUtils;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;

/**
 * INTERNAL: Output Producing Tag for writing out the
 * URI of an object. All characters listed as non-safe
 * in RFC 2396 are escaped using URI %-escapes.
 *
 * @see java.net.URLEncoder
 */
public class LinkTag extends BaseOutputProducingTag {

  // default link generator
  private static final LinkGeneratorIF DEF_LINK_GENERATOR =
    new DefaultUniversalLinkGenerator();

  // tag attributes
  private String templateStr;
  private LinkGeneratorIF linkGenerator;

  public LinkTag() {
    // we care ourselves about escaping
    super(false, true);
  }

  public final void generateOutput(JspWriter out, Iterator iter)
    throws JspTagException, IOException {

    generateOutput(out, iter.next());
  }
  
  public final void generateOutput(JspWriter out, Object elem)
    throws JspTagException, IOException {

    String link = null;
    if (linkGenerator == null)
      linkGenerator = DEF_LINK_GENERATOR;

    // --- first try if object is instance of TMObjectIF
    try {
      TMObjectIF tmobj = (TMObjectIF) elem;
      NavigatorApplicationIF navApp = contextTag.getNavigatorApplication();
      String tmRef = navApp.getTopicMapRefId(tmobj.getTopicMap());
      link = linkGenerator.generate(contextTag, tmobj, tmRef, templateStr);
    } catch (ClassCastException e) {
      // --- TopicMapReferenceIF
      if (elem instanceof TopicMapReferenceIF)
        link = linkGenerator.generate(contextTag, (TopicMapReferenceIF) elem,
                                      templateStr);
      else
        // --- otherwise            
        throw new NavigatorRuntimeException("LinkTag: Unsupported object type: " +
                                            elem.getClass().getName());
    }
    
    // finally put it out
    if (link != null)
      print2Writer(out, link);      
  }

  // -----------------------------------------------------------------
  // set methods for tag attributes
  // -----------------------------------------------------------------

  public final void setTemplate(String templateStr) {
    this.templateStr = templateStr;
  }

  public final void setGenerator(String classname)
    throws NavigatorRuntimeException {
    
    contextTag = FrameworkUtils.getContextTag(pageContext);

    if (contextTag == null) {
      String msg = "LinkTag cannot set attribute 'generator' to '" +
        classname + "', because contextTag is null!";
      throw new NavigatorRuntimeException(msg);
    }
    if (contextTag.getNavigatorApplication() == null) {
      String msg = "LinkTag cannot set attribute 'generator' to '" +
        classname + "', because unable to access NavigatorApplication!";
      throw new NavigatorRuntimeException(msg);
    }
    
    // should use logging to debug channel 
    // System.out.println("Parent: " + getParent());
    // System.out.println("cT: " + contextTag);
    // System.out.println("nA: " + contextTag.getNavigatorApplication());
    
    linkGenerator = (LinkGeneratorIF) contextTag.getNavigatorApplication()
      .getInstanceOf(classname);
  }

}
