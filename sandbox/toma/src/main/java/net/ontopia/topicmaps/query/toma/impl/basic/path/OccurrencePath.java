package net.ontopia.topicmaps.query.toma.impl.basic.path;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.impl.basic.ResultSet;
import net.ontopia.topicmaps.query.toma.impl.basic.expression.PathExpression;

/**
 * INTERNAL: Occurrence path element in an path expression. Returns all occurrences
 * of a given input topic.
 * <p>
 * <b>Allowed Input</b>:
 * <ul>
 * <li>TOPIC
 * </ul>
 * </p><p>
 * <b>Output</b>: OCCURRENCE
 * </p>
 */
public class OccurrencePath extends AbstractBasicPathElement { 
  static final Set<TYPE> inputSet;
  
  static {
    inputSet = new HashSet<TYPE>();
    inputSet.add(TYPE.TOPIC);
  }

  public OccurrencePath() {
    super("OC");
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
    return TYPE.OCCURRENCE;
  }
  
  @SuppressWarnings("unchecked")
  public Collection<OccurrenceIF> evaluate(LocalContext context, Object input) {
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
    
    Collection<OccurrenceIF> ocs = topic.getOccurrences();
    if (validTypes == null && validScopes == null) {
      return ocs;
    } else {
      Collection<OccurrenceIF> result = new LinkedList<OccurrenceIF>();
      for (OccurrenceIF oc : ocs) {
        TopicIF ocType = oc.getType();
        if (validTypes == null || validTypes.contains(ocType)) {
          if (validScopes == null || 
              containsAny(oc.getScope(), validScopes)) {
            result.add(oc);
          }
        }
      }
      return result;
    }
  }  
}
