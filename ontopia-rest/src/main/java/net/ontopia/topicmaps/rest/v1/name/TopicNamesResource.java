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

package net.ontopia.topicmaps.rest.v1.name;

import java.util.Collection;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.rest.model.FetchOptions;
import net.ontopia.topicmaps.rest.resources.AbstractTransactionalResource;
import net.ontopia.topicmaps.rest.resources.Parameters;
import org.restlet.resource.Get;

public class TopicNamesResource extends AbstractTransactionalResource {
	
	@Get
	public Collection<TopicNameIF> getTopicNames(FetchOptions options) {
		TopicIF topic = getRequestParameter(TopicIF.class, Parameters.ID.toString(), true);
		TopicIF type = getRequestParameter(Parameters.TYPE, true);
		
		if (topic != null) {
			if (type == null) {
				return topic.getTopicNames();
			} else {
				return topic.getTopicNamesByType(type);
			}
		} else {
			return getIndex(ClassInstanceIndexIF.class).getTopicNames(type);
		}
	}
}
