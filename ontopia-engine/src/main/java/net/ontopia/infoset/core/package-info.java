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
/**
<p>Interfaces for working with information resources and locators that
refer to information resources.</p>

<h3>Package Specification</h3>

<p>The interfaces in this package provide uniform means of identifying
information resources, and retrieving content and metadata from them.
The interfaces are highly abstract and can be used to provide support
for any locator syntax.</p>

<h3> Interface Summary</h3>

<p>The LocatorIF interface provides methods for getting the the
notation of the resource address held by the locator, and for getting
the absolute address itself as a string. Locators are considered to be
primitive values in the same way as strings and numbers, and so
LocatorIF implementations should also implement the toString(),
equals() and hashCode() methods.</p>

<p>LocatorResolverIF provides a method for dereferencing a locator and
retrieving the resources referred to by it. Since a locator may refer
to more than one resource, the returned result is a Collection of
objects. The Collection may contain any number, in any order, of topic
map objects (@see net.ontopia.topicmaps.core) and resources
implementing the InformationResourceIF interface. Note that it is up
to the user of this collection to check the nature of each object
returned from it.</p>

<p>InformationResourceIF provides methods for getting metadata (as a
Map object) and content (as an arbitrary Object) from an information
resource. In addition, a method is provided for getting the locator
from which the information resource was accessed (if known).</p>
*/

package net.ontopia.infoset.core;
