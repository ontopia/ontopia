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

package net.ontopia.topicmaps.rest.v1.association;

import java.io.IOException;
import java.util.Map;
import net.ontopia.topicmaps.rest.Constants;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.Association;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import org.junit.Assert;
import org.junit.Test;
import org.restlet.data.MediaType;

public class AssociationResourceGETTest extends AbstractV1ResourceTest {

	public AssociationResourceGETTest() {
		super(ASSOCIATIONS_LTM, "associations");
	}

	@Test
	public void testGetConverted() {
		Association association = get("2", Association.class);

		Assert.assertNotNull(association);

		Assert.assertEquals("2", association.getObjectId());

		Assert.assertNotNull(association.getType());
		Assert.assertEquals("1", association.getType().getObjectId());

		Assert.assertNotNull(association.getRoles());
		Assert.assertEquals(1, association.getRoles().size());
		
		// has no reifier
		Assert.assertNull(association.getReifier());

		// has no scope
		Assert.assertNotNull(association.getScope());
		Assert.assertTrue(association.getScope().isEmpty());
	}

	public void testReified() {
		Association association = get("5", Association.class);

		Assert.assertNotNull(association.getReifier());
		Assert.assertEquals("4", association.getReifier().getObjectId());
	}

	@Test
	public void testWithScope() {
		Association association = get("5", Association.class);

		Assert.assertNotNull(association.getScope());
		Assert.assertFalse(association.getScope().isEmpty());
		Assert.assertEquals(1, association.getScope().size());
		assertContainsTopics(association.getScope(), "4");
	}

	@Test
	public void testGetJSON() throws IOException {
		Map<String, Object> parsed = getAsJson("2");
		Assert.assertNotNull(parsed);
		Assert.assertEquals("2", parsed.get("objectId"));
	}

	// test recoverable client failures
	@Test public void testGetUnexisting() throws IOException {
		assertGetFails("foo", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_NULL);
	}
	@Test public void testGetWrongType() throws IOException {
		assertGetFails("1", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}

	// Unsupported topicmap media types: CTM, LTM, XTM, TMXML all result in http 406
	// test text/plain as a non-topicmap mime
	@Test public void testGetText() throws IOException {
		assertGetFails("2", MediaType.TEXT_PLAIN, OntopiaRestErrors.UNSUPPORTED_MIME_TYPE);
	}
	@Test public void testGetCTM() throws IOException {
		assertGetFails("2", Constants.CTM_MEDIA_TYPE, OntopiaRestErrors.UNSUPPORTED_MIME_TYPE);
	}
	@Test public void testGetLTM() throws IOException {
		assertGetFails("2", Constants.LTM_MEDIA_TYPE, OntopiaRestErrors.UNSUPPORTED_MIME_TYPE);
	}
	@Test public void testGetXTM() throws IOException {
		assertGetFails("2", Constants.XTM_MEDIA_TYPE, OntopiaRestErrors.UNSUPPORTED_MIME_TYPE);
	}
	@Test public void testGetTMXML() throws IOException {
		assertGetFails("2", Constants.TMXML_MEDIA_TYPE, OntopiaRestErrors.UNSUPPORTED_MIME_TYPE);
	}
}
