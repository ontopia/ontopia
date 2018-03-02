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

public class TopicTypesResourceGETTest extends AbstractV1ResourceTest {

	private final TypeReference<Collection<Topic>> REF = new TypeReference<Collection<Topic>>(){};

	public TopicTypesResourceGETTest() {
		super(TOPICS_LTM, "topics");
	}

	@Test
	public void testGetTypes() throws IOException {
		Collection<Topic> topics = get("types", REF);

		Assert.assertNotNull(topics);
		Assert.assertEquals(3, topics.size());
		assertContainsTopics(topics, "1", "5");
	}

	@Test
	public void testGetTopicTypes() throws IOException {
		Collection<Topic> topics = get("1/types", REF);

		Assert.assertNotNull(topics);
		Assert.assertEquals(1, topics.size());
		assertContainsTopics(topics, "1");
	}

	@Test
	public void testGetUnexistingTopicTypes() throws IOException {
		Collection<Topic> topics = get("unexisting/types", REF); // fallback to all types

		Assert.assertNotNull(topics);
		Assert.assertEquals(3, topics.size());
		assertContainsTopics(topics, "1", "5");
	}

	/* -- Failing requests -- */

	@Test
	public void testInvalidTopicAssociationRoles() {
		assertGetFails("2/types", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}
}
