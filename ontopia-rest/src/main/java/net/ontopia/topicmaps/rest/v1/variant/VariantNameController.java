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

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Collection;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.rest.controller.AbstractController;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.VariantName;
import net.ontopia.topicmaps.rest.v1.ReifiableController;
import net.ontopia.topicmaps.rest.v1.TMObjectController;
import net.ontopia.topicmaps.rest.v1.name.TopicNameController;
import net.ontopia.topicmaps.rest.v1.scoped.ScopedController;

public class VariantNameController extends AbstractController {

	private ScopedController scoped;
	private ReifiableController reifiable;
	private TMObjectController tmobject;

	@Override
	protected void init() {
		scoped = getController(ScopedController.class);
		reifiable = getController(ReifiableController.class);
		tmobject = getController(TMObjectController.class);
	}
	
	public VariantNameIF add(TopicMapIF tm, VariantName variant) {
		requireNotNull(variant.getTopicName(), "topicName");
		TopicNameIF name = getController(TopicNameController.class).resolve(tm, variant.getTopicName());
		return add(tm, name, variant);
	}
	public VariantNameIF add(TopicMapIF tm, TopicNameIF name, VariantName variant) {
		requireNotNull(variant.getValue(), "value");
		requireNotNull(variant.getScope(), "scope");

		LocatorIF dataType = variant.getDataType();
		if (dataType == null) {
			dataType = DataTypes.TYPE_STRING;
		}
		
		TopicMapBuilderIF builder = tm.getBuilder();
		Collection<TopicIF> resolvedScope = scoped.resolve(tm, variant.getScope());
		
		VariantNameIF result;
		if (DataTypes.TYPE_URI.equals(dataType)) {
			try {
				result = builder.makeVariantName(name, new URILocator(variant.getValue()), resolvedScope);
			} catch (URISyntaxException mufe) {
				throw OntopiaRestErrors.MALFORMED_LOCATOR.build(mufe, variant.getValue());
			}
		} else {
			result = builder.makeVariantName(name, variant.getValue(), dataType, resolvedScope);
		}
		
		// ReifiableIF
		reifiable.setReifier(result, variant.getReifier());
		// TMObjectIF
		tmobject.setItemIdentifiers(result, variant);
		
		return result;
	}
	
	public void remove(TopicMapIF tm, VariantName variant) {
		remove(resolve(tm, variant));
	}
	
	public void remove(VariantNameIF variant) {
		variant.remove();
	}
	
	public VariantNameIF change(TopicMapIF tm, VariantNameIF result, VariantName variant) {
		
		// VariantNameIF
		if ((variant.getValue() != null) && (!variant.getValue().equals(result.getValue()))) {
			result.setValue(variant.getValue());
		}
		if ((variant.getDataType() != null) && (!variant.getDataType().equals(result.getDataType()))) {
			result.setValue(result.getValue(), variant.getDataType()); // no shortcut
		}
		
		// ScopedIF
		scoped.setScope(result, variant.getScope());
		// ReifiableIF
		reifiable.setReifier(result, variant.getReifier());
		// TMObjectIF
		tmobject.setItemIdentifiers(result, variant);
		
		return result;
	}
	
	public VariantNameIF resolve(TopicMapIF tm, VariantName variant) {
		return tmobject.resolve(tm, variant, VariantNameIF.class);
	}
}
