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
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.index.ScopeIndexIF;
import net.ontopia.topicmaps.rest.model.FetchOptions;
import net.ontopia.topicmaps.rest.model.mixin.MFlatTopic;
import net.ontopia.topicmaps.rest.resources.AbstractTransactionalResource;
import org.restlet.data.Status;
import org.restlet.resource.Get;

public class ScopesResource extends AbstractTransactionalResource {
	
	@Get
	public Collection<TopicIF> getScopes(FetchOptions options) {
		addMixInAnnotations(TopicIF.class, MFlatTopic.class);

		ScopeIndexIF index = getIndex(ScopeIndexIF.class);

		switch (getAttribute("type").toUpperCase()) {
			case "ASSOCIATIONS": return index.getAssociationThemes();
			case "OCCURRENCES": return index.getOccurrenceThemes();
			case "NAMES": return index.getTopicNameThemes();
			case "VARIANTS": return index.getVariantThemes();
			default:
				setStatus(Status.CLIENT_ERROR_NOT_FOUND);
				return null;
		}
	}
}
