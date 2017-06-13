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

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.Collection;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.Association;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import org.junit.Assert;
import org.junit.Test;

public class AssociationsResourceGETTest extends AbstractV1ResourceTest {

	private final TypeReference<Collection<Association>> REF = new TypeReference<Collection<Association>>(){};

	public AssociationsResourceGETTest() {
		super(OPERA_TM, null);
	}

	@Test
	public void testTopicAssociations() throws IOException {
		Collection<Association> associations = get("topics/301/associations", REF);

		Assert.assertNotNull(associations);
		Assert.assertEquals(3, associations.size());
		Assert.assertEquals(Association.class, associations.iterator().next().getClass());
		assertContainsTopics(associations, "406", "403", "400");
	}

	@Test
	public void testUnexistingTopicAssociations() throws IOException {
		Collection<Association> associations = get("topics/unexisting/associations", REF);

		Assert.assertNotNull(associations);
		Assert.assertEquals(100, associations.size()); // all associations in the topicmap, paged
	}

	@Test
	public void testTopicAssociationsByType() throws IOException {
		Collection<Association> associations = get("topics/1/associations/26", REF);

		Assert.assertNotNull(associations);
		Assert.assertEquals(1, associations.size());
		assertContainsTopics(associations, "27");
	}

	@Test
	public void testUnexistingTopicAssociationsByType() throws IOException {
		Collection<Association> associations = get("topics/301/associations/unexisting", REF);

		Assert.assertNotNull(associations);
		Assert.assertEquals(3, associations.size()); // all associations of the topic
	}

	@Test
	public void testTopicmapAssociations() throws IOException {
		Collection<Association> associations = get("associations", REF);

		Assert.assertNotNull(associations);
		Assert.assertEquals(100, associations.size()); // paged
	}

	@Test
	public void testTopicmapAssociationsByType() throws IOException {
		Collection<Association> associations = get("associations/typed/26", REF);

		Assert.assertNotNull(associations);
		Assert.assertEquals(1, associations.size());
		assertContainsTopics(associations, "27");
	}

	/* -- Failing requests -- */
	
	@Test
	public void testInvalidTopicAssociations() {
		assertGetFails("topics/13/associations", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}

	@Test
	public void testTopicAssociationsByInvalidType() {
		assertGetFails("topics/1/associations/13", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}

	@Test
	public void testTopicmapAssociationsByInvalidType() {
		assertGetFails("associations/typed/13", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE);
	}
}
