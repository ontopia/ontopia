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

package net.ontopia.topicmaps.rest.v1.association;

import net.ontopia.topicmaps.rest.v1.role.RoleTypesResource;
import net.ontopia.topicmaps.rest.v1.role.RolesResource;
import net.ontopia.topicmaps.rest.v1.scoped.ScopedRouter;
import org.restlet.Context;

public class AssociationRouter extends ScopedRouter {

	public AssociationRouter(Context context) {
		super(context);
		
		setName("Association router");
		setDescription("Binds the resources related to association operations");
		
		// TopicMapIF.getAssociations
		attach("", AssociationsResource.class);
		
		// ClassInstanceIndex.getAssociations
		attach("/typed/{type}", AssociationsResource.class);
		
		// single
		attach("/{id}", AssociationResource.class);
		
		// roles
		attach("/{id}/roles", RolesResource.class);
		attach("/{id}/roles/{roletype}", RolesResource.class);
		
		// role types
		attach("/{id}/roles/type", RoleTypesResource.class);		
	}
}
