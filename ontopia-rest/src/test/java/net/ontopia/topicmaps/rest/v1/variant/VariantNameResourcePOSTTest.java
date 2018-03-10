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

package net.ontopia.topicmaps.rest.v1.variant;

import java.net.URISyntaxException;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.Topic;
import net.ontopia.topicmaps.rest.model.TopicName;
import net.ontopia.topicmaps.rest.model.VariantName;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import org.junit.Assert;
import org.junit.Test;

public class VariantNameResourcePOSTTest extends AbstractV1ResourceTest {
	
	public VariantNameResourcePOSTTest() {
		super(VARIANTS_LTM, "variants");
	}

	@Test
	public void testValue() {
		VariantName variant = get("4", VariantName.class);
		variant.setValue("Another value");
		
		VariantName changed = post("4", variant, VariantName.class);
		
		Assert.assertEquals("Another value", changed.getValue());
	}

	@Test
	public void testInvalidValue() {
		VariantName variant = get("4", VariantName.class);
		variant.setValue(null);
		
		VariantName changed = post("4", variant, VariantName.class);
		Assert.assertNotNull(changed.getValue());
	}

	@Test
	public void testDatatype() throws URISyntaxException {
		VariantName variant = get("4", VariantName.class);
		variant.setDatatype(DataTypes.TYPE_INTEGER);
		VariantName changed = post("4", variant, VariantName.class);
		Assert.assertEquals(DataTypes.TYPE_INTEGER, changed.getDataType());
	}
	
	@Test
	public void testDatatype2() {
		VariantName variant = get("4", VariantName.class);
		variant.setDatatype(URILocator.create("dt:foo"));
		VariantName changed = post("4", variant, VariantName.class);
		Assert.assertNotNull(changed.getDataType());
		Assert.assertEquals("dt:foo", changed.getDataType().getAddress());
	}
	
	@Test
	public void testClearDatatype() {
		VariantName variant = get("4", VariantName.class);
		variant.setDatatype(null);
		VariantName changed = post("4", variant, VariantName.class);
		Assert.assertEquals(DataTypes.TYPE_STRING, changed.getDataType());
	}
	
	@Test
	public void testTopicName() {
		VariantName variant = get("4", VariantName.class);
		variant.setTopicName(new TopicName("7"));

		VariantName changed = post("4", variant, VariantName.class);
		
		Assert.assertNotNull(changed.getTopicName());
		Assert.assertEquals("2", changed.getTopicName().getObjectId());
	}
	
	@Test
	public void testInvalidTopicName() {
		VariantName variant = get("4", VariantName.class);
		variant.setTopicName(new TopicName("1"));

		VariantName changed = post("4", variant, VariantName.class);
		
		Assert.assertNotNull(changed.getTopicName());
		Assert.assertEquals("2", changed.getTopicName().getObjectId());
	}

	@Test
	public void testUnexistingTopicName() {
		VariantName variant = get("4", VariantName.class);
		variant.setTopicName(new TopicName("unexistig_topic_id"));

		VariantName changed = post("4", variant, VariantName.class);
		
		Assert.assertNotNull(changed.getTopicName());
		Assert.assertEquals("2", changed.getTopicName().getObjectId());
	}

	@Test
	public void testNullTopicName() {
		VariantName variant = get("4", VariantName.class);
		variant.setTopicName(null);

		VariantName changed = post("4", variant, VariantName.class);
		
		Assert.assertNotNull(changed.getTopicName());
		Assert.assertEquals("2", changed.getTopicName().getObjectId());
	}

//	@Test
//	public void testTopicNameByItemIdentifier() {
//		VariantName variant = get("2", VariantName.class);
//		TopicName name = new TopicName();
//		name.getItemIdentifiers().add(URILocator.create("foo:bar#network-location"));
//		variant.setTopicName(name);
//
//		VariantName changed = post("2", variant, VariantName.class);
//		
//		Assert.assertNotNull(changed.getTopic());
//		Assert.assertEquals("1", changed.getTopic().getObjectId());
//	}

	@Test
	public void testReifier() {
		VariantName variant = get("4", VariantName.class);
		variant.setReifier(new Topic("1"));
		
		VariantName changed = post("4", variant, VariantName.class);
		
		Assert.assertNotNull(changed.getReifier());
		Assert.assertEquals("1", changed.getReifier().getObjectId());
	}

