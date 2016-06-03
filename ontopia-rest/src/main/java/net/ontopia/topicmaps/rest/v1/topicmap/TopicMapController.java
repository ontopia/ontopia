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

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.rest.controller.AbstractController;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.TopicMap;
import net.ontopia.topicmaps.rest.v1.TMObjectController;

public class TopicMapController extends AbstractController {

	private TMObjectController tmobject;

	@Override
	protected void init() {
		tmobject = getController(TMObjectController.class);
	}

	public void change(TopicMapReferenceIF reference, TopicMapIF tm, TopicMap topicmap) {
		
		if ((topicmap.getTitle() != null) && (!topicmap.getTitle().equals(reference.getTitle()))) {
			reference.setTitle(topicmap.getTitle());
		}
		
		// TMObjectIF
		tmobject.setItemIdentifiers(tm, topicmap);
	}

	public void remove(TopicMapReferenceIF reference) {
		if (!reference.getSource().supportsDelete()) {
			throw OntopiaRestErrors.TOPICMAP_DELETE_NOT_SUPPORTED.build(reference.getId());
		}

		// todo: options to block this
		
		reference.delete();
	}
}
