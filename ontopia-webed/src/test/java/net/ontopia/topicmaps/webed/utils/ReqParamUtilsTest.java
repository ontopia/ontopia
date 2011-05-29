
package net.ontopia.topicmaps.webed.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletRequest;

import net.ontopia.topicmaps.webed.impl.utils.Parameters;
import net.ontopia.topicmaps.webed.impl.utils.ReqParamUtils;
import net.ontopia.utils.ontojsp.FakeServletRequest;
import junit.framework.TestCase;
  
public class ReqParamUtilsTest extends TestCase {
  
  public ReqParamUtilsTest(String name) {
    super(name);
  }

  public void setUp() throws Exception {
    super.setUp();
  }

  private String params2URLQuery(ServletRequest req) {
    return params2URLQuery(req, new HashMap());
  }
  
  private String params2URLQuery(Map wantedParams) {
    return params2URLQuery(null, wantedParams);
  }

  private String params2URLQuery(ServletRequest req, Map wantedParams) {
    Parameters params = new Parameters();
    if (req != null) {
      Map map = req.getParameterMap();
      Iterator it = map.keySet().iterator();
      while (it.hasNext()) {
        String key = (String) it.next();
        params.addParameter(key, (String) map.get(key));
      }
    }

    try {
      return ReqParamUtils.params2URLQuery(wantedParams, params, "utf-8");
    } catch (IOException e) {
      fail("Couldn't encode parameters");
      return null;
    }
  }
  
  // Tests
  
  public void testReversible() throws IOException {
    Map params = new HashMap();
    params.put("topic_id", "42");
    params.put("topicmap_id", "opera.xtm");
    Map parsed  = ReqParamUtils.parseURLQuery(params2URLQuery(params));
    assertTrue("Parsed map is not like input map: " + parsed,
               parsed.equals(params));
  }

  public void testParseURLQuery() {
    Map parsed  = ReqParamUtils.parseURLQuery("a=b&cde=fgh&d=h");
    assertTrue("result does not have three parameters",
               parsed.size() == 3);
    assertTrue("result does not have 'a' set to 'b'",
               parsed.get("a").equals("b"));
    assertTrue("result does not have 'cde' set to 'fgh'",
               parsed.get("cde").equals("fgh"));
    assertTrue("result does not have 'd' set to 'h'",
               parsed.get("d").equals("h"));
  }

  public void testEndsInAmpersand() {
    Map parsed  = ReqParamUtils.parseURLQuery("a=b&cde=fgh&d=h&");
    assertTrue("result does not have three parameters",
               parsed.size() == 3);
    assertTrue("result does not have 'a' set to 'b'",
               parsed.get("a").equals("b"));
    assertTrue("result does not have 'cde' set to 'fgh'",
               parsed.get("cde").equals("fgh"));
    assertTrue("result does not have 'd' set to 'h'",
               parsed.get("d").equals("h"));
  }

  public void testParseURLQuerySingleParam() {
    Map parsed  = ReqParamUtils.parseURLQuery("foo=bar");
    assertTrue("result does not have one parameter",
               parsed.size() == 1);
    assertTrue("result does not have 'foo' set to 'bar'",
               parsed.get("foo").equals("bar"));
  }
  
  public void testParams2URLQueryUnrestricted() {
    ServletRequest fakeRequest;
    String queryURL;
    Hashtable params = new Hashtable();
    
    // (1) setup servlet mockup environ
    params.clear();
    params.put("foo_bar_42", "Kroyt");
    fakeRequest = new FakeServletRequest(params);
    
    queryURL = params2URLQuery(fakeRequest);
    assertTrue("Generated String is not like expected one (1): '" + queryURL + "'",
               queryURL.equals(""));

    // (2) setup servlet mockup environ
    Map keep = new HashMap();
    keep.put("add_something_42", null);
    params.clear();
    params.put("add_something_42", "langer dumpf backen Garden");
    fakeRequest = new FakeServletRequest(params);
    
    queryURL = params2URLQuery(fakeRequest, keep);
    assertTrue("Generated string is not like expected one (2).",
               queryURL.equals("add_something_42=langer+dumpf+backen+Garden"));

    // (3) setup servlet mockup environ
    keep.clear();
    keep.put("topic_id", null);
    keep.put("topicmap_id", null);
    params.clear();
    params.put("topic_id", "42");
    params.put("topicmap_id", "opera.xtm");
    fakeRequest = new FakeServletRequest(params);
    
    queryURL = params2URLQuery(fakeRequest, keep);
    assertTrue("Generated String is not like expected one (3): '" + queryURL + "'",
               queryURL.equals("topic_id=42&topicmap_id=opera.xtm") ||
               queryURL.equals("topicmap_id=opera.xtm&topic_id=42"));
  }

