
package net.ontopia.topicmaps.nav2.impl.framework;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.io.IOException;
import java.util.Map;
import java.util.Hashtable;
import java.util.Iterator;
import javax.servlet.jsp.PageContext;

import net.ontopia.utils.ontojsp.FakeHttpSession;
import net.ontopia.utils.ontojsp.FakePageContext;
import net.ontopia.utils.ontojsp.FakeServletConfig;
import net.ontopia.utils.ontojsp.FakeServletContext;
import net.ontopia.utils.ontojsp.FakeServletRequest;
import net.ontopia.utils.ontojsp.JSPPageExecuter;
import net.ontopia.utils.ontojsp.JSPPageReader;
import net.ontopia.utils.ontojsp.JSPTreeNodeIF;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.TestFileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.HashMap;
import java.io.InputStream;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;
import net.ontopia.xml.ConfiguredXMLReaderFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import net.ontopia.utils.StreamUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * INTERNAL: A class which represents a single testcase of the nav2
 * testing framework.
 */
@RunWith(Parameterized.class)
public class TaglibTestCase extends AbstractTaglibTestCase {

  private final static String testdataDirectory = "nav2";

  // initialization of logging facility
  private static Logger log = LoggerFactory
    .getLogger(TaglibTestCase.class.getName());

  // navigator environment (app-scope) shared by all test cases
  private static Hashtable appAttrs;
  // filename of output result file without path
  private String filename;
  // boolean value which is true if the test should fail.
  private boolean shouldFail;
  // string value that holds the expected exception if it is given.
  private String expectedException = "";

  @Parameters
  public static List generateTests() throws IOException, SAXException {
    String config = TestFileUtils.getTestInputFile(testdataDirectory, "config", "tests.xml");
    InputStream in = StreamUtils.getInputStream(config);

    XMLReader parser = new ConfiguredXMLReaderFactory().createXMLReader();
    TestCaseContentHandler handler = new TestCaseContentHandler();
    handler.register(parser);
    parser.parse(new InputSource(in));
    Map result = handler.getTests();
    List tests = new ArrayList<Object[]>();

    Iterator it = result.keySet().iterator();
    while (it.hasNext()) {
      String key = (String) it.next();
      Collection value = (Collection) result.get(key);
      StringTokenizer strtok = new StringTokenizer(key, "$$$");
      String tm = strtok.nextToken();
      String jsp = strtok.nextToken();
      Iterator iter = value.iterator();
      while (iter.hasNext()) {
        Map test_params = (HashMap) iter.next();
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
      if (test.equals("true"))
        shouldFail = true;
      else
        shouldFail = false;
      params.remove("fail");
    } else
      shouldFail = false;
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
      String jspSource = TestFileUtils.getTestInputFile(testdataDirectory, "jsp", jspfile);
      JSPPageReader reader = new JSPPageReader(jspSource);
      JSPTreeNodeIF root = reader.read();
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
        if (e.getClass().getName().equals(expectedException))
          Assert.assertTrue(true);
        else
          throw new OntopiaRuntimeException("Expected exception " + expectedException+
                                            ", got " + e.getClass(), e);
      } else {
        if (e instanceof java.io.FileNotFoundException)
          // handle this special to get rid of the baseline not found problem
          Assert.assertTrue("Could not find file: " + e.getMessage(), false);
        else if (e.getClass().getName().equals(expectedException)) {
          try {
            javax.servlet.jsp.JspWriter out = makePageContext().getOut();
            out.print(e.getMessage());
            out.flush();
            log.info("Compare results.");
            evaluate();
          } catch (Exception e1) {
            throw new OntopiaRuntimeException(e1);
          }
        } else
          throw new OntopiaRuntimeException(e);
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
      if (!shouldFail)
        Assert.fail("Cannot compare result, because baseline " +
             "file does not exist: " + infile);
      else {
        Assert.assertTrue("This test case should fail, and the file does not exist", true);
        return;
      }
    }

    if (shouldFail) {
      Assert.assertTrue("This testcase should have failed, but the result from the JSP file" +
                 " is the same as the baseline. [" + infile + "]",
                 !FileUtils.compareFileToResource(outfile, infile));
    } else {
      Assert.assertTrue("Result from the JSP file is not the same as baseline. [" +
                 infile + "]",
                 FileUtils.compareFileToResource(outfile, infile));
    }
  }

  /**
   * Creates the fake page context for the fake environment.
   */
  private PageContext makePageContext() throws IOException {
    // reuse same NavigatorApplication object for all test cases
    // so the topicmaps have not to be loaded several times.
    // setup attributes for application and session context
    if (appAttrs == null) appAttrs = new Hashtable();

    // Set up a complete page context, reusing the application scope attributes
    FakePageContext pageContext = new FakePageContext(getWriter());
    FakeServletRequest servletRequest = new FakeServletRequest(getRequestParameters());
    servletRequest.setContextPath("jsp/" + getJspFileName());
    String path = "classpath:net/ontopia/testdata/nav2/"; // so that it can find the WEB-INF directory.

    Hashtable initParams = new Hashtable();
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
    StringBuffer filename = new StringBuffer(32);

    // (1) Append the topicmap ID first.
    filename.append( getTopicMapId() );
    filename.append("-");
    // (2) Append the jsp file name next (without file extension).
    String jspname = getJspFileName();
    filename.append( jspname.substring(0, jspname.lastIndexOf(".jsp")) );
    // (3) Append the request parameters
    Hashtable params = getRequestParameters();
    Iterator it = params.keySet().iterator();
    while (it.hasNext()) {
      String key = (String) it.next();

      Object objectVal = params.get(key);
      if (objectVal instanceof String) {
        String val = (String) objectVal;
        filename.append("-").append(key).append("=").append(val);
      } else {
        String vals[] = (String[]) objectVal;

        if (0 < vals.length)
          filename.append("-").append(key).append("=").append(vals[0]);
        for (int i = 1; i < vals.length; i++)
          filename.append("_").append(vals[i]);
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
    StringBuffer descriptor = new StringBuffer(32);

    // (1) Append the topicmap ID first.
    descriptor.append("TopicMapId: ");
    descriptor.append( getTopicMapId() );
    // (2) Append the jsp file name next (without file extension).
    descriptor.append(", InputFile: ");
    descriptor.append( getJspFileName() );
    // (3) Append the request parameters
    descriptor.append(", Params: ");
    Hashtable params = getRequestParameters();
    Iterator it = params.keySet().iterator();
    while (it.hasNext()) {
      String key = (String) it.next();
      descriptor.append(" ").append(key).append("=");
      Object val = params.get(key);
      if (val instanceof String)
        descriptor.append(val);
      else {
        String[] vals = (String[]) val;
        for (int i=0; i < vals.length; i++) {
          descriptor.append(vals[i]);
          if (i < vals.length-1)
            descriptor.append(",");
        }
      }
    } // while
    return descriptor.toString();
  }

}
