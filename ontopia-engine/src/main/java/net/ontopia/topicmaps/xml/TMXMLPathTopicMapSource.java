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

  @Override
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
  @Override
  protected TopicMapWriterIF getWriter(File file) throws IOException {
    return new TMXMLWriter(file);
  }
}
