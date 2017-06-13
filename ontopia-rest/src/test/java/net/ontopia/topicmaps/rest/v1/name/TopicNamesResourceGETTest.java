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

package net.ontopia.topicmaps.rest.v1.name;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.Collection;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.TopicName;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import org.junit.Assert;
import org.junit.Test;

public class TopicNamesResourceGETTest extends AbstractV1ResourceTest {

	private final TypeReference<Collection<TopicName>> REF = new TypeReference<Collection<TopicName>>(){};

	public TopicNamesResourceGETTest() {
		super(OPERA_TM, null);
	}

	@Test
	public void testTopicTopicNames() throws IOException {
		Collection<TopicName> names = get("topics/6721/names", REF);

		Assert.assertNotNull(names);
		Assert.assertEquals(3, names.size());
		Assert.assertEquals(TopicName.class, names.iterator().next().getClass());
		assertContainsTopics(names, "6724", "6722", "6723");
	}

	@Test
	public void testUnexistingTopicTopicNames() throws IOException {
		Collection<TopicName> names = get("topics/unexisting/names", REF);

		Assert.assertNotNull(names);
		Assert.assertEquals(100, names.size()); // all default names
	}

	@Test
	public void testTopicTopicNamesByType() throws IOException {
		Collection<TopicName> names = get("topics/6721/names/3", REF); // note: no other name types in opera

		Assert.assertNotNull(names);
		Assert.assertEquals(3, names.size());
		assertContainsTopics(names, "6724", "6722", "6723");
	}

	@Test
	public void testUnexistingTopicTopicNamesByType() throws IOException {
		Collection<TopicName> names = get("topics/301/names/unexisting", REF); // falls back to default name type

		Assert.assertNotNull(names);
		Assert.assertEquals(3, names.size());
		assertContainsTopics(names, "302", "303", "304");
	}

	@Test
	public void testTopicmapTopicNamesByType() throws IOException {
		Collection<TopicName> names = get("names/typed/3", REF);

		Assert.assertNotNull(names);
		Assert.assertEquals(100, names.size()); // paged
	}

	/* -- Failing requests -- */

	@Test
	public void testInvalidTopicTopicNames() {
		assertGetFails("topics/13/names", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}

	@Test
	public void testTopicTopicNamesByInvalidType() {
		assertGetFails("topics/1/names/13", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}

	@Test
	public void testTopicmapTopicNamesByInvalidType() {
		assertGetFails("names/typed/13", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}
}
