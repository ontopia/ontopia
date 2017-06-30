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

package net.ontopia.topicmaps.rest.controller;

import net.ontopia.topicmaps.rest.OntopiaRestApplication;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;

public abstract class AbstractController {

	protected OntopiaRestApplication ontopia;

	public void setOntopia(OntopiaRestApplication ontopia) {
		this.ontopia = ontopia;
		
		init();
	}

	public OntopiaRestApplication getOntopia() {
		return ontopia;
	}

	protected <C extends AbstractController> C getController(Class<C> controllerClass) {
		return getOntopia().getController(controllerClass);
	}

	protected abstract void init();

	protected void requireNotNull(Object field, String name) {
		if (field == null) {
			throw OntopiaRestErrors.MANDATORY_FIELD_IS_NULL.build(name);
		}
	}
}
