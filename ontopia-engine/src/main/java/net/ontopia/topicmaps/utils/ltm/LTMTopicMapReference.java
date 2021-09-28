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

package net.ontopia.topicmaps.utils.ltm;

import java.io.IOException;
import java.net.URL;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapImporterIF;
import net.ontopia.topicmaps.entry.AbstractOntopolyURLReference;

/**
 * INTERNAL: An LTM file topic map reference.
 */
public class LTMTopicMapReference extends AbstractOntopolyURLReference {
  
  public LTMTopicMapReference(URL url, String id, String title) {
    super(url, id, title, null);
  }
  
  public LTMTopicMapReference(URL url, String id, String title, LocatorIF base_address) {
    super(url, id, title, base_address);
  }

  // using loadTopicMap inherited from AbstractOntopolyURLReference

  public TopicMapImporterIF getImporter() {
    try {
      return makeReader();
    } catch (IOException e) {
      throw new OntopiaRuntimeException("Bad URL: " + url, e);
    }
  }

  private LTMTopicMapReader makeReader() throws IOException {
    if (base_address == null)
      return new LTMTopicMapReader(url.toString());
    else
      return new LTMTopicMapReader(new org.xml.sax.InputSource(url.toString()), base_address);      
  }
}
