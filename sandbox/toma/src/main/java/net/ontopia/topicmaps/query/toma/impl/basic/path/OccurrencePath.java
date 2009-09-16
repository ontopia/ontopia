package net.ontopia.topicmaps.query.toma.impl.basic.path;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicPathElementIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.impl.basic.PathExpression;
import net.ontopia.topicmaps.query.toma.impl.basic.ResultSet;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractPathElement;

public class OccurrencePath extends AbstractPathElement 
  implements BasicPathElementIF {
  
  static final Set<TYPE> inputSet;
  
  static {
    inputSet = new HashSet<TYPE>();
    inputSet.add(TYPE.TOPIC);
  }

  public OccurrencePath() {
    super("OC");
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
    return TYPE.OCCURRENCE;
  }
  
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
              containsScope(oc.getScope(), validScopes)) {
            result.add(oc);
          }
        }
      }
      return result;
    }
  }
  
  private boolean containsScope(Collection scopes, Collection valid) {
    for (Object scope : valid) {
      if (scopes.contains(scope)) {
        return true;
      }
    }
    return false;
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
