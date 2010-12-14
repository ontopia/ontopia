
// $Id: DataTypes.java,v 1.5 2009/02/27 11:58:15 lars.garshol Exp $

package net.ontopia.topicmaps.core;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;

/**
 * PUBLIC: Enumeration of common data types.
 *
 * @since 4.0
 */

public class DataTypes {

  public static final int SIZE_THRESHOLD = 65536;

  /**
   * PUBLIC: xsd:string datatype.
   */  
  public static final LocatorIF TYPE_STRING = URILocator.create("http://www.w3.org/2001/XMLSchema#string");

  /**
   * PUBLIC: xsd:anyType datatype.
   */  
  public static final LocatorIF TYPE_XML = URILocator.create("http://www.w3.org/2001/XMLSchema#anyType");

  /**
   * PUBLIC: xsd:anyURI datatype.
   */  
  public static final LocatorIF TYPE_URI = URILocator.create("http://www.w3.org/2001/XMLSchema#anyURI");

  /**
   * PUBLIC: xsd:decimal datatype.
   */  
  public static final LocatorIF TYPE_DECIMAL = URILocator.create("http://www.w3.org/2001/XMLSchema#decimal");

  /**
   * PUBLIC: xsd:integer datatype.
   */  
  public static final LocatorIF TYPE_INTEGER = URILocator.create("http://www.w3.org/2001/XMLSchema#integer");

  /**
   * PUBLIC: xsd:long datatype.
   */  
  public static final LocatorIF TYPE_LONG = URILocator.create("http://www.w3.org/2001/XMLSchema#long");

  /**
   * PUBLIC: xsd:float datatype.
   */  
  public static final LocatorIF TYPE_FLOAT = URILocator.create("http://www.w3.org/2001/XMLSchema#float");

  /**
   * PUBLIC: xsd:double datatype.
   */  
  public static final LocatorIF TYPE_DOUBLE = URILocator.create("http://www.w3.org/2001/XMLSchema#double");

  /**
   * PUBLIC: xsd:date datatype.
   */  
  public static final LocatorIF TYPE_DATE = URILocator.create("http://www.w3.org/2001/XMLSchema#date");

  /**
   * PUBLIC: xsd:dateTime datatype.
   */  
  public static final LocatorIF TYPE_DATETIME = URILocator.create("http://www.w3.org/2001/XMLSchema#dateTime");

  /**
   * PUBLIC: xsd:boolean datatype.
   */  
  public static final LocatorIF TYPE_BOOLEAN = URILocator.create("http://www.w3.org/2001/XMLSchema#boolean");

  /**
   * PUBLIC: xsd:base64Binary datatype.
   */  
  public static final LocatorIF TYPE_BINARY = URILocator.create("http://www.w3.org/2001/XMLSchema#base64Binary");
  
}
