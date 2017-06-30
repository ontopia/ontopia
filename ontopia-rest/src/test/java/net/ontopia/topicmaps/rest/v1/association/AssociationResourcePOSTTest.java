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

package net.ontopia.topicmaps.rest.v1.association;

import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.Association;
import net.ontopia.topicmaps.rest.model.AssociationRole;
import net.ontopia.topicmaps.rest.model.Topic;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import org.junit.Assert;
import org.junit.Test;

public class AssociationResourcePOSTTest extends AbstractV1ResourceTest {

	public AssociationResourcePOSTTest() {
		super(OPERA_TM, "associations");
	}
	
	@Test
	public void testAddRole() {
		Association association = get("10350", Association.class);
		AssociationRole r = new AssociationRole();
		r.setType(new Topic("1"));
		r.setPlayer(new Topic("1"));
		association.getRoles().add(r);
		
		Association changed = post("10350", association, Association.class);
		Assert.assertNotNull(changed.getRoles());
		Assert.assertEquals(2, changed.getRoles().size()); // changes to roles are ignored
	}

	@Test
	public void testRemoveRole() {
		Association association = get("10350", Association.class);
		removeById(association.getRoles(), "10352");
		
		Association changed = post("10350", association, Association.class);
		Assert.assertNotNull(changed.getRoles());
		Assert.assertEquals(2, changed.getRoles().size()); // changes to roles are ignored
	}

	@Test
	public void testEmptyRoles() {
		Association association = get("10350", Association.class);
		association.getRoles().clear();
		
		Association changed = post("10350", association, Association.class);
		Assert.assertNotNull(changed.getRoles());
		Assert.assertEquals(2, changed.getRoles().size()); // changes to roles are ignored
	}

	@Test
	public void testVoidRoles() {
		Association association = get("10350", Association.class);
		association.setRoles(null);
		
		Association changed = post("10350", association, Association.class);
		Assert.assertNotNull(changed.getRoles());
		Assert.assertEquals(2, changed.getRoles().size()); // changes to roles are ignored
	}

	@Test
	public void testType() {
		Association association = get("17157", Association.class);
		association.setType(new Topic("1"));

		Association changed = post("17157", association, Association.class);

		Assert.assertNotNull(changed.getType());
		Assert.assertEquals("1", changed.getType().getObjectId());
	}

	@Test
	public void testNullType() {
		Association association = get("8889", Association.class);
		association.setType(null);

		Association changed = post("8889", association, Association.class);

		Assert.assertNotNull(changed.getType());
		Assert.assertEquals("441", changed.getType().getObjectId());
	}

	@Test
	public void testTypeByItemIdentifier() {
		Association association = get("12582", Association.class);
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:#network-location"));
		association.setType(topic);

		Association changed = post("12582", association, Association.class);

		Assert.assertNotNull(changed.getType());
		Assert.assertEquals("261", changed.getType().getObjectId());
	}

	@Test
	public void testReifier() {
		Association association = get("10872", Association.class);
		association.setReifier(new Topic("1464"));

		Association changed = post("10872", association, Association.class);

		Assert.assertNotNull(changed.getReifier());
		Assert.assertEquals("1464", changed.getReifier().getObjectId());
	}

	@Test
	public void testClearReifier() {
		Association association = get("10125", Association.class);
		association.setReifier(null);

		Association changed = post("10125", association, Association.class);

		Assert.assertNull(changed.getReifier());
	}

	@Test
	public void testReifierByItemIdentifier() {
		Association association = get("10581", Association.class);
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:#deutsche-oper"));
		association.setReifier(topic);

		Association changed = post("10581", association, Association.class);

		Assert.assertNotNull(changed.getReifier());
		Assert.assertEquals("6174", changed.getReifier().getObjectId());
	}

	@Test
	public void testScope() {
		Association association = get("17157", Association.class);
		association.getScope().add(new Topic("1"));

		Association changed = post("17157", association, Association.class);

		Assert.assertNotNull(changed.getScope());
		assertContainsTopics(changed.getScope(), "1");
	}

	@Test
	public void testScopes() {
		Association association = get("17157", Association.class);
		association.getScope().add(new Topic("1"));
		association.getScope().add(new Topic("12"));

		Association changed = post("17157", association, Association.class);

		Assert.assertNotNull(changed.getScope());
		assertContainsTopics(changed.getScope(), "1", "12");
	}

	@Test
	public void testRemoveScope() {
		Association association = get("11547", Association.class);
		removeById(association.getScope(), "2965");

		Association changed = post("11547", association, Association.class);

		Assert.assertNotNull(changed.getScope());
		Assert.assertTrue(changed.getScope().isEmpty());
	}

	@Test
	public void testClearScope() {
		Association association = get("13431", Association.class);
		association.getScope().clear();

		Association changed = post("13431", association, Association.class);

		Assert.assertNotNull(changed.getScope());
		Assert.assertTrue(changed.getScope().isEmpty());
	}

	@Test
	public void testChangeScope() {
		Association association = get("14085", Association.class);
		removeById(association.getScope(), "2965");
		association.getScope().add(new Topic("1"));

		Association changed = post("14085", association, Association.class);

		Assert.assertNotNull(changed.getScope());
		Assert.assertEquals(1, changed.getScope().size());
		assertContainsTopics(changed.getScope(), "1");
	}

