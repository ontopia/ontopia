
// $Id: YahooTree.java,v 1.1 2008/12/04 11:27:13 lars.garshol Exp $

package net.ontopia.topicmaps.nav2.portlets.pojos;

import java.util.List;
import net.ontopia.utils.StringifierIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.utils.TopicStringifiers;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.RowMapperIF;
import net.ontopia.topicmaps.query.utils.QueryWrapper;

/**
 * PUBLIC: This component can create a view of a two-level tree. It's
 * an open question whether it really rather ought to show any number
 * of levels.
 */
public class YahooTree {
  private TopicIF topictype;
  private QueryWrapper query;
  private StringifierIF stringifier;
  
  public YahooTree(TopicIF topictype) {
    this.topictype = topictype;
    this.query = new QueryWrapper(topictype.getTopicMap());
    this.query.setDeclarations(
      "using nrk for i\"http://bogus.ontopia.net/nrk/\" " +
      "using emne for i\"http://bogus.ontopia.net/nrk/emne/\" " +
      "using tech for i\"http://www.techquila.com/psi/thesaurus/#\" ");
    this.stringifier = TopicStringifiers.getDefaultStringifier();
  }

  public List getTopLevel() {
    return query.queryForList(
      "select $TOP from " +
      "  instance-of($TOP, nrk:emne), " +
      "  not(tech:broader-narrower($TOP : tech:narrower, $O : tech:broader)), "+
      "  $TOP /= emne:ukategoriserbart, " +
      "  $TOP /= emne:nytt-emne " +
      "order by $TOP?", new NodeMapper());
  }

  // ========================================================================

  public class TreeNode {
    private TopicIF topic;

    public TreeNode(TopicIF topic) {
      this.topic = topic;
    }

    public String getName() {
      return stringifier.toString(topic);
    }
    
    public TopicIF getTopic() {
      return topic;
    }

    public List getChildren() {
      return query.queryForList(
        "select $CHILD from " +
        "  tech:broader-narrower(%this% : tech:broader, $CHILD : tech:narrower) " +
        "order by $CHILD?", new NodeMapper(),
        query.makeParams("this", topic));
    }
  }
  
  class NodeMapper implements RowMapperIF {

    public Object mapRow(QueryResultIF result, int row) {
      return new TreeNode((TopicIF) result.getValue(0));
    }
    
  }
}
