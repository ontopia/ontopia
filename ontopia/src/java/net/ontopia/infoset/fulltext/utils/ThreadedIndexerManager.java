
// $Id: ThreadedIndexerManager.java,v 1.11 2005/07/08 13:29:48 grove Exp $

package net.ontopia.infoset.fulltext.utils;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import net.ontopia.infoset.fulltext.core.DocumentIF;
import net.ontopia.infoset.fulltext.core.DocumentProcessorIF;
import net.ontopia.infoset.fulltext.core.IndexerIF;
import net.ontopia.utils.OntopiaRuntimeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import EDU.oswego.cs.dl.util.concurrent.Callable;
import EDU.oswego.cs.dl.util.concurrent.LinkedQueue;
import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;
import EDU.oswego.cs.dl.util.concurrent.QueuedExecutor;
import EDU.oswego.cs.dl.util.concurrent.ThreadFactory;
import EDU.oswego.cs.dl.util.concurrent.TimedCallable;
import EDU.oswego.cs.dl.util.concurrent.TimeoutException;
  
/**
 * INTERNAL: A standalone index manager that uses threads and in-memory
 * queues to manage document processing and indexing processes.<p>
 *
 * <b>Warning:</b> The effects of this class are asynchronous, so
 * indexing might not happen right away even though the index, delete
 * and flush method have returned.<p>
 *
 * <b>Warning:</b> The delete() method is not supported. Call the
 * method on the nested indexer instead.<p>
 */

public class ThreadedIndexerManager implements IndexerIF {

  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(ThreadedIndexerManager.class.getName());
  
  protected IndexerIF se_indexer;
  protected DocumentProcessorIF doc_processor;
  
  protected QueuedExecutor indexer;
  protected PooledExecutor processor;  

  protected int timeout = 60000;
  protected int max_threads = 10;
  // protected int min_threads = 1;
  protected ThreadFactory processor_thread_factory;
  
  protected Collection non_processable = new Vector();
  protected Collection non_indexable = new Vector();
  
  protected int total;
  
  protected int processing = 0;
  protected int indexing = 0;
  
  protected int processed = 0;
  protected int indexed = 0;

  boolean shutting_down = false;

  /**
   * Creates the manager and gives it the indexer used to do the actual
   * indexing.
   */
  public ThreadedIndexerManager(IndexerIF se_indexer) {
    this(se_indexer, null, null);
  }

  /**
   * INTERNAL:
   */
  public ThreadedIndexerManager(IndexerIF se_indexer, PooledExecutor _processor, QueuedExecutor _indexer) {
    this.se_indexer = se_indexer;

    this.processor_thread_factory = new DefaultThreadFactory("processor", false);
    
    if (_processor != null)
      this.processor = _processor;
    else {    
      // Instantiate document processor pool
      processor = new PooledExecutor(new LinkedQueue()); // new BoundedBuffer(20));
      processor.setThreadFactory(processor_thread_factory);
      //processor.setMaximumPoolSize(max_threads);
      processor.setMinimumPoolSize(max_threads);
      
      // processor.waitWhenBlocked();
      // processor.setKeepAliveTime(5); // die after 5 seconds
      // processor.createThreads(5);

      
    }

    if (_indexer != null)
      this.indexer = _indexer;
    else {
      // Instantiate document indexer queue
      indexer = new QueuedExecutor(new LinkedQueue());
      indexer.setThreadFactory(new DefaultThreadFactory("indexer", false));
    }    
  }

  /**
   * INTERNAL: Gets the document processor used by the indexer manager.
   */
  public DocumentProcessorIF getDocumentProcessor() {
    return doc_processor;
  }

  /**
   * INTERNAL: Sets the document processor which is to be used by the
   * indexer manager.
   */
  public void setDocumentProcessor(DocumentProcessorIF doc_processor) {
    this.doc_processor = doc_processor;
  }

  /**
   * INTERNAL: Gets the document processor thread factory.
   */
  public ThreadFactory getProcessorThreadFactory() {
    return processor_thread_factory;
  }

  /**
   * INTERNAL: Sets the document processor thread factory.
   */
  public void setProcessorThreadFactory(ThreadFactory thread_factory) {
    this.processor_thread_factory = thread_factory;
  }

  /**
   * INTERNAL: Gets the document processor timeout (milliseconds). A
   * document processor is interrupted if its execution time exceeds
   * the size of the timeout.
   */
  public int getProcessorTimeout() {
    return timeout;
  }

  /**
   * INTERNAL: Sets the document processor timeout (milliseconds).
   */
  public void setProcessorTimeout(int timeout) {
    this.timeout = timeout;
  }

  /**
   * INTERNAL: Returns the maximum number of threads allowed in the
   * thread pool. (default 10).
   */
  public int getMaxThreads() {
    return max_threads;
  }

