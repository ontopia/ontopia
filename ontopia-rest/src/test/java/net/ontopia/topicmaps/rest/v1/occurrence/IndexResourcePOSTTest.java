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
import java.util.HashMap;
import java.util.Map;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.Occurrence;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import org.junit.Assert;
import org.junit.Test;
import org.restlet.data.Form;

public class IndexResourcePOSTTest extends AbstractV1ResourceTest {

	private final TypeReference<Collection<Occurrence>> REF = new TypeReference<Collection<Occurrence>>(){};
	private final TypeReference<Collection<String>> REF2 = new TypeReference<Collection<String>>(){};

	public IndexResourcePOSTTest() {
		super(OCCURRENCES_LTM, "occurrences/index");
	}
	
	private Map<String, String> createMap(String... keyval) {
		Map<String, String> map = new HashMap<>(keyval.length / 2);
		for (int i = 0; i < keyval.length; i += 2) {
			map.put(keyval[i], keyval[i + 1]);
		}
		return map;
	}

	@Test
	public void testValue() throws IOException {
		Collection<Occurrence> occurrences = post("value", REF, "Opera");

		Assert.assertNotNull(occurrences);
		Assert.assertEquals(1, occurrences.size());
		assertContainsTopics(occurrences, "9");
	}

	@Test
	public void testValueDatatypeJSON() throws IOException {
		Collection<Occurrence> occurrences = post("value", REF, 
				createMap("value", "Opera", "datatype", DataTypes.TYPE_STRING.getAddress()));

		Assert.assertNotNull(occurrences);
		Assert.assertEquals(1, occurrences.size());
		assertContainsTopics(occurrences, "9");
	}

	@Test
	public void testValueDatatypeFORM() throws IOException {
		Form form = new Form();
		form.add("value", "Opera");
		form.add("datatype", DataTypes.TYPE_STRING.getAddress());
		Collection<Occurrence> occurrences = post("value", REF, form);

		Assert.assertNotNull(occurrences);
		Assert.assertEquals(1, occurrences.size());
		assertContainsTopics(occurrences, "9");
	}

	@Test
	public void testNoResultsValue() throws IOException {
		Collection<Occurrence> occurrences = post("value", REF, "Foobar");

		Assert.assertNotNull(occurrences);
		Assert.assertTrue(occurrences.isEmpty());
	}

	@Test
	public void testPrefix() throws IOException {
		Collection<Occurrence> occurrences = post("prefix", REF, "f");

		Assert.assertNotNull(occurrences);
		Assert.assertEquals(5, occurrences.size());
		assertContainsTopics(occurrences, "2", "4", "7", "12");
	}

	@Test
	public void testPrefixDatatypeJSON() throws IOException {
		Collection<Occurrence> occurrences = post("prefix", REF, 
				createMap("value", "f", "datatype", DataTypes.TYPE_STRING.getAddress()));

		Assert.assertNotNull(occurrences);
		Assert.assertEquals(4, occurrences.size());
		assertContainsTopics(occurrences, "2", "4", "7");
	}

	@Test
	public void testPrefixDatatypeFORM() throws IOException {
		Form form = new Form();
		form.add("value", "f");
		form.add("datatype", DataTypes.TYPE_STRING.getAddress());
		Collection<Occurrence> occurrences = post("prefix", REF, form);

		Assert.assertNotNull(occurrences);
		Assert.assertEquals(4, occurrences.size());
		assertContainsTopics(occurrences, "2", "4", "7");
	}

	@Test
	public void testNoResultsPrefix() throws IOException {
		Collection<Occurrence> occurrences = post("prefix", REF, "Foobar");

		Assert.assertNotNull(occurrences);
		Assert.assertTrue(occurrences.isEmpty());
	}

	@Test
	public void testGreater() throws IOException {
		Collection<String> occurrences = post("gte", REF2, "foo");

		Assert.assertNotNull(occurrences);
		Assert.assertEquals(3, occurrences.size());
		Assert.assertTrue(occurrences.contains("foo"));
		Assert.assertTrue(occurrences.contains("foo:barbar")); // don't know why this is in there: locator
		Assert.assertTrue(occurrences.contains("作曲家"));
	}

	@Test
	public void testNoResultsGreater() throws IOException {
		Collection<String> occurrences = post("gte", REF2, "曲");

		Assert.assertNotNull(occurrences);
		Assert.assertTrue(occurrences.isEmpty());
	}

	@Test
	public void testLesser() throws IOException {
		Collection<String> occurrences = post("lte", REF2, "Z");

		Assert.assertNotNull(occurrences);
		Assert.assertEquals(1, occurrences.size());
		Assert.assertTrue(occurrences.contains("Opera"));
	}

	@Test
	public void testNullValue() throws IOException {
		assertPostFails("value", null, OntopiaRestErrors.EMPTY_ENTITY);
	}

	@Test
	public void testInvalidValueDatatypeJSON() throws IOException {
		assertPostFails("value", createMap("value", "foo", "datatype", "notauri"), OntopiaRestErrors.MALFORMED_LOCATOR);
	}

	@Test
	public void testNullPrefix() throws IOException {
		assertPostFails("prefix", null, OntopiaRestErrors.EMPTY_ENTITY);
	}

	@Test
	public void testNullLesser() throws IOException {
		assertPostFails("lte", null, OntopiaRestErrors.EMPTY_ENTITY);
	}

	@Test
	public void testEmptyLesser() throws IOException {
		assertPostFails("lte", "", OntopiaRestErrors.EMPTY_ENTITY);
	}
}
