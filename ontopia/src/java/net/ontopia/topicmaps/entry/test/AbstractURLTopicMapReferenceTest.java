
// $Id: AbstractURLTopicMapReferenceTest.java,v 1.2 2008/01/09 10:07:29 geir.gronmo Exp $

package net.ontopia.topicmaps.entry.test;

import junit.framework.*;

import java.io.File;
import java.net.URL;
import java.util.*;
import net.ontopia.test.*;
import net.ontopia.topicmaps.entry.*;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.topicmaps.utils.NullResolvingExternalReferenceHandler;
import net.ontopia.topicmaps.utils.ltm.*;
import net.ontopia.topicmaps.utils.rdf.*;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.GenericLocator;

public class AbstractURLTopicMapReferenceTest extends AbstractTopicMapReferenceTest {

  public AbstractURLTopicMapReferenceTest(String name) {
    super(name);
  }

  // --- utility methods

  public void doAbstractURLTopicMapReferenceTests(AbstractURLTopicMapReference ref) throws java.io.IOException {
    // test url
    assertTrue("URL == null", ref.getURL() != null);

    // test base address
    LocatorIF ba = ref.getBaseAddress();
    LocatorIF nba = new GenericLocator("URI", "file:/tmp/foo");
    ref.setBaseAddress(nba);
    assertTrue("BaseAddress != NBA", ref.getBaseAddress() == nba);
    ref.setBaseAddress(null);
    assertTrue("BaseAddress is not null", ref.getBaseAddress() == null);   
    ref.setBaseAddress(ba);
    assertTrue("BaseAddress != BA", ref.getBaseAddress() == ba);   

    // test duplicate suppression
    boolean ds = ref.getDuplicateSuppression();
    ref.setDuplicateSuppression(true);
    assertTrue("DuplicateSuppression != true", ref.getDuplicateSuppression());
    ref.setDuplicateSuppression(false);
    assertTrue("DuplicateSuppression != false", !ref.getDuplicateSuppression());
    ref.setDuplicateSuppression(ds);
    assertTrue("DuplicateSuppression != DS", ref.getDuplicateSuppression() == ds);

    // reuse store
    boolean rs = ref.getReuseStore();
    ref.setReuseStore(true);
    assertTrue("ReuseStore != true", ref.getReuseStore());
    ref.setReuseStore(false);
    assertTrue("ReuseStore != false", !ref.getReuseStore());
    ref.setReuseStore(rs);
    assertTrue("ReuseStore != RS", ref.getReuseStore() == rs);

    boolean checkOpenAfterClose = true;
    doAbstractTopicMapReferenceTests(ref, checkOpenAfterClose);
  }

  // --- Test cases (XTM)

  public void testXTMRef() throws java.net.MalformedURLException, java.io.IOException {
    String id = "jill.xtm";
    String title = "XTMTM";
    File file = new File(getTestDirectory() + File.separator + "various" + File.separator + id);

    XTMTopicMapReference ref = new XTMTopicMapReference(file.toURL(), id, title);

    // test validation
    assertTrue("Validation default is not true", ref.getValidation());   
    ref.setValidation(false);
    assertTrue("Validation is not false", !ref.getValidation());   
    ref.setValidation(true);
    assertTrue("Validation is not true", ref.getValidation());   

    // test external reference handler
    ExternalReferenceHandlerIF nerh = new NullResolvingExternalReferenceHandler();
    assertTrue("ExtRefHandler default is not null", ref.getExternalReferenceHandler() == null);   
    ref.setExternalReferenceHandler(nerh);
    assertTrue("ExtRefHandler != NERH", ref.getExternalReferenceHandler() == nerh);
    ref.setExternalReferenceHandler(null);
    assertTrue("ExtRefHandler is not null", ref.getExternalReferenceHandler() == null);   

    // run abstract url topic map reference tests
    doAbstractURLTopicMapReferenceTests(ref);
  }

  // --- Test cases (LTM)

  public void testLTMRef() throws java.net.MalformedURLException, java.io.IOException {
    String id = "small-test.ltm";
    String title = "LTMTM";
    File file = new File(getTestDirectory() + File.separator + "various" + File.separator + id);

    LTMTopicMapReference ref = new LTMTopicMapReference(file.toURL(), id, title);

    // run abstract url topic map reference tests
    doAbstractURLTopicMapReferenceTests(ref);
  }

  // --- Test cases (RDF)

  public void testRDFRef() throws java.net.MalformedURLException, java.io.IOException {
    String id = "instance-of.rdf";
    String title = "RDFTM";
    File file = new File(getTestDirectory() + File.separator + "rdf" + File.separator + "in" + File.separator + id);

    RDFTopicMapReference ref = new RDFTopicMapReference(file.toURL(), id, title);
    
    // test mapping file
    String mf = ref.getMappingFile();
    assertTrue("Mappingfile default is set", ref.getMappingFile() == null);
    ref.setMappingFile("foo");
    assertTrue("Mappingfile not equals 'foo'", "foo".equals(ref.getMappingFile()));
    ref.setMappingFile(mf);
    assertTrue("Mappingfile != " + mf, mf == ref.getMappingFile());
    
    // test syntax
    String sx = ref.getSyntax();
    assertTrue("Syntax default is set", ref.getSyntax() == null);
    ref.setSyntax("foo");
    assertTrue("Syntax not equals 'foo'", "foo".equals(ref.getSyntax()));
    ref.setSyntax(sx);
    assertTrue("Syntax != " + sx, sx == ref.getSyntax());

    // test generate names
    boolean gg = ref.getGenerateNames();
    assertTrue("GenerateNames default is not false", !gg);   
    ref.setGenerateNames(true);
    assertTrue("GenerateNames is not true", ref.getGenerateNames());   
    ref.setGenerateNames(gg);
    assertTrue("GenerateNames is not " + gg, gg == ref.getGenerateNames());

    // run abstract url topic map reference tests
    doAbstractURLTopicMapReferenceTests(ref);

  }
  
}
