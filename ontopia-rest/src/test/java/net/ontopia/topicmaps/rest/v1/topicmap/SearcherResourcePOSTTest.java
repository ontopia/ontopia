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

package net.ontopia.topicmaps.rest.v1.topicmap;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import org.junit.Assert;
import org.junit.Test;

public class SearcherResourcePOSTTest extends AbstractV1ResourceTest {

	private final TypeReference<Collection<Map>> REF = new TypeReference<Collection<Map>>(){};

	public SearcherResourcePOSTTest() {
		super(TOPICS_LTM, "search");
	}

	@Test
	public void testSearch() throws IOException {
		Collection<Map> results = post(null, REF, "sloc");

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());

		Iterator<Map> iterator = results.iterator();

		Map first = iterator.next();
		Assert.assertNotNull(first);
		Assert.assertEquals("B", first.get("class"));

		Map o1 = (Map) first.get("object");
		Assert.assertNotNull(o1);
		Assert.assertEquals("6", o1.get("objectId"));
	}

	/* failing requests */

	@Test
	public void searchWithoutIndex() {
		tmid = "empty.xtm";
		assertPostFails(null, "foo", OntopiaRestErrors.INDEX_NOT_SUPPORTED);
	}

	@Test
	public void searchEmpty() {
		assertPostFails(null, "", OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_NULL);
	}

	@Test
	public void searchInvalid() {
		assertPostFails(null, "*", OntopiaRestErrors.INDEX_USAGE_ERROR);
	}
}
