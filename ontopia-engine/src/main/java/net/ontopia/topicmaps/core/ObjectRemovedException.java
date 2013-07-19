/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
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

package net.ontopia.topicmaps.core;


/**
 * PUBLIC: Thrown when a deleted topic map object is attempted
 * reassigned to a property in a topic map.</p>
 *
 * Extends ConstraintViolationException but does not provide additional
 * methods; this is because the purpose is just to provide a different
 * exception, to allow API users to handle them differently.</p>
 *
 * @since 4.0.0
 */

public class ObjectRemovedException extends ConstraintViolationException {

	public ObjectRemovedException(TMObjectIF tmobject) {
		super("Cannot reassign object " + tmobject + " as it has already been removed from the topic map."); 
	}

}





