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

import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.Topic;
import net.ontopia.topicmaps.rest.model.TopicName;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import net.ontopia.topicmaps.utils.PSI;
import org.junit.Assert;
import org.junit.Test;

public class TopicNameResourcePUTTest extends AbstractV1ResourceTest {

	public TopicNameResourcePUTTest() {
		super(NAMES_LTM, "names");
	}

	/* -- Successfull requests -- */

	private TopicName createTopicName() {
		TopicName name = new TopicName();
		name.setValue("foo");
		name.setTopic(new Topic("6"));
		name.setType(new Topic("9"));
		return name;
	}

	@Test
	public void testPUT() {
		TopicName added = put(createTopicName(), TopicName.class);

		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getObjectId());
		Assert.assertNotNull(added.getTopic());
		Assert.assertEquals("6", added.getTopic().getObjectId());
		Assert.assertNotNull(added.getType());
		Assert.assertEquals("9", added.getType().getObjectId());
		Assert.assertEquals("foo", added.getValue());
	}

	@Test
	public void testWithTopicByItemIdentifier() {
		TopicName name = createTopicName();
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:bar#topic1"));
		name.setTopic(topic);
		TopicName added = put(name, TopicName.class);

		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getObjectId());
		Assert.assertNotNull(added.getTopic());
		Assert.assertEquals("1", added.getTopic().getObjectId());
	}

	@Test
	public void testWithTypeByItemIdentifier() {
		TopicName name = createTopicName();
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:bar#topic1"));
		name.setType(topic);
		TopicName added = put(name, TopicName.class);

		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getObjectId());
		Assert.assertNotNull(added.getType());
		Assert.assertEquals("1", added.getType().getObjectId());
	}

	@Test
	public void testWithItemIdentifier() {
		TopicName name = createTopicName();
		name.getItemIdentifiers().add(URILocator.create("foo:barbar"));

		TopicName added = put(name, TopicName.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getItemIdentifiers());
		Assert.assertFalse(added.getItemIdentifiers().isEmpty());
		Assert.assertEquals("foo:barbar", added.getItemIdentifiers().iterator().next().getAddress());
	}

	@Test
	public void testWithItemIdentifiers() {
		TopicName name = createTopicName();
		name.getItemIdentifiers().add(URILocator.create("foo:barbar"));
		name.getItemIdentifiers().add(URILocator.create("bar:foo"));

		TopicName added = put(name, TopicName.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getItemIdentifiers());
		Assert.assertFalse(added.getItemIdentifiers().isEmpty());
		Assert.assertEquals(2, added.getItemIdentifiers().size());
		Assert.assertTrue(added.getItemIdentifiers().contains(URILocator.create("foo:barbar")));
		Assert.assertTrue(added.getItemIdentifiers().contains(URILocator.create("bar:foo")));
	}

	@Test
	public void testWithEmptyIdentifiers() {
		TopicName name = createTopicName();
		name.getItemIdentifiers().clear();

		TopicName added = put(name, TopicName.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getItemIdentifiers());
		Assert.assertTrue(added.getItemIdentifiers().isEmpty());
	}

	@Test
	public void testWithScope() {
		TopicName name = createTopicName();
		name.getScope().add(new Topic("1"));

		TopicName added = put(name, TopicName.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getScope());
		Assert.assertFalse(added.getScope().isEmpty());
		Assert.assertEquals("1", added.getScope().iterator().next().getObjectId());
	}

	@Test
	public void testWithScopes() {
		TopicName name = createTopicName();
		name.getScope().add(new Topic("1"));
		name.getScope().add(new Topic("6"));

		TopicName added = put(name, TopicName.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getScope());
		Assert.assertFalse(added.getScope().isEmpty());
		Assert.assertEquals(2, added.getScope().size());
		assertContainsTopics(added.getScope(), "1", "6");
	}

	@Test
	public void testWithScopeByItemIdentifier() {
		TopicName name = createTopicName();
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:bar#topic1"));
		name.getScope().add(topic);

		TopicName added = put(name, TopicName.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getScope());
		Assert.assertFalse(added.getScope().isEmpty());
		Assert.assertEquals(1, added.getScope().size());
		assertContainsTopics(added.getScope(), "1");
	}

	@Test
	public void testWithReification() {
		TopicName name = createTopicName();
		name.setReifier(new Topic("1"));

		TopicName added = put(name, TopicName.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getReifier());
		Assert.assertEquals("1", added.getReifier().getObjectId());
	}

	@Test
	public void testWithReificationByItemIdentifier() {
		TopicName name = createTopicName();
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:bar#topic1"));
		name.setReifier(topic);

		TopicName added = put(name, TopicName.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getReifier());
		Assert.assertEquals("1", added.getReifier().getObjectId());
	}

	@Test
	public void testMissingType() {
		TopicName name = createTopicName();
		name.setType(null);
		TopicName added = put(name, TopicName.class);

		Assert.assertNotNull(added.getType());
		Assert.assertTrue(added.getType().getSubjectIdentifiers().contains(PSI.getSAMNameType()));
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
		TopicName name = createTopicName();
		name.setTopic(null);
		assertPutFails(name, OntopiaRestErrors.MANDATORY_FIELD_IS_NULL);
	}

	@Test
	public void testInvalidTopic() {
		TopicName name = createTopicName();
		name.setTopic(new Topic("2"));
		assertPutFails(name, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingTopic() {
		TopicName name = createTopicName();
		name.setTopic(new Topic("unexisting_topic_id"));
		assertPutFails(name, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testInvalidType() {
		TopicName name = createTopicName();
		name.setType(new Topic("2"));
		assertPutFails(name, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingType() {
		TopicName name = createTopicName();
		name.setType(new Topic("unexisting_topic_id"));
		assertPutFails(name, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testMissingValue() {
		TopicName name = createTopicName();
		name.setValue(null);
		assertPutFails(name, OntopiaRestErrors.MANDATORY_FIELD_IS_NULL);
	}

	@Test
	public void testInvalidReification() {
		TopicName name = createTopicName();
		name.setReifier(new Topic("2"));
		assertPutFails(name, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingReification() {
		TopicName name = createTopicName();
		name.setReifier(new Topic("unexisting_topic_id"));
		assertPutFails(name, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testInvalidScope() {
		TopicName name = createTopicName();
		name.getScope().add(new Topic("2"));
		assertPutFails(name, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingScope() {
		TopicName name = createTopicName();
		name.getScope().add(new Topic("unexisting_topic_id"));
		assertPutFails(name, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}
}
