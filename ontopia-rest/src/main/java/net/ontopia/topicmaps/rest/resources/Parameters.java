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

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;

public enum Parameters {

	TOPICMAP(String.class),
	ID(String.class),
	TYPE(TopicIF.class),
	ROLETYPE(TopicIF.class),
	ASSOCIATIONTYPE(TopicIF.class),
	TOPIC(TopicIF.class),
	TOPICNAME(TopicNameIF.class);

	private final Class<?> expected;
	
	private Parameters(Class<?> expected) {
		this.expected = expected;
	}

	public Class<?> getExpected() {
		return expected;
	}

	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
}
