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
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.rest.model.Topic;
import net.ontopia.topicmaps.rest.model.mixin.MFlatTopic;
import net.ontopia.topicmaps.rest.resources.AbstractTransactionalResource;
import net.ontopia.topicmaps.rest.resources.Parameters;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;

public class TopicsResource extends AbstractTransactionalResource {

	@Get
	public Collection<TopicIF> getTopics() {
		addMixInAnnotations(TopicIF.class, MFlatTopic.class);

		TopicIF type = Parameters.TYPE.optional(this);
		if (type != null) {
			return getIndex(ClassInstanceIndexIF.class).getTopics(type);
		} else {
			return getTopicMap().getTopics();
		}
	}
	
	@Put
	public void addTopic(Topic topic) {
		TopicMapIF tm = getTopicMap();
		TopicIF type = Parameters.TYPE.optional(this);
		TopicIF result;
		if (type != null) {
			result = getController(TopicController.class).add(tm, type, topic);
		} else {
			result = getController(TopicController.class).add(tm, topic);
		}
		store.commit();
		redirectSeeOther(result.getObjectId());
	}
	
	@Delete
	public void deleteTopic(Topic topic) {
		getController(TopicController.class).remove(getTopicMap(), topic);
		store.commit();
	}
}
