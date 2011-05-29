
package net.ontopia.infoset.fulltext.impl.lucene;

import java.io.IOException;

import net.ontopia.infoset.fulltext.core.DocumentIF;
import net.ontopia.infoset.fulltext.core.SearchResultIF;

import org.apache.lucene.search.Hits;
  
/**
 * INTERNAL: Lucene search result wrapper implementation.<p>
 */

public class LuceneSearchResult implements SearchResultIF {

  protected Hits hits;
  
  LuceneSearchResult(Hits hits) {
    this.hits = hits;
  }
  
  public DocumentIF getDocument(int hit) throws IOException {
    return new LuceneDocument(hits.doc(hit));
  }

  public float getScore(int hit) throws IOException {
    return hits.score(hit);
  }

  public int hits() {
    return hits.length();
  }
    
}
