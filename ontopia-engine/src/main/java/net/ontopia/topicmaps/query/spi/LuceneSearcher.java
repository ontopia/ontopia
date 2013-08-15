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

package net.ontopia.topicmaps.query.spi;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import net.ontopia.infoset.fulltext.impl.lucene.OmnigatorAnalyzer;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * EXPERIMENTAL: Lucene searcher implementation.<p>
 */

public class LuceneSearcher extends AbstractSearcher {

  /**
   * PUBLIC: The mandatory default constructor.
   */
  public LuceneSearcher() {
  }
  
  public int getValueType() {
    return SearcherIF.OBJECT_ID;
  }

  public SearchResultIF getResult(String query) {
    return new LuceneSearchResult(query);
  }

  private class LuceneSearchResult extends AbstractSearchResult {

    protected String valueField = "object_id";
    protected String defaultField = "content";
    protected Analyzer analyzer = OmnigatorAnalyzer.INSTANCE;

    protected IndexSearcher searcher;
    protected Hits hits;
    protected Document doc;
    protected int c = -1;
    
    LuceneSearchResult(String query) {
      try {
        Query q = new QueryParser(defaultField, analyzer).parse(query);
        this.searcher = new IndexSearcher(getDirectory());
        this.hits = this.searcher.search(q);
      } catch (Exception e) {
        throw new OntopiaRuntimeException(e);
      }
      // get overridden parameters
      if (parameters != null) {
        if (parameters.containsKey("valueField"))
          this.valueField = (String)parameters.get("valueField");
        if (parameters.containsKey("defaultField"))
          this.defaultField = (String)parameters.get("defaultField");
        if (parameters.containsKey("analyzer")) {
          String analyzerClassName = (String)parameters.get("analyzer");
          Class analyzerClass = null;
          try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            analyzerClass = Class.forName(analyzerClassName, true, classLoader);
          } catch (Exception e) {
            throw new OntopiaRuntimeException("Lucene analyzer class '" + analyzerClassName + "' cannot be found.", e);
          }
          try {
            this.analyzer = (Analyzer)analyzerClass.newInstance();
          } catch (Exception e) {
            throw new OntopiaRuntimeException("Lucene analyzer class '" + analyzerClassName + "' cannot be instantiated.", e);
          }
        }
      }
    }

    protected Directory getDirectory() throws IOException {
      String path = (String)parameters.get("index");
      return FSDirectory.getDirectory(path, false);
    }
    
    public boolean next() {
      if (c+1 >= hits.length())
        return false;
      try {
        c++;      
        this.doc = hits.doc(c);
        return true;
      } catch (Exception e) {
        throw new OntopiaRuntimeException(e);
      }
    }
    
    public Object getValue() {
      Field field = doc.getField(valueField);
      return field.stringValue();
    }
    
    public float getScore() {
      try {
        return hits.score(c);
      } catch (Exception e) {
        throw new OntopiaRuntimeException(e);
      }
    }
    
    public void close() {
      try {
        this.searcher.close();
      } catch (Exception e) {
        throw new OntopiaRuntimeException(e);
      }
    }
  };

}
