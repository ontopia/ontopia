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

package net.ontopia.topicmaps.rest.v1.topicmap;

import net.ontopia.topicmaps.core.index.StatisticsIndexIF;
import net.ontopia.topicmaps.rest.model.mixin.MStatisticsIndex;
import net.ontopia.topicmaps.rest.resources.AbstractTransactionalResource;
import org.restlet.resource.Get;

public class StatisticsResource extends AbstractTransactionalResource {

	@Get
	public StatisticsIndexIF getStatistics() {
		addMixInAnnotations(StatisticsIndexIF.class, MStatisticsIndex.class);
		return getIndex(StatisticsIndexIF.class);
	}
}
