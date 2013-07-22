/*
 * #!
 * Ontopia TMRAP
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
