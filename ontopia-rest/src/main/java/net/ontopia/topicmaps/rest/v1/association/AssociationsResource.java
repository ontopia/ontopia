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

package net.ontopia.topicmaps.rest.v1.association;

import java.util.Collection;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.rest.resources.AbstractTransactionalResource;
import net.ontopia.topicmaps.rest.resources.Parameters;
import org.restlet.resource.Get;

public class AssociationsResource extends AbstractTransactionalResource {
	
	@Get
	public Collection<AssociationIF> getAssociations() {
		TopicIF topic = Parameters.ID.withExpected(TopicIF.class).optional(this);
		TopicIF type = Parameters.TYPE.optional(this);
		
		if (topic == null) {
			if (type != null) {
				return getIndex(ClassInstanceIndexIF.class).getAssociations(type);
			} else {
				return store.getTopicMap().getAssociations();
			}
		} else {
			if (type == null) {
				return topic.getAssociations();
			} else {
				return topic.getAssociationsByType(type);
			}
		}
	}
}
