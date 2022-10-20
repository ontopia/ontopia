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
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.utils.TestFileUtils;
import net.ontopia.utils.ontojsp.FakeHttpSession;
import net.ontopia.utils.ontojsp.FakePageContext;
import net.ontopia.utils.ontojsp.FakeServletConfig;
import net.ontopia.utils.ontojsp.FakeServletContext;
import net.ontopia.utils.ontojsp.FakeServletRequest;
import net.ontopia.utils.ontojsp.JSPPageExecuter;
import net.ontopia.utils.ontojsp.JSPPageReader;
import net.ontopia.utils.ontojsp.JSPTreeNodeIF;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * INTERNAL: Test for OKS JSP attribute access.
 */
public class JSPAttributeTest {

  private final static String testdataDirectory = "nav2";

  @Test
  public void testSingle() throws Exception {
    runJSPTest("jspattr-single.jsp", makeMap("foo", "foovalue"));
  }
  
  @Test
  public void testSingleNotFound() throws Exception {
    runJSPTestNotFound("jspattr-single.jsp", null);
  }

  @Test
  public void testDoubleNotFound() throws Exception {
    runJSPTestNotFound("jspattr-double.jsp", null);
  }

  @Test
  public void testDoubleNull() throws Exception {
    runJSPTestNotFound("jspattr-double.jsp", makeMap("foo", null));
  }

  @Test
  public void testDoubleEmptyColl() throws Exception {
    runJSPTestNotFound("jspattr-double.jsp",
                       makeMap("foo", Collections.EMPTY_SET));
  }

  @Test
  public void testDoubleNoFoo() throws Exception {
    runJSPTestNotFound("jspattr-double.jsp", new HashMap());
  }

  @Test
  public void testDoubleEmptyMap() throws Exception {
    runJSPTestNotFound("jspattr-double.jsp", makeMap("foo", new HashMap()));
  }

  @Test
  public void testDoubleNoMethod() throws Exception {
    runJSPTestNotFound("jspattr-double.jsp", makeMap("foo", "foovalue"));
  }

  @Test
  public void testDoublePrivateMethod() throws Exception {
    runJSPTestNotFound("jspattr-double.jsp",
                       makeMap("foo", new PrivateObject()));
  }
  private class PrivateObject {
    private Object getBar() { return "badvalue"; }
  }

  @Test
  public void testDoubleMethodWithParameters() throws Exception {
    runJSPTestNotFound("jspattr-double.jsp",
                       makeMap("foo", new ParametersObject()));
  }
  class ParametersObject {
    public Object getBar(String whatever) { return "badvalue"; }
  }

  @Test
  public void testDoubleStaticMethod() throws Exception {
    runJSPTest("jspattr-double.jsp",
                       makeMap("foo", new StaticObject()));
  }

  static class StaticObject {
    public static Object getBar() { return "barvalue"; }
  }

  @Test
  public void testDoubleMap() throws Exception {
    runJSPTest("jspattr-double.jsp",
               makeMap("foo", makeMap("bar", "barvalue")));
  }

  @Test
  public void testDoubleObject() throws Exception {
    runJSPTest("jspattr-double.jsp",
               makeMap("foo", new NiceObject()));
  }
  public class NiceObject {
    public Object getBar() { return "barvalue"; }
  }

  @Test
  public void testTripleMap() throws Exception {
    runJSPTest("jspattr-triple.jsp",
               makeMap("foo", makeMap("bar", makeMap("baz", "bazvalue"))));
  }
  
  // --- Helpers

  private void runJSPTestNotFound(String file, Map attributes) 
    throws IOException, JspException, SAXException {
    try {
      runJSPTest(file, attributes);
      Assert.fail("Reference to undefined variable went undetected");
    } catch (NavigatorRuntimeException e) {
      String msg = e.getMessage();
      Assert.assertTrue("Error message does not mention variable: " + msg,
                 msg.indexOf("foo") != -1);
    }
  }
  
  private Map makeMap(String var, Object value) {
    Map attrs = new HashMap();
    attrs.put(var, value);
    return attrs;
  }

  /**
   * Runs the JSP file and compares it against the baseline.
   */
  private void runJSPTest(String file, Map attributes)
    throws IOException, JspException, SAXException {
    URL jsp = TestFileUtils.getTestInputURL(testdataDirectory, "jsp", file);

    // run test
    PageContext page = makePageContext(file, attributes);
    JSPPageReader reader = new JSPPageReader(jsp);
    JSPTreeNodeIF root = reader.read();
    JSPPageExecuter exec = new JSPPageExecuter();
    exec.run(page, null, root);
    page.getOut().flush();

    // compare baseline and actual result
    String baseline = TestFileUtils.getTestInputFile(testdataDirectory, "baseline", file);
    File outfile  = TestFileUtils.getTestOutputFile(testdataDirectory, "out", file);
    Assert.assertTrue("result not equal to baseline for file '" + file + "'",
               TestFileUtils.compareFileToResource(outfile, baseline));
  }

  /**
   * Creates the fake page context for the fake environment.
   */
  private PageContext makePageContext(String file, Map attributes) 
    throws IOException {
    File jspout = TestFileUtils.getTestOutputFile(testdataDirectory, "out", file);
    Writer out = new OutputStreamWriter(new FileOutputStream(jspout),
                                        "iso-8859-1");
    FakePageContext pageContext = new FakePageContext(out);
    if (attributes != null) {
      pageContext.setAttributes(attributes);
    }
    FakeServletRequest servletRequest = new FakeServletRequest();
    servletRequest.setContextPath("jsp/" + file);
    
    String path = "classpath:net/ontopia/testdata/nav2/";

    Hashtable initParams = new Hashtable();
    initParams.put("source_config", "classpath:net/ontopia/testdata/nav2/WEB-INF/config/tm-sources.xml");
    FakeServletContext servletContext = new FakeServletContext(path, new Hashtable(), initParams);

    FakeServletConfig servletConfig = new FakeServletConfig(servletContext);
    FakeHttpSession session = new FakeHttpSession(servletContext);
    pageContext.setRequest(servletRequest);
    pageContext.setServletConfig(servletConfig);
    pageContext.setSession(session);
    return pageContext;
  }  
}
