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

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.rest.Constants;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.Topic;
import net.ontopia.topicmaps.rest.resources.AbstractTMObjectResource;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

public class TopicResource extends AbstractTMObjectResource<TopicIF> {

	public TopicResource() {
		super(TopicIF.class);
	}

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		// CTM and LTM cannot export a single topic
		blockMimeType(Constants.CTM_MEDIA_TYPE, Constants.LTM_MEDIA_TYPE);
	}
	
	@Get
	public TopicIF getTopic() {
		return resolve();
	}
	
	@Put
	public void addTopic(Topic topic) {
		if (topic == null) {
			throw OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL.build("Topic");
		}

		TopicIF result = getController(TopicController.class).add(getTopicMap(), topic);
		store.commit();
		redirectTo(result);
	}
	
	@Post
	public TopicIF changeTopic(Topic topic) {
		TopicIF result = getController(TopicController.class).change(getTopicMap(), resolve(), topic);
		store.commit();
		return result;
	}
	
	@Delete
	public void removeTopic() {
		getController(TopicController.class).remove(resolve());
		store.commit();
	}
}
