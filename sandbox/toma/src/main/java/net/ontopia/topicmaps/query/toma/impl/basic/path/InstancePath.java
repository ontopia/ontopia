package net.ontopia.topicmaps.query.toma.impl.basic.path;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicPathElementIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractPathElement;

public class InstancePath extends AbstractPathElement implements BasicPathElementIF {
  
  static final Set<TYPE> inputSet;
  
  static {
    inputSet = new HashSet<TYPE>();
    inputSet.add(TYPE.TOPIC);
  }
  
  public InstancePath() {
    super("INSTANCE");
  }

  @Override
  protected boolean isLevelAllowed() {
    return true;
  }

  @Override
  protected boolean isScopeAllowed() {
    return false;
  }
  
  @Override
  protected boolean isTypeAllowed() {
    return false;
  }

  @Override
  protected boolean isChildAllowed() {
    return false;
  }

  public Set<TYPE> validInput() {
    return inputSet;
  }
  
  public TYPE output() {
    return TYPE.STRING;
  }

  public Collection<?> evaluate(LocalContext context, Object input) {
    ClassInstanceIndexIF index = 
      (ClassInstanceIndexIF) context.getTopicMap().getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");

    Collection<TopicIF> instances = index.getTopics((TopicIF) input);
    return instances;
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
