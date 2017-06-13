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

import java.util.Collection;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.TopicName;
import net.ontopia.topicmaps.rest.model.mixin.MTopicNameWithoutTopic;
import net.ontopia.topicmaps.rest.resources.AbstractTransactionalResource;
import net.ontopia.topicmaps.rest.resources.Parameters;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.Put;

public class TopicNamesResource extends AbstractTransactionalResource {
	
	@Get
	public Collection<TopicNameIF> getTopicNames() {
		TopicIF topic = Parameters.ID.withExpected(TopicIF.class).optional(this);
		TopicIF type = Parameters.TYPE.optional(this);
		
		if (topic != null) {
			addMixInAnnotations(TopicNameIF.class, MTopicNameWithoutTopic.class);
			
			if (type == null) {
				return topic.getTopicNames();
			} else {
				return topic.getTopicNamesByType(type);
			}
		} else {
			return getIndex(ClassInstanceIndexIF.class).getTopicNames(type);
		}
	}
	
	@Put
	public void addTopicName(TopicName name) {
		
		if (optionalRequestParameter(Parameters.TYPE) != null) {
			setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
			return;
		}

		if (name == null) {
			throw OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL.build("TopicName");
		}

		TopicNameIF result = getController(TopicNameController.class).add(
				getTopicMap(), 
				Parameters.ID.withExpected(TopicIF.class).required(this), 
				name);
		store.commit();

		// todo: maybe this should be '302 Found' instead
		redirectSeeOther("../../names/" + result.getObjectId()); // todo: how to make this stable?
	}
}
