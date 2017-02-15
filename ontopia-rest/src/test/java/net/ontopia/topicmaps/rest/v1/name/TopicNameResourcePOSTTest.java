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

package net.ontopia.topicmaps.rest.v1.name;

import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.Topic;
import net.ontopia.topicmaps.rest.model.TopicName;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import org.junit.Assert;
import org.junit.Test;

public class TopicNameResourcePOSTTest extends AbstractV1ResourceTest {
	
	public TopicNameResourcePOSTTest() {
		super(OPERA_TM, "names");
	}

	@Test
	public void testValue() {
		TopicName topicname = get("2", TopicName.class);
		topicname.setValue("Another value");
		
		TopicName changed = post("2", topicname, TopicName.class);
		
		Assert.assertEquals("Another value", changed.getValue());
	}

	@Test
	public void testInvalidValue() {
		TopicName topicname = get("2", TopicName.class);
		topicname.setValue(null);
		
		TopicName changed = post("2", topicname, TopicName.class);
		Assert.assertNotNull(changed.getValue());
	}

	@Test
	public void testTopic() {
		TopicName topicname = get("2", TopicName.class);
		topicname.setTopic(new Topic("12"));

		TopicName changed = post("2", topicname, TopicName.class);
		
		Assert.assertNotNull(changed.getTopic());
		Assert.assertEquals("1", changed.getTopic().getObjectId());
	}
	
	@Test
	public void testInvalidTopic() {
		TopicName topicname = get("2", TopicName.class);
		topicname.setTopic(new Topic("2"));

		TopicName changed = post("2", topicname, TopicName.class);
		
		Assert.assertNotNull(changed.getTopic());
		Assert.assertEquals("1", changed.getTopic().getObjectId());
	}

	@Test
	public void testUnexistingTopic() {
		TopicName topicname = get("2", TopicName.class);
		topicname.setTopic(new Topic("unexistig_topic_id"));

		TopicName changed = post("2", topicname, TopicName.class);
		
		Assert.assertNotNull(changed.getTopic());
		Assert.assertEquals("1", changed.getTopic().getObjectId());
	}

	@Test
	public void testNullTopic() {
		TopicName topicname = get("2", TopicName.class);
		topicname.setTopic(null);

		TopicName changed = post("2", topicname, TopicName.class);
		
		Assert.assertNotNull(changed.getTopic());
		Assert.assertEquals("1", changed.getTopic().getObjectId());
	}

	@Test
	public void testTopicByItemIdentifier() {
		TopicName topicname = get("2", TopicName.class);
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:#network-location"));
		topicname.setTopic(topic);

		TopicName changed = post("2", topicname, TopicName.class);
		
		Assert.assertNotNull(changed.getTopic());
		Assert.assertEquals("1", changed.getTopic().getObjectId());
	}

	@Test
	public void testType() {
		TopicName topicname = get("2", TopicName.class);
		topicname.setType(new Topic("1"));

		TopicName changed = post("2", topicname, TopicName.class);
		
		Assert.assertNotNull(changed.getType());
		Assert.assertEquals("1", changed.getType().getObjectId());
	}
	
	@Test
	public void testNullType() {
		TopicName topicname = get("2", TopicName.class);
		topicname.setType(null);

		TopicName changed = post("2", topicname, TopicName.class);
		
		Assert.assertNotNull(changed.getType());
		Assert.assertEquals("1", changed.getType().getObjectId());
	}
	
	@Test
	public void testTypeByItemIdentifier() {
		TopicName topicname = get("2", TopicName.class);
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:#network-location"));
		topicname.setType(topic);

		TopicName changed = post("2", topicname, TopicName.class);
		
		Assert.assertNotNull(changed.getType());
		Assert.assertEquals("261", changed.getType().getObjectId());
	}
	
	@Test
	public void testReifier() {
		TopicName topicname = get("2", TopicName.class);
		topicname.setReifier(new Topic("5962"));
		
		TopicName changed = post("2", topicname, TopicName.class);
		
		Assert.assertNotNull(changed.getReifier());
		Assert.assertEquals("5962", changed.getReifier().getObjectId());
	}

	@Test
	public void testClearReifier() {
		TopicName topicname = get("2", TopicName.class);
		topicname.setReifier(null);
		
		TopicName changed = post("2", topicname, TopicName.class);
		
		Assert.assertNull(changed.getReifier());
	}

	@Test
	public void testReifierByItemIdentifier() {
		TopicName topicname = get("2", TopicName.class);
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:#la-vergine-di-kermo"));
		topicname.setReifier(topic);

		TopicName changed = post("2", topicname, TopicName.class);
		
		Assert.assertNotNull(changed.getReifier());
		Assert.assertEquals("5432", changed.getReifier().getObjectId());
	}
	
