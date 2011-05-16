// $Id: DocumentPreloaderProcessor.java,v 1.7 2005/07/07 13:15:08 grove Exp $

package net.ontopia.infoset.fulltext.utils;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.core.PreloaderIF;
import net.ontopia.infoset.fulltext.core.DocumentIF;
import net.ontopia.infoset.fulltext.core.DocumentProcessorIF;
import net.ontopia.infoset.fulltext.core.GenericField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: A document processor that preloads an external document
 * referenced by one of the documents fields.<p>
 */

public class DocumentPreloaderProcessor extends AbstractLocatorDocumentProcessor {

  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(DocumentPreloaderProcessor.class.getName());

  // protected String content_field;

  protected PreloaderIF preloader;

  protected DocumentProcessorIF pre_processor;
  protected DocumentProcessorIF post_processor;
    
  public DocumentPreloaderProcessor(PreloaderIF preloader) {
    this(preloader, "notation", "address");
  }

  public DocumentPreloaderProcessor(PreloaderIF preloader,
				    String notation_field, String address_field) {
    super(notation_field, address_field);
    this.preloader = preloader;
    //this.content_field = content_field;
  }

  /**
   * INTERNAL: Gets the document pre-processor if any.
   */  
  public DocumentProcessorIF getPreProcessor() {
    return pre_processor;
  }

  /**
   * INTERNAL: Sets the document pre-processor.
   */  
  public void setPreProcessor(DocumentProcessorIF pre_processor) {
    this.pre_processor = pre_processor;
  }

  /**
   * INTERNAL: Gets the document post-processor if any.
   */  
  public DocumentProcessorIF getPostProcessor() {
    return post_processor;
  }

  /**
   * INTERNAL: Sets the document post-processor.
   */  
  public void setPostProcessor(DocumentProcessorIF post_processor) {
    this.post_processor = post_processor;
  }

  // /**
  //  * INTERNAL: Gets the name of the content field.
  //  */  
  // public String getContentField() {
  //   return content_field;
  // }
  // 
  // /**
  //  * INTERNAL: Sets the name of the content field.
  //  */  
  // public void setContentField(String content_field) {
  //   this.content_field = content_field;
  // }

  /**
   * INTERNAL: Gets the name of the preloaded notation field. This is
   * the field in which the notation of the locator pointing to the
   * preloaded resource is put.
   */  
  public String getPreloadedNotationField() {
    return "dp:" + getNotationField();
  }

  /**
   * INTERNAL: Gets the name of the preloaded address field. This is
   * the field in which the address of the locator pointing to the
   * preloaded resource is put.
   */  
  public String getPreloadedAddressField() {
    return "dp:" + getAddressField();
  }
  
  public void process(DocumentIF document) throws Exception {

    // Pass document to pre-processor
    if (pre_processor != null) {
      pre_processor.process(document);
    }
    
    // Get original locator
    LocatorIF old_locator = getLocator(document);
    
    // Preload resource and get new locator
    LocatorIF new_locator = preloader.preload(old_locator);
    
    // Add new notation and address fields
    document.addField(GenericField.createTextField(getPreloadedNotationField(), new_locator.getNotation()));
    document.addField(GenericField.createTextField(getPreloadedAddressField(), new_locator.getAddress()));

    // Pass document to post-processor
    if (post_processor != null) {
      post_processor.process(document);
    }
    
  }

  public boolean needsProcessing(DocumentIF document) {
    // If disk preloading is required leave it for later.
    LocatorIF locator = getLocator(document);
    // If no locator was found we don't need to process document.
    if (locator == null) {
      log.debug("Problems finding locator in " + document);
      return false;
    }

    // Check preloader if it is necessary to preload document.
    if (preloader.needsPreloading(locator)) {
      return true;
    } else {
      try {
	// If it doesn't have to be preloaded let's just no it right now.
	process(document);
      } catch (Exception e) {
	// Ignore, since preloading failed.
	log.debug("Could not short-circuit the process step.:" + e);
      }
      return true;
    }
  }
  
}





