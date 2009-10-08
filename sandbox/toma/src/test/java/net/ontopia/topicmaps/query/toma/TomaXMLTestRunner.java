package net.ontopia.topicmaps.query.toma;

import net.ontopia.topicmaps.query.toma.parser.TomaParser;

import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TomaXMLTestRunner 
{
  private String queryFile = "src/test-data/toma/queries.xml";
  
  protected void setUp() 
  {
  }

  protected void tearDown() 
  {
  }
  
  public void testXML()
  {
    QueryHandler handler = new QueryHandler();
    SAXParser p = new SAXParser();
    p.setContentHandler(handler);
    
    try { 
      p.parse(queryFile); 
    } catch (Exception e) {
      e.printStackTrace();
      //fail("could not load query file '" + queryFile + "'");
    }
  }
    
  class QueryHandler extends DefaultHandler 
  {
    String desc;
    String query;
    String result;
    String success;
    
    String pcdata;
    
    @Override
    public void startElement(String namespaceURI, String localName,
        String qName, Attributes atts) 
    {
      pcdata = "";
    }

    @Override
    public void endElement(String uri, String localName, String qName)
    throws SAXException 
    {
      if (localName.equalsIgnoreCase("testcase")) 
      {
        System.out.println("-----------------------------");
        System.out.println("running query: ");
        System.out.println("    desc - " + desc);
        System.out.println("    query - " + query);
        
        TomaParser parser = new TomaParser();
        try {
          //parser.parse(query);
          //assertTrue(true);
        } catch (Exception e) {
          //fail("parsing error");
        }
        
        System.out.println();
        
      } else if (localName.equalsIgnoreCase("desc")) {
        desc = pcdata;
      } else if (localName.equalsIgnoreCase("query")) {
        query = pcdata;
      } else if (localName.equalsIgnoreCase("result")) {
        result = pcdata;
      } else if (localName.equalsIgnoreCase("success")) {
        success = pcdata;
      }
    }

    @Override
    public void characters(char[] characters, int start, int length) throws SAXException 
    {
      pcdata += new String(characters, start, length);
    }
  }
}
