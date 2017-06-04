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

public class ScopeUseResourceGETTest extends AbstractV1ResourceTest {

	public ScopeUseResourceGETTest() {
		super(OPERA_TM, "topics");
	}

	@Test
	public void testScopeUse() throws IOException {
		Map<String, Object> use = getAsJson("1/scope/use");

		Assert.assertNotNull(use);
		Assert.assertEquals(true, (Boolean) use.get("usedAsOccurrenceTheme"));
		Assert.assertEquals(true, (Boolean) use.get("usedAsAssociationTheme"));
		Assert.assertEquals(true, (Boolean) use.get("usedAsVariantTheme"));
		Assert.assertEquals(true, (Boolean) use.get("usedAsTopicNameTheme"));
		Assert.assertEquals(true, (Boolean) use.get("usedAsTheme"));
	}

	@Test
	public void testScopeUnused() throws IOException {
		Map<String, Object> use = getAsJson("3/scope/use");

		Assert.assertNotNull(use);
		Assert.assertEquals(false, (Boolean) use.get("usedAsOccurrenceTheme"));
		Assert.assertEquals(false, (Boolean) use.get("usedAsAssociationTheme"));
		Assert.assertEquals(false, (Boolean) use.get("usedAsVariantTheme"));
		Assert.assertEquals(false, (Boolean) use.get("usedAsTopicNameTheme"));
		Assert.assertEquals(false, (Boolean) use.get("usedAsTheme"));
	}

	/* invalid requests */

	@Test
	public void testInvalidScopeUse() {
		assertGetFails("13/scope/use", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingScopeUse() {
		assertGetFails("unexisting/scope/use", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_NULL);
	}
}
