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

import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.rest.Constants;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.VariantName;
import net.ontopia.topicmaps.rest.resources.AbstractTMObjectResource;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

public class VariantResource extends AbstractTMObjectResource<VariantNameIF> {

	public VariantResource() {
		super(VariantNameIF.class);
	}
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		// CTM LTM, TMXML and XTM cannot export a single variant name
		blockMimeType(Constants.CTM_MEDIA_TYPE, Constants.LTM_MEDIA_TYPE, 
				Constants.TMXML_MEDIA_TYPE, Constants.XTM_MEDIA_TYPE);
	}

	@Get
	public VariantNameIF getVariant() {
		return resolve();
	}
	
	@Put
	public void addVariantName(VariantName variant) {
		
		if (variant == null) {
			throw OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL.build("VariantName");
		}
		
		VariantNameIF result = getController(VariantNameController.class).add(getTopicMap(), variant);
		store.commit();
		redirectTo(result);
	}
	
	@Post
	public VariantNameIF changeVariantName(VariantName variant) {
		VariantNameIF result = getController(VariantNameController.class).change(getTopicMap(), resolve(), variant);
		store.commit();
		return result;
	}
	
	@Delete
	public void removeVariantName() {
		getController(VariantNameController.class).remove(resolve());
		store.commit();
	}
}
