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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.topicmaps.nav2.plugins.PluginIF;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;

/**
 * INTERNAL: Framework related tag for listing all activated plugins available
 * in the navigator configuration.
 */
public class PluginTableTag extends TagSupport {

  // tag attributes
  private String groupId = null;
  private String excludePluginId = "";
  private String active = "";

  /**
   * Process the start tag for this instance.
   */
  @Override
  public int doStartTag() throws JspTagException {

    ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);

    try {
      JspWriter out = pageContext.getOut();
      List plugins = getActivePlugins(contextTag);
      Iterator it = plugins.iterator();

      int activeIndex = getActiveIndex(plugins);

      out.write("<table id=\"linkTable\" border=\"0\"><tr>");

      // loop over all activated plugins and let them generate html
      while (it.hasNext()) {
        String html;
        PluginIF plugin;
        plugin = (PluginIF) it.next();

        out.write("<td class=\"");
        out.write(pluginIdFor(plugin, plugins, activeIndex));
        out.write("\">");
        html = plugin.generateHTML(contextTag);
        if (html != null) {
          out.write(html);
        }
        out.write("</td>");
      }

      out.write("</tr></table>");

    } catch (IOException ioe) {
      throw new JspTagException("Error in PluginListTag: "
          + "JspWriter not there: " + ioe);
    }

    // empty tag has not to eval anything
    return SKIP_BODY;
  }

  // -------------------------------------------------------
  // set methods for tag attributes
  // -------------------------------------------------------

  private int getActiveIndex(List plugins) {
    for (int i = 0; i < plugins.size(); i++) {
      PluginIF plugin = (PluginIF) plugins.get(i);
      if (plugin.getId().equals(active)) {
        return i;
      }
    }
    return -1;
  }

  private List getActivePlugins(ContextTag contextTag) {

    Collection plugins = contextTag.getNavigatorConfiguration().getPlugins(
        groupId);
    List activePlugins = new ArrayList(plugins.size());

    for (Iterator iter = plugins.iterator(); iter.hasNext();) {
      PluginIF plugin = (PluginIF) iter.next();
      if (!excludePluginId.equals(plugin.getId())
          && (plugin.getState() == PluginIF.ACTIVATED)) {
        activePlugins.add(plugin);
      }
    }
    return activePlugins;
  }

  private String pluginIdFor(PluginIF plugin, List plugins, int activeIndex) {

    /*
     * Possible classes are: 
     * 
     * tab-first-active 
     * tab-first-inactive
     * tab-first-beforeactive (the next tab is the active one)
     * 
     * tab-mid-active 
     * tab-mid-inactive 
     * tab-mid-beforeactive (the next tab is the active one) 
     * tab-mid-afteractive (the previous tab was the active one) 
     * 
     * tab-last-active 
     * tab-last-inactive 
     * tab-last-afteractive (the previous tab was the active one) 
     * 
     * tab-sole (special case)
     */

    // Special Case
    if (plugins.size() == 1) {
      return "tab-sole";
    }

    int index = plugins.indexOf(plugin);
    boolean isFirst = index == 0;
    boolean isAfterActive = index == activeIndex + 1;
    boolean isBeforeActive = index == activeIndex - 1;
    boolean isLast = index == (plugins.size() - 1);
    boolean isActive = active.equals(plugin.getId());

    // First Tab
    if (isFirst) {
      if (isActive) {
        return "tab-first-active";
      } else if (isBeforeActive) {
        return "tab-first-beforeactive";
      } else {
        return "tab-first-inactive";
      }
    }
    
    // Last Tab
    if (isLast) {
      if (isActive) {
        return "tab-last-active";
      } else if (isAfterActive) {
        return "tab-last-afteractive";
      } else {
        return "tab-last-inactive";
      }
    }      

    // Middle Tab
      if (isActive) {
        return "tab-mid-active";
    } else if (isAfterActive) {
      return "tab-mid-afteractive";
    } else if (isBeforeActive) {
      return "tab-mid-beforeactive";
    } else {
      return "tab-mid-inactive";
    }

  }

  /**
   * Sets String that is representing the Plugin Group Id for the plugins which
   * should be listed.
   */
  public void setGroup(String groupId) {
    this.groupId = groupId;
  }

  /**
   * Set the currently active plugin id
   */
  public void setActive(String id) {
    active = id;
  }

  /**
   * Sets String that is representing a Plugin Id which should not be listed.
   */
  public void setExclude(String excludePluginId) {
    this.excludePluginId = excludePluginId;
  }

}
