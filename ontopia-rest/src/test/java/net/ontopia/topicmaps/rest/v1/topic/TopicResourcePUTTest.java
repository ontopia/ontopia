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

import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.Occurrence;
import net.ontopia.topicmaps.rest.model.Topic;
import net.ontopia.topicmaps.rest.model.TopicName;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import org.junit.Assert;
import org.junit.Test;

public class TopicResourcePUTTest extends AbstractV1ResourceTest {

	public TopicResourcePUTTest() {
		super(TOPICS_LTM, "topics");
	}
	
	@Test
	public void testPUT() {
		Topic added = put(new Topic(), Topic.class);
		
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getObjectId());
		
		Assert.assertNotNull(added.getItemIdentifiers());
		Assert.assertNotNull(added.getSubjectIdentifiers());
		Assert.assertNotNull(added.getSubjectLocators());
		Assert.assertNotNull(added.getTopicNames());
		Assert.assertNotNull(added.getOccurrences());
		Assert.assertNotNull(added.getRoles());
	}
	
	@Test
	public void testWithItemIdentifier() {
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:bartopic_ii"));
		
		Topic added = put(topic, Topic.class);
		Assert.assertNotNull(added.getItemIdentifiers());
		Assert.assertEquals(1, added.getItemIdentifiers().size());
		Assert.assertTrue(added.getItemIdentifiers().contains(URILocator.create("foo:bartopic_ii")));
	}
	
	@Test
	public void testWithItemIdentifiers() {
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:bartopic_ii2"));
		topic.getItemIdentifiers().add(URILocator.create("foo:bartopic_ii3"));
		
		Topic added = put(topic, Topic.class);
		Assert.assertNotNull(added.getItemIdentifiers());
		Assert.assertEquals(2, added.getItemIdentifiers().size());
		Assert.assertTrue(added.getItemIdentifiers().contains(URILocator.create("foo:bartopic_ii2")));
		Assert.assertTrue(added.getItemIdentifiers().contains(URILocator.create("foo:bartopic_ii3")));
	}
	
	@Test
	public void testWithSubjectIdentifier() {
		Topic topic = new Topic();
		topic.getSubjectIdentifiers().add(URILocator.create("foo:bartopic_si"));
		
		Topic added = put(topic, Topic.class);
		Assert.assertNotNull(added.getSubjectIdentifiers());
		Assert.assertEquals(1, added.getSubjectIdentifiers().size());
		Assert.assertTrue(added.getSubjectIdentifiers().contains(URILocator.create("foo:bartopic_si")));
	}
	
	@Test
	public void testWithSubjectIdentifiers() {
		Topic topic = new Topic();
		topic.getSubjectIdentifiers().add(URILocator.create("foo:bartopic_si2"));
		topic.getSubjectIdentifiers().add(URILocator.create("foo:bartopic_si3"));
		
		Topic added = put(topic, Topic.class);
		Assert.assertNotNull(added.getSubjectIdentifiers());
		Assert.assertEquals(2, added.getSubjectIdentifiers().size());
		Assert.assertTrue(added.getSubjectIdentifiers().contains(URILocator.create("foo:bartopic_si2")));
		Assert.assertTrue(added.getSubjectIdentifiers().contains(URILocator.create("foo:bartopic_si3")));
	}
	
	@Test
	public void testWithSubjectLocator() {
		Topic topic = new Topic();
		topic.getSubjectLocators().add(URILocator.create("foo:bartopic_sl"));
		
		Topic added = put(topic, Topic.class);
		Assert.assertNotNull(added.getSubjectLocators());
		Assert.assertEquals(1, added.getSubjectLocators().size());
		Assert.assertTrue(added.getSubjectLocators().contains(URILocator.create("foo:bartopic_sl")));
	}
	
	@Test
	public void testWithSubjectLocators() {
		Topic topic = new Topic();
		topic.getSubjectLocators().add(URILocator.create("foo:bartopic_sl2"));
		topic.getSubjectLocators().add(URILocator.create("foo:bartopic_sl3"));
		
		Topic added = put(topic, Topic.class);
		Assert.assertNotNull(added.getSubjectLocators());
		Assert.assertEquals(2, added.getSubjectLocators().size());
		Assert.assertTrue(added.getSubjectLocators().contains(URILocator.create("foo:bartopic_sl2")));
		Assert.assertTrue(added.getSubjectLocators().contains(URILocator.create("foo:bartopic_sl3")));
	}
	
	@Test
	public void testWithType() {
		Topic topic = new Topic();
		topic.getTypes().add(new Topic("1"));
		
		Topic added = put(topic, Topic.class);
		Assert.assertNotNull(added.getTypes());
		Assert.assertEquals(1, added.getTypes().size());
		Assert.assertEquals("1", added.getTypes().iterator().next().getObjectId());
	}
	
	@Test
	public void testWithTypes() {
		Topic topic = new Topic();
		topic.getTypes().add(new Topic("1"));
		topic.getTypes().add(new Topic("5"));
		
		Topic added = put(topic, Topic.class);
		Assert.assertNotNull(added.getTypes());
		Assert.assertEquals(2, added.getTypes().size());
		assertContainsTopics(added.getTypes(), "1", "5");
	}
	
	@Test
	public void testWithTypeByIdentifier() {
		Topic topic = new Topic();
		Topic type = new Topic();
		type.getItemIdentifiers().add(URILocator.create("foo:bar#topic1"));
		topic.getTypes().add(type);
		
		Topic added = put(topic, Topic.class);
		Assert.assertNotNull(added.getTypes());
		Assert.assertEquals(1, added.getTypes().size());
		assertContainsTopics(added.getTypes(), "1");
	}
	
	@Test
	public void testWithName() {
		Topic topic = new Topic();
		TopicName name = new TopicName();
		name.setValue("foo");
		topic.getTopicNames().add(name);
		
		Topic added = put(topic, Topic.class);
		Assert.assertNotNull(added.getTopicNames());
		Assert.assertEquals(1, added.getTopicNames().size());
		Assert.assertEquals("foo", added.getTopicNames().iterator().next().getValue());
	}

	@Test
	public void testWithOccurrence() {
		Topic topic = new Topic();
		Occurrence occ = new Occurrence();
		occ.setValue("foo");
		occ.setType(new Topic("1"));
		topic.getOccurrences().add(occ);
		
		Topic added = put(topic, Topic.class);
		Assert.assertNotNull(added.getOccurrences());
		Assert.assertEquals(1, added.getOccurrences().size());
		Assert.assertEquals("foo", added.getOccurrences().iterator().next().getValue());
	}
	
	@Test
	public void testWrongObjectType() {
		Topic added = put(new TopicName("foobar"), Topic.class);
		Assert.assertNotNull(added);
	}

	/* -- Failing requests -- */

	@Test
	public void testEmptyBody() {
		assertPutFails(null, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testInvalidType() {
		Topic topic = new Topic();
		topic.getTypes().add(new Topic("2"));
		assertPutFails(topic, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingType() {
		Topic topic = new Topic();
		topic.getTypes().add(new Topic("unexisting_topic_id"));
		assertPutFails(topic, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}
}
