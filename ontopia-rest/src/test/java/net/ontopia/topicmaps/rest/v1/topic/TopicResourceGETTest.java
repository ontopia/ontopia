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

package net.ontopia.topicmaps.rest.v1.topic;

import java.io.IOException;
import java.util.Map;
import org.junit.Assert;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.rest.Constants;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.Topic;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import net.ontopia.topicmaps.xml.TMXMLReader;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import org.junit.Ignore;
import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.xml.sax.InputSource;

public class TopicResourceGETTest extends AbstractV1ResourceTest {

	public TopicResourceGETTest() {
		super(TOPICS_LTM, "topics");
	}

	@Test
	public void testGetConverted() throws IOException {
		Topic topic = get("1", Topic.class);

		Assert.assertNotNull(topic);

		Assert.assertEquals("1", topic.getObjectId());

		Assert.assertNotNull(topic.getItemIdentifiers());
		Assert.assertEquals("foo:bar#topic1", topic.getItemIdentifiers().iterator().next().getAddress());

		Assert.assertNotNull(topic.getSubjectIdentifiers());
		Assert.assertEquals("foo:bar", topic.getSubjectIdentifiers().iterator().next().getAddress());

		Assert.assertNotNull(topic.getTypes());
		Assert.assertEquals(1, topic.getTypes().size());
		assertContainsTopics(topic.getTypes(), "1");

		Assert.assertNotNull(topic.getTopicNames());
		Assert.assertEquals(1, topic.getTopicNames().size());

		Assert.assertNotNull(topic.getOccurrences());
		Assert.assertEquals(1, topic.getOccurrences().size());
	}

	@Test
	@Ignore
	public void testWithReified() {
		Topic topic = get("1", Topic.class);

		// currently ignored field, due to failure to deserialize
		Assert.assertNotNull(topic.getReified());
	}

	@Test
	public void testWithSubjectLocator() {
		Topic topic = get("5", Topic.class);

		Assert.assertNotNull(topic.getSubjectLocators());
		Assert.assertEquals("http://bar.foo/", topic.getSubjectLocators().iterator().next().getAddress());
	}

	@Test
	public void testWithMultipleTypes() {
		Topic topic = get("7", Topic.class);

		Assert.assertNotNull(topic.getTypes());
		Assert.assertEquals(2, topic.getTypes().size());
		assertContainsTopics(topic.getTypes(), "1", "5");
	}

	@Test
	public void testWithUnTyped() {
		Topic topic = get("8", Topic.class);

		Assert.assertNotNull(topic.getTypes());
		Assert.assertTrue(topic.getTypes().isEmpty());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testGetJSON() throws IOException {
		Map<String, Object> parsed = getAsJson("1");
		Assert.assertNotNull(parsed);
		Assert.assertEquals("1", parsed.get("objectId"));

		// raw reified does work, but there is no class information
		Assert.assertEquals("4", ((Map<String, Object>) parsed.get("reified")).get("objectId"));
	}

	@Test
	public void testGetXTM() throws IOException {
		Representation xtm = getRaw("1", Constants.XTM_MEDIA_TYPE);

		try {
			Assert.assertNotNull(xtm);
			XTMTopicMapReader reader = new XTMTopicMapReader(xtm.getReader(), URILocator.create("foo:bar"));
			reader.setValidation(false); // ignore foo:bar url failures
			TopicMapIF tm = reader.read();
			Assert.assertNotNull(tm.getObjectByItemIdentifier(URILocator.create("foo:bar#topic1")));
		} finally {
			xtm.release();
		}
	}

	@Test
	public void testGetTMXML() throws IOException {
		Representation tmxml = getRaw("5", Constants.TMXML_MEDIA_TYPE);
		try {
			Assert.assertNotNull(tmxml);
			TopicMapIF tm = new TMXMLReader(new InputSource(tmxml.getReader()), URILocator.create("foo:bar")).read();
			Assert.assertNotNull(tm.getObjectByItemIdentifier(URILocator.create("foo:bar#topic2")));
		} finally {
			tmxml.release();
		}
	}

	// test recoverable client failures
	@Test public void testGetUnexisting() throws IOException {
		assertGetFails("foo", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_NULL);
	}
	@Test public void testGetWrongType() throws IOException {
		assertGetFails("2", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}

	// Unsupported topicmap media types: CTM and LTM all result in http 406
	// test text/plain as a non-topicmap mime
	@Test public void testGetText() throws IOException {
		assertGetFails("1", MediaType.TEXT_PLAIN, OntopiaRestErrors.UNSUPPORTED_MIME_TYPE);
	}
	@Test public void testGetCTM() throws IOException {
		assertGetFails("1", Constants.CTM_MEDIA_TYPE, OntopiaRestErrors.UNSUPPORTED_MIME_TYPE);
	}
	@Test public void testGetLTM() throws IOException {
		assertGetFails("1", Constants.LTM_MEDIA_TYPE, OntopiaRestErrors.UNSUPPORTED_MIME_TYPE);
	}
}
