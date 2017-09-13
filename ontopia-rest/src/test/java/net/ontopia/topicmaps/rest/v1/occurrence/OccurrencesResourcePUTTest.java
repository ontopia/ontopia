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

package net.ontopia.topicmaps.rest.v1.occurrence;

import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.Occurrence;
import net.ontopia.topicmaps.rest.model.Topic;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import org.junit.Assert;
import org.junit.Test;

public class OccurrencesResourcePUTTest extends AbstractV1ResourceTest {

	public OccurrencesResourcePUTTest() {
		super(OCCURRENCES_LTM, "topics");
	}

	@Test
	public void testAdd() {
		Occurrence occurrence = new Occurrence();
		occurrence.setType(new Topic("1"));
		occurrence.setValue("foo");
		Occurrence added = put("1/occurrences", occurrence, Occurrence.class);

		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getObjectId());
		Assert.assertNotNull(added.getType());
		Assert.assertEquals("1", added.getType().getObjectId());
		Assert.assertNotNull(added.getTopic());
		Assert.assertEquals("1", added.getTopic().getObjectId());
	}

	@Test
	public void testAddUnexisting() {
		Occurrence occurrence = new Occurrence();
		occurrence.setType(new Topic("1"));
		occurrence.setValue("foo");
		assertPutFails("unexisting/occurrences", occurrence, OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_NULL);
	}

	@Test
	public void testEmpty() {
		assertPutFails("1/occurrences", null, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testAddInvalid() {
		assertPutFails("2/occurrences", new Occurrence(), OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}
}
