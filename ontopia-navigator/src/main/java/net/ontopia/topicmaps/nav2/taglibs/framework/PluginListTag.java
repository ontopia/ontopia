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

package net.ontopia.topicmaps.nav2.taglibs.framework;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.topicmaps.nav2.plugins.PluginIF;
import net.ontopia.topicmaps.nav2.plugins.IncludePluginIF;
import net.ontopia.topicmaps.nav2.impl.basic.JSPEngineWrapper;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;

/**
 * INTERNAL: Framework related tag for listing all activated plugins
 * available in the navigator configuration.
 */
public class PluginListTag extends TagSupport {

  // constants
  public static final String DEF_SEPARATOR = " ";
  
  // tag attributes
  private String separator = DEF_SEPARATOR;
  private String groupId = null;
  private String excludePluginId = null;
  private boolean preSeparatorFlag = false;
  
  /**
   * Process the start tag for this instance.
   */
  @Override
  public int doStartTag() throws JspTagException {
    ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);
    if (contextTag == null) {
      throw new JspTagException("The framework:pluginList tag can only be used " +
                                "inside the context tag");
    }

    try {
      JspWriter out = pageContext.getOut();

      Iterator it = contextTag.getNavigatorConfiguration()
        .getPlugins(groupId).iterator();
      PluginIF plugin;
      String html;
      boolean isFirst = true;
      // loop over all activated plugins and let them generate html 
      while (it.hasNext()) {
        plugin = (PluginIF) it.next();
        // if this plugin should be excluded than go to next one
        if (excludePluginId != null && excludePluginId.equals(plugin.getId())) {
          continue;
        }
        // if activated then generate HTML
        if (plugin.getState() == PluginIF.ACTIVATED) {          
          html = plugin.generateHTML(contextTag);
          if (html != null) {
            // don't put out separator first time and last time
            if (!isFirst || preSeparatorFlag) {
              out.write(separator);
            }
            
            if (plugin instanceof IncludePluginIF) {             
              pageContext.include(html);
            } else {
              out.write(html);
            }
            
            isFirst = false;
          }
        }
      } // while it

    } catch (ServletException sr) {
      throw JSPEngineWrapper.getJspTagException("Error in PluginListTag", sr);
    } catch (IOException ioe) {
      throw JSPEngineWrapper.getJspTagException("Error in PluginListTag", ioe);
    }

    // empty tag has not to eval anything
    return SKIP_BODY;
  }
  
  // -------------------------------------------------------
  // set methods for tag attributes
  // -------------------------------------------------------

  /**
   * Sets String that is displayed between the listed plugins to
   * separate them.
   */
  public void setSeparator(String sep) {
    this.separator = sep;
  }
  
  /**
   * Sets if a separator should be displayed before the list of
   * plugins starts. Allowed values true | false (default = false).
   */
  public void setPreSeparator(String preSeparator) {
    this.preSeparatorFlag = preSeparator.equalsIgnoreCase("true");
  }
  
  /**
   * Sets String that is representing the Plugin Group Id for the
   * plugins which should be listed.
   */
  public void setGroup(String groupId) {
    this.groupId = groupId;
  }
  
  /**
   * Sets String that is representing a Plugin Id which should not be
   * listed.
   */
  public void setExclude(String excludePluginId) {
    this.excludePluginId = excludePluginId;
  }
  
}
