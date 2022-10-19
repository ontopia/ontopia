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
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.GenericLocator;
import net.ontopia.topicmaps.utils.NullResolvingExternalReferenceHandler;
import net.ontopia.topicmaps.utils.ltm.LTMTopicMapReference;
import net.ontopia.topicmaps.xml.ExternalReferenceHandlerIF;
import net.ontopia.topicmaps.xml.XTMTopicMapReference;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Test;

public class AbstractURLTopicMapReferenceTest extends AbstractTopicMapReferenceTest {

  // --- utility methods

  public void assertCompliesToAbstractURLTopicMapReference(AbstractURLTopicMapReference ref) throws java.io.IOException {
    // test url
    Assert.assertTrue("URL == null", ref.getURL() != null);

    // test base address
    LocatorIF ba = ref.getBaseAddress();
    LocatorIF nba = new GenericLocator("URI", "file:/tmp/foo");
    ref.setBaseAddress(nba);
    Assert.assertTrue("BaseAddress != NBA", ref.getBaseAddress() == nba);
    ref.setBaseAddress(null);
    Assert.assertTrue("BaseAddress is not null", ref.getBaseAddress() == null);   
    ref.setBaseAddress(ba);
    Assert.assertTrue("BaseAddress != BA", ref.getBaseAddress() == ba);   

    // test duplicate suppression
    boolean ds = ref.getDuplicateSuppression();
    ref.setDuplicateSuppression(true);
    Assert.assertTrue("DuplicateSuppression != true", ref.getDuplicateSuppression());
    ref.setDuplicateSuppression(false);
    Assert.assertTrue("DuplicateSuppression != false", !ref.getDuplicateSuppression());
    ref.setDuplicateSuppression(ds);
    Assert.assertTrue("DuplicateSuppression != DS", ref.getDuplicateSuppression() == ds);

    // reuse store
    boolean rs = ref.getReuseStore();
    ref.setReuseStore(true);
    Assert.assertTrue("ReuseStore != true", ref.getReuseStore());
    ref.setReuseStore(false);
    Assert.assertTrue("ReuseStore != false", !ref.getReuseStore());
    ref.setReuseStore(rs);
    Assert.assertTrue("ReuseStore != RS", ref.getReuseStore() == rs);

    boolean checkOpenAfterClose = true;
    assertCompliesToAbstractTopicMapReference(ref, checkOpenAfterClose);
  }

  // --- Test cases (XTM)

  @Test
  public void testXTMRef() throws java.net.MalformedURLException, java.io.IOException {
    String id = "jill.xtm";
    String title = "XTMTM";
    URL file = TestFileUtils.getTestInputURL("various", id);
    XTMTopicMapReference ref = new XTMTopicMapReference(file, id, title);

    // test validation
    Assert.assertTrue("Validation default is not true", ref.getValidation());   
    ref.setValidation(false);
    Assert.assertTrue("Validation is not false", !ref.getValidation());   
    ref.setValidation(true);
    Assert.assertTrue("Validation is not true", ref.getValidation());   

    // test external reference handler
    ExternalReferenceHandlerIF nerh = new NullResolvingExternalReferenceHandler();
    Assert.assertTrue("ExtRefHandler default is not null", ref.getExternalReferenceHandler() == null);   
    ref.setExternalReferenceHandler(nerh);
    Assert.assertTrue("ExtRefHandler != NERH", ref.getExternalReferenceHandler() == nerh);
    ref.setExternalReferenceHandler(null);
    Assert.assertTrue("ExtRefHandler is not null", ref.getExternalReferenceHandler() == null);   

    // run abstract url topic map reference tests
    assertCompliesToAbstractURLTopicMapReference(ref);
  }

  // --- Test cases (LTM)

  @Test
  public void testLTMRef() throws java.net.MalformedURLException, java.io.IOException {
    String id = "small-test.ltm";
    String title = "LTMTM";
    URL file = TestFileUtils.getTestInputURL("various", id);
    LTMTopicMapReference ref = new LTMTopicMapReference(file, id, title);

    // run abstract url topic map reference tests
    assertCompliesToAbstractURLTopicMapReference(ref);
  }
}
