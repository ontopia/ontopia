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

package net.ontopia.topicmaps.rest.v1.topic;

import java.util.Collection;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.index.ScopeIndexIF;
import net.ontopia.topicmaps.rest.resources.AbstractTMObjectResource;
import org.restlet.data.Status;
import org.restlet.resource.Get;

public class ScopedResource extends AbstractTMObjectResource<TopicIF> {

	public ScopedResource() {
		super(TopicIF.class);
	}
	
	@Get
	public Collection<? extends TMObjectIF> getScoped() {
		TopicIF topic = resolve();
		ScopeIndexIF index = getIndex(ScopeIndexIF.class);

		switch (getAttribute("type").toUpperCase()) {
			case "ASSOCIATIONS": return index.getAssociations(topic);
			case "OCCURRENCES": return index.getOccurrences(topic);
			case "NAMES": return index.getTopicNames(topic);
			case "VARIANTS": return index.getVariants(topic);
			default:
				setStatus(Status.CLIENT_ERROR_NOT_FOUND, "Expected type one of associations, occurrences, names, variants");
				return null;
		}
	}
}
