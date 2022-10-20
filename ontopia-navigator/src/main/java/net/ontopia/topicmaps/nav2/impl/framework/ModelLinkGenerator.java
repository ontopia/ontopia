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

package net.ontopia.topicmaps.nav2.impl.framework;

import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.nav2.core.LinkGeneratorIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.core.UserIF;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import net.ontopia.topicmaps.nav2.utils.NavigatorUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * INTERNAL: class which implements interface LinkGeneratorIF.
 * Generate a URI link to a model page (MVS environment).
 *
 * <p>This implementation should be used by a <code>link</code> tag
 * in a customized JSP when working with the MVS approach.
 *
 * <p>Example usage within the link tag:
 * <pre>template="/models/topic_%model%.jsp?tm=%topicmap%&id=%id%"</pre>
 *
 * @see net.ontopia.topicmaps.nav2.taglibs.output.LinkTag
 */
public class ModelLinkGenerator implements LinkGeneratorIF {

  /**
   * INTERNAL: Constant String value representing the name of a
   * template variable being replaced with the object id.
   */
  public static final String LINK_MODEL_KEY = "%model%";

  
  @Override
  public String generate(ContextTag contextTag, TMObjectIF tmObj,
                         String topicmapId, String template)
    throws NavigatorRuntimeException {

    String link = template;
    
    // replace object id placeholder with real value
    if (tmObj != null) {
      String objectId = NavigatorUtils.getStableId(tmObj);
      link = StringUtils.replace(link, LINK_ID_KEY, objectId);
    }

    // replace topicmap id placeholder with real value
    if (topicmapId != null) {
      link = StringUtils.replace(link, LINK_TOPICMAP_KEY, topicmapId);
    }

    // replace model placeholder with real value
    UserIF user = FrameworkUtils.getUser(contextTag.getPageContext());
    String model = user.getModel();
    link = StringUtils.replace(link, LINK_MODEL_KEY, model);

    return link;
  }

  @Override
  public String generate(ContextTag contextTag, TopicMapReferenceIF tmRefObj,
                         String template) 
    throws NavigatorRuntimeException {
    
    String link = template;
    
    // replace topicmap id placeholder with real value
    if (tmRefObj != null) {
      String topicmapId = tmRefObj.getId();
      link = StringUtils.replace(link, LINK_TOPICMAP_KEY, topicmapId);
    }

    // replace model placeholder with real value
    UserIF user = FrameworkUtils.getUser(contextTag.getPageContext());
    String model = user.getModel();
    link = StringUtils.replace(link, LINK_MODEL_KEY, model);

    return link;
  }
  
}
