/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2016 The Ontopia Project
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

package net.ontopia.infoset.fulltext.impl.basic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.ontopia.infoset.fulltext.core.DocumentIF;
import net.ontopia.infoset.fulltext.core.FulltextImplementationIF;
import net.ontopia.infoset.fulltext.core.GenericDocument;
import net.ontopia.infoset.fulltext.core.GenericField;
import net.ontopia.infoset.fulltext.core.IndexerIF;
import net.ontopia.infoset.fulltext.core.SearchResultIF;
import net.ontopia.infoset.fulltext.core.SearcherIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;

/**
 * INTERNAL: a very basic fulltext implementation for testing purposes that goes through the 
 * topicmap brute force to find matches.
 */
public class DummyFulltextImplementation implements FulltextImplementationIF {
  
  private TopicMapIF tm;

  public void initialize(InMemoryTopicMapStore store) throws IOException {
    tm = store.getTopicMap();
  }

  public SearcherIF getSearcher() throws IOException {
    return new SearcherIF() {
      public SearchResultIF search(String query) throws IOException {
        String _query = query.toLowerCase();
        final List<DocumentIF> hits = new ArrayList<DocumentIF>();
        for (TopicIF topic : tm.getTopics()) {
          for (OccurrenceIF occurrence : topic.getOccurrences()) {
            if (occurrence.getValue().toLowerCase().contains(_query)) {
              DocumentIF d = new GenericDocument();
              d.addField(new GenericField("object_class", "O", true, true, false));
              d.addField(new GenericField("object_id", occurrence.getObjectId(), true, true, false));
              hits.add(d);
            }
          }
          for (TopicNameIF name : topic.getTopicNames()) {
            if (name.getValue().toLowerCase().contains(_query)) {
              DocumentIF d = new GenericDocument();
              d.addField(new GenericField("object_class", "B", true, true, false));
              d.addField(new GenericField("object_id", name.getObjectId(), true, true, false));
              hits.add(d);
            }
            for (VariantNameIF var : name.getVariants()) {
              if (var.getValue().toLowerCase().contains(_query)) {
                DocumentIF d = new GenericDocument();
                d.addField(new GenericField("object_class", "V", true, true, false));
                d.addField(new GenericField("object_id", var.getObjectId(), true, true, false));
                hits.add(d);
              }
            }
          }
        }
        
        return new SearchResultIF() {
          public DocumentIF getDocument(int hit) throws IOException {
            return hits.get(hit);
          }

          public float getScore(int hit) throws IOException {
            return 1;
          }

          public int hits() throws IOException {
            return hits.size();
          }
        };
      }

      public void close() throws IOException {
        // no-op
      }
    };
  }

  public IndexerIF getIndexer(boolean replaceIndex) throws IOException {
    return new IndexerIF() {
      public void index(DocumentIF document) throws IOException {
        // no-op
      }

      public void delete(String field, String value) throws IOException {
        // no-op
      }

      public void flush() throws IOException {
        // no-op
      }

      public void delete() throws IOException {
        // no-op
      }

      public void close() throws IOException {
        // no-op
      }
    };
  }

  public void close() throws IOException {
        // no-op
  }
}
