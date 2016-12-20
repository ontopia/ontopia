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

import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;

import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.io.IOException;

import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.utils.TopicStringifiers;
import net.ontopia.topicmaps.impl.utils.TMRevitalizer;
import net.ontopia.topicmaps.utils.MergeUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopicMapPreferences extends AbstractPreferences {
	
	private static final Logger logger = LoggerFactory.getLogger(TopicMapPreferences.class);

	protected HashMap<String, TopicMapPreferences> children = new HashMap<String, TopicMapPreferences>();
	protected HashMap<String, String> properties = new HashMap<String, String>();

	protected final TopicMapPreferencesFactory factory;
	protected TopicMapReferenceIF topicMapReference;
	protected TopicIF topic;
	private final boolean isUserNode;
	
	private HashSet<String> propertiesStored   = new HashSet<String>();
	private HashSet<String> propertiesAdded    = new HashSet<String>();
	private HashSet<String> propertiesModified = new HashSet<String>();
	private HashSet<String> propertiesRemoved  = new HashSet<String>();

	private enum PropertyStatus { NONE, ADDED, MODIFIED, REMOVED; }
	private synchronized void handlePropertyStatusChange(String key, PropertyStatus newStatus) {
		propertiesAdded.remove(key);
		propertiesModified.remove(key);
		propertiesRemoved.remove(key);
		switch (newStatus) {
			case ADDED:    propertiesAdded.add(key);    break;
			case MODIFIED: propertiesModified.add(key); break;
			case REMOVED:  propertiesRemoved.add(key);  break;
		}
	}


	public static TopicMapPreferences createSystemRoot(TopicMapReferenceIF topicMapReference, TopicMapPreferencesFactory factory) {
		return new TopicMapPreferences(false, topicMapReference, factory);
	}
	public static TopicMapPreferences createUserRoot(TopicMapReferenceIF topicMapReference, TopicMapPreferencesFactory factory) {
		return new TopicMapPreferences(true, topicMapReference, factory);
	}

	// constructor to create root Preference, boolean argument determines wether systemRoot or userRoot is created
	private TopicMapPreferences(boolean isUserNode, TopicMapReferenceIF topicMapReference, TopicMapPreferencesFactory factory) {
		super(null, "");
		this.isUserNode = isUserNode;
		this.topicMapReference = topicMapReference;
		this.factory = factory;
		newNode = true;
		try {
			this.sync(); // load from topic map upon creation
		} catch (BackingStoreException e) {
			logger.error("BackingStoreException: ", e);
		}
	}

	// constructor to create non-root Preference, with parent and name
	private TopicMapPreferences(TopicMapPreferences parent, String name) {
		super(parent, name);
		this.isUserNode = parent.isUserNode;
		this.topicMapReference = parent.topicMapReference;
		this.factory = parent.factory;
		newNode = true;
	}


	// --- AbstractPreferences abstract method implementations

	@Override
	public boolean isUserNode() {
		return isUserNode;
	}

	public String getSpi(String key) {
		return properties.get(key);
	}

	public void putSpi(String key, String value) {
		properties.put(key, value);
		handlePropertyStatusChange(key, propertiesStored.contains(key) ? PropertyStatus.MODIFIED : PropertyStatus.ADDED);
		propertiesStored.add(key);
	}

	public void removeSpi(String key) {
		properties.remove(key);
		handlePropertyStatusChange(key, propertiesStored.contains(key) ? PropertyStatus.REMOVED : PropertyStatus.NONE);
		propertiesStored.remove(key);
	}

	public TopicMapPreferences childSpi(String name) {
		if (children.get(name) == null) {
			children.put(name, new TopicMapPreferences(this, name));
		}
		return children.get(name);
	}

	public void removeNodeSpi() {
		children.clear();
		properties.clear();
		((TopicMapPreferences) parent()).children.remove(this.name());
	}

	public String[] keysSpi() {
		return properties.keySet().toArray(new String[properties.size()]);
	}

	public String[] childrenNamesSpi() {
		return children.keySet().toArray(new String[children.size()]);
	}

	public synchronized void syncSpi() throws BackingStoreException {

		flushSpi();
		
		if (isRemoved()) {
			return;
		}
		
		TopicMapStoreIF store = null;
		try {
			store = createStore(topicMapReference, true); // readonly, we're only loading here
			TopicMapIF topicmap = store.getTopicMap();
		
			for (TopicIF childTopic : fetchChildren(topicmap, topic)) {
				String childName = TopicStringifiers.toString(childTopic);
				TopicMapPreferences childNode = (TopicMapPreferences) this.node(childName);
				if ((childNode.topic != null) && (!MergeUtils.shouldMerge(childNode.topic, childTopic))) {
					// if there already is a topic connectied to this node, different then childTopic, merge them
					logger.warn("Duplicate topics found, merging them into " + absolutePath() + "/" + childName);
					mergeTopics(childNode.topic, childTopic);
				} else {
					childNode.topic = childTopic;
				}
			}
		
			propertiesStored.clear();
			for (OccurrenceIF property : fetchProperties(topicmap)) {
				String propertyName = TopicStringifiers.toString(property.getType());
				String propertyValue = property.getValue();
				properties.put(propertyName, propertyValue);
				propertiesStored.add(propertyName);
			}
		
		} finally {
			if (store != null) {
				store.close();
			}
		}

	}

	public synchronized void flushSpi() throws BackingStoreException {
		
		logger.debug("Flushing " + this);
		
		TopicMapStoreIF store = null;
		try {
			store = createStore(topicMapReference, false); // read-write
			TopicMapIF topicmap = store.getTopicMap();
			TopicMapPreferencesOntology.loadOntology(topicmap); // load ontology as it might not be present
			
			if (isRemoved()) {
				if (topic != null) {
					logger.debug("Removing " + this);
					removeTopic(topicmap, topic);
				}
			} else {
				topic = fetchTopic(topicmap);
				for (String key : propertiesAdded) {
					logger.debug("Adding property '" + key + "' = '" + properties.get(key) + "'");
					TopicIF propertyType = fetchPropertyType(key, topicmap);
					topicmap.getBuilder().makeOccurrence(topic, propertyType, properties.get(key));
				}
				for (String key : propertiesModified) {
					logger.debug("Modifying property '" + key + "' = '" + properties.get(key) + "'");
					TopicIF propertyType = fetchPropertyType(key, topicmap);
					for (OccurrenceIF occurrence : topic.getOccurrences()) {
						if (propertyType.equals(occurrence.getType())) {
							occurrence.setValue(properties.get(key));
						}
					}
				}
				for (String key : propertiesRemoved) {
					logger.debug("Removing property '" + key + "'");
					TopicIF propertyType = fetchPropertyType(key, topicmap);
					OccurrenceIF[] occurrencesArray = topic.getOccurrences().toArray(new OccurrenceIF[0]);
					for (int i = 0; i < occurrencesArray.length; i++) {
						OccurrenceIF occurrence = occurrencesArray[i];
						if (propertyType.equals(occurrence.getType())) {
							occurrence.remove();
						}
					}
				}
				propertiesAdded.clear();
				propertiesModified.clear();
				propertiesRemoved.clear();

			}
			store.commit();
		} finally {
			if (store != null) {
				store.close();
			}
		}
	}



	protected TopicMapStoreIF createStore(TopicMapReferenceIF topicMapReference, boolean readonly) throws BackingStoreException {
		try {
			return topicMapReference.createStore(readonly);
		} catch (IOException e) {
			throw new BackingStoreException(e);
		}
	}

	protected Set<TopicIF> fetchChildren(TopicMapIF topicmap, TopicIF topic) {
		TopicIF parentChildType = TopicMapPreferencesOntology.PARENTCHILD.t(topicmap);
		TopicIF parentType = TopicMapPreferencesOntology.PARENT.t(topicmap);
		TopicIF childType = TopicMapPreferencesOntology.CHILD.t(topicmap);
		TopicIF preferenceType = TopicMapPreferencesOntology.PREFERENCE.t(topicmap);

		Set<TopicIF> result = new HashSet<TopicIF>();
		for (AssociationRoleIF role1 : revitalize(topic, topicmap).getRolesByType(parentType, parentChildType)) {
			for (AssociationRoleIF role2 : role1.getAssociation().getRolesByType(childType)) {
				TopicIF child = role2.getPlayer();
				if (child.getTypes().contains(preferenceType)) {
					result.add(child);
				}
			}
		}
		return result;
	}
	
	protected Set<OccurrenceIF> fetchProperties(TopicMapIF topicmap) {
		TopicIF preferencePropertyType = TopicMapPreferencesOntology.PREFERENCEPROPERTY.t(topicmap);
		Set<OccurrenceIF> result = new HashSet<OccurrenceIF>();
		for (OccurrenceIF property : revitalize(topic, topicmap).getOccurrences()) {
			if (property.getType().getTypes().contains(preferencePropertyType)) {
				result.add(property);
			}
		}
		return result;
	}
	
	protected TopicIF fetchPropertyType(String key, TopicMapIF topicmap) {
		TopicIF preferencePropertyType = TopicMapPreferencesOntology.PREFERENCEPROPERTY.t(topicmap);
		
		// try to fetch existing propertyType from topic map
		ClassInstanceIndexIF index = (ClassInstanceIndexIF) topicmap.getIndex(ClassInstanceIndexIF.class.getName());
		for (TopicIF propertyType : index.getTopics(preferencePropertyType)) {
			if (key.equals(TopicStringifiers.toString(propertyType))) {
				return revitalize(propertyType, topicmap);
			}
		}

		// propertyType not found in topic map, create it now
		TopicMapBuilderIF builder = topicmap.getBuilder();
		TopicIF propertyType = builder.makeTopic(preferencePropertyType);
		builder.makeTopicName(propertyType, key);
		return propertyType;
	}

	protected TopicIF fetchTopic(TopicMapIF topicmap) throws BackingStoreException {
		return (topic != null)
			? revitalize(topic, topicmap)
			: (parent() != null) 
				? createTopic(topicmap) 
				: (isUserNode) 
					? factory.createUserRootTopic(topicmap)
					: factory.createSystemRootTopic(topicmap);
	}
	
	protected TopicIF createTopic(TopicMapIF topicmap) throws BackingStoreException {
		TopicIF preferenceType = TopicMapPreferencesOntology.PREFERENCE.t(topicmap);
		TopicIF parentChildType = TopicMapPreferencesOntology.PARENTCHILD.t(topicmap);
		TopicIF parentType = TopicMapPreferencesOntology.PARENT.t(topicmap);
		TopicIF childType = TopicMapPreferencesOntology.CHILD.t(topicmap);

		TopicMapBuilderIF builder = topicmap.getBuilder();
		TopicIF topic = builder.makeTopic(preferenceType);
		builder.makeTopicName(topic, this.name());
		AssociationIF a = builder.makeAssociation(parentChildType, childType, topic);
		TopicIF parentTopic = ((TopicMapPreferences) parent()).fetchTopic(topicmap);
		builder.makeAssociationRole(a, parentType, parentTopic);
		return topic;
	}
	
	protected void removeTopic(TopicMapIF topicmap, TopicIF topic) throws BackingStoreException {
		// specs: "If this method is invoked on a node that has been removed with the removeNode() method, 
		//        flushSpi() is invoked on this node, but not on others."
		// so, first remove children, then topic itself
		logger.debug("Removing topic " + TopicStringifiers.toString(topic));
		for (TopicIF child : fetchChildren(topicmap, topic)) {
			removeTopic(topicmap, child);
		}
		topic.remove();
	}
	
	protected void mergeTopics(TopicIF targetTopic, TopicIF sourceTopic) throws BackingStoreException {
		TopicMapStoreIF store = null;
		try {
			store = createStore(topicMapReference, false);
			TopicMapIF topicmap = store.getTopicMap();
			TopicIF currentTopic = revitalize(targetTopic, topicmap);
			TopicIF duplicateTopic = revitalize(sourceTopic, topicmap);
			MergeUtils.mergeInto(currentTopic, duplicateTopic);
			store.commit();
		} finally {
			if (store != null) {
				store.close();
			}
		}
	}
	
	protected TopicIF revitalize(TopicIF topic, TopicMapIF topicmap) {
		return (TopicIF) new TMRevitalizer(topicmap).revitalize(topic);
	}
	
}
