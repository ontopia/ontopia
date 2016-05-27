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
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.rest.resources.AbstractTMObjectResource;
import org.restlet.resource.Get;

public class TopicUseResource extends AbstractTMObjectResource<TopicIF> {

	public TopicUseResource() {
		super(TopicIF.class);
	}
	
	@Get
	public Map<String, Object> getTopicUse() {
		TopicIF topic = resolve();
		
		ClassInstanceIndexIF index = getIndex(ClassInstanceIndexIF.class);
		
		boolean usedAsAssociationRoleType = index.usedAsAssociationRoleType(topic);
		boolean usedAsAssociationType = index.usedAsAssociationType(topic);
		boolean usedAsOccurrenceType = index.usedAsOccurrenceType(topic);
		boolean usedAsTopicNameType = index.usedAsTopicNameType(topic);
		boolean usedAsTopicType = index.usedAsTopicType(topic);

		Map<String, Object> result = new HashMap<>();
		result.put("usedAsType", index.usedAsType(topic));
		result.put("usedAsAssociationRoleType", usedAsAssociationRoleType);
		result.put("usedAsAssociationType", usedAsAssociationType);
		result.put("usedAsOccurrenceType", usedAsOccurrenceType);
		result.put("usedAsTopicNameType", usedAsTopicNameType);
		result.put("usedAsTopicType", usedAsTopicType);
		result.put("usedAsType", 
				usedAsAssociationRoleType ||
				usedAsAssociationType ||
				usedAsOccurrenceType ||
				usedAsTopicNameType ||
				usedAsTopicType	
		);
		
		return result;
	}
}
