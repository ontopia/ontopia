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
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.VariantName;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import org.junit.Assert;
import org.junit.Test;

public class VariantsResourceGETTest extends AbstractV1ResourceTest {

	private final TypeReference<Collection<VariantName>> REF = new TypeReference<Collection<VariantName>>(){};

	public VariantsResourceGETTest() {
		super(OPERA_TM, null);
	}

	@Test
	public void testVariants() throws IOException {
		Collection<VariantName> variants = get("names/568/variants", REF);

		Assert.assertNotNull(variants);
		Assert.assertEquals(1, variants.size());
		assertContainsTopics(variants, "569");
	}

	@Test
	public void testEmptyVariants() throws IOException {
		Collection<VariantName> variants = get("names/1176/variants", REF);

		Assert.assertNotNull(variants);
		Assert.assertTrue(variants.isEmpty());
	}

	/* -- Failing requests -- */

	@Test
	public void testInvalidName() {
		assertGetFails("names/1/variants", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingName() {
		assertGetFails("names/unexisting/variants", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_NULL);
	}
}
