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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.topicmaps.utils.ltm.LTMTopicMapWriter;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.TestFileUtils;
import net.ontopia.xml.PrettyPrinter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.xml.sax.SAXException;

@RunWith(Parameterized.class)
public class TMExporterFilterTestCase {

  // Set to true only when developing test cases, in which case it is easier to
  // manually inspect LTM than CXTM.
  public static final boolean ENABLE_LTM = true;

  private final static String testdataDirectory = "filter-tests";
  
  /**
   * @return The test cases generated by this.
   */
  @Parameters
  public static List generateTests() {
    return TestFileUtils.getTestInputFiles(testdataDirectory, "in", ".ltm|.rdf|.xtm");
  }

  // --- Test case classes

    private String base;
    private String filename;

    public TMExporterFilterTestCase(String root, String filename) {
      this.filename = filename;
      this.base = TestFileUtils.getTestdataOutputDirectory() + testdataDirectory;
    }

    @Test
    public void testXTMFragmentFile() throws IOException {
      String xtmFragBase = base + File.separator  + "xtm-frag";
      
      TestFileUtils.verifyDirectory(xtmFragBase, "out");
      TestFileUtils.verifyDirectory(xtmFragBase, "xtm");

      // Path to the input topic map document.
      String in = TestFileUtils.getTestInputFile(testdataDirectory, "in", filename);

      // Path to the baseline (canonicalized output of the source topic map).
      String baseline = TestFileUtils.getTestInputFile(testdataDirectory, "xtm-frag/baseline", 
        filename + ".cxtm");

      // Path to the exported xtm topic map document.
      String xtm = xtmFragBase + File.separator + "xtm" + File.separator +
          filename + ".xtm";

      // Path to the output (canonicalized output of exported xtm topic map).
      File out = new File(xtmFragBase + File.separator + "out" + File.separator +
          filename + ".cxtm");
      
      TopicMapIF sourceMap;
      sourceMap = ImportExportUtils.getReader(in).read();
      Collection topics = sourceMap.getTopics();

      OutputStream outputStream = new FileOutputStream(xtm);
      PrettyPrinter prettyPrinter = new PrettyPrinter(outputStream);
      XTMFragmentExporter exporter = new XTMFragmentExporter();
      exporter.setExportSourceLocators(false);
      exporter.setAddIds(false);
      
      Predicate filter = new TestDecider();
      exporter.setFilter(filter);
      try {
        exporter.exportAll(topics.iterator(), prettyPrinter);
      } catch (SAXException e) {
        throw new OntopiaRuntimeException(e);
      }

      // Read the exported topic map for canonicalization
      
      XTMTopicMapReader reader = (XTMTopicMapReader)ImportExportUtils.getReader(xtm);
      reader.setFollowTopicRefs(false);
      TopicMapIF exportedMap = ImportExportUtils.getReader(xtm).read();

      if (ENABLE_LTM) {
        TestFileUtils.verifyDirectory(xtmFragBase, "ltm");
        // Path to the ltm (only used when making test cases).
        File ltm = new File(xtmFragBase + File.separator + "ltm" + File.separator + filename
            + ".ltm");
        
        // Export the topic map to ltm.
        // This line is for use when developing tests, and should be commented
        // out whenever submitted to CVS.
        new LTMTopicMapWriter(ltm).write(exportedMap);
      }

      new CanonicalXTMWriter(out).write(exportedMap);

      // compare results
      Assert.assertTrue("the canonicalized xtm fragment export of " + filename
          + " does not match the baseline.", TestFileUtils.compareFileToResource(out, baseline));

    }

    @Test
    public void testTMXMLFile() throws IOException {
      String tmxmlBase = base + File.separator  + "tmxml";
      
      TestFileUtils.verifyDirectory(tmxmlBase, "out");
      TestFileUtils.verifyDirectory(tmxmlBase, "tmxml");

      // Path to the input topic map document.
      String in = TestFileUtils.getTestInputFile(testdataDirectory, "in", filename);

      // Path to the baseline (canonicalized output of the source topic map).
      String baseline = TestFileUtils.getTestInputFile(testdataDirectory, "tmxml/baseline", 
        filename + ".cxtm");

      // Path to the exported xtm topic map document.
      File tmxml = new File(tmxmlBase + File.separator + "tmxml" + File.separator +
          filename + ".xml");

      // Path to the output (canonicalized output of exported xtm topic map).
      File out = new File(tmxmlBase + File.separator + "out" + File.separator +
          filename + ".cxtm");
      
      TopicMapIF sourceMap = ImportExportUtils.getReader(in).read();

      TMXMLWriter exporter = new TMXMLWriter(tmxml);
      
      Predicate filter = new TestDecider();
      exporter.setFilter(filter);
      exporter.write(sourceMap);
      exporter.close();
      
      // Read the exported topic map for canonicalization

      TopicMapIF exportedMap = ImportExportUtils.getReader(tmxml).read();

      if (ENABLE_LTM) {
        TestFileUtils.verifyDirectory(tmxmlBase, "ltm");
        // Path to the ltm (only used when making test cases).
        File ltm = new File(tmxmlBase + File.separator + "ltm" + File.separator + filename
            + ".ltm");
        
        // Export the topic map to ltm.
        // This line is for use when developing tests, and should be commented
        // out whenever submitted to CVS.
        new LTMTopicMapWriter(ltm).write(exportedMap);
      }

      new CanonicalXTMWriter(out).write(exportedMap);

      // compare results
      Assert.assertTrue("the canonicalized tmxml export of " + filename
          + " does not match the baseline.", TestFileUtils.compareFileToResource(out,baseline));

    }

