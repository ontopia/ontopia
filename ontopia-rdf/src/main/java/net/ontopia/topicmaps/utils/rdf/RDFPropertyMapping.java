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

/**
 * INTERNAL: Represents the mapping of a single RDF property.
 */
public class RDFPropertyMapping {
  private String property; // full uri
  private String mapsto;   // full uri
  private String inscope;  // full uri
  private String type;     // full uri
  private String subject;  // full uri
  private String object;   // full uri

  public RDFPropertyMapping(String property) {
    this.property = property;
  }

  public String getProperty() {
    return property;
  }
 
  public String getMapsTo() {
    return mapsto;
  }

  public void setMapsTo(String mapsto) {
    this.mapsto = mapsto;
  }

  public String getInScope() {
    return inscope;
  }

  public void setInScope(String inscope) {
    this.inscope = inscope;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getSubjectRole() {
    return subject;
  }

  public void setSubjectRole(String subject) {
    this.subject = subject;
  }

  public String getObjectRole() {
    return object;
  }

  public void setObjectRole(String object) {
    this.object = object;
  }
}
