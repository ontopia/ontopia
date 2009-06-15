
// $Id: RDBMSSearchResult.java,v 1.7 2005/07/07 13:15:08 grove Exp $

package net.ontopia.infoset.fulltext.impl.rdbms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ontopia.infoset.fulltext.core.DocumentIF;
import net.ontopia.infoset.fulltext.core.SearchResultIF;
import net.ontopia.persistence.proxy.QueryResultIF;
  
/**
 * INTERNAL: RDBMS search result implementation.<p>
 */

public class RDBMSSearchResult implements SearchResultIF {

  // TODO: Should make the result traversal lazy, so that all the work
  // does not have to be done upfront.
  
  protected List docs;
  protected float[] scores;
  
  RDBMSSearchResult(QueryResultIF result, String[] fnames) {
    // Retrieve documents
    this.docs = new ArrayList();    
    Object[] row = new Object[fnames.length];
    while (result.next()) {
      result.getValues(row);
      // Produce fields
      Map fields = new HashMap(fnames.length);
      for (int i=0; i < fnames.length-1; i++) { // skip last, since it has the score
        fields.put(fnames[i], new RDBMSField(fnames[i], (String)row[i]));
      }
      // Get document score
      float score = ((Float)row[fnames.length-1]).floatValue();

      // Create document
      docs.add(new RDBMSDocument(fields, score));
    }
    // Close query result
    result.close();
  }
  
  public DocumentIF getDocument(int hit) throws IOException {
    return (DocumentIF)docs.get(hit);
  }

  public float getScore(int hit) throws IOException {
    RDBMSDocument doc = (RDBMSDocument)docs.get(hit);
    if (doc == null)
      return 0f;
    else
      return doc.getScore();
  }

  public int hits() {
    return docs.size();
  }
    
}