	@Test
	public void testClearReifier() {
		VariantName variant = get("4", VariantName.class);
		variant.setReifier(new Topic("1"));
		
		VariantName changed = post("4", variant, VariantName.class);
		
		Assert.assertNotNull(changed.getReifier());
		Assert.assertEquals("1", changed.getReifier().getObjectId());
		
		changed.setReifier(null);
		changed = post("4", changed, VariantName.class);
		
		Assert.assertNull(changed.getReifier());
	}

	@Test
	public void testReifierByItemIdentifier() {
		VariantName variant = get("4", VariantName.class);
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:bar#topic1"));
		variant.setReifier(topic);

		VariantName changed = post("4", variant, VariantName.class);
		
		Assert.assertNotNull(changed.getReifier());
		Assert.assertEquals("1", changed.getReifier().getObjectId());
	}
	
	@Test
	public void testScope() {
		VariantName variant = get("4", VariantName.class);
		variant.getScope().add(new Topic("6"));
		
		VariantName changed = post("4", variant, VariantName.class);
		
		Assert.assertNotNull(changed.getScope());
		assertContainsTopics(changed.getScope(), "1", "6");
	}
	
	@Test
	public void testScopes() {
		VariantName variant = get("4", VariantName.class);
		variant.getScope().add(new Topic("6"));
		variant.getScope().add(new Topic("10"));
		
		VariantName changed = post("4", variant, VariantName.class);
		
		Assert.assertNotNull(changed.getScope());
		assertContainsTopics(changed.getScope(), "1", "6", "10");
	}
	
	@Test
	public void testRemoveScope() {
		VariantName variant = get("13", VariantName.class);
		removeById(variant.getScope(), "6");
		
		VariantName changed = post("13", variant, VariantName.class);
		
		Assert.assertNotNull(changed.getScope());
		Assert.assertEquals(1, changed.getScope().size());
		assertContainsTopics(changed.getScope(), "10");
	}
	
//	@Test
//	public void testClearScope() {
//		VariantName variant = get("5349", VariantName.class);
//		variant.getScope().clear();
//		
//		VariantName changed = post("5349", variant, VariantName.class);
//		
//		Assert.assertNotNull(changed.getScope());
//		Assert.assertEquals(0, changed.getScope().size());
//	}
	
	@Test
	public void testChangeScope() {
		VariantName variant = get("13", VariantName.class);
		variant.getScope().add(new Topic("1"));
		VariantName changed = post("13", variant, VariantName.class);
		
		Assert.assertNotNull(changed.getScope());
		Assert.assertEquals(3, changed.getScope().size());
		assertContainsTopics(changed.getScope(), "1");
		
		removeById(changed.getScope(), "1");
		changed.getScope().add(new Topic("3"));
		changed = post("13", changed, VariantName.class);

		Assert.assertNotNull(changed.getScope());
		Assert.assertEquals(3, changed.getScope().size());
		assertContainsTopics(changed.getScope(), "6", "10", "3");
	}
	
	@Test
	public void testChangeScopeVoid() {
		VariantName variant = get("13", VariantName.class);
		variant.setScope(null);
		
		VariantName changed = post("13", variant, VariantName.class);
		
		Assert.assertNotNull(changed.getScope());
		Assert.assertEquals(2, changed.getScope().size());
		assertContainsTopics(changed.getScope(), "6", "10");
	}
	
	@Test
	public void testChangeScopeByItemIdentifier() {
		VariantName variant = get("13", VariantName.class);
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:bar#topic1"));
		variant.getScope().add(topic);
		
		VariantName changed = post("13", variant, VariantName.class);
		
		Assert.assertNotNull(changed.getScope());
		Assert.assertEquals(3, changed.getScope().size());
		assertContainsTopics(changed.getScope(), "1", "6", "10");
	}
	
	@Test
	public void testAddItemIdentifier() {
		VariantName variant = get("4", VariantName.class);
		variant.getItemIdentifiers().add(URILocator.create("foo:barbar10"));
		
		VariantName changed = post("4", variant, VariantName.class);
		
		Assert.assertNotNull(changed.getItemIdentifiers());
		Assert.assertEquals(1, changed.getItemIdentifiers().size());
		Assert.assertEquals("foo:barbar10", changed.getItemIdentifiers().iterator().next().getAddress());
	}
	
