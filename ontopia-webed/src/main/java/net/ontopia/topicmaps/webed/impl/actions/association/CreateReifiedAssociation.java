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

package net.ontopia.topicmaps.webed.impl.actions.association;

import java.net.MalformedURLException;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;

/**
 * PUBLIC: Action for creating a new association with one topic playing
 * a given role. In addition the association is automatically reified.
 *
 * @since 2.0
 */
public class CreateReifiedAssociation implements ActionIF {

  @Override
  public void perform(ActionParametersIF params, ActionResponseIF response) {

    TopicIF srctopic = (TopicIF) params.get(0);
    TopicIF roleType = (TopicIF) params.get(1);
		TopicIF atype = (TopicIF) params.get(2);

    TopicMapBuilderIF builder = srctopic.getTopicMap().getBuilder();

		if (atype == null)
			atype = builder.makeTopic();

    AssociationIF assoc = builder.makeAssociation(atype);
    AssociationRoleIF role = builder.makeAssociationRole(assoc, roleType, srctopic);

    // refify the topic and assign a default name
    TopicIF reifier = builder.makeTopic();
    builder.makeTopicName(reifier, "New Association");

    // create src locator for assoc
    LocatorIF srcloc;
    try {
      srcloc= new URILocator("http://net.ontopia.identity/assoc#" + assoc.getObjectId());
    } catch (MalformedURLException e) {
      throw new ActionRuntimeException("Malformed URL for occurrence source locator: " + e);
    }

    assoc.addItemIdentifier(srcloc);

    // reify the assoc
    reifier.addSubjectIdentifier(srcloc);

    response.addParameter("associd", assoc.getObjectId());
  }
}
