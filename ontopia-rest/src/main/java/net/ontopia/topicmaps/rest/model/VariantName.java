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
import java.net.URISyntaxException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;

@JsonIgnoreProperties(ignoreUnknown = true, value = {"topic"})
public class VariantName extends Scoped {

	private TopicName topicName;
	private URILocator dataType;
	private String value;

	public VariantName() {
	}

	public VariantName(String objectId) {
		super(objectId);
	}

	public TopicName getTopicName() {
		return topicName;
	}

	public void setTopicName(TopicName topicName) {
		this.topicName = topicName;
	}

	public URILocator getDataType() {
		return dataType;
	}

	@JsonIgnore
	public void setDatatype(URILocator datatype) {
		this.dataType = datatype;
	}

	public void setDatatype(LocatorIF datatype) throws URISyntaxException {
		this.dataType = new URILocator(datatype.getAddress());
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public long getLength() {
		return value == null ? 0 : value.length();
	}

	public Topic getTopic() {
		return topicName == null ? null : topicName.getTopic();
	}
}
