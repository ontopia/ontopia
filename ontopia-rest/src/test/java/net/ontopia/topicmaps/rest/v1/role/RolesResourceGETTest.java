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

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.Collection;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.AssociationRole;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import org.junit.Assert;
import org.junit.Test;

public class RolesResourceGETTest extends AbstractV1ResourceTest {

	private final TypeReference<Collection<AssociationRole>> REF = new TypeReference<Collection<AssociationRole>>(){};

	public RolesResourceGETTest() {
		super(OPERA_TM, null);
	}

	@Test
	public void testTopicAssociationRoles() throws IOException {
		Collection<AssociationRole> roles = get("topics/5772/roles", REF);

		Assert.assertNotNull(roles);
		Assert.assertEquals(22, roles.size());
		Assert.assertEquals(AssociationRole.class, roles.iterator().next().getClass());
		assertContainsTopics(roles, "9694", "9706", "9709");
	}

	@Test
	public void testUnexistingTopicAssociationRoles() throws IOException {
		Collection<AssociationRole> roles = get("topics/unexisting/roles", REF);

		Assert.assertNotNull(roles);
		Assert.assertTrue(roles.isEmpty());
	}

	@Test
	public void testTopicAssociationRolesByType() throws IOException {
		Collection<AssociationRole> roles = get("topics/5772/roles/434", REF);

		Assert.assertNotNull(roles);
		Assert.assertEquals(1, roles.size());
		assertContainsTopics(roles, "15344");
	}

	@Test
	public void testUnexistingTopicAssociationRolesByType() throws IOException {
		Collection<AssociationRole> roles = get("topics/5772/roles/unexisting", REF); // fallback to all roles

		Assert.assertNotNull(roles);
		Assert.assertEquals(22, roles.size());
		assertContainsTopics(roles, "9694", "9706", "9709");
	}

	@Test
	public void testTopicAssociationRolesByTypes() throws IOException {
		Collection<AssociationRole> roles = get("topics/5772/roles/301/441", REF);

		Assert.assertNotNull(roles);
		Assert.assertEquals(15, roles.size());
		assertContainsTopics(roles, "9694");
	}

	@Test
	public void testUnexistingTopicAssociationRolesByTypes() throws IOException {
		Collection<AssociationRole> roles = get("topics/5772/roles/434/unexisting", REF); // fallback to all roles by type

		Assert.assertNotNull(roles);
		Assert.assertEquals(1, roles.size());
		assertContainsTopics(roles, "15344");
	}

	@Test
	public void testAssociationRoles() throws IOException {
		Collection<AssociationRole> roles = get("associations/15343/roles", REF);

		Assert.assertNotNull(roles);
		Assert.assertEquals(2, roles.size());
		assertContainsTopics(roles, "15344", "15345");
	}

	@Test
	public void testUnexistingAssociationRoles() throws IOException {
		Collection<AssociationRole> roles = get("associations/unexisting/roles", REF);

		Assert.assertNotNull(roles);
		Assert.assertTrue(roles.isEmpty());
	}

	@Test
	public void testAssociationRolesByType() throws IOException {
		Collection<AssociationRole> roles = get("associations/15343/roles/434", REF);

		Assert.assertNotNull(roles);
		Assert.assertEquals(1, roles.size());
		assertContainsTopics(roles, "15344");
	}

	@Test
	public void testUnexistingAssociationRolesByType() throws IOException {
		Collection<AssociationRole> roles = get("associations/unexisting/roles/263", REF); // fallback to tm.getRolesByType

		Assert.assertNotNull(roles);
		Assert.assertEquals(100, roles.size());
	}

	@Test
	public void testAssociationRolesByUnexistingType() throws IOException {
		Collection<AssociationRole> roles = get("associations/15343/roles/unexisting", REF); // fallback to assoc.getRoles

		Assert.assertNotNull(roles);
		Assert.assertEquals(2, roles.size());
		assertContainsTopics(roles, "15344", "15345");
	}

	@Test
	public void testTopicmapAssociationRolesByType() throws IOException {
		Collection<AssociationRole> roles = get("roles/typed/355", REF);

		Assert.assertNotNull(roles);
		Assert.assertEquals(100, roles.size()); // paged
	}

	/* -- Failing requests -- */

	@Test
	public void testInvalidTopicAssociationRoles() {
		assertGetFails("topics/13/roles", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}

	@Test
	public void testTopicAssociationRolesByInvalidType() {
		assertGetFails("topics/1/roles/13", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}

	@Test
	public void testTopicAssociationRolesByInvalidAssociationType() {
		assertGetFails("topics/1/roles/355/13", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}

	@Test
	public void testInvalidAssociationAssociationRoles() {
		assertGetFails("associations/13/roles", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}

	@Test
	public void testAssociationAssociationRolesByInvalidType() {
		assertGetFails("topics/10257/roles/13", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}

	@Test
	public void testAssociationAssociationRolesByInvalidAssociationType() {
		assertGetFails("topics/10257/roles/355/13", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}

	@Test
	public void testTopicmapAssociationRolesByInvalidType() {
		assertGetFails("roles/typed/13", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}
}
