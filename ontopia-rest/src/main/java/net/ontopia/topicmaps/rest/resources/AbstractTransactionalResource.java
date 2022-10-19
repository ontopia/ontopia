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

package net.ontopia.topicmaps.rest.resources;

import java.io.IOException;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.index.IndexIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.rest.Constants;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.utils.OntopiaUnsupportedException;
import org.restlet.data.Method;
import org.restlet.resource.ResourceException;

public class AbstractTransactionalResource extends AbstractPagedResource {

	protected TopicMapStoreIF store;
	protected boolean openStore = true;

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		if (openStore) {
			try {
				TopicMapReferenceIF reference = getTopicMapReference();
				if (reference == null) {
					throw OntopiaRestErrors.TOPICMAP_NOT_FOUND.build();
				}
				store = reference.createStore(getReadOnly());
			} catch (IOException ioe) {
				throw OntopiaRestErrors.CANNOT_OPEN_STORE.build();
			}
		}
	}

	@Override
	protected void setInfoHeaders() {
		super.setInfoHeaders();
		TopicMapReferenceIF reference = getTopicMapReference();
		if (openStore && (reference != null)) {
			addResponseHeader(Constants.HEADER_ONTOPIA_TOPICMAP, reference.getId());
		}
	}

	@Override
	protected void doRelease() throws ResourceException {
		if ((store != null) && (store.isOpen())) {
			store.close();
		}
	}

	protected boolean getReadOnly() {
		return Method.GET.equals(getMethod());
	}
	
	protected TopicMapIF getTopicMap() {
		return store.getTopicMap();
	}

	protected <C> C getRequestParameter(Class<C> klass, boolean allowNull) {
		return Parameters.ID.withExpected(klass).resolve(this, allowNull);
	}
	
	protected <C> C getRequestParameter(Class<C> klass, String name, boolean allowNull) {
		return getOntopia().getResolver().resolve(getTopicMap(), getRequest(), name, klass, allowNull);
	}
	
	protected <C> C requiredRequestParameter(Parameters<C> parameter) {
		return parameter.required(this);
	}
	
	protected <C> C optionalRequestParameter(Parameters<C> parameter) {
		return parameter.optional(this);
	}
	
	@SuppressWarnings("unchecked")
	protected <I extends IndexIF> I getIndex(Class<I> indexClass) {
		try {
			if (store != null) {
				return (I) getTopicMap().getIndex(indexClass.getName());
			} else {
				return null;
			}
		} catch (OntopiaUnsupportedException e) {
			throw OntopiaRestErrors.INDEX_NOT_SUPPORTED.build(e, indexClass.getSimpleName());
		}
	}
}
