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

public class ScopedResourcePUTTest extends AbstractV1ResourceTest {

	private final TypeReference<Collection<Topic>> REF = new TypeReference<Collection<Topic>>(){};

	public ScopedResourcePUTTest() {
		super(SCOPED_LTM, null);
	}

	@Test
	public void testAddScopeAssociation() throws IOException {
		Collection<Topic> scope = put("associations/7/scope", REF, new Topic("11"));

		Assert.assertNotNull(scope);
		Assert.assertEquals(3, scope.size());
		assertContainsTopics(scope, "11");
	}

	@Test
	public void testAddScopeName() throws IOException {
		Collection<Topic> scope = put("names/4/scope", REF, new Topic("11"));

		Assert.assertNotNull(scope);
		Assert.assertEquals(3, scope.size());
		assertContainsTopics(scope, "11");
	}

	@Test
	public void testAddScopeOccurrence() throws IOException {
		Collection<Topic> scope = put("occurrences/2/scope", REF, new Topic("11"));

		Assert.assertNotNull(scope);
		Assert.assertEquals(3, scope.size());
		assertContainsTopics(scope, "11");
	}

	@Test
	public void testAddScopeVariant() throws IOException {
		Collection<Topic> scope = put("variants/6/scope", REF, new Topic("11"));

		Assert.assertNotNull(scope);
		Assert.assertEquals(3, scope.size());
		assertContainsTopics(scope, "11");
	}

	/* -- Failing requests -- */

	@Test
	public void testInvalidNameScope() {
		assertPutFails("names/1/scope", new Topic("1"), OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}

	@Test
	public void testNameInvalidScope() {
		assertPutFails("names/4/scope", new Topic("2"), OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingNameScope() {
		assertPutFails("names/unexisting/scope", new Topic("1"), OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_NULL);
	}

	@Test
	public void testNameUnexistingScope() {
		assertPutFails("names/4/scope", new Topic("unexisting"), OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testInvalidOccurrenceScope() {
		assertPutFails("occurrences/1/scope", new Topic("1"), OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}

	@Test
	public void testOccurrenceInvalidScope() {
		assertPutFails("occurrences/2/scope", new Topic("2"), OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingOccurrenceScope() {
		assertPutFails("occurrences/unexisting/scope", new Topic("1"), OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_NULL);
	}

	@Test
	public void testOccurrenceUnexistingScope() {
		assertPutFails("occurrences/2/scope", new Topic("unexisting"), OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testInvalidVariantScope() {
		assertPutFails("variants/1/scope", new Topic("1"), OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}

	@Test
	public void testVariantInvalidScope() {
		assertPutFails("variants/6/scope", new Topic("2"), OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingVariantScope() {
		assertPutFails("variants/unexisting/scope", new Topic("1"), OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_NULL);
	}

	@Test
	public void testVariantUnexistingScope() {
		assertPutFails("variants/6/scope", new Topic("unexisting"), OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testInvalidAssociationScope() {
		assertPutFails("associations/1/scope", new Topic("1"), OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}

	@Test
	public void testAssociationInvalidScope() {
		assertPutFails("associations/7/scope", new Topic("2"), OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingAssociationScope() {
		assertPutFails("associations/unexisting/scope", new Topic("1"), OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_NULL);
	}

	@Test
	public void testAssociationUnexistingScope() {
		assertPutFails("associations/7/scope", new Topic("unexisting"), OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}
}
