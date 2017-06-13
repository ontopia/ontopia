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

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.Collection;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.Occurrence;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import org.junit.Assert;
import org.junit.Test;

public class OccurrencesResourceGETTest extends AbstractV1ResourceTest {

	private final TypeReference<Collection<Occurrence>> REF = new TypeReference<Collection<Occurrence>>(){};

	public OccurrencesResourceGETTest() {
		super(OPERA_TM, null);
	}

	@Test
	public void testTopicOccurrences() throws IOException {
		Collection<Occurrence> occurrences = get("topics/6721/occurrences", REF);

		Assert.assertNotNull(occurrences);
		Assert.assertEquals(2, occurrences.size());
		Assert.assertEquals(Occurrence.class, occurrences.iterator().next().getClass());
		assertContainsTopics(occurrences, "6725", "6726");
	}

	@Test
	public void testUnexistingTopicOccurrences() throws IOException {
		Collection<Occurrence> occurrences = get("topics/unexisting/occurrences", REF);

		Assert.assertNotNull(occurrences);
		Assert.assertTrue(occurrences.isEmpty());
	}

	@Test
	public void testTopicOccurrencesByType() throws IOException {
		Collection<Occurrence> occurrences = get("topics/6721/occurrences/511", REF);

		Assert.assertNotNull(occurrences);
		Assert.assertEquals(1, occurrences.size());
		Assert.assertEquals(Occurrence.class, occurrences.iterator().next().getClass());
		assertContainsTopics(occurrences, "6725");
	}

	@Test
	public void testUnexistingTopicOccurrencesByType() throws IOException {
		Collection<Occurrence> occurrences = get("topics/301/occurrences/unexisting", REF);

		Assert.assertNotNull(occurrences);
		Assert.assertTrue(occurrences.isEmpty());
	}

	@Test
	public void testTopicmapOccurrencesByType() throws IOException {
		Collection<Occurrence> occurrences = get("occurrences/typed/511", REF);

		Assert.assertNotNull(occurrences);
		Assert.assertEquals(100, occurrences.size()); // paged
	}

	/* -- Failing requests -- */

	@Test
	public void testInvalidTopicOccurrences() {
		assertGetFails("topics/13/occurrences", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}

	@Test
	public void testTopicOccurrencesByInvalidType() {
		assertGetFails("topics/1/occurrences/13", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}

	@Test
	public void testTopicmapOccurrencesByInvalidType() {
		assertGetFails("occurrences/typed/13", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}
}
