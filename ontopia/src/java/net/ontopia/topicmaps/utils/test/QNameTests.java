
package net.ontopia.topicmaps.utils.test;

import java.net.MalformedURLException;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.test.*;
import net.ontopia.topicmaps.utils.QNameLookup;
import net.ontopia.topicmaps.utils.QNameRegistry;

public class QNameTests extends AbstractTopicMapTestCase {
  protected TopicMapIF        topicmap; 
  protected TopicIF           topic; 
  protected TopicMapBuilderIF builder;
  protected QNameRegistry     registry; 
  protected QNameLookup       q; 

  public QNameTests(String name) {
    super(name);
  }
    
  public void setUp() {
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    builder = store.getTopicMap().getBuilder();
    topicmap = store.getTopicMap();
    topic = builder.makeTopic();

    registry = new QNameRegistry();
    q = registry.getLookup(topicmap);
  }
 
  // --- Test cases for QNameRegistry

  public void testBadSyntaxEmpty() {
    try {
      registry.resolve("");
      fail("empty string accepted as qname");
    } catch (OntopiaRuntimeException e) {
      // expected
    }
  }

  public void testBadSyntaxIdentifier() {
    try {
      registry.resolve("foo");
      fail("identifier accepted as qname");
    } catch (OntopiaRuntimeException e) {
      // expected
    }
  }

  public void testNoSuchPrefix() {
    try {
      registry.resolve("foo:bar");
      fail("unregistered prefix accepted");
    } catch (OntopiaRuntimeException e) {
      // expected
    }
  }

  public void testSimpleLookup() {
    registry.registerPrefix("tst", "http://psi.example.org/");
    LocatorIF loc = registry.resolve("tst:test");
    assertTrue("qname resolved incorrectly: " + loc,
               loc.getAddress().equals("http://psi.example.org/test"));
  }

  public void testRegisterTwice() {
    registry.registerPrefix("tst", "http://www.example.org/");
    registry.registerPrefix("tst", "http://psi.example.org/");
    LocatorIF loc = registry.resolve("tst:test");
    assertTrue("qname resolved incorrectly: " + loc,
               loc.getAddress().equals("http://psi.example.org/test"));
  }

  // --- Test cases for QNameLookup

  public void testBadSyntaxEmpty2() {
    try {
      q.lookup("");
      fail("empty string accepted as qname");
    } catch (OntopiaRuntimeException e) {
      // expected
    }
  }

  public void testBadSyntaxIdentifier2() {
    try {
      q.lookup("foo");
      fail("identifier accepted as qname");
    } catch (OntopiaRuntimeException e) {
      // expected
    }
  }

  public void testNoSuchPrefix2() {
    try {
      q.lookup("foo:bar");
      fail("unregistered prefix accepted");
    } catch (OntopiaRuntimeException e) {
      // expected
    }
  }

  public void testSimpleLookupFails() {
    registry.registerPrefix("tst", "http://psi.example.org/");
    TopicIF found = q.lookup("tst:test");
    assertTrue("lookup returned topic when none was to be found",
               found == null);
  }

  public void testSimpleLookupSucceeds() throws MalformedURLException {
    URILocator loc = new URILocator("http://psi.example.org/test");
    topic.addSubjectIdentifier(loc);
    
    registry.registerPrefix("tst", "http://psi.example.org/");
    TopicIF found = q.lookup("tst:test");
    assertTrue("lookup returned wrong topic",
               topic == found);
  }

  public void testSimpleLookupFails2() throws MalformedURLException {
    URILocator loc = new URILocator("http://psi.example.org/test");
    topic.addItemIdentifier(loc);
    
    registry.registerPrefix("tst", "http://psi.example.org/");
    TopicIF found = q.lookup("tst:test");
    assertTrue("lookup returned wrong topic: " + found,
               found == null);
  }

  public void testSimpleLookupFails3() throws MalformedURLException {
    URILocator loc = new URILocator("http://psi.example.org/test");
    topic.addSubjectLocator(loc);
    
    registry.registerPrefix("tst", "http://psi.example.org/");
    TopicIF found = q.lookup("tst:test");
    assertTrue("lookup returned wrong topic: " + found,
               found == null);
  }
  
  public void testRegisterTwice2() throws MalformedURLException {
    URILocator loc = new URILocator("http://psi.example.org/test");
    topic.addSubjectIdentifier(loc);
    
    registry.registerPrefix("tst", "http://www.example.org/");
    registry.registerPrefix("tst", "http://psi.example.org/");
    TopicIF found = q.lookup("tst:test");
    assertTrue("qname resolved incorrectly: " + found,
               topic == found);
  }
  
}
