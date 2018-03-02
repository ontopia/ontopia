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

import java.io.IOException;
import java.util.Map;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import org.junit.Assert;
import org.junit.Test;

public class TopicUseResourceGETTest extends AbstractV1ResourceTest {

	public TopicUseResourceGETTest() {
		super(TOPICS_LTM, null);
	}

	@Test
	public void testTopicUse() throws IOException {
		assertUse("8", false, false, false, false, false);
	}

	@Test
	public void testTopicTypeUse() throws IOException {
		assertUse("14", true, false, false, false, false);
	}

	@Test
	public void testNameTypeUse() throws IOException {
		assertUse("3", false, false, true, false, false);
	}

	@Test
	public void testOccurrenceTypeUse() throws IOException {
		assertUse("15", false, true, false, false, false);
	}

	@Test
	public void testAssociationTypeUse() throws IOException {
		assertUse("17", false, false, false, true, false);
	}

	@Test
	public void testRoleTypeUse() throws IOException {
		assertUse("21", false, false, false, false, true);
	}

	private void assertUse(String id, boolean tt, boolean ot, boolean nt, boolean at, boolean rt) throws IOException {
		Map<String, Object> json = getAsJson("topics/" + id + "/use");
		Assert.assertEquals(rt, (boolean) json.get("usedAsAssociationRoleType"));
		Assert.assertEquals(tt, (boolean) json.get("usedAsTopicType"));
		Assert.assertEquals(ot, (boolean) json.get("usedAsOccurrenceType"));
		Assert.assertEquals(tt || ot || nt || at || rt, (boolean) json.get("usedAsType"));
		Assert.assertEquals(nt, (boolean) json.get("usedAsTopicNameType"));
		Assert.assertEquals(at, (boolean) json.get("usedAsAssociationType"));
	}

	/* -- Failing requests -- */

	@Test
	public void testInvalidUse() {
		assertGetFails("topics/2/use", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingUse() {
		assertGetFails("topics/unexisting/use", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_NULL);
	}
}
