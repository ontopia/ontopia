//$Id: RemoteXMLReaderFactory.java,v 1.2 2004/11/25 10:59:52 ian Exp $

package net.ontopia.topicmaps.utils.tmrap;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.xml.DefaultXMLReaderFactory;
import net.ontopia.xml.XMLReaderFactoryIF;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * INTERNAL: 
 * PRIVATE: 
 * Purpose: An implementation of the XMLReaderFactoryIF interface where the XML reader is reused instead of being created each time.
 */

public class RemoteXMLReaderFactory extends Object implements
    XMLReaderFactoryIF {

  private Class readerClass;

  /*
   * (non-Javadoc)
   * 
   * @see net.ontopia.xml.XMLReaderFactoryIF#createXMLReader()
   */
  public XMLReader createXMLReader() throws SAXException {

    // Get a XMLReader once using the standard utilities, save the class that is created, then 
    // create new readers from this each time a new reader is required.
    
    if( this.readerClass == null) {
      XMLReader reader;
      reader = new DefaultXMLReaderFactory().createXMLReader();
      this.readerClass = reader.getClass();
      return reader;
    }
      
    try {
      return (XMLReader)readerClass.newInstance();
    } catch (InstantiationException e) {
      e.printStackTrace();
      throw new OntopiaRuntimeException(e);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
      throw new OntopiaRuntimeException(e);
    }
  }

}
