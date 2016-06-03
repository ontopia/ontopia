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

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.utils.PSI;

public class Occurrence extends Scoped {

	private Topic topic;
	private Topic type;
	private URILocator datatype;
	private String value;

	public Topic getTopic() {
		return topic;
	}

	public URILocator getDataType() {
		return datatype;
	}

	public void setDatatype(URILocator datatype) {
		this.datatype = datatype;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setLocator(LocatorIF locator) {
		setValue(locator.getAddress());
		setDatatype(PSI.getXSDURI());
	}

	public Topic getType() {
		return type;
	}
}
