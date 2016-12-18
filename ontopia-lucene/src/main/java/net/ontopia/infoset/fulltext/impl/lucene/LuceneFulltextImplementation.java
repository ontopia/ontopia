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

import java.io.IOException;
import net.ontopia.infoset.fulltext.core.FulltextImplementationIF;
import net.ontopia.infoset.fulltext.core.IndexerIF;
import net.ontopia.infoset.fulltext.core.SearcherIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class LuceneFulltextImplementation implements FulltextImplementationIF {

  private Directory directory;
  
  public void initialize(InMemoryTopicMapStore store) throws IOException {
    directory = FSDirectory.open(store.getIndexDirectory());
  }
  
  public SearcherIF getSearcher() throws IOException {
    return new LuceneSearcher(directory);
  }

  public IndexerIF getIndexer(boolean replaceIndex) throws IOException {
    return new LuceneIndexer(directory, replaceIndex);
  }

  public void close() throws IOException {
    if (directory != null) {
      directory.close();
    }
  }
}
