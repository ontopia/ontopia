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

import java.util.Collection;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.AssociationRole;
import net.ontopia.topicmaps.rest.model.mixin.MAssociationRoleWithoutAssociation;
import net.ontopia.topicmaps.rest.resources.AbstractTransactionalResource;
import net.ontopia.topicmaps.rest.resources.Parameters;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.Put;

public class RolesResource extends AbstractTransactionalResource {
	
	@Get
	public Collection<AssociationRoleIF> getRolesByType() {
		TMObjectIF object = Parameters.ID.withExpected(TMObjectIF.class).optional(this);
		TopicIF roleType = Parameters.ROLETYPE.optional(this);
		TopicIF associationType = Parameters.ASSOCIATIONTYPE.optional(this);
		
		if (object == null) {
			return getIndex(ClassInstanceIndexIF.class).getAssociationRoles(roleType);
		} else if (object instanceof AssociationIF) {
			return getAssociationRoles((AssociationIF) object, roleType);
		} else if (object instanceof TopicIF) {
			return getTopicRoles((TopicIF) object, roleType, associationType);
		} else {
			throw OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE.build(Parameters.ID, "TopicIF or AssociationIF", object.getClass().getSimpleName());
		}
	}

	private Collection<AssociationRoleIF> getTopicRoles(TopicIF topic, TopicIF roleType, TopicIF associationType) {
		if (roleType == null) {
			return topic.getRoles();
		} else {
			if (associationType == null) {
				return topic.getRolesByType(roleType);
			} else {
				return topic.getRolesByType(roleType, associationType);
			}
		}
	}

	private Collection<AssociationRoleIF> getAssociationRoles(AssociationIF association, TopicIF roleType) {
		addMixInAnnotations(AssociationRoleIF.class, MAssociationRoleWithoutAssociation.class);
		if (roleType != null) {
			return association.getRolesByType(roleType);
		} else {
			return association.getRoles();
		}
	}
	
	@Put
	public void addAssociationRole(AssociationRole role) {

		if ((optionalRequestParameter(Parameters.ROLETYPE) != null) || (optionalRequestParameter(Parameters.ASSOCIATIONTYPE) != null)) {
			setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
			return;
		}
		
		if (role == null) {
			throw OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL.build("Role");
		}
		
		AssociationRoleIF result = getController(RoleController.class).add(
				getTopicMap(),
				Parameters.ID.withExpected(AssociationIF.class).required(this),
				role);
		store.commit();

		// todo: maybe this should be '302 Found' instead
		redirectSeeOther("../../roles/" + result.getObjectId()); // todo: how to make this stable?
	}
}
