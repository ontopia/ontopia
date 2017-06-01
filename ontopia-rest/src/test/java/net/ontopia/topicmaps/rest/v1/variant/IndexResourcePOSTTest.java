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

package net.ontopia.topicmaps.rest.v1.variant;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.rest.model.VariantName;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import org.junit.Assert;
import org.junit.Test;
import org.restlet.data.Form;

public class IndexResourcePOSTTest extends AbstractV1ResourceTest {
	
	private final TypeReference<Collection<VariantName>> REF = new TypeReference<Collection<VariantName>>(){};
	private final TypeReference<Collection<String>> REF2 = new TypeReference<Collection<String>>(){};

	public IndexResourcePOSTTest() {
		super(OPERA_TM, "variants/index");
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
		Collection<VariantName> variants = post(null, REF, "Si");

		Assert.assertNotNull(variants);
		Assert.assertEquals(2, variants.size());
		assertContainsTopics(variants, "3085", "5742");
	}
	
	@Test
	public void testValueDatatypeJSON() throws IOException {
		Collection<VariantName> variants = post(null, REF, 
				createMap("value", "Si", "datatype", DataTypes.TYPE_STRING.getAddress()));

		Assert.assertNotNull(variants);
		Assert.assertEquals(2, variants.size());
		assertContainsTopics(variants, "3085", "5742");
	}

	@Test
	public void testValueDatatypeFORM() throws IOException {
		Form form = new Form();
		form.add("value", "Si");
		form.add("datatype", DataTypes.TYPE_STRING.getAddress());
		Collection<VariantName> variants = post(null, REF, form);

		Assert.assertNotNull(variants);
		Assert.assertEquals(2, variants.size());
		assertContainsTopics(variants, "3085", "5742");
	}

	@Test
	public void testNoResultsValue() throws IOException {
		Collection<VariantName> variants = post(null, REF, "Foobar");

		Assert.assertNotNull(variants);
		Assert.assertTrue(variants.isEmpty());
	}
}
