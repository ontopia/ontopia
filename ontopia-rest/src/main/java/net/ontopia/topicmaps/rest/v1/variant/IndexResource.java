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

package net.ontopia.topicmaps.rest.v1.variant;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.core.index.NameIndexIF;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.resources.AbstractTransactionalResource;
import org.restlet.data.Form;
import org.restlet.resource.Post;

public class IndexResource extends AbstractTransactionalResource {

	@Post("txt:json")
	public Collection<VariantNameIF> getVariantNames(String value) {
		return getVariantNames(value, null);
	}

	@Post("json:json")
	public Collection<VariantNameIF> getVariantNames(Map<String, String> values) {
		return getVariantNames(values.get("value"), values.get("datatype"));
	}
	
	@Post("form:json")
	public Collection<VariantNameIF> getVariantNames(Form values) {
		return getVariantNames(values.getFirstValue("value"), values.getFirstValue("datatype"));
	}
	
	protected Collection<VariantNameIF> getVariantNames(String value, String datatype) {
		NameIndexIF index = getIndex(NameIndexIF.class);
		
		try {
			if (datatype != null) {
				return index.getVariants(value, new URILocator(datatype));
			} else {
				return index.getVariants(value);
			}
		} catch (URISyntaxException mufe) {
			throw OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE.build(mufe, "datatype", "LocatorIF", datatype);
		}
	}
}
