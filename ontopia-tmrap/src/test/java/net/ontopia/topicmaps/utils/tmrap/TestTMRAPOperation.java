
package net.ontopia.topicmaps.utils.tmrap;

import java.io.File;
import java.io.Writer;
import java.io.Reader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;
import javax.servlet.ServletException;

import net.ontopia.utils.FileUtils;
import net.ontopia.utils.TestFileUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.ontojsp.FakeServletConfig;
import net.ontopia.utils.ontojsp.FakeServletContext;
import net.ontopia.utils.ontojsp.FakeServletRequest;
import net.ontopia.utils.ontojsp.FakeServletResponse;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.utils.NullResolvingExternalReferenceHandler;
import net.ontopia.topicmaps.utils.tmrap.RAPServlet;
import net.ontopia.topicmaps.xml.CanonicalXTMWriter;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;

import org.junit.Assert;
import org.junit.BeforeClass;

public abstract class TestTMRAPOperation {

  protected final static String testdataDirectory = "tmrap";

  protected static final String TEST_TOPIC = RAPServlet.RAP_NAMESPACE +
      "testTopic";
  
  protected String uriPrefix;
  protected RAPServlet rapServlet;

  @BeforeClass
  public static void transferInputFiles() throws IOException {
    TestFileUtils.transferTestInputDirectory(testdataDirectory + "/topicmaps");
  }
  
