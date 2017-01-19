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

package net.ontopia.topicmaps.rest.v1.occurrence;

import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.rest.controller.AbstractController;
import net.ontopia.topicmaps.rest.model.Occurrence;
import net.ontopia.topicmaps.rest.v1.ReifiableController;
import net.ontopia.topicmaps.rest.v1.TMObjectController;
import net.ontopia.topicmaps.rest.v1.TypedController;
import net.ontopia.topicmaps.rest.v1.scoped.ScopedController;
import net.ontopia.topicmaps.rest.v1.topic.TopicController;

public class OccurrenceController extends AbstractController {

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

	public OccurrenceIF add(TopicMapIF tm, Occurrence occurrence) {
		requireNotNull(occurrence.getTopic(), "topic");
		return add(tm, topics.resolve(tm, occurrence.getTopic()), occurrence);
	}
	
	public OccurrenceIF add(TopicMapIF tm, TopicIF topic, Occurrence occurrence) {
		
		requireNotNull(occurrence.getValue(), "value");
		requireNotNull(occurrence.getType(), "type");
		
		TopicMapBuilderIF builder = tm.getBuilder();
		TopicIF type = topics.resolve(tm, occurrence.getType());
		
		OccurrenceIF result;
		if (occurrence.getDataType() == null) {
			result = builder.makeOccurrence(topic, type, occurrence.getValue());
		} else {
			result = builder.makeOccurrence(topic, type, occurrence.getValue(), occurrence.getDataType());
		}
		
		// ScopedIF
		scoped.setScope(result, occurrence.getScope());
		// ReifiableIF
		reifiable.setReifier(result, occurrence.getReifier());
		// TMObjectIF
		tmobject.setItemIdentifiers(result, occurrence);
		
		return result;
	}
	
	public void remove(TopicMapIF tm, Occurrence occurrence) {
		remove(resolve(tm, occurrence));
	}

	public void remove(OccurrenceIF occurrence) {
		occurrence.remove();
	}
	
	public OccurrenceIF change(TopicMapIF tm, Occurrence occurrence) {
		OccurrenceIF result = resolve(tm, occurrence);
		
		// OccurrenceIF
		if ((occurrence.getValue() != null) && (!occurrence.getValue().equals(result.getValue()))) {
			result.setValue(occurrence.getValue());
		}
		if ((occurrence.getDataType() != null) && (!occurrence.getDataType().equals(result.getDataType()))) {
			result.setValue(result.getValue(), occurrence.getDataType()); // no shortcut
		}
		
		// TypedIF
		typed.setType(result, occurrence.getType());
		// ScopedIF
		scoped.setScope(result, occurrence.getScope());
		// ReifiableIF
		reifiable.setReifier(result, occurrence.getReifier());
		// TMObjectIF
		tmobject.setItemIdentifiers(result, occurrence);
		
		return result;
	}
	
	public OccurrenceIF resolve(TopicMapIF tm, Occurrence occurrence) {
		return tmobject.resolve(tm, occurrence, OccurrenceIF.class);
	}
}
