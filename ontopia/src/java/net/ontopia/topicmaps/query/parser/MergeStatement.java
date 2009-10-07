
// $Id$

package net.ontopia.topicmaps.query.parser;

import java.util.Map;
import java.util.List;
import java.util.Collections;
import java.net.MalformedURLException;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;

/**
 * INTERNAL: Represents a parsed MERGE statement.
 */
public class MergeStatement extends UpdateStatement {

  public MergeStatement() {
    super();
  }

  public int doStaticUpdates() throws InvalidQueryException {
    TopicIF topic1 = (TopicIF) litlist.get(0);
    TopicIF topic2 = (TopicIF) litlist.get(1);
    if (topic1 != topic2)
      topic1.merge(topic2);
    return 1;
  }

  public int doUpdates(QueryMatches matches)
    throws InvalidQueryException {
    int merges = 0;

    Object arg1 = litlist.get(0);
    int varix1 = getIndex(arg1, matches);
    Object arg2 = litlist.get(1);
    int varix2 = getIndex(arg2, matches);
    
    for (int row = 0; row <= matches.last; row++) {
      if (varix1 != -1)
        arg1 = matches.data[row][varix1];

      if (varix2 != -1)
        arg2 = matches.data[row][varix2];

      TopicIF topic1 = (TopicIF) arg1;
      TopicIF topic2 = (TopicIF) arg2;
      if (topic1 != topic2 &&
          topic1.getTopicMap() != null &&
          topic2.getTopicMap() != null) {
        topic1.merge(topic2);
        merges++;
      }
    }

    return merges;
  }
}