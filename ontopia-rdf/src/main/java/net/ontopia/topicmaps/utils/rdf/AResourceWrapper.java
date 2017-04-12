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

package net.ontopia.topicmaps.utils.rdf;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdfxml.xmlinput.AResource;

/**
 * INTERNAL: This class is used to wrap Jena Resource objects in the
 * ARP AResource interface so that they can be streamed through the ARP
 * StatementHandler interface without requiring new objects to be
 * created.
 */
public class AResourceWrapper implements AResource {
  public Resource resource;

  public boolean isAnonymous() {
    return resource.isAnon();
  }

  public String getAnonymousID() {
    return null;
  }

  public String getURI() {
    return resource.toString();
  }

  public Object getUserData() {
    return null;
  }

  public void setUserData(Object d) {
  }

  public int hashCode() {
    return resource.hashCode();
  }

  public boolean equals(Object obj) {
    return resource.equals(obj);
  }

  public String toString() {
    return "<" + resource.toString() + ">";
  }

  public boolean hasNodeID() {
    return false;
  }

}
