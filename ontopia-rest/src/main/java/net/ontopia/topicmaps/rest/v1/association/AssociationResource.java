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

package net.ontopia.topicmaps.rest.v1.association;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.rest.Constants;
import net.ontopia.topicmaps.rest.model.Association;
import net.ontopia.topicmaps.rest.resources.AbstractTMObjectResource;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

public class AssociationResource extends AbstractTMObjectResource<AssociationIF> {

	public AssociationResource() {
		super(AssociationIF.class);
	}
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		// CTM LTM, TMXML and XTM cannot export a single occurrence
		blockMimeType(Constants.CTM_MEDIA_TYPE, Constants.LTM_MEDIA_TYPE, 
				Constants.TMXML_MEDIA_TYPE, Constants.XTM_MEDIA_TYPE);
	}

	@Get
	public AssociationIF getAssociation() {
		return resolve();
	}
	
	@Put
	public void addAssociation(Association association) {
		AssociationIF result = getController(AssociationController.class).add(getTopicMap(), association);
		store.commit();
		redirectTo(result);
	}
	
	@Post
	public AssociationIF changeAssociation(Association association) {
		AssociationIF result = getController(AssociationController.class).change(getTopicMap(), resolve(), association);
		store.commit();
		return result;
	}
	
	@Delete
	public void removeAssociation() {
		getController(AssociationController.class).remove(resolve());
		store.commit();
	}
}
