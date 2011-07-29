
package net.ontopia.topicmaps.utils.rdf;

import java.io.File;
import java.io.IOException;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.xml.CanonicalTopicMapWriter;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.TestFileUtils;
import net.ontopia.utils.URIUtils;

import org.junit.Assert;
import org.junit.Test;

public class RDFTopicMapReaderTest {
  
  private final static String testdataDirectory = "rdf";

  // complex tests

  @Test  
  public void testFile() throws IOException {
    String in = TestFileUtils.getTestInputFile(testdataDirectory, "various", "simple-foaf.rdf");
    String map = TestFileUtils.getTestInputFile(testdataDirectory, "various", "simple-foaf-map.rdf");
    File out = TestFileUtils.getTestOutputFile(testdataDirectory, "out", "simple-foaf.rdf");
    String base = TestFileUtils.getTestInputFile(testdataDirectory, "baseline", "simple-foaf.rdf");
    
    RDFTopicMapReader reader = new RDFTopicMapReader(URIUtils.getURI(in));
    reader.setMappingURL(URIUtils.getURI(map).getAddress());
    TopicMapIF tm = reader.read();
    new CanonicalTopicMapWriter(out).write(tm);
      
    // compare results
    Assert.assertTrue("Wrong canonicalization using external mapping file",
               FileUtils.compareFileToResource(out, base));    
  }
    
  @Test  
  public void testBug1317() throws IOException {
    String in = TestFileUtils.getTestInputFile(testdataDirectory, "various", "simple-foaf.rdf");
    String map = TestFileUtils.getTestInputFile(testdataDirectory, "various", "simple-foaf-map-2.rdf");
    File out = TestFileUtils.getTestOutputFile(testdataDirectory, "out", "simple-foaf-2.rdf");
    String base = TestFileUtils.getTestInputFile(testdataDirectory, "baseline", "simple-foaf.rdf");
    
    RDFTopicMapReader reader = new RDFTopicMapReader(URIUtils.getURI(in));
    reader.setMappingURL(URIUtils.getURI(map).getAddress());
    TopicMapIF tm = reader.read();
    new CanonicalTopicMapWriter(out).write(tm);
      
    // compare results
    Assert.assertTrue("Wrong canonicalization using external mapping file",
               FileUtils.compareFileToResource(out, base));    
  }

  @Test  
  public void testBug1339() throws IOException {
    // need to use ARP for this test to be effective
    
    String in = TestFileUtils.getTestInputFile(testdataDirectory, "various", "bug1339.rdf");
    String map = TestFileUtils.getTestInputFile(testdataDirectory, "various", "bug1339-map.rdf");
    File out = TestFileUtils.getTestOutputFile(testdataDirectory, "out",  "bug1339.rdf");
    String base = TestFileUtils.getTestInputFile(testdataDirectory, "baseline", "bug1339.rdf");
    
    RDFTopicMapReader reader = new RDFTopicMapReader(URIUtils.getURI(in));
    reader.setMappingURL(URIUtils.getURI(map).getAddress());
    TopicMapIF tm = reader.read();
    new CanonicalTopicMapWriter(out).write(tm);
      
    // compare results
    Assert.assertTrue("Wrong canonicalization using external mapping file",
               FileUtils.compareFileToResource(out, base));    
  }  
    
  @Test  
  public void testNullGeneratedName() throws IOException {
    String in = TestFileUtils.getTestInputFile(testdataDirectory, "various", "null-generated-name.rdf");
    String map = TestFileUtils.getTestInputFile(testdataDirectory, "various", "simple-foaf-map-2.rdf");
    File out = TestFileUtils.getTestOutputFile(testdataDirectory, "out", "null-generated-name.rdf");
    String base = TestFileUtils.getTestInputFile(testdataDirectory, "baseline", "null-generated-name.rdf");
    
    RDFTopicMapReader reader = new RDFTopicMapReader(URIUtils.getURI(in));
    reader.setMappingURL(URIUtils.getURI(map).getAddress());
    reader.setGenerateNames(true);
    TopicMapIF tm = reader.read();
    new CanonicalTopicMapWriter(out).write(tm);
      
    // compare results
    Assert.assertTrue("Wrong canonicalization using external mapping file",
               FileUtils.compareFileToResource(out, base));    
  }
}
