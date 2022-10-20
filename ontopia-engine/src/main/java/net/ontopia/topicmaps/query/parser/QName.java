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

package net.ontopia.topicmaps.query.parser;

/**
 * INTERNAL: Used to represent qualified name references in tolog queries.
 */
public class QName {
  protected String prefix;
  protected String localname;
  
  public QName(String qname) {
    int pos = qname.indexOf(':');
    if (pos == -1) {
      localname = qname;
    } else {
      prefix = qname.substring(0, pos);
      localname = qname.substring(pos+1);
    }
  }

  public String getPrefix() {
    return prefix;
  }
  
  public String getLocalName() {
    return localname;
  }

  /// Object

  @Override
  public String toString() {
    if (prefix == null) {
      return localname;
    } else {
      return prefix + ':' + localname;
    }
  }
  
}
