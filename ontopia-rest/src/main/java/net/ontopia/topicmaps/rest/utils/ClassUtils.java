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

package net.ontopia.topicmaps.rest.utils;

public class ClassUtils {
	public static final String PREFIX = "net.ontopia.topicmaps.rest.";
	public static final String PREFIX_RESOURCES = "net.ontopia.topicmaps.rest.resources.";
	public static final String SHORT = "n.o.t.r.";
	public static final String SHORT_RESOURCES = "n.o.t.r.r.";
	
	public static String collapsedName(Class<?> klass) {
		String name = klass.getName();
		if (name.startsWith(PREFIX_RESOURCES)) {
			return SHORT_RESOURCES + name.substring(PREFIX_RESOURCES.length());
		}
		if (name.startsWith(PREFIX)) {
			return SHORT + name.substring(PREFIX.length());
		}
		return name;
	}

}
