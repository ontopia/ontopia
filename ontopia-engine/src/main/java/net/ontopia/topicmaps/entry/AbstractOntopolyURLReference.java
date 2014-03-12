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

package net.ontopia.topicmaps.entry;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.TopicMapImporterIF;
import net.ontopia.topicmaps.impl.utils.TopicMapTransactionIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.impl.utils.TransactionEventListenerIF;
import net.ontopia.topicmaps.utils.DuplicateSuppressionUtils;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Common abstract superclass for references from sources
 * that support what Ontopoly needs.
 */
public abstract class AbstractOntopolyURLReference
  extends AbstractURLTopicMapReference
  implements TransactionEventListenerIF {
  protected boolean alwaysReindexOnLoad;

  public AbstractOntopolyURLReference(URL url, String id, String title,
                                      LocatorIF base) {
    super(id, title, url, base);
  }

  /**
   * INTERNAL: Sets the topic map store instance. This method is
   * intended to be called from the outside and by code that manually
   * constructs the topic map reference.
   */
  void setLoadedTopicMapStore(TopicMapStoreIF store) {
    this.store = store;
  }

  protected TopicMapIF loadTopicMap(boolean readonly) throws IOException {
    // create topic map importer
    TopicMapImporterIF reader = getImporter();

    File _indexDirectory = null;
    if (maintainFulltextIndexes) {
      _indexDirectory = new File(getIndexDirectory(), getId());
    }

    // create empty topic map
    InMemoryTopicMapStore store = new InMemoryTopicMapStore(maintainFulltextIndexes, _indexDirectory);
    if (base_address != null)
      store.setBaseAddress(base_address);
    TopicMapIF tm = store.getTopicMap();

    // import file into topic map
    reader.importInto(tm);

    // suppress duplicates
    if (getDuplicateSuppression())
      DuplicateSuppressionUtils.removeDuplicates(tm);

    return tm;
  }

  protected long getLastModifiedAt(File file) {
    return file.lastModified();
  }

  protected long getLastModifiedAt(URL url) {
    if ("file".equals(url.getProtocol())) {
      return new File(url.getFile()).lastModified();
    }
    return 0L;
  }
  
  public boolean getAlwaysReindexOnLoad() {
    return this.alwaysReindexOnLoad;
  }

  public void setAlwaysReindexOnLoad(boolean alwaysReindexOnLoad) {
    this.alwaysReindexOnLoad = alwaysReindexOnLoad;
  }

  public synchronized void delete() {
    if (source == null)
      throw new UnsupportedOperationException("This reference cannot be deleted as it does not belong to a source.");
    if (!source.supportsDelete())
      throw new UnsupportedOperationException("This reference cannot be deleted as the source does not allow deleting.");

    super.delete();
    if (getMaintainFulltextIndexes()) {
      try {
        // delete index directory
        ((InMemoryTopicMapStore)store).deleteFullTextIndex();
      } catch (IOException e) {
        throw new OntopiaRuntimeException(e);
      }
    }
  }
  
  // --------------------------------------------------------------------------
  // TransactionEventListenerIF implementation
  // --------------------------------------------------------------------------

  public void transactionCommit(TopicMapTransactionIF transaction) {
    // synchronize fulltext index
    try {
      ((InMemoryTopicMapStore)store).synchronizeFulltextIndex();
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  public void transactionAbort(TopicMapTransactionIF transaction) {
    // synchronize fulltext index
    try {
      ((InMemoryTopicMapStore)store).synchronizeFulltextIndex();
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  // --------------------------------------------------------------------------
  // Abstract methods
  // --------------------------------------------------------------------------

  protected abstract TopicMapImporterIF getImporter();
  
}
