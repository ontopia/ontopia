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

import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.Topic;
import net.ontopia.topicmaps.rest.model.TopicName;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import org.junit.Assert;
import org.junit.Test;

public class TopicNamesResourcePUTTest extends AbstractV1ResourceTest {

	public TopicNamesResourcePUTTest() {
		super(NAMES_LTM, "topics");
	}

	@Test
	public void testAdd() {
		TopicName name = new TopicName();
		name.setType(new Topic("1"));
		name.setValue("foo");
		TopicName added = put("1/names", name, TopicName.class);

		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getObjectId());
		Assert.assertNotNull(added.getType());
		Assert.assertEquals("1", added.getType().getObjectId());
		Assert.assertNotNull(added.getTopic());
		Assert.assertEquals("1", added.getTopic().getObjectId());
	}

	@Test
	public void testAddUnexisting() {
		TopicName name = new TopicName();
		name.setType(new Topic("1"));
		name.setValue("foo");
		assertPutFails("unexisting/names", name, OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_NULL);
	}

	@Test
	public void testEmpty() {
		assertPutFails("1/names", null, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testAddInvalid() {
		assertPutFails("2/names", new TopicName(), OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}
}
