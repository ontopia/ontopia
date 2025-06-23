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

import java.net.URISyntaxException;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.Occurrence;
import net.ontopia.topicmaps.rest.model.Topic;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.JVM)
public class OccurrenceResourcePOSTTest extends AbstractV1ResourceTest {
	
	public OccurrenceResourcePOSTTest() {
		super(OCCURRENCES_LTM, "occurrences");
	}

	@Test
	public void testValue() {
		Occurrence occurrence = get("2", Occurrence.class);
		occurrence.setValue("Another value");
		
		Occurrence changed = post("2", occurrence, Occurrence.class);
		
		Assert.assertEquals("Another value", changed.getValue());
	}

	@Test
	public void testInvalidValue() {
		Occurrence occurrence = get("2", Occurrence.class);
		occurrence.setValue(null);
		
		Occurrence changed = post("2", occurrence, Occurrence.class);
		Assert.assertNotNull(changed.getValue());
	}

	@Test
	public void testDatatype() throws URISyntaxException {
		Occurrence occurrence = get("2", Occurrence.class);
		occurrence.setDatatype(DataTypes.TYPE_BOOLEAN);
		
		Occurrence changed = post("2", occurrence, Occurrence.class);
		
		Assert.assertEquals(DataTypes.TYPE_BOOLEAN, changed.getDataType());
	}

	@Test
	public void testInvalidDatatype() {
		Occurrence occurrence = get("2", Occurrence.class);
		occurrence.setDatatype(null);
		
		Occurrence changed = post("2", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getDataType());
	}

	@Test
	public void testTopic() {
		Occurrence occurrence = get("2", Occurrence.class);
		occurrence.setTopic(new Topic("3"));

		Occurrence changed = post("2", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getTopic());
		Assert.assertEquals("1", changed.getTopic().getObjectId());
	}
	
	@Test
	public void testInvalidTopic() {
		Occurrence occurrence = get("2", Occurrence.class);
		occurrence.setTopic(new Topic("2"));

		Occurrence changed = post("2", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getTopic());
		Assert.assertEquals("1", changed.getTopic().getObjectId());
	}

	@Test
	public void testUnexistingTopic() {
		Occurrence occurrence = get("2", Occurrence.class);
		occurrence.setTopic(new Topic("unexistig_topic_id"));

		Occurrence changed = post("2", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getTopic());
		Assert.assertEquals("1", changed.getTopic().getObjectId());
	}

	@Test
	public void testNullTopic() {
		Occurrence occurrence = get("2", Occurrence.class);
		occurrence.setTopic(null);

		Occurrence changed = post("2", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getTopic());
		Assert.assertEquals("1", changed.getTopic().getObjectId());
	}

	@Test
	public void testTopicByItemIdentifier() {
		Occurrence occurrence = get("2", Occurrence.class);
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:bar#topic2"));
		occurrence.setTopic(topic);

		Occurrence changed = post("2", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getTopic());
		Assert.assertEquals("1", changed.getTopic().getObjectId());
	}

	@Test
	public void testType() {
		Occurrence occurrence = get("2", Occurrence.class);
		occurrence.setType(new Topic("3"));

		Occurrence changed = post("2", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getType());
		Assert.assertEquals("3", changed.getType().getObjectId());
	}
	
	@Test
	public void testNullType() {
		Occurrence occurrence = get("2", Occurrence.class);
		occurrence.setType(null);

		Occurrence changed = post("2", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getType());
		Assert.assertEquals("1", changed.getType().getObjectId());
	}
	
	@Test
	public void testTypeByItemIdentifier() {
		Occurrence occurrence = get("2", Occurrence.class);
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:bar#topic2"));
		occurrence.setType(topic);

		Occurrence changed = post("2", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getType());
		Assert.assertEquals("3", changed.getType().getObjectId());
	}
	
	@Test
	public void testReifier() {
		Occurrence occurrence = get("2", Occurrence.class);
		occurrence.setReifier(new Topic("1"));
		
		Occurrence changed = post("2", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getReifier());
		Assert.assertEquals("1", changed.getReifier().getObjectId());
	}

