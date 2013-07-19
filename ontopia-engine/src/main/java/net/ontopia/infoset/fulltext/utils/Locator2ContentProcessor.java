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

import java.io.Reader;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.core.LocatorReaderFactoryIF;
import net.ontopia.infoset.fulltext.core.DocumentIF;
import net.ontopia.infoset.fulltext.core.GenericField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: A document processor that replaces notation and address
 * fields with a reader field.<p>
 */

public class Locator2ContentProcessor extends AbstractLocatorDocumentProcessor {

  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(Locator2ContentProcessor.class.getName());

  protected String content_field;

  protected LocatorReaderFactoryIF lrf;
    
  public Locator2ContentProcessor(LocatorReaderFactoryIF lrf) {
    this(lrf, "notation", "address", "content");
  }
  
  public Locator2ContentProcessor(LocatorReaderFactoryIF lrf,
				 String notation_field, String address_field, String content_field) {
    super(notation_field, address_field);
    this.lrf = lrf;
    this.content_field = content_field;
  }

  /**
   * INTERNAL: Gets the name of the content field. This is the field in
   * which the Reader will be stored.
   */  
  public String getContentField() {
    return content_field;
  }

  /**
   * INTERNAL: Sets the name of the content field.
   */  
  public void setContentField(String content_field) {
    this.content_field = content_field;
  }
  
  public void process(DocumentIF document) throws Exception {

    // Create locator for this document
    LocatorIF locator = getLocator(document);

    // Get reader for preloaded resource
    Reader reader = lrf.createReader(locator);
    
    // Set content field
    document.addField(GenericField.createUnstoredField(getContentField(), reader));
  }

  public boolean needsProcessing(DocumentIF document) {
    return true;
  }
  
}





