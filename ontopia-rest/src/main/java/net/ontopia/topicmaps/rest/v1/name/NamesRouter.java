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

package net.ontopia.topicmaps.rest.v1.name;

import net.ontopia.topicmaps.rest.v1.scoped.ScopedRouter;
import net.ontopia.topicmaps.rest.v1.variant.VariantsResource;
import org.restlet.Context;

public class NamesRouter extends ScopedRouter {

	public NamesRouter(Context context) {
		super(context);
		
		setName("Names router");
		setDescription("Binds the resources related to name operations");

		// list
		// ClassInstanceIndexIF.getTopicNames
		attach("/typed/{type}", TopicNamesResource.class);
		
		// ClassInstanceIndexIF.getTopicNameTypes
		attach("/types/{type}", TopicNameTypesResource.class);
		
		// NameIndexIF.getTopicNames
		attach("/index", IndexResource.class);

		// single
		attach("/{id}", TopicNameResource.class);
		
		// variants
		attach("/{id}/variants", VariantsResource.class);
		
	}
}
