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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Collection;
import java.util.HashSet;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Association extends Scoped {

	private Collection<AssociationRole> roles = new HashSet<>();
	private Topic type;

	public Association() {
	}

	public Association(String objectId) {
		super(objectId);
	}

	public Collection<AssociationRole> getRoles() {
		return roles;
	}

	public void setRoles(Collection<AssociationRole> roles) {
		this.roles = roles;
	}

	public Topic getType() {
		return type;
	}

	public void setType(Topic type) {
		this.type = type;
	}
}
