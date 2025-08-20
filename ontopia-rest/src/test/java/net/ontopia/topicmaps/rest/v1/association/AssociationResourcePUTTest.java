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

import java.util.HashSet;
import java.util.Set;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.Association;
import net.ontopia.topicmaps.rest.model.AssociationRole;
import net.ontopia.topicmaps.rest.model.Topic;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import org.junit.Assert;
import org.junit.Test;

public class AssociationResourcePUTTest extends AbstractV1ResourceTest {

	public AssociationResourcePUTTest() {
		super(ASSOCIATIONS_LTM, "associations");
	}

	/* -- Successfull requests -- */

	private Association createAssociation() {
		Association association = new Association();
		association.setType(new Topic("1"));
		Set<AssociationRole> roles = new HashSet<>();
		AssociationRole role = new AssociationRole();
		role.setType(new Topic("1"));
		role.setPlayer(new Topic("1"));
		roles.add(role);
		association.setRoles(roles);
		return association;
	}

	@Test
	public void testPUT() {
		Association added = put(createAssociation(), Association.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getObjectId());
		Assert.assertNotNull(added.getType());
		Assert.assertEquals("1", added.getType().getObjectId());
		Assert.assertNotNull(added.getRoles());
		Assert.assertEquals(1, added.getRoles().size());
		AssociationRole r = added.getRoles().iterator().next();
		Assert.assertNotNull(r);
		Assert.assertNotNull(r.getType());
		Assert.assertEquals("1", r.getType().getObjectId());
		Assert.assertNotNull(r.getPlayer());
		Assert.assertEquals("1", r.getPlayer().getObjectId());
	}

	@Test
	public void testMultipleRoles() {
		Association association = createAssociation();
		AssociationRole role = new AssociationRole();
		role.setType(new Topic("4"));
		role.setPlayer(new Topic("4"));
		association.getRoles().add(role);

		Association added = put(association, Association.class);
		Assert.assertNotNull(added.getRoles());
		Assert.assertEquals(2, added.getRoles().size());
	}

	@Test
	public void testRoleWithItemIdentifier() {
		Association association = createAssociation();
		AssociationRole role = new AssociationRole();
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:bar#topic1"));
		role.setType(topic);
		role.setPlayer(topic);
		association.getRoles().clear();
		association.getRoles().add(role);

		Association added = put(association, Association.class);
		Assert.assertNotNull(added.getRoles());
		Assert.assertEquals(1, added.getRoles().size());
		AssociationRole r = added.getRoles().iterator().next();
		Assert.assertNotNull(r);
		Assert.assertNotNull(r.getType());
		Assert.assertEquals("1", r.getType().getObjectId());
		Assert.assertNotNull(r.getPlayer());
		Assert.assertEquals("1", r.getPlayer().getObjectId());
	}
	
	@Test
	public void testInvalidRole() {
		Association association = createAssociation();
		association.getRoles().clear();
		AssociationRole role = new AssociationRole("1");
		role.setType(new Topic("1"));
		role.setPlayer(new Topic("1"));
		association.getRoles().add(role);
		Association added = put(association, Association.class);
		Assert.assertNotNull(added.getRoles());
		AssociationRole r = added.getRoles().iterator().next();
		Assert.assertFalse("1".equals(added.getObjectId()));
		Assert.assertNotNull(r.getType());
		Assert.assertEquals("1", r.getType().getObjectId());
		Assert.assertNotNull(r.getPlayer());
		Assert.assertEquals("1", r.getPlayer().getObjectId());
	}

	@Test
	public void testUnexistingRole() {
		Association association = createAssociation();
		association.getRoles().clear();
		AssociationRole role = new AssociationRole("unexisting_role_id");
		role.setType(new Topic("1"));
		role.setPlayer(new Topic("1"));
		association.getRoles().add(role);
		Association added = put(association, Association.class);
		Assert.assertNotNull(added.getRoles());
		AssociationRole r = added.getRoles().iterator().next();
		Assert.assertFalse("unexisting_role_id".equals(added.getObjectId()));
		Assert.assertNotNull(r.getType());
		Assert.assertEquals("1", r.getType().getObjectId());
		Assert.assertNotNull(r.getPlayer());
		Assert.assertEquals("1", r.getPlayer().getObjectId());
	}

	@Test
	public void testExistingRole() {
		Association association = createAssociation();
		association.getRoles().clear();
		AssociationRole role = new AssociationRole("6");
		role.setType(new Topic("1"));
		role.setPlayer(new Topic("1"));
		association.getRoles().add(role);
		Association added = put(association, Association.class);
		Assert.assertNotNull(added.getRoles());
		AssociationRole r = added.getRoles().iterator().next();
		Assert.assertFalse("6".equals(added.getObjectId()));
		Assert.assertNotNull(r.getType());
		Assert.assertEquals("1", r.getType().getObjectId());
		Assert.assertNotNull(r.getPlayer());
		Assert.assertEquals("1", r.getPlayer().getObjectId());
	}

