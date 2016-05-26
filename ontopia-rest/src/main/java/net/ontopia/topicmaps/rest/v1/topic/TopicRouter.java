/*
 * #!
 * Ontopia Rest
 * #-
 * Copyright (C) 2001 - 2016 The Ontopia Project
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

package net.ontopia.topicmaps.rest.v1.topic;

import net.ontopia.topicmaps.rest.v1.association.AssociationsResource;
import net.ontopia.topicmaps.rest.v1.name.TopicNamesResource;
import net.ontopia.topicmaps.rest.v1.occurrence.OccurrencesResource;
import net.ontopia.topicmaps.rest.v1.role.RolesResource;
import org.restlet.Context;
import org.restlet.routing.Router;

public class TopicRouter extends Router {

	public TopicRouter(Context context) {
		super(context);
		
		setName("Topic router");
		setDescription("Binds the resources related to topic operations");
		
		// list
		// TopicMapIF.getTopics
		attach("", TopicsResource.class);

		// ClassInstanceIndex.getTopics
		attach("/typed/{type}", TopicsResource.class);
		
		// ClassInstanceIndex.getTopicTypes
		attach("/types", TopicTypesResource.class);
		
		// single
		attach("/{id}", TopicResource.class);
		
		// names
		attach("/{id}/names", TopicNamesResource.class);
		attach("/{id}/names/{type}", TopicNamesResource.class);
		
		// occurrences
		attach("/{id}/occurrences", OccurrencesResource.class);
		attach("/{id}/occurrences/{type}", OccurrencesResource.class);

		// roles
		attach("/{id}/roles", RolesResource.class);
		attach("/{id}/roles/{roletype}", RolesResource.class);
		attach("/{id}/roles/{roletype}/{associationtype}", RolesResource.class);

		// associations
		attach("/{id}/associations", AssociationsResource.class);
		attach("/{id}/associations/{type}", AssociationsResource.class);
		
		// usage
		attach("/{id}/use", TopicUseResource.class);
		
		// scope
		attach("/{id}/scope/use", ScopeUseResource.class);
		attach("/{id}/scope/{type}", ScopedResource.class);
	}
}
