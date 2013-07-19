/*
 * #!
 * Ontopia Engine
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