  public void testParams2URLQueryRestricted() {
    ServletRequest fakeRequest;
    String queryURL;
    Hashtable params = new Hashtable();
    Map wantedParamNames = new HashMap();
    
    // (1) setup servlet mockup environ
    params.clear();
    params.put("foo_bar_42", "Kr\u00f8yt");
    fakeRequest = new FakeServletRequest(params);
    
    queryURL = params2URLQuery(fakeRequest, wantedParamNames);
    assertTrue("Generated String is not like expected one (1): '" + queryURL + "'",
               queryURL.equals(""));

    // (2) setup servlet mockup environ
    wantedParamNames.put("id", null);
    params.clear();
    params.put("add_something_42", "langer dumpf backen Garden");
    params.put("id", "42");
    fakeRequest = new FakeServletRequest(params);
    
    queryURL = params2URLQuery(fakeRequest, wantedParamNames);
    assertTrue("Generated String is not like expected one (2).",
               queryURL.equals("id=42"));

    // (3) setup servlet mockup environ
    wantedParamNames.put("tm", null);
    params.clear();
    params.put("id", "42");
    params.put("add_something_42", "langer dumpf backen Garden");
    params.put("tm", "opera.xtm");
    fakeRequest = new FakeServletRequest(params);
    
    queryURL = params2URLQuery(fakeRequest, wantedParamNames);
    assertTrue("Generated String is not like expected one (3).",
               queryURL.equals("id=42&tm=opera.xtm") ||
               queryURL.equals("tm=opera.xtm&id=42"));
  }

  public void testParams2URLQueryNonAscii() {
    ServletRequest fakeRequest;
    String queryURL;
    Hashtable params = new Hashtable();
    Map wantedParamNames = new HashMap();
    wantedParamNames.put("foo_bar_42", null);

    String correct = "foo_bar_42=Kr%C3%B8yt";
    // NOTE: the following is no longer reproducible on 1.3. It looks
    // like Sun has fixed the problem.
    //
    // // need to account for JDK 1.3
    // if (System.getProperty("java.vm.version").startsWith("1.3"))
    //   correct = "foo_bar_42=Kr%C3%B8yt";
    
    // (1) setup servlet mockup environ
    params.clear();
    params.put("foo_bar_42", "Kr\u00f8yt");
    fakeRequest = new FakeServletRequest(params);
    
    queryURL = params2URLQuery(fakeRequest, wantedParamNames);
    assertTrue("Generated string not like expected (1): '" + queryURL + "'",
               queryURL.equals(correct));

    // (2) setup servlet mockup environ
    wantedParamNames.clear();
    wantedParamNames.put("id", null);
    params.clear();
    params.put("add_something_42", "langer dumpf backen Garden");
    params.put("id", "42");
    fakeRequest = new FakeServletRequest(params);
    
    queryURL = params2URLQuery(fakeRequest, wantedParamNames);
    assertTrue("Generated String is not like expected one (2): '" + queryURL + "'",
               queryURL.equals("id=42"));

    // (3) setup servlet mockup environ
    wantedParamNames.put("tm", null);
    params.clear();
    params.put("id", "42");
    params.put("add_something_42", "langer dumpf backen Garden");
    params.put("tm", "opera.xtm");
    fakeRequest = new FakeServletRequest(params);
    
    queryURL = params2URLQuery(fakeRequest, wantedParamNames);
    assertTrue("Generated String is not like expected one (3).",
               queryURL.equals("id=42&tm=opera.xtm") ||
               queryURL.equals("tm=opera.xtm&id=42"));
  }
  
  public void testParams2URLQueryMap() throws IOException {
    String queryURL;
    Map params = new HashMap();
    
    params.put("topic_id", "42");
    params.put("topicmap_id", "opera.xtm");

    queryURL = params2URLQuery(params);
    assertTrue("Generated String is not like expected one.",
               queryURL.equals("topic_id=42&topicmap_id=opera.xtm") ||
               queryURL.equals("topicmap_id=opera.xtm&topic_id=42"));
  }
  
}
