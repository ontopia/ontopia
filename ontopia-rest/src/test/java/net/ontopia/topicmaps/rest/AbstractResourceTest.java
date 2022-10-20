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

package net.ontopia.topicmaps.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.ontopia.topicmaps.rest.OntopiaTestResource.OntopiaTestResourceException;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.Error;
import net.ontopia.topicmaps.rest.model.TMObject;
import org.junit.After;
import org.junit.Assert;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractResourceTest {
	
	protected String tmid;
	private final String path;
	protected MediaType defaultMediatype = MediaType.APPLICATION_JSON;
	protected final Logger logger;

	public AbstractResourceTest() {
		this(null, null);
	}
	
	public AbstractResourceTest(String tmid, String path) {
		this.tmid = tmid;
		this.path = path;
		logger = LoggerFactory.getLogger(getClass());
	}

	protected String getWebappRoot() {
		String host = "localhost";
		String port = System.getProperty("ontopia.jetty.port", "8080");
		//String context = "ontopia-rest";
		return "http://" + host + ":" + port + "/";// + context + "/";
	}
	
	protected String getAPIRoot() {
		return getWebappRoot() + "api/";
	}
	
	@After
	public void reset() throws IOException {
		if (tmid != null) {
			new OntopiaTestResource(Method.POST, getAPIRoot() + "topicmaps/" + tmid + "/reload", defaultMediatype).request();
		}
	}
	
	protected String getUrl(String url) {
		StringBuilder b = new StringBuilder(getAPIRoot());
		if (tmid != null) {
			b.append("topicmaps/").append(tmid);
		}
				
		if (path != null) { b.append("/").append(path); }
		if (url != null) { b.append("/").append(url); }
		return b.toString();
	}
	
	/* -- Expected result requests -- */
	
	protected <T> T request(String url, Method method, Object object, Class<T> expected) {
		try {
			return new OntopiaTestResource(method, getUrl(url), defaultMediatype).request(object, expected);
		} catch (OntopiaTestResourceException e) {
			Assert.fail("Unexpected request error encountered: " + e.getError().getMessage());
		} catch (ResourceException re) {
			Assert.fail("Unexpected request error encountered: " + re.getMessage() + " " + re.getStatus().getReasonPhrase());
		}
		return null;
	}
	protected <T> T get(String url, Class<T> expected) {
		return request(url, Method.GET, null, expected);
	}
	protected Map<String, Object> getAsJson(String url) throws IOException {
		return new ObjectMapper().readValue(
				new OntopiaTestResource(Method.GET, getUrl(url), defaultMediatype).request().getText(), 
				new TypeReference<Map<String, Object>>(){}
		);
	}
	protected <T> T get(String url, TypeReference<T> expected) throws IOException {
		return new ObjectMapper().readValue(
				new OntopiaTestResource(Method.GET, getUrl(url), defaultMediatype).request().getText(), expected
		);
	}
	protected <T> T post(String url, TypeReference<T> expected, Object body) throws IOException {
		return new ObjectMapper().readValue(
				new OntopiaTestResource(Method.GET, getUrl(url), defaultMediatype).post(body).getText(), expected
		);
	}
	protected <T> T put(String url, TypeReference<T> expected, Object body) throws IOException {
		return new ObjectMapper().readValue(
				new OntopiaTestResource(Method.PUT, getUrl(url), defaultMediatype).put(body).getText(), expected
		);
	}
	protected Representation getRaw(String url, MediaType mime) throws IOException {
		return new OntopiaTestResource(Method.GET, getUrl(url), mime).request();
	}
	protected <T> T put(Object object, Class<T> expected) {
		return request(null, Method.PUT, object, expected);
	}
	protected <T> T put(String url, Object object, Class<T> expected) {
		return request(url, Method.PUT, object, expected);
	}
	protected <T> T post(String url, Object object, Class<T> expected) {
		return request(url, Method.POST, object, expected);
	}
	protected <T> T delete(String url, Class<T> expected) {
		return request(url, Method.DELETE, null, expected);
	}
	
	/* -- Expected request failures -- */

	protected void assertRequestFails(String url, Method method, Object object, OntopiaRestErrors expected) {
		assertRequestFails(url, method, defaultMediatype, object, expected);
	}
	protected void assertRequestFails(String url, Method method, MediaType preferred, Object object, OntopiaRestErrors expected) {
		OntopiaTestResource cr = new OntopiaTestResource(method, getUrl(url), preferred);
		try {
			cr.request(object, Object.class);
			Assert.fail("Expected Ontopia error " + expected.name() + ", but request succeeded");
		} catch (OntopiaTestResourceException e) {
			Error result = e.getError();
			try {
				Assert.assertNotNull("Expected error, found null", result);
				Assert.assertEquals("Ontopia error code mismatch", expected.getCode(), result.getCode());
				Assert.assertEquals("HTTP status code mismatch", expected.getStatus().getCode(), result.getHttpcode());
				Assert.assertNotNull("Error message is empty", result.getMessage());
			} catch (AssertionError ae) {
				Assert.fail("Expected ontopia error " + expected.name() + 
						", but received [" + result.getHttpcode() + ":" + result.getCode() + ", " + result.getMessage() + "]");
			}
		} catch (ResourceException re) {
			Assert.fail("Expected ontopia error " + expected.name() + 
					", but received [" + re.getStatus().getCode() + ":" + re.getStatus().getDescription() + "]");
		}
	}
	protected void assertGetFails(String url, OntopiaRestErrors expected) {
		assertRequestFails(url, Method.GET, null, expected);
	}
	protected void assertGetFails(String url, MediaType preferred, OntopiaRestErrors expected) {
		assertRequestFails(url, Method.GET, preferred, null, expected);
	}
	protected void assertPutFails(Object object, OntopiaRestErrors expected) {
		assertRequestFails(null, Method.PUT, object, expected);
	}
	protected void assertPutFails(String url, Object object, OntopiaRestErrors expected) {
		assertRequestFails(url, Method.PUT, object, expected);
	}
	protected void assertPostFails(String url, Object object, OntopiaRestErrors expected) {
		assertRequestFails(url, Method.POST, object, expected);
	}
	protected void assertDeleteFails(String url, OntopiaRestErrors expected) {
		assertRequestFails(url, Method.DELETE, null, expected);
	}
	
	/* -- Utility assertions -- */
	
	protected void assertContainsTopics(Collection<? extends TMObject> topics, String... expected) {
		Set<String> ids = new HashSet<>(Arrays.asList(expected));
		for (TMObject t : topics) {
			ids.remove(t.getObjectId());
		}
		Assert.assertTrue("Missing objects " + ids, ids.isEmpty());
	}
	
	protected void removeById(Collection<? extends TMObject> collection, String id) {
		if (collection == null) { return; }
		Iterator<? extends TMObject> iterator = collection.iterator();
		while (iterator.hasNext()) {
			TMObject tmo = iterator.next();
			if (id.equals(tmo.getObjectId())) {
				iterator.remove();
			}
		}
	}
}
