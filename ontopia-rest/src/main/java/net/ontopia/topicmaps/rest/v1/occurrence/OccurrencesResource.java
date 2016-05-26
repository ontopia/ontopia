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

package net.ontopia.topicmaps.rest.v1.occurrence;

import java.util.Collection;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.rest.model.FetchOptions;
import net.ontopia.topicmaps.rest.model.mixin.MOccurrenceWithoutTopic;
import net.ontopia.topicmaps.rest.resources.AbstractTransactionalResource;
import net.ontopia.topicmaps.rest.resources.Parameters;
import org.restlet.resource.Get;

/**
 * Provides a rest point for {@link TopicIF#getOccurrences()}, 
 * {@link TopicIF#getOccurrencesByType(TopicIF)} and 
 * {@link ClassInstanceIndexIF#getOccurrences(TopicIF)}. Only supports GET.
 */
public class OccurrencesResource extends AbstractTransactionalResource {
	
	@Get
	public Collection<OccurrenceIF> getOccurrences(FetchOptions options) {
		addMixInAnnotations(OccurrenceIF.class, MOccurrenceWithoutTopic.class);
		
		TopicIF topic = getRequestParameter(TopicIF.class, Parameters.ID, true);
		TopicIF type = getRequestParameter(Parameters.TYPE, true);

		if (topic != null) {
			if (type != null) {
				return topic.getOccurrencesByType(type);
			} else {
				return topic.getOccurrences();
			}
		} else {
			return getIndex(ClassInstanceIndexIF.class).getOccurrences(type);
		}
	}
}