	@Test
	public void testClearReifier() {
		Occurrence occurrence = get("7", Occurrence.class);
		occurrence.setReifier(null);
		
		Occurrence changed = post("7", occurrence, Occurrence.class);
		
		Assert.assertNull(changed.getReifier());
	}

	@Test
	public void testReifierByItemIdentifier() {
		Occurrence occurrence = get("2", Occurrence.class);
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:bar#topic2"));
		occurrence.setReifier(topic);

		Occurrence changed = post("2", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getReifier());
		Assert.assertEquals("3", changed.getReifier().getObjectId());
	}
	
	@Test
	public void testScope() {
		Occurrence occurrence = get("2", Occurrence.class);
		occurrence.getScope().add(new Topic("1"));
		
		Occurrence changed = post("2", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getScope());
		assertContainsTopics(changed.getScope(), "1");
	}
	
	@Test
	public void testScopes() {
		Occurrence occurrence = get("2", Occurrence.class);
		occurrence.getScope().add(new Topic("1"));
		occurrence.getScope().add(new Topic("3"));
		
		Occurrence changed = post("2", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getScope());
		assertContainsTopics(changed.getScope(), "1", "3");
	}
	
	@Test
	public void testRemoveScope() {
		Occurrence occurrence = get("4", Occurrence.class);
		removeById(occurrence.getScope(), "5");
		
		Occurrence changed = post("4", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getScope());
		Assert.assertEquals(1, changed.getScope().size());
		assertContainsTopics(changed.getScope(), "6");
	}
	
	@Test
	public void testClearScope() {
		Occurrence occurrence = get("4", Occurrence.class);
		occurrence.getScope().clear();
		
		Occurrence changed = post("4", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getScope());
		Assert.assertEquals(0, changed.getScope().size());
	}
	
	@Test
	public void testChangeScope() {
		Occurrence occurrence = get("4", Occurrence.class);
		removeById(occurrence.getScope(), "5");
		occurrence.getScope().add(new Topic("1"));
		
		Occurrence changed = post("4", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getScope());
		Assert.assertEquals(2, changed.getScope().size());
		assertContainsTopics(changed.getScope(), "6", "1");
	}
	
	@Test
	public void testChangeScopeVoid() {
		Occurrence occurrence = get("4", Occurrence.class);
		occurrence.setScope(null);
		
		Occurrence changed = post("4", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getScope());
		Assert.assertEquals(2, changed.getScope().size());
		assertContainsTopics(changed.getScope(), "5", "6");
	}
	
	@Test
	public void testChangeScopeByItemIdentifier() {
		Occurrence occurrence = get("4", Occurrence.class);
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:bar#topic1"));
		occurrence.getScope().add(topic);
		
		Occurrence changed = post("4", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getScope());
		Assert.assertEquals(3, changed.getScope().size());
		assertContainsTopics(changed.getScope(), "1", "5", "6");
	}
	
	@Test
	public void testAddItemIdentifier() {
		Occurrence occurrence = get("2", Occurrence.class);
		occurrence.getItemIdentifiers().add(URILocator.create("foo:barbar2"));
		
		Occurrence changed = post("2", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getItemIdentifiers());
		Assert.assertEquals(1, changed.getItemIdentifiers().size());
		Assert.assertEquals("foo:barbar2", changed.getItemIdentifiers().iterator().next().getAddress());
	}
	
	@Test
	public void testAddItemIdentifiers() {
		Occurrence occurrence = get("2", Occurrence.class);
		occurrence.getItemIdentifiers().add(URILocator.create("foo:barbar3"));
		occurrence.getItemIdentifiers().add(URILocator.create("foo:barbar4"));
		
		Occurrence changed = post("2", occurrence, Occurrence.class);
		
		Assert.assertNotNull(changed.getItemIdentifiers());
		Assert.assertEquals(2, changed.getItemIdentifiers().size());
	}
	