    @Test
    public void testXTMFile() throws IOException {
      String xtmBase = base + File.separator  + "xtm";
      
      TestFileUtils.verifyDirectory(xtmBase, "out");
      TestFileUtils.verifyDirectory(xtmBase, "xtm");

      // Path to the input topic map document.
      String in = TestFileUtils.getTestInputFile(testdataDirectory, "in", filename);

      // Path to the baseline (canonicalized output of the source topic map).
      String baseline = TestFileUtils.getTestInputFile(testdataDirectory, "xtm/baseline", filename + ".cxtm");

      // Path to the exported xtm topic map document.
      File xtm = new File(xtmBase + File.separator + "xtm" + File.separator +
          filename + ".xml");

      // Path to the output (canonicalized output of exported xtm topic map).
      File out = new File(xtmBase + File.separator + "out" + File.separator +
          filename + ".cxtm");
      
      TopicMapIF sourceMap = ImportExportUtils.getReader(in).read();

      XTMTopicMapWriter exporter = new XTMTopicMapWriter(xtm);
      
      Predicate filter = new TestDecider();
      exporter.setFilter(filter);
      exporter.write(sourceMap);

      // Read the exported topic map for canonicalization

      XTMTopicMapReader reader = new XTMTopicMapReader(xtm);
      TopicMapIF exportedMap = reader.read();

      if (ENABLE_LTM) {
        TestFileUtils.verifyDirectory(xtmBase, "ltm");
        // Path to the ltm (only used when making test cases).
        File ltm = new File(xtmBase + File.separator + "ltm" + File.separator + 
            filename + ".ltm");
        
        // Export the topic map to ltm.
        // This line is for use when developing tests, and should be commented
        // out whenever submitted to CVS.
        new LTMTopicMapWriter(ltm).write(exportedMap);
      }

      new CanonicalXTMWriter(out).write(exportedMap);

      // compare results
      Assert.assertTrue("the canonicalized xtm export of " + filename
          + " does not match the baseline.", TestFileUtils.compareFileToResource(out, baseline));
    }

    // Need to work on the LTM test cases to make them always produce the same output.
    // @Test
    public void testLTMFile() throws IOException {
      String ltmBase = base + File.separator  + "ltm";
      
      TestFileUtils.verifyDirectory(ltmBase, "out");
      TestFileUtils.verifyDirectory(ltmBase, "ltm");

      // Path to the input topic map document.
      String in = TestFileUtils.getTestInputFile(testdataDirectory, "in", filename);

      // Path to the ltm (only used when making test cases).
      File ltm = new File(ltmBase + File.separator + "ltm" + File.separator + filename
          + ".ltm");
      
      // Path to the baseline (canonicalized output of the source topic map).
      String baseline = TestFileUtils.getTestInputFile(testdataDirectory, "ltm/baseline", 
        filename + ".xtm.cxtm");

      // Path to the output (canonicalized output of exported xtm topic map).
      File out = new File(ltmBase + File.separator + "out" + File.separator +
          filename + ".cxtm");
      
      TopicMapIF sourceMap = ImportExportUtils.getReader(in).read();

      LTMTopicMapWriter exporter = new LTMTopicMapWriter(ltm);
      
      Predicate filter = new TestDecider();
      exporter.setFilter(filter);
      exporter.write(sourceMap);

      // Read the exported topic map for canonicalization
      
      TopicMapIF exportedMap = ImportExportUtils.getReader(ltm).read();

      new CanonicalXTMWriter(out).write(exportedMap);

      // compare results
      Assert.assertTrue("the canonicalized ltm export of " + filename
          + " does not match the baseline.", TestFileUtils.compareFileToResource(out, baseline));

    }

  public class TestDecider implements Predicate {
    @Override
    public boolean test(Object object) {
      if (object instanceof TopicIF) {
        TopicIF topic = (TopicIF)object;
        Iterator baseNamesIt = topic.getTopicNames().iterator();
        while (baseNamesIt.hasNext()) {
          TopicNameIF name = (TopicNameIF)baseNamesIt.next();
          if (name.getValue().startsWith(("fTopic"))) {
            return false;
          }
        }
        return true;
      } else if (object instanceof AssociationIF) {
        AssociationIF assoc = (AssociationIF)object;
        TopicIF topic = assoc.getType();
        Iterator baseNamesIt = topic.getTopicNames().iterator();
        while (baseNamesIt.hasNext()) {
          TopicNameIF name = (TopicNameIF)baseNamesIt.next();
          if (name.getValue().startsWith(("fAssocs"))) {
            return false;
          }
        }
        return true;
      } else if (object instanceof OccurrenceIF) {
        OccurrenceIF occ = (OccurrenceIF)object;
        TopicIF topic = occ.getType();
        Iterator baseNamesIt = topic.getTopicNames().iterator();
        while (baseNamesIt.hasNext()) {
          TopicNameIF name = (TopicNameIF)baseNamesIt.next();
          if (name.getValue().startsWith(("fOccs"))) {
            return false;
          }
        }
        return true;
      } else if (object instanceof TopicNameIF) {
        TopicNameIF name = (TopicNameIF)object;
        if (name.getValue().startsWith(("fName"))) {
          return false;
        }
        return true;
      } else {
        return true;
      }
    }
    
  }
}
