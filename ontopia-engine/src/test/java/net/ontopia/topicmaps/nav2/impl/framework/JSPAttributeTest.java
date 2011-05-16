
// $Id: JSPAttributeTest.java,v 1.2 2007/06/06 12:34:55 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.impl.framework;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Collections;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspException;

import org.xml.sax.SAXException;

import net.ontopia.utils.FileUtils;
import net.ontopia.utils.ontojsp.FakeHttpSession;
import net.ontopia.utils.ontojsp.FakePageContext;
import net.ontopia.utils.ontojsp.FakeServletConfig;
import net.ontopia.utils.ontojsp.FakeServletContext;
import net.ontopia.utils.ontojsp.FakeServletRequest;
import net.ontopia.utils.ontojsp.JSPPageExecuter;
import net.ontopia.utils.ontojsp.JSPPageReader;
import net.ontopia.utils.ontojsp.JSPTreeNodeIF;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.test.AbstractTopicMapTestCase;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;

/**
 * INTERNAL: Test for OKS JSP attribute access.
 */
public class JSPAttributeTest extends AbstractTopicMapTestCase {

  public JSPAttributeTest(String name) {
    super(name);
  }

  public void testSingle() throws Exception {
    runJSPTest("jspattr-single.jsp", makeMap("foo", "foovalue"));
  }
  
  public void testSingleNotFound() throws Exception {
    runJSPTestNotFound("jspattr-single.jsp", null);
  }

  public void testDoubleNotFound() throws Exception {
    runJSPTestNotFound("jspattr-double.jsp", null);
  }

  public void testDoubleNull() throws Exception {
    runJSPTestNotFound("jspattr-double.jsp", makeMap("foo", null));
  }

  public void testDoubleEmptyColl() throws Exception {
    runJSPTestNotFound("jspattr-double.jsp",
                       makeMap("foo", Collections.EMPTY_SET));
  }

  public void testDoubleNoFoo() throws Exception {
    runJSPTestNotFound("jspattr-double.jsp", new HashMap());
  }

  public void testDoubleEmptyMap() throws Exception {
    runJSPTestNotFound("jspattr-double.jsp", makeMap("foo", new HashMap()));
  }

  public void testDoubleNoMethod() throws Exception {
    runJSPTestNotFound("jspattr-double.jsp", makeMap("foo", "foovalue"));
  }

  public void testDoublePrivateMethod() throws Exception {
    runJSPTestNotFound("jspattr-double.jsp",
                       makeMap("foo", new PrivateObject()));
  }
  private class PrivateObject {
    private Object getBar() { return "badvalue"; }
  }

  public void testDoubleMethodWithParameters() throws Exception {
    runJSPTestNotFound("jspattr-double.jsp",
                       makeMap("foo", new ParametersObject()));
  }
  class ParametersObject {
    public Object getBar(String whatever) { return "badvalue"; }
  }

  public void testDoubleStaticMethod() throws Exception {
    runJSPTestNotFound("jspattr-double.jsp",
                       makeMap("foo", new StaticObject()));
  }
  static class StaticObject {
    public static Object getBar() { return "badvalue"; }
  }

  public void testDoubleMap() throws Exception {
    runJSPTest("jspattr-double.jsp",
               makeMap("foo", makeMap("bar", "barvalue")));
  }

  public void testDoubleObject() throws Exception {
    runJSPTest("jspattr-double.jsp",
               makeMap("foo", new NiceObject()));
  }
  public class NiceObject {
    public Object getBar() { return "barvalue"; }
  }

  public void testTripleMap() throws Exception {
    runJSPTest("jspattr-triple.jsp",
               makeMap("foo", makeMap("bar", makeMap("baz", "bazvalue"))));
  }
  
  // --- Helpers

  private void runJSPTestNotFound(String file, Map attributes) 
    throws IOException, JspException, SAXException {
    try {
      runJSPTest(file, attributes);
      fail("Reference to undefined variable went undetected");
    } catch (NavigatorRuntimeException e) {
      String msg = e.getMessage();
      assertTrue("Error message does not mention variable: " + msg,
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
    String jsp = resolveFileName("nav2", "jsp", file);

    // run test
    PageContext page = makePageContext(file, attributes);
    JSPPageReader reader = new JSPPageReader(new File(jsp));
    JSPTreeNodeIF root = reader.read();
    JSPPageExecuter exec = new JSPPageExecuter();
    exec.run(page, null, root);
    page.getOut().flush();

    // compare baseline and actual result
    String baseline = resolveFileName("nav2", "baseline", file);
    String outfile  = resolveFileName("nav2", "out", file);
    assertTrue("result not equal to baseline",
               FileUtils.compare(baseline, outfile));
  }

  /**
   * Creates the fake page context for the fake environment.
   */
  private PageContext makePageContext(String file, Map attributes) 
    throws IOException {
    String jspout = resolveFileName("nav2", "out", file);
    Writer out = new OutputStreamWriter(new FileOutputStream(jspout),
                                        "iso-8859-1");
    FakePageContext pageContext = new FakePageContext(out);
    if (attributes != null)
      pageContext.setAttributes(attributes);
    FakeServletRequest servletRequest = new FakeServletRequest();
    servletRequest.setContextPath("jsp/" + file);
    
    String path = resolveFileName("nav2");

    Hashtable initParams = new Hashtable();
    initParams.put("source_config", "WEB-INF/config/tm-sources.xml");
    FakeServletContext servletContext = new FakeServletContext(path, new Hashtable(), initParams);

    FakeServletConfig servletConfig = new FakeServletConfig(servletContext);
    FakeHttpSession session = new FakeHttpSession(servletContext);
    pageContext.setRequest(servletRequest);
    pageContext.setServletConfig(servletConfig);
    pageContext.setSession(session);
    return pageContext;
  }  
}
