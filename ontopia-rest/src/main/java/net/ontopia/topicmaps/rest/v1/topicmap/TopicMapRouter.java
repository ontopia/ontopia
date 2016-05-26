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

package net.ontopia.topicmaps.rest.v1.topicmap;

import net.ontopia.topicmaps.rest.v1.association.AssociationRouter;
import net.ontopia.topicmaps.rest.v1.name.NamesRouter;
import net.ontopia.topicmaps.rest.v1.occurrence.OccurrenceRouter;
import net.ontopia.topicmaps.rest.v1.role.RoleRouter;
import net.ontopia.topicmaps.rest.v1.scoped.ScopesResource;
import net.ontopia.topicmaps.rest.v1.topic.TopicRouter;
import net.ontopia.topicmaps.rest.v1.variant.VariantRouter;
import org.restlet.Context;
import org.restlet.routing.Router;

public class TopicMapRouter extends Router {

	public TopicMapRouter(Context context) {
		super(context);
		setName("Topicmap router");
		
		// Topicmap
		attach("", TopicMapResource.class);
		attach("/", TopicMapResource.class);
		
		// objects
		attach("/topics", new TopicRouter(context));
		attach("/names", new NamesRouter(context));
		attach("/occurrences", new OccurrenceRouter(context));
		attach("/variants", new VariantRouter(context));
		attach("/associations", new AssociationRouter(context));
		attach("/roles", new RoleRouter(context));
		
		// StatisticsIndexIF
		attach("/statistics", StatisticsResource.class);
		
		// ScopeIndexIF
		attach("/scopes/{type}", ScopesResource.class);

		// SearchIF
		attach("/search", SearcherResource.class);
	}
}
