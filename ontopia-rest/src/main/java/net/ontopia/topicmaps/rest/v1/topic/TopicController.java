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
import java.util.HashSet;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.rest.controller.AbstractController;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.Topic;
import net.ontopia.topicmaps.rest.v1.TMObjectController;
import org.apache.commons.collections4.CollectionUtils;

public class TopicController extends AbstractController {

	private TMObjectController tmobject;

	@Override
	protected void init() {
		tmobject = getController(TMObjectController.class);
	}

	// todo: somehow make use of ontopia.resolver?
	public TopicIF resolve(TopicMapIF tm, Topic topic) {
		TMObjectIF resolved = tmobject.resolve(tm, topic.getObjectId(), topic.getItemIdentifiers());
		if (resolved != null) {
			if (resolved instanceof TopicIF) {
				return (TopicIF) resolved;
			} else {
				throw OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE.build("TopicIF", resolved);
			}
		}
		
		// SI
		if (!CollectionUtils.isEmpty(topic.getSubjectIdentifiers())) {
			for (URILocator si : topic.getSubjectIdentifiers()) {
				TopicIF t = tm.getTopicBySubjectIdentifier(si);
				if (t != null) {
					return t;
				}
			}
		}
		
		// SL
		if (!CollectionUtils.isEmpty(topic.getSubjectLocators())) {
			for (URILocator sl : topic.getSubjectLocators()) {
				TopicIF t = tm.getTopicBySubjectLocator(sl);
				if (t != null) {
					return t;
				}
			}
		}
		
		throw OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL.build("TopicIF");
	}

	public TopicIF add(TopicMapIF tm, Topic topic) {
		return add(tm, null, topic);
	}
	
	public TopicIF add(TopicMapIF tm, TopicIF type, Topic topic) {
		TopicMapBuilderIF builder = tm.getBuilder();
		
		TopicIF result;
		if (type == null) {
			result = builder.makeTopic();
		} else {
			result = builder.makeTopic(type);
		}
		
		// TopicIF
		setSubjectIdentifiers(result, topic);
		setSubjectLocators(result, topic);
		setTypes(result, topic);
		
		// todo: names
		// todo: occurrences
		// todo: roles? can we even?
		
		// TMObjectIF
		tmobject.setItemIdentifiers(result, topic);
		
		return result;
	}
	
	public void remove(TopicMapIF tm, Topic topic) {
		remove(resolve(tm, topic));
	}
	
	public void remove(TopicIF topic) {
		topic.remove();
	}
	
	public TopicIF change(TopicMapIF tm, Topic topic) {
		TopicIF result = resolve(tm, topic);
		
		// TopicIF
		setSubjectIdentifiers(result, topic);
		setSubjectLocators(result, topic);
		setTypes(result, topic);
		
		// todo: names?
		// todo: occurrences?
		// todo: roles? can we even?
		
		// TMObjectIF
		tmobject.setItemIdentifiers(result, topic);
		
		return result;
	}
	
	public void setSubjectIdentifiers(TopicIF object, Topic pojo) {
		if (pojo.getSubjectIdentifiers() != null) {
			Collection<LocatorIF> toRemove = CollectionUtils.subtract(object.getSubjectIdentifiers(), pojo.getSubjectIdentifiers());
			for (LocatorIF si : pojo.getSubjectIdentifiers()) {
				object.addSubjectIdentifier(si);
			}
			for (LocatorIF si : toRemove) {
				object.removeSubjectIdentifier(si);
			}
		}
	}

	public void setSubjectLocators(TopicIF object, Topic pojo) {
		if (pojo.getSubjectLocators() != null) {
			Collection<LocatorIF> toRemove = CollectionUtils.subtract(object.getSubjectLocators(), pojo.getSubjectLocators());
			for (LocatorIF sl : pojo.getSubjectLocators()) {
				object.addSubjectLocator(sl);
			}
			for (LocatorIF sl : toRemove) {
				object.removeSubjectLocator(sl);
			}
		}
	}

	public void setTypes(TopicIF object, Topic pojo) {
		if (pojo.getTypes() != null) {
			Collection<TopicIF> newTypes = new HashSet<>(pojo.getTypes().size());
			for (Topic t : pojo.getTypes()) {
				TopicIF resolved = resolve(object.getTopicMap(), t);
				newTypes.add(resolved);
				object.addType(object);
			}
			
			for (TopicIF remove : CollectionUtils.subtract(object.getTypes(), newTypes)) {
				object.removeType(remove);
			}
		}
	}

	public void addType(TopicIF topic, Topic type) {
		topic.addType(resolve(topic.getTopicMap(), type));
	}

	public void removeType(TopicIF topic, Topic type) {
		topic.removeType(resolve(topic.getTopicMap(), type));
	}
}