  protected TestTMRAPOperation() {
    uriPrefix = "http://localhost:8080/omnigator/plugins/viz/";
    rapServlet = new RAPServlet();
    
    try {
      setupRAPServlet(rapServlet, uriPrefix);
    } catch (ServletException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  /**
   * Generate a HashMap from a given parameter String.
   * @param parameters The String containing the source parameters.
   * @return A HashMap containig those parameters as key-value pairs.
   */
  protected static HashMap tokenizeParameters(String parameters) {
    HashMap retVal = new HashMap();
    
    StringTokenizer tok = new StringTokenizer(parameters);
    
    while (tok.hasMoreTokens()) {
      String currentToken = tok.nextToken("&");
      
      StringTokenizer keyValueTokenizer =  new StringTokenizer(currentToken);
      String key = keyValueTokenizer.hasMoreTokens() ?
          keyValueTokenizer.nextToken("=") : null;
      String value = keyValueTokenizer.hasMoreTokens() ?
          keyValueTokenizer.nextToken("=") : null;
      
      if (key != null)
        addParam(retVal, key, value);
    }

    return retVal;
  }
  
  /**
   * Add a given key-value pair to to a given map.
   * If the mapping already exists:
   *   If it is a Collection then add value to it.
   *   Otherwise, replace it with an ArrayList holding the old and new value.
   * @param map The map to which the key-value pair should be added.
   * @param key The to of the new mapping.
   * @param value The value of the new mapping.
   */
  protected static void addParam(Map map, Object key, Object value) {
    if (map.containsKey(key)) {
      Object oldValue = map.get(key);
      
      Collection newValue;      
      if (oldValue instanceof Collection)
        newValue = (Collection)oldValue;
      else {
        newValue = new ArrayList();
        newValue.add(oldValue);
      }
        
      newValue.add(value);
    } else
      map.put(key, value);
  }
  
  protected int doGet(String operationURI, String parameters,
                       Hashtable params, RAPServlet rapServlet, Writer out) 
    throws ServletException, IOException {
    return doGet(operationURI, parameters, params, rapServlet, out, -1);
  }
  
  protected int doGet(String operationURI, String parameters,
                      Hashtable params, RAPServlet rapServlet, Writer out,
                      int httpcode) 
    throws ServletException, IOException {
    
    Hashtable tempTable = new Hashtable(params);
    tempTable.putAll(TMRAPTestUtils
        .tabularizeParameters(TMRAPTestUtils
            .tokenizeParameters(parameters)));
    
    FakeServletRequest request = new FakeServletRequest(tempTable);
    PrintWriter writer = new PrintWriter(out);
    FakeServletResponse response = new FakeServletResponse(writer);
    rapServlet.doGet(request, response, operationURI);

    if (httpcode != -1 && response.getStatus() != httpcode)
      throw new ServletException("Error in HTTP operation: " + 
                                 response.getMessage());
    
    return response.getStatus();
  }

  // FIXME: aaaarghgh! This is evil! Kill it!
  // <evil>
  protected static final Hashtable paramsTable = createParamsTable();
  protected static Hashtable createParamsTable() {
    Map paramsMap = new HashMap();
    String viewURI = "http://localhost:8080/omnigator/plugins/viz/get-topic" +
        "/models/topic_complete.jsp?tm=%tmid%&id=%topicid%";
    paramsMap.put("view_uri", viewURI);
    
    return TMRAPTestUtils.tabularizeParameters(paramsMap);
  }
  // </evil>
  
  protected static void setupRAPServlet(RAPServlet rapServlet, 
                                        String viewURIPrefix)
    throws ServletException {
    String base = TestFileUtils.getTestdataOutputDirectory() + testdataDirectory + File.separator;
    
    Hashtable initParams = new Hashtable();
    initParams.put("source_config", TestFileUtils.getTestInputFile(testdataDirectory, "WEB-INF/config/tm-sources.xml"));
    FakeServletContext servletContext = new FakeServletContext(base, new Hashtable(), initParams);

    Map params = new HashMap();
    String viewURI = viewURIPrefix +
        "get-topic/models/topic_complete.jsp?tm=%tmid%&id=%topicid%";
    params.put("view_uri", viewURI);

    Hashtable paramsTable = TMRAPTestUtils.tabularizeParameters(params);
    FakeServletConfig servletConfig = new FakeServletConfig(servletContext,
        paramsTable);
    rapServlet.init(servletConfig);
  }

  private static int counter = 0;
  protected String canonicalizeXTM(String XTM) throws IOException {
    // Figure out base URL
    String root = TestFileUtils.getTestdataOutputDirectory() +
      File.separator + testdataDirectory + File.separator + "topicmaps" + File.separator;
    LocatorIF base = new URILocator(new File(root));
    
    // Import the TM
    StringReader reader = new StringReader(XTM);
    XTMTopicMapReader xtmReader = new XTMTopicMapReader(reader, base);
    xtmReader.setExternalReferenceHandler(
        new NullResolvingExternalReferenceHandler());
    
    TopicMapIF importedTM = xtmReader.read();
    TMRAPTestCase.filterUnifyingTopics(importedTM);

    // Canonicalize the reimported TM
    StringWriter stringWriter = new StringWriter();    
    new CanonicalXTMWriter(stringWriter).write(importedTM);
    String result = stringWriter.toString();
    
    return result;
  }
  
  protected int doPost(String operationURI, String parameters,
      Hashtable params, RAPServlet rapServlet, Writer out) 
          throws ServletException, IOException {
    Hashtable tempTable = new Hashtable(params);
    tempTable.putAll(TMRAPTestUtils.tabularizeParameters(TMRAPTestUtils
        .tokenizeParameters(parameters)));
    return doPost(operationURI, tempTable, rapServlet, out);
  }

  protected int doPost(String operationURI, Hashtable params, 
                       RAPServlet rapServlet, Writer out) 
    throws ServletException, IOException {

    FakeServletRequest request = new FakeServletRequest(params);    
    PrintWriter writer = new PrintWriter(out);
    FakeServletResponse response = new FakeServletResponse(writer);
    rapServlet.doPost(request, response, operationURI);
    return response.getStatus();
  }
  
  protected void verifyCanonical(String filename) throws IOException {
    // Figure out base URL
    String root = TestFileUtils.getTestdataOutputDirectory() +
     File.separator + testdataDirectory + File.separator;
    LocatorIF base = new URILocator(new File(root));    
    
    // Import the TM
    String xtmfile = root + "out" + File.separator + filename;
    Reader reader = new FileReader(xtmfile);
    XTMTopicMapReader xtmReader = new XTMTopicMapReader(reader, base);
    xtmReader.setExternalReferenceHandler(
        new NullResolvingExternalReferenceHandler());
    TopicMapIF importedTM = xtmReader.read();
    reader.close();
    TMRAPTestCase.filterUnifyingTopics(importedTM);

    // Canonicalize the reimported TM
    Writer out = new FileWriter(xtmfile + ".cxtm");
    new CanonicalXTMWriter(out).write(importedTM);
    out.close();

    // Compare results
    String baseline = TestFileUtils.getTestInputFile(testdataDirectory, "baseline", filename);
    Assert.assertTrue(filename + " did not match baseline",
               FileUtils.compareFileToResource(xtmfile + ".cxtm", baseline));
  }
}
