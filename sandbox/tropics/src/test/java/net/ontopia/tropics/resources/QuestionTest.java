package net.ontopia.tropics.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import net.ontopia.tropics.utils.TMTestUtils;
import net.ontopia.tropics.utils.URIUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.Test;
import org.restlet.resource.ClientResource;
import org.xml.sax.SAXException;

public class QuestionTest extends BasicTropicsTest {

  @SuppressWarnings({ "serial" })
  @Test
  public void testWhichWriterDiedInRome() throws ParserConfigurationException, SAXException, IOException {
    Map<String, String> params = new HashMap<String, String>() {{
      put("ti", TMTestUtils.OPERA_TM);
      put("qs" ,"which writers died in Rome?");
    }};

    ClientResource client = new ClientResource(URIUtils.buildURI(TMTestUtils.QUESTION_URI, params));
    String response = get(client);
    
    Set<String> expected = new HashSet<String>(
        Arrays.asList("http://localhost:8182/api/v1/topics/romagnoli",
                      "http://localhost:8182/api/v1/topics/falena",
                      "http://localhost:8182/api/v1/topics/civinini",
                      "http://localhost:8182/api/v1/topics/colautti",
                      "http://localhost:8182/api/v1/topics/forzano",
                      "http://localhost:8182/api/v1/topics/moschino",
                      "http://localhost:8182/api/v1/topics/morselli"));
    
    
    checkExpectedItemIdentifiers(response, expected);
  }
  
  @SuppressWarnings({ "serial" })
  @Test
  public void testPucciniDateOfBirth() throws ParserConfigurationException, SAXException, IOException {
    Map<String, String> params = new HashMap<String, String>() {{
      put("ti", TMTestUtils.OPERA_TM);
      put("qs" ,"when was puccini born?");
    }};

    ClientResource client = new ClientResource(URIUtils.buildURI(TMTestUtils.QUESTION_URI, params));
    String response = get(client);
    System.out.println(response);
    
    Set<String> expected = new HashSet<String>(
        Arrays.asList("http://localhost:8182/api/v1/topics/puccini"));
    
    checkExpectedItemIdentifiers(response, expected);    
  }

  @SuppressWarnings("unchecked")
  private void checkExpectedItemIdentifiers(String response, Set<String> expected) {
    JSONObject jsonObject = JSONObject.fromObject(response);        
    
    JSONArray answers = jsonObject.getJSONArray("answers");
    assertEquals(expected.size(), answers.size());

    for (Iterator iterator = answers.iterator(); iterator.hasNext();) {
      JSONObject topic = (JSONObject) iterator.next();      
      
      JSONArray itemIdentifiers = topic.getJSONArray("item_identifiers");
      boolean foundExpected = false; 
      for (Iterator iterator2 = itemIdentifiers.iterator(); iterator2.hasNext();) {
        String itemIdentifier = (String) iterator2.next();
        if (expected.contains(itemIdentifier)) {
          foundExpected = true;
          break;
        }        
      }
      
      assertTrue(foundExpected);
    }
  }

}
