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

public class AssociationRoleResourcePUTTest extends AbstractV1ResourceTest {

	public AssociationRoleResourcePUTTest() {
		super(ROLES_LTM, "roles");
	}

	/* -- Successfull requests -- */

	private AssociationRole createAssociationRole() {
		AssociationRole role = new AssociationRole();
		role.setAssociation(new Association("2"));
		role.setPlayer(new Topic("1"));
		role.setType(new Topic("4"));
		return role;
	}

	@Test
	public void testPUT() {
		AssociationRole added = put(createAssociationRole(), AssociationRole.class);

		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getObjectId());
		Assert.assertNotNull(added.getPlayer());
		Assert.assertEquals("1", added.getPlayer().getObjectId());
		Assert.assertNotNull(added.getType());
		Assert.assertEquals("4", added.getType().getObjectId());
		Assert.assertNotNull(added.getAssociation());
		Assert.assertEquals("2", added.getAssociation().getObjectId());
	}

	@Test
	public void testWithPlayerByItemIdentifier() {
		AssociationRole role = createAssociationRole();
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:bar#topic1"));
		role.setPlayer(topic);
		AssociationRole added = put(role, AssociationRole.class);

		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getObjectId());
		Assert.assertNotNull(added.getPlayer());
		Assert.assertEquals("1", added.getPlayer().getObjectId());
	}

	@Test
	public void testWithTypeByItemIdentifier() {
		AssociationRole role = createAssociationRole();
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:bar#topic2"));
		role.setType(topic);
		AssociationRole added = put(role, AssociationRole.class);

		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getObjectId());
		Assert.assertNotNull(added.getType());
		Assert.assertEquals("4", added.getType().getObjectId());
	}

	@Test
	public void testWithItemIdentifier() {
		AssociationRole role = createAssociationRole();
		role.getItemIdentifiers().add(URILocator.create("foo:barbar30"));

		AssociationRole added = put(role, AssociationRole.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getItemIdentifiers());
		Assert.assertFalse(added.getItemIdentifiers().isEmpty());
		Assert.assertEquals("foo:barbar30", added.getItemIdentifiers().iterator().next().getAddress());
	}

	@Test
	public void testWithItemIdentifiers() {
		AssociationRole role = createAssociationRole();
		role.getItemIdentifiers().add(URILocator.create("bar:foo31"));
		role.getItemIdentifiers().add(URILocator.create("bar:bar32"));

		AssociationRole added = put(role, AssociationRole.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getItemIdentifiers());
		Assert.assertFalse(added.getItemIdentifiers().isEmpty());
		Assert.assertEquals(2, added.getItemIdentifiers().size());
		Assert.assertTrue(added.getItemIdentifiers().contains(URILocator.create("bar:foo31")));
		Assert.assertTrue(added.getItemIdentifiers().contains(URILocator.create("bar:bar32")));
	}

	@Test
	public void testWithEmptyIdentifiers() {
		AssociationRole role = createAssociationRole();
		role.getItemIdentifiers().clear();

		AssociationRole added = put(role, AssociationRole.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getItemIdentifiers());
		Assert.assertTrue(added.getItemIdentifiers().isEmpty());
	}

	@Test
	public void testWithReification() {
		AssociationRole role = createAssociationRole();
		role.setReifier(new Topic("1"));

		AssociationRole added = put(role, AssociationRole.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getReifier());
		Assert.assertEquals("1", added.getReifier().getObjectId());
	}

	@Test
	public void testWithReificationByItemIdentifier() {
		AssociationRole role = createAssociationRole();
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:bar#topic1"));
		role.setReifier(topic);

		AssociationRole added = put(role, AssociationRole.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getReifier());
		Assert.assertEquals("1", added.getReifier().getObjectId());
	}

	/* -- Failing requests -- */

	@Test
	public void testEmptyBody() {
		assertPutFails(null, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testWrongObjectType() {
		assertPutFails(new Topic("foobar"), OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testMissingAssociation() {
		AssociationRole role = createAssociationRole();
		role.setAssociation(null);
		assertPutFails(role, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testInvalidAssociation() {
		AssociationRole role = createAssociationRole();
		role.setAssociation(new Association("1"));
		assertPutFails(role, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingAssociation() {
		AssociationRole role = createAssociationRole();
		role.setAssociation(new Association("unexisting_association_id"));
		assertPutFails(role, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testMissingPlayer() {
		AssociationRole role = createAssociationRole();
		role.setPlayer(null);
		assertPutFails(role, OntopiaRestErrors.MANDATORY_FIELD_IS_NULL);
	}

	@Test
	public void testInvalidPlayer() {
		AssociationRole role = createAssociationRole();
		role.setPlayer(new Topic("2"));
		assertPutFails(role, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingPlayer() {
		AssociationRole role = createAssociationRole();
		role.setPlayer(new Topic("unexisting_topic_id"));
		assertPutFails(role, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testMissingType() {
		AssociationRole role = createAssociationRole();
		role.setType(null);
		assertPutFails(role, OntopiaRestErrors.MANDATORY_FIELD_IS_NULL);
	}

	@Test
	public void testInvalidType() {
		AssociationRole role = createAssociationRole();
		role.setType(new Topic("2"));
		assertPutFails(role, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingType() {
		AssociationRole role = createAssociationRole();
		role.setType(new Topic("unexisting_topic_id"));
		assertPutFails(role, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testInvalidReification() {
		AssociationRole role = createAssociationRole();
		role.setReifier(new Topic("2"));
		assertPutFails(role, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingReification() {
		AssociationRole role = createAssociationRole();
		role.setReifier(new Topic("unexisting_topic_id"));
		assertPutFails(role, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}
}
