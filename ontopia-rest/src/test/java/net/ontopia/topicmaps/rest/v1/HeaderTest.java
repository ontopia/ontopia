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

package net.ontopia.topicmaps.rest.v1;

import net.ontopia.topicmaps.rest.OntopiaTestResource;
import org.junit.Assert;
import org.junit.Test;
import org.restlet.data.Header;
import org.restlet.data.Method;
import org.restlet.util.Series;

public class HeaderTest extends AbstractV1ResourceTest {

	public HeaderTest() {
		super(TOPICS_LTM, "topics");
	}

	@Test
	public void testHeaders() {
		OntopiaTestResource request = new OntopiaTestResource(Method.GET, getUrl(null), defaultMediatype);
		request.request();
		Series<Header> headers = request.getResponse().getHeaders();

		Assert.assertNotNull(headers);
		Assert.assertEquals("v1", headers.getFirstValue("X-Ontopia-API-Version"));
		Assert.assertEquals("n.o.t.r.OntopiaRestApplication", headers.getFirstValue("X-Ontopia-Application"));
		Assert.assertEquals("n.o.t.r.v1.topic.TopicsResource", headers.getFirstValue("X-Ontopia-Resource"));
		Assert.assertEquals("topics.ltm", headers.getFirstValue("X-Ontopia-Topicmap"));
	}
}
