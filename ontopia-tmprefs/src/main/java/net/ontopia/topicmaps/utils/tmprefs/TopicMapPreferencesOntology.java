/*
 * #!
 * Ontopia TopicMapPreferences
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
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

package net.ontopia.topicmaps.utils.tmprefs;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import java.util.EnumMap;

public enum TopicMapPreferencesOntology {
	
	PREFERENCE("preference"),
	PREFERENCEPROPERTY("preference-property"),
	PARENTCHILD("parent-child"),
	PARENT("parent"),
	CHILD("child"),
	SYSTEMROOT("system-root");

	public static final String PREFIX = "http://psi.ontopia.net/tmprefs/";

	private final String postfix;
	private final URILocator locator;

	private TopicMapPreferencesOntology(String postfix) {
		this.postfix = postfix;
		this.locator = URILocator.create(PREFIX + postfix);
	}
	
	public String getPostfix() {
		return postfix;
	}

	public LocatorIF getLocator() {
		return locator;
	}

	public TopicIF t(TopicMapIF tm) {
		return tm.getTopicBySubjectIdentifier(locator);
	}

	public static EnumMap<TopicMapPreferencesOntology, TopicIF> loadOntology(TopicMapIF topicmap) {
		return loadOntology(topicmap, true);
	}
	public static EnumMap<TopicMapPreferencesOntology, TopicIF> loadOntology(TopicMapIF topicmap, boolean forceCreate) {
		EnumMap<TopicMapPreferencesOntology, TopicIF> ontology = new EnumMap<TopicMapPreferencesOntology, TopicIF>(TopicMapPreferencesOntology.class);
		for (TopicMapPreferencesOntology topic: TopicMapPreferencesOntology.values()) {
			LocatorIF psi = topic.getLocator();
			TopicIF t = topicmap.getTopicBySubjectIdentifier(psi);
			if (forceCreate && (t == null)) {
				t = topicmap.getBuilder().makeTopic();
				topicmap.getBuilder().makeTopicName(t, topic.getPostfix());
				t.addSubjectIdentifier(psi);
			}
			ontology.put(topic, t);
		}
		return ontology;
	}

}
