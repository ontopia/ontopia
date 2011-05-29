
package net.ontopia.infoset.fulltext.topicmaps;

import java.io.IOException;

import net.ontopia.infoset.fulltext.core.IndexerIF;
import net.ontopia.infoset.fulltext.utils.DefaultIndexerManager;
import net.ontopia.infoset.fulltext.utils.DocumentPreloaderProcessor;
import net.ontopia.infoset.fulltext.utils.Locator2ContentProcessor;
import net.ontopia.infoset.fulltext.utils.ThreadedIndexerManager;
import net.ontopia.infoset.utils.DiskPreloader;
import net.ontopia.infoset.utils.URLLocatorReaderFactory;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.VariantNameIF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: The default topic map indexer manager. This indexer is
 * preconfigured in a way that it should be suitable for most cases
 * when topic maps need to be indexed.<p>
 *
 * If external resources is not to be indexed the {@link
 * net.ontopia.infoset.fulltext.utils.DefaultIndexerManager} indexer
 * manager is used internally, otherwise the following
 * configuration:<p>
 *
 * <ul>
 *   <li>Uses the default {@link net.ontopia.infoset.fulltext.utils.ThreadedIndexerManager} 
 *       internally, except that it has its document processor timeout set to 30
 *       seconds.</li>
 *   <li>The document processor configuration is a document preloader that downloads
 *       external resources and stores them in the "contents" field.</li>
 *   <li>Uses the {@link DefaultTopicMapDocumentGenerator} to generate DocumentIFs for the
 *       topic map objects.</li>
 * </ul>
 */

public class DefaultTopicMapIndexer {

  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(DefaultTopicMapIndexer.class.getName());

  protected IndexerIF indexer;
  protected boolean index_external;

  protected IndexerIF imanager;
  protected TopicMapDocumentGeneratorIF docgen;

  /**
   * Creates a default topic map indexer. External resources are not indexed.
   * @param indexer The indexer that will do the actual indexing.
   */
  public DefaultTopicMapIndexer(IndexerIF indexer) {
    this(indexer, false, null);
  }
  
  /**
   * Creates a topic map indexer.
   * @param indexer The indexer that will do the actual indexing.
   * @param index_external Whether external resources will be indexed.
   */
  public DefaultTopicMapIndexer(IndexerIF indexer, boolean index_external, String preloaddir) {
    this.indexer = indexer;
    this.index_external = index_external;
    
    // Use the default indexer manager.
    if (!index_external) {
      imanager = new DefaultIndexerManager(indexer);
      log.info("Using default indexer manager.");
    }
    // Configure index manager for downloading and indexing of external resources
    else {      
      
      // Set up the indexer configuration
      ThreadedIndexerManager _imanager = new ThreadedIndexerManager(indexer);
      _imanager.setProcessorTimeout(30000);
      
      // Setup document processors that downloads external resources
      if (index_external) {
        // Create disk preloader
        DiskPreloader preloader = new DiskPreloader(preloaddir, new URLLocatorReaderFactory());

        // Create locator preloader processor
        DocumentPreloaderProcessor pl_processor = new DocumentPreloaderProcessor(preloader);
	
        // Create locator to content processor
        Locator2ContentProcessor l2c_processor = new Locator2ContentProcessor(preloader);
        l2c_processor.setNotationField(pl_processor.getPreloadedNotationField());
        l2c_processor.setAddressField(pl_processor.getPreloadedAddressField());

        // Set the locator to content processor as a post-processor of the preloader processor
        pl_processor.setPostProcessor(l2c_processor);
	
        // Register processor(s) with indexer manager
        _imanager.setDocumentProcessor(pl_processor);
      }
      this.imanager = _imanager;
      log.info("Using threaded indexer manager.");
    }
    
    // Create document generator
    docgen = DefaultTopicMapDocumentGenerator.INSTANCE;
  }

  /**
   * INTERNAL: Gets the nested indexer manager.
   */
  public IndexerIF getNestedIndexer() {
    return imanager;
  }
  
  /**
   * INTERNAL: Indexes the given topic map.
   */
  public void index(TopicMapIF topicmap) throws IOException {
      
    // Create event generator
    TopicMapIteratorGenerator tmieg = new TopicMapIteratorGenerator();
    tmieg.setTopicMap(topicmap);
    tmieg.setIndexer(imanager);
    tmieg.setDocumentGenerator(docgen);
    
    // Trigger event generation and indexing process
    tmieg.generate();
    
  }
  
  /**
   * INTERNAL: Indexes the given topic name.
   */
  public void index(TopicNameIF name) throws IOException {
    imanager.index(docgen.generate(name));
  }
    
  /**
   * INTERNAL: Indexes the given variant name.
   */
  public void index(VariantNameIF variant) throws IOException {
    imanager.index(docgen.generate(variant));
  }
    
  /**
   * INTERNAL: Indexes the given occurrence.
   */
  public void index(OccurrenceIF occurs) throws IOException {
    imanager.index(docgen.generate(occurs));
  }
    
  /**
   * INTERNAL: Deletes the given topic map object.
   */
  public void delete(TMObjectIF tmobject) throws IOException {
    imanager.delete("object_id", tmobject.getObjectId());
  }

  /**
   * INTERNAL: Flushes the index.
   */
  public void flush() throws IOException {
    // Flush the index
    imanager.flush();
  }
  
  /**
   * INTERNAL: Closes the indexer manager. Note that the wrapped indexer
   * is not closed, only flushed, since this class isn't managing the
   * indexer.
   */
  public void close() throws IOException {
    // Close index manager [also flushes the indexer]
    imanager.close();    
  }
  
}
