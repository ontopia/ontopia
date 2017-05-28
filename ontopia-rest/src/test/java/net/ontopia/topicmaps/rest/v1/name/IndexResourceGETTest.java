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

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.Collection;
import net.ontopia.topicmaps.rest.model.TopicName;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import org.junit.Assert;
import org.junit.Test;

public class IndexResourceGETTest extends AbstractV1ResourceTest {

	private final TypeReference<Collection<TopicName>> REF = new TypeReference<Collection<TopicName>>(){};

	public IndexResourceGETTest() {
		super(OPERA_TM, "names/index");
	}

	@Test
	public void testIndex() throws IOException {
		Collection<TopicName> names = post(null, REF, "Opera");

		Assert.assertNotNull(names);
		Assert.assertEquals(1, names.size());
		assertContainsTopics(names, "122");
	}

	@Test
	public void testUTF() throws IOException {
		Collection<TopicName> names = post(null, REF, "作曲家");

		Assert.assertNotNull(names);
		Assert.assertEquals(1, names.size());
		assertContainsTopics(names, "203");
	}

	@Test
	public void testEmoty() throws IOException {
		Collection<TopicName> names = post(null, REF, (Object) null);

		Assert.assertNotNull(names);
		Assert.assertTrue(names.isEmpty());
	}
}