	@Test
	public void testScope() {
		TopicName topicname = get("2", TopicName.class);
		topicname.getScope().add(new Topic("1"));
		
		TopicName changed = post("2", topicname, TopicName.class);
		
		Assert.assertNotNull(changed.getScope());
		assertContainsTopics(changed.getScope(), "1");
	}
	
	@Test
	public void testScopes() {
		TopicName topicname = get("2", TopicName.class);
		topicname.getScope().add(new Topic("1"));
		topicname.getScope().add(new Topic("12"));
		
		TopicName changed = post("2", topicname, TopicName.class);
		
		Assert.assertNotNull(changed.getScope());
		assertContainsTopics(changed.getScope(), "1", "12");
	}
	
	@Test
	public void testRemoveScope() {
		TopicName topicname = get("3252", TopicName.class);
		removeById(topicname.getScope(), "5");
		
		TopicName changed = post("3252", topicname, TopicName.class);
		
		Assert.assertNotNull(changed.getScope());
		Assert.assertEquals(1, changed.getScope().size());
		assertContainsTopics(changed.getScope(), "838");
	}
	
	@Test
	public void testClearScope() {
		TopicName topicname = get("5913", TopicName.class);
		topicname.getScope().clear();
		
		TopicName changed = post("5913", topicname, TopicName.class);
		
		Assert.assertNotNull(changed.getScope());
		Assert.assertEquals(0, changed.getScope().size());
	}
	
	@Test
	public void testChangeScope() {
		TopicName topicname = get("5906", TopicName.class);
		removeById(topicname.getScope(), "5");
		topicname.getScope().add(new Topic("1"));
		
		TopicName changed = post("5906", topicname, TopicName.class);
		
		Assert.assertNotNull(changed.getScope());
		Assert.assertEquals(2, changed.getScope().size());
		assertContainsTopics(changed.getScope(), "149", "1");
	}
	
	@Test
	public void testChangeScopeVoid() {
		TopicName topicname = get("875", TopicName.class);
		topicname.setScope(null);
		
		TopicName changed = post("875", topicname, TopicName.class);
		
		Assert.assertNotNull(changed.getScope());
		Assert.assertEquals(2, changed.getScope().size());
		assertContainsTopics(changed.getScope(), "5", "876");
	}
	
	@Test
	public void testChangeScopeByItemIdentifier() {
		TopicName topicname = get("787", TopicName.class);
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:#style"));
		topicname.getScope().add(topic);
		
		TopicName changed = post("787", topicname, TopicName.class);
		
		Assert.assertNotNull(changed.getScope());
		Assert.assertEquals(3, changed.getScope().size());
		assertContainsTopics(changed.getScope(), "165", "5", "287");
	}
	
	@Test
	public void testAddItemIdentifier() {
		TopicName topicname = get("2382", TopicName.class);
		topicname.getItemIdentifiers().add(URILocator.create("foo:bar5"));
		
		TopicName changed = post("2382", topicname, TopicName.class);
		
		Assert.assertNotNull(changed.getItemIdentifiers());
		Assert.assertEquals(1, changed.getItemIdentifiers().size());
		Assert.assertEquals("foo:bar5", changed.getItemIdentifiers().iterator().next().getAddress());
	}
	
	@Test
	public void testAddItemIdentifiers() {
		TopicName topicname = get("4187", TopicName.class);
		topicname.getItemIdentifiers().add(URILocator.create("foo:bar6"));
		topicname.getItemIdentifiers().add(URILocator.create("foo:bar7"));
		
		TopicName changed = post("4187", topicname, TopicName.class);
		
		Assert.assertNotNull(changed.getItemIdentifiers());
		Assert.assertEquals(2, changed.getItemIdentifiers().size());
	}
	
	/* -- Failing requests -- */
	
	@Test
	public void testInvalidType() {
		TopicName topicname = get("2", TopicName.class);
		topicname.setType(new Topic("2"));

		assertPostFails("2", topicname, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingType() {
		TopicName topicname = get("2", TopicName.class);
		topicname.setType(new Topic("unexistig_topic_id"));

		assertPostFails("2", topicname, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testInvalidReifier() {
		TopicName topicname = get("2", TopicName.class);
		topicname.setReifier(new Topic("2"));

		assertPostFails("2", topicname, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingReifier() {
		TopicName topicname = get("2", TopicName.class);
		topicname.setReifier(new Topic("unexistig_topic_id"));

		assertPostFails("2", topicname, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testInvalidScope() {
		TopicName topicname = get("2", TopicName.class);
		topicname.getScope().add(new Topic("2"));

		assertPostFails("2", topicname, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingScope() {
		TopicName topicname = get("2", TopicName.class);
		topicname.getScope().add(new Topic("unexisting_topic_id"));

		assertPostFails("2", topicname, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}
}
