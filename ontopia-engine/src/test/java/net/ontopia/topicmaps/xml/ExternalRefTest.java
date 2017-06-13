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

package net.ontopia.topicmaps.xml;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the ExternalReferenceHandlerIF interface by counting
 * calls made to that interface and testing them against an
 * expected count for a given test file.
 * Note that the testing is currently split into two test cases.
 * This is because the testExternalTMRefs fails and if everything is
 * in one function which is renamed to _testFoo(), the framework fails as
 * there are no tests it can access.
 */
public class ExternalRefTest
{

  private final static String testdataDirectory = "various";

  protected CountingRefHandler readTestFile(String fileName) {
    CountingRefHandler extRefHandler = new CountingRefHandler();
    try {

      String testfile = TestFileUtils.getTestInputFile(testdataDirectory, fileName);
      XTMTopicMapReader reader = new XTMTopicMapReader(TestFileUtils.getTestInputURL(testfile));
      reader.setExternalReferenceHandler(extRefHandler);
      reader.read();
    } catch (MalformedURLException ex) {
      Assert.fail("MalformedURLException initialising base address of test file.");
    } catch (IOException ex) {
      Assert.fail("IOException parsing test file." + ex.toString());
    }

    return extRefHandler;
  }

  @Test
  public void testExternalRefs() {
    int expectTopics = 7;
    CountingRefHandler extRefHandler = readTestFile("external-ref.xtm");
    Assert.assertTrue("Not all external topics reported. Expected " + String.valueOf(expectTopics) + ", got " 
               + extRefHandler.getTopicRefs().size(),
               extRefHandler.getTopicRefs().size() == expectTopics);
  }

  @Test
  public void testExternalTMRefs() {
    int expectMaps   = 1;
    CountingRefHandler extRefHandler = readTestFile("external-tm.xtm");
    Assert.assertTrue("Not all external topic maps reported. Expected: " + String.valueOf(expectMaps) + ", got "
               + extRefHandler.getTMRefs().size(),
               extRefHandler.getTMRefs().size() == expectMaps);
  }
  
}

class CountingRefHandler implements ExternalReferenceHandlerIF
{
  protected ArrayList tmrefs;
  protected ArrayList topicrefs;

  public CountingRefHandler() {
    tmrefs = new ArrayList();
    topicrefs = new ArrayList();
  }

  public Collection getTMRefs() { return tmrefs; }
  public Collection getTopicRefs() { return topicrefs; }

  public LocatorIF externalTopicMap(LocatorIF address) {
    tmrefs.add(address);
    return null;
  }

  public LocatorIF externalTopic(LocatorIF address) {
    topicrefs.add(address);
    return null;
  }

}
