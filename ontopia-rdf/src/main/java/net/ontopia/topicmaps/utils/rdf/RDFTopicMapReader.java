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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.utils.DuplicateSuppressionUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.URIUtils;
import org.apache.jena.shared.JenaException;

/**
 * PUBLIC: Converts an RDF model to a topic map using a
 * schema-specific mapping defined using RDF. The mapping is taken
 * from the RDF model unless a different model is specifically
 * indicated to contain the mapping.
 *
 * @since 2.0
 */
public class RDFTopicMapReader implements TopicMapReaderIF {
  public static final String PROPERTY_DUPLICATE_SUPPRESSION = "duplicateSuppression";
  public static final String PROPERTY_GENERATE_NAMES = "generateNames";
  public static final String PROPERTY_LENIENT = "lenient";
  public static final String PROPERTY_MAPPING_FILE = "mappingFile";
  public static final String PROPERTY_MAPPING_URL = "mappingURL";
  public static final String PROPERTY_MAPPING_SYNTAX = "mappingSyntax";
  protected URL infileurl;
  protected InputStream inputStream;
  protected String syntax;
  protected URL mappingurl;
  protected String mappingsyntax;
  protected boolean duplicate_suppression;
  protected boolean generate_names;
  protected boolean lenient;
  protected LocatorIF baseAddress;

  /**
   * PUBLIC: Creates a reader that will read RDF/XML from the given file.
   */
  public RDFTopicMapReader(File infile) throws MalformedURLException {
    this(infile, null);
  }

  /**
   * PUBLIC: Creates a reader that will read RDF from the given file in
   * the indicated syntax.
   * @param syntax The RDF syntax to use. Possible values are "RDF/XML", "N3",
   *               "N-TRIPLE". If the value is null it defaults to "RDF/XML".
   */
  public RDFTopicMapReader(File infile, String syntax) {
    this(URIUtils.toURL(infile), syntax);
  }
  
  /**
   * PUBLIC: Creates a reader that will read RDF/XML from the given URL.
   */
  public RDFTopicMapReader(URL infileurl) {
    this(infileurl, null);
  }

  /**
   * PUBLIC: Creates a reader that will read RDF from the given URL in
   * the indicated syntax.
   * @param syntax The RDF syntax to use. Possible values are "RDF/XML", "N3",
   *               "N-TRIPLE". If the value is null it defaults to "RDF/XML".
   */
  public RDFTopicMapReader(URL infileurl, String syntax) {
    this.infileurl = infileurl;
    this.syntax = syntax;
  }

  /**
   * PUBLIC: Creates a reader that will read RDF from the given InputStream in
   * the indicated syntax.
   * @param syntax The RDF syntax to use. Possible values are "RDF/XML", "N3",
   *               "N-TRIPLE". If the value is null it defaults to "RDF/XML".
   */
  public RDFTopicMapReader(InputStream inputStream, String syntax) {
    this.inputStream = inputStream;
    this.syntax = syntax;
  }

  /**
   * PUBLIC: Sets the file from which the reader will read the
   * RDF-to-topic map mapping definition. The syntax will be assumed
   * to be "RDF/XML".
   */
  public void setMappingFile(File mappingfile) {
    this.mappingurl = URIUtils.toURL(mappingfile);
  }

  /**
   * PUBLIC: Sets the file from which the reader will read the
   * RDF-to-topic map mapping definition.
   * @param syntax The RDF syntax to use. Possible values are "RDF/XML", "N3",
   *               "N-TRIPLE". If the value is null it defaults to "RDF/XML".
   */
  public void setMappingFile(File mappingfile, String syntax) {
    this.mappingurl = URIUtils.toURL(mappingfile);
    this.mappingsyntax = syntax;
  }

  /**
   * PUBLIC: Sets the URL from which the reader will read the
   * RDF-to-topic map mapping definition. The syntax will be assumed
   * to be "RDF/XML".
   */
  public void setMappingURL(URL url) {
    this.mappingurl = url;
  }

  /**
   * PUBLIC: Sets the URL from which the reader will read the
   * RDF-to-topic map mapping definition.
   * @param syntax The RDF syntax to use. Possible values are "RDF/XML", "N3",
   *               "N-TRIPLE". If the value is null it defaults to "RDF/XML".
   */
  public void setMappingURL(URL url, String syntax) {
    this.mappingurl = url;
    this.mappingsyntax = syntax;
  }

  /**
   * PUBLIC: Controls whether or not to automatically generate names
   * for nameless topics from their subject indicators.
   *
   * @since 2.0.5
   */
  public void setGenerateNames(boolean generate_names) {
    this.generate_names = generate_names;
  }

