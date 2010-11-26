package net.ontopia.tropics.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import net.ontopia.tropics.utils.TMTestUtils;
import net.ontopia.tropics.utils.URIUtils;
import net.ontopia.tropics.utils.XMLTestUtils;

import org.junit.Test;
import org.restlet.data.Status;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class SearchTest extends BasicTropicsTest {

  @SuppressWarnings("serial")
  @Test
  public void testLivesInLovecraft() throws ParserConfigurationException, SAXException, IOException {
    Map<String, String> params = new HashMap<String, String>() {{
      put("tms-include", TMTestUtils.LOVECRAFT_TM);
      put("query"      , "lives-in($CITIZEN : citizen, $CITY : city)?");
    }};

    ClientResource client = new ClientResource(URIUtils.buildURI(TMTestUtils.SEARCH_URI, params));
    String response = get(client);    
    Document doc = XMLTestUtils.readIntoDOM(response);

    NodeList rows = XMLTestUtils.getLengthCheckedNodeList(doc, "row", 1);        
    NodeList values = XMLTestUtils.getLengthCheckedNodeList((Element) rows.item(0), "value", 2);

    String[] expected = { "http://localhost:8182/api/v1/topics/cthulhu",
                          "http://localhost:8182/api/v1/topics/rlyeh"    };    
    for (int i = 0; i < 2; ++i) {
      NodeList entries = XMLTestUtils.getLengthCheckedNodeList((Element) values.item(i), "x:topicRef", 1);
      assertEquals(expected[i], ((Element) entries.item(0)).getAttribute("l:href"));
    }
  }

  @SuppressWarnings("serial")
  @Test
  public void testInstanceOfLovecraft() throws ParserConfigurationException, SAXException, IOException {
    Map<String, String> params = new HashMap<String, String>() {{
      put("tms-include", TMTestUtils.LOVECRAFT_TM);
      put("query"      , "instance-of($TOPIC, $TYPE) order by $TOPIC?");
    }};

    ClientResource client = new ClientResource(URIUtils.buildURI(TMTestUtils.SEARCH_URI, params));
    String response = get(client);    
    Document doc = XMLTestUtils.readIntoDOM(response);

    NodeList rows = XMLTestUtils.getLengthCheckedNodeList(doc, "row", 4);        

    String[][] expected = { { "http://localhost:8182/api/v1/topics/cthulhu", "http://localhost:8182/api/v1/topics/great_old_one"},
                            { "http://localhost:8182/api/v1/topics/nyarlathotep", "http://localhost:8182/api/v1/topics/god"},
                            { "http://localhost:8182/api/v1/topics/rlyeh", "http://localhost:8182/api/v1/topics/city"},    
                            { "http://localhost:8182/api/v1/topics/yig", "http://localhost:8182/api/v1/topics/great_old_one"} };

    for (int i = 0; i < 2; ++i) {
      NodeList values = XMLTestUtils.getLengthCheckedNodeList((Element) rows.item(i), "value", 2);

      for (int j = 0; j < 2; ++j) {
        NodeList entries = XMLTestUtils.getLengthCheckedNodeList((Element) values.item(j), "x:topicRef", 1);
        assertEquals(expected[i][j], ((Element) entries.item(0)).getAttribute("l:href"));
      }
    }
  }

  @SuppressWarnings("serial")
  @Test
  public void testNonExistantPredicateLovecraft() throws ParserConfigurationException, SAXException, IOException {
    Map<String, String> params = new HashMap<String, String>() {{
      put("tms-include", TMTestUtils.LOVECRAFT_TM);
      put("query"      , "eaten-by($FOOD : food, $MONSTER : monster)?");
    }};

    ClientResource client = new ClientResource(URIUtils.buildURI(TMTestUtils.SEARCH_URI, params));

    try {
      client.get();
      fail();
    } catch (ResourceException e) {
      assertEquals(Status.SERVER_ERROR_INTERNAL, e.getStatus());   
    }
  }

  @SuppressWarnings("serial")
  @Test
  public void testSomethingInItalianOpera() throws ParserConfigurationException, SAXException, IOException {
    Map<String, String> params = new HashMap<String, String>() {{
      put("tms-include", TMTestUtils.OPERA_TM);
      put("query"      , "select $PERSON from born-in($PERSON : person, $CITY : place), died-in($PERSON : person, $CITY : place), located-in($CITY : containee, italy : container) order by $PERSON?");
    }};

    ClientResource client = new ClientResource(URIUtils.buildURI(TMTestUtils.SEARCH_URI, params));
    String response = get(client);
    System.out.println(response);
    Document doc = XMLTestUtils.readIntoDOM(response);

    NodeList rows = XMLTestUtils.getLengthCheckedNodeList(doc, "row", 15);        

    String[] expected = { "http://localhost:8182/api/v1/topics/cammarano",
                          "http://localhost:8182/api/v1/topics/castelnova",
                          "http://localhost:8182/api/v1/topics/falena",
                          "http://localhost:8182/api/v1/topics/salvatore-di-giacomo",
                          "http://localhost:8182/api/v1/topics/giacosa",
                          "http://localhost:8182/api/v1/topics/golisciani",
                          "http://localhost:8182/api/v1/topics/illica",
                          "http://localhost:8182/api/v1/topics/manzoni",
                          "http://localhost:8182/api/v1/topics/menasci",
                          "http://localhost:8182/api/v1/topics/ricordi",
                          "http://localhost:8182/api/v1/topics/tito-ricordi",
                          "http://localhost:8182/api/v1/topics/romagnoli",
                          "http://localhost:8182/api/v1/topics/targioni-tozzetti",
                          "http://localhost:8182/api/v1/topics/verga",
                          "http://localhost:8182/api/v1/topics/zangarini" };

    for (int i = 0; i < 15; ++i) {
      NodeList values = XMLTestUtils.getLengthCheckedNodeList((Element) rows.item(i), "value", 1);
      NodeList entries = XMLTestUtils.getLengthCheckedNodeList((Element) values.item(0), "x:topicRef", 1);
      assertTrue(((Element) entries.item(0)).getAttribute("l:href").endsWith(expected[i]));
    }
  }
/*
  @SuppressWarnings("serial")
  @Test
  public void testSearchInMergedTopicMap() throws ParserConfigurationException, SAXException, IOException {
    Map<String, String> params = new HashMap<String, String>() {{
      put("tms-include", TMTestUtils.ALL_TM);
      put("query"      , "select $EXPERIMENT, $CARRIER " +
      		               "from s\"" + TMTestUtils.TOPICS_URI + "carried_out_on\"(" +
      		                         "$EXPERIMENT : s\"" + TMTestUtils.TOPICS_URI + "project\", " +
      		                         "$CARRIER : s\"" + TMTestUtils.TOPICS_URI + "platform\")" +
      		               "order by $EXPERIMENT?");
    }};
    
    ClientResource client = new ClientResource(URIUtils.buildURI(TMTestUtils.SEARCH_URI, params));
    String response = get(client);
    System.out.println(response);
    Document doc = XMLTestUtils.readIntoDOM(response);

    NodeList rows = XMLTestUtils.getLengthCheckedNodeList(doc, "row", 5);        

    String[][] expected = { { "http://localhost:8182/api/v1/topics/apis",    "http://localhost:8182/api/v1/topics/iss"},                                                        
                            { "http://localhost:8182/api/v1/topics/leukin",    "http://localhost:8182/api/v1/topics/iss"},
                            { "http://localhost:8182/api/v1/topics/lengsdorf_alni_pf2008_nequisol__prelim__", "http://localhost:8182/api/v1/topics/airbus_a_300_zero_g"},
                            { "http://localhost:8182/api/v1/topics/pulsar_2",   "http://localhost:8182/api/v1/topics/maxus_4"},
                            { "http://localhost:8182/api/v1/topics/sors_coronas_i",   "http://localhost:8182/api/v1/topics/coronas_i"}};
    

    for (int i = 0; i < 2; ++i) {
      NodeList values = XMLTestUtils.getLengthCheckedNodeList((Element) rows.item(i), "value", 2);

      for (int j = 0; j < 2; ++j) {
        NodeList entries = XMLTestUtils.getLengthCheckedNodeList((Element) values.item(j), "x:topicRef", 1);
        assertEquals(expected[i][j], ((Element) entries.item(0)).getAttribute("l:href"));
      }
    }
  }
*/  
}
