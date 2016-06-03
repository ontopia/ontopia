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

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.rest.controller.AbstractController;
import net.ontopia.topicmaps.rest.model.AssociationRole;
import net.ontopia.topicmaps.rest.v1.ReifiableController;
import net.ontopia.topicmaps.rest.v1.TMObjectController;
import net.ontopia.topicmaps.rest.v1.TypedController;
import net.ontopia.topicmaps.rest.v1.topic.TopicController;

public class RoleController extends AbstractController {

	private TypedController typed;
	private ReifiableController reifiable;
	private TMObjectController tmobject;
	private TopicController topics;

	@Override
	protected void init() {
		typed = getController(TypedController.class);
		reifiable = getController(ReifiableController.class);
		tmobject = getController(TMObjectController.class);
		topics = getController(TopicController.class);
	}

	public AssociationRoleIF add(TopicMapIF tm, AssociationIF association, AssociationRole role) {
		TopicMapBuilderIF builder = tm.getBuilder();
		TopicIF type = topics.resolve(tm, role.getType());
		TopicIF player = topics.resolve(tm, role.getPlayer());
		
		AssociationRoleIF result = builder.makeAssociationRole(association, type, player);
		
		// ReifiableIF
		reifiable.setReifier(result, role.getReifier());
		// TMObjectIF
		tmobject.setItemIdentifiers(result, role);
		
		return result;
	}
	
	public void remove(TopicMapIF tm, AssociationRole role) {
		remove(resolve(tm, role));
	}
	
	public void remove(AssociationRoleIF role) {
		role.remove();
	}
	
	public AssociationRoleIF change(TopicMapIF tm, AssociationRole role) {
		AssociationRoleIF result = resolve(tm, role);
		
		// AssociationRoleIF
		if (role.getPlayer() != null) {
			result.setPlayer(topics.resolve(tm, role.getPlayer()));
		}
		
		// TypedIF
		typed.setType(result, role.getType());
		// ReifiableIF
		reifiable.setReifier(result, role.getReifier());
		// TMObjectIF
		tmobject.setItemIdentifiers(result, role);
		
		return result;
	}
	
	public AssociationRoleIF resolve(TopicMapIF tm, AssociationRole role) {
		return tmobject.resolve(tm, role, AssociationRoleIF.class);
	}
}
