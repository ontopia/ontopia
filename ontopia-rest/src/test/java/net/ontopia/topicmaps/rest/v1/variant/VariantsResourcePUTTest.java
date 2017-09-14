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

import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.VariantName;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import org.junit.Assert;
import org.junit.Test;

public class VariantsResourcePUTTest extends AbstractV1ResourceTest {

	public VariantsResourcePUTTest() {
		super(VARIANTS_LTM, "names");
	}

	@Test
	public void testAdd() {
		VariantName variant = new VariantName();
		variant.setValue("foo");
		VariantName added = put("2/variants", variant, VariantName.class);

		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getObjectId());
	}

	@Test
	public void testAddUnexisting() {
		VariantName variant = new VariantName();
		variant.setValue("foo");
		assertPutFails("unexisting/variants", variant, OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_NULL);
	}

	@Test
	public void testEmpty() {
		assertPutFails("2/variants", null, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testAddInvalid() {
		assertPutFails("1/variants", new VariantName(), OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}
}
