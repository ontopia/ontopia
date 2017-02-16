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

package net.ontopia.topicmaps.rest.v1.occurrence;

import java.net.MalformedURLException;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.Occurrence;
import net.ontopia.topicmaps.rest.model.Topic;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import org.junit.Assert;
import org.junit.Test;

public class OccurrenceResourcePOSTTest extends AbstractV1ResourceTest {
	
	public OccurrenceResourcePOSTTest() {
		super(OPERA_TM, "occurrences");
	}

	@Test
	public void testValue() {
		Occurrence occurrence = get("13", Occurrence.class);
		occurrence.setValue("Another value");
		
		Occurrence changed = post("13", occurrence, Occurrence.class);
		
		Assert.assertEquals("Another value", changed.getValue());
	}

	@Test
	public void testInvalidValue() {
		Occurrence occurrence = get("13", Occurrence.class);
		occurrence.setValue(null);
		
		Occurrence changed = post("13", occurrence, Occurrence.class);
		Assert.assertNotNull(changed.getValue());
	}

	@Test
	public void testDatatype() throws MalformedURLException {
		Occurrence occurrence = get("13", Occurrence.class);
		occurrence.setDatatype(DataTypes.TYPE_BOOLEAN);
		
		Occurrence changed = post("13", occurrence, Occurrence.class);
		
		Assert.assertEquals(DataTypes.TYPE_BOOLEAN, changed.getDataType());
	}

	@Test
	public void testInvalidDatatype() {
		Occurrence occurrence = get("13", Occurrence.class);
		occurrence.setDatatype(null);
		
		Occurrence changed = post("13", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getDataType());
	}

	@Test
	public void testTopic() {
		Occurrence occurrence = get("13", Occurrence.class);
		occurrence.setTopic(new Topic("12"));

		Occurrence changed = post("13", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getTopic());
		Assert.assertEquals("1", changed.getTopic().getObjectId());
	}
	
	@Test
	public void testInvalidTopic() {
		Occurrence occurrence = get("13", Occurrence.class);
		occurrence.setTopic(new Topic("13"));

		Occurrence changed = post("13", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getTopic());
		Assert.assertEquals("1", changed.getTopic().getObjectId());
	}

	@Test
	public void testUnexistingTopic() {
		Occurrence occurrence = get("13", Occurrence.class);
		occurrence.setTopic(new Topic("unexistig_topic_id"));

		Occurrence changed = post("13", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getTopic());
		Assert.assertEquals("1", changed.getTopic().getObjectId());
	}

	@Test
	public void testNullTopic() {
		Occurrence occurrence = get("13", Occurrence.class);
		occurrence.setTopic(null);

		Occurrence changed = post("13", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getTopic());
		Assert.assertEquals("1", changed.getTopic().getObjectId());
	}

	@Test
	public void testTopicByItemIdentifier() {
		Occurrence occurrence = get("13", Occurrence.class);
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:#network-location"));
		occurrence.setTopic(topic);

		Occurrence changed = post("13", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getTopic());
		Assert.assertEquals("1", changed.getTopic().getObjectId());
	}

	@Test
	public void testType() {
		Occurrence occurrence = get("13", Occurrence.class);
		occurrence.setType(new Topic("1"));

		Occurrence changed = post("13", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getType());
		Assert.assertEquals("1", changed.getType().getObjectId());
	}
	
	@Test
	public void testNullType() {
		Occurrence occurrence = get("13", Occurrence.class);
		occurrence.setType(null);

		Occurrence changed = post("13", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getType());
		Assert.assertEquals("1", changed.getType().getObjectId());
	}
	
	@Test
	public void testTypeByItemIdentifier() {
		Occurrence occurrence = get("13", Occurrence.class);
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:#network-location"));
		occurrence.setType(topic);

		Occurrence changed = post("13", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getType());
		Assert.assertEquals("261", changed.getType().getObjectId());
	}
	
	@Test
	public void testReifier() {
		Occurrence occurrence = get("13", Occurrence.class);
		occurrence.setReifier(new Topic("152"));
		
		Occurrence changed = post("13", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getReifier());
		Assert.assertEquals("152", changed.getReifier().getObjectId());
	}

