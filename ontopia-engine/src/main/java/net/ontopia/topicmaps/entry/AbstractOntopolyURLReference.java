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
import java.util.Set;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.fulltext.core.FulltextImplementationIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.impl.utils.TopicMapTransactionIF;
import net.ontopia.topicmaps.impl.utils.TransactionEventListenerIF;
import net.ontopia.topicmaps.utils.DuplicateSuppressionUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.ServiceUtils;

/**
 * INTERNAL: Common abstract superclass for references from sources
 * that support what Ontopoly needs.
 */
public abstract class AbstractOntopolyURLReference
  extends AbstractURLTopicMapReference
  implements TransactionEventListenerIF {
  protected boolean alwaysReindexOnLoad;
  protected Set<FulltextImplementationIF> ftmanagers;

  public AbstractOntopolyURLReference(URL url, String id, String title,
                                      LocatorIF base) {
    super(id, title, url, base);
  }

  @Override
  public synchronized void open() {
    if (!isOpen() && !isDeleted() && maintainFulltextIndexes) {
      try {
        ftmanagers = ServiceUtils.loadServices(FulltextImplementationIF.class);
      } catch (IOException ioe) {
        throw new OntopiaRuntimeException("Could not retrieve fulltext services: " + ioe.getMessage(), ioe);
      }
      for (FulltextImplementationIF ft : ftmanagers) {
        ft.install(this);
      }
    }
    super.open();

    if (maintainFulltextIndexes) {
      for (FulltextImplementationIF ft : ftmanagers) {
        if (alwaysReindexOnLoad) {
          ft.reindex();
        } else {
          ft.synchronize(store);
        }
      }
    }
  }

  @Override
  protected TopicMapIF loadTopicMap(boolean readonly) throws IOException {
    // create topic map importer
    TopicMapReaderIF reader = getImporter();

    // create empty topic map
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    
    if (base_address != null) {
      store.setBaseAddress(base_address);
    }
    TopicMapIF tm = store.getTopicMap();

    // import file into topic map
    reader.importInto(tm);

    // suppress duplicates
    if (getDuplicateSuppression()) {
      DuplicateSuppressionUtils.removeDuplicates(tm);
    }

    if (maintainFulltextIndexes) {
      for (FulltextImplementationIF ft : ftmanagers) {
        ft.storeOpened(store);
      }
    }

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

  @Override
  public synchronized void delete() {
    if (source == null) {
      throw new UnsupportedOperationException("This reference cannot be deleted as it does not belong to a source.");
    }
    if (!source.supportsDelete()) {
      throw new UnsupportedOperationException("This reference cannot be deleted as the source does not allow deleting.");
    }

    deleteFullTextIndex();
    super.delete();
  }

  @Override
  public synchronized void close() {
    if (isopen && maintainFulltextIndexes) {
      for (FulltextImplementationIF ft : ftmanagers) {
        ft.close();
      }
    }
    super.close();
  }
  
  /**
   * INTERNAL: Synchronizes the underlying fulltext index with the latest
   * changes in the topic map.
   * @since 5.4.0
   */
  public void synchronizeFulltextIndex(TopicMapStoreIF store) {
    if (maintainFulltextIndexes) {
      for (FulltextImplementationIF ft : ftmanagers) {
        ft.synchronize(store);
      }
    }
  }
  
  /**
   * PUBLIC: Triggers a full reindexing of the topicmap if full-text indexing is enabled.
   * @since 5.4.0
   */
  public void reindexFulltextIndex() {
    if (maintainFulltextIndexes) {
      if (!isopen) {
        open();
      }
      for (FulltextImplementationIF ft : ftmanagers) {
        ft.reindex();
      }
    }
  }

  /**
   * PUBLIC: Removes the full-text index of the topicmap if full-text indexing is enabled.
   * @since 5.4.0
   */
  public void deleteFullTextIndex() {
    if (maintainFulltextIndexes) {
      if (!isopen) {
        open();
      }
      for (FulltextImplementationIF ft : ftmanagers) {
        ft.deleteIndex();
      }
    }
  }

  // --------------------------------------------------------------------------
  // TransactionEventListenerIF implementation
  // --------------------------------------------------------------------------

  @Override
  public void transactionCommit(TopicMapTransactionIF transaction) {
    synchronizeFulltextIndex(transaction.getStore());
  }

  @Override
  public void transactionAbort(TopicMapTransactionIF transaction) {
    synchronizeFulltextIndex(transaction.getStore());
  }

  // --------------------------------------------------------------------------
  // Abstract methods
  // --------------------------------------------------------------------------

  protected abstract TopicMapReaderIF getImporter() throws IOException;
  
}