	@Test
	public void testRemoveItemIdentifier() {
		final URILocator locator = URILocator.create("foo:barto-remove");

		Occurrence occurrence = get("2", Occurrence.class);
		occurrence.getItemIdentifiers().add(locator);
		
		occurrence = post("2", occurrence, Occurrence.class);
		Assert.assertNotNull(occurrence.getItemIdentifiers());
		Assert.assertEquals(1, occurrence.getItemIdentifiers().size());
		
		occurrence.getItemIdentifiers().remove(locator);
		occurrence = post("2", occurrence, Occurrence.class);
		Assert.assertNotNull(occurrence.getItemIdentifiers());
		Assert.assertTrue(occurrence.getItemIdentifiers().isEmpty());
	}

	@Test
	public void testClearItemIdentifiers() {
		final URILocator locator = URILocator.create("foo:barto-remove");

		Occurrence occurrence = get("2", Occurrence.class);
		occurrence.getItemIdentifiers().add(locator);
		
		occurrence = post("2", occurrence, Occurrence.class);
		Assert.assertNotNull(occurrence.getItemIdentifiers());
		Assert.assertEquals(1, occurrence.getItemIdentifiers().size());
		
		occurrence.getItemIdentifiers().clear();
		occurrence = post("2", occurrence, Occurrence.class);
		Assert.assertNotNull(occurrence.getItemIdentifiers());
		Assert.assertTrue(occurrence.getItemIdentifiers().isEmpty());
	}
	
	@Test
	public void testChangeItemIdentifier() {
		final URILocator locator = URILocator.create("foo:barto-remove");

		Occurrence occurrence = get("2", Occurrence.class);
		occurrence.getItemIdentifiers().add(locator);
		
		occurrence = post("2", occurrence, Occurrence.class);
		Assert.assertNotNull(occurrence.getItemIdentifiers());
		Assert.assertEquals(1, occurrence.getItemIdentifiers().size());
		
		occurrence.getItemIdentifiers().remove(locator);
		occurrence.getItemIdentifiers().add(URILocator.create("foo:barto-keep-occ"));
		occurrence = post("2", occurrence, Occurrence.class);
		Assert.assertNotNull(occurrence.getItemIdentifiers());
		Assert.assertEquals(1, occurrence.getItemIdentifiers().size());
		Assert.assertEquals("foo:barto-keep-occ", occurrence.getItemIdentifiers().iterator().next().getAddress());
	}
	
	@Test
	public void testChangeItemIdentifierVoid() {
		final URILocator locator = URILocator.create("foo:barto-keep-occ-2");

		Occurrence occurrence = get("2", Occurrence.class);
		occurrence.getItemIdentifiers().add(locator);
		
		occurrence = post("2", occurrence, Occurrence.class);
		Assert.assertNotNull(occurrence.getItemIdentifiers());
		Assert.assertEquals(1, occurrence.getItemIdentifiers().size());
		
		occurrence.setItemIdentifiers(null);
		occurrence = post("2", occurrence, Occurrence.class);
		Assert.assertNotNull(occurrence.getItemIdentifiers());
		Assert.assertEquals(1, occurrence.getItemIdentifiers().size());
	}
	
	/* -- Failing requests -- */
	
	@Test
	public void testInvalidType() {
		Occurrence occurrence = get("2", Occurrence.class);
		occurrence.setType(new Topic("2"));

		assertPostFails("2", occurrence, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingType() {
		Occurrence occurrence = get("2", Occurrence.class);
		occurrence.setType(new Topic("unexistig_topic_id"));

		assertPostFails("2", occurrence, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testInvalidReifier() {
		Occurrence occurrence = get("2", Occurrence.class);
		occurrence.setReifier(new Topic("2"));

		assertPostFails("2", occurrence, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingReifier() {
		Occurrence occurrence = get("2", Occurrence.class);
		occurrence.setReifier(new Topic("unexistig_topic_id"));

		assertPostFails("2", occurrence, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testInvalidScope() {
		Occurrence occurrence = get("2", Occurrence.class);
		occurrence.getScope().add(new Topic("2"));

		assertPostFails("2", occurrence, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingScope() {
		Occurrence occurrence = get("2", Occurrence.class);
		occurrence.getScope().add(new Topic("unexisting_topic_id"));

		assertPostFails("2", occurrence, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}
}