	@Test
	public void testClearReifier() {
		Occurrence occurrence = get("145", Occurrence.class);
		occurrence.setReifier(null);
		
		Occurrence changed = post("145", occurrence, Occurrence.class);
		
		Assert.assertNull(changed.getReifier());
	}

	@Test
	public void testReifierByItemIdentifier() {
		Occurrence occurrence = get("145", Occurrence.class);
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:#style"));
		occurrence.setReifier(topic);

		Occurrence changed = post("145", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getReifier());
		Assert.assertEquals("287", changed.getReifier().getObjectId());
	}
	
	@Test
	public void testScope() {
		Occurrence occurrence = get("13", Occurrence.class);
		occurrence.getScope().add(new Topic("1"));
		
		Occurrence changed = post("13", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getScope());
		assertContainsTopics(changed.getScope(), "1");
	}
	
	@Test
	public void testScopes() {
		Occurrence occurrence = get("13", Occurrence.class);
		occurrence.getScope().add(new Topic("1"));
		occurrence.getScope().add(new Topic("12"));
		
		Occurrence changed = post("13", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getScope());
		assertContainsTopics(changed.getScope(), "1", "12");
	}
	
	@Test
	public void testRemoveScope() {
		Occurrence occurrence = get("5770", Occurrence.class);
		removeById(occurrence.getScope(), "143");
		
		Occurrence changed = post("5770", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getScope());
		Assert.assertEquals(1, changed.getScope().size());
		assertContainsTopics(changed.getScope(), "4781");
	}
	
	@Test
	public void testClearScope() {
		Occurrence occurrence = get("5770", Occurrence.class);
		occurrence.getScope().clear();
		
		Occurrence changed = post("5770", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getScope());
		Assert.assertEquals(0, changed.getScope().size());
	}
	
	@Test
	public void testChangeScope() {
		Occurrence occurrence = get("5632", Occurrence.class);
		removeById(occurrence.getScope(), "143");
		occurrence.getScope().add(new Topic("1"));
		
		Occurrence changed = post("5632", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getScope());
		Assert.assertEquals(2, changed.getScope().size());
		assertContainsTopics(changed.getScope(), "4781", "1");
	}
	
	@Test
	public void testChangeScopeVoid() {
		Occurrence occurrence = get("5679", Occurrence.class);
		occurrence.setScope(null);
		
		Occurrence changed = post("5679", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getScope());
		Assert.assertEquals(2, changed.getScope().size());
		assertContainsTopics(changed.getScope(), "4781", "143");
	}
	
	@Test
	public void testChangeScopeByItemIdentifier() {
		Occurrence occurrence = get("5796", Occurrence.class);
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:#style"));
		occurrence.getScope().add(topic);
		
		Occurrence changed = post("5796", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getScope());
		Assert.assertEquals(3, changed.getScope().size());
		assertContainsTopics(changed.getScope(), "4781", "141", "287");
	}
	
	@Test
	public void testAddItemIdentifier() {
		Occurrence occurrence = get("5796", Occurrence.class);
		occurrence.getItemIdentifiers().add(URILocator.create("foo:bar2"));
		
		Occurrence changed = post("5796", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getItemIdentifiers());
		Assert.assertEquals(1, changed.getItemIdentifiers().size());
		Assert.assertEquals("foo:bar2", changed.getItemIdentifiers().iterator().next().getAddress());
	}
	
	@Test
	public void testAddItemIdentifiers() {
		Occurrence occurrence = get("5679", Occurrence.class);
		occurrence.getItemIdentifiers().add(URILocator.create("foo:bar3"));
		occurrence.getItemIdentifiers().add(URILocator.create("foo:bar4"));
		
		Occurrence changed = post("5679", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getItemIdentifiers());
		Assert.assertEquals(2, changed.getItemIdentifiers().size());
	}
	
