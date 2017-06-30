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

package net.ontopia.topicmaps.rest.v1;

import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.rest.controller.AbstractController;
import net.ontopia.topicmaps.rest.model.Topic;
import net.ontopia.topicmaps.rest.v1.topic.TopicController;

public class ReifiableController extends AbstractController {

	private TopicController topic;
	
	@Override
	protected void init() {
		topic = getController(TopicController.class);
	}
	
	public void setReifier(ReifiableIF reifiable, Topic reifier) {
		if (reifier == null) {
			if (reifiable.getReifier() != null) {
				reifiable.setReifier(null);
			}
		} else {
			reifiable.setReifier(topic.resolve(reifiable.getTopicMap(), reifier));
		}
	}
}
