/*
 * #!
 * Ontopia Rest
 * #-
 * Copyright (C) 2001 - 2016 The Ontopia Project
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

package net.ontopia.topicmaps.rest.v1.variant;

import java.net.URISyntaxException;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.Topic;
import net.ontopia.topicmaps.rest.model.TopicName;
import net.ontopia.topicmaps.rest.model.VariantName;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import org.junit.Assert;
import org.junit.Test;

/*
	NOTE: variants inherit scope from parent topicname, see
	http://www.isotopicmaps.org/sam/sam-model/#sect-variant
	There is however, no rule that specifies that the computed scope has to have at least one topic.
*/

public class VariantNameResourcePUTTest extends AbstractV1ResourceTest {

	public VariantNameResourcePUTTest() {
		super(VARIANTS_LTM, "variants");
	}

	/* -- Successfull requests -- */

	private VariantName createVariantName() {
		VariantName variant = new VariantName();
		variant.setValue("foo");
		variant.setTopicName(new TopicName("2"));
		return variant;
	}

	@Test
	public void testPUT() {
		VariantName added = put(createVariantName(), VariantName.class);

		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getObjectId());
		Assert.assertNotNull(added.getTopicName());
		Assert.assertEquals("2", added.getTopicName().getObjectId());
		Assert.assertEquals("foo", added.getValue());
		Assert.assertEquals(DataTypes.TYPE_STRING, added.getDataType());
	}

