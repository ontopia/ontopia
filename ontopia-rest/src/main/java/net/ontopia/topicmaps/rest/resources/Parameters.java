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

public class Parameters<C> {

	public static final Parameters<String> TOPICMAP = new Parameters<>("topicmap", String.class);
	public static final Parameters<String> ID = new Parameters<>("id", String.class);

	public static final Parameters<TopicIF> TOPIC = new Parameters<>("topic", TopicIF.class);
	public static final Parameters<TopicIF> TYPE = TOPIC.withName("type");
	public static final Parameters<TopicIF> ROLETYPE = TOPIC.withName("roletype");
	public static final Parameters<TopicIF> ASSOCIATIONTYPE = TOPIC.withName("associationtype");

	public static final Parameters<TopicNameIF> TOPICNAME = new Parameters<>("topicname", TopicNameIF.class);
	
	// paging
	public static final Parameters<Integer> LIMIT = new Parameters<>("limit", Integer.class);
	public static final Parameters<Integer> OFFSET = new Parameters<>("offset", Integer.class);
	
	// query
	public static final Parameters<String> LANGUAGE = new Parameters<>("language", String.class);

	private final Class<C> expected;
	private final String name;
	
	protected Parameters(String name, Class<C> expected) {
		this.expected = expected;
		this.name = name;
	}

	public Class<C> getExpected() {
		return expected;
	}

	public String getName() {
		return name;
	}

	public Parameters<C> withName(String name) {
		return new Parameters<>(name, expected);
	}

	public <T> Parameters<T> withExpected(Class<T> expected) {
		return new Parameters<>(name, expected);
	}

	public C required(AbstractTransactionalResource resource) {
		return resolve(resource, false);
	}

	public C optional(AbstractTransactionalResource resource) {
		return resolve(resource, true);
	}

	public C resolve(AbstractTransactionalResource resource, boolean allowNull) {
		return resource.getRequestParameter(expected, name, allowNull);
	}
}
