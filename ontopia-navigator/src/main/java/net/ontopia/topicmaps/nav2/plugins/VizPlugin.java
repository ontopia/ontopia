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

package net.ontopia.topicmaps.nav2.plugins;

import java.util.Set;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.utils.CompactHashSet;
import net.ontopia.utils.OntopiaRuntimeException;
import org.apache.commons.lang3.StringUtils;

/** 
 * INTERNAL: Simple extension of the DefaultPlugin used to block
 * vizigation of certain topic maps that don't vizigate well.
 */
public class VizPlugin extends DefaultPlugin {
  private Set blocked;
  private String message;
  
  @Override
  public void init() {
    message = getParameter("message");
    if (message == null) {
      message = "Vizigation is disabled for this topic map";
    }
    
    
    blocked = new CompactHashSet();
    String tmids = getParameter("blocked");
    if (tmids == null) {
      return;
    }

    String[] ids = StringUtils.split(tmids, ",");
    for (int ix = 0; ix < ids.length; ix++) {
      blocked.add(ids[ix].trim());
    }
  }
  
  @Override
  public String generateHTML(ContextTag context) {
    if (context == null) {
      throw new OntopiaRuntimeException("Plugin must have a parent logic:context tag.");
    }
    
    String tm = context.getTopicMapId();
    if (!blocked.contains(tm)) {
      return super.generateHTML(context);
    }
      
    // generate HTML String
    return "<span title=\"" + message + "\">Vizigate</span>";
  }
}
