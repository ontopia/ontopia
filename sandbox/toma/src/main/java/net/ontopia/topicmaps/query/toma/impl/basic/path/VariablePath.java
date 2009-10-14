package net.ontopia.topicmaps.query.toma.impl.basic.path;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicPathElementIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.impl.basic.ResultSet;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractVariable;
import net.ontopia.topicmaps.query.toma.parser.ast.PathElementIF;

/**
 * INTERNAL: Represents a variable within a TOMA query.
 */
public class VariablePath extends AbstractVariable implements
    BasicPathElementIF {
  static final Set<TYPE> inputSet;

  static {
    inputSet = new HashSet<TYPE>();
    inputSet.add(TYPE.NONE);
  }

  public VariablePath(String name) {
    super(name.toUpperCase());
  }

  public String[] getColumnNames() {
    return new String[] { toString() };
  }

  public int getResultSize() {
    return 1;
  }

  @Override
  protected boolean isChildAllowed() {
    return false;
  }

  @Override
  protected boolean isLevelAllowed() {
    return false;
  }

  @Override
  protected boolean isScopeAllowed() {
    return false;
  }

  @Override
  protected boolean isTypeAllowed() {
    return false;
  }

  public TYPE output() {
    // TODO: return the correct output type after semantic analysis.
    return PathElementIF.TYPE.UNKNOWN;
  }

  public Set<TYPE> validInput() {
    return inputSet;
  }

  public Collection<?> evaluate(LocalContext context, Object input) {
    // try to get a ResultSet that already bound the variable
    ResultSet rs = context.getResultSet(toString());

    // TODO: Variables can be of any type, not just topics

    if (rs != null) {
      return rs.getValues(toString());
    } else {
      TopicMapIF topicmap = context.getTopicMap();
      return topicmap.getTopics();
    }
  }
}
