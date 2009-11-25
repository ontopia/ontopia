package net.ontopia.topicmaps.query.toma.impl.basic.path;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
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
  
  private Collection<?> validScopes = null;
  private Collection<?> validTypes = null;
  
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
  public Collection evaluate(LocalContext context, Object input)
      throws InvalidQueryException {
    TopicIF topic = (TopicIF) input;
    
    if (getScope() != null && !isAssignScope()) {
      PathExpression scope = (PathExpression) getScope();
      // Optimization: if the scope expression does not contain a variable, we
      // can cache it.
      if (scope.getVariableName() != null || validScopes == null) {
        ResultSet scopes = scope.evaluate(context);
        validScopes = scopes.getValidValues(scopes.getLastIndex());
      }
    }
    
    if (getType() != null && !isAssignType()) {
      PathExpression type = (PathExpression) getType();
      // Optimization: if the type expression does not contain a variable, we
      // can cache it.
      if (type.getVariableName() != null || validTypes == null) {
        ResultSet types = type.evaluate(context);
        validTypes = types.getValidValues(types.getLastIndex());
      }
    }

    Collection<TopicNameIF> names = topic.getTopicNames();
    if (validTypes == null && validScopes == null && !isAssignScope()
        && !isAssignType()) {
      return names;
    } else {
      Collection<Object[]> result = new LinkedList<Object[]>();
      for (TopicNameIF name : names) {
        TopicIF nameType = name.getType();
        if (validTypes == null || validTypes.contains(nameType)) {
          if (validScopes == null || 
              containsAny(name.getScope(), validScopes)) {
            fillResultCollection(result, name);
          }
        }
      }
      return result;
    }
  }
  
  @SuppressWarnings("unchecked")
  private void fillResultCollection(Collection<Object[]> result,
      TopicNameIF name) {
    if (isAssignScope()) {
      Collection<TopicIF> scopes = name.getScope();
      if (scopes.isEmpty()) {
        if (isAssignType()) {
          result.add(new Object[] { null, name.getType(), name });
        } else {
          result.add(new Object[] { null, name });
        }
      } else {
        for (TopicIF scope : scopes) {
          result.add(new Object[] { scope, name.getType(), name });
        }
      }
    } else if (isAssignType()) {
      result.add(new Object[] { name.getType(), name });
    } else {
      result.add(new Object[] { name });
    }
  }
}
