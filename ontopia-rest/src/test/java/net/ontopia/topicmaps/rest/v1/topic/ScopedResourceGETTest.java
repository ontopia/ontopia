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

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.Collection;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.Association;
import net.ontopia.topicmaps.rest.model.Occurrence;
import net.ontopia.topicmaps.rest.model.TopicName;
import net.ontopia.topicmaps.rest.model.VariantName;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import org.junit.Assert;
import org.junit.Test;

public class ScopedResourceGETTest extends AbstractV1ResourceTest {

	private final TypeReference<Collection<Association>> AREF = new TypeReference<Collection<Association>>(){};
	private final TypeReference<Collection<Occurrence>> OREF = new TypeReference<Collection<Occurrence>>(){};
	private final TypeReference<Collection<TopicName>> NREF = new TypeReference<Collection<TopicName>>(){};
	private final TypeReference<Collection<VariantName>> VREF = new TypeReference<Collection<VariantName>>(){};

	public ScopedResourceGETTest() {
		super(TOPICS_LTM, "topics");
	}

	@Test
	public void testAssociations() throws IOException {
		Collection<Association> associations = get("1/scope/associations", AREF);

		Assert.assertNotNull(associations);
		assertContainsTopics(associations, "9");
	}

	@Test
	public void testEmptyAssociations() throws IOException {
		Collection<Association> associations = get("3/scope/associations", AREF);

		Assert.assertNotNull(associations);
		Assert.assertTrue(associations.isEmpty());
	}

	@Test
	public void testOccurrences() throws IOException {
		Collection<Occurrence> occurrences = get("5/scope/occurrences", OREF);

		Assert.assertNotNull(occurrences);
		Assert.assertEquals(1, occurrences.size());
		assertContainsTopics(occurrences, "11");
	}

	@Test
	public void testEmptyOccurrences() throws IOException {
		Collection<Occurrence> occurrences = get("1/scope/occurrences", OREF);

		Assert.assertNotNull(occurrences);
		Assert.assertTrue(occurrences.isEmpty());
	}

	@Test
	public void testTopicNames() throws IOException {
		Collection<TopicName> names = get("7/scope/names", NREF);

		Assert.assertNotNull(names);
		Assert.assertEquals(1, names.size());
		assertContainsTopics(names, "12");
	}

	@Test
	public void testEmptyTopicNames() throws IOException {
		Collection<TopicName> names = get("1/scope/names", NREF);

		Assert.assertNotNull(names);
		Assert.assertTrue(names.isEmpty());
	}

	@Test
	public void testVariantNames() throws IOException {
		Collection<VariantName> names = get("7/scope/variants", VREF);

		Assert.assertNotNull(names);
		Assert.assertEquals(1, names.size());
		assertContainsTopics(names, "13");
	}

	@Test
	public void testEmptyVariantNames() throws IOException {
		Collection<VariantName> names = get("1/scope/variants", VREF);

		Assert.assertNotNull(names);
		Assert.assertTrue(names.isEmpty());
	}

	/* failing requests */

	@Test
	public void testInvalidAssociations() {
		assertGetFails("2/scope/associations", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}

	@Test
	public void testInvalidOccurrences() {
		assertGetFails("2/scope/occurrences", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}

	@Test
	public void testInvalidTopicNames() {
		assertGetFails("2/scope/names", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}

	@Test
	public void testInvalidVariants() {
		assertGetFails("2/scope/variants", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingAssociations() {
		assertGetFails("unexisting/scope/associations", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_NULL);
	}

	@Test
	public void testUnexistingOccurrences() {
		assertGetFails("unexisting/scope/occurrences", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_NULL);
	}

	@Test
	public void testUnexistingTopicNames() {
		assertGetFails("unexisting/scope/names", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_NULL);
	}

	@Test
	public void testUnexistingVariants() {
		assertGetFails("unexisting/scope/variants", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_NULL);
	}
}
