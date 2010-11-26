package net.ontopia.tropics.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ontopia.tropics.exceptions.UnknownParameterException;
import net.ontopia.tropics.resources.QueryParam;

import org.junit.Test;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;

public class URIUtilsTest {

  private static final String BASE_URI = "http://example.org/api/v1/stuff";

  @Test
  public void testNonParameterURICreation() throws UnsupportedEncodingException {
    String uri = URIUtils.buildURI(BASE_URI, null);
    assertEquals(BASE_URI, uri);
  }

  @SuppressWarnings("serial")
  @Test
  public void testOneParameterURICreation() throws UnsupportedEncodingException {
    Map<String, String> params = new HashMap<String, String>() {{
      put("item", "/topicmaps/lovecraft");
    }};
    
    String uri = URIUtils.buildURI(BASE_URI, params);
    assertEquals(BASE_URI + "?item=%2Ftopicmaps%2Flovecraft", uri);
  }

  @SuppressWarnings("serial")
  @Test
  public void testTwoParameterURICreation() throws UnsupportedEncodingException {
    Map<String, String> params = new HashMap<String, String>() {{
      put("item", "/topicmaps/lovecraft");
      put("lang", "en, du");
    }};
    
    String uri = URIUtils.buildURI(BASE_URI, params);
    assertEquals(BASE_URI + "?item=%2Ftopicmaps%2Flovecraft&lang=en%2C+du", uri);
  }
  
  @Test
  public void testNonParameterURIExtraction() throws UnknownParameterException {
    Form form = new Form("");
    
    Map<QueryParam, String> params = URIUtils.extractParameters(new Response(new Request()), form);    
    assertEquals(0, params.size());
  }
  
  @Test
  public void testOneParameterURIExtraction() throws UnknownParameterException {    
    String include = "/topicmaps/foobar";
    Form form = new Form("ti=" + include);
    
    Map<QueryParam, String> params = URIUtils.extractParameters(new Response(new Request()), form);    
    assertEquals(1, params.size());
    
    assertTrue(params.containsKey(QueryParam.INCLUDE));
    assertEquals(include, params.get(QueryParam.INCLUDE));
  }

  @Test
  public void testTwoParameterURIExtraction() throws UnknownParameterException {
    String include = "/topicmaps/foobar";
    String query = "cthulhu";
    Form form = new Form("ti=" + include + "&q=" + query);
    
    Map<QueryParam, String> params = URIUtils.extractParameters(new Response(new Request()), form);    
    assertEquals(2, params.size());
    
    assertTrue(params.containsKey(QueryParam.INCLUDE));
    assertEquals(include, params.get(QueryParam.INCLUDE));
    assertTrue(params.containsKey(QueryParam.QUERY));
    assertEquals(query, params.get(QueryParam.QUERY));
  }
  
  @Test
  public void testUnkownParameterInExtraction() {
    String include = "/topicmaps/foobar";
    Form form = new Form("lmao=" + include);
    
    try {
      URIUtils.extractParameters(new Response(new Request()), form);    
    } catch (UnknownParameterException e) {
      assertEquals("Parameter <lmao> is not supported.", e.getMessage());
      return;
    }
    
    fail();
  }
  
  @Test
  public void testAliasesInExtraction() throws UnknownParameterException {
    String value = "=foobar";
    
    for (QueryParam param : QueryParam.values()) {
      List<String> aliases = new ArrayList<String>(param.getAliases());
      
      for (int i = 0; i < aliases.size(); ++i) {
        for (int j = i+1; j < aliases.size(); ++j) {
          Form form1 = new Form(aliases.get(i) + value);
          Form form2 = new Form(aliases.get(j) + value);
          
          Map<QueryParam, String> params1 = URIUtils.extractParameters(new Response(new Request()), form1);
          Map<QueryParam, String> params2 = URIUtils.extractParameters(new Response(new Request()), form2);
          
          assertEquals(1, params1.size());
          assertEquals(1, params2.size());
          
          assertTrue(params1.containsKey(param));
          assertTrue(params2.containsKey(param));
          
          assertEquals(params1.get(param), params2.get(param));
        }
      }
    }
  }
}
