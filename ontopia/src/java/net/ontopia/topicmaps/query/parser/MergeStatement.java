
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
    return 0;
  }
}