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

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.topicmaps.rest.controller.AbstractController;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;

public class QueryController extends AbstractController {

	@Override
	protected void init() {
		// no-op
	}

	public QueryResultIF query(TopicMapIF topicmap, String language, String query) {
		QueryProcessorIF qp;
		if (language != null) {
			qp = QueryUtils.getQueryProcessor(language, topicmap, topicmap.getStore().getBaseAddress());
		} else {
			qp = QueryUtils.getQueryProcessor(topicmap, topicmap.getStore().getBaseAddress());
		}
		if (qp == null) {
			throw OntopiaRestErrors.UNKNOWN_QUERY_LANGUAGE.build(language);
		}
		try {
			return qp.execute(query, getOntopia().getDeclarationContext(topicmap));
		} catch (InvalidQueryException iqe) {
			throw OntopiaRestErrors.INVALID_QUERY.build(iqe.getMessage());
		}
	}
}
