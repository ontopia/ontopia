/*
 * #!
 * Ontopia Rest
 * #-
 * Copyright (C) 2001 - 2017 The Ontopia Project
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

package net.ontopia.topicmaps.rest.v1.query;

import java.io.IOException;
import java.io.OutputStream;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.QueryResultIterator;
import net.ontopia.topicmaps.rest.converters.jackson.JacksonRepresentationImpl;
import net.ontopia.topicmaps.rest.resources.AbstractTransactionalResource;
import net.ontopia.topicmaps.rest.resources.Parameters;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;

public class QueryResource extends AbstractTransactionalResource {

	@Post
	public Representation query(String query) {
		String language = Parameters.LANGUAGE.optional(this);
		final QueryResultIF result = getController(QueryController.class).query(getTopicMap(), language, query);
		
		return new JacksonRepresentationImpl<QueryResultIterator>(new QueryResultIterator(result)) {
			@Override
			public void write(OutputStream outputStream) throws IOException {
				try {
					super.write(outputStream);
				} finally {
					result.close();
				}
			}
		};
	}
}
