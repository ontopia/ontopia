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

import java.util.Collection;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.utils.CompactHashSet;

public class TMObject {

	private String objectId;
	private Collection<URILocator> itemIdentifiers = new CompactHashSet<>();

	public TMObject() {
	}

	public TMObject(String objectId) {
		this.objectId = objectId;
	}
	
	public String getObjectId() {
		return objectId;
	}

	public Collection<URILocator> getItemIdentifiers() {
		return itemIdentifiers;
	}

	public void setItemIdentifiers(Collection<URILocator> itemIdentifiers) {
		this.itemIdentifiers = itemIdentifiers;
	}
}
