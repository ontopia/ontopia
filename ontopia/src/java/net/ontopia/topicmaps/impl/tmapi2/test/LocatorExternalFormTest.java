/**
 * 
 */
package net.ontopia.topicmaps.impl.tmapi2.test;

import junit.framework.TestCase;

import org.tmapi.core.Locator;
import org.tmapi.core.Occurrence;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;

/**
 * @author niederhausen
 *
 */
public class LocatorExternalFormTest extends TestCase {

	public void testLocatorExternalForm() throws TMAPIException {
		 String male = "http://www.ex%25-st.com";
		   TopicMapSystemFactory factory = TopicMapSystemFactory.newInstance();
		   TopicMapSystem sys = factory.newTopicMapSystem();
		   TopicMap tm = sys.createTopicMap("foo:baa");
		   Topic t = tm.createTopic();
		   Locator l = tm.createLocator(male);
		   Occurrence o = t.createOccurrence(tm.createTopic(), l, tm.createTopic());
		   try {
		     o.locatorValue().toExternalForm();
		   } catch (NullPointerException e) {
		     fail("NullPointerException occurred");
		   }
	}
}
