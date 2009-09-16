package net.ontopia.topicmaps.query.toma.impl.basic.path;

import java.util.HashSet;
import java.util.Set;

import net.ontopia.topicmaps.query.toma.parser.ast.AbstractPathElement;

public class SuperTypePath extends AbstractPathElement {
  
  static final Set<TYPE> inputSet;
  
  static {
    inputSet = new HashSet<TYPE>();
    inputSet.add(TYPE.TOPIC);
  }
  
  public SuperTypePath() {
    super("SUPER");
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
    return TYPE.TOPIC;
  }
}
