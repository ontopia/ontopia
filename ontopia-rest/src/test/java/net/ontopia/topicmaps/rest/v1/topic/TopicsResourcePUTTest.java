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

import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.Topic;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import org.junit.Assert;
import org.junit.Test;

public class TopicsResourcePUTTest extends AbstractV1ResourceTest {

	public TopicsResourcePUTTest() {
		super(TOPICS_LTM, "topics/typed");
	}

	@Test
	public void testAdd() {
		Topic added = put("1", new Topic(), Topic.class);
		
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getObjectId());
		Assert.assertNotNull(added.getTypes());
		Assert.assertEquals(1, added.getTypes().size());
		assertContainsTopics(added.getTypes(), "1");
	}
	
	@Test
	public void testAddUnexisting() {
		Topic added = put("unexisting", new Topic(), Topic.class);
		
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getObjectId());
		Assert.assertNotNull(added.getTypes());
		Assert.assertTrue(added.getTypes().isEmpty());
	}
	
	@Test
	public void testEmpty() {
		assertPutFails("1", null, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}
	
	@Test
	public void testAddInvalid() {
		assertPutFails("2", new Topic(), OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}
}
