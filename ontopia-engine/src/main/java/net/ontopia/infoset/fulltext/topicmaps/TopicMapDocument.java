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

package net.ontopia.infoset.fulltext.topicmaps;

import net.ontopia.infoset.fulltext.core.GenericDocument;

/**
 * INTERNAL: A class that extends GenericDocument to add an appropriate
 * toString implementation for topic map documents.<p>
 *
 * The used fields are: object_id, notation and address.<p>
 */

public class TopicMapDocument extends GenericDocument {

  protected String _toString() {
    final String OBJECT_ID = "object_id";
    if (fields.containsKey(OBJECT_ID) && !fields.containsKey("address") && fields.containsKey("content")) {
      return "Document ["  + (fields.get(OBJECT_ID)).getValue() + "] \"" + (fields.get("content")).getValue()  + "\"";
    } else if (fields.containsKey(OBJECT_ID) && fields.containsKey("address")) {
      return "Document ["  + (fields.get(OBJECT_ID)).getValue() + "] <" + (fields.get("address")).getValue()  + ">";
    } else if (fields.containsKey(OBJECT_ID)) {
      return "Document ["  + (fields.get(OBJECT_ID)).getValue() + "]";
    } else {
      return super.toString();
    }    
  }
  
}
