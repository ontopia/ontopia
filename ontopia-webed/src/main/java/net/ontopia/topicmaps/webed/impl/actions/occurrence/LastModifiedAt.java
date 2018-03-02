/*
 * #!
 * Ontopia Webed
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

package net.ontopia.topicmaps.webed.impl.actions.occurrence;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.impl.utils.ActionSignature;


/**
 * PUBLIC: Action that sets a timestamp on the topic passed to it as
 * an internal occurrence using the last-modified-at PSI as the
 * occurrence type.
 *
 * @since 2.0
 */
public class LastModifiedAt implements ActionIF {
  private LocatorIF psi = URILocator.create("http://psi.ontopia.net/xtm/occurrence-type/last-modified-at");
  protected DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
  
  public LastModifiedAt() {
  }

  @Override
  public void perform(ActionParametersIF params,
                      ActionResponseIF response) {

    // test params
    ActionSignature paramsType = ActionSignature.getSignature("t");
    paramsType.validateArguments(params, this);

    TopicIF topic = (TopicIF) params.get(0);
    if (topic == null)
      // the topic has been deleted
      return;

    TopicMapIF topicmap = topic.getTopicMap();
    if (topicmap == null)
      // this means that the topic has been deleted, almost certainly by the
      // current request. that means that it does not make any sense for us
      // to do anything, so we just silently stop
      return;
    
    TopicIF lastmod = getLastModifiedTopic(topicmap);

    OccurrenceIF lastocc = null;
    
    Iterator it = topic.getOccurrences().iterator();
    while (it.hasNext()) {
      OccurrenceIF occ = (OccurrenceIF) it.next();
      if (lastmod.equals(occ.getType())) {
        lastocc = occ;
        break;
      }
    }

    if (lastocc == null)
      lastocc = topicmap.getBuilder().makeOccurrence(topic, lastmod, getTimeStamp());
		else
			lastocc.setValue(getTimeStamp());
  }

  // Internals

  protected String getTimeStamp() {
    return formatter.format(new Date());
  }

  private TopicIF getLastModifiedTopic(TopicMapIF topicmap) {
    TopicIF lastmod = topicmap.getTopicBySubjectIdentifier(psi);
    if (lastmod == null) {
      lastmod = topicmap.getBuilder().makeTopic();
      lastmod.addSubjectIdentifier(psi);
    }
    return lastmod;
  }
  
}
