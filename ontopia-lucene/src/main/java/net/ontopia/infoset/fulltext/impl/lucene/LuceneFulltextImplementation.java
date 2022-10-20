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
package net.ontopia.infoset.fulltext.impl.lucene;

import java.io.File;
import java.io.IOException;
import java.util.WeakHashMap;
import net.ontopia.infoset.fulltext.core.FulltextImplementationIF;
import net.ontopia.infoset.fulltext.core.SearchResultIF;
import net.ontopia.infoset.fulltext.core.SearcherIF;
import net.ontopia.infoset.fulltext.topicmaps.DefaultTopicMapDocumentGenerator;
import net.ontopia.infoset.fulltext.topicmaps.TopicMapIteratorGenerator;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.AbstractOntopolyURLReference;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.impl.utils.AbstractIndexManager;
import net.ontopia.topicmaps.impl.utils.FulltextIndexManager;
import net.ontopia.utils.OntopiaRuntimeException;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FulltextImplementationIF implementation that is based on Lucene indexing.
 * @since 5.4.0
 */
public class LuceneFulltextImplementation implements FulltextImplementationIF {

  private static Logger logger = LoggerFactory.getLogger(LuceneFulltextImplementation.class);

  private static final Object READER_LOCK = new Object();
  private static final Analyzer ANALYZER = new StopAnalyzer();

  private final WeakHashMap<TopicMapStoreIF, FulltextIndexManager> managers = new WeakHashMap<>();

  private File directoryFile;
  private Directory directory = null;
  private IndexReader reader;
  private TopicMapReferenceIF reference;
  private String defaultField = "content";

  @Override
  public synchronized void install(TopicMapReferenceIF reference) {
    this.reference = reference;
    if (reference instanceof AbstractOntopolyURLReference) {
      AbstractOntopolyURLReference ref = (AbstractOntopolyURLReference) reference;

      String indexDirectory = ref.getIndexDirectory();
      if ((indexDirectory == null) || (indexDirectory.trim().isEmpty())) {
        throw new OntopiaRuntimeException("Reference " + ref.getId() + " was marked as fulltext indexable, but 'indexDirectory' configuration is missing");
      }
      directoryFile = new File(indexDirectory + File.separatorChar + ref.getId());
    }
  }

  @Override
  public synchronized void storeOpened(TopicMapStoreIF store) {
    if (store instanceof InMemoryTopicMapStore) {
      InMemoryTopicMapStore memory = (InMemoryTopicMapStore) store;
      if (!managers.containsKey(memory)) {
        managers.put(memory, new FulltextIndexManager(memory));
        ((AbstractIndexManager) memory.getTransaction().getIndexManager())
                .registerIndex("net.ontopia.infoset.fulltext.core.SearcherIF", new LuceneSearcher());
      }
    }
  }

  @Override
  public synchronized void synchronize(TopicMapStoreIF store) {
    if (managers.containsKey(store)) {
      try {
        try (IndexWriter writer = getWriter()) {
          managers.get(store).synchronizeIndex(new LuceneIndexer(writer));
        }
        closeReader();
      } catch (IOException ioe) {
        throw new OntopiaRuntimeException("Could not synchronize fulltext index for topicmap " + reference.getId() + ": " + ioe.getMessage(), ioe);
      }
    }
  }

  @Override
  public synchronized void reindex() {
    deleteIndex();
    
    try (TopicMapStoreIF store = reference.createStore(true)) {
      try (IndexWriter writer = getWriter()) {
        new TopicMapIteratorGenerator(store.getTopicMap(), new LuceneIndexer(writer), DefaultTopicMapDocumentGenerator.INSTANCE)
                .generate();
      }
      closeReader();
    } catch (IOException ioe) {
      throw new OntopiaRuntimeException("Could not fulltext reindex topicmap " + reference.getId() + ": " + ioe.getMessage(), ioe);
    }
  }

  @Override
  public synchronized void deleteIndex() {
    close();

    if ((directoryFile != null) && (directoryFile.exists())) {
      try {
        FileUtils.deleteDirectory(directoryFile);
      } catch (IOException ioe) {
        throw new OntopiaRuntimeException("Could not delete lucene index directory: " + ioe.getMessage(), ioe);
      }
    }
  }

  @Override
  public synchronized void close() {
    managers.clear();
    closeReader();
    closeDirectory();
  }

  private void openReader() {
    if (reader == null) {
      synchronized (READER_LOCK) {
        try {
          openDirectory();
          reader = DirectoryReader.open(directory);
        } catch (IOException ioe) {
          throw new OntopiaRuntimeException("Could not open lucene index directory: " + ioe.getMessage(), ioe);
        }
      }
    }
  }

  private synchronized IndexWriter getWriter() {
    openDirectory();
    try {
      return new IndexWriter(directory, new IndexWriterConfig(ANALYZER));
    } catch (IOException ioe) {
      throw new OntopiaRuntimeException("Could not open lucene index writer: " + ioe.getMessage(), ioe);
    }
  }

  private synchronized void openDirectory() {
    if (directory == null) {
      try {
        directory = FSDirectory.open(directoryFile.toPath());
      } catch (IOException ioe) {
        throw new OntopiaRuntimeException("Could not open lucene index reader: " + ioe.getMessage(), ioe);
      }
    }
  }

  private void closeReader() {
    if (reader != null) {
      synchronized (READER_LOCK) {
        try {
          reader.close();
          reader = null;
        } catch (IOException ioe) {
          throw new OntopiaRuntimeException("Could not close lucene reader " + ioe.getMessage(), ioe);
        }
      }
    }
  }

  private synchronized void closeDirectory() {
    if (directory != null) {
      try {
        directory.close();
        directory = null;
      } catch (IOException ioe) {
        throw new OntopiaRuntimeException("Could not close lucene directory " + ioe.getMessage(), ioe);
      }
    }
  }

  private class LuceneSearcher implements SearcherIF {

    @Override
    public SearchResultIF search(String query) throws IOException {
      synchronized (READER_LOCK) {
        openReader();
        IndexSearcher searcher = new IndexSearcher(reader);

        try {
          logger.debug("Searching for: '" + query + "'");
          Query _query = new QueryParser(defaultField, ANALYZER).parse(query);
          return new LuceneSearchResult(searcher, searcher.search(_query, Integer.MAX_VALUE));
        } catch (org.apache.lucene.queryparser.classic.ParseException e) {
          logger.error("Error parsing query: '" + e.getMessage() + "'");
          throw new IOException(e.getMessage(), e);
        }
      }
    }

    @Override
    public void close() throws IOException {
      closeReader();
    }
  }

  public String getDefaultField() {
    return defaultField;
  }

  public void setDefaultField(String defaultField) {
    this.defaultField = defaultField;
  }

  // methods for testing
  public void setDirectoryFile(File directoryFile) {
    this.directoryFile = directoryFile;
  }

  public SearcherIF getSearcher() {
    return new LuceneSearcher();
  }
}
