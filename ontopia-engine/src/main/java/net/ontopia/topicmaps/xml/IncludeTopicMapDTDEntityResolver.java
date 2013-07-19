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

import java.io.StringReader;
import net.ontopia.xml.*;
import org.xml.sax.InputSource;

/**
 * INTERNAL: SAX entity resolver that makes sure that doctype
 * declarations referencing the XTM 1.0 DTD using public ids are given
 * the correct DTD.</p>
 *
 * The resolver returns an input source refering to the correct DTDs
 * for each entity with the public ids:</p>
 *
 * <pre>
 *   "-//TopicMaps.Org//DTD XML Topic Map (XTM) 1.0//EN"
 *   "+//IDN ontopia.net//DTD Topic Map Interchange Format (Strict 1.0)//EN"
 * </pre>
 */
public class IncludeTopicMapDTDEntityResolver extends ConfigurableEntityResolver {

  public IncludeTopicMapDTDEntityResolver() {
    InputSourceFactoryIF xtm_factory = new InputSourceFactoryIF() {
        public org.xml.sax.InputSource createInputSource() {
          return new InputSource(new StringReader(DTD.getXTMDocumentType()));
        }
      };
    addPublicIdSource("-//TopicMaps.Org//DTD XML Topic Map (XTM) 1.0//EN", xtm_factory);
  }
    
}




