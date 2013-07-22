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

package net.ontopia.topicmaps.nav2.impl.basic;

import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.nav2.core.LinkGeneratorIF;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.utils.StringUtils;

/**
 * INTERNAL: Default link generator which produces links to any kind
 * of topic map object. Can replace the %id% and %topicmap% variables,
 * but does not recognize any others. Used by the <code>link</code> tag.
 *
 * @see net.ontopia.topicmaps.nav2.taglibs.output.LinkTag
 */
public class DefaultUniversalLinkGenerator implements LinkGeneratorIF {

  public String generate(ContextTag contextTag, TMObjectIF tmObj,
                         String topicmapId, String template) {
    String link = template;
    
    // replace object id placeholder with real value
    String objectId = tmObj.getObjectId();
    link = StringUtils.replace(link, LINK_ID_KEY, objectId);

    // replace topicmap id placeholder with real value
    link = StringUtils.replace(link, LINK_TOPICMAP_KEY, topicmapId);

    return link;
  }
  
  public String generate(ContextTag contextTag, TopicMapReferenceIF tmRefObj,
                         String template) {
    String link = template;
    
    // replace topicmap id placeholder with real value
    String topicmapId = tmRefObj.getId();
    link = StringUtils.replace(link, LINK_TOPICMAP_KEY, topicmapId);

    return link;
  }
  
}
