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

package net.ontopia.topicmaps.rest.v1.query;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.v1.AbstractV1ResourceTest;
import org.junit.Assert;
import org.junit.Test;

public class QueryResourcePOSTTest extends AbstractV1ResourceTest {
	
	private final TypeReference<Collection<Map<String, Object>>> REF = new TypeReference<Collection<Map<String, Object>>>(){};

	public QueryResourcePOSTTest() {
		super(OPERA_TM, "query");
	}
	
	@Test
	public void testQuery() throws IOException {
		Collection<Map<String, Object>> result = post(null, REF, "reifies($topic, $reified), topicmap($reified)?");
		
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		
		Map map = result.iterator().next();
		
		Assert.assertTrue(map.containsKey("topic"));
		Assert.assertTrue(map.containsKey("reified"));
		
		// more advanced tests requires improvements in use of jackson
	}
	
	@Test
	public void testInvalidQuery() throws IOException {
		assertPostFails(null, "foo", OntopiaRestErrors.INVALID_QUERY);
	}

	@Test
	public void testInvalidQueryLanguage() throws IOException {
		assertPostFails("unknown", "?", OntopiaRestErrors.UNKNOWN_QUERY_LANGUAGE);
	}
}
