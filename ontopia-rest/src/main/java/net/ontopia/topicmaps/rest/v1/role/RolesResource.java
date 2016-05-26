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
import net.ontopia.topicmaps.rest.model.FetchOptions;
import net.ontopia.topicmaps.rest.resources.AbstractTransactionalResource;
import net.ontopia.topicmaps.rest.resources.Parameters;
import org.restlet.resource.Get;

public class RolesResource extends AbstractTransactionalResource {
	
	@Get
	public Collection<AssociationRoleIF> getRolesByType(FetchOptions options) {
		TMObjectIF object = getRequestParameter(TMObjectIF.class, Parameters.ID, true);
		TopicIF roleType = getRequestParameter(Parameters.ROLETYPE, true);
		TopicIF associationType = getRequestParameter(Parameters.ASSOCIATIONTYPE, true);
		
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
		if (roleType != null) {
			return association.getRolesByType(roleType);
		} else {
			return association.getRoles();
		}
	}
}
