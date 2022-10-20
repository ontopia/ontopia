/*
 * #!
 * Ontopia Navigator
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

package net.ontopia.topicmaps.nav2.impl.framework;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import javax.servlet.jsp.PageContext;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.StreamUtils;
import net.ontopia.utils.TestFileUtils;
import net.ontopia.utils.ontojsp.FakeHttpSession;
import net.ontopia.utils.ontojsp.FakePageContext;
import net.ontopia.utils.ontojsp.FakeServletConfig;
import net.ontopia.utils.ontojsp.FakeServletContext;
import net.ontopia.utils.ontojsp.FakeServletRequest;
import net.ontopia.utils.ontojsp.JSPPageExecuter;
import net.ontopia.utils.ontojsp.JSPPageReader;
import net.ontopia.utils.ontojsp.JSPTreeNodeIF;
import net.ontopia.xml.DefaultXMLReaderFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * INTERNAL: A class which represents a single testcase of the nav2
 * testing framework.
 */
@RunWith(Parameterized.class)
public class TaglibTestCase extends AbstractTaglibTestCase {

  private final static String testdataDirectory = "nav2";

  // initialization of logging facility
  private static final Logger log = LoggerFactory
    .getLogger(TaglibTestCase.class.getName());

  // navigator environment (app-scope) shared by all test cases
  private static Map<String, Object> appAttrs;
  // filename of output result file without path
  private String filename;
  // boolean value which is true if the test should fail.
  private final boolean shouldFail;
  // string value that holds the expected exception if it is given.
  private String expectedException = "";
  // boolean value that indicates whether tag pooling should be used
  private final boolean useTagPooling;

  private final String PARAM_TAGPOOLING = "tagpooling";

  @Parameters
  public static List<Object[]> generateTests() throws IOException, SAXException {
    InputStream in = StreamUtils.getInputStream(
            TestFileUtils.getTestInputFile(testdataDirectory, "config", "tests.xml"));

    XMLReader parser = DefaultXMLReaderFactory.createXMLReader();
    TestCaseContentHandler handler = new TestCaseContentHandler();
    handler.register(parser);
    parser.parse(new InputSource(in));
    Map<String, Set<Map<String, String>>> result = handler.getTests();
    List<Object[]> tests = new ArrayList<Object[]>();

    for (String key : result.keySet()) {
      Set<Map<String, String>> value = result.get(key);
      StringTokenizer strtok = new StringTokenizer(key, "$$$");
      String tm = strtok.nextToken();
      String jsp = strtok.nextToken();
      for (Map<String, String> test_params : value) {
        tests.add(new Object[] {jsp, /* base, */ tm, test_params});
      }
    }
    return tests;
  }

  /**
   * Default constructor.
   */
  public TaglibTestCase(String jspfile, 
                        String topicmapId, Map params) {
    super(jspfile, topicmapId);
    if (params.containsKey("output")) {
      filename = (String) params.get("output");
      params.remove("output");
    }
    if (params.containsKey("fail")) {
      String test = (String) params.get("fail");
      if ("true".equals(test)) {
        shouldFail = true;
      } else {
        shouldFail = false;
      }
      params.remove("fail");
    } else {
      shouldFail = false;
    }
    if (params.containsKey(PARAM_TAGPOOLING)) {
      useTagPooling = ("true".equals(params.get(PARAM_TAGPOOLING)));
      params.remove(PARAM_TAGPOOLING);
    } else {
      useTagPooling = false;
    }
    if (params.containsKey("exception")) {
      expectedException = (String) params.get("exception");
      params.remove("exception");
    }
    setRequestParameters(params);
  }

  @Test
  public void testJSP() throws OntopiaRuntimeException {
    try {
      // setup environment and execute single test case
      PageContext page = makePageContext();
      JSPPageReader reader = new JSPPageReader(TestFileUtils.getTestInputURL(testdataDirectory, "jsp", jspfile));
      JSPTreeNodeIF root = reader.read(useTagPooling);
      JSPPageExecuter exec = new JSPPageExecuter();
      log.info("Run testcase for " + generateTestCaseDescriptor());
      exec.run(page, null, root);
      page.getOut().flush();
      log.debug("Compare results.");
      evaluate();
    } catch (Exception e) {
      if (e instanceof OntopiaRuntimeException) {
        e = (Exception) e.getCause();
      }
      log.info("Got an exception: " + e);
      if (shouldFail) {
        if (e.getClass().getName().equals(expectedException)) {
          Assert.assertTrue(true);
        } else {
          throw new OntopiaRuntimeException("Expected exception " + expectedException+
                                            ", got " + e.getClass(), e);
        }
      } else {
        if (e instanceof java.io.FileNotFoundException) {
          // handle this special to get rid of the baseline not found problem
          Assert.assertTrue("Could not find file: " + e.getMessage(), false);
        } else if (e.getClass().getName().equals(expectedException)) {
          try {
            javax.servlet.jsp.JspWriter out = makePageContext().getOut();
            out.print(e.getMessage());
            out.flush();
            log.info("Compare results.");
            evaluate();
          } catch (Exception e1) {
            throw new OntopiaRuntimeException(e1);
          }
        } else {
          throw new OntopiaRuntimeException(e);
        }
      }
    }
  }

