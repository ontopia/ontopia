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

/**
 * INTERNAL: Describes a TMRAP test by holding a uri and an id or
 * expectedException.
 */
public class TMRAPTestDescriptor {
  private String id = null;
  private String uri = null;
  private String expectedException = null;
  private boolean edit;
  private boolean view;
  
  /**
   * Create a new TMRAPTestDescriptor
   * uri must be non-null.
   * id or expectedException (but not both) must be non-null.
   */
  public TMRAPTestDescriptor(String id, String uri, String expectedException,
      String editString, String viewString) {
    if (uri == null) {
      throw new OntopiaRuntimeException("Missing parameter 'uri'");
    }
    
    if (id == null && expectedException == null) {
      throw new OntopiaRuntimeException("Invalid test parameters: id=\"" +
          id + "\" uri=\"" + uri + "\" exception=\"" + expectedException 
          + "\". Either the id or the exception parameter must be given.");
    }

    if (id != null && expectedException != null) {  
      throw new OntopiaRuntimeException("Invalid test parameters: id=\"" +
          id + "\" uri=\"" + uri + "\" exception=\"" + expectedException 
          + "\". Either the id or exception parameter must be given, "
          + "but not both.");
    }

    edit = (editString != null) && (editString.equalsIgnoreCase("true"));
    view = (viewString != null) && (viewString.equalsIgnoreCase("true"));
    if (!(this.edit || this.view)) {
      this.view = true; // One of edit or view must be true. Use view by defualt
    }
    this.id = id;
    this.uri = uri;
    this.expectedException = expectedException;
  }
  
  /**
   * @return true iff this test should be run with a edit base-uri
   * (and hence generate view-pages).
   */
  public boolean getEdit() {
    return edit;
  }
  
  /**
   * @return true iff this test should be run with a view base-uri
   * (and hence generate view-pages).
   */
  public boolean getView() {
    return view;
  }
  
  /**
   * @return the id (if any) of this TMRAPDescriptor.
   */
  public String getId() {
    return id;
  }
  
  /**
   * @return the uri of this TMRAPDescriptor.
   */
  public String getUri() {
    return uri;
  }

  /**
   * @return the expected exception (if any) of this TMRAPDescriptor.
   */
  public String getExpectedException() {
    return expectedException;
  }
}
