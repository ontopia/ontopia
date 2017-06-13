/*
 * #!
 * Ontopia Rest
 * #-
 * Copyright (C) 2001 - 2016 The Ontopia Project
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

import java.io.IOException;
import java.util.Map;
import junit.framework.Assert;
import net.ontopia.topicmaps.rest.Constants;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.VariantName;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import org.junit.Test;
import org.restlet.data.MediaType;

public class VariantNameResourceGETTest extends AbstractV1ResourceTest {

	public VariantNameResourceGETTest() {
		super(OPERA_TM, "variants");
	}

	@Test
	public void testGetConverted() throws IOException {
		VariantName variant = get("5197", VariantName.class);

		Assert.assertNotNull(variant);

		Assert.assertEquals("5197", variant.getObjectId());

		Assert.assertNotNull(variant.getTopicName());
		Assert.assertEquals("5196", variant.getTopicName().getObjectId());

		Assert.assertEquals("Tabarro", variant.getValue());

		// has no reifier
		Assert.assertNull(variant.getReifier());

		// has no scope
		Assert.assertNotNull(variant.getScope());
		Assert.assertEquals(1, variant.getScope().size());
	}

	@Test
	public void testWithScopes() {
		VariantName variant = get("5327", VariantName.class);

		Assert.assertNotNull(variant.getScope());
		Assert.assertFalse(variant.getScope().isEmpty());
		Assert.assertEquals(2, variant.getScope().size());
		assertContainsTopics(variant.getScope(), "174", "29");
	}

	@Test
	public void testGetJSON() throws IOException {
		Map<String, Object> parsed = getAsJson("5197");
		Assert.assertNotNull(parsed);
		Assert.assertEquals("5197", parsed.get("objectId"));
		Assert.assertEquals("Tabarro", (String)parsed.get("value"));
	}

	// test recoverable client failures
	@Test public void testGetUnexisting() throws IOException {
		assertGetFails("foo", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_NULL);
	}
	@Test public void testGetWrongType() throws IOException {
		assertGetFails("13", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}

	// Unsupported topicmap media types: CTM, LTM, XTM, TMXML all result in http 406
	// test text/plain as a non-topicmap mime
	@Test public void testGetText() throws IOException {
		assertGetFails("5197", MediaType.TEXT_PLAIN, OntopiaRestErrors.UNSUPPORTED_MIME_TYPE);
	}
	@Test public void testGetCTM() throws IOException {
		assertGetFails("5197", Constants.CTM_MEDIA_TYPE, OntopiaRestErrors.UNSUPPORTED_MIME_TYPE);
	}
	@Test public void testGetLTM() throws IOException {
		assertGetFails("5197", Constants.LTM_MEDIA_TYPE, OntopiaRestErrors.UNSUPPORTED_MIME_TYPE);
	}
	@Test public void testGetXTM() throws IOException {
		assertGetFails("5197", Constants.XTM_MEDIA_TYPE, OntopiaRestErrors.UNSUPPORTED_MIME_TYPE);
	}
	@Test public void testGetTMXML() throws IOException {
		assertGetFails("5197", Constants.TMXML_MEDIA_TYPE, OntopiaRestErrors.UNSUPPORTED_MIME_TYPE);
	}
}
