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

package net.ontopia.topicmaps.rest.v1.name;

import java.io.IOException;
import java.util.Map;
import net.ontopia.topicmaps.rest.Constants;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.TopicName;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import org.junit.Assert;
import org.junit.Test;
import org.restlet.data.MediaType;

public class TopicNameResourceGETTest extends AbstractV1ResourceTest {

	public TopicNameResourceGETTest() {
		super(NAMES_LTM, "names");
	}
	
	@Test
	public void testGetConverted() throws IOException {
		TopicName name = get("2", TopicName.class);
		
		Assert.assertNotNull(name);
		
		Assert.assertEquals("2", name.getObjectId());
		
		Assert.assertNotNull(name.getType());
		Assert.assertEquals("3", name.getType().getObjectId());
		
		Assert.assertNotNull(name.getTopic());
		Assert.assertEquals("1", name.getTopic().getObjectId());
		
		Assert.assertEquals("Topic 1", name.getValue());
		
		// has no reifier
		Assert.assertNull(name.getReifier());
		
		// has no scope
		Assert.assertNotNull(name.getScope());
		Assert.assertTrue(name.getScope().isEmpty());
		
		// has no variants
		Assert.assertNotNull(name.getVariants());
		Assert.assertTrue(name.getVariants().isEmpty());
	}
	
	@Test
	public void testWithScope() {
		TopicName name = get("5", TopicName.class);
		
		Assert.assertNotNull(name.getScope());
		Assert.assertFalse(name.getScope().isEmpty());
		Assert.assertEquals(2, name.getScope().size());
		assertContainsTopics(name.getScope(), "1", "6");
	}
	
	@Test
	public void testGetJSON() throws IOException {
		Map<String, Object> parsed = getAsJson("2");
		Assert.assertNotNull(parsed);
		Assert.assertEquals("2", parsed.get("objectId"));
		Assert.assertEquals("Topic 1", (String)parsed.get("value"));
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
