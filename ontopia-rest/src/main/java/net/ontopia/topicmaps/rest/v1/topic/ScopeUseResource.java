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

import java.util.HashMap;
import java.util.Map;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.index.ScopeIndexIF;
import net.ontopia.topicmaps.rest.model.FetchOptions;
import net.ontopia.topicmaps.rest.resources.AbstractTMObjectResource;
import org.restlet.resource.Get;

public class ScopeUseResource extends AbstractTMObjectResource<TopicIF> {
	
	public ScopeUseResource() {
		super(TopicIF.class);
	}

	@Get
	public Map<String, Object> getScopeUse(FetchOptions options) {
		TopicIF topic = resolve(options);
		
		ScopeIndexIF index = getIndex(ScopeIndexIF.class);
		
		boolean usedAsAssociationTheme = index.usedAsAssociationTheme(topic);
		boolean usedAsOccurrenceTheme = index.usedAsOccurrenceTheme(topic);
		boolean usedAsTopicNameTheme = index.usedAsTopicNameTheme(topic);
		boolean usedAsVariantTheme = index.usedAsVariantTheme(topic);

		Map<String, Object> result = new HashMap<>();
		result.put("usedAsAssociationTheme", usedAsAssociationTheme);
		result.put("usedAsOccurrenceTheme", usedAsOccurrenceTheme);
		result.put("usedAsTopicNameTheme", usedAsTopicNameTheme);
		result.put("usedAsVariantTheme", usedAsVariantTheme);
		result.put("usedAsTheme", 
				usedAsAssociationTheme ||
				usedAsOccurrenceTheme ||
				usedAsTopicNameTheme ||
				usedAsVariantTheme
		);
		
		return result;
	}
}
