/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
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

package net.ontopia.topicmaps.entry;

import java.net.URL;
import net.ontopia.topicmaps.utils.NullResolvingExternalReferenceHandler;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.GenericLocator;
import net.ontopia.topicmaps.utils.ltm.LTMTopicMapReference;
import net.ontopia.topicmaps.utils.rdf.RDFTopicMapReference;
import net.ontopia.topicmaps.xml.ExternalReferenceHandlerIF;
import net.ontopia.topicmaps.xml.XTMTopicMapReference;
import net.ontopia.utils.TestFileUtils;
import net.ontopia.utils.URIUtils;

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
    String file = TestFileUtils.getTestInputFile("various", id);
    XTMTopicMapReference ref = new XTMTopicMapReference(new URL(URIUtils.getURI(file).getAddress()), id, title);

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
    String file = TestFileUtils.getTestInputFile("various", id);
    LTMTopicMapReference ref = new LTMTopicMapReference(new URL(URIUtils.getURI(file).getAddress()), id, title);

    // run abstract url topic map reference tests
    doAbstractURLTopicMapReferenceTests(ref);
  }

  // --- Test cases (RDF)

  public void testRDFRef() throws java.net.MalformedURLException, java.io.IOException {
    String id = "instance-of.rdf";
    String title = "RDFTM";
    String file = TestFileUtils.getTestInputFile("rdf", "in", id);
    RDFTopicMapReference ref = new RDFTopicMapReference(new URL(URIUtils.getURI(file).getAddress()), id, title);
    
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
