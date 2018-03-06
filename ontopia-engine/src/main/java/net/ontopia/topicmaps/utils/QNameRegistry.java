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

package net.ontopia.topicmaps.utils;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * PUBLIC: A utility class for producing full URIs from QNames. Allows
 * QName prefixes to be registered, and has a set of predefined QName
 * prefixes. Also allows topics to be looked up, via the QNameLookup
 * class.
 * @since 5.0.0
 */
public class QNameRegistry {
  private Map<String, String> prefixes;
  
  /**
   * PUBLIC: Creates an empty registry.
   */
  public QNameRegistry() {
    this.prefixes = new HashMap<String, String>();
  }

  /**
   * PUBLIC: Registers a new prefix.
   */
  public void registerPrefix(String prefix, String uri) {
    prefixes.put(prefix, uri);
  }

  /**
   * PUBLIC: Creates a locator from a QName.
   * @throws OntopiaRuntimeException if the syntax is incorrect, the prefix
   *    is not bound, or the resulting locator is not a valid URI.
   */
  public LocatorIF resolve(String qname) {
    int pos = qname.indexOf(':');
    if (pos == -1) {
      throw new OntopiaRuntimeException("Qname " + qname + " has no colon!");
    }

    String prefix = qname.substring(0, pos);
    String localpart = qname.substring(pos + 1);

    String uri = (String) prefixes.get(prefix);
    if (uri == null) {
      throw new OntopiaRuntimeException("Unknown prefix " + prefix + " in " +
                                        qname);
    }

    try {
      return new URILocator(uri + localpart);
    } catch (URISyntaxException e) {
      throw new OntopiaRuntimeException("QName " + qname + " produced invalid " +
                                        "URI", e);
    }
  }

  /**
   * PUBLIC: Returns a QNameLookup object bound to a specific topic map.
   */
  public QNameLookup getLookup(TopicMapIF topicmap) {
    return new QNameLookup(this, topicmap);
  }
}
