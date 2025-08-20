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

import java.net.URISyntaxException;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.Occurrence;
import net.ontopia.topicmaps.rest.model.Topic;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import org.junit.Assert;
import org.junit.Test;

public class OccurrenceResourcePUTTest extends AbstractV1ResourceTest {

	public OccurrenceResourcePUTTest() {
		super(OCCURRENCES_LTM, "occurrences");
	}
	
	/* -- Successfull requests -- */
	
	private Occurrence createOccurrence() {
		Occurrence occurrence = new Occurrence();
		occurrence.setValue("foo");
		occurrence.setTopic(new Topic("1"));
		occurrence.setType(new Topic("3"));
		return occurrence;
	}

	@Test
	public void testPUT() {
		Occurrence added = put(createOccurrence(), Occurrence.class);
		
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getObjectId());
		Assert.assertNotNull(added.getTopic());
		Assert.assertEquals("1", added.getTopic().getObjectId());
		Assert.assertNotNull(added.getType());
		Assert.assertEquals("3", added.getType().getObjectId());
		Assert.assertNotNull(added.getDataType());
		Assert.assertEquals(DataTypes.TYPE_STRING, added.getDataType());
		Assert.assertEquals("foo", added.getValue());
	}
	
	@Test
	public void testWithTopicByItemIdentifier() {
		Occurrence occurrence = createOccurrence();
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:bar#topic1"));
		occurrence.setTopic(topic);
		Occurrence added = put(occurrence, Occurrence.class);
		
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getObjectId());
		Assert.assertNotNull(added.getTopic());
		Assert.assertEquals("1", added.getTopic().getObjectId());
	}

	@Test
	public void testWithTypeByItemIdentifier() {
		Occurrence occurrence = createOccurrence();
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:bar#topic1"));
		occurrence.setType(topic);
		Occurrence added = put(occurrence, Occurrence.class);
		
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getObjectId());
		Assert.assertNotNull(added.getType());
		Assert.assertEquals("1", added.getType().getObjectId());
	}

	@Test
	public void testAlternativeDatatype() throws URISyntaxException {
		Occurrence occurrence = createOccurrence();
		occurrence.setValue("1");
		occurrence.setDatatype(DataTypes.TYPE_INTEGER);
		
		Occurrence added = put(occurrence, Occurrence.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getDataType());
		Assert.assertEquals(DataTypes.TYPE_INTEGER, added.getDataType());
		Assert.assertEquals("1", added.getValue());
	}

	@Test
	public void testAlternativeDatatype2() {
		Occurrence occurrence = createOccurrence();
		occurrence.setValue("1");
		occurrence.setDatatype(URILocator.create("dt:foo"));
		
		Occurrence added = put(occurrence, Occurrence.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getDataType());
		Assert.assertEquals(URILocator.create("dt:foo"), added.getDataType());
		Assert.assertEquals("1", added.getValue());
	}	
	
	@Test
	public void testWithItemIdentifier() {
		Occurrence occurrence = createOccurrence();
		occurrence.getItemIdentifiers().add(URILocator.create("foo:barbar"));
		
		Occurrence added = put(occurrence, Occurrence.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getItemIdentifiers());
		Assert.assertFalse(added.getItemIdentifiers().isEmpty());
		Assert.assertEquals("foo:barbar", added.getItemIdentifiers().iterator().next().getAddress());
	}	
	
	@Test
	public void testWithItemIdentifiers() {
		Occurrence occurrence = createOccurrence();
		occurrence.getItemIdentifiers().add(URILocator.create("bar:foo"));
		occurrence.getItemIdentifiers().add(URILocator.create("bar:bar"));
		
		Occurrence added = put(occurrence, Occurrence.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getItemIdentifiers());
		Assert.assertFalse(added.getItemIdentifiers().isEmpty());
		Assert.assertEquals(2, added.getItemIdentifiers().size());
		Assert.assertTrue(added.getItemIdentifiers().contains(URILocator.create("bar:foo")));
		Assert.assertTrue(added.getItemIdentifiers().contains(URILocator.create("bar:bar")));
	}	

	@Test
	public void testWithEmptyIdentifiers() {
		Occurrence occurrence = createOccurrence();
		occurrence.getItemIdentifiers().clear();
		
		Occurrence added = put(occurrence, Occurrence.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getItemIdentifiers());
		Assert.assertTrue(added.getItemIdentifiers().isEmpty());
	}	
	
	@Test
	public void testWithScope() {
		Occurrence occurrence = createOccurrence();
		occurrence.getScope().add(new Topic("1"));
		
		Occurrence added = put(occurrence, Occurrence.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getScope());
		Assert.assertFalse(added.getScope().isEmpty());
		Assert.assertEquals("1", added.getScope().iterator().next().getObjectId());
	}	
	
	@Test
	public void testWithScopes() {
		Occurrence occurrence = createOccurrence();
		occurrence.getScope().add(new Topic("1"));
		occurrence.getScope().add(new Topic("3"));
		
		Occurrence added = put(occurrence, Occurrence.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getScope());
		Assert.assertFalse(added.getScope().isEmpty());
		Assert.assertEquals(2, added.getScope().size());
		assertContainsTopics(added.getScope(), "1", "3");
	}	
	
	@Test
	public void testWithScopeByItemIdentifier() {
		Occurrence occurrence = createOccurrence();
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:bar#topic1"));
		occurrence.getScope().add(topic);
		
		Occurrence added = put(occurrence, Occurrence.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getScope());
		Assert.assertFalse(added.getScope().isEmpty());
		Assert.assertEquals(1, added.getScope().size());
		assertContainsTopics(added.getScope(), "1");
	}	
	
	@Test
	public void testWithReification() {
		Occurrence occurrence = createOccurrence();
		occurrence.setReifier(new Topic("3"));
		
		Occurrence added = put(occurrence, Occurrence.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getReifier());
		Assert.assertEquals("3", added.getReifier().getObjectId());
	}

	@Test
	public void testWithReificationByItemIdentifier() {
		Occurrence occurrence = createOccurrence();
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:bar#topic2"));
		occurrence.setReifier(topic);
		
		Occurrence added = put(occurrence, Occurrence.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getReifier());
		Assert.assertEquals("3", added.getReifier().getObjectId());
	}

	/* -- Failing requests -- */

	@Test
	public void testEmptyBody() {
		assertPutFails(null, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testWrongObjectType() {
		assertPutFails(new Topic("foobar"), OntopiaRestErrors.MANDATORY_FIELD_IS_NULL);
	}

	@Test
	public void testMissingTopic() {
		Occurrence occurrence = createOccurrence();
		occurrence.setTopic(null);
		assertPutFails(occurrence, OntopiaRestErrors.MANDATORY_FIELD_IS_NULL);
	}

	@Test
	public void testInvalidTopic() {
		Occurrence occurrence = createOccurrence();
		occurrence.setTopic(new Topic("2"));
		assertPutFails(occurrence, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingTopic() {
		Occurrence occurrence = createOccurrence();
		occurrence.setTopic(new Topic("unexisting_topic_id"));
		assertPutFails(occurrence, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testMissingType() {
		Occurrence occurrence = createOccurrence();
		occurrence.setType(null);
		assertPutFails(occurrence, OntopiaRestErrors.MANDATORY_FIELD_IS_NULL);
	}

	@Test
	public void testInvalidType() {
		Occurrence occurrence = createOccurrence();
		occurrence.setType(new Topic("2"));
		assertPutFails(occurrence, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingType() {
		Occurrence occurrence = createOccurrence();
		occurrence.setType(new Topic("unexisting_topic_id"));
		assertPutFails(occurrence, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testMissingValue() {
		Occurrence occurrence = createOccurrence();
		occurrence.setValue(null);
		assertPutFails(occurrence, OntopiaRestErrors.MANDATORY_FIELD_IS_NULL);
	}
	
	@Test
	public void testInvalidReification() {
		Occurrence occurrence = createOccurrence();
		occurrence.setReifier(new Topic("2"));
		assertPutFails(occurrence, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingReification() {
		Occurrence occurrence = createOccurrence();
		occurrence.setReifier(new Topic("unexisting_topic_id"));
		assertPutFails(occurrence, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testInvalidScope() {
		Occurrence occurrence = createOccurrence();
		occurrence.getScope().add(new Topic("2"));
		assertPutFails(occurrence, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingScope() {
		Occurrence occurrence = createOccurrence();
		occurrence.getScope().add(new Topic("unexisting_topic_id"));
		assertPutFails(occurrence, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}
}
