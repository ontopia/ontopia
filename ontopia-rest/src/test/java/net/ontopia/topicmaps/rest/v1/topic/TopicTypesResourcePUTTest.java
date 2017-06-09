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

public class TopicTypesResourcePUTTest extends AbstractV1ResourceTest {

	private final TypeReference<Collection<Topic>> REF = new TypeReference<Collection<Topic>>(){};

	public TopicTypesResourcePUTTest() {
		super(OPERA_TM, "topics");
	}

	@Test
	public void testAdd() throws IOException {
		Collection<Topic> types = put("3254/types", REF, new Topic("3705"));

		Assert.assertNotNull(types);
		Assert.assertEquals(2, types.size());
		assertContainsTopics(types, "165", "3705");
	}

	/* Failing requests */

	@Test
	public void testAddUnexistingType() throws IOException {
		assertPutFails("3254/types", new Topic("unexisting"), OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testAddInvalidType() throws IOException {
		assertPutFails("3254/types", new Topic("13"), OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testAddEmptyType() throws IOException {
		assertPutFails("3254/types", null, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testUnexistingTopicAddType() throws IOException {
		assertPutFails("unexiting/types", new Topic("1"), OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_NULL);
	}

	@Test
	public void testInvalidAddType() throws IOException {
		assertPutFails("13/types", new Topic("1"), OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}
}
