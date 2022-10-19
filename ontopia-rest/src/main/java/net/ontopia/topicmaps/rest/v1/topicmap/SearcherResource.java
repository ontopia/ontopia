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

import java.io.IOException;
import java.util.AbstractList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.ontopia.infoset.fulltext.core.SearchResultIF;
import net.ontopia.infoset.fulltext.core.SearcherIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.resources.AbstractTransactionalResource;
import org.apache.commons.lang3.StringUtils;
import org.restlet.resource.Post;

public class SearcherResource extends AbstractTransactionalResource {
	
	@Post
	public List<Map<String, Object>> search(String value) throws IOException {

		if (StringUtils.isEmpty(value)) {
			throw OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_NULL.build("value", "String");
		}

		final TopicMapIF tm = getTopicMap();
		SearcherIF index = getIndex(SearcherIF.class);
		try {
			final SearchResultIF result = index.search(value);
			return new AbstractList<Map<String, Object>>() {
				@Override
				public Map<String, Object> get(int index) {
					try {
						Map<String, Object> object = new HashMap<>();
						object.put("object", tm.getObjectById(result.getDocument(index).getField("object_id").getValue()));
						object.put("class", result.getDocument(index).getField("class").getValue());
						object.put("score", result.getScore(index));
						return object;
					} catch (IOException e) {
						throw OntopiaRestErrors.INDEX_USAGE_ERROR.build(e, "SearcherIF");
					}
				}

				@Override
				public int size() {
					try {
						return result.hits();
					} catch (IOException e) {
						throw OntopiaRestErrors.INDEX_USAGE_ERROR.build(e, "SearcherIF");
					}
				}
			};
		} catch (IOException e) {
			throw OntopiaRestErrors.INDEX_USAGE_ERROR.build(e, "SearcherIF");
		}
	}
}
