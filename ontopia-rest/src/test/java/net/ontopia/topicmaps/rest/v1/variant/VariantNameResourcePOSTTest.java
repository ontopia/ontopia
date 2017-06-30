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

import java.net.MalformedURLException;
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
		super(OPERA_TM, "variants");
	}

	@Test
	public void testValue() {
		VariantName variant = get("1952", VariantName.class);
		variant.setValue("Another value");
		
		VariantName changed = post("1952", variant, VariantName.class);
		
		Assert.assertEquals("Another value", changed.getValue());
	}

	@Test
	public void testInvalidValue() {
		VariantName variant = get("1952", VariantName.class);
		variant.setValue(null);
		
		VariantName changed = post("1952", variant, VariantName.class);
		Assert.assertNotNull(changed.getValue());
	}

	@Test
	public void testDatatype() throws MalformedURLException {
		VariantName variant = get("1952", VariantName.class);
		variant.setDatatype(DataTypes.TYPE_INTEGER);
		VariantName changed = post("1952", variant, VariantName.class);
		Assert.assertEquals(DataTypes.TYPE_INTEGER, changed.getDataType());
	}
	
	@Test
	public void testDatatype2() {
		VariantName variant = get("1952", VariantName.class);
		variant.setDatatype(URILocator.create("dt:foo"));
		VariantName changed = post("1952", variant, VariantName.class);
		Assert.assertNotNull(changed.getDataType());
		Assert.assertEquals("dt:foo", changed.getDataType().getAddress());
	}
	
	@Test
	public void testClearDatatype() {
		VariantName variant = get("5458", VariantName.class);
		variant.setDatatype(null);
		VariantName changed = post("5458", variant, VariantName.class);
		Assert.assertEquals(DataTypes.TYPE_STRING, changed.getDataType());
	}
	
	@Test
	public void testTopicName() {
		VariantName variant = get("6569", VariantName.class);
		variant.setTopicName(new TopicName("2"));

		VariantName changed = post("6569", variant, VariantName.class);
		
		Assert.assertNotNull(changed.getTopicName());
		Assert.assertEquals("6568", changed.getTopicName().getObjectId());
	}
	
	@Test
	public void testInvalidTopicName() {
		VariantName variant = get("5448", VariantName.class);
		variant.setTopicName(new TopicName("1"));

		VariantName changed = post("5448", variant, VariantName.class);
		
		Assert.assertNotNull(changed.getTopicName());
		Assert.assertEquals("5447", changed.getTopicName().getObjectId());
	}

	@Test
	public void testUnexistingTopicName() {
		VariantName variant = get("1952", VariantName.class);
		variant.setTopicName(new TopicName("unexistig_topic_id"));

		VariantName changed = post("1952", variant, VariantName.class);
		
		Assert.assertNotNull(changed.getTopicName());
		Assert.assertEquals("1951", changed.getTopicName().getObjectId());
	}

	@Test
	public void testNullTopicName() {
		VariantName variant = get("1952", VariantName.class);
		variant.setTopicName(null);

		VariantName changed = post("1952", variant, VariantName.class);
		
		Assert.assertNotNull(changed.getTopicName());
		Assert.assertEquals("1951", changed.getTopicName().getObjectId());
	}

