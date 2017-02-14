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

import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.rest.exceptions.OntopiaClientException;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestException;
import org.restlet.data.Reference;
import org.restlet.data.Status;

public abstract class AbstractTMObjectResource<TMO extends TMObjectIF> extends AbstractTransactionalResource {
	
	protected final Class<TMO> objectClass;
	
	protected AbstractTMObjectResource(Class<TMO> objectClass) {
		this.objectClass = objectClass;
	}

	public TMO resolve() throws OntopiaRestException {
		return resolve(false);
	}
	
	public TMO resolve(boolean allowNull) throws OntopiaRestException {
		return Parameters.ID.withExpected(objectClass).resolve(this, allowNull);
	}
	
	public TMO remove(TMO object) throws OntopiaRestException {
		if (object == null) {
			// todo: OntopiaRestErrors
			throw new OntopiaClientException(Status.CLIENT_ERROR_NOT_FOUND, "Cannot find " + objectClass.getSimpleName() + " to remove");
		}
		
		// todo: move to controller
		
		object.remove();
		store.commit();
		return object;
	}
	
	protected void redirectTo(TMO object) {
		Reference baseRef = getRequest().getResourceRef().getBaseRef();
		if (!baseRef.getLastSegment().endsWith("/")) {
			baseRef = new Reference(baseRef.toString() + "/");
		}
		// todo: maybe this should be '302 Found' instead
		redirectSeeOther(new Reference(baseRef, object.getObjectId()).getTargetRef());
	}
}
