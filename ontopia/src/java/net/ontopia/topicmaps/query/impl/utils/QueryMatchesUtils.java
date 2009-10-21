
package net.ontopia.topicmaps.query.impl.utils;

import java.util.Map;
import java.util.Collection;
import net.ontopia.topicmaps.query.impl.basic.QueryContext;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;

/**
 * INTERNAL: Various utility methods for working with QueryMatches
 * objects.
 */
public class QueryMatchesUtils {

  public static QueryMatches createInitialMatches(QueryContext context,
                                                  Collection columnDefs) {
    QueryMatches matches = new QueryMatches(columnDefs, context);
    matches.last++; // enter a single empty match to seed the process
    matches.insertConstants();
    return matches;
  }
  
}