  /**
   * PUBLIC: Tells the reader whether or not to perform duplicate
   * suppression at the end of the import. The default is to not do
   * it.
   * @since 2.0.3
   */
  public void setDuplicateSuppression(boolean duplicate_suppression) {
    this.duplicate_suppression = duplicate_suppression;
  }

  /**
   * PUBLIC: Tells the reader whether or not to stop when errors are
   * found in the mapping. The default is to stop.
   * @since 2.1
   */
  public void setLenient(boolean lenient) {
    this.lenient = lenient;
  }

  /**
   * PUBLIC: Sets the base address of the topic maps retrieved from
   * the source.
   */
  public void setBaseAddress(LocatorIF baseAddress) {
    this.baseAddress = baseAddress;
  }
  
  // --- TopicMapReaderIF implementation
  
  @Override
  public TopicMapIF read() throws IOException {
    TopicMapIF topicmap = new InMemoryTopicMapStore().getTopicMap();
    
    LocatorIF base = baseAddress;
    if ((baseAddress == null) && (infileurl != null)){
      base = new URILocator(infileurl);
    }
    
    ((InMemoryTopicMapStore) topicmap.getStore()).
      setBaseAddress(base);
    importInto(topicmap);
    return topicmap;
  }

  @Override
  public Collection readAll() throws IOException {
    return Collections.singleton(read());
  }

  // --- TopicMapImporterIF implementation

  @Override
  public void importInto(TopicMapIF topicmap) throws IOException {
    try {
      if (inputStream != null) {
        RDFToTopicMapConverter.convert(inputStream, syntax, mappingurl == null ? null : mappingurl.toString(), 
                                       mappingsyntax, topicmap, lenient);
      } else {
        RDFToTopicMapConverter.convert(infileurl, syntax, mappingurl == null ? null : mappingurl.toString(), 
                                       mappingsyntax, topicmap, lenient);
      }
      if (generate_names) {
        RDFToTopicMapConverter.generateNames(topicmap);
      }
    } catch (URISyntaxException e) {
      throw new IOException(e);
    } catch (JenaException e) {
      throw new OntopiaRuntimeException(e);
    }

    if (duplicate_suppression) {
      DuplicateSuppressionUtils.removeDuplicates(topicmap);
    }
  }

  /**
   * Sets additional RDFTopicMapReader properties. Accepts the following properties:
   * <ul><li>'duplicateSuppression' (Boolean), corresponds to 
   * {@link #setDuplicateSuppression(boolean)}</li>
   * <li>'generateNames' (Boolean), corresponds to 
   * {@link #setGenerateNames(boolean)}</li>
   * <li>'lenient' (Boolean), corresponds to 
   * {@link #setLenient(boolean)}</li>
   * <li>'mappingFile' (File), corresponds to 
   * {@link #setMappingFile(java.io.File)}</li>
   * <li>'mappingURL' (String), corresponds to 
   * {@link #setMappingURL(java.lang.String)}</li>
   * <li>'mappingSyntax' (String), sets the syntax to use in combination with 
   * {@link #setMappingFile(java.io.File)} and {@link #setMappingURL(java.lang.String)}</li>
   * </ul>
   * @param properties 
   */
  @Override
  public void setAdditionalProperties(Map<String, Object> properties) {
    Object value = properties.get(PROPERTY_DUPLICATE_SUPPRESSION);
    if ((value != null) && (value instanceof Boolean)) {
      setDuplicateSuppression((Boolean) value);
    }
    value = properties.get(PROPERTY_GENERATE_NAMES);
    if ((value != null) && (value instanceof Boolean)) {
      setGenerateNames((Boolean) value);
    }
    value = properties.get(PROPERTY_LENIENT);
    if ((value != null) && (value instanceof Boolean)) {
      setLenient((Boolean) value);
    }
    value = properties.get(PROPERTY_MAPPING_FILE);
    if ((value != null) && (value instanceof File)) {
      setMappingFile((File) value);
    }
    value = properties.get(PROPERTY_MAPPING_URL);
    if ((value != null) && (value instanceof String)) {
      try {
        setMappingURL(new URL((String) value));
      } catch (MalformedURLException mufe) {
        throw new OntopiaRuntimeException(mufe);
      }
    }
    value = properties.get(PROPERTY_MAPPING_SYNTAX);
    if ((value != null) && (value instanceof String)) {
      this.mappingsyntax = (String) value;
    }
  }
}