  /**
   * INTERNAL: Sets the maximum number of threads allowed in the thread
   * pool.
   */
  public void setMaxThreads(int max_threads) {
    this.max_threads = max_threads;
  }

  // /**
  //  * INTERNAL: Returns the minimum number of threads to use with the thread pool.
  //  */
  // public int getMinThreads() {
  //   return min_threads;
  // }
  // 
  // /**
  //  * INTERNAL: Sets the minimum number of threads to use with the thread pool.
  //  */
  // public void setMinThreads(int min_threads) {
  //   this.min_threads = min_threads;
  // }
  
  class DefaultThreadFactory implements ThreadFactory {
    protected ThreadGroup group;
    protected boolean daemon;
    DefaultThreadFactory(String group_name, boolean daemon) {
      this.group = new ThreadGroup(group_name);
      this.daemon = daemon;
    }
    public Thread newThread(Runnable command) {
      Thread thread = new DefaultThread(group, command);
      if (daemon) thread.setDaemon(true);
      return thread;
    }

    class DefaultThread extends Thread {
      protected Runnable runnable;
      DefaultThread(ThreadGroup group, Runnable runnable) {
        super(group, runnable);
        this.runnable = runnable;
      }
      public String toString() {
        return super.toString() + " (" + runnable.toString() + ")";
      }
    }
  }
  
  public synchronized void index(DocumentIF document) {
    // Check to see if the manager isn't already closed.
    if (shutting_down)
      throw new OntopiaRuntimeException("The indexer manager is closed.");

    total++;
    startProcess(document);
  }
  
  public synchronized int delete(String field, String value) {
    // Check to see if the manager isn't already closed.
    if (shutting_down)
      throw new OntopiaRuntimeException("The indexer manager is closed.");

    // Delete documents with the specified field value
    try {
      indexer.execute(new Deleter(field, value));
    } catch (InterruptedException e) {
      log.error("Couldn't delete document(s) (interrupted).");
    }

    // Since the deletion is asynchronous we don't really know how
    // many documents were deleted.
    return -1;
  }

  public synchronized void flush() {
    // Check to see if the manager isn't already closed.
    if (shutting_down)
      throw new OntopiaRuntimeException("The indexer manager is closed.");
    
    // Flush indexer
    try {
      indexer.execute(new Runnable() {
          public void run() {
            try {
              log.info("Flushing the index.");
              se_indexer.flush();
            } catch (IOException e) {
              log.error("Couldn't flush index: " + e.toString());
            }
          }
        });
    } catch (InterruptedException e) {
      log.error("Couldn't flush index (interrupted).");
    }
  }
  
  public void delete() throws IOException {
    throw new UnsupportedOperationException("IndexerIF.close() is not supported.");
  }

  public synchronized void close() {
    // Check to see if the manager isn't already closed.
    if (shutting_down)
      throw new OntopiaRuntimeException("Indexer manager has already been closed.");

    // Set the shutdown flag
    shutting_down = true;
    
    // Put shutdown task onto processor queue
    try {
      processor.execute(new ShutdownProcessor());
    } catch (InterruptedException e) {
      log.error("ShutdownProcessor interrupted [1]: " + e.toString());
    }
  }

  class ShutdownProcessor implements Runnable {
    public void run() {
      try {
        // Wait until all queued task are finished. Note that the
        // processor pool cannot be modified anymore because of the
        // shutting_down flag.
        while (!(processor.getPoolSize() == 1)) {
          Thread.currentThread().sleep(200);
        }

        // Tell processor to stop accepting tasks
        processor.shutdownAfterProcessingCurrentlyQueuedTasks();

        log.info("Processor shut down.");
	
        // Put shutdown task onto indexer queue
        indexer.execute(new ShutdownIndexer());
      } catch (InterruptedException e) {
        log.error("ShutdownProcessor interrupted [2]: " + e.toString());
      } finally {      
        // Tell indexer to stop accepting tasks
        indexer.shutdownAfterProcessingCurrentlyQueuedTasks();
      }
    }
  }

  class ShutdownIndexer implements Runnable {
    public void run() {
      try {
        // Flush the index    
        se_indexer.flush();
        // Close the indexer
        se_indexer.close();
      } catch (IOException e) {
        log.warn("Problems occurred when closing the indexer.");
      }
      log.info("Indexer shut down.");

      // Dump some statistics
      if (log.isDebugEnabled()) {
        status();
        endStatus();
      }
    }
  }
  
  class Indexer implements Runnable {

    protected DocumentIF document;
    
    Indexer(DocumentIF document) {
      this.document = document;
    }
    