	@Test
	public void testWithTypeByItemIdentifier() {
		Association association = createAssociation();
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:bar#topic1"));
		association.setType(topic);
		Association added = put(association, Association.class);

		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getObjectId());
		Assert.assertNotNull(added.getType());
		Assert.assertEquals("1", added.getType().getObjectId());
	}

	@Test
	public void testWithItemIdentifier() {
		Association association = createAssociation();
		association.getItemIdentifiers().add(URILocator.create("foo:barassociation:bar"));
		Association added = put(association, Association.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getItemIdentifiers());
		Assert.assertFalse(added.getItemIdentifiers().isEmpty());
		Assert.assertEquals("foo:barassociation:bar", added.getItemIdentifiers().iterator().next().getAddress());
	}

	@Test
	public void testWithItemIdentifiers() {
		Association association = createAssociation();
		association.getItemIdentifiers().add(URILocator.create("bar:association:foo"));
		association.getItemIdentifiers().add(URILocator.create("bar:association:bar"));

		Association added = put(association, Association.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getItemIdentifiers());
		Assert.assertFalse(added.getItemIdentifiers().isEmpty());
		Assert.assertEquals(2, added.getItemIdentifiers().size());
		Assert.assertTrue(added.getItemIdentifiers().contains(URILocator.create("bar:association:foo")));
		Assert.assertTrue(added.getItemIdentifiers().contains(URILocator.create("bar:association:bar")));
	}

	@Test
	public void testWithEmptyIdentifiers() {
		Association association = createAssociation();
		association.getItemIdentifiers().clear();

		Association added = put(association, Association.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getItemIdentifiers());
		Assert.assertTrue(added.getItemIdentifiers().isEmpty());
	}

	@Test
	public void testWithScope() {
		Association association = createAssociation();
		association.getScope().add(new Topic("1"));

		Association added = put(association, Association.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getScope());
		Assert.assertFalse(added.getScope().isEmpty());
		Assert.assertEquals("1", added.getScope().iterator().next().getObjectId());
	}

	@Test
	public void testWithScopes() {
		Association association = createAssociation();
		association.getScope().add(new Topic("1"));
		association.getScope().add(new Topic("4"));

		Association added = put(association, Association.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getScope());
		Assert.assertFalse(added.getScope().isEmpty());
		Assert.assertEquals(2, added.getScope().size());
		assertContainsTopics(added.getScope(), "1", "4");
	}

	@Test
	public void testWithScopeByItemIdentifier() {
		Association association = createAssociation();
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:bar#topic2"));
		association.getScope().add(topic);

		Association added = put(association, Association.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getScope());
		Assert.assertFalse(added.getScope().isEmpty());
		Assert.assertEquals(1, added.getScope().size());
		assertContainsTopics(added.getScope(), "4");
	}

	@Test
	public void testWithReification() {
		Association association = createAssociation();
		association.setReifier(new Topic("1"));

		Association added = put(association, Association.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getReifier());
		Assert.assertEquals("1", added.getReifier().getObjectId());
	}

	@Test
	public void testWithReificationByItemIdentifier() {
		Association association = createAssociation();
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:bar#topic1"));
		association.setReifier(topic);

		Association added = put(association, Association.class);
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
		assertPutFails(new Topic("foobar"), OntopiaRestErrors.MANDATORY_FIELD_IS_NULL);
	}

	@Test
	public void testMissingType() {
		Association name = createAssociation();
		name.setType(null);
		assertPutFails(name, OntopiaRestErrors.MANDATORY_FIELD_IS_NULL);
	}

	@Test
	public void testInvalidType() {
		Association association = createAssociation();
		association.setType(new Topic("7"));
		assertPutFails(association, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingType() {
		Association association = createAssociation();
		association.setType(new Topic("unexisting_topic_id"));
		assertPutFails(association, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testEmptyRoles() {
		Association association = createAssociation();
		association.getRoles().clear();
		assertPutFails(association, OntopiaRestErrors.MANDATORY_FIELD_IS_NULL);
	}

	@Test
	public void testInvalidReification() {
		Association association = createAssociation();
		association.setReifier(new Topic("7"));
		assertPutFails(association, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingReification() {
		Association association = createAssociation();
		association.setReifier(new Topic("unexisting_topic_id"));
		assertPutFails(association, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testInvalidScope() {
		Association association = createAssociation();
		association.getScope().add(new Topic("7"));
		assertPutFails(association, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingScope() {
		Association association = createAssociation();
		association.getScope().add(new Topic("unexisting_topic_id"));
		assertPutFails(association, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}
}
