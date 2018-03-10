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

package net.ontopia.topicmaps.utils.rdf;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.xml.CanonicalTopicMapWriter;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Test;

public class RDFTopicMapReaderTest {
  
  private final static String testdataDirectory = "rdf";

  // complex tests

  @Test  
  public void testFile() throws IOException {
    String in = TestFileUtils.getTestInputFile(testdataDirectory, "various", "simple-foaf.rdf");
    URL map = TestFileUtils.getTestInputURL(testdataDirectory, "various", "simple-foaf-map.rdf");
    File out = TestFileUtils.getTestOutputFile(testdataDirectory, "out", "simple-foaf.rdf");
    String base = TestFileUtils.getTestInputFile(testdataDirectory, "baseline", "simple-foaf.rdf");
    
    RDFTopicMapReader reader = new RDFTopicMapReader(TestFileUtils.getTestInputURL(in));
    reader.setMappingURL(map);
    TopicMapIF tm = reader.read();
    new CanonicalTopicMapWriter(out).write(tm);
      
    // compare results
    Assert.assertTrue("Wrong canonicalization using external mapping file",
               TestFileUtils.compareFileToResource(out, base));    
  }
    
  @Test  
  public void testInputStream() throws IOException {
    String in = TestFileUtils.getTestInputFile(testdataDirectory, "various", "simple-foaf.rdf");
    URL map = TestFileUtils.getTestInputURL(testdataDirectory, "various", "simple-foaf-map.rdf");
    File out = TestFileUtils.getTestOutputFile(testdataDirectory, "out", "simple-foaf.rdf");
    String base = TestFileUtils.getTestInputFile(testdataDirectory, "baseline", "simple-foaf.rdf");
    
    RDFTopicMapReader reader = new RDFTopicMapReader(TestFileUtils.getTestInputURL(in).openStream(), null);
    reader.setMappingURL(map);
    TopicMapIF tm = reader.read();
    new CanonicalTopicMapWriter(out).write(tm);
      
    // compare results
    Assert.assertTrue("Wrong canonicalization using external mapping file",
               TestFileUtils.compareFileToResource(out, base));    
  }
    
  @Test  
  public void testBug1317() throws IOException {
    String in = TestFileUtils.getTestInputFile(testdataDirectory, "various", "simple-foaf.rdf");
    URL map = TestFileUtils.getTestInputURL(testdataDirectory, "various", "simple-foaf-map-2.rdf");
    File out = TestFileUtils.getTestOutputFile(testdataDirectory, "out", "simple-foaf-2.rdf");
    String base = TestFileUtils.getTestInputFile(testdataDirectory, "baseline", "simple-foaf.rdf");
    
    RDFTopicMapReader reader = new RDFTopicMapReader(TestFileUtils.getTestInputURL(in));
    reader.setMappingURL(map);
    TopicMapIF tm = reader.read();
    new CanonicalTopicMapWriter(out).write(tm);
      
    // compare results
    Assert.assertTrue("Wrong canonicalization using external mapping file",
               TestFileUtils.compareFileToResource(out, base));    
  }

  @Test  
  public void testBug1339() throws IOException {
    // need to use ARP for this test to be effective
    
    String in = TestFileUtils.getTestInputFile(testdataDirectory, "various", "bug1339.rdf");
    URL map = TestFileUtils.getTestInputURL(testdataDirectory, "various", "bug1339-map.rdf");
    File out = TestFileUtils.getTestOutputFile(testdataDirectory, "out",  "bug1339.rdf");
    String base = TestFileUtils.getTestInputFile(testdataDirectory, "baseline", "bug1339.rdf");
    
    RDFTopicMapReader reader = new RDFTopicMapReader(TestFileUtils.getTestInputURL(in));
    reader.setMappingURL(map);
    TopicMapIF tm = reader.read();
    new CanonicalTopicMapWriter(out).write(tm);
      
    // compare results
    Assert.assertTrue("Wrong canonicalization using external mapping file",
               TestFileUtils.compareFileToResource(out, base));    
  }  
    
  @Test  
  public void testNullGeneratedName() throws IOException {
    String in = TestFileUtils.getTestInputFile(testdataDirectory, "various", "null-generated-name.rdf");
    URL map = TestFileUtils.getTestInputURL(testdataDirectory, "various", "simple-foaf-map-2.rdf");
    File out = TestFileUtils.getTestOutputFile(testdataDirectory, "out", "null-generated-name.rdf");
    String base = TestFileUtils.getTestInputFile(testdataDirectory, "baseline", "null-generated-name.rdf");
    
    RDFTopicMapReader reader = new RDFTopicMapReader(TestFileUtils.getTestInputURL(in));
    reader.setMappingURL(map);
    reader.setGenerateNames(true);
    TopicMapIF tm = reader.read();
    new CanonicalTopicMapWriter(out).write(tm);
      
    // compare results
    Assert.assertTrue("Wrong canonicalization using external mapping file",
               TestFileUtils.compareFileToResource(out, base));    
  }

  @Test  
  public void testIssue366() throws IOException {
    String in = TestFileUtils.getTestInputFile(testdataDirectory, "various", "issue366.rdf");
    URL map = TestFileUtils.getTestInputURL(testdataDirectory, "various", "issue366-map.rdf");
    RDFTopicMapReader reader = new RDFTopicMapReader(TestFileUtils.getTestInputURL(in));
    reader.setMappingURL(map);
    TopicMapIF tm = reader.read();
    Assert.assertNotNull(tm.getTopicBySubjectIdentifier(
            URILocator.create("http://www.infocom.co.jp/dsp/sample#MAIN-\u30BF\u30A4\u30C8\u30EB\u8AAD\u307F")));
  }
}
