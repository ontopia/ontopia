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

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.rest.controller.AbstractController;
import net.ontopia.topicmaps.rest.model.TopicName;
import net.ontopia.topicmaps.rest.v1.ReifiableController;
import net.ontopia.topicmaps.rest.v1.TMObjectController;
import net.ontopia.topicmaps.rest.v1.TypedController;
import net.ontopia.topicmaps.rest.v1.scoped.ScopedController;
import net.ontopia.topicmaps.rest.v1.topic.TopicController;

public class TopicNameController extends AbstractController {

	private TypedController typed;
	private ScopedController scoped;
	private ReifiableController reifiable;
	private TMObjectController tmobject;
	private TopicController topics;

	@Override
	protected void init() {
		typed = getController(TypedController.class);
		scoped = getController(ScopedController.class);
		reifiable = getController(ReifiableController.class);
		tmobject = getController(TMObjectController.class);
		topics = getController(TopicController.class);
	}

	public TopicNameIF add(TopicMapIF tm, TopicName name) {
		requireNotNull(name.getTopic(), "topic");
		return add(tm, getController(TopicController.class).resolve(tm, name.getTopic()), name);
	}

	public TopicNameIF add(TopicMapIF tm, TopicIF topic, TopicName name) {
		
		requireNotNull(name.getValue(), "value");

		TopicMapBuilderIF builder = tm.getBuilder();
		
		TopicNameIF result;
		if (name.getType() == null) {
			result = builder.makeTopicName(topic, name.getValue());
		} else {
			TopicIF type = topics.resolve(tm, name.getType());
			result = builder.makeTopicName(topic, type, name.getValue());
		}
		
		// todo: variants?
		
		// ScopedIF
		scoped.setScope(result, name.getScope());
		// ReifiableIF
		reifiable.setReifier(result, name.getReifier());
		// TMObjectIF
		tmobject.setItemIdentifiers(result, name);
		
		return result;
	}

	public void remove(TopicMapIF tm, TopicName name) {
		remove(resolve(tm, name));
	}

	public void remove(TopicNameIF name) {
		name.remove();
	}

	public TopicNameIF change(TopicMapIF topicMap, TopicNameIF result, TopicName name) {
		
		// NameIF
		if ((name.getValue() != null) && (!name.getValue().equals(result.getValue()))) {
			result.setValue(name.getValue());
		}
		
		// TypedIF
		typed.setType(result, name.getType());
		// ScopedIF
		scoped.setScope(result, name.getScope());
		// ReifiableIF
		reifiable.setReifier(result, name.getReifier());
		// TMObjectIF
		tmobject.setItemIdentifiers(result, name);
		
		return result;
	}

	public TopicNameIF resolve(TopicMapIF tm, TopicName name) {
		return tmobject.resolve(tm, name, TopicNameIF.class);
	}
}
