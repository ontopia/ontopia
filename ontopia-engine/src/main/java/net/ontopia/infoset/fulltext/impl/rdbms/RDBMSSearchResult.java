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

package net.ontopia.infoset.fulltext.impl.rdbms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ontopia.infoset.fulltext.core.DocumentIF;
import net.ontopia.infoset.fulltext.core.FieldIF;
import net.ontopia.infoset.fulltext.core.SearchResultIF;
import net.ontopia.persistence.proxy.QueryResultIF;
  
/**
 * INTERNAL: RDBMS search result implementation.<p>
 */

public class RDBMSSearchResult implements SearchResultIF {

  // TODO: Should make the result traversal lazy, so that all the work
  // does not have to be done upfront.
  
  protected List<RDBMSDocument> docs;
  protected float[] scores;
  
  RDBMSSearchResult(QueryResultIF result, String[] fnames) {
    // Retrieve documents
    this.docs = new ArrayList<RDBMSDocument>();    
    Object[] row = new Object[fnames.length];
    while (result.next()) {
      result.getValues(row);
      // Produce fields
      Map<String, FieldIF> fields = new HashMap<String, FieldIF>(fnames.length);
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
  
  @Override
  public DocumentIF getDocument(int hit) throws IOException {
    return docs.get(hit);
  }

  @Override
  public float getScore(int hit) throws IOException {
    RDBMSDocument doc = docs.get(hit);
    if (doc == null) {
      return 0f;
    } else {
      return doc.getScore();
    }
  }

  @Override
  public int hits() {
    return docs.size();
  }
    
}
