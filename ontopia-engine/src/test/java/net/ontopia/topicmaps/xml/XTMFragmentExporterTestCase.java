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
public class XTMFragmentExporterTestCase {

  private final static String testdataDirectory = "xtmFragmentExporter";

  private String base;
  private String filename;

  @Parameters
  public static List generateTests() {
    return TestFileUtils.getTestInputFiles(testdataDirectory, "in", ".ltm|.rdf|.xtm");
  }

  public XTMFragmentExporterTestCase(String root, String filename) {
    this.filename = filename;
    this.base = TestFileUtils.getTestdataOutputDirectory() + testdataDirectory;
  }

  @Test
  public void testFile() throws IOException {
    TestFileUtils.verifyDirectory(base, "xtm");
    TestFileUtils.verifyDirectory(base, "ltm");
    TestFileUtils.verifyDirectory(base, "out");

    // Path to the input topic map document.
    String in = TestFileUtils.getTestInputFile(testdataDirectory, "in", filename);
    // Path to the baseline (canonicalized output of the source topic map).
    String baseline = TestFileUtils.getTestInputFile(testdataDirectory, "baseline",
            filename + ".xtm.cxtm");
    // Path to the exported ltm topic map document.
    String xtm = base + File.separator + "xtm" + File.separator + filename
            + ".xtm";
    File ltm = new File(base + File.separator + "ltm" + File.separator + filename
            + ".ltm");
    // Path to the output (canonicalized output of exported ltm topic map).
    File out = new File(base + File.separator + "out" + File.separator + filename
            + ".xtm.cxtm");

    TopicMapIF sourceMap;
    sourceMap = ImportExportUtils.getReader(in).read();
    Collection topics = sourceMap.getTopics();

    OutputStream outputStream = new FileOutputStream(xtm);
    XTMFragmentExporter exporter = new XTMFragmentExporter();
    exporter.setExportSourceLocators(false);

    Predicate filter = new TestDecider();
    PrettyPrinter printer = new PrettyPrinter(outputStream);
    exporter.setFilter(filter);
    try {
      exporter.exportAll(topics.iterator(), printer);
    } catch (SAXException e) {
      throw new OntopiaRuntimeException(e);
    }

    // Read the exported topic map for canonicalization
    XTMTopicMapReader reader = (XTMTopicMapReader) ImportExportUtils.getReader(xtm);
    reader.setFollowTopicRefs(false);
    TopicMapIF exportedMap = ImportExportUtils.getReader(xtm).read();

    // Export the topic map to ltm.
    // This line is for use when developing tests, and should be commented
    // out whenever submitted to CVS.
    new LTMTopicMapWriter(ltm).write(exportedMap);

    new CanonicalXTMWriter(out).write(exportedMap);

    // compare results
    Assert.assertTrue("the canonicalized xtm fragment export of " + filename
            + " Does not match the baseline.",
            TestFileUtils.compareFileToResource(out, baseline));

  }

  public class TestDecider implements Predicate {

    @Override
    public boolean test(Object object) {
      if (object instanceof TopicIF) {
        TopicIF topic = (TopicIF) object;
        Iterator baseNamesIt = topic.getTopicNames().iterator();
        while (baseNamesIt.hasNext()) {
          TopicNameIF name = (TopicNameIF) baseNamesIt.next();
          if (name.getValue().startsWith(("fTopic"))) {
            return false;
          }
        }
        return true;
      } else if (object instanceof AssociationIF) {
        AssociationIF assoc = (AssociationIF) object;
        TopicIF topic = assoc.getType();
        Iterator baseNamesIt = topic.getTopicNames().iterator();
        while (baseNamesIt.hasNext()) {
          TopicNameIF name = (TopicNameIF) baseNamesIt.next();
          if (name.getValue().startsWith(("fAssocs"))) {
            return false;
          }
        }
        return true;
      } else if (object instanceof OccurrenceIF) {
        OccurrenceIF occ = (OccurrenceIF) object;
        TopicIF topic = occ.getType();
        Iterator baseNamesIt = topic.getTopicNames().iterator();
        while (baseNamesIt.hasNext()) {
          TopicNameIF name = (TopicNameIF) baseNamesIt.next();
          if (name.getValue().startsWith(("fOccs"))) {
            return false;
          }
        }
        return true;
      } else if (object instanceof TopicNameIF) {
        TopicNameIF name = (TopicNameIF) object;
        return !name.getValue().startsWith("fName");
      } else {
        return true;
      }
    }
  }
}
