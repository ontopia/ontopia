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

import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.rest.model.VariantName;
import net.ontopia.topicmaps.rest.resources.AbstractTMObjectResource;
import net.ontopia.topicmaps.rest.v1.name.TopicNameController;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;

public class VariantResource extends AbstractTMObjectResource<VariantNameIF> {

	public VariantResource() {
		super(VariantNameIF.class);
	}
	
	@Get
	public VariantNameIF getVariant() {
		return resolve();
	}
	
	@Put
	public void addVariantName(VariantName variant) {
		TopicNameIF name = getController(TopicNameController.class).resolve(getTopicMap(), variant.getTopicName());
		VariantNameIF result = getController(VariantNameController.class).add(getTopicMap(), name, variant);
		store.commit();
		redirectTo(result);
	}
	
	@Post
	public VariantNameIF changeVariantName(VariantName variant) {
		VariantNameIF result = getController(VariantNameController.class).change(getTopicMap(), variant);
		store.commit();
		return result;
	}
	
	@Delete
	public void removeVariantName() {
		getController(VariantNameController.class).remove(resolve());
		store.commit();
	}
}
