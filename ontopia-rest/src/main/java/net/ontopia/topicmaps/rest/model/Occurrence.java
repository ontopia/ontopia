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
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.utils.PSI;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Occurrence extends Scoped {

	private Topic topic;
	private Topic type;
	private URILocator dataType;
	private String value;

	public Occurrence() {
	}

	public Occurrence(String objectId) {
		super(objectId);
	}

	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
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

	@JsonIgnore
	public void setLocator(LocatorIF locator) {
		if (locator != null) {
			setValue(locator.getAddress());
			setDatatype(PSI.getXSDURI());
		}
	}
	
	public LocatorIF getLocator() {
		if (DataTypes.TYPE_URI.equals(dataType)) {
			return URILocator.create(value);
		}
		return null;
	}
	
	public int getLength() {
		return value != null ? value.length() : 0;
	}

	public Topic getType() {
		return type;
	}

	public void setType(Topic type) {
		this.type = type;
	}
}
