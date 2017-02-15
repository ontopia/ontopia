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

package net.ontopia.topicmaps.rest.v1.variant;

import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.Topic;
import net.ontopia.topicmaps.rest.model.TopicName;
import net.ontopia.topicmaps.rest.model.VariantName;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import org.junit.Assert;
import org.junit.Test;

/*
	NOTE: variants inherit scope from parent topicname, see
	http://www.isotopicmaps.org/sam/sam-model/#sect-variant
	There is however, no rule that specifies that the computed scope has to have at least one topic.
*/

public class VariantNameResourcePUTTest extends AbstractV1ResourceTest {

	public VariantNameResourcePUTTest() {
		super(OPERA_TM, "variants");
	}

	/* -- Successfull requests -- */

	private VariantName createVariantName() {
		VariantName variant = new VariantName();
		variant.setValue("foo");
		variant.setTopicName(new TopicName("4"));
		return variant;
	}

	@Test
	public void testPUT() {
		VariantName added = put(createVariantName(), VariantName.class);

		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getObjectId());
		Assert.assertNotNull(added.getTopicName());
		Assert.assertEquals("4", added.getTopicName().getObjectId());
		Assert.assertEquals("foo", added.getValue());
	}

//	@Test
//	public void testWithTopicNameByItemIdentifier() {
//		VariantName variant = createVariantName();
//		TopicName name = new TopicName();
//		name.getItemIdentifiers().add(URILocator.create("foo:#network-location"));
//		variant.setTopicName(name);
//		VariantName added = put(variant, VariantName.class);
//
//		Assert.assertNotNull(added);
//		Assert.assertNotNull(added.getObjectId());
//		Assert.assertNotNull(added.getTopic());
//		Assert.assertEquals("261", added.getTopic().getObjectId());
//	}

	@Test
	public void testWithItemIdentifier() {
		VariantName variant = createVariantName();
		variant.getItemIdentifiers().add(URILocator.create("foo:variant:bar"));

		VariantName added = put(variant, VariantName.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getItemIdentifiers());
		Assert.assertFalse(added.getItemIdentifiers().isEmpty());
		Assert.assertEquals("foo:variant:bar", added.getItemIdentifiers().iterator().next().getAddress());
	}

	@Test
	public void testWithItemIdentifiers() {
		VariantName variant = createVariantName();
		variant.getItemIdentifiers().add(URILocator.create("bar:variant:foo"));
		variant.getItemIdentifiers().add(URILocator.create("bar:variant:bar"));

		VariantName added = put(variant, VariantName.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getItemIdentifiers());
		Assert.assertFalse(added.getItemIdentifiers().isEmpty());
		Assert.assertEquals(2, added.getItemIdentifiers().size());
		Assert.assertTrue(added.getItemIdentifiers().contains(URILocator.create("bar:variant:foo")));
		Assert.assertTrue(added.getItemIdentifiers().contains(URILocator.create("bar:variant:bar")));
	}

	@Test
	public void testWithEmptyIdentifiers() {
		VariantName variant = createVariantName();
		variant.getItemIdentifiers().clear();

		VariantName added = put(variant, VariantName.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getItemIdentifiers());
		Assert.assertTrue(added.getItemIdentifiers().isEmpty());
	}

	@Test
	public void testWithScope() {
		VariantName variant = createVariantName();
		variant.getScope().add(new Topic("1"));
		
		VariantName added = put(variant, VariantName.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getScope());
		Assert.assertFalse(added.getScope().isEmpty());
		Assert.assertEquals(2, added.getScope().size());
		assertContainsTopics(added.getScope(), "1", "5");
	}

	@Test
	public void testWithoutScope() {
		VariantName variant = new VariantName();
		variant.setTopicName(new TopicName("2785")); // has no scope
		variant.setValue("foo");
		variant.getScope().clear();
		
		VariantName added = put(variant, VariantName.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getScope());
		Assert.assertTrue(added.getScope().isEmpty());
	}

	@Test
	public void testWithScopes() {
		VariantName variant = createVariantName();
		variant.getScope().add(new Topic("1"));
		variant.getScope().add(new Topic("12"));

		VariantName added = put(variant, VariantName.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getScope());
		Assert.assertFalse(added.getScope().isEmpty());
		Assert.assertEquals(3, added.getScope().size());
		assertContainsTopics(added.getScope(), "1", "12", "5");
	}

	@Test
	public void testWithScopeByItemIdentifier() {
		VariantName variant = createVariantName();
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:#network-location"));
		variant.getScope().add(topic);

		VariantName added = put(variant, VariantName.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getScope());
		Assert.assertFalse(added.getScope().isEmpty());
		Assert.assertEquals(2, added.getScope().size());
		assertContainsTopics(added.getScope(), "261", "5");
	}

	@Test
	public void testWithReification() {
		VariantName variant = createVariantName();
		variant.setReifier(new Topic("909"));

		VariantName added = put(variant, VariantName.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getReifier());
		Assert.assertEquals("909", added.getReifier().getObjectId());
	}

	@Test
	public void testWithReificationByItemIdentifier() {
		VariantName variant = createVariantName();
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:#datri"));
		variant.setReifier(topic);

		VariantName added = put(variant, VariantName.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getReifier());
		Assert.assertEquals("4320", added.getReifier().getObjectId());
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
	public void testMissingTopicName() {
		VariantName variant = createVariantName();
		variant.setTopicName(null);
		assertPutFails(variant, OntopiaRestErrors.MANDATORY_FIELD_IS_NULL);
	}

	@Test
	public void testInvalidTopicName() {
		VariantName variant = createVariantName();
		variant.setTopicName(new TopicName("13")); // object with id 13 is an occurrence
		assertPutFails(variant, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingTopicName() {
		VariantName variant = createVariantName();
		variant.setTopicName(new TopicName("unexisting_topic_id"));
		assertPutFails(variant, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testMissingValue() {
		VariantName variant = createVariantName();
		variant.setValue(null);
		assertPutFails(variant, OntopiaRestErrors.MANDATORY_FIELD_IS_NULL);
	}

	@Test
	public void testInvalidReification() {
		VariantName variant = createVariantName();
		variant.setReifier(new Topic("13")); // object with id 13 is an occurrence
		assertPutFails(variant, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingReification() {
		VariantName variant = createVariantName();
		variant.setReifier(new Topic("unexisting_topic_id"));
		assertPutFails(variant, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testInvalidScope() {
		VariantName variant = createVariantName();
		variant.getScope().add(new Topic("13")); // object with id 13 is an occurrence
		assertPutFails(variant, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingScope() {
		VariantName variant = createVariantName();
		variant.getScope().add(new Topic("unexisting_topic_id"));
		assertPutFails(variant, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}
}
