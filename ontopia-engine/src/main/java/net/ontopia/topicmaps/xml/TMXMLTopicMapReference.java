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
 * INTERNAL: A TM/XML file topic map reference.
 */
public class TMXMLTopicMapReference extends AbstractOntopolyURLReference {
  protected boolean validate;
  
  public TMXMLTopicMapReference(URL url, String id, String title) {
    super(url, id, title, null);
    this.validate = true;
  }

  public TMXMLTopicMapReference(URL url, String id, String title,
                                LocatorIF base_address) {
    super(url, id, title, base_address);
    this.validate = true;
  }

  /**
   * PUBLIC: Turn validation of TM/XML files according to the RELAX-NG
   * schema on or off. The validation checks if the documents read
   * follow the schema, and will abort import if they do not.
   * 
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
   * INTERNAL: Saves the topic map as an XTM document in the location
   * managed by the reference's source.
   */
  public synchronized void save() throws IOException {
    if (store != null && source instanceof TMXMLPathTopicMapSource) {
      TMXMLPathTopicMapSource src = (TMXMLPathTopicMapSource) source;
      String path = src.getPath();
      if (path != null) {
        File file = new File(path + File.separator + this.getId());
        TopicMapWriterIF writer = new TMXMLWriter(file);
        writer.write(store.getTopicMap());
      }
    }
  }
  
  // --------------------------------------------------------------------------
  // Abstract methods
  // --------------------------------------------------------------------------

  @Override
  protected TopicMapReaderIF getImporter() throws IOException {
    // create topic map importer
    TMXMLReader reader;
    if (base_address == null) {
      reader = new TMXMLReader(url);
    } else {
      reader = new TMXMLReader(url, base_address);
    }
    reader.setValidate(validate);
    return reader;
  }

}
