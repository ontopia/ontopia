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
package net.ontopia.topicmaps.rest.v1.scoped;

import java.util.Collection;
import java.util.Collections;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.rest.model.Topic;
import net.ontopia.topicmaps.rest.resources.AbstractTMObjectResource;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;

public class ScopedResource extends AbstractTMObjectResource<ScopedIF> {

	public ScopedResource() {
		super(ScopedIF.class);
	}
	
	@Get
	public Collection<TopicIF> getScope() {
		return resolve().getScope();
	}
	
	@Put
	public void addScope(Topic scope) {
		addScope(Collections.singletonList(scope));
	}

	@Put
	public void addScope(Collection<Topic> scopes) {
		for (Topic scope : scopes) {
			getController(ScopedController.class).add(resolve(), scope);
		}
		store.commit();
	}
	
	@Delete
	public void removeScope(Topic scope) {
		removeScope(Collections.singletonList(scope));
	}

	@Delete
	public void removeScope(Collection<Topic> scopes) {
		for (Topic scope : scopes) {
			getController(ScopedController.class).remove(resolve(), scope);
		}
		store.commit();
	}
}
