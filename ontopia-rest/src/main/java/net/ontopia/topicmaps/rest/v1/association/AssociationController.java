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

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.rest.controller.AbstractController;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.Association;
import net.ontopia.topicmaps.rest.model.AssociationRole;
import net.ontopia.topicmaps.rest.v1.ReifiableController;
import net.ontopia.topicmaps.rest.v1.TMObjectController;
import net.ontopia.topicmaps.rest.v1.TypedController;
import net.ontopia.topicmaps.rest.v1.role.RoleController;
import net.ontopia.topicmaps.rest.v1.scoped.ScopedController;
import net.ontopia.topicmaps.rest.v1.topic.TopicController;
import org.apache.commons.collections4.CollectionUtils;

public class AssociationController extends AbstractController {

	private TypedController typed;
	private ScopedController scoped;
	private ReifiableController reifiable;
	private TMObjectController tmobject;
	private TopicController topics;
	private RoleController roles;

	@Override
	protected void init() {
		typed = getController(TypedController.class);
		scoped = getController(ScopedController.class);
		reifiable = getController(ReifiableController.class);
		tmobject = getController(TMObjectController.class);
		topics = getController(TopicController.class);
		roles = getController(RoleController.class);
	}

	public AssociationIF add(TopicMapIF tm, Association association) {
		requireNotNull(association.getType(), "type");
		return add(tm, topics.resolve(tm, association.getType()), association);
	}

	public AssociationIF add(TopicMapIF tm, TopicIF type, Association association) {
		
		if (CollectionUtils.isEmpty(association.getRoles())) {
			throw OntopiaRestErrors.MANDATORY_FIELD_IS_NULL.build("roles");
		}
		
		TopicMapBuilderIF builder = tm.getBuilder();
		
		AssociationIF result = builder.makeAssociation(type);
		
		for (AssociationRole role : association.getRoles()) {
			roles.add(tm, result, role);
		}
		
		// ScopedIF
		scoped.setScope(result, association.getScope());
		// ReifiableIF
		reifiable.setReifier(result, association.getReifier());
		// TMObjectIF
		tmobject.setItemIdentifiers(result, association);
		
		return result;
	}
	
	public void remove(TopicMapIF tm, Association association) {
		remove(resolve(tm, association));
	}
	
	public void remove(AssociationIF association) {
		association.remove();
	}
	
	public AssociationIF change(TopicMapIF tm, AssociationIF result, Association association) {
		
		// TypedIF
		typed.setType(result, association.getType());
		// ScopedIF
		scoped.setScope(result, association.getScope());
		// ReifiableIF
		reifiable.setReifier(result, association.getReifier());
		// TMObjectIF
		tmobject.setItemIdentifiers(result, association);

		return result;
	}
	
	public AssociationIF resolve(TopicMapIF tm, Association association) {
		return tmobject.resolve(tm, association, AssociationIF.class);
	}
}
