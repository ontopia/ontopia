
// $Id: URLTopicMapSource.java,v 1.14 2008/01/09 10:07:28 geir.gronmo Exp $

package net.ontopia.topicmaps.entry;

import java.io.*;
import java.net.*;
import java.util.*;
import org.xml.sax.*;
import org.apache.log4j.Logger;

import net.ontopia.utils.*;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.topicmaps.utils.ltm.LTMTopicMapReference;
import net.ontopia.topicmaps.utils.rdf.RDFTopicMapReference;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;

/**
 * INTERNAL: TopicMapSourceIF that can reference individual topic map
 * documents by their URL address. The properties id, title, url, and
 * syntax are the most commonly used. The syntaxes XTM, HyTM, and LTM
 * are currently supported.<p>
 *
 * @since 2.0
 */
public class URLTopicMapSource implements TopicMapSourceIF {

  // initialization of log facility
  private static Logger log = Logger
    .getLogger(URLTopicMapSource.class.getName());
  
  protected String id;
  protected String refid;
  protected String title;
  protected String url;
  protected String syntax;
  protected boolean hidden;

  protected LocatorIF base_address;
  protected boolean duplicate_suppression;
  protected boolean validate;
  protected ExternalReferenceHandlerIF ref_handler;
  
  protected Collection reflist;
  
  public URLTopicMapSource() {
  }