	@Test
	public void testChangeScopeVoid() {
		Association association = get("12087", Association.class);
		association.setScope(null);

		Association changed = post("12087", association, Association.class);

		Assert.assertNotNull(changed.getScope());
		Assert.assertEquals(1, changed.getScope().size());
		assertContainsTopics(changed.getScope(), "2956");
	}

	@Test
	public void testChangeScopeByItemIdentifier() {
		Association association = get("16474", Association.class);
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:#style"));
		association.getScope().add(topic);

		Association changed = post("16474", association, Association.class);

		Assert.assertNotNull(changed.getScope());
		Assert.assertEquals(1, changed.getScope().size());
		assertContainsTopics(changed.getScope(), "287");
	}

	@Test
	public void testAddItemIdentifier() {
		Association association = get("17157", Association.class);
		association.getItemIdentifiers().add(URILocator.create("foo:bar8"));

		Association changed = post("17157", association, Association.class);

		Assert.assertNotNull(changed.getItemIdentifiers());
		Assert.assertEquals(1, changed.getItemIdentifiers().size());
		Assert.assertEquals("foo:bar8", changed.getItemIdentifiers().iterator().next().getAddress());
	}

	@Test
	public void testAddItemIdentifiers() {
		Association association = get("8889", Association.class);
		association.getItemIdentifiers().add(URILocator.create("foo:bar21"));
		association.getItemIdentifiers().add(URILocator.create("foo:bar22"));

		Association changed = post("8889", association, Association.class);

		Assert.assertNotNull(changed.getItemIdentifiers());
		Assert.assertEquals(2, changed.getItemIdentifiers().size());
	}

	@Test
	public void testRemoveItemIdentifier() {
		final URILocator locator = URILocator.create("foo:to-remove");

		Association association = get("8424", Association.class);
		association.getItemIdentifiers().add(locator);

		association = post("8424", association, Association.class);
		Assert.assertNotNull(association.getItemIdentifiers());
		Assert.assertEquals(1, association.getItemIdentifiers().size());

		association.getItemIdentifiers().remove(locator);
		association = post("8424", association, Association.class);
		Assert.assertNotNull(association.getItemIdentifiers());
		Assert.assertTrue(association.getItemIdentifiers().isEmpty());
	}

	@Test
	public void testClearItemIdentifiers() {
		final URILocator locator = URILocator.create("foo:to-remove");

		Association association = get("11721", Association.class);
		association.getItemIdentifiers().add(locator);

		association = post("11721", association, Association.class);
		Assert.assertNotNull(association.getItemIdentifiers());
		Assert.assertEquals(1, association.getItemIdentifiers().size());

		association.getItemIdentifiers().clear();
		association = post("11721", association, Association.class);
		Assert.assertNotNull(association.getItemIdentifiers());
		Assert.assertTrue(association.getItemIdentifiers().isEmpty());
	}

	@Test
	public void testChangeItemIdentifier() {
		final URILocator locator = URILocator.create("foo:to-remove");

		Association association = get("15448", Association.class);
		association.getItemIdentifiers().add(locator);

		association = post("15448", association, Association.class);
		Assert.assertNotNull(association.getItemIdentifiers());
		Assert.assertEquals(1, association.getItemIdentifiers().size());

		association.getItemIdentifiers().remove(locator);
		association.getItemIdentifiers().add(URILocator.create("foo:to-keep-association"));
		association = post("15448", association, Association.class);
		Assert.assertNotNull(association.getItemIdentifiers());
		Assert.assertEquals(1, association.getItemIdentifiers().size());
		Assert.assertEquals("foo:to-keep-association", association.getItemIdentifiers().iterator().next().getAddress());
	}

	@Test
	public void testChangeItemIdentifierVoid() {
		final URILocator locator = URILocator.create("foo:to-keep-association-2");

		Association association = get("14632", Association.class);
		association.getItemIdentifiers().add(locator);

		association = post("14632", association, Association.class);
		Assert.assertNotNull(association.getItemIdentifiers());
		Assert.assertEquals(1, association.getItemIdentifiers().size());

		association.setItemIdentifiers(null);
		association = post("14632", association, Association.class);
		Assert.assertNotNull(association.getItemIdentifiers());
		Assert.assertEquals(1, association.getItemIdentifiers().size());
	}

	/* -- Failing requests -- */

	@Test
	public void testInvalidType() {
		Association association = get("17157", Association.class);
		association.setType(new Topic("17157"));

		assertPostFails("17157", association, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingType() {
		Association association = get("17157", Association.class);
		association.setType(new Topic("unexistig_topic_id"));

		assertPostFails("17157", association, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testInvalidReifier() {
		Association association = get("17157", Association.class);
		association.setReifier(new Topic("17157"));

		assertPostFails("17157", association, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingReifier() {
		Association association = get("17157", Association.class);
		association.setReifier(new Topic("unexistig_topic_id"));

		assertPostFails("17157", association, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testInvalidScope() {
		Association association = get("17157", Association.class);
		association.getScope().add(new Topic("17157"));

		assertPostFails("17157", association, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingScope() {
		Association association = get("17157", Association.class);
		association.getScope().add(new Topic("unexisting_topic_id"));

		assertPostFails("17157", association, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}
}
