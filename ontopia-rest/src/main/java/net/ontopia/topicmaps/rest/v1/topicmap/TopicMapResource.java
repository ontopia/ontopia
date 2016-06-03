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

package net.ontopia.topicmaps.rest.v1.topicmap;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Collection;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.rest.model.TopicMap;
import net.ontopia.topicmaps.rest.resources.AbstractTransactionalResource;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

public class TopicMapResource extends AbstractTransactionalResource {

	@Get
	public TopicMapWrapper getTopicMapInfo() {
		return new TopicMapWrapper(getTopicMap(), getTopicMapReference());
	}
	
	@Post
	public TopicMapWrapper changeTopicMap(TopicMap topicmap) {
		getController(TopicMapController.class).change(getTopicMapReference(), getTopicMap(), topicmap);
		store.commit();
		return getTopicMapInfo();
	}
	
	@Delete
	public void removeTopicMap() {
		store.close();
		getController(TopicMapController.class).remove(getTopicMapReference());
	}
	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public static class TopicMapWrapper {

		private final TopicMapReferenceIF reference;
		private final TopicMapIF tm;

		public TopicMapWrapper(TopicMapIF tm, TopicMapReferenceIF reference) {
			this.tm = tm;
			this.reference = reference;
		}
		
		public String getObjectId() {
			return tm.getObjectId();
		}
		
		public TopicIF getReifier() {
			return tm.getReifier();
		}

		public Collection<LocatorIF> getItemIdentifiers() {
			return tm.getItemIdentifiers();
		}
		
		public TopicMapReferenceIF getReference() {
			return reference;
		}
	}
}
