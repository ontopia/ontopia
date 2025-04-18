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

package net.ontopia.topicmaps.utils.ctm;

import java.net.URL;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.entry.AbstractOntopolyURLReference;

/**
 * INTERNAL: An CTM file topic map reference.
 */
public class CTMTopicMapReference extends AbstractOntopolyURLReference {
  
  public CTMTopicMapReference(URL url, String id, String title) {
    super(url, id, title, null);
  }
  
  public CTMTopicMapReference(URL url, String id, String title, LocatorIF base_address) {
    super(url, id, title, base_address);
  }

  // using loadTopicMap inherited from AbstractOntopolyURLReference

  @Override
  public TopicMapReaderIF getImporter() {
    if (base_address == null) {
      return new CTMTopicMapReader(url);
    } else {
      return new CTMTopicMapReader(url, base_address);
    }
  }
}
