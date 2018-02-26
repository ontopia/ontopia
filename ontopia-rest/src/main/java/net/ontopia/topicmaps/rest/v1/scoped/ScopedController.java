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

package net.ontopia.topicmaps.rest.v1.scoped;

import java.util.Collection;
import java.util.HashSet;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.rest.controller.AbstractController;
import net.ontopia.topicmaps.rest.model.Topic;
import net.ontopia.topicmaps.rest.v1.topic.TopicController;
import org.apache.commons.collections4.CollectionUtils;

public class ScopedController extends AbstractController {

	private TopicController topic;

	@Override
	protected void init() {
		topic = getController(TopicController.class);
	}

	public void setScope(ScopedIF scoped, Collection<Topic> scope) {
		if (scope != null) {
			Collection<TopicIF> newScope = new HashSet<>(scope.size());
			for (Topic t : scope) {
				TopicIF resolved = topic.resolve(scoped.getTopicMap(), t);
				newScope.add(resolved);
				scoped.addTheme(resolved);
			}
			
			for (TopicIF remove : CollectionUtils.subtract(scoped.getScope(), newScope)) {
				scoped.removeTheme(remove);
			}
		}
	}
	
	public Collection<TopicIF> resolve(TopicMapIF tm, Collection<Topic> scope) {
		Collection<TopicIF> newScope = new HashSet<>(scope.size());
		for (Topic t : scope) {
			newScope.add(topic.resolve(tm, t));
		}
		return newScope;
	}

	public void add(ScopedIF scoped, Topic scope) {
		scoped.addTheme(
			topic.resolve(scoped.getTopicMap(), scope)
		);
	}

	public void remove(ScopedIF scoped, Topic scope) {
		scoped.removeTheme(
			topic.resolve(scoped.getTopicMap(), scope)
		);
	}
}
