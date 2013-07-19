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

package net.ontopia.infoset.fulltext.utils;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.fulltext.core.DocumentIF;
import net.ontopia.infoset.fulltext.core.DocumentProcessorIF;
import net.ontopia.infoset.fulltext.core.FieldIF;
import net.ontopia.infoset.impl.basic.GenericLocator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: An abstract document processor class that can be subclassed
 * by classes that needs to process locator information stored in
 * documents.<p>
 */

public abstract class AbstractLocatorDocumentProcessor implements DocumentProcessorIF {

  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(AbstractLocatorDocumentProcessor.class.getName());

  protected String notation_field;
  protected String address_field;
    
  public AbstractLocatorDocumentProcessor() {
    this("notation", "address");
  }
  
  public AbstractLocatorDocumentProcessor(String notation_field, String address_field) {
    this.notation_field = notation_field;
    this.address_field = address_field;
  }

  /**
   * INTERNAL: Gets the name of the notation field.
   */  
  public String getNotationField() {
    return notation_field;
  }

  /**
   * INTERNAL: Sets the name of the notation field.
   */  
  public void setNotationField(String notation_field) {
    this.notation_field = notation_field;
  }

  /**
   * INTERNAL: Gets the name of the address field.
   */  
  public String getAddressField() {
    return address_field;
  }

  /**
   * INTERNAL: Sets the name of the address field.
   */  
  public void setAddressField(String address_field) {
    this.address_field = address_field;
  }

  protected LocatorIF getLocator(DocumentIF document) {

    // Get notation field from document
    FieldIF notation = document.getField(getNotationField());
    if (notation == null) {
      if (log.isDebugEnabled())
	log.debug("Document " + document + " has no notation field '" + getNotationField() + "'.");
      return null;
    }
    
    // Get address field from document
    FieldIF address = document.getField(getAddressField());
    if (address == null) {
      if (log.isDebugEnabled())
	log.debug("Document " + document + " has no address field '" + getAddressField() + "'.");
      return null;
    }
    
    // Create locator
    return new GenericLocator(notation.getValue(), address.getValue());
  }
  
}





