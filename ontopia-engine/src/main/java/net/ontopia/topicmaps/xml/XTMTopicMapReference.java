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

package net.ontopia.topicmaps.xml;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.core.TopicMapWriterIF;
import net.ontopia.topicmaps.entry.AbstractOntopolyURLReference;

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
        File file = new File(path + File.separator + this.getId());
        TopicMapWriterIF writer = new XTMTopicMapWriter(file);
        writer.write(store.getTopicMap());
      }
    }
  }
  
  // ---------------------------------------------------------------------------
  // Abstract methods
  // ---------------------------------------------------------------------------

  @Override
  protected TopicMapReaderIF getImporter() throws IOException {
    // create topic map importer
    XTMTopicMapReader reader;
    if (base_address == null) {
      reader = new XTMTopicMapReader(url);
    } else {
      reader = new XTMTopicMapReader(url, base_address);
    }
    if (ref_handler != null) {
      reader.setExternalReferenceHandler(ref_handler);
    } else {
      reader.setFollowTopicRefs(followTopicRefs);
    }
    reader.setValidation(validate);
    return reader;
  }

}
