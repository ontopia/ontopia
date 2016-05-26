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

package net.ontopia.topicmaps.rest.v1.role;

import org.restlet.Context;
import org.restlet.routing.Router;

public class RoleRouter extends Router {

	public RoleRouter(Context context) {
		super(context);
		
		setName("Role router");
		setDescription("Binds the resources related to association role operations");
		
		// list
		// ClassInstanceIndexIF.getAssociationRoles
		attach("/typed/{type}", RolesResource.class);

		// ClassInstanceIndexIF.getAssociationRoleTypes
		attach("/types", RoleTypesResource.class);

		// single
		attach("/{id}", RoleResource.class);
	}
}
