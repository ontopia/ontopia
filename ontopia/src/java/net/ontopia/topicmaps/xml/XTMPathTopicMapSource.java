
// $Id: XTMPathTopicMapSource.java,v 1.38 2007/08/29 12:56:02 geir.gronmo Exp $

package net.ontopia.topicmaps.xml;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.TopicMapWriterIF;
import net.ontopia.topicmaps.entry.AbstractOntopolyTopicMapSource;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Source that locates XTM topic map documents in a directory
 * on the file system.
 */
public class XTMPathTopicMapSource extends AbstractOntopolyTopicMapSource {

  protected ExternalReferenceHandlerIF ref_handler;
  protected boolean followTopicRefs = true;
  protected boolean validate;
  //! protected String xtmVersion;
  
  public XTMPathTopicMapSource() {
    this.validate = true;
  }

  public XTMPathTopicMapSource(String path, String suffix) {
    super(path, suffix);
    this.validate = true;
  }

  /**
   * INTERNAL: Constructor that takes the file directory and a file filter.
   *
   * @param path the path to search for TopicMaps
   * @param filter a java.io.FileFilter to filter the specified path
   *
   * @since 1.3.4
   */
  public XTMPathTopicMapSource(String path, FileFilter filter) {
    super(path, filter);
  }

  public TopicMapReferenceIF createReference(URL url, String id, String title,
                                                LocatorIF base_address) {
    XTMTopicMapReference ref = new XTMTopicMapReference(url, id, title,
                                                        base_address);
    ref.setSource(this);
    ref.setDuplicateSuppression(duplicate_suppression);
    ref.setValidation(validate);
    //! ref.setXtmVersion(xtmVersion);
    ref.setMaintainFulltextIndexes(maintainFulltextIndexes);
    ref.setIndexDirectory(indexDirectory);
    ref.setAlwaysReindexOnLoad(alwaysReindexOnLoad);
    if (ref_handler != null)
      ref.setExternalReferenceHandler(ref_handler);
    else
      ref.setFollowTopicRefs(followTopicRefs);      
    return ref;
  }

  /**
   * PUBLIC: If set to false topicRef elements pointing to external
   * documents will not be traversed. The default is that the
   * documents pointed to by those elements will be loaded (as per the
   * XTM 1.0 specification).
   *
   * @since 3.2
   */
  public void setFollowTopicRefs(boolean followTopicRefs) {
    this.followTopicRefs = followTopicRefs;
  }

  /**
   * PUBLIC: Sets the external reference handler.
   */
  public void setExternalReferenceHandler(ExternalReferenceHandlerIF ref_handler) {
    this.ref_handler = ref_handler;
  }

  /**
   * EXPERIMENTAL: Sets the name of the external reference handler
   * class. The specified class must have a default constructor.
   */
  public void setExternalReferenceHandlerClass(String ref_handler) {
    try {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      setExternalReferenceHandler((ExternalReferenceHandlerIF) Class.forName(ref_handler, true, classLoader).newInstance());
    } catch (Throwable e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  /**
   * EXPERIMENTAL: Gets the name of the external reference handler class.
   */
  public String getExternalReferenceHandlerClass() {
    return (ref_handler == null ? null : ref_handler.getClass().getName());
  }

  /**
   * PUBLIC: Gets the external reference handler. The reference
   * handler will receive notifications on references to external
   * topics and topic maps.
   */
  public ExternalReferenceHandlerIF getExternalReferenceHandler() {
    return ref_handler;
  }
  
  /**
   * PUBLIC: Turn validation of XTM documents according to DTD on or
   * off. The validation checks if the documents read follow the DTD,
   * and will abort import if they do not.
   * @param validate Will validate if true, will not if false.
   * @since 2.0
   */
  public void setValidation(boolean validate) {
    this.validate = validate;
  }

  /**
   * PUBLIC: Returns true if validation is on, false otherwise.
   * @since 2.0
   */
  public boolean getValidation() {
    return validate;
  }
  
  //! public String getXtmVersion() {
  //!   return xtmVersion;
  //! }
  //! 
  //! public void setXtmVersion(String xtmVersion) {
  //!   this.xtmVersion = xtmVersion;
  //! }

  /**
   * INTERNAL: Used by createTopicMap to serialize the new topic map.
   */
  protected TopicMapWriterIF getWriter(File file) throws IOException {
    return new XTMTopicMapWriter(file);
  }
}
