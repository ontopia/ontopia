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

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.utils.TopicStringifiers;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;

/**
 * INTERNAL: Plug-in that creates link to Omnigator topic map page.
 *
 * @since 3.4
 */
public class TopicMapLinkPlugin extends DefaultPlugin {
  
  @Override
  public String generateHTML(ContextTag contextTag) {
    if (contextTag == null)
      throw new OntopiaRuntimeException("Plugin must have a parent tolog:context tag.");
    
    TopicMapIF tm = contextTag.getTopicMap();
    if (tm == null)
      return "<span>No topic map!</span>";

    // get omnigator model
    String model = FrameworkUtils.getUser(contextTag.getPageContext()).getModel();
        
    // get topic map id
    String tmid = contextTag.getTopicMapId();

    // get topic map title
    String tmtitle = "[No name]";
    TopicIF reifier = tm.getReifier();    
    if (reifier != null)
      tmtitle = TopicStringifiers.toString(reifier);
    else {
      TopicMapReferenceIF reference = tm.getStore().getReference();
      if (reference != null)
        tmtitle = reference.getTitle();
    }

    return "<a href=\"" + uri + "/models/topicmap_" + model + ".jsp?tm=" + tmid + "\" title=\"Link to topic map page.\">" + tmtitle + "</a>";
  }

}
