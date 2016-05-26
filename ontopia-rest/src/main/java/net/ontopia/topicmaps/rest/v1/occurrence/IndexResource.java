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

import java.util.Collection;
import java.util.Map;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.index.OccurrenceIndexIF;
import net.ontopia.topicmaps.rest.resources.AbstractTransactionalResource;
import net.ontopia.utils.IteratorCollection;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

// todo: how to add FetchOptions here?
public class IndexResource extends AbstractTransactionalResource {
	
	@Get("text:")
	@Post("text:")
	public Collection<?> getOccurrences(String value) {
		OccurrenceIndexIF index = getIndex(OccurrenceIndexIF.class);
		
		switch (getAttribute("type").toUpperCase()) {
			case "VALUE": return index.getOccurrences(value);
			case "PREFIX": return index.getOccurrencesByPrefix(value);
			case "GTE": return new IteratorCollection<>(index.getValuesGreaterThanOrEqual(value));
			case "LTE": return new IteratorCollection<>(index.getValuesSmallerThanOrEqual(value));
			
			default: 
				setStatus(Status.CLIENT_ERROR_NOT_FOUND);
				return null;
		}
	}
	
	@Get("json|form:")
	@Post("json|form:")
	public Collection<?> getVariantNames(Map<String, String> values) {
		String value = values.get("value");
		LocatorIF datatype = URILocator.create(values.get("datatype"));
		
		OccurrenceIndexIF index = getIndex(OccurrenceIndexIF.class);
		
		switch (getAttribute("type").toUpperCase()) {
			case "VALUE": return index.getOccurrences(value, datatype);
			case "PREFIX": return index.getOccurrencesByPrefix(value, datatype);
			case "GTE": return new IteratorCollection<>(index.getValuesGreaterThanOrEqual(value));
			case "LTE": return new IteratorCollection<>(index.getValuesSmallerThanOrEqual(value));
			
			default: 
				setStatus(Status.CLIENT_ERROR_NOT_FOUND);
				return null;
		}
	}
}
