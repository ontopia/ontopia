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
