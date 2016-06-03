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

package net.ontopia.topicmaps.rest.v1;

import java.util.Collection;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.rest.controller.AbstractController;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.TMObject;
import org.apache.commons.collections4.CollectionUtils;

public class TMObjectController extends AbstractController {

	@Override
	protected void init() {

	}

	public void setItemIdentifiers(TMObjectIF object, TMObject pojo) {
		if (pojo.getItemIdentifiers() != null) {
			Collection<LocatorIF> toRemove = CollectionUtils.subtract(object.getItemIdentifiers(), pojo.getItemIdentifiers());
			for (LocatorIF ii : pojo.getItemIdentifiers()) {
				object.addItemIdentifier(ii);
			}
			for (LocatorIF ii : toRemove) {
				object.removeItemIdentifier(ii);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <C extends TMObjectIF> C resolve(TopicMapIF tm, TMObject object, Class<C> expected) {
		if (object == null) {
			throw OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL.build(expected.getSimpleName());
		}
		
		TMObjectIF o = resolve(tm, object.getObjectId(), object.getItemIdentifiers());
		if (o == null) {
			throw OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL.build(expected.getSimpleName());
		} else {
			if (expected.isAssignableFrom(o.getClass())) {
				return (C) o;
			} else {
				throw OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE.build(expected.getSimpleName(), o);
			}
		}
	}
	
	public TMObjectIF resolve(TopicMapIF tm, String objectId, Collection<URILocator> itemIdentifiers) {
		if (objectId != null) {
			return tm.getObjectById(objectId);
		}
		
		if (!CollectionUtils.isEmpty(itemIdentifiers)) {
			for (URILocator ii : itemIdentifiers) {
				TMObjectIF objectByItemIdentifier = tm.getObjectByItemIdentifier(ii);
				if (objectByItemIdentifier != null) {
					return objectByItemIdentifier;
				}
			}
		}
		
		return null;
	}
}
