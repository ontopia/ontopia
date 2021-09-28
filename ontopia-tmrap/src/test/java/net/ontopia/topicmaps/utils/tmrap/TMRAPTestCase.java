/*
 * #!
 * Ontopia TMRAP
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

package net.ontopia.topicmaps.utils.tmrap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletException;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URIFragmentLocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.entry.TopicMapRepositoryIF;
import net.ontopia.topicmaps.entry.TopicMaps;
import net.ontopia.topicmaps.nav2.utils.NavigatorUtils;
import net.ontopia.topicmaps.utils.NullResolvingExternalReferenceHandler;
import net.ontopia.topicmaps.utils.ltm.LTMTopicMapWriter;
import net.ontopia.topicmaps.utils.tmrap.RAPServlet;
import net.ontopia.topicmaps.xml.CanonicalXTMWriter;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.TestFileUtils;
import net.ontopia.utils.StreamUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.ontojsp.FakeServletConfig;
import net.ontopia.utils.ontojsp.FakeServletContext;
import net.ontopia.utils.ontojsp.FakeServletRequest;
import net.ontopia.utils.ontojsp.FakeServletResponse;
import net.ontopia.xml.ConfiguredXMLReaderFactory;
import org.junit.After;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;

import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TMRAPTestCase {

  private final static String testdataDirectory = "tmrap";

  @Parameters
  public static Collection generateTests() throws IOException {
	
    TestFileUtils.transferTestInputDirectory(testdataDirectory + "/topicmaps");
	
    String source = TestFileUtils.getTestInputFile(testdataDirectory, "tests.xml");
    InputStream in = StreamUtils.getInputStream(source);

    // Parse the test configuration file to get all the test descriptors.
    try {
      XMLReader parser = new ConfiguredXMLReaderFactory().createXMLReader();
      TMRAPTestCaseContentHandler handler = new TMRAPTestCaseContentHandler();
      handler.register(parser);
      parser.parse(new InputSource(in));
      return handler.getTestDescriptors();
    } catch (SAXException e) {
      throw new OntopiaRuntimeException(e);
    } catch (MalformedURLException e) {
      throw new OntopiaRuntimeException(e);
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  // --- Test case class

    private String base;
    private TMRAPTestDescriptor descriptor;

    public TMRAPTestCase(TMRAPTestDescriptor descriptor) {
      this.base = TestFileUtils.getTestdataOutputDirectory() + testdataDirectory + File.separator;
      this.descriptor = descriptor;
    }

    /**
     * Run an error test, i.e. a test that is meant to fail with some given
     * exception class.
     */
    private void runErrorTest() {
      try {
        runRapServlet(base, new PrintWriter(new StringWriter()), 
            descriptor.getUri(), false, false);
        Assert.fail("Expected to fail with: " + descriptor.getExpectedException()
            + " but executed without errors.");
      } catch (Exception e) {
        String eName = e.getClass().getName();
        Assert.assertTrue("Expected to fail with: " 
            + descriptor.getExpectedException() + " but failed with "
            + eName + " instead.", eName.equals(descriptor
                .getExpectedException()));
      }
    }
    
    @Test
    public void testFile() throws IOException, ServletException {
      if (descriptor.getId() == null) {
        runErrorTest();
        return;
      }
      
      TestFileUtils.verifyDirectory(base, "out");
      TestFileUtils.verifyDirectory(base, "cxtm");

      String id = descriptor.getId();
      
      // Path to the output.
      String out = base + "out" + File.separator + id
          + ".xtm";

      // Path to the canonicalized output.
      String cxtm = base + "cxtm" + File.separator 
          + id + ".cxtm";

      // Path to the baseline.
      String baseline = TestFileUtils.getTestInputFile(testdataDirectory, "baseline",
          id + ".cxtm");

      PrintWriter pw = new PrintWriter(new FileWriter(out));
      runRapServlet(base, pw, 
          descriptor.getUri(), descriptor.getEdit(), descriptor.getView());
      pw.close();
            
      // FIXME: When other syntaxes are supported, the Reader must take the
      // chosen syntax into account.
      XTMTopicMapReader xtmReader = new XTMTopicMapReader(new File(out));
      xtmReader.setExternalReferenceHandler(
          new NullResolvingExternalReferenceHandler());
      
      // Import the tm.
      TopicMapIF importedTM = xtmReader.read();
      
      filterUnifyingTopics(importedTM);

      // Canonicalize the reimported tm.
      FileOutputStream fos = new FileOutputStream(cxtm);
      (new CanonicalXTMWriter(fos)).write(importedTM);
      fos.close();
      
      // NOTE: Only for observational purposes when making tests.
      // (new LTMTopicMapWriter(new FileOutputStream(base + "ltm" + File.separator
      //     + id + ".ltm"))).write(importedTM);

      // Compare 'out' with 'baseline'.
      Assert.assertTrue("The output of the test with id " + id
          + " does not match the baseline: " + cxtm + " " + baseline, FileUtils.compareFileToResource(cxtm, baseline));
    }
  
  /**
   * A unifying topic is generated when a topic occurrs in multiple topicmaps
   * of a TMRAP query.
   * It has source locator with a time stamp, which makes testing harder since
   * it makes the output vary over time.
   * This method removes those source locators.
   * @param topicMap The topic map to remove the source locators from.
   */
  public static void filterUnifyingTopics(TopicMapIF topicMap) {
    Collection topics = topicMap.getTopics();
    Iterator topicsIt = topics.iterator();
    while (topicsIt.hasNext()) {
      TopicIF currentTopic = (TopicIF)topicsIt.next();
      
      Collection removables = new ArrayList();
      
      Collection sources = currentTopic.getItemIdentifiers();
      Iterator sourcesIt = sources.iterator();
      while (sourcesIt.hasNext()) {
        LocatorIF currentLocator = (LocatorIF)sourcesIt.next();
        if (currentLocator instanceof URIFragmentLocator
            && currentLocator.getAddress().indexOf("unifying-topic") != -1)
          removables.add(currentLocator);
      }
      
      Iterator removablesIt = removables.iterator();
      while (removablesIt.hasNext()) {
        LocatorIF currentLocator = (LocatorIF)removablesIt.next();
        currentTopic.removeItemIdentifier(currentLocator);
      }
    }
  }
  
  /**
   * Run RapServlet at a given base directory writing to a given PrintWriter.
   * @param base The base directory of the test.
   * @param responseWriter  Handles the output.
   * @param uriString The source URI which defines the TMRAP query.
   * @throws ServletException
   * @throws IOException
   */
  private static void runRapServlet(String base, PrintWriter responseWriter,
      String uriString, boolean edit, boolean view) throws ServletException, 
      IOException {
    int queryIndex = uriString.indexOf('?');    
    if (queryIndex == -1)
      queryIndex = uriString.length();
    String parameterPart = 
        (queryIndex == 0 || queryIndex == uriString.length()) ? ""
        : uriString.substring(queryIndex + 1);
    uriString = uriString.substring(0, queryIndex);

    HashMap params = TMRAPTestUtils.tokenizeParameters(parameterPart);
    params.put("server_name", "TMRAP Test Suite");
    if (edit)
      params.put("edit_uri", "http://localhost:8080/ontopoly/topicTypeConfig.ted?tm=%tmid%&id=%topicid%");
    if (view)
      params.put("view_uri", "http://localhost:8080/omnigator/models/topic_complete.jsp?tm=%tmid%&id=%topicid%");

    Hashtable paramsTable = TMRAPTestUtils.tabularizeParameters(params);
    
    FakeServletContext servletContext = new FakeServletContext(base, new Hashtable(), 
            Collections.singletonMap("source_config", 
                    TestFileUtils.getTestInputFile(testdataDirectory, "WEB-INF/config/tm-sources.xml")));
    
    FakeServletConfig servletConfig = new FakeServletConfig(servletContext,
        paramsTable);
    FakeServletRequest servletRequest = new FakeServletRequest(FakeServletRequest.transform(paramsTable));
    
    FakeServletResponse servletResponse = 
        new FakeServletResponse(responseWriter);
    
    RAPServlet rapServlet = new RAPServlet();
    
    rapServlet.init(servletConfig);
    rapServlet.doGet(servletRequest, servletResponse, uriString);

    TopicMaps.forget(NavigatorUtils.getTopicMapRepository(servletContext));

    if (servletResponse.getStatus() != 200)
      throw new ServletException("Error in running RAP servlet: " +
                                 servletResponse.getStatus() + " " +
                                 servletResponse.getMessage());
  }
}
