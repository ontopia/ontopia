
package net.ontopia.topicmaps.xml;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;

import org.xml.sax.InputSource;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapImporterIF;
import net.ontopia.topicmaps.core.TopicMapWriterIF;
import net.ontopia.topicmaps.entry.AbstractOntopolyURLReference;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: An XTM document topic map reference.
 */
public class XTMTopicMapReference extends AbstractOntopolyURLReference {

  protected ExternalReferenceHandlerIF ref_handler;
  protected boolean followTopicRefs = true;
  protected boolean validate;
  
  public XTMTopicMapReference(URL url, String id, String title) {
    super(url, id, title, null);
    this.validate = true;
  }

  public XTMTopicMapReference(URL url, String id, String title,
                              LocatorIF base_address) {
    super(url, id, title, base_address);
    this.validate = true;
  }

  /**
   * PUBLIC: If set to false topicRef elements pointing to external
   * documents will not be traversed. The default is that those the
   * documents pointed to by those elements will be loaded (as per the
   * XTM specification).
   *
   * @since 3.2
   */
  public void setFollowTopicRefs(boolean followTopicRefs) {
    this.followTopicRefs = followTopicRefs;
  }

  /**
   * PUBLIC: Sets the external reference handler.
   */
  public void setExternalReferenceHandler(ExternalReferenceHandlerIF handler) {
    this.ref_handler = handler;
  }

  /**
   * PUBLIC: Gets the external reference handler. The reference handler will
   * receive notifications on references to external topics and topic maps.
   */
  public ExternalReferenceHandlerIF getExternalReferenceHandler() {
    return ref_handler;
  }

  /**
   * PUBLIC: Turn validation of XTM documents according to DTD on or off. The
   * validation checks if the documents read follow the DTD, and will abort
   * import if they do not.
   * 
   * @param validate Will validate if true, will not if false.
   * @since 2.0
   */
  public void setValidation(boolean validate) {
    this.validate = validate;
  }

  /**
   * PUBLIC: Returns true if validation is on, false otherwise.
   * 
   * @since 2.0
   */
  public boolean getValidation() {
    return validate;
  }

  /**
   * INTERNAL: Saves the topic map as an XTM document in the location managed by
   * the reference's source.
   */
  public synchronized void save() throws IOException {
    if (store != null && source instanceof XTMPathTopicMapSource) {
      XTMPathTopicMapSource src = (XTMPathTopicMapSource) source;
      String path = src.getPath();
      if (path != null) {
        String filename = path + File.separator + this.getId();
        TopicMapWriterIF writer = new XTMTopicMapWriter(filename);
        writer.write(store.getTopicMap());
      }
    }
  }
  
  // ---------------------------------------------------------------------------
  // Abstract methods
  // ---------------------------------------------------------------------------

  protected TopicMapImporterIF getImporter() {
    // create topic map importer
    XTMTopicMapReader reader;
    if (base_address == null) {
      try {
        reader = new XTMTopicMapReader(url.toString());
      } catch (MalformedURLException e) {
        throw new OntopiaRuntimeException(e); // impossible error
      }
    } else
      reader = new XTMTopicMapReader(new InputSource(url.toString()),
                                     base_address);
    if (ref_handler != null)
      reader.setExternalReferenceHandler(ref_handler);
    else
      reader.setFollowTopicRefs(followTopicRefs);
    reader.setValidation(validate);
    return reader;
  }

}
