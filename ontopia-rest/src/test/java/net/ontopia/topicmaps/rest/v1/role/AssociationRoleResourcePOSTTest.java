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
package net.ontopia.topicmaps.rest.v1.role;

import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.Association;
import net.ontopia.topicmaps.rest.model.AssociationRole;
import net.ontopia.topicmaps.rest.model.Topic;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import org.junit.Assert;
import org.junit.Test;

public class AssociationRoleResourcePOSTTest extends AbstractV1ResourceTest {

	public AssociationRoleResourcePOSTTest() {
		super(ROLES_LTM, "roles");
	}

	@Test
	public void testAssociation() {
		AssociationRole role = get("3", AssociationRole.class);
		role.setAssociation(new Association("5"));

		AssociationRole changed = post("3", role, AssociationRole.class);

		Assert.assertNotNull(changed.getAssociation());
		Assert.assertEquals("2", changed.getAssociation().getObjectId());
	}

	@Test
	public void testInvalidAssociation() {
		AssociationRole role = get("3", AssociationRole.class);
		role.setAssociation(new Association("1"));

		AssociationRole changed = post("3", role, AssociationRole.class);

		Assert.assertNotNull(changed.getAssociation());
		Assert.assertEquals("2", changed.getAssociation().getObjectId());
	}

	@Test
	public void testUnexistingAssociation() {
		AssociationRole role = get("3", AssociationRole.class);
		role.setAssociation(new Association("unexisting_association_id"));

		AssociationRole changed = post("3", role, AssociationRole.class);

		Assert.assertNotNull(changed.getAssociation());
		Assert.assertEquals("2", changed.getAssociation().getObjectId());
	}

	@Test
	public void testVoidAssociation() {
		AssociationRole role = get("3", AssociationRole.class);
		role.setAssociation(null);

		AssociationRole changed = post("3", role, AssociationRole.class);

		Assert.assertNotNull(changed.getAssociation());
		Assert.assertEquals("2", changed.getAssociation().getObjectId());
	}

	@Test
	public void testPlayer() {
		AssociationRole role = get("3", AssociationRole.class);
		role.setPlayer(new Topic("4"));

		AssociationRole changed = post("3", role, AssociationRole.class);

		Assert.assertNotNull(changed.getPlayer());
		Assert.assertEquals("4", changed.getPlayer().getObjectId());
	}

	@Test
	public void testNullPlayer() {
		AssociationRole role = get("3", AssociationRole.class);
		role.setPlayer(null);

		AssociationRole changed = post("3", role, AssociationRole.class);

		Assert.assertNotNull(changed.getPlayer());
		Assert.assertEquals("1", changed.getPlayer().getObjectId());
	}

	@Test
	public void testPlayerByItemIdentifier() {
		AssociationRole role = get("3", AssociationRole.class);
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:bar#topic2"));
		role.setPlayer(topic);

		AssociationRole changed = post("3", role, AssociationRole.class);

		Assert.assertNotNull(changed.getPlayer());
		Assert.assertEquals("4", changed.getPlayer().getObjectId());
	}

	@Test
	public void testType() {
		AssociationRole role = get("3", AssociationRole.class);
		role.setType(new Topic("4"));

		AssociationRole changed = post("3", role, AssociationRole.class);

		Assert.assertNotNull(changed.getType());
		Assert.assertEquals("4", changed.getType().getObjectId());
	}

	@Test
	public void testNullType() {
		AssociationRole role = get("3", AssociationRole.class);
		role.setType(null);

		AssociationRole changed = post("3", role, AssociationRole.class);

		Assert.assertNotNull(changed.getType());
		Assert.assertEquals("1", changed.getType().getObjectId());
	}

	@Test
	public void testTypeByItemIdentifier() {
		AssociationRole role = get("3", AssociationRole.class);
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:bar#topic2"));
		role.setType(topic);

		AssociationRole changed = post("3", role, AssociationRole.class);

		Assert.assertNotNull(changed.getType());
		Assert.assertEquals("4", changed.getType().getObjectId());
	}

	@Test
	public void testReifier() {
		AssociationRole role = get("3", AssociationRole.class);
		role.setReifier(new Topic("4"));

		AssociationRole changed = post("3", role, AssociationRole.class);

		Assert.assertNotNull(changed.getReifier());
		Assert.assertEquals("4", changed.getReifier().getObjectId());
	}

	@Test
	public void testClearReifier() {
		AssociationRole role = get("3", AssociationRole.class);
		role.setReifier(null);

		AssociationRole changed = post("3", role, AssociationRole.class);

		Assert.assertNull(changed.getReifier());
	}

	@Test
	public void testReifierByItemIdentifier() {
		AssociationRole role = get("3", AssociationRole.class);
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:bar#topic1"));
		role.setReifier(topic);

		AssociationRole changed = post("3", role, AssociationRole.class);

		Assert.assertNotNull(changed.getReifier());
		Assert.assertEquals("1", changed.getReifier().getObjectId());
	}

