
package net.ontopia.topicmaps.utils.rdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.net.MalformedURLException;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.URIUtils;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.MergeUtils;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.utils.DuplicateSuppressionUtils;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;

import com.hp.hpl.jena.shared.JenaException;

/**
 * PUBLIC: Converts an RDF model to a topic map using a
 * schema-specific mapping defined using RDF. The mapping is taken
 * from the RDF model unless a different model is specifically
 * indicated to contain the mapping.
 *
 * @since 2.0
 */
public class RDFTopicMapReader implements TopicMapReaderIF, TopicMapImporterIF {
  protected String infileurl;
  protected String syntax;
  protected String mappingurl;
  protected String mappingsyntax;
  protected boolean duplicate_suppression;
  protected boolean generate_names;
  protected boolean lenient;

  /**
   * PUBLIC: Creates a reader that will read RDF/XML from the given file.
   */
  public RDFTopicMapReader(File infile) {
    this(infile, null);
  }

  /**
   * PUBLIC: Creates a topic map reader bound to the URL given in the
   * arguments.   
   * @param url The URL of the topic map document.
   */  
  public RDFTopicMapReader(LocatorIF url) {
    this(url.getExternalForm());
  }

  /**
   * PUBLIC: Creates a topic map reader bound to the URL given in the
   * arguments.   
   * @param url The URL of the topic map document.
   * @param syntax The RDF syntax to use. Possible values are "RDF/XML", "N3",
   *               "N-TRIPLE". If the value is null it defaults to "RDF/XML".
   */  
  public RDFTopicMapReader(LocatorIF url, String syntax) {
    this(url.getExternalForm(), syntax);
  }

  /**
   * PUBLIC: Creates a reader that will read RDF from the given file in
   * the indicated syntax.
   * @param syntax The RDF syntax to use. Possible values are "RDF/XML", "N3",
   *               "N-TRIPLE". If the value is null it defaults to "RDF/XML".
   */
  public RDFTopicMapReader(File infile, String syntax) {
    this(file2Locator(infile), syntax);
  }
  
  /**
   * PUBLIC: Creates a reader that will read RDF/XML from the given URL.
   */
  public RDFTopicMapReader(String infileurl) {
    this(infileurl, null);
  }

  /**
   * PUBLIC: Creates a reader that will read RDF from the given URL in
   * the indicated syntax.
   * @param syntax The RDF syntax to use. Possible values are "RDF/XML", "N3",
   *               "N-TRIPLE". If the value is null it defaults to "RDF/XML".
   */
  public RDFTopicMapReader(String infileurl, String syntax) {
    this.infileurl = infileurl;
    this.syntax = syntax;
  }

  /**
   * PUBLIC: Sets the file from which the reader will read the
   * RDF-to-topic map mapping definition. The syntax will be assumed
   * to be "RDF/XML".
   */
  public void setMappingFile(File mappingfile) {
    this.mappingurl = file2Locator(mappingfile);
  }

  /**
   * PUBLIC: Sets the file from which the reader will read the
   * RDF-to-topic map mapping definition.
   * @param syntax The RDF syntax to use. Possible values are "RDF/XML", "N3",
   *               "N-TRIPLE". If the value is null it defaults to "RDF/XML".
   */
  public void setMappingFile(File mappingfile, String syntax) {
    this.mappingurl = file2Locator(mappingfile);
    this.mappingsyntax = syntax;
  }

  /**
   * PUBLIC: Sets the URL from which the reader will read the
   * RDF-to-topic map mapping definition. The syntax will be assumed
   * to be "RDF/XML".
   */
  public void setMappingURL(String url) {
    this.mappingurl = url;
  }

  /**
   * PUBLIC: Sets the URL from which the reader will read the
   * RDF-to-topic map mapping definition.
   * @param syntax The RDF syntax to use. Possible values are "RDF/XML", "N3",
   *               "N-TRIPLE". If the value is null it defaults to "RDF/XML".
   */
  public void setMappingURL(String url, String syntax) {
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
  
  // --- TopicMapReaderIF implementation
  
  public TopicMapIF read() throws IOException {
    TopicMapIF topicmap = new InMemoryTopicMapStore().getTopicMap();
    ((InMemoryTopicMapStore) topicmap.getStore()).
      setBaseAddress(new URILocator(infileurl));
    importInto(topicmap);
    return topicmap;
  }

  public Collection readAll() throws IOException {
    return Collections.singleton(read());
  }

  // --- TopicMapImporterIF implementation

  public void importInto(TopicMapIF topicmap) throws IOException {
    try {
      RDFToTopicMapConverter.convert(infileurl, syntax, mappingurl, mappingsyntax,
                                     topicmap, lenient);
      if (generate_names)
        RDFToTopicMapConverter.generateNames(topicmap);
    } catch (JenaException e) {
      throw new OntopiaRuntimeException(e);
    }

    if (duplicate_suppression)
      DuplicateSuppressionUtils.removeDuplicates(topicmap);
  }

  // --- Internal methods

  private static String file2Locator(File file) {
    try {
      return URIUtils.toURL(file).toExternalForm(); // FIXME: isn't right!
    } catch (MalformedURLException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
}
