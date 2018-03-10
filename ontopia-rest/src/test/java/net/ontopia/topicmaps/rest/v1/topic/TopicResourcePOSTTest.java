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
import net.ontopia.topicmaps.rest.model.Topic;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import org.junit.Assert;
import org.junit.Test;

public class TopicResourcePOSTTest extends AbstractV1ResourceTest {

	public TopicResourcePOSTTest() {
		super(TOPICS_LTM, "topics");
	}

	@Test
	public void testAddItemIdentifier() {
		Topic topic = get("1", Topic.class);
		topic.getItemIdentifiers().add(URILocator.create("foo:bartopic_add_ii"));

		Topic changed = post("1", topic, Topic.class);

		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getItemIdentifiers());
		Assert.assertEquals(2, changed.getItemIdentifiers().size());
		Assert.assertTrue(changed.getItemIdentifiers().contains(URILocator.create("foo:bar#topic1")));
		Assert.assertTrue(changed.getItemIdentifiers().contains(URILocator.create("foo:bartopic_add_ii")));
	}

	@Test
	public void testAddItemIdentifiers() {
		Topic topic = get("1", Topic.class);
		topic.getItemIdentifiers().add(URILocator.create("foo:bartopic_add_ii_2"));
		topic.getItemIdentifiers().add(URILocator.create("foo:bartopic_add_ii_3"));

		Topic changed = post("1", topic, Topic.class);

		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getItemIdentifiers());
		Assert.assertEquals(3, changed.getItemIdentifiers().size());
		Assert.assertTrue(changed.getItemIdentifiers().contains(URILocator.create("foo:bar#topic1")));
		Assert.assertTrue(changed.getItemIdentifiers().contains(URILocator.create("foo:bartopic_add_ii_2")));
		Assert.assertTrue(changed.getItemIdentifiers().contains(URILocator.create("foo:bartopic_add_ii_3")));
	}

	@Test
	public void testRemoveItemIdentifier() {
		Topic topic = get("1", Topic.class);
		topic.getItemIdentifiers().remove(URILocator.create("foo:bar#topic1"));

		Assert.assertTrue(topic.getItemIdentifiers().isEmpty());

		Topic changed = post("1", topic, Topic.class);

		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getItemIdentifiers());
		Assert.assertTrue(changed.getItemIdentifiers().isEmpty());
	}

	@Test
	public void testClearItemIdentifiers() {
		Topic topic = get("1", Topic.class);
		topic.getItemIdentifiers().clear();

		Topic changed = post("1", topic, Topic.class);

		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getItemIdentifiers());
		Assert.assertTrue(changed.getItemIdentifiers().isEmpty());
	}

	@Test
	public void testChangeItemIdentifiers() {
		Topic topic = get("1", Topic.class);
		topic.getItemIdentifiers().remove(URILocator.create("foo:bar#topic1"));
		topic.getItemIdentifiers().add(URILocator.create("foo:bartopic_add_ii_4"));

		Topic changed = post("1", topic, Topic.class);

		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getItemIdentifiers());
		Assert.assertEquals(1, changed.getItemIdentifiers().size());
		Assert.assertTrue(changed.getItemIdentifiers().contains(URILocator.create("foo:bartopic_add_ii_4")));
	}

	@Test
	public void testVoidItemIdentifiers() {
		Topic topic = get("1", Topic.class);
		topic.setItemIdentifiers(null);

		Topic changed = post("1", topic, Topic.class);

		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getItemIdentifiers());
		Assert.assertEquals(1, changed.getItemIdentifiers().size());
		Assert.assertTrue(changed.getItemIdentifiers().contains(URILocator.create("foo:bar#topic1")));
	}

	@Test
	public void testAddSubjectIdentifier() {
		Topic topic = get("8", Topic.class);
		topic.getSubjectIdentifiers().add(URILocator.create("foo:bartopic_add_si"));

		Topic changed = post("8", topic, Topic.class);

		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getSubjectIdentifiers());
		Assert.assertEquals(1, changed.getSubjectIdentifiers().size());
		Assert.assertTrue(changed.getSubjectIdentifiers().contains(URILocator.create("foo:bartopic_add_si")));
	}

	@Test
	public void testAddSubjectIdentifiers() {
		Topic topic = get("8", Topic.class);
		topic.getSubjectIdentifiers().add(URILocator.create("foo:bartopic_add_si_2"));
		topic.getSubjectIdentifiers().add(URILocator.create("foo:bartopic_add_si_3"));

		Topic changed = post("8", topic, Topic.class);

		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getSubjectIdentifiers());
		Assert.assertEquals(2, changed.getSubjectIdentifiers().size());
		Assert.assertTrue(changed.getSubjectIdentifiers().contains(URILocator.create("foo:bartopic_add_si_2")));
		Assert.assertTrue(changed.getSubjectIdentifiers().contains(URILocator.create("foo:bartopic_add_si_3")));
	}

	@Test
	public void testRemoveSubjectIdentifier() {
		Topic topic = get("1", Topic.class);
		topic.getSubjectIdentifiers().remove(URILocator.create("foo:bar"));

		Assert.assertTrue(topic.getSubjectIdentifiers().isEmpty());

		Topic changed = post("1", topic, Topic.class);

		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getSubjectIdentifiers());
		Assert.assertTrue(changed.getSubjectIdentifiers().isEmpty());
	}

	@Test
	public void testClearSubjectIdentifiers() {
		Topic topic = get("1", Topic.class);
		topic.getSubjectIdentifiers().clear();

		Topic changed = post("1", topic, Topic.class);

		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getSubjectIdentifiers());
		Assert.assertTrue(changed.getSubjectIdentifiers().isEmpty());
	}

	@Test
	public void testChangeSubjectIdentifiers() {
		Topic topic = get("1", Topic.class);
		topic.getSubjectIdentifiers().remove(URILocator.create("foo:bar"));
		topic.getSubjectIdentifiers().add(URILocator.create("foo:bartopic_add_si_4"));

		Topic changed = post("1", topic, Topic.class);

		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getSubjectIdentifiers());
		Assert.assertEquals(1, changed.getSubjectIdentifiers().size());
		Assert.assertTrue(changed.getSubjectIdentifiers().contains(URILocator.create("foo:bartopic_add_si_4")));
	}

	@Test
	public void testVoidSubjectIdentifiers() {
		Topic topic = get("1", Topic.class);
		topic.setSubjectIdentifiers(null);

		Topic changed = post("1", topic, Topic.class);

		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getSubjectIdentifiers());
		Assert.assertEquals(1, changed.getSubjectIdentifiers().size());
		Assert.assertTrue(changed.getSubjectIdentifiers().contains(URILocator.create("foo:bar")));
	}

	@Test
	public void testAddSubjectLocator() {
		Topic topic = get("1", Topic.class);
		topic.getSubjectLocators().add(URILocator.create("foo:bartopic_add_sl"));

		Topic changed = post("1", topic, Topic.class);

		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getSubjectLocators());
		Assert.assertEquals(1, changed.getSubjectLocators().size());
		Assert.assertTrue(changed.getSubjectLocators().contains(URILocator.create("foo:bartopic_add_sl")));
	}

	@Test
	public void testAddSubjectLocators() {
		Topic topic = get("1", Topic.class);
		topic.getSubjectLocators().add(URILocator.create("foo:bartopic_add_sl_2"));
		topic.getSubjectLocators().add(URILocator.create("foo:bartopic_add_sl_3"));

		Topic changed = post("1", topic, Topic.class);

		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getSubjectLocators());
		Assert.assertEquals(2, changed.getSubjectLocators().size());
		Assert.assertTrue(changed.getSubjectLocators().contains(URILocator.create("foo:bartopic_add_sl_2")));
		Assert.assertTrue(changed.getSubjectLocators().contains(URILocator.create("foo:bartopic_add_sl_3")));
	}

	@Test
	public void testRemoveSubjectLocator() {
		Topic topic = get("5", Topic.class);
		
		topic.getSubjectLocators().remove(URILocator.create("http://bar.foo/"));
		
		Assert.assertTrue(topic.getSubjectLocators().isEmpty());

		Topic changed = post("5", topic, Topic.class);

		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getSubjectLocators());
		Assert.assertTrue(changed.getSubjectLocators().isEmpty());
	}

	@Test
	public void testClearSubjectLocators() {
		Topic topic = get("5", Topic.class);
		topic.getSubjectLocators().clear();
		Topic changed = post("5", topic, Topic.class);

		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getSubjectLocators());
		Assert.assertTrue(changed.getSubjectLocators().isEmpty());
	}

	@Test
	public void testChangeSubjectLocators() {
		Topic topic = get("5", Topic.class);
		topic.getSubjectLocators().remove(URILocator.create("http://bar.foo/"));
		topic.getSubjectLocators().add(URILocator.create("foo:bartopic_add_sl_5"));

		Topic changed = post("5", topic, Topic.class);

		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getSubjectLocators());
		Assert.assertEquals(1, changed.getSubjectLocators().size());
		Assert.assertTrue(changed.getSubjectLocators().contains(URILocator.create("foo:bartopic_add_sl_5")));
	}

	@Test
	public void testVoidSubjectLocators() {
		Topic topic = get("5", Topic.class);
		topic.setSubjectLocators(null);

		Topic changed = post("5", topic, Topic.class);

		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getSubjectLocators());
		Assert.assertEquals(1, changed.getSubjectLocators().size());
		Assert.assertTrue(changed.getSubjectLocators().contains(URILocator.create("http://bar.foo/")));
	}

	@Test
	public void testAddType() {
		Topic topic = get("1", Topic.class);
		topic.getTypes().add(new Topic("5"));

		Topic changed = post("1", topic, Topic.class);
		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getTypes());
		Assert.assertEquals(2, changed.getTypes().size());
		assertContainsTopics(changed.getTypes(), "1", "5");
	}

	@Test
	public void testRemoveType() {
		Topic topic = get("1", Topic.class);
		removeById(topic.getTypes(), "1");

		Topic changed = post("1", topic, Topic.class);
		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getTypes());
		Assert.assertTrue(changed.getTypes().isEmpty());
	}

	@Test
	public void testChangeType() {
		Topic topic = get("1", Topic.class);
		removeById(topic.getTypes(), "1");
		topic.getTypes().add(new Topic("5"));

		Topic changed = post("1", topic, Topic.class);
		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getTypes());
		Assert.assertEquals(1, changed.getTypes().size());
		assertContainsTopics(changed.getTypes(), "5");
	}

	@Test
	public void testClearTypes() {
		Topic topic = get("1", Topic.class);
		topic.getTypes().clear();

		Topic changed = post("1", topic, Topic.class);
		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getTypes());
		Assert.assertTrue(changed.getTypes().isEmpty());
	}

	@Test
	public void testAddTypeByItemIdentifier() {
		Topic topic = get("1", Topic.class);
		Topic type = new Topic();
		type.getItemIdentifiers().add(URILocator.create("foo:bar#topic4"));
		topic.getTypes().add(type);

		Topic changed = post("1", topic, Topic.class);
		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getTypes());
		Assert.assertEquals(2, changed.getTypes().size());
		assertContainsTopics(changed.getTypes(), "1", "8");
	}

	/* -- Failing requests -- */

	@Test
	public void testInvalidType() {
		Topic topic = get("1", Topic.class);
		topic.getTypes().add(new Topic("2"));
		assertPostFails("1", topic, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingType() {
		Topic topic = get("1", Topic.class);
		topic.getTypes().add(new Topic("unexisting_topic_id"));
		assertPostFails("1", topic, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}
}