	@Test
	public void testAddItemIdentifier() {
		AssociationRole role = get("3", AssociationRole.class);
		role.getItemIdentifiers().add(URILocator.create("foo:barbar40"));

		AssociationRole changed = post("3", role, AssociationRole.class);

		Assert.assertNotNull(changed.getItemIdentifiers());
		Assert.assertEquals(1, changed.getItemIdentifiers().size());
		Assert.assertEquals("foo:barbar40", changed.getItemIdentifiers().iterator().next().getAddress());
	}

	@Test
	public void testAddItemIdentifiers() {
		AssociationRole role = get("3", AssociationRole.class);
		role.getItemIdentifiers().add(URILocator.create("foo:barbar41"));
		role.getItemIdentifiers().add(URILocator.create("foo:barbar42"));

		AssociationRole changed = post("3", role, AssociationRole.class);

		Assert.assertNotNull(changed.getItemIdentifiers());
		Assert.assertEquals(2, changed.getItemIdentifiers().size());
	}

	@Test
	public void testRemoveItemIdentifier() {
		final URILocator locator = URILocator.create("foo:barto-remove");

		AssociationRole role = get("3", AssociationRole.class);
		role.getItemIdentifiers().add(locator);

		role = post("3", role, AssociationRole.class);
		Assert.assertNotNull(role.getItemIdentifiers());
		Assert.assertEquals(1, role.getItemIdentifiers().size());

		role.getItemIdentifiers().remove(locator);
		role = post("3", role, AssociationRole.class);
		Assert.assertNotNull(role.getItemIdentifiers());
		Assert.assertTrue(role.getItemIdentifiers().isEmpty());
	}

	@Test
	public void testClearItemIdentifiers() {
		final URILocator locator = URILocator.create("foo:barto-remove");

		AssociationRole role = get("3", AssociationRole.class);
		role.getItemIdentifiers().add(locator);

		role = post("3", role, AssociationRole.class);
		Assert.assertNotNull(role.getItemIdentifiers());
		Assert.assertEquals(1, role.getItemIdentifiers().size());

		role.getItemIdentifiers().clear();
		role = post("3", role, AssociationRole.class);
		Assert.assertNotNull(role.getItemIdentifiers());
		Assert.assertTrue(role.getItemIdentifiers().isEmpty());
	}

	@Test
	public void testChangeItemIdentifier() {
		final URILocator locator = URILocator.create("foo:barto-remove");

		AssociationRole role = get("3", AssociationRole.class);
		role.getItemIdentifiers().add(locator);

		role = post("3", role, AssociationRole.class);
		Assert.assertNotNull(role.getItemIdentifiers());
		Assert.assertEquals(1, role.getItemIdentifiers().size());

		role.getItemIdentifiers().remove(locator);
		role.getItemIdentifiers().add(URILocator.create("foo:barto-keep-role"));
		role = post("3", role, AssociationRole.class);
		Assert.assertNotNull(role.getItemIdentifiers());
		Assert.assertEquals(1, role.getItemIdentifiers().size());
		Assert.assertEquals("foo:barto-keep-role", role.getItemIdentifiers().iterator().next().getAddress());
	}

	@Test
	public void testChangeItemIdentifierVoid() {
		final URILocator locator = URILocator.create("foo:barto-keep-role-2");

		AssociationRole role = get("3", AssociationRole.class);
		role.getItemIdentifiers().add(locator);

		role = post("3", role, AssociationRole.class);
		Assert.assertNotNull(role.getItemIdentifiers());
		Assert.assertEquals(1, role.getItemIdentifiers().size());

		role.setItemIdentifiers(null);
		role = post("3", role, AssociationRole.class);
		Assert.assertNotNull(role.getItemIdentifiers());
		Assert.assertEquals(1, role.getItemIdentifiers().size());
	}

	/* -- Failing requests -- */

	@Test
	public void testInvalidPlayer() {
		AssociationRole role = get("3", AssociationRole.class);
		role.setPlayer(new Topic("2"));

		assertPostFails("3", role, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingPlayer() {
		AssociationRole role = get("3", AssociationRole.class);
		role.setPlayer(new Topic("unexistig_topic_id"));

		assertPostFails("3", role, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testInvalidType() {
		AssociationRole role = get("3", AssociationRole.class);
		role.setType(new Topic("2"));

		assertPostFails("3", role, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingType() {
		AssociationRole role = get("3", AssociationRole.class);
		role.setType(new Topic("unexistig_topic_id"));

		assertPostFails("3", role, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testInvalidReifier() {
		AssociationRole role = get("3", AssociationRole.class);
		role.setReifier(new Topic("2"));

		assertPostFails("3", role, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingReifier() {
		AssociationRole role = get("3", AssociationRole.class);
		role.setReifier(new Topic("unexistig_topic_id"));

		assertPostFails("3", role, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}
}
