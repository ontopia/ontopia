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
import static net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest.OPERA_TM;
import org.junit.Assert;
import org.junit.Test;

public class IndexResourcePOSTTest extends AbstractV1ResourceTest {

	private final TypeReference<Collection<Occurrence>> REF = new TypeReference<Collection<Occurrence>>(){};
	private final TypeReference<Collection<String>> REF2 = new TypeReference<Collection<String>>(){};

	public IndexResourcePOSTTest() {
		super(OPERA_TM, "occurrences/index");
	}

	@Test
	public void testValue() throws IOException {
		Collection<Occurrence> occurrences = post("value", REF, "$Revision: 2.1a $");

		Assert.assertNotNull(occurrences);
		Assert.assertEquals(1, occurrences.size());
		assertContainsTopics(occurrences, "11");
	}

	@Test
	public void testemptyValue() throws IOException {
		Collection<Occurrence> occurrences = post("value", REF, "Foobar");

		Assert.assertNotNull(occurrences);
		Assert.assertTrue(occurrences.isEmpty());
	}

	@Test
	public void testPrefix() throws IOException {
		Collection<Occurrence> occurrences = post("prefix", REF, "This");

		Assert.assertNotNull(occurrences);
		Assert.assertEquals(2, occurrences.size());
		assertContainsTopics(occurrences, "9", "4588");
	}

	@Test
	public void testEmptyPrefix() throws IOException {
		Collection<Occurrence> occurrences = post("prefix", REF, "Foobar");

		Assert.assertNotNull(occurrences);
		Assert.assertTrue(occurrences.isEmpty());
	}

	@Test
	public void testGreater() throws IOException {
		Collection<String> occurrences = post("gte", REF2, "Z");

		Assert.assertNotNull(occurrences);
		Assert.assertEquals(100, occurrences.size()); // 500+, includes locators for some reason
		Assert.assertTrue(occurrences.contains("Zazà's mother"));
		Assert.assertTrue(occurrences.contains("http://dante.di.unipi.it/ricerca/libretti/Fedora.html")); // don't know why this is in there
	}

	@Test
	public void testEmptyGreater() throws IOException {
		Collection<String> occurrences = post("gte", REF2, "vstudent");

		Assert.assertNotNull(occurrences);
		Assert.assertTrue(occurrences.isEmpty());
	}

	@Test
	public void testLesser() throws IOException {
		Collection<String> occurrences = post("lte", REF2, "-100");

		Assert.assertNotNull(occurrences);
		Assert.assertEquals(6, occurrences.size());
		Assert.assertTrue(occurrences.contains("(unknown)"));
		Assert.assertTrue(occurrences.contains("$Revision: 2.1a $"));
	}

	@Test
	public void testEmptyLesser() throws IOException {
		Collection<String> occurrences = post("lte", REF2, "");

		Assert.assertNotNull(occurrences);
		Assert.assertTrue(occurrences.isEmpty());
	}

	@Test
	public void testNullPrefix() throws IOException {
		assertPostFails("prefix", null, OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_NULL);
	}
}
