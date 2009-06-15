// $Id: ElementCount.java,v 1.5 2002/05/29 13:38:38 hca Exp $

package net.ontopia.test.perf;

import java.io.*;
import java.net.*;
import java.util.*;
import net.ontopia.xml.*;
import net.ontopia.topicmaps.xml.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

public class ElementCount extends DefaultHandler {

  protected Map elements;

  public ElementCount() {
    elements = new HashMap();
  }

  public Map getResult() {
    return elements;
  }
  
  public static void main(String args[]) throws Exception {
    
    // Create new parser object
    ConfiguredXMLReaderFactory cxrfactory = new ConfiguredXMLReaderFactory();
    cxrfactory.setEntityResolver(new IgnoreTopicMapDTDEntityResolver());

    ElementCount handler = new ElementCount();

    for (int ix = 0; ix < args.length; ix++) {
      XMLReader parser = cxrfactory.createXMLReader();
      parser.setContentHandler(handler);
      parser.parse(new URL(args[ix]).toExternalForm());
    }

    List result = new ArrayList(handler.getResult().entrySet());
    Collections.sort(result, new Comparator() {
        public int compare(Object o1, Object o2) {
          Integer i1 = (Integer)((Map.Entry)o1).getValue();
          Integer i2 = (Integer)((Map.Entry)o2).getValue();
          return i2.compareTo(i1);
        }
      });

    Iterator iter = result.iterator();
    while (iter.hasNext())
      System.out.println(iter.next());
    
  }

  public void startElement (String uri, String name, String qName, Attributes atts) throws SAXException {
    //String element = uri + "->" + name;
    String element = name;
    
    Integer count = (Integer)elements.get(element);
    if (count == null) count = new Integer(0);
    
    elements.put(element, new Integer(count.intValue() + 1));    
  }
  
}