  public URLTopicMapSource(String url) {
    this.url = url;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  /**
   * INTERNAL: Gets the id of the topic map reference for this topic map
   * source.
   */
  public String getReferenceId() {
    return refid;
  }

  /**
   * INTERNAL: Sets the id of the topic map reference for this topic map
   * source.
   */
  public void setReferenceId(String refid) {
    this.refid = refid;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * INTERNAL: Returns the syntax of the document.
   */
  public String getSyntax() {
    return syntax;
  }

  /**
   * INTERNAL: Specifies the syntax of the document. This property will
   * be used to ensure that the topic map syntax is correctly
   * recognized. The supported syntaxes are 'XTM', 'HyTM', and
   * 'LTM'. If the syntax is not specified the class will attempt to
   * guess it by looking at the address suffix.
   */
  public void setSyntax(String syntax) {
    this.syntax = syntax;
  }

  /**
   * INTERNAL: Gets the URL of the source topic map.
   */
  public String getUrl() {
    return url;
  }

  /**
   * INTERNAL: Sets the URL of the source topic map.
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * INTERNAL: Gets the base locator of the topic maps retrieved from
   * the source.
   */
  public LocatorIF getBase() {
    return base_address;
  }

  /**
   * INTERNAL: Sets the base locator of the topic maps retrieved from
   * the source.
   */
  public void setBase(LocatorIF base_address) {
    this.base_address = base_address;
  }

  /**
   * INTERNAL: Gets the base address of the topic maps retrieved from
   * the source. The notation is assumed to be 'URI'.
   */
  public String getBaseAddress() {
    return base_address.getAddress();
  }

  /**
   * INTERNAL: Sets the base address of the topic maps retrieved from
   * the source. The notation is assumed to be 'URI'.
   */
  public void setBaseAddress(String base_address) {
    try {
      this.base_address = new URILocator(base_address);
    } catch (MalformedURLException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  /**
   * INTERNAL: Gets the duplicate suppression flag. If the flag is
   * true duplicate suppression is to be performed when loading the
   * topic maps.
   */
  public boolean getDuplicateSuppression() {
    return duplicate_suppression;
  }

  /**
   * INTERNAL: Sets the duplicate suppression flag. If the flag is
   * true duplicate suppression is to be performed when loading the
   * topic maps.
   */
  public void setDuplicateSuppression(boolean duplicate_suppression) {
    this.duplicate_suppression = duplicate_suppression;
  }

  /**
   * INTERNAL: Turn validation of XTM documents according to DTD on or
   * off. The validation checks if the documents read follow the DTD,
   * and will abort import if they do not.
   * @param validate Will validate if true, will not if false.
   */
  public void setValidation(boolean validate) {
    this.validate = validate;
  }

  /**
   * INTERNAL: Returns true if validation is on, false otherwise.
   */
  public boolean getValidation() {
    return validate;
  }

  /**
   * INTERNAL: Sets the external reference handler.
   */
  public void setExternalReferenceHandler(ExternalReferenceHandlerIF ref_handler) {
    this.ref_handler = ref_handler;
  }

  /**
   * INTERNAL: Gets the external reference handler. The reference
   * handler will receive notifications on references to external
   * topics and topic maps.
   */
  public ExternalReferenceHandlerIF getExternalReferenceHandler() {
    return ref_handler;
  }

  // ----
  
  public synchronized Collection getReferences() {
    if (reflist == null) refresh();
    return reflist;
  }

  public synchronized void refresh() {
    if (url == null)
      throw new OntopiaRuntimeException("'url' property has not been set.");
    if (refid == null)
      refid = id;
    if (refid == null)
      throw new OntopiaRuntimeException("Neither 'id' nor 'referenceId' properties has been set.");

    // Look at file suffix and guess file syntax.
    if (syntax == null) {
      if (url.endsWith(".xtm"))
        syntax = "XTM";
      else if (url.endsWith(".ltm"))
        syntax = "LTM";
      else if (url.endsWith(".rdf"))
        syntax = "RDF";
      else if (url.endsWith(".n3"))
        syntax = "N3";
    }

    // Create proper URL object
    URL url2;
    try {
      url2 = new URL(url);
    } catch (MalformedURLException e) {
      throw new OntopiaRuntimeException(e);
    }

    // Use id if title not set.
    if (title == null)
      title = id;
    
    if (syntax == null) {
      throw new OntopiaRuntimeException("Syntax not specified for '" + url + "'. Please set the 'syntax' parameter.");
    } else if (syntax.equalsIgnoreCase("XTM")) {
      // Create XTM reference
      XTMTopicMapReference ref = new XTMTopicMapReference(url2, refid, title, base_address);
      ref.setSource(this);
      ref.setDuplicateSuppression(duplicate_suppression);
      ref.setValidation(validate);
      if (ref_handler!=null)
        ref.setExternalReferenceHandler(ref_handler);
      reflist = Collections.singleton(ref);

    } else if (syntax.equalsIgnoreCase("LTM")) {
      // Create LTM reference
      LTMTopicMapReference ref = new LTMTopicMapReference(url2, refid, title, base_address);
      ref.setDuplicateSuppression(duplicate_suppression);
      ref.setSource(this);
      reflist = Collections.singleton(ref);

    } else if (syntax.equalsIgnoreCase("RDF/XML") ||
               syntax.equalsIgnoreCase("RDF") ||
               syntax.equalsIgnoreCase("N3") ||
               syntax.equalsIgnoreCase("N-TRIPLE")) {
      // Create RDF reference
      RDFTopicMapReference ref = new RDFTopicMapReference(url2, refid, title, base_address, null);
      ref.setDuplicateSuppression(duplicate_suppression);
      ref.setSource(this);

      if (!syntax.equalsIgnoreCase("RDF"))
        ref.setSyntax(syntax.toUpperCase());
      
      reflist = Collections.singleton(ref);
    } else 
      throw new OntopiaRuntimeException("Topic maps syntax '" + syntax +
                                        "' not supported.");
    
  }

  public boolean supportsCreate() {
    return false;
  }

  public boolean supportsDelete() {
    return false;
  }

  public TopicMapReferenceIF createTopicMap(String name, String baseAddress) {
    throw new UnsupportedOperationException();
  }

  public boolean getHidden() {
    return hidden;
  }

  public void setHidden(boolean hidden) {
    this.hidden = hidden;
  }

}
