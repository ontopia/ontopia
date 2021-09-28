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

import java.io.IOException;
import java.util.Iterator;
import net.ontopia.infoset.fulltext.core.IndexerIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
  
/**
 * INTERNAL: A topic map event generator that iterates the objects in a
 * topic map and produces documents for an indexer.<p>
 *
 * Basenames, variant names, and occurrences are indexed.<p>
 *
 * Note: the properties topicMap, indexer and documentGenerator must
 * be set before the event generation can be started.<p>
 */

public class TopicMapIteratorGenerator {

  protected TopicMapDocumentGeneratorIF docgen;
  protected TopicMapIF topicmap;
  protected IndexerIF indexer;
  
  public TopicMapIteratorGenerator() {
  }

  public TopicMapIteratorGenerator(TopicMapIF topicmap, IndexerIF indexer, TopicMapDocumentGeneratorIF docgen) {
    this.topicmap = topicmap;
    this.indexer = indexer;
    this.docgen = docgen;
  }

  /**
   * INTERNAL: Gets the topic map that is to be iterated.
   */  
  public TopicMapIF getTopicMap() {
    return topicmap;
  }

  /**
   * INTERNAL: Sets the topic map that is to be iterated.
   */  
  public void setTopicMap(TopicMapIF topicmap) {
    this.topicmap = topicmap;
  }

  /**
   * INTERNAL: Gets the indexer that should receive the documents that
   * are generated during the iteration.
   */  
  public IndexerIF getIndexer() {
    return indexer;
  }

  /**
   * INTERNAL: Sets the indexer that should receive the documents that
   * are generated during the iteration.
   */  
  public void setIndexer(IndexerIF indexer) {
    this.indexer = indexer;
  }

  /**
   * INTERNAL: Gets the topic map document generator that is to be used.
   */  
  public TopicMapDocumentGeneratorIF getDocumentGenerator() {
    return docgen;
  }

  /**
   * INTERNAL: Gets the topic map document generator that is to be used.
   */  
  public void setDocumentGenerator(TopicMapDocumentGeneratorIF docgen) {
    this.docgen = docgen;
  }

  /**
   * INTERNAL: Iterates over the objects in a topic map and generates
   * indexable documents for objects in the topic map. Basenames,
   * variant names, and occurrences are indexed.
   */  
  public void generate() throws IOException {

    // Loop over the topics in the topic map
    Iterator<TopicIF> topics = topicmap.getTopics().iterator();
    while (topics.hasNext()) {
      TopicIF topic = topics.next();

      // Loop over the basenames of the topic
      Iterator<TopicNameIF> basenames = topic.getTopicNames().iterator();
      while (basenames.hasNext()) {
        TopicNameIF basename = basenames.next();
        indexer.index(docgen.generate(basename));

        // Loop over the variants of the basename
        Iterator<VariantNameIF> variants = basename.getVariants().iterator();
        while (variants.hasNext()) {
          VariantNameIF variant = variants.next();
          indexer.index(docgen.generate(variant));
        }
      }

      // Loop over the occurences of the topic
      Iterator<OccurrenceIF> occurs = topic.getOccurrences().iterator();
      while (occurs.hasNext()) {
        OccurrenceIF occur = occurs.next();
        indexer.index(docgen.generate(occur));
      }      
    }

  }
  
}





