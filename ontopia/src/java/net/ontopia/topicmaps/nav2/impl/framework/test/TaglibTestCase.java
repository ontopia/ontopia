
// $Id: TaglibTestCase.java,v 1.52 2007/09/10 13:35:22 lars.garshol Exp $

package net.ontopia.topicmaps.nav2.impl.framework.test;

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

import org.apache.log4j.Logger;

/**
 * INTERNAL: A class which represents a single testcase of the nav2
 * testing framework.
 */
public class TaglibTestCase extends AbstractTaglibTestCase {

  // initialization of logging facility
  private static Logger log = Logger
    .getLogger(TaglibTestCase.class.getName());

  // navigator environment (app-scope) shared by all test cases
  private static Hashtable appAttrs;
  // filename of output result file without path
  private String filename;
  // boolean value which is true if the test should fail.
  private boolean shouldFail;
  // string value that holds the expected exception if it is given.
  private String expectedException = "";

  /**
   * Default constructor.
   */
  public TaglibTestCase(String jspfile, String base,
                        String topicmapId, Map params) {
    super(jspfile, base, topicmapId);
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

  public void testJSP() throws OntopiaRuntimeException {
    try {
      // setup environment and execute single test case
      PageContext page = makePageContext();
      JSPPageReader reader = new JSPPageReader(new File(getJSPSource()));
      JSPTreeNodeIF root = reader.read();
      JSPPageExecuter exec = new JSPPageExecuter();
      log.info("Run testcase for " + generateTestCaseDescriptor());
      exec.run(page, null, root);
      page.getOut().flush();
      log.debug("Compare results.");
      evaluate();
    } catch (Exception e) {
      log.info("Got an exception: " + e);
      if (shouldFail) {
        if (e.getClass().getName().equals(expectedException))
          assertTrue(true);
        else
          throw new OntopiaRuntimeException("Expected exception " + expectedException+
                                            ", got " + e.getClass(), e);
      } else {
        if (e instanceof java.io.FileNotFoundException)
          // handle this special to get rid of the baseline not found problem
          assertTrue("Could not find file: " + e.getMessage(), false);
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

  public void setUp() {
    verifyDirectory(getBase(), "out");
  }

  /**
   * Compares if output result and baseline are identical.
   */
  private void evaluate() throws IOException {
    StringBuffer inbuf = new StringBuffer();
    StringBuffer outbuf = new StringBuffer();
    // Gets the base path for where to find the test results.
    inbuf.append(getBase() + "baseline" + File.separator);
    outbuf.append(getBase() + "out" + File.separator);
    if (filename == null) {
      inbuf.append(generateTestCaseFilename());
      outbuf.append(generateTestCaseFilename());
    } else {
      inbuf.append(filename);
      outbuf.append(filename);
    }

    if (!(new File(inbuf.toString()).exists())) {
      if (!shouldFail)
        fail("Cannot compare result, because baseline " +
             "file does not exist: " + inbuf.toString());
      else {
        assertTrue("This test case should fail, and the file does not exist", true);
        return;
      }
    }

    if (shouldFail) {
      assertTrue("This testcase should have failed, but the result from the JSP file" +
                 " is the same as the baseline. [" + inbuf.toString() + "]",
                 !FileUtils.compare(inbuf.toString(), outbuf.toString()));
    } else {
      assertTrue("Result from the JSP file is not the same as baseline. [" +
                 inbuf.toString() + "]",
                 FileUtils.compare(inbuf.toString(), outbuf.toString()));
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
    String path = getBase(); // so that it can find the WEB-INF directory.

    Hashtable initParams = new Hashtable();
    initParams.put("source_config", "WEB-INF/config/tm-sources.xml");
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
    StringBuffer outfile = new StringBuffer();
    // Append the base + the outdir.
    outfile.append(getBase() + "out" + File.separator);
    if (filename == null)
      outfile.append(generateTestCaseFilename());
    else outfile.append(filename);

    File file = new File(outfile.toString());
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
