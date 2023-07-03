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

package net.ontopia.topicmaps.utils;

import net.ontopia.infoset.impl.basic.URILocator;

/**
 * INTERNAL: This class collects core PSIs in a single place as a
 * convenience for topic map developers. See the XTM 1.0 specification
 * for definitions of the meanings of these PSIs.
 */
public class PSI {

  // --- Strings

  private static final String XTM_BASE = "http://www.topicmaps.org/xtm/1.0/core.xtm#";
  
  public static final String XTM_DISPLAY = XTM_BASE + "display";
  public static final String XTM_SORT = XTM_BASE + "sort";
  public static final String XTM_CLASS_INSTANCE = XTM_BASE + "class-instance";
  public static final String XTM_CLASS = XTM_BASE + "class";
  public static final String XTM_INSTANCE = XTM_BASE + "instance";
  public static final String XTM_OCCURRENCE = XTM_BASE + "occurrence";

  public static final String XTM_SUPERCLASS_SUBCLASS =
    XTM_BASE + "superclass-subclass";

  public static final String XTM_SUPERCLASS =
    XTM_BASE + "superclass";
        
  public static final String XTM_SUBCLASS =
    XTM_BASE + "subclass";

  private static final String SAM_BASE =
    "http://psi.topicmaps.org/iso13250/model/";

  public static final String SAM_TYPE_INSTANCE = SAM_BASE + "type-instance";
  public static final String SAM_TYPE = SAM_BASE + "type";
  public static final String SAM_INSTANCE = SAM_BASE + "instance";
  public static final String SAM_NAMETYPE = SAM_BASE + "topic-name";
  public static final String SAM_SUPERTYPE_SUBTYPE = SAM_BASE + "supertype-subtype";
  public static final String SAM_SUPERTYPE = SAM_BASE + "supertype";
  public static final String SAM_SUBTYPE = SAM_BASE + "subtype";

  private static final String XSD_BASE = "http://www.w3.org/2001/XMLSchema#";
  public static final String XSD_STRING = XSD_BASE + "string";
  public static final String XSD_INTEGER = XSD_BASE + "integer";
  public static final String XSD_DECIMAL = XSD_BASE + "decimal";
  public static final String XSD_DATE = XSD_BASE + "date";
  public static final String XSD_DATETIME = XSD_BASE + "dateTime";
  public static final String XSD_URI = XSD_BASE + "anyURI";

  private static final String CTM_BASE = "http://psi.topicmaps.org/iso13250/";
  public static final String CTM_INTEGER = CTM_BASE + "ctm-integer";
  public static final String CTM_CTM = CTM_BASE + "ctm";
  public static final String CTM_XTM = CTM_BASE + "xtm";
  
  // --- Internal cache

  private static URILocator xtmDisplay;
  private static URILocator xtmSort;

  private static URILocator xtmClassInstance;
  private static URILocator xtmClass;
  private static URILocator xtmInstance;
  private static URILocator xtmOccurrence;

  private static URILocator xtmSuperclassSubclass;
  private static URILocator xtmSuperclass;
  private static URILocator xtmSubclass;

  private static URILocator samTypeInstance;
  private static URILocator samType;
  private static URILocator samInstance;
  private static URILocator samNameType;
  private static URILocator samSupertypeSubtype;
  private static URILocator samSubtype;
  private static URILocator samSupertype;

  private static URILocator xsdString;
  private static URILocator xsdInteger;
  private static URILocator xsdDecimal;
  private static URILocator xsdDate;
  private static URILocator xsdDatetime;
  private static URILocator xsdUri;

  private static URILocator ctmInteger;
  private static URILocator ctmCtm;
  private static URILocator ctmXtm;
  
  // --- Locator objects
        
  public static URILocator getXTMDisplay() {
    if (xtmDisplay == null) {
      xtmDisplay = makeLocator(XTM_DISPLAY);
    }
    return xtmDisplay;
  }

  public static URILocator getXTMSort() {
    if (xtmSort == null) {
      xtmSort = makeLocator(XTM_SORT);
    }
    return xtmSort;
  }


  public static URILocator getXTMClassInstance() {
    if (xtmClassInstance == null) {
      xtmClassInstance = makeLocator(XTM_CLASS_INSTANCE);
    }
    return xtmClassInstance;
  }

