package net.ontopia.topicmaps.query.toma.impl.basic.path;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.impl.basic.ResultSet;
import net.ontopia.topicmaps.query.toma.impl.basic.expression.PathExpression;

/**
 * INTERNAL: Variant path element in an path expression. Returns all variants
 * of a given input name.
 * <p>
 * <b>Allowed Input</b>:
 * <ul>
 * <li>NAME
 * </ul>
 * </p><p>
 * <b>Output</b>: VARIANT
 * </p>
 */
public class VariantPath extends AbstractBasicPathElement {
  static final Set<TYPE> inputSet;
  
  static {
    inputSet = new HashSet<TYPE>();
    inputSet.add(TYPE.NAME);
  }
  
  private Collection<?> validScopes = null;

  public VariantPath() {
    super("VAR");
  }

  protected boolean isLevelAllowed() {
    return false;
  }

  protected boolean isScopeAllowed() {
    return true;
  }
  
  protected boolean isTypeAllowed() {
    return false;
  }

  protected boolean isChildAllowed() {
    return false;
  }
  
  public Set<TYPE> validInput() {
    return inputSet;
  }
  
  public TYPE output() {
    return TYPE.VARIANT;
  }
  
  @SuppressWarnings("unchecked")
  public Collection<VariantNameIF> evaluate(LocalContext context, Object input)
      throws InvalidQueryException {
    TopicNameIF name = (TopicNameIF) input;
    
    if (getScope() != null) {
      PathExpression scope = (PathExpression) getScope();
      // Optimization: if the scope expression does not contain a variable, we
      // can cache it.
      if (scope.getVariableName() != null || validScopes == null) {
        ResultSet scopes = scope.evaluate(context);
        validScopes = scopes.getValidValues(scopes.getLastIndex());
      }
    }

    Collection<VariantNameIF> variants = name.getVariants();
    if (validScopes == null) {
      return variants;
    } else {
      Collection<VariantNameIF> result = new LinkedList<VariantNameIF>();
      for (VariantNameIF var : variants) {
        if (containsAny(var.getScope(), validScopes)) {
          result.add(var);
        }
      }
      return result;
    }
  }
}
