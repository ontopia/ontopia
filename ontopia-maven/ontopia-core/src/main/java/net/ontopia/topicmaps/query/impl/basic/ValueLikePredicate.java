
// $Id: ValueLikePredicate.java,v 1.28 2008/06/11 16:56:01 geir.gronmo Exp $

package net.ontopia.topicmaps.query.impl.basic;

import java.io.IOException;

import net.ontopia.infoset.fulltext.core.IndexerIF;
import net.ontopia.infoset.fulltext.core.SearchResultIF;
import net.ontopia.infoset.fulltext.core.SearcherIF;
import net.ontopia.infoset.fulltext.impl.lucene.LuceneIndexer;
import net.ontopia.infoset.fulltext.impl.lucene.LuceneSearcher;
import net.ontopia.infoset.fulltext.topicmaps.DefaultTopicMapIndexer;
import net.ontopia.infoset.fulltext.topicmaps.TopicMapSearchResult;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.utils.PredicateSignature;
import net.ontopia.topicmaps.query.impl.utils.Prefetcher;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.OntopiaUnsupportedException;
import net.ontopia.utils.StringUtils;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;

import org.apache.lucene.store.RAMDirectory;

/**
 * INTERNAL: Implements the 'value-like' predicate.
 */
public class ValueLikePredicate implements BasicPredicateIF {
  protected TopicMapIF topicmap;
  protected SearcherIF searcher;

  public ValueLikePredicate(TopicMapIF topicmap) {
    this.topicmap = topicmap;
  }
  
  public String getName() {
    return "value-like";
  }

  public String getSignature() {
    return "bov s! f?";
  }
  
  public int getCost(boolean[] boundparams) {
    if (!boundparams[1])
      // cannot run predicate before we have the query
      return PredicateDrivenCostEstimator.INFINITE_RESULT;
    else
      // this is not true, but we want to run the full-text as early as
      // possible, to avoid running it many times. 
      return PredicateDrivenCostEstimator.FILTER_RESULT;
  }

  public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
    throws InvalidQueryException {

    QueryMatches result = new QueryMatches(matches);
    int topicix = result.getIndex(arguments[0]);
    int valueix = result.getIndex(arguments[1]);
    int scoreix = (arguments.length > 2 ? result.getIndex(arguments[2]) : -1);

    PredicateSignature sign = PredicateSignature.getSignature(this);
    sign.verifyBound(matches, arguments, this);
    
    if (matches.bound(topicix))
      throw new InvalidQueryException("First argument to " + getName() + " must " +
                                      "be unbound");
    else if (scoreix >= 0 && matches.bound(scoreix))
      throw new InvalidQueryException("Third argument to " + getName() + " must " +
                                      "be unbound");
    else
      satisfyWithAllUnbound(matches, result, topicix, valueix, scoreix);
    
    return result;
  }

  private void satisfyWithAllUnbound(QueryMatches matches, QueryMatches result,
                                     int topicix, int valueix, int scoreix) {

    // loop over all existing matches
    String previous = StringUtils.VERY_UNLIKELY_STRING;
    TopicMapSearchResult ftresult = null;
    for (int ix = 0; ix <= matches.last; ix++) {

      // loop over all matches for this value
      String value = (String) matches.data[ix][valueix];
      if ("".equals(value))
        continue;
      else if (!previous.equals(value))
        ftresult = search(value);
        
      int length = ftresult.size();
      for (int i=0; i < length; i++) {
        
        if (result.last+1 == result.size) 
          result.increaseCapacity();
        result.last++;
        
        Object[] newRow = (Object[]) matches.data[ix].clone();
        newRow[topicix] = ftresult.get(i);
        if (scoreix >= 0)
          newRow[scoreix] = new Float(ftresult.getScore(i));
        result.data[result.last] = newRow;
      }
    }
    
  }

  private TopicMapSearchResult search(String value) {
    try {
      // Get hold of fulltext index.
      if (this.searcher == null)
        this.searcher = getSearcher(topicmap);

      // search
      SearchResultIF result = this.searcher.search(value);
      
      // prefetch identities
      Prefetcher.prefetch(topicmap, result, "object_id");

      // return result
      return new TopicMapSearchResult(topicmap, result);
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  // FIXME: this is totally unacceptable
  private SearcherIF getSearcher(TopicMapIF topicmap) {

    try {
      return (SearcherIF)topicmap
        .getIndex("net.ontopia.infoset.fulltext.core.SearcherIF");
    } catch (OntopiaUnsupportedException e) {
      // so we don't have an index. use the fallback below
    }

    // FIXME: might move the rest of this into an index and mark it as
    // non-updating. could then regenerate every time, or make it
    // auto-updating.
    
    try {
      RAMDirectory dir = new RAMDirectory();
      
      IndexerIF ixer = new LuceneIndexer(dir, true);
      DefaultTopicMapIndexer imanager = new DefaultTopicMapIndexer(ixer, false, null);
      imanager.index(topicmap);

      // close indexers to flush out last pieces of data
      imanager.close();
      ixer.close();
      
      return new LuceneSearcher(dir);
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
}
