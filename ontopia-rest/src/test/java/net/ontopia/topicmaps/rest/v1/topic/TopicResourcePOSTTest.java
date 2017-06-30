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
import static net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest.OPERA_TM;
import org.junit.Assert;
import org.junit.Test;

public class TopicResourcePOSTTest extends AbstractV1ResourceTest {

	public TopicResourcePOSTTest() {
		super(OPERA_TM, "topics");
	}

	@Test
	public void testAddItemIdentifier() {
		Topic topic = get("1613", Topic.class);
		topic.getItemIdentifiers().add(URILocator.create("foo:topic_add_ii"));

		Topic changed = post("1613", topic, Topic.class);

		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getItemIdentifiers());
		Assert.assertEquals(2, changed.getItemIdentifiers().size());
		Assert.assertTrue(changed.getItemIdentifiers().contains(URILocator.create("foo:#freddie")));
		Assert.assertTrue(changed.getItemIdentifiers().contains(URILocator.create("foo:topic_add_ii")));
	}

	@Test
	public void testAddItemIdentifiers() {
		Topic topic = get("2246", Topic.class);
		topic.getItemIdentifiers().add(URILocator.create("foo:topic_add_ii_2"));
		topic.getItemIdentifiers().add(URILocator.create("foo:topic_add_ii_3"));

		Topic changed = post("2246", topic, Topic.class);

		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getItemIdentifiers());
		Assert.assertEquals(3, changed.getItemIdentifiers().size());
		Assert.assertTrue(changed.getItemIdentifiers().contains(URILocator.create("foo:#le-comte-de-toulouse")));
		Assert.assertTrue(changed.getItemIdentifiers().contains(URILocator.create("foo:topic_add_ii_2")));
		Assert.assertTrue(changed.getItemIdentifiers().contains(URILocator.create("foo:topic_add_ii_3")));
	}

	@Test
	public void testRemoveItemIdentifier() {
		Topic topic = get("827", Topic.class);
		topic.getItemIdentifiers().remove(URILocator.create("foo:#amantio"));

		Assert.assertTrue(topic.getItemIdentifiers().isEmpty());

		Topic changed = post("827", topic, Topic.class);

		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getItemIdentifiers());
		Assert.assertTrue(changed.getItemIdentifiers().isEmpty());
	}

	@Test
	public void testClearItemIdentifiers() {
		Topic topic = get("1159", Topic.class);
		topic.getItemIdentifiers().clear();

		Topic changed = post("1159", topic, Topic.class);

		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getItemIdentifiers());
		Assert.assertTrue(changed.getItemIdentifiers().isEmpty());
	}

	@Test
	public void testChangeItemIdentifiers() {
		Topic topic = get("635", Topic.class);
		topic.getItemIdentifiers().remove(URILocator.create("foo:#nel-verde-maggio"));
		topic.getItemIdentifiers().add(URILocator.create("foo:topic_add_ii_4"));

		Topic changed = post("635", topic, Topic.class);

		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getItemIdentifiers());
		Assert.assertEquals(1, changed.getItemIdentifiers().size());
		Assert.assertTrue(changed.getItemIdentifiers().contains(URILocator.create("foo:topic_add_ii_4")));
	}

	@Test
	public void testVoidItemIdentifiers() {
		Topic topic = get("3729", Topic.class);
		topic.setItemIdentifiers(null);

		Topic changed = post("3729", topic, Topic.class);

		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getItemIdentifiers());
		Assert.assertEquals(1, changed.getItemIdentifiers().size());
		Assert.assertTrue(changed.getItemIdentifiers().contains(URILocator.create("foo:#rennes")));
	}

	@Test
	public void testAddSubjectIdentifier() {
		Topic topic = get("1613", Topic.class);
		topic.getSubjectIdentifiers().add(URILocator.create("foo:topic_add_si"));

		Topic changed = post("1613", topic, Topic.class);

		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getSubjectIdentifiers());
		Assert.assertEquals(1, changed.getSubjectIdentifiers().size());
		Assert.assertTrue(changed.getSubjectIdentifiers().contains(URILocator.create("foo:topic_add_si")));
	}

	@Test
	public void testAddSubjectIdentifiers() {
		Topic topic = get("2246", Topic.class);
		topic.getSubjectIdentifiers().add(URILocator.create("foo:topic_add_si_2"));
		topic.getSubjectIdentifiers().add(URILocator.create("foo:topic_add_si_3"));

		Topic changed = post("2246", topic, Topic.class);

		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getSubjectIdentifiers());
		Assert.assertEquals(2, changed.getSubjectIdentifiers().size());
		Assert.assertTrue(changed.getSubjectIdentifiers().contains(URILocator.create("foo:topic_add_si_2")));
		Assert.assertTrue(changed.getSubjectIdentifiers().contains(URILocator.create("foo:topic_add_si_3")));
	}

	@Test
	public void testRemoveSubjectIdentifier() {
		Topic topic = get("3729", Topic.class);
		topic.getSubjectIdentifiers().remove(URILocator.create("http://psi.ontopedia.net/Rennes"));

		Assert.assertTrue(topic.getSubjectIdentifiers().isEmpty());

		Topic changed = post("3729", topic, Topic.class);

		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getSubjectIdentifiers());
		Assert.assertTrue(changed.getSubjectIdentifiers().isEmpty());
	}

	@Test
	public void testClearSubjectIdentifiers() {
		Topic topic = get("5781", Topic.class);
		topic.getSubjectIdentifiers().clear();

		Topic changed = post("5781", topic, Topic.class);

		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getSubjectIdentifiers());
		Assert.assertTrue(changed.getSubjectIdentifiers().isEmpty());
	}

	@Test
	public void testChangeSubjectIdentifiers() {
		Topic topic = get("4078", Topic.class);
		topic.getSubjectIdentifiers().remove(URILocator.create("http://www.topicmaps.org/xtm/1.0/country.xtm#PL"));
		topic.getSubjectIdentifiers().add(URILocator.create("foo:topic_add_si_4"));

		Topic changed = post("4078", topic, Topic.class);

		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getSubjectIdentifiers());
		Assert.assertEquals(1, changed.getSubjectIdentifiers().size());
		Assert.assertTrue(changed.getSubjectIdentifiers().contains(URILocator.create("foo:topic_add_si_4")));
	}

	@Test
	public void testVoidSubjectIdentifiers() {
		Topic topic = get("152", Topic.class);
		topic.setSubjectIdentifiers(null);

		Topic changed = post("152", topic, Topic.class);

		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getSubjectIdentifiers());
		Assert.assertEquals(1, changed.getSubjectIdentifiers().size());
		Assert.assertTrue(changed.getSubjectIdentifiers().contains(URILocator.create("http://psi.ontopedia.net/Poem")));
	}

	@Test
	public void testAddSubjectLocator() {
		Topic topic = get("1613", Topic.class);
		topic.getSubjectLocators().add(URILocator.create("foo:topic_add_sl"));

		Topic changed = post("1613", topic, Topic.class);

		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getSubjectLocators());
		Assert.assertEquals(1, changed.getSubjectLocators().size());
		Assert.assertTrue(changed.getSubjectLocators().contains(URILocator.create("foo:topic_add_sl")));
	}

	@Test
	public void testAddSubjectLocators() {
		Topic topic = get("2246", Topic.class);
		topic.getSubjectLocators().add(URILocator.create("foo:topic_add_sl_2"));
		topic.getSubjectLocators().add(URILocator.create("foo:topic_add_sl_3"));

		Topic changed = post("2246", topic, Topic.class);

		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getSubjectLocators());
		Assert.assertEquals(2, changed.getSubjectLocators().size());
		Assert.assertTrue(changed.getSubjectLocators().contains(URILocator.create("foo:topic_add_sl_2")));
		Assert.assertTrue(changed.getSubjectLocators().contains(URILocator.create("foo:topic_add_sl_3")));
	}

	@Test
	public void testRemoveSubjectLocator() {
		Topic topic = get("3213", Topic.class);
		
		// first add
		topic.getSubjectLocators().add(URILocator.create("http://example.com/to-remove"));
		Topic changed = post("3213", topic, Topic.class);

		Assert.assertFalse(changed.getSubjectLocators().isEmpty());

		// then remove
		changed.getSubjectLocators().remove(URILocator.create("http://example.com/to-remove"));
		
		Assert.assertTrue(changed.getSubjectLocators().isEmpty());

		changed = post("3213", changed, Topic.class);

		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getSubjectLocators());
		Assert.assertTrue(changed.getSubjectLocators().isEmpty());
	}

	@Test
	public void testClearSubjectLocators() {
		Topic topic = get("5781", Topic.class);

		// first add
		topic.getSubjectLocators().add(URILocator.create("foo:topic_add_sl_remove"));
		Topic changed = post("5781", topic, Topic.class);

		Assert.assertFalse(changed.getSubjectLocators().isEmpty());

		// now clear
		changed.getSubjectLocators().clear();
		changed = post("5781", changed, Topic.class);

		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getSubjectLocators());
		Assert.assertTrue(changed.getSubjectLocators().isEmpty());
	}

	@Test
	public void testChangeSubjectLocators() {
		Topic topic = get("4078", Topic.class);

		// first add
		topic.getSubjectLocators().add(URILocator.create("foo:topic_add_sl_remove"));
		Topic changed = post("4078", topic, Topic.class);

		Assert.assertFalse(changed.getSubjectLocators().isEmpty());

		// change
		changed.getSubjectLocators().remove(URILocator.create("foo:topic_add_sl_remove"));
		changed.getSubjectLocators().add(URILocator.create("foo:topic_add_sl_5"));
		changed = post("4078", changed, Topic.class);

		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getSubjectLocators());
		Assert.assertEquals(1, changed.getSubjectLocators().size());
		Assert.assertTrue(changed.getSubjectLocators().contains(URILocator.create("foo:topic_add_sl_5")));
	}

	@Test
	public void testVoidSubjectLocators() {
		Topic topic = get("152", Topic.class);

		// first add
		topic.getSubjectLocators().add(URILocator.create("foo:topic_add_sl_6"));
		Topic changed = post("152", topic, Topic.class);

		Assert.assertFalse(changed.getSubjectLocators().isEmpty());

		// void it
		changed.setSubjectLocators(null);
		changed = post("152", topic, Topic.class);

		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getSubjectLocators());
		Assert.assertEquals(1, changed.getSubjectLocators().size());
		Assert.assertTrue(changed.getSubjectLocators().contains(URILocator.create("foo:topic_add_sl_6")));
	}

	@Test
	public void testAddType() {
		Topic topic = get("3329", Topic.class);
		topic.getTypes().add(new Topic("1"));

		Topic changed = post("3329", topic, Topic.class);
		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getTypes());
		Assert.assertEquals(2, changed.getTypes().size());
		assertContainsTopics(changed.getTypes(), "1", "165");
	}

	@Test
	public void testRemoveType() {
		Topic topic = get("2772", Topic.class);
		removeById(topic.getTypes(), "165");

		Topic changed = post("2772", topic, Topic.class);
		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getTypes());
		Assert.assertTrue(changed.getTypes().isEmpty());
	}

	@Test
	public void testChangeType() {
		Topic topic = get("1095", Topic.class);
		removeById(topic.getTypes(), "165");
		topic.getTypes().add(new Topic("1"));

		Topic changed = post("1095", topic, Topic.class);
		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getTypes());
		Assert.assertEquals(1, changed.getTypes().size());
		assertContainsTopics(changed.getTypes(), "1");
	}

	@Test
	public void testClearTypes() {
		Topic topic = get("2880", Topic.class);
		topic.getTypes().clear();

		Topic changed = post("2880", topic, Topic.class);
		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getTypes());
		Assert.assertTrue(changed.getTypes().isEmpty());
	}

	@Test
	public void testAddTypeByItemIdentifier() {
		Topic topic = get("177", Topic.class);
		Topic type = new Topic();
		type.getItemIdentifiers().add(URILocator.create("foo:#radu"));
		topic.getTypes().add(type);

		Topic changed = post("177", topic, Topic.class);
		Assert.assertNotNull(changed);
		Assert.assertNotNull(changed.getTypes());
		Assert.assertEquals(2, changed.getTypes().size());
		assertContainsTopics(changed.getTypes(), "2880", "235");
	}

	/* -- Failing requests -- */

	@Test
	public void testInvalidType() {
		Topic topic = get("177", Topic.class);
		topic.getTypes().add(new Topic("13"));
		assertPostFails("177", topic, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingType() {
		Topic topic = get("177", Topic.class);
		topic.getTypes().add(new Topic("unexisting_topic_id"));
		assertPostFails("177", topic, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}
}
