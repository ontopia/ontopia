// $Id: AbstractLocatorDocumentProcessor.java,v 1.7 2005/07/07 13:15:08 grove Exp $

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





