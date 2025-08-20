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

import java.net.MalformedURLException;
import java.net.URL;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.entry.AbstractOntopolyURLReference;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: An RDF file topic map reference.
 */
public class RDFTopicMapReference extends AbstractOntopolyURLReference {
  private String syntax;
  private String mapfile;
  private boolean generateNames;
  private boolean lenient;
  
  public RDFTopicMapReference(URL url, String id, String title) {
    super(url, id, title, new URILocator(url));
  }
  
  public RDFTopicMapReference(URL url, String id, String title, LocatorIF base_address, String syntax) {
    super(url, id, title, base_address);
    this.syntax = syntax;
  }

  /**
   * @since 2.0.3
   */
  public void setMappingFile(String file) {
    this.mapfile = file;
  }

  /**
   * @since 2.0.5
   */
  public String getMappingFile() {
    return mapfile;
  }

  /**
   * @since 2.0.3
   */
  public void setSyntax(String syntax) {
    this.syntax = syntax;
  }

  /**
   * @since 2.1
   */
  public String getSyntax() {
    return syntax;
  }

  /**
   * @since 2.1
   */
  public void setLenient(boolean lenient) {
    this.lenient = lenient;
  }
  
  /**
   * @since 2.0.5
   */
  public void setGenerateNames(boolean generateNames) {
    this.generateNames = generateNames;
  }

  /**
   * @since 2.1
   */
  public boolean getGenerateNames() {
    return generateNames;
  }
  
  @Override
  protected TopicMapReaderIF getImporter() {
      RDFTopicMapReader reader = new RDFTopicMapReader(url, syntax);
      reader.setDuplicateSuppression(duplicate_suppression);
      if (mapfile != null) {
        try {
          reader.setMappingURL(new URL(mapfile));
        } catch (MalformedURLException mufe) {
          throw new OntopiaRuntimeException(mufe);
        }
      }
      reader.setGenerateNames(generateNames);
      reader.setLenient(lenient);
      
      return reader;
  }
  
}
