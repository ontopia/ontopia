package net.ontopia.topicmaps.query.toma.impl.basic.path;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicPathElementIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractPathElement;

public class NamePath extends AbstractPathElement implements BasicPathElementIF {
  
  static final Set<TYPE> inputSet;
  
  static {
    inputSet = new HashSet<TYPE>();
    inputSet.add(TYPE.TOPIC);
  }
  
  public NamePath() {
    super("NAME");
  }

  @Override
  protected boolean isLevelAllowed() {
    return false;
  }

  @Override
  protected boolean isScopeAllowed() {
    return true;
  }
  
  @Override
  protected boolean isTypeAllowed() {
    return true;
  }

  @Override
  protected boolean isChildAllowed() {
    return false;
  }
  
  public Set<TYPE> validInput() {
    return inputSet;
  }
  
  public TYPE output() {
    return TYPE.NAME;
  }

  public Collection<TopicNameIF> evaluate(LocalContext context, Object input) {
    TopicIF topic = (TopicIF) input;
    return topic.getTopicNames();
  }

  public String[] getColumnNames() {
    if (getBoundVariable() != null) {
      return new String[] { getBoundVariable().toString() };
    } else {
      return new String[0];
    }
  }

  public int getResultSize() {
    if (getBoundVariable() != null) {
      return 1;
    } else {
      return 0;
    }
  }
}
