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

package net.ontopia.topicmaps.rest.v1.topicmap;

import java.io.IOException;
import java.util.Map;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import org.junit.Assert;
import org.junit.Test;

public class StatisticsResourceGETTest extends AbstractV1ResourceTest {

	public StatisticsResourceGETTest() {
		super(TOPICS_LTM, "statistics");
	}

	@Test
	public void testStatistics() throws IOException {
		Map<String, Object> json = getAsJson(null);

		Assert.assertNotNull(json);
		Assert.assertNotNull(json.get("untypedTopicCount"));
		Assert.assertEquals(6, (int) json.get("untypedTopicCount"));

		Assert.assertNotNull(json.get("topicTypeCount"));
		Assert.assertEquals(3, (int) json.get("topicTypeCount"));

		Assert.assertNotNull(json.get("typedTopicCount"));
		Assert.assertEquals(4, (int) json.get("typedTopicCount"));

		Assert.assertNotNull(json.get("topicCount"));
		Assert.assertEquals(9, (int) json.get("topicCount"));

		Assert.assertNotNull(json.get("associationCount"));
		Assert.assertEquals(3, (int) json.get("associationCount"));

		Assert.assertNotNull(json.get("associationTypeCount"));
		Assert.assertEquals(2, (int) json.get("associationTypeCount"));

		Assert.assertNotNull(json.get("roleCount"));
		Assert.assertEquals(3, (int) json.get("roleCount"));

		Assert.assertNotNull(json.get("roleTypeCount"));
		Assert.assertEquals(2, (int) json.get("roleTypeCount"));

		Assert.assertNotNull(json.get("occurrenceCount"));
		Assert.assertEquals(3, (int) json.get("occurrenceCount"));

		Assert.assertNotNull(json.get("occurrenceTypeCount"));
		Assert.assertEquals(3, (int) json.get("occurrenceTypeCount"));

		Assert.assertNotNull(json.get("topicNameCount"));
		Assert.assertEquals(3, (int) json.get("topicNameCount"));

		Assert.assertNotNull(json.get("noNameTopicCount"));
		Assert.assertEquals(6, (int) json.get("noNameTopicCount"));

		Assert.assertNotNull(json.get("topicNameTypeCount"));
		Assert.assertEquals(1, (int) json.get("topicNameTypeCount"));

		Assert.assertNotNull(json.get("variantCount"));
		Assert.assertEquals(1, (int) json.get("variantCount"));

		Assert.assertNotNull(json.get("subjectIdentifierCount"));
		Assert.assertEquals(2, (int) json.get("subjectIdentifierCount"));

		Assert.assertNotNull(json.get("subjectLocatorCount"));
		Assert.assertEquals(1, (int) json.get("subjectLocatorCount"));

		Assert.assertNotNull(json.get("itemIdentifierCount"));
		Assert.assertEquals(8, (int) json.get("itemIdentifierCount"));
	}
}
