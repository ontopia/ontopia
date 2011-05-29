
package net.ontopia.topicmaps.xml;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.TopicMapWriterIF;
import net.ontopia.topicmaps.entry.AbstractOntopolyTopicMapSource;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;

/**
 * INTERNAL: Source that locates TM/XML files in a directory on the
 * file system.
 */
public class TMXMLPathTopicMapSource extends AbstractOntopolyTopicMapSource {
  protected boolean validate;
  
  public TMXMLPathTopicMapSource() {
    this.validate = true;
  }

  public TMXMLPathTopicMapSource(String path, String suffix) {
    super(path, suffix);
    this.validate = true;
  }

  public TopicMapReferenceIF createReference(URL url, String id,
                                             String title,
                                             LocatorIF base_address) {
    TMXMLTopicMapReference ref = new TMXMLTopicMapReference(url, id, title,
                                                            base_address);
    ref.setSource(this);
    ref.setDuplicateSuppression(duplicate_suppression);
    ref.setValidation(validate);
    ref.setMaintainFulltextIndexes(maintainFulltextIndexes);
    ref.setIndexDirectory(indexDirectory);
    ref.setAlwaysReindexOnLoad(alwaysReindexOnLoad);
    return ref;
  }
  
  /**
   * PUBLIC: Turn validation of TM/XML documents according to RELAX-NG
   * schema on or off. The validation checks if the documents read
   * follow the schema, and will abort import if they do not.
   * @param validate Will validate if true, will not if false.
   */
  public void setValidation(boolean validate) {
    this.validate = validate;
  }

  /**
   * PUBLIC: Returns true if validation is on, false otherwise.
   */
  public boolean getValidation() {
    return validate;
  }

  /**
   * INTERNAL: Used by createTopicMap to serialize the new topic map.
   */
  protected TopicMapWriterIF getWriter(File file) throws IOException {
    return new TMXMLWriter(file);
  }
}
