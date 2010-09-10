
package net.ontopia.topicmaps.nav2.portlets.pojos;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;

import net.ontopia.topicmaps.core.TopicIF;

import net.ontopia.topicmaps.query.utils.QueryWrapper;

/**
 * PUBLIC: This component can produce a list of topics similar to the
 * input topic by finding other topics with similar associations as
 * the input topic.
 */
public class SimilarTopics {

  public SimilarTopics() {
  }

  // --- Configuration

  // set a limit for how many similar topics to include
  // association control as in RelatedTopics
  // type control as in RelatedTopics
  
  // --- Produce model

  /**
   * PUBLIC: Returns a list of similar topics, ordered most similar to
   * least similar.
   */
  public List<TopicIF> makeModel(TopicIF topic) {
    // maps topics to their points
    Map<TopicIF, Float> points = new HashMap();
    
    // find the topics associated with this one
    List<TopicIF> relateds = getRelated(topic);

    // go through the associated topics to find those another step out
    Float zero = new Float(0);
    for (TopicIF related : relateds) {
      // say we have 100 points available for this topic. all the other
      // topics related to this one should have an equal share of those
      // points.
      List<TopicIF> similars = getRelated(related);
      float score = 100.0f / similars.size();
      for (TopicIF similar : similars) {
        if (similar == topic)
          continue;
        Float prev = points.get(similar);
        if (prev == null)
          prev = zero;
        points.put(similar, new Float(score + prev.floatValue()));
      }
    }

    // sort topics by similarity
    List model = new ArrayList(points.keySet());
    Collections.sort(model, new PointsComparator(points));

    // ok, done
    return model;
  }

  // --- Internal helpers

  private List<TopicIF> getRelated(TopicIF topic) {
    QueryWrapper w = new QueryWrapper(topic.getTopicMap());
    Map params = w.makeParams("topic", topic);
    return w.queryForList(
      "select $OTHER from " +                                            
      "role-player($ROLE1, %topic%), " +
      "association-role($ASSOC, $ROLE1), " +
      "association-role($ASSOC, $ROLE2), " +
      "$ROLE1 /= $ROLE2, " +
      "role-player($ROLE2, $OTHER)?", params);
  }

  private static class PointsComparator implements Comparator {
    private Map<TopicIF, Float> points;

    private PointsComparator(Map<TopicIF, Float> points) {
      this.points = points;
    }

    public int compare(Object o1, Object o2) {
      Float f1 = points.get((TopicIF) o1);
      Float f2 = points.get((TopicIF) o2);
      return -1 * f1.compareTo(f2);
    }
  }
}
