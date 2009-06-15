
// $Id: AbstractOntopolyURLReference.java,v 1.3 2008/06/11 16:55:57 geir.gronmo Exp $

package net.ontopia.topicmaps.entry;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.fulltext.core.IndexerIF;
import net.ontopia.infoset.fulltext.core.SearcherIF;
import net.ontopia.infoset.fulltext.impl.lucene.LuceneIndexer;
import net.ontopia.infoset.fulltext.impl.lucene.LuceneSearcher;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.TopicMapWriterIF;
import net.ontopia.topicmaps.core.TopicMapImporterIF;
import net.ontopia.topicmaps.impl.utils.TopicMapTransactionIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.impl.utils.FulltextIndexManager;
import net.ontopia.topicmaps.impl.utils.TransactionEventListenerIF;
import net.ontopia.topicmaps.utils.DuplicateSuppressionUtils;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.OntopiaRuntimeException;

import org.apache.lucene.store.FSDirectory;

/**
 * INTERNAL: Common abstract superclass for references from sources
 * that support what Ontopoly needs.
 */
public abstract class AbstractOntopolyURLReference
  extends AbstractURLTopicMapReference
  implements TransactionEventListenerIF {
  protected FulltextIndexManager ftmanager;
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

    // create empty topic map
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    if (base_address != null)
      store.setBaseAddress(base_address);
    TopicMapIF tm = store.getTopicMap();

    // register fulltext index manager at this point, so that we can track
    // all events that occur at loading
    boolean replaceIndex = true;
    
    if (getMaintainFulltextIndexes())
      // if index is not to be replaced, defer registration until after import
      this.ftmanager = FulltextIndexManager.manageTopicMap(tm);

    // import file into topic map
    reader.importInto(tm);

    // suppress duplicates
    if (getDuplicateSuppression())
      DuplicateSuppressionUtils.removeDuplicates(tm);

    // deferred fulltext index manager registration
    if (getMaintainFulltextIndexes()) {
      //! if (!replaceIndex)
      //!   this.ftmanager = FulltextIndexManager.manageTopicMap(tm);

      // synchronize fulltext index
      synchronizeFulltextIndex(true);

      File ixdir = new File(getIndexDirectory(), getId());
      ftmanager.setLuceneDirectory(FSDirectory.getDirectory(ixdir, false));
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

  /**
   * INTERNAL: Synchronizes the underlying fulltext index with the latest
   * changes in the topic map.
   * 
   * @return True if index was modified.
   */
  public boolean synchronizeFulltextIndex() throws IOException {
    if (getMaintainFulltextIndexes())
      return synchronizeFulltextIndex(false);
    else
      return false;
  }

  protected synchronized boolean synchronizeFulltextIndex(boolean replaceIndex)
      throws IOException {
    
    File ixdir = new File(getIndexDirectory(), getId());
    IndexerIF indexer = null;
    boolean modified = false;
    
    try {
      if (replaceIndex)
        indexer = new LuceneIndexer(ixdir.getPath(), replaceIndex);

      if (ftmanager != null && ftmanager.needSynchronization()) {      
        if (indexer == null)
          indexer = new LuceneIndexer(ixdir.getPath(), replaceIndex);

        modified = ftmanager.synchronizeIndex(indexer);
        if (modified) indexer.flush();      
      }
    } finally {
      if (indexer != null) indexer.close();
    }
    return modified;
  }

  public SearcherIF getSearcher() {
    if (getMaintainFulltextIndexes()) {
      File ixdir = new File(getIndexDirectory(), getId());
      try {
        return new LuceneSearcher(ixdir.getPath());
      } catch (IOException e) {
        throw new OntopiaRuntimeException(e);
      }
    } else
      return null;
  }

  public synchronized void close() {
    super.close();
    // close and dereference ftmanager
    try {
      if (ftmanager != null) {
        ftmanager.close();
        ftmanager = null;
      }
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    }
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
        File ixdir = new File(getIndexDirectory(), getId());
        if (ixdir.exists())
          FileUtils.deleteDirectory(ixdir, true);
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
      synchronizeFulltextIndex();
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  public void transactionAbort(TopicMapTransactionIF transaction) {
    // synchronize fulltext index
    try {
      synchronizeFulltextIndex();
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  // --------------------------------------------------------------------------
  // Abstract methods
  // --------------------------------------------------------------------------

  protected abstract TopicMapImporterIF getImporter();
  
}
