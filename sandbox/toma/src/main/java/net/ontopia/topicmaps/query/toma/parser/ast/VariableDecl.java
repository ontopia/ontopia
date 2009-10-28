package net.ontopia.topicmaps.query.toma.parser.ast;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: The variable declaration within a SelectStatement.
 */
public class VariableDecl {
  private String varName;
  private Set<PathElementIF.TYPE> validTypes;
  
  public VariableDecl(String name) {
    this.varName = name;
    this.validTypes = new HashSet<PathElementIF.TYPE>();
    
    // add all valid types
    validTypes.add(PathElementIF.TYPE.TOPIC);
    validTypes.add(PathElementIF.TYPE.ASSOCIATION);
    validTypes.add(PathElementIF.TYPE.NAME);
    validTypes.add(PathElementIF.TYPE.OCCURRENCE);
    validTypes.add(PathElementIF.TYPE.VARIANT);
    validTypes.add(PathElementIF.TYPE.LOCATOR);
    validTypes.add(PathElementIF.TYPE.STRING);
  }
  
  /**
   * Get the name of the variable.
   * 
   * @return the name of the variable.
   */
  public String getVariableName() {
    return varName;
  }

  /**
   * Get all valid types for this variable.
   * 
   * @return the valid types as a Set.
   */
  public Set<PathElementIF.TYPE> getValidTypes() {
    return validTypes;
  }

  /**
   * Further constrain the valid types of a variable. The result is {current
   * types} intersect {constrained types}.
   * 
   * @param types the further constrained types for this variable.
   * @throws InvalidQueryException if there resulting set is empty.
   */
  public void constrainTypes(PathElementIF.TYPE... types)
    throws InvalidQueryException {
    constrainTypes(Arrays.asList(types));
  }

  /**
   * Further constrain the valid types of a variable. The result is {current
   * types} intersect {constrained types}.
   * 
   * @param types the further constrained types for this variable.
   * @throws InvalidQueryException if there resulting set is empty.
   */
  public void constrainTypes(Collection<PathElementIF.TYPE> types)
      throws InvalidQueryException {
    
    Set<PathElementIF.TYPE> newTypes = new HashSet<PathElementIF.TYPE>();
    for (PathElementIF.TYPE type : types) {
      if (validTypes.contains(type)) {
        newTypes.add(type);
      }
    }

    if (newTypes.isEmpty()) {
      throw new InvalidQueryException("Resulting Type Set for variable '"
          + varName + "' is empty.");
    } else {
      validTypes.clear();
      validTypes = newTypes;
    }
  }
}
