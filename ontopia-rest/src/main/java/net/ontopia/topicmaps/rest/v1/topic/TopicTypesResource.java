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

import java.util.Collection;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.Topic;
import net.ontopia.topicmaps.rest.model.mixin.MFlatTopic;
import net.ontopia.topicmaps.rest.resources.AbstractTransactionalResource;
import net.ontopia.topicmaps.rest.resources.Parameters;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

public class TopicTypesResource extends AbstractTransactionalResource {

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		addMixInAnnotations(TopicIF.class, MFlatTopic.class);
	}

	@Get
	public Collection<TopicIF> getTopicTypes() {
		TopicIF topic = Parameters.ID.withExpected(TopicIF.class).optional(this);
		if (topic != null) {
			return topic.getTypes();
		} else {
			return getIndex(ClassInstanceIndexIF.class).getTopicTypes();
		}
	}
	
	@Put
	public Collection<TopicIF> addType(Topic type) {
		TopicIF topic = Parameters.ID.withExpected(TopicIF.class).required(this);

		if (type == null) {
			throw OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL.build("Topic");
		}

		getController(TopicController.class).addType(topic, type);
		store.commit();
		return topic.getTypes();
	}
}
