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

import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.AssociationRole;
import net.ontopia.topicmaps.rest.model.Topic;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import org.junit.Assert;
import org.junit.Test;

public class RolesResourcePUTTest extends AbstractV1ResourceTest {

	public RolesResourcePUTTest() {
		super(OPERA_TM, "associations");
	}

	@Test
	public void testAdd() {
		AssociationRole role = new AssociationRole();
		role.setType(new Topic("1"));
		role.setPlayer(new Topic("1"));
		AssociationRole added = put("14698/roles", role, AssociationRole.class);

		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getObjectId());
	}

	@Test
	public void testAddUnexisting() {
		AssociationRole role = new AssociationRole();
		role.setType(new Topic("1"));
		role.setPlayer(new Topic("1"));
		assertPutFails("unexisting/roles", role, OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_NULL);
	}

	@Test
	public void testEmpty() {
		assertPutFails("14698/roles", null, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testAddInvalid() {
		assertPutFails("1/roles", new AssociationRole(), OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}
}
