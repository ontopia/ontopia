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

import net.ontopia.infoset.fulltext.core.DocumentIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.VariantNameIF;
  
/**
 * INTERNAL: A document generator interface that can be implemented to
 * generate DocumentIFs for topic map objects.<p>
 */

public interface TopicMapDocumentGeneratorIF {

  /**
   * INTERNAL: Generate a document for the given association.
   */
  DocumentIF generate(AssociationIF assoc);
  
  /**
   * INTERNAL: Generate a document for the given association role.
   */
  DocumentIF generate(AssociationRoleIF assocrl);

  /**
   * INTERNAL: Generate a document for the given basename.
   */
  DocumentIF generate(TopicNameIF basename);

  /**
   * INTERNAL: Generate a document for the given occurrence.
   */
  DocumentIF generate(OccurrenceIF occurs);

  /**
   * INTERNAL: Generate a document for the given topic.
   */
  DocumentIF generate(TopicIF topic);

  /**
   * INTERNAL: Generate a document for the given topic map.
   */
  DocumentIF generate(TopicMapIF topicmap);

  /**
   * INTERNAL: Generate a document for the given variant name.
   */
  DocumentIF generate(VariantNameIF variant);
  
}
