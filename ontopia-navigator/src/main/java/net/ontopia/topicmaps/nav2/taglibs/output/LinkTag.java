/*
 * #!
 * Ontopia Navigator
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.topicmaps.nav2.taglibs.output;

import java.io.IOException;
import java.util.Iterator;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;

import net.ontopia.topicmaps.nav2.core.LinkGeneratorIF;
import net.ontopia.topicmaps.nav2.core.NavigatorApplicationIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.impl.basic.DefaultUniversalLinkGenerator;
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

  @Override
  public final void generateOutput(JspWriter out, Iterator iter)
    throws JspTagException, IOException {

    generateOutput(out, iter.next());
  }
  
  public final void generateOutput(JspWriter out, Object elem)
    throws JspTagException, IOException {

    String link = null;
    if (linkGenerator == null) {
      linkGenerator = DEF_LINK_GENERATOR;
    }

    // --- first try if object is instance of TMObjectIF
    try {
      TMObjectIF tmobj = (TMObjectIF) elem;
      NavigatorApplicationIF navApp = contextTag.getNavigatorApplication();
      String tmRef = navApp.getTopicMapRefId(tmobj.getTopicMap());
      link = linkGenerator.generate(contextTag, tmobj, tmRef, templateStr);
    } catch (ClassCastException e) {
      // --- TopicMapReferenceIF
      if (elem instanceof TopicMapReferenceIF) {
        link = linkGenerator.generate(contextTag, (TopicMapReferenceIF) elem,
                templateStr);
      } else {
        // --- otherwise            
        throw new NavigatorRuntimeException("LinkTag: Unsupported object type: " +
                                            elem.getClass().getName());
      }
    }
    
    // finally put it out
    if (link != null) {
      print2Writer(out, link);
    }      
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
