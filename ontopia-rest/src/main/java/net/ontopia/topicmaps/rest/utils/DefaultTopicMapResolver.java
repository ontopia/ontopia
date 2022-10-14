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

package net.ontopia.topicmaps.rest.utils;

import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.entry.TopicMapRepositoryIF;
import net.ontopia.topicmaps.entry.TopicMaps;
import net.ontopia.topicmaps.rest.core.TopicMapResolverIF;
import net.ontopia.topicmaps.rest.resources.Parameters;
import org.restlet.Request;

public class DefaultTopicMapResolver implements TopicMapResolverIF {
	private final TopicMapRepositoryIF repository;

	public DefaultTopicMapResolver() {
		repository = TopicMaps.getRepository(); // todo: options etc
	}
	
	@Override
	public TopicMapReferenceIF resolve(Request request) {
		String id = (String) request.getAttributes().get(Parameters.TOPICMAP.getName());
		return repository.getReferenceByKey(id);
	}

	@Override
	public void close() {
		if (repository != null) {
			repository.close();
		}
	}
}
