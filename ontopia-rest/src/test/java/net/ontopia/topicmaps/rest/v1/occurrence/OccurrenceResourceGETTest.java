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

package net.ontopia.topicmaps.rest.v1.occurrence;

import java.io.IOException;
import java.util.Map;
import junit.framework.Assert;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.rest.Constants;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.Occurrence;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import org.junit.Test;
import org.restlet.data.MediaType;

public class OccurrenceResourceGETTest extends AbstractV1ResourceTest {

	public OccurrenceResourceGETTest() {
		super(OPERA_TM, "occurrences");
	}
	
	@Test
	public void testGetConverted() throws IOException {
		Occurrence occurrence = get("13", Occurrence.class);
		
		Assert.assertNotNull(occurrence);
		
		Assert.assertEquals("13", occurrence.getObjectId());
		
		Assert.assertNotNull(occurrence.getType());
		Assert.assertEquals("12", occurrence.getType().getObjectId());
		
		Assert.assertNotNull(occurrence.getTopic());
		Assert.assertEquals("1", occurrence.getTopic().getObjectId());
		
		Assert.assertTrue(occurrence.getValue().startsWith("Copyright (c) 1999-2009, Steve Pepper"));
		Assert.assertTrue(occurrence.getValue().endsWith("this copyright notice is left intact."));
		Assert.assertEquals(226, occurrence.getLength());
		
		Assert.assertNotNull(occurrence.getDataType());
		Assert.assertEquals(DataTypes.TYPE_STRING, occurrence.getDataType());

		Assert.assertNull(occurrence.getLocator());
		
		// has no reifier
		Assert.assertNull(occurrence.getReifier());
		
		// has no scope
		Assert.assertNotNull(occurrence.getScope());
		Assert.assertTrue(occurrence.getScope().isEmpty());
	}
	
	@Test
	public void testWithScope() {
		Occurrence occurrence = get("4917", Occurrence.class);
		
		Assert.assertNotNull(occurrence.getScope());
		Assert.assertFalse(occurrence.getScope().isEmpty());
		Assert.assertEquals(1, occurrence.getScope().size());
		Assert.assertEquals("143", occurrence.getScope().iterator().next().getObjectId());
	}
	
	@Test
	public void testWithScopes() {
		Occurrence occurrence = get("5767", Occurrence.class);
		
		Assert.assertNotNull(occurrence.getScope());
		Assert.assertFalse(occurrence.getScope().isEmpty());
		Assert.assertEquals(3, occurrence.getScope().size());
		
		assertContainsTopics(occurrence.getScope(), "182", "143", "4019");
	}
	
	@Test
	public void testWithReifier() {
		Occurrence occurrence = get("145", Occurrence.class);
		
		Assert.assertNotNull(occurrence.getReifier());
		Assert.assertEquals("146", occurrence.getReifier().getObjectId());
	}
	
	@Test
	public void testGetJSON() throws IOException {
		Map<String, Object> parsed = getAsJson("13");
		Assert.assertNotNull(parsed);
		Assert.assertEquals("13", parsed.get("objectId"));
		Assert.assertTrue(((String)parsed.get("value")).startsWith("Copyright (c) 1999-2009, Steve Pepper"));
		Assert.assertTrue(((String)parsed.get("value")).endsWith("this copyright notice is left intact."));
	}
	
	// test recoverable client failures
	@Test public void testGetUnexisting() throws IOException {
		assertGetFails("foo", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_NULL);
	}
	@Test public void testGetWrongType() throws IOException {
		assertGetFails("14", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}

	// Unsupported topicmap media types: CTM, LTM, XTM, TMXML all result in http 406
	// test text/plain as a non-topicmap mime
	@Test public void testGetText() throws IOException {
		assertGetFails("13", MediaType.TEXT_PLAIN, OntopiaRestErrors.UNSUPPORTED_MIME_TYPE);
	}
	@Test public void testGetCTM() throws IOException {
		assertGetFails("13", Constants.CTM_MEDIA_TYPE, OntopiaRestErrors.UNSUPPORTED_MIME_TYPE);
	}
	@Test public void testGetLTM() throws IOException {
		assertGetFails("13", Constants.LTM_MEDIA_TYPE, OntopiaRestErrors.UNSUPPORTED_MIME_TYPE);
	}
	@Test public void testGetXTM() throws IOException {
		assertGetFails("13", Constants.XTM_MEDIA_TYPE, OntopiaRestErrors.UNSUPPORTED_MIME_TYPE);
	}
	@Test public void testGetTMXML() throws IOException {
		assertGetFails("13", Constants.TMXML_MEDIA_TYPE, OntopiaRestErrors.UNSUPPORTED_MIME_TYPE);
	}
}
