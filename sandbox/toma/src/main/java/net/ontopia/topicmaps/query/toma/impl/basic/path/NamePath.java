package net.ontopia.topicmaps.query.toma.impl.basic.path;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.impl.basic.ResultSet;
import net.ontopia.topicmaps.query.toma.impl.basic.expression.PathExpression;

/**
 * INTERNAL: Name path element in an path expression. Returns all names
 * of a given input topic.
 * <p>
 * <b>Allowed Input</b>:
 * <ul>
 * <li>TOPIC
 * </ul>
 * </p><p>
 * <b>Output</b>: NAME
 * </p>
 */
public class NamePath extends AbstractBasicPathElement {
  static final Set<TYPE> inputSet;
  
  static {
    inputSet = new HashSet<TYPE>();
    inputSet.add(TYPE.TOPIC);
  }
  
  public NamePath() {
    super("NAME");
  }

  protected boolean isLevelAllowed() {
    return false;
  }

  protected boolean isScopeAllowed() {
    return true;
  }
  
  protected boolean isTypeAllowed() {
    return true;
  }

  protected boolean isChildAllowed() {
    return false;
  }
  
  public Set<TYPE> validInput() {
    return inputSet;
  }
  
  public TYPE output() {
    return TYPE.NAME;
  }

  @SuppressWarnings("unchecked")
  public Collection<TopicNameIF> evaluate(LocalContext context, Object input) {
    TopicIF topic = (TopicIF) input;
    
    Collection<?> validScopes = null;
    Collection<?> validTypes = null;
    
    if (getScope() != null) {
      PathExpression scope = (PathExpression) getScope();
      ResultSet scopes = scope.evaluate(context);
      validScopes = scopes.getValues(scopes.getColumnCount() - 1);
    }
    
    if (getType() != null) {
      PathExpression type = (PathExpression) getType();
      ResultSet types = type.evaluate(context);
      validTypes = types.getValues(types.getColumnCount() - 1);
    }
    
    Collection<TopicNameIF> names = topic.getTopicNames();
    if (validTypes == null && validScopes == null) {
      return names;
    } else {
      Collection<TopicNameIF> result = new LinkedList<TopicNameIF>();
      for (TopicNameIF name : names) {
        TopicIF nameType = name.getType();
        if (validTypes == null || validTypes.contains(nameType)) {
          if (validScopes == null || 
              containsAny(name.getScope(), validScopes)) {
            result.add(name);
          }
        }
      }
      return result;
    }
  }
}
