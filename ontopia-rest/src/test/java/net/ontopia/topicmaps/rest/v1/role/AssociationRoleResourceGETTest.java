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

package net.ontopia.topicmaps.rest.v1.role;

import java.io.IOException;
import java.util.Map;
import net.ontopia.topicmaps.rest.Constants;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.AssociationRole;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import org.junit.Assert;
import org.junit.Test;
import org.restlet.data.MediaType;

public class AssociationRoleResourceGETTest extends AbstractV1ResourceTest {

	public AssociationRoleResourceGETTest() {
		super(ROLES_LTM, "roles");
	}

	@Test
	public void testGetConverted() {
		AssociationRole role = get("3", AssociationRole.class);

		Assert.assertNotNull(role);

		Assert.assertEquals("3", role.getObjectId());

		Assert.assertNotNull(role.getType());
		Assert.assertEquals("1", role.getType().getObjectId());

		Assert.assertNotNull(role.getPlayer());
		Assert.assertEquals("1", role.getPlayer().getObjectId());
		
		Assert.assertNotNull(role.getAssociation());
		Assert.assertEquals("2", role.getAssociation().getObjectId());

		// has no reifier
		Assert.assertNull(role.getReifier());
	}

	@Test
	public void testGetJSON() throws IOException {
		Map<String, Object> parsed = getAsJson("3");
		Assert.assertNotNull(parsed);
		Assert.assertEquals("3", parsed.get("objectId"));
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
		assertGetFails("3", MediaType.TEXT_PLAIN, OntopiaRestErrors.UNSUPPORTED_MIME_TYPE);
	}
	@Test public void testGetCTM() throws IOException {
		assertGetFails("3", Constants.CTM_MEDIA_TYPE, OntopiaRestErrors.UNSUPPORTED_MIME_TYPE);
	}
	@Test public void testGetLTM() throws IOException {
		assertGetFails("3", Constants.LTM_MEDIA_TYPE, OntopiaRestErrors.UNSUPPORTED_MIME_TYPE);
	}
	@Test public void testGetXTM() throws IOException {
		assertGetFails("3", Constants.XTM_MEDIA_TYPE, OntopiaRestErrors.UNSUPPORTED_MIME_TYPE);
	}
	@Test public void testGetTMXML() throws IOException {
		assertGetFails("3", Constants.TMXML_MEDIA_TYPE, OntopiaRestErrors.UNSUPPORTED_MIME_TYPE);
	}
}
