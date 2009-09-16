package net.ontopia.topicmaps.query.toma.impl.basic.path;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicPathElementIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractPathElement;

public class PlayerPath extends AbstractPathElement implements BasicPathElementIF {
  
  static final Set<TYPE> inputSet;
  
  static {
    inputSet = new HashSet<TYPE>();
    inputSet.add(TYPE.ASSOCIATION);
  }
  
  public PlayerPath() {
    super("PLAYER");
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
  
  public Collection<TopicIF> evaluate(LocalContext context, Object input) {
    AssociationIF assoc = (AssociationIF) input;
    Collection<AssociationRoleIF> roles = assoc.getRoles();
    LinkedList<TopicIF> result = new LinkedList<TopicIF>();
    for (AssociationRoleIF role : roles) {
      result.add(role.getPlayer());
    }
    return result;
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