  public static URILocator getXTMClass() {
    if (xtmClass == null) {
      xtmClass = makeLocator(XTM_CLASS);
    }
    return xtmClass;
  }

  public static URILocator getXTMInstance() {
    if (xtmInstance == null) {
      xtmInstance = makeLocator(XTM_INSTANCE);
    }
    return xtmInstance;
  }

  public static URILocator getXTMOccurrence() {
    if (xtmOccurrence == null) {
      xtmOccurrence = makeLocator(XTM_OCCURRENCE);
    }
    return xtmOccurrence;
  }

  public static URILocator getXTMSuperclassSubclass() {
    if (xtmSuperclassSubclass == null) {
      xtmSuperclassSubclass = makeLocator(XTM_SUPERCLASS_SUBCLASS);
    }
    return xtmSuperclassSubclass;
  }

  public static URILocator getXTMSuperclass() {
    if (xtmSuperclass == null) {
      xtmSuperclass = makeLocator(XTM_SUPERCLASS);
    }
    return xtmSuperclass;
  }

  public static URILocator getXTMSubclass() {
    if (xtmSubclass == null) {
      xtmSubclass = makeLocator(XTM_SUBCLASS);
    }
    return xtmSubclass;
  }

  // --- SAM locator objects

  public static URILocator getSAMTypeInstance() {
    if (samTypeInstance == null) {
      samTypeInstance = makeLocator(SAM_TYPE_INSTANCE);
    }
    return samTypeInstance;
  }

  public static URILocator getSAMInstance() {
    if (samInstance == null) {
      samInstance = makeLocator(SAM_INSTANCE);
    }
    return samInstance;
  }

  public static URILocator getSAMType() {
    if (samType == null) {
      samType = makeLocator(SAM_TYPE);
    }
    return samType;
  }

  public static URILocator getSAMNameType() {
    if (samNameType == null) {
      samNameType = makeLocator(SAM_NAMETYPE);
    }
    return samNameType;
  }

  public static URILocator getSAMSupertypeSubtype() {
    if (samSupertypeSubtype == null) {
      samSupertypeSubtype = makeLocator(SAM_SUPERTYPE_SUBTYPE);
    }
    return samSupertypeSubtype;
  }

  public static URILocator getSAMSupertype() {
    if (samSupertype == null) {
      samSupertype = makeLocator(SAM_SUPERTYPE);
    }
    return samSupertype;
  }

  public static URILocator getSAMSubtype() {
    if (samSubtype == null) {
      samSubtype = makeLocator(SAM_SUBTYPE);
    }
    return samSubtype;
  }

  public static URILocator getXSDString() {
    if (xsdString == null) {
      xsdString = makeLocator(XSD_STRING);
    }
    return xsdString;
  }

  public static URILocator getXSDInteger() {
    if (xsdInteger == null) {
      xsdInteger = makeLocator(XSD_INTEGER);
    }
    return xsdInteger;
  }

  public static URILocator getXSDDecimal() {
    if (xsdDecimal == null) {
      xsdDecimal = makeLocator(XSD_DECIMAL);
    }
    return xsdDecimal;
  }

  public static URILocator getXSDDate() {
    if (xsdDate == null) {
      xsdDate = makeLocator(XSD_DATE);
    }
    return xsdDate;
  }

  public static URILocator getXSDDatetime() {
    if (xsdDatetime == null) {
      xsdDatetime = makeLocator(XSD_DATETIME);
    }
    return xsdDatetime;
  }

  public static URILocator getXSDURI() {
    if (xsdUri == null) {
      xsdUri = makeLocator(XSD_URI);
    }
    return xsdUri;
  }

  public static URILocator getCTMInteger() {
    if (ctmInteger == null) {
      ctmInteger = makeLocator(CTM_INTEGER);
    }
    return ctmInteger;
  }

  public static URILocator getCTMSyntax() {
    if (ctmCtm == null) {
      ctmCtm = makeLocator(CTM_CTM);
    }
    return ctmCtm;
  }

  public static URILocator getCTMXTMSyntax() {
    if (ctmXtm == null) {
      ctmXtm = makeLocator(CTM_XTM);
    }
    return ctmXtm;
  }
  
  private static URILocator makeLocator(String loc) {
    return URILocator.create(loc);
  }
}
