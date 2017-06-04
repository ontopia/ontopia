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

package net.ontopia.topicmaps.rest.v1.scoped;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.Collection;
import net.ontopia.topicmaps.rest.model.Topic;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import org.junit.Assert;
import org.junit.Test;

public class ScopesResourceGETTest extends AbstractV1ResourceTest {

	private final TypeReference<Collection<Topic>> REF = new TypeReference<Collection<Topic>>(){};

	public ScopesResourceGETTest() {
		super(OPERA_TM, "scopes");
	}

	@Test
	public void testAssociationThemes() throws IOException {
		Collection<Topic> scopes = get("associations", REF);

		Assert.assertNotNull(scopes);
		Assert.assertTrue(scopes.size() >= 2); // added to by tests
		assertContainsTopics(scopes, "2965", "2956");
	}

	@Test
	public void testOccurrenceThemes() throws IOException {
		Collection<Topic> scopes = get("occurrences", REF);

		Assert.assertNotNull(scopes);
		Assert.assertTrue(scopes.size() >= 25); // added to by tests
		assertContainsTopics(scopes, "6150", "143", "4855");
	}

	@Test
	public void testTopicNameThemes() throws IOException {
		Collection<Topic> scopes = get("names", REF);

		Assert.assertNotNull(scopes);
		Assert.assertEquals(100, scopes.size()); // added to by tests, paged
		assertContainsTopics(scopes, "29", "117");
	}

	@Test
	public void testVariantThemes() throws IOException {
		Collection<Topic> scopes = get("variants", REF);

		Assert.assertNotNull(scopes);
		Assert.assertTrue(scopes.size() >= 16); // added to by tests
		assertContainsTopics(scopes, "5", "179");
	}
}
