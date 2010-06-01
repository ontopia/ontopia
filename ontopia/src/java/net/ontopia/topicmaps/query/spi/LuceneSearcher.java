// $Id: LuceneSearcher.java,v 1.3 2006/06/07 11:12:33 grove Exp $

package net.ontopia.topicmaps.query.spi;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import net.ontopia.topicmaps.core.TopicMapIF;
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
            analyzerClass = Class.forName(analyzerClassName);
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
