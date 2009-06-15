
// $Id: URLTopicMapSourceTest.java,v 1.5 2008/01/09 10:07:29 geir.gronmo Exp $

package net.ontopia.topicmaps.entry.test;

import junit.framework.*;

import java.util.*;
import net.ontopia.test.*;
import net.ontopia.topicmaps.entry.*;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.topicmaps.utils.ltm.*;
import net.ontopia.topicmaps.utils.rdf.*;

public class URLTopicMapSourceTest extends AbstractTopicMapSourceTest {

  public URLTopicMapSourceTest(String name) {
    super(name);
  }

  // --- Test cases (XTM)

  public void testXTM() {
    URLTopicMapSource source = new URLTopicMapSource("file:/tmp/foobar.xtm");
    source.setId("fooid");
    source.setTitle("footitle");
    source.setSyntax("XTM");

    // run abstract topic map source tests
    doAbstractTopicMapSourceTests(source);
  }

  public void testXTM1() {
    URLTopicMapSource source = new URLTopicMapSource("file:/tmp/foobar.xtm");
    source.setId("fooid");
    source.setTitle("footitle");
    source.setSyntax("XTM");
    verifyXTMSource(source);
  }

  public void testXTM2() {
    URLTopicMapSource source = new URLTopicMapSource();
    source.setUrl("file:/tmp/foobar.xtm");
    source.setId("fooid");
    source.setTitle("footitle");
    verifyXTMSource(source);
  }

  protected void verifyXTMSource(URLTopicMapSource source) {
    Collection refs = source.getReferences();
    assertTrue("URLTopicMapSource.getReferences().size() != 1", refs.size() == 1);
    TopicMapReferenceIF ref = (TopicMapReferenceIF)refs.iterator().next();
    assertTrue("!TopicMapReference.getId().equals('foobar')", "fooid".equals(ref.getId()));
    assertTrue("!TopicMapReference.getTitle().equals('foobar')", "footitle".equals(ref.getTitle()));    
    assertTrue("!(TopicMapReferenceIF instanceof XTMTopicMapReference)", ref instanceof XTMTopicMapReference);
  }

  // --- Test cases (LTM)

  public void testLTM1() {
    URLTopicMapSource source = new URLTopicMapSource("file:/tmp/foobar.ltm");
    source.setId("fooid");
    source.setTitle("footitle");
    source.setSyntax("LTM");
    verifyLTMSource(source);
  }

  public void testLTM2() {
    URLTopicMapSource source = new URLTopicMapSource();
    source.setUrl("file:/tmp/foobar.ltm");
    source.setId("fooid");
    source.setTitle("footitle");
    verifyLTMSource(source);
  }

  protected void verifyLTMSource(URLTopicMapSource source) {
    Collection refs = source.getReferences();
    assertTrue("URLTopicMapSource.getReferences().size() != 1", refs.size() == 1);
    TopicMapReferenceIF ref = (TopicMapReferenceIF)refs.iterator().next();
    assertTrue("!TopicMapReference.getId().equals('foobar')", "fooid".equals(ref.getId()));
    assertTrue("!TopicMapReference.getTitle().equals('foobar')", "footitle".equals(ref.getTitle()));    
    assertTrue("!(TopicMapReferenceIF instanceof LTMTopicMapReference)", ref instanceof LTMTopicMapReference);
  }

  // --- Test cases (RDF)

  public void testRDF1() {
    URLTopicMapSource source = new URLTopicMapSource("file:/tmp/foobar.rdf");
    source.setId("fooid");
    source.setTitle("footitle");
    source.setSyntax("RDF");
    verifyRDFSource(source);
  }

  public void testRDF2() {
    URLTopicMapSource source = new URLTopicMapSource();
    source.setUrl("file:/tmp/foobar.rdf");
    source.setId("fooid");
    source.setTitle("footitle");
    verifyRDFSource(source);
  }

  protected void verifyRDFSource(URLTopicMapSource source) {
    Collection refs = source.getReferences();
    assertTrue("URLTopicMapSource.getReferences().size() != 1", refs.size() == 1);
    TopicMapReferenceIF ref = (TopicMapReferenceIF)refs.iterator().next();
    assertTrue("!TopicMapReference.getId().equals('foobar')", "fooid".equals(ref.getId()));
    assertTrue("!TopicMapReference.getTitle().equals('foobar')", "footitle".equals(ref.getTitle()));    
    assertTrue("!(TopicMapReferenceIF instanceof RDFTopicMapReference)", ref instanceof RDFTopicMapReference);
  }
  
}
