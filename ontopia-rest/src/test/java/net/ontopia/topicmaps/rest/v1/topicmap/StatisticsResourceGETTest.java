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
		super(OPERA_TM, "statistics");
	}

	@Test
	public void testStatistics() throws IOException {
		Map<String, Object> json = getAsJson(null);

		Assert.assertNotNull(json);
		Assert.assertNotNull(json.get("untypedTopicCount"));
		Assert.assertTrue((int) json.get("untypedTopicCount") > 10);

		Assert.assertNotNull(json.get("topicTypeCount"));
		Assert.assertTrue((int) json.get("topicTypeCount") > 10);

		Assert.assertNotNull(json.get("typedTopicCount"));
		Assert.assertTrue((int) json.get("typedTopicCount") > 1000);

		Assert.assertNotNull(json.get("topicCount"));
		Assert.assertTrue((int) json.get("topicCount") > 1000);

		Assert.assertNotNull(json.get("associationCount"));
		Assert.assertTrue((int) json.get("associationCount") > 3000);

		Assert.assertNotNull(json.get("associationTypeCount"));
		Assert.assertTrue((int) json.get("associationTypeCount") > 10);

		Assert.assertNotNull(json.get("roleCount"));
		Assert.assertTrue((int) json.get("roleCount") > 7000);

		Assert.assertNotNull(json.get("roleTypeCount"));
		Assert.assertTrue((int) json.get("roleTypeCount") > 10);

		Assert.assertNotNull(json.get("occurrenceCount"));
		Assert.assertTrue((int) json.get("occurrenceCount") > 1000);

		Assert.assertNotNull(json.get("occurrenceTypeCount"));
		Assert.assertTrue((int) json.get("occurrenceTypeCount") > 10);

		Assert.assertNotNull(json.get("topicNameCount"));
		Assert.assertTrue((int) json.get("topicNameCount") > 2000);

		Assert.assertNotNull(json.get("noNameTopicCount"));
		Assert.assertTrue((int) json.get("noNameTopicCount") > 0);

		Assert.assertNotNull(json.get("topicNameTypeCount"));
		Assert.assertTrue((int) json.get("topicNameTypeCount") > 0);

		Assert.assertNotNull(json.get("variantCount"));
		Assert.assertTrue((int) json.get("variantCount") > 100);

		Assert.assertNotNull(json.get("subjectIdentifierCount"));
		Assert.assertTrue((int) json.get("subjectIdentifierCount") > 400);

		Assert.assertNotNull(json.get("subjectLocatorCount"));
		Assert.assertTrue((int) json.get("subjectLocatorCount") > 0);

		Assert.assertNotNull(json.get("itemIdentifierCount"));
		Assert.assertTrue((int) json.get("itemIdentifierCount") > 1000);
	}
}
