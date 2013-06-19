
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
