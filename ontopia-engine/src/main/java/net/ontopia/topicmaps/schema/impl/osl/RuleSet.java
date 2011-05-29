
package net.ontopia.topicmaps.schema.impl.osl;

import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import net.ontopia.topicmaps.core.TopicIF;

/**
 * INTERNAL: Represents a rule set, that is, a collection of reusable
 * topic constraints.
 */
public class RuleSet extends TopicConstraintCollection {

  // --- RuleSet methods
  
  /**
   * INTERNAL: Creates a new rule set object.
   * @param schema The parent schema.
   * @param id The ID of the rule set.
   */
  public RuleSet(OSLSchema schema, String id) {
    super(schema, id);
  }

  // --- Object methods
  
  public String toString() {
    return "<RuleSet " + id + ">";
  }
}