	@Test
	public void testRemoveItemIdentifier() {
		final URILocator locator = URILocator.create("foo:to-remove");

		Occurrence occurrence = get("4337", Occurrence.class);
		occurrence.getItemIdentifiers().add(locator);
		
		occurrence = post("4337", occurrence, Occurrence.class);
		Assert.assertNotNull(occurrence.getItemIdentifiers());
		Assert.assertEquals(1, occurrence.getItemIdentifiers().size());
		
		occurrence.getItemIdentifiers().remove(locator);
		occurrence = post("4337", occurrence, Occurrence.class);
		Assert.assertNotNull(occurrence.getItemIdentifiers());
		Assert.assertTrue(occurrence.getItemIdentifiers().isEmpty());
	}

	@Test
	public void testClearItemIdentifiers() {
		final URILocator locator = URILocator.create("foo:to-remove");

		Occurrence occurrence = get("4337", Occurrence.class);
		occurrence.getItemIdentifiers().add(locator);
		
		occurrence = post("4337", occurrence, Occurrence.class);
		Assert.assertNotNull(occurrence.getItemIdentifiers());
		Assert.assertEquals(1, occurrence.getItemIdentifiers().size());
		
		occurrence.getItemIdentifiers().clear();
		occurrence = post("4337", occurrence, Occurrence.class);
		Assert.assertNotNull(occurrence.getItemIdentifiers());
		Assert.assertTrue(occurrence.getItemIdentifiers().isEmpty());
	}
	
	@Test
	public void testChangeItemIdentifier() {
		final URILocator locator = URILocator.create("foo:to-remove");

		Occurrence occurrence = get("4337", Occurrence.class);
		occurrence.getItemIdentifiers().add(locator);
		
		occurrence = post("4337", occurrence, Occurrence.class);
		Assert.assertNotNull(occurrence.getItemIdentifiers());
		Assert.assertEquals(1, occurrence.getItemIdentifiers().size());
		
		occurrence.getItemIdentifiers().remove(locator);
		occurrence.getItemIdentifiers().add(URILocator.create("foo:to-keep-occ"));
		occurrence = post("4337", occurrence, Occurrence.class);
		Assert.assertNotNull(occurrence.getItemIdentifiers());
		Assert.assertEquals(1, occurrence.getItemIdentifiers().size());
		Assert.assertEquals("foo:to-keep-occ", occurrence.getItemIdentifiers().iterator().next().getAddress());
	}
	
	@Test
	public void testChangeItemIdentifierVoid() {
		final URILocator locator = URILocator.create("foo:to-keep-occ-2");

		Occurrence occurrence = get("4338", Occurrence.class);
		occurrence.getItemIdentifiers().add(locator);
		
		occurrence = post("4338", occurrence, Occurrence.class);
		Assert.assertNotNull(occurrence.getItemIdentifiers());
		Assert.assertEquals(1, occurrence.getItemIdentifiers().size());
		
		occurrence.setItemIdentifiers(null);
		occurrence = post("4338", occurrence, Occurrence.class);
		Assert.assertNotNull(occurrence.getItemIdentifiers());
		Assert.assertEquals(1, occurrence.getItemIdentifiers().size());
	}
	
	/* -- Failing requests -- */
	
	@Test
	public void testInvalidType() {
		Occurrence occurrence = get("13", Occurrence.class);
		occurrence.setType(new Topic("13"));

		assertPostFails("13", occurrence, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingType() {
		Occurrence occurrence = get("13", Occurrence.class);
		occurrence.setType(new Topic("unexistig_topic_id"));

		assertPostFails("13", occurrence, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testInvalidReifier() {
		Occurrence occurrence = get("13", Occurrence.class);
		occurrence.setReifier(new Topic("13"));

		assertPostFails("13", occurrence, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingReifier() {
		Occurrence occurrence = get("13", Occurrence.class);
		occurrence.setReifier(new Topic("unexistig_topic_id"));

		assertPostFails("13", occurrence, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testInvalidScope() {
		Occurrence occurrence = get("13", Occurrence.class);
		occurrence.getScope().add(new Topic("13"));

		assertPostFails("13", occurrence, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingScope() {
		Occurrence occurrence = get("13", Occurrence.class);
		occurrence.getScope().add(new Topic("unexisting_topic_id"));

		assertPostFails("13", occurrence, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}
}
