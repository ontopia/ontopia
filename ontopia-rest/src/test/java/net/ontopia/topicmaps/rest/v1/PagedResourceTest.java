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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collection;
import net.ontopia.topicmaps.rest.Constants;
import net.ontopia.topicmaps.rest.OntopiaTestResource;
import net.ontopia.topicmaps.rest.model.Topic;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.restlet.Response;
import org.restlet.data.Method;

public class PagedResourceTest extends AbstractV1ResourceTest {

	private final TypeReference<Collection<Topic>> REF = new TypeReference<Collection<Topic>>(){};
	private final ObjectMapper OM = new ObjectMapper();

	private Collection<Topic> topics;
	private Response response;

	@After
	public void cleanup() {
		if (topics != null) {
			topics.clear();
			topics = null;
		}
		if (response != null) {
			response = null;
		}
	}

	public PagedResourceTest() {
		super(PAGED_LTM, "topics");
	}

	private void request(Integer limit, Integer offset) throws IOException {
		OntopiaTestResource request = new OntopiaTestResource(Method.GET, getUrl(null), defaultMediatype);

		if (limit != null) {
			request.setQueryValue("limit", limit.toString());
		}
		if (offset != null) {
			request.setQueryValue("offset", offset.toString());
		}

		topics = OM.readValue(request.request().getText(), REF);
		response = request.getResponse();
	}

	@Test
	public void testPaged() throws IOException {
		request(null, null);
		Assert.assertEquals(100, topics.size());
		int count = getCount(response);
		Assert.assertEquals(300, count);
		Assert.assertEquals(100, getLimit(response));
		Assert.assertEquals(0, getOffset(response));
	}

	@Test
	public void testOffsetToLarge() throws IOException {
		request(null, null);
		int count = getCount(response);

		request(null, count + 10);
		Assert.assertEquals(0, topics.size());
		Assert.assertEquals(count, getCount(response));
		Assert.assertEquals(100, getLimit(response));
		Assert.assertEquals(count + 10, getOffset(response));
	}

	@Test
	public void testOffset() throws IOException {
		request(null, 50);
		Assert.assertEquals(100, topics.size());
		Assert.assertEquals(300, getCount(response));
		Assert.assertEquals(100, getLimit(response));
		Assert.assertEquals(50, getOffset(response));
	}

	@Test
	public void testLimit() throws IOException {
		request(10, null);
		Assert.assertEquals(10, topics.size());
		Assert.assertEquals(300, getCount(response));
		Assert.assertEquals(10, getLimit(response));
		Assert.assertEquals(0, getOffset(response));
	}

	@Test
	public void testNegativeLimit() throws IOException {
		request(-10, null);
		Assert.assertEquals(0, topics.size());
		Assert.assertEquals(300, getCount(response));
		Assert.assertEquals(-10, getLimit(response));
		Assert.assertEquals(0, getOffset(response));
	}

	@Test
	public void testNegativeOffset() throws IOException {
		request(null, -10);
		Assert.assertEquals(0, topics.size());
		Assert.assertEquals(300, getCount(response));
		Assert.assertEquals(100, getLimit(response));
		Assert.assertEquals(-10, getOffset(response));
	}
	
	private int getCount(Response response) {
		return getIntHeader(response, Constants.HEADER_PAGING_COUNT);
	}

	private int getLimit(Response response) {
		return getIntHeader(response, Constants.HEADER_PAGING_LIMIT);
	}

	private int getOffset(Response response) {
		return getIntHeader(response, Constants.HEADER_PAGING_OFFSET);
	}

	private int getIntHeader(Response response, String header) {
		try {
			return Integer.parseInt(response.getHeaders().getFirstValue(header));
		} catch (NumberFormatException nfe) {
			return -1;
		}
	}	
}