//	@Test
//	public void testTopicNameByItemIdentifier() {
//		VariantName variant = get("2", VariantName.class);
//		TopicName name = new TopicName();
//		name.getItemIdentifiers().add(URILocator.create("foo:#network-location"));
//		variant.setTopicName(name);
//
//		VariantName changed = post("2", variant, VariantName.class);
//		
//		Assert.assertNotNull(changed.getTopic());
//		Assert.assertEquals("1", changed.getTopic().getObjectId());
//	}

	@Test
	public void testReifier() {
		VariantName variant = get("1952", VariantName.class);
		variant.setReifier(new Topic("3171"));
		
		VariantName changed = post("1952", variant, VariantName.class);
		
		Assert.assertNotNull(changed.getReifier());
		Assert.assertEquals("3171", changed.getReifier().getObjectId());
	}

	@Test
	public void testClearReifier() {
		VariantName variant = get("5175", VariantName.class);
		variant.setReifier(new Topic("680"));
		
		VariantName changed = post("5175", variant, VariantName.class);
		
		Assert.assertNotNull(changed.getReifier());
		Assert.assertEquals("680", changed.getReifier().getObjectId());
		
		changed.setReifier(null);
		changed = post("5175", changed, VariantName.class);
		
		Assert.assertNull(changed.getReifier());
	}

	@Test
	public void testReifierByItemIdentifier() {
		VariantName variant = get("6012", VariantName.class);
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:#dormeville"));
		variant.setReifier(topic);

		VariantName changed = post("6012", variant, VariantName.class);
		
		Assert.assertNotNull(changed.getReifier());
		Assert.assertEquals("4332", changed.getReifier().getObjectId());
	}
	
	@Test
	public void testScope() {
		VariantName variant = get("750", VariantName.class);
		variant.getScope().add(new Topic("1"));
		
		VariantName changed = post("750", variant, VariantName.class);
		
		Assert.assertNotNull(changed.getScope());
		assertContainsTopics(changed.getScope(), "1", "174");
	}
	
	@Test
	public void testScopes() {
		VariantName variant = get("5892", VariantName.class);
		variant.getScope().add(new Topic("1"));
		variant.getScope().add(new Topic("12"));
		
		VariantName changed = post("5892", variant, VariantName.class);
		
		Assert.assertNotNull(changed.getScope());
		assertContainsTopics(changed.getScope(), "1", "12", "174");
	}
	
	@Test
	public void testRemoveScope() {
		VariantName variant = get("569", VariantName.class);
		removeById(variant.getScope(), "174");
		
		VariantName changed = post("569", variant, VariantName.class);
		
		Assert.assertNotNull(changed.getScope());
		Assert.assertEquals(1, changed.getScope().size());
		assertContainsTopics(changed.getScope(), "29");
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
		VariantName variant = get("5899", VariantName.class);
		variant.getScope().add(new Topic("1"));
		VariantName changed = post("5899", variant, VariantName.class);
		
		Assert.assertNotNull(changed.getScope());
		Assert.assertEquals(2, changed.getScope().size());
		assertContainsTopics(changed.getScope(), "1");
		
		removeById(changed.getScope(), "1");
		changed.getScope().add(new Topic("1366"));
		changed = post("5899", changed, VariantName.class);

		Assert.assertNotNull(changed.getScope());
		Assert.assertEquals(2, changed.getScope().size());
		assertContainsTopics(changed.getScope(), "1366", "174");
	}
	
	@Test
	public void testChangeScopeVoid() {
		VariantName variant = get("5162", VariantName.class);
		variant.setScope(null);
		
		VariantName changed = post("5162", variant, VariantName.class);
		
		Assert.assertNotNull(changed.getScope());
		Assert.assertEquals(2, changed.getScope().size());
		assertContainsTopics(changed.getScope(), "29", "174");
	}
	
	@Test
	public void testChangeScopeByItemIdentifier() {
		VariantName variant = get("538", VariantName.class);
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:#style"));
		variant.getScope().add(topic);
		
		VariantName changed = post("538", variant, VariantName.class);
		
		Assert.assertNotNull(changed.getScope());
		Assert.assertEquals(2, changed.getScope().size());
		assertContainsTopics(changed.getScope(), "174", "287");
	}
	
	@Test
	public void testAddItemIdentifier() {
		VariantName variant = get("5160", VariantName.class);
		variant.getItemIdentifiers().add(URILocator.create("foo:bar10"));
		
		VariantName changed = post("5160", variant, VariantName.class);
		
		Assert.assertNotNull(changed.getItemIdentifiers());
		Assert.assertEquals(1, changed.getItemIdentifiers().size());
		Assert.assertEquals("foo:bar10", changed.getItemIdentifiers().iterator().next().getAddress());
	}
	
	@Test
	public void testAddItemIdentifiers() {
		VariantName variant = get("2261", VariantName.class);
		variant.getItemIdentifiers().add(URILocator.create("foo:bar11"));
		variant.getItemIdentifiers().add(URILocator.create("foo:bar12"));
		
		VariantName changed = post("2261", variant, VariantName.class);
		
		Assert.assertNotNull(changed.getItemIdentifiers());
		Assert.assertEquals(2, changed.getItemIdentifiers().size());
	}
	
	@Test
	public void testRemoveItemIdentifier() {
		final URILocator locator = URILocator.create("foo:to-remove");

		VariantName name = get("538", VariantName.class);
		name.getItemIdentifiers().add(locator);
		
		name = post("538", name, VariantName.class);
		Assert.assertNotNull(name.getItemIdentifiers());
		Assert.assertEquals(1, name.getItemIdentifiers().size());
		
		name.getItemIdentifiers().remove(locator);
		name = post("538", name, VariantName.class);
		Assert.assertNotNull(name.getItemIdentifiers());
		Assert.assertTrue(name.getItemIdentifiers().isEmpty());
	}

	@Test
	public void testClearItemIdentifiers() {
		final URILocator locator = URILocator.create("foo:to-remove");

		VariantName name = get("538", VariantName.class);
		name.getItemIdentifiers().add(locator);
		
		name = post("538", name, VariantName.class);
		Assert.assertNotNull(name.getItemIdentifiers());
		Assert.assertEquals(1, name.getItemIdentifiers().size());
		
		name.getItemIdentifiers().clear();
		name = post("538", name, VariantName.class);
		Assert.assertNotNull(name.getItemIdentifiers());
		Assert.assertTrue(name.getItemIdentifiers().isEmpty());
	}
	
	@Test
	public void testChangeItemIdentifier() {
		final URILocator locator = URILocator.create("foo:to-remove");

		VariantName name = get("2878", VariantName.class);
		name.getItemIdentifiers().add(locator);
		
		name = post("2878", name, VariantName.class);
		Assert.assertNotNull(name.getItemIdentifiers());
		Assert.assertEquals(1, name.getItemIdentifiers().size());
		
		name.getItemIdentifiers().remove(locator);
		name.getItemIdentifiers().add(URILocator.create("foo:to-keep-var"));
		name = post("2878", name, VariantName.class);
		Assert.assertNotNull(name.getItemIdentifiers());
		Assert.assertEquals(1, name.getItemIdentifiers().size());
		Assert.assertEquals("foo:to-keep-var", name.getItemIdentifiers().iterator().next().getAddress());
	}
	
	@Test
	public void testChangeItemIdentifierVoid() {
		final URILocator locator = URILocator.create("foo:to-keep-var-2");

		VariantName name = get("5299", VariantName.class);
		name.getItemIdentifiers().add(locator);
		
		name = post("5299", name, VariantName.class);
		Assert.assertNotNull(name.getItemIdentifiers());
		Assert.assertEquals(1, name.getItemIdentifiers().size());
		
		name.setItemIdentifiers(null);
		name = post("5299", name, VariantName.class);
		Assert.assertNotNull(name.getItemIdentifiers());
		Assert.assertEquals(1, name.getItemIdentifiers().size());
	}

	/* -- Failing requests -- */
	
	@Test
	public void testInvalidReifier() {
		VariantName variant = get("1952", VariantName.class);
		variant.setReifier(new Topic("2"));

		assertPostFails("1952", variant, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingReifier() {
		VariantName variant = get("1952", VariantName.class);
		variant.setReifier(new Topic("unexistig_topic_id"));

		assertPostFails("1952", variant, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testInvalidScope() {
		VariantName variant = get("1952", VariantName.class);
		variant.getScope().add(new Topic("2"));

		assertPostFails("1952", variant, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingScope() {
		VariantName variant = get("1952", VariantName.class);
		variant.getScope().add(new Topic("unexisting_topic_id"));

		assertPostFails("1952", variant, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}
}