//	@Test
//	public void testWithTopicNameByItemIdentifier() {
//		VariantName variant = createVariantName();
//		TopicName name = new TopicName();
//		name.getItemIdentifiers().add(URILocator.create("foo:bar#network-location"));
//		variant.setTopicName(name);
//		VariantName added = put(variant, VariantName.class);
//
//		Assert.assertNotNull(added);
//		Assert.assertNotNull(added.getObjectId());
//		Assert.assertNotNull(added.getTopic());
//		Assert.assertEquals("261", added.getTopic().getObjectId());
//	}

	@Test
	public void testAlternativeDatatype() throws URISyntaxException {
		VariantName variant = createVariantName();
		variant.setValue("1");
		variant.setDatatype(DataTypes.TYPE_INTEGER);

		VariantName added = put(variant, VariantName.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getDataType());
		Assert.assertEquals(DataTypes.TYPE_INTEGER, added.getDataType());
		Assert.assertEquals("1", added.getValue());
	}

	@Test
	public void testAlternativeDatatype2() {
		VariantName variant = createVariantName();
		variant.setValue("1");
		variant.setDatatype(URILocator.create("dt:foo"));

		VariantName added = put(variant, VariantName.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getDataType());
		Assert.assertEquals(URILocator.create("dt:foo"), added.getDataType());
		Assert.assertEquals("1", added.getValue());
	}

	@Test
	public void testWithItemIdentifier() {
		VariantName variant = createVariantName();
		variant.getItemIdentifiers().add(URILocator.create("foo:barvariant:bar"));

		VariantName added = put(variant, VariantName.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getItemIdentifiers());
		Assert.assertFalse(added.getItemIdentifiers().isEmpty());
		Assert.assertEquals("foo:barvariant:bar", added.getItemIdentifiers().iterator().next().getAddress());
	}

	@Test
	public void testWithItemIdentifiers() {
		VariantName variant = createVariantName();
		variant.getItemIdentifiers().add(URILocator.create("bar:variant:foo"));
		variant.getItemIdentifiers().add(URILocator.create("bar:variant:bar"));

		VariantName added = put(variant, VariantName.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getItemIdentifiers());
		Assert.assertFalse(added.getItemIdentifiers().isEmpty());
		Assert.assertEquals(2, added.getItemIdentifiers().size());
		Assert.assertTrue(added.getItemIdentifiers().contains(URILocator.create("bar:variant:foo")));
		Assert.assertTrue(added.getItemIdentifiers().contains(URILocator.create("bar:variant:bar")));
	}

	@Test
	public void testWithEmptyIdentifiers() {
		VariantName variant = createVariantName();
		variant.getItemIdentifiers().clear();

		VariantName added = put(variant, VariantName.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getItemIdentifiers());
		Assert.assertTrue(added.getItemIdentifiers().isEmpty());
	}

	@Test
	public void testWithScope() {
		VariantName variant = createVariantName();
		variant.getScope().add(new Topic("1"));

		VariantName added = put(variant, VariantName.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getScope());
		Assert.assertFalse(added.getScope().isEmpty());
		Assert.assertEquals(1, added.getScope().size());
		assertContainsTopics(added.getScope(), "1");
	}

	@Test
	public void testWithoutScope() {
		VariantName variant = createVariantName();
		variant.getScope().clear();

		VariantName added = put(variant, VariantName.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getScope());
		Assert.assertTrue(added.getScope().isEmpty());
	}

	@Test
	public void testWithScopes() {
		VariantName variant = createVariantName();
		variant.getScope().add(new Topic("1"));
		variant.getScope().add(new Topic("3"));

		VariantName added = put(variant, VariantName.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getScope());
		Assert.assertFalse(added.getScope().isEmpty());
		Assert.assertEquals(2, added.getScope().size());
		assertContainsTopics(added.getScope(), "1", "3");
	}

	@Test
	public void testWithScopeByItemIdentifier() {
		VariantName variant = createVariantName();
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:bar#topic3"));
		variant.getScope().add(topic);

		VariantName added = put(variant, VariantName.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getScope());
		Assert.assertFalse(added.getScope().isEmpty());
		Assert.assertEquals(1, added.getScope().size());
		assertContainsTopics(added.getScope(), "10");
	}

	@Test
	public void testWithReification() {
		VariantName variant = createVariantName();
		variant.setReifier(new Topic("1"));

		VariantName added = put(variant, VariantName.class);
		Assert.assertNotNull(added);
		Assert.assertNotNull(added.getReifier());
		Assert.assertEquals("1", added.getReifier().getObjectId());
	}

	@Test
	public void testWithReificationByItemIdentifier() {
		VariantName variant = createVariantName();
		Topic topic = new Topic();
		topic.getItemIdentifiers().add(URILocator.create("foo:bar#topic1"));
		variant.setReifier(topic);

		VariantName added = put(variant, VariantName.class);
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
	public void testMissingTopicName() {
		VariantName variant = createVariantName();
		variant.setTopicName(null);
		assertPutFails(variant, OntopiaRestErrors.MANDATORY_FIELD_IS_NULL);
	}

	@Test
	public void testInvalidTopicName() {
		VariantName variant = createVariantName();
		variant.setTopicName(new TopicName("1"));
		assertPutFails(variant, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingTopicName() {
		VariantName variant = createVariantName();
		variant.setTopicName(new TopicName("unexisting_topic_id"));
		assertPutFails(variant, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testMissingValue() {
		VariantName variant = createVariantName();
		variant.setValue(null);
		assertPutFails(variant, OntopiaRestErrors.MANDATORY_FIELD_IS_NULL);
	}

	@Test
	public void testInvalidReification() {
		VariantName variant = createVariantName();
		variant.setReifier(new Topic("2"));
		assertPutFails(variant, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingReification() {
		VariantName variant = createVariantName();
		variant.setReifier(new Topic("unexisting_topic_id"));
		assertPutFails(variant, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}

	@Test
	public void testInvalidScope() {
		VariantName variant = createVariantName();
		variant.getScope().add(new Topic("2"));
		assertPutFails(variant, OntopiaRestErrors.MANDATORY_OBJECT_IS_WRONG_TYPE);
	}

	@Test
	public void testUnexistingScope() {
		VariantName variant = createVariantName();
		variant.getScope().add(new Topic("unexisting_topic_id"));
		assertPutFails(variant, OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL);
	}
}