	@Test
	public void testAddItemIdentifiers() {
		VariantName variant = get("4", VariantName.class);
		variant.getItemIdentifiers().add(URILocator.create("foo:barbar11"));
		variant.getItemIdentifiers().add(URILocator.create("foo:barbar12"));
		
		VariantName changed = post("4", variant, VariantName.class);
		
		Assert.assertNotNull(changed.getItemIdentifiers());
		Assert.assertEquals(2, changed.getItemIdentifiers().size());
	}
	
	@Test
	public void testRemoveItemIdentifier() {
		final URILocator locator = URILocator.create("foo:barto-remove");

		VariantName name = get("4", VariantName.class);
		name.getItemIdentifiers().add(locator);
		
		name = post("4", name, VariantName.class);
		Assert.assertNotNull(name.getItemIdentifiers());
		Assert.assertEquals(1, name.getItemIdentifiers().size());
		
		name.getItemIdentifiers().remove(locator);
		name = post("4", name, VariantName.class);
		Assert.assertNotNull(name.getItemIdentifiers());
		Assert.assertTrue(name.getItemIdentifiers().isEmpty());
	}

	@Test
	public void testClearItemIdentifiers() {
		final URILocator locator = URILocator.create("foo:barto-remove");

		VariantName name = get("4", VariantName.class);
		name.getItemIdentifiers().add(locator);
		
		name = post("4", name, VariantName.class);
		Assert.assertNotNull(name.getItemIdentifiers());
		Assert.assertEquals(1, name.getItemIdentifiers().size());
		
		name.getItemIdentifiers().clear();
		name = post("4", name, VariantName.class);
		Assert.assertNotNull(name.getItemIdentifiers());
		Assert.assertTrue(name.getItemIdentifiers().isEmpty());
	}
	
	@Test
	public void testChangeItemIdentifier() {
		final URILocator locator = URILocator.create("foo:barto-remove");

		VariantName name = get("4", VariantName.class);
		name.getItemIdentifiers().add(locator);
		
		name = post("4", name, VariantName.class);
		Assert.assertNotNull(name.getItemIdentifiers());
		Assert.assertEquals(1, name.getItemIdentifiers().size());
		
		name.getItemIdentifiers().remove(locator);
		name.getItemIdentifiers().add(URILocator.create("foo:barto-keep-var"));
		name = post("4", name, VariantName.class);
		Assert.assertNotNull(name.getItemIdentifiers());
		Assert.assertEquals(1, name.getItemIdentifiers().size());
		Assert.assertEquals("foo:barto-keep-var", name.getItemIdentifiers().iterator().next().getAddress());
	}
	
	@Test
	public void testChangeItemIdentifierVoid() {
		final URILocator locator = URILocator.create("foo:barto-keep-var-2");

		VariantName name = get("4", VariantName.class);
		name.getItemIdentifiers().add(locator);
		
		name = post("4", name, VariantName.class);
		Assert.assertNotNull(name.getItemIdentifiers());
		Assert.assertEquals(1, name.getItemIdentifiers().size());
		
		name.setItemIdentifiers(null);
		name = post("4", name, VariantName.class);
		Assert.assertNotNull(name.getItemIdentifiers());
		Assert.assertEquals(1, name.getItemIdentifiers().size());
	}

	/* -- Failing requests -- */
	
	@Test
	public void testInvalidReifier() {
		VariantName variant = get("4", VariantName.class);
		variant.setReifier(new Topic("2"));

		assertPostFails("4", variant, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingReifier() {
		VariantName variant = get("4", VariantName.class);
		variant.setReifier(new Topic("unexistig_topic_id"));

		assertPostFails("4", variant, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testInvalidScope() {
		VariantName variant = get("4", VariantName.class);
		variant.getScope().add(new Topic("2"));

		assertPostFails("4", variant, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingScope() {
		VariantName variant = get("4", VariantName.class);
		variant.getScope().add(new Topic("unexisting_topic_id"));

		assertPostFails("4", variant, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}
}
