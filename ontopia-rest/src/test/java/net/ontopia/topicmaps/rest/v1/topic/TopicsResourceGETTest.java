/*
 * #!
 * Ontopia Rest
 * #-
 * Copyright (C) 2001 - 2017 The Ontopia Project
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

package net.ontopia.topicmaps.rest.v1.topic;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.Collection;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.Topic;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import org.junit.Assert;
import org.junit.Test;

public class TopicsResourceGETTest extends AbstractV1ResourceTest {

	private final TypeReference<Collection<Topic>> REF = new TypeReference<Collection<Topic>>(){};

	public TopicsResourceGETTest() {
		super(OPERA_TM, "topics");
	}

	@Test
	public void testTopics() throws IOException {
		Collection<Topic> topics = get(null, REF);

		Assert.assertNotNull(topics);
		Assert.assertEquals(100, topics.size()); // paged
	}

	@Test
	public void testTopicsByType() throws IOException {
		Collection<Topic> topics = get("typed/43", REF);

		Assert.assertNotNull(topics);
		Assert.assertEquals(100, topics.size()); // paged
	}

	@Test
	public void testTopicsByUnexistingType() throws IOException {
		Collection<Topic> topics = get("typed/unexisting", REF); // fallback to all topics

		Assert.assertNotNull(topics);
		Assert.assertEquals(100, topics.size()); // paged
	}

	/* -- Failing requests -- */

	@Test
	public void testInvalidTopicAssociationRoles() {
		assertGetFails("typed/13", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}
}
