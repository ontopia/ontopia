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
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.Topic;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import org.junit.Assert;
import org.junit.Test;

public class ScopedResourceGETTest extends AbstractV1ResourceTest {

	private final TypeReference<Collection<Topic>> REF = new TypeReference<Collection<Topic>>(){};

	public ScopedResourceGETTest() {
		super(OPERA_TM, null);
	}

	@Test
	public void testEmptyScope() throws IOException {
		Collection<Topic> scope = get("associations/9537/scope", REF);

		Assert.assertNotNull(scope);
		Assert.assertTrue(scope.isEmpty());
	}

	@Test
	public void testScopedName() throws IOException {
		Collection<Topic> scope = get("names/1879/scope", REF);

		Assert.assertNotNull(scope);
		Assert.assertEquals(2, scope.size());
		assertContainsTopics(scope, "1880", "5");
	}

	@Test
	public void testScopedOccurrence() throws IOException {
		Collection<Topic> scope = get("occurrences/5375/scope", REF);

		Assert.assertNotNull(scope);
		Assert.assertEquals(2, scope.size());
		assertContainsTopics(scope, "141", "4778");
	}

	@Test
	public void testScopedAssociation() throws IOException {
		Collection<Topic> scope = get("associations/11793/scope", REF);

		Assert.assertNotNull(scope);
		Assert.assertEquals(1, scope.size());
		assertContainsTopics(scope, "2956");
	}

	@Test
	public void testScopedVariant() throws IOException {
		Collection<Topic> scope = get("variants/6023/scope", REF);

		Assert.assertNotNull(scope);
		Assert.assertEquals(3, scope.size());
		assertContainsTopics(scope, "5", "174", "149");
	}

	/* -- Failing requests -- */

	@Test
	public void testInvalidName() {
		assertGetFails("names/1/scope", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}

	@Test
	public void testInvalidVariant() {
		assertGetFails("variants/1/scope", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}

	@Test
	public void testInvalidOccurrence() {
		assertGetFails("occurrences/1/scope", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}

	@Test
	public void testInvalidAssociation() {
		assertGetFails("associations/1/scope", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingName() {
		assertGetFails("names/unexisting/scope", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_NULL);
	}

	@Test
	public void testUnexistingVariant() {
		assertGetFails("variants/unexisting/scope", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_NULL);
	}

	@Test
	public void testUnexistingOccurrence() {
		assertGetFails("occurrences/unexisting/scope", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_NULL);
	}

	@Test
	public void testUnexistingAssociation() {
		assertGetFails("associations/unexisting/scope", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_NULL);
	}
}
