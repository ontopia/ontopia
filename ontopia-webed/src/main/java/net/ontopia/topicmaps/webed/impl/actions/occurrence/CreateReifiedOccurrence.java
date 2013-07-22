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

import java.net.MalformedURLException;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.impl.basic.Constants;

/**
 * PUBLIC: Action for adding an occurrence to a topic. The type
 * of the occurrence can also be provided.  
 * In addition the occurrence created is reified by a new topic.
 *
 * @since 2.0
 */
public class CreateReifiedOccurrence implements ActionIF {

  public void perform(ActionParametersIF params, ActionResponseIF response) {

    TopicIF topic = (TopicIF) params.get(0);
    TopicIF occtype = (TopicIF) params.get(1);
    
    TopicMapBuilderIF builder = topic.getTopicMap().getBuilder();

    // create new (external) occurrence for topic
    URILocator locator = null;
    try {
      locator = new URILocator(Constants.DUMMY_LOCATOR);
    } catch (MalformedURLException e) {
      throw new ActionRuntimeException("Malformed URL for occurrence: " + e);
    }

    OccurrenceIF occurrence = builder.makeOccurrence(topic, occtype, locator);

    // refify the topic and assign a basic name
    TopicIF reifier = builder.makeTopic();
    builder.makeTopicName(reifier, "Navn");

    // create src locator for occurrence
    LocatorIF srcloc;
    try {
			srcloc= new URILocator("http://net.ontopia.identity/occur#" + occurrence.getObjectId());
    } catch (MalformedURLException e) {
      throw new ActionRuntimeException("Malformed URL for occurrence source locator: " + e);
    }

    occurrence.addItemIdentifier(srcloc);

    // reify the occurrnce by hand
    reifier.addSubjectIdentifier(srcloc);

    System.out.println("store occurid : " + occurrence.getObjectId());

    response.addParameter("occurid", occurrence.getObjectId());
  }
}
