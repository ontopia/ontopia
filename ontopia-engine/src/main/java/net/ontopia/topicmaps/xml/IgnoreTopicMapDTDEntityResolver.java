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

import net.ontopia.xml.ConfigurableEntityResolver;
import net.ontopia.xml.EmptyInputSourceFactory;
import net.ontopia.xml.InputSourceFactoryIF;
import org.xml.sax.InputSource;

/**
 * INTERNAL: SAX entity resolver that makes sure that doctype
 * declarations referencing the ISO 13250 and XTM 1.0 DTDs using
 * public ids are ignored.</p>
 *
 * The resolver returns an empty input source for each entity with the
 * public ids:</p>
 *
 * <pre>
 *   "-//TopicMaps.Org//DTD XML Topic Map (XTM) 1.0//EN"
 *   "+//IDN ontopia.net//DTD Topic Map Interchange Format (Strict 1.0)//EN"
 * </pre>
 *
 * An empty input source is also used if the system_id ends with '.dtd'.</p>
 */
public class IgnoreTopicMapDTDEntityResolver extends ConfigurableEntityResolver {

  protected InputSourceFactoryIF factory;

  public IgnoreTopicMapDTDEntityResolver() {
    this(new EmptyInputSourceFactory());
  }
  
  public IgnoreTopicMapDTDEntityResolver(InputSourceFactoryIF factory) {
    this.factory = factory;
    addPublicIdSource("-//TopicMaps.Org//DTD XML Topic Map (XTM) 1.0//EN", factory);
    addPublicIdSource("+//IDN ontopia.net//DTD Topic Map Interchange Format (Strict 1.0)//EN", factory);    
  }

  @Override
  public InputSource resolveEntity (String public_id, String system_id) {
    if (system_id != null && system_id.endsWith(".dtd")) {
      return factory.createInputSource();
    }
    return super.resolveEntity(public_id, system_id);
  }
  
}