    public void run() {
      // Index document
      boolean success = false;
      try {
        se_indexer.index(document);
        success = true;
        if (log.isDebugEnabled()) log.debug("Document successfully indexed: " + document);
      } catch (Exception e) {
        log.error("Cannot index document: " + document + " " + e.toString());
        non_indexable.add(document);
      } finally {
        endIndex(document, success);
      }
    }
  }
  
  class Deleter implements Runnable {

    protected String field;
    protected String value;
    
    Deleter(String field, String value) {
      this.field = field;
      this.value = value;
    }
    
    public void run() {
      try {
        log.info("Deleting document(s) ("+ field + "='" + value + "'.");
        se_indexer.delete(field, value);
      } catch (IOException e) {
        log.error("Couldn't delete document(s).");
      }
    }
  }
  
  class Processor implements Runnable {

    protected DocumentIF document;
    protected boolean success = false;
    
    Processor(DocumentIF document) {
      this.document = document;
    }
    
    public void run() {
      // Process the document
      try {
        // FIXME: not really necesary to use a timer when the timeout is <= 0.
        Callable callable = new DocumentCallable(document);
        if (getProcessorTimeout() > 0) {
          TimedCallable timed = new TimedCallable(callable, getProcessorTimeout());
          timed.setThreadFactory(new DefaultThreadFactory("callables", false));
          timed.call();
        } else {
          callable.call();
        }
        if (log.isDebugEnabled()) log.debug("Document successfully processed: " + document);
      } catch (TimeoutException e) {
        log.error("Timeout: " + document);
      } catch (Exception e) {
        log.error("Cannot process document: " + document + " " + e.toString());
        // e.printStackTrace();
      } finally {
        if (!success) non_processable.add(document);
        endProcess(document, success);
      }	
    }

    class DocumentCallable implements Callable {
      protected DocumentIF document;
      DocumentCallable(DocumentIF document) { this.document = document; }
      public Object call() throws Exception {
        try {
          doc_processor.process(document);
          success = true;
        } catch (Exception e) {
          log.error("Processing error: " + document + " " + e.toString());
          return document;
          //throw e;
        }
        return document;
      }
      public String toString() {
        return document.getField("address").getValue();
      }
    }    
  }
  
  void startProcess(DocumentIF document) {
    if (doc_processor != null && doc_processor.needsProcessing(document)) {
      processing++;
      try {
        // Add processor on processor queue
        if (log.isDebugEnabled()) log.debug("Document added to processing queue: " + document);
        processor.execute(new Processor(document));
      } catch (InterruptedException e) {
        log.error("Processor interrupted.");
      }
    } else {
      startIndex(document);
    }
  }
  
  void endProcess(DocumentIF document, boolean success) {
    processing--;
    // Index the document
    if (success) {
      processed++;
      startIndex(document);
    }
  }
    
  void startIndex(DocumentIF document) {
    indexing++;
    try {
      if (log.isDebugEnabled()) log.debug("Document added to indexing queue: " + document);
      indexer.execute(new Indexer(document));
    } catch (InterruptedException e) {
      log.error("Indexer interrupted.");
    }
  }
    
  void endIndex(DocumentIF document, boolean success) {
    indexing--;
    if (success) indexed++;
  }

  /**
   * INTERNAL: Outputs execution status information to log4j.
   */
  public void status() {
    log.debug("Total: " + total + " Indexed: " + indexed + " Processed: " + processed
              + " Indexing: " + indexing + " Processing: " + processing);
    log.debug("Non-indexable: " + non_indexable.size() + " Non-processable: " + non_processable.size());
    log.debug("Processor: " + processor.getPoolSize());

    log.debug("Threads: " + Thread.currentThread().getThreadGroup().activeCount());
    // log.debug("Indexer: " + indexer.getThread().isAlive());
  }

  /**
   * INTERNAL: Outputs thread status information to log4j.
   */
  public void threadStatus() {
    ThreadGroup group = Thread.currentThread().getThreadGroup();
    Thread[] threads = new Thread[group.activeCount()];
    group.enumerate(threads);
    for (int i = 0; i < threads.length; i++) {
      Thread thread = threads[i];
      log.debug("Thread: " + thread);
    }

  }
    
  /**
   * INTERNAL: Outputs post execution status information to log4j.
   */
  public void endStatus() {
    log.debug("Non-indexable: " + non_indexable.size() + "; " + non_indexable);
    log.debug("Non-processable: " + non_processable.size() + "; " + non_processable);	
  }

  /**
   * INTERNAL: Gets the document processor executor that is used.
   */
  public PooledExecutor getProcessorExecutor() {
    return processor;
  }
  
  /**
   * INTERNAL: Sets the document processor executor that is to be used.
   */
  public void setProcessorExecutor(PooledExecutor executor) {
    this.processor = executor;
  }
  
}