  /**
   * Compares if output result and baseline are identical.
   */
  private void evaluate() throws IOException {
    String _filename = (filename == null) ? generateTestCaseFilename() : filename;
    String infile = TestFileUtils.getTestInputFile(testdataDirectory, "baseline", _filename);
    File outfile = TestFileUtils.getTestOutputFile(testdataDirectory, "out", _filename);

    boolean fileExists = true;
    try {
      StreamUtils.getInputStream(infile);
    } catch (IOException e) {
      fileExists = false;
    }
    if (!fileExists) {
      if (!shouldFail) {
        Assert.fail("Cannot compare result, because baseline " +
             "file does not exist: " + infile);
      } else {
        Assert.assertTrue("This test case should fail, and the file does not exist", true);
        return;
      }
    }

    if (shouldFail) {
      Assert.assertTrue("This testcase should have failed, but the result from the JSP file" +
                 " is the same as the baseline. [" + infile + "]",
                 !TestFileUtils.compareFileToResource(outfile, infile));
    } else {
      Assert.assertTrue("Result from the JSP file is not the same as baseline. [" +
                 infile + "]",
                 TestFileUtils.compareFileToResource(outfile, infile));
    }
  }

  /**
   * Creates the fake page context for the fake environment.
   */
  private PageContext makePageContext() throws IOException {
    // reuse same NavigatorApplication object for all test cases
    // so the topicmaps have not to be loaded several times.
    // setup attributes for application and session context
    if (appAttrs == null) {
      appAttrs = new HashMap<String, Object>();
    }

    // Set up a complete page context, reusing the application scope attributes
    FakePageContext pageContext = new FakePageContext(getWriter());
    FakeServletRequest servletRequest = new FakeServletRequest(getRequestParameters());
    servletRequest.setContextPath("jsp/" + getJspFileName());
    String path = "classpath:net/ontopia/testdata/nav2/"; // so that it can find the WEB-INF directory.

    Map<String, String> initParams = new HashMap<String, String>(2);
    initParams.put("source_config", "classpath:net/ontopia/testdata/nav2/WEB-INF/config/tm-sources.xml");
    initParams.put("app_config",    "classpath:net/ontopia/testdata/nav2/WEB-INF/config/application.xml");
    FakeServletContext servletContext = new FakeServletContext(path, appAttrs, initParams);

    FakeServletConfig servletConfig = new FakeServletConfig(servletContext);
    FakeHttpSession session = new FakeHttpSession(servletContext);
    pageContext.setRequest(servletRequest);
    pageContext.setServletConfig(servletConfig);
    pageContext.setSession(session);
    return pageContext;
  }

  /**
   * Creates the outputfile in the "out" directory.
   */
  private Writer getWriter() throws IOException {
    String _filename = (filename == null) ? generateTestCaseFilename() : filename;
    File file = TestFileUtils.getTestOutputFile(testdataDirectory, "out", _filename);

    if (!file.createNewFile()) {
      file.delete();
      file.createNewFile();
      log.info("Deleting old resultfile, and creating a new one.");
    }
    return new OutputStreamWriter(new FileOutputStream(file), "iso-8859-1");
  }

  /**
   * Constructs filename with the help of the topicmap id, the jsp
   * filename and the request parameters.
   */
  private String generateTestCaseFilename() {
    StringBuilder filename = new StringBuilder(32);

    // (1) Append the topicmap ID first.
    filename.append( getTopicMapId() );
    filename.append("-");
    // (2) Append the jsp file name next (without file extension).
    String jspname = getJspFileName();
    filename.append( jspname.substring(0, jspname.lastIndexOf(".jsp")) );
    // (3) Append the request parameters
    Map<String, String[]> params = getRequestParameters();
    for (String key : params.keySet()) {
       String vals[] = params.get(key);

      if (vals.length != 0) {
        filename.append("-").append(key).append("=").append(vals[0]);
        for (int i = 1; i < vals.length; i++) {
          filename.append("_").append(vals[i]);
        }
      }
    }

    // (4) filename suffix
    filename.append(".res");

    return filename.toString();
  }

  /**
   * Constructs description of this testcase which consists of the
   * topicmap id, the jsp filename and the request parameters.
   */
  private String generateTestCaseDescriptor() {
    StringBuilder descriptor = new StringBuilder(32);

    // (1) Append the topicmap ID first.
    descriptor.append("TopicMapId: ");
    descriptor.append( getTopicMapId() );
    // (2) Append the jsp file name next (without file extension).
    descriptor.append(", InputFile: ");
    descriptor.append( getJspFileName() );
    // (3) Append the request parameters
    descriptor.append(", Params: ");
    Map<String, String[]> params = getRequestParameters();
    for (String key : params.keySet()) {
      descriptor.append(" ").append(key).append("=");
      Object val = params.get(key);
      if (val instanceof String) {
        descriptor.append(val);
      } else {
        String[] vals = (String[]) val;
        for (int i=0; i < vals.length; i++) {
          descriptor.append(vals[i]);
          if (i < vals.length-1) {
            descriptor.append(",");
          }
        }
      }
    } // while
    return descriptor.toString();
  }

}
