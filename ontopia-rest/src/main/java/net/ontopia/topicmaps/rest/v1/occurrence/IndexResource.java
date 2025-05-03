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

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.index.OccurrenceIndexIF;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.resources.AbstractTransactionalResource;
import org.apache.commons.collections4.IteratorUtils;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.resource.Post;

public class IndexResource extends AbstractTransactionalResource {
	private static final String TYPE_ERROR_MESSAGE = "Expected type one of value, prefix, gte, lte";

	@Post("txt:json")
	public Collection<?> getOccurrences(String value) {
		return getOccurrences(value, null);
	}
	
	@Post("json:json")
	public Collection<?> getOccurrences(Map<String, String> data) {
		if (data == null) {
			throw OntopiaRestErrors.EMPTY_ENTITY.build();
		}
		return getOccurrences(data.get("value"), data.get("datatype"));
	}
	
	@Post("form:json")
	public Collection<?> getOccurrences(Form data) {
		if (data == null) {
			throw OntopiaRestErrors.EMPTY_ENTITY.build();
		}
		return getOccurrences(data.getFirstValue("value"), data.getFirstValue("datatype"));
	}
	
	protected Collection<?> getOccurrences(String value, String datatype) {
		OccurrenceIndexIF index = getIndex(OccurrenceIndexIF.class);
		
		try {
			switch (getAttribute("type").toUpperCase()) {
				case "VALUE":
					if (datatype == null) { return index.getOccurrences(value); }
          else { return index.getOccurrences(value, new URILocator(datatype)); }
				case "PREFIX":
					if (value == null) { throw OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_NULL.build("value", "String"); }
					if (datatype == null) { return index.getOccurrencesByPrefix(value); }
          else { return index.getOccurrencesByPrefix(value, new URILocator(datatype)); }
				case "GTE": return IteratorUtils.toList(index.getValuesGreaterThanOrEqual(value));
				case "LTE": return IteratorUtils.toList(index.getValuesSmallerThanOrEqual(value));

				default: 
					setStatus(Status.CLIENT_ERROR_NOT_FOUND, TYPE_ERROR_MESSAGE);
					return null;
			}
		} catch (URISyntaxException mufe) {
			throw OntopiaRestErrors.MALFORMED_LOCATOR.build(mufe, datatype);
		}
	}
}
