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

package net.ontopia.topicmaps.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collection;
import java.util.HashSet;
import net.ontopia.infoset.impl.basic.URILocator;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Topic extends TMObject {

	private Collection<URILocator> subjectLocators = new HashSet<>();
	private Collection<URILocator> subjectIdentifiers = new HashSet<>();
	private Collection<Topic> types = new HashSet<>();
	
	@JsonProperty("names")
	private Collection<TopicName> topicNames = new HashSet<>();
	private Collection<Occurrence> occurrences = new HashSet<>();
	private Collection<AssociationRole> roles = new HashSet<>();
	
	@JsonIgnore
	private Reifiable reified;

	public Topic() {
	}

	public Topic(String objectId) {
		super(objectId);
	}

	public Collection<URILocator> getSubjectLocators() {
		return subjectLocators;
	}

	public Collection<URILocator> getSubjectIdentifiers() {
		return subjectIdentifiers;
	}

	public Collection<Topic> getTypes() {
		return types;
	}

	public Collection<TopicName> getTopicNames() {
		return topicNames;
	}

	public Collection<Occurrence> getOccurrences() {
		return occurrences;
	}

	public Collection<AssociationRole> getRoles() {
		return roles;
	}

	@JsonProperty("reified")
	public Reifiable getReified() {
		return reified;
	}
}
