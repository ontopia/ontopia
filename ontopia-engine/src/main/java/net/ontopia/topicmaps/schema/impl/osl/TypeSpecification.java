
package net.ontopia.topicmaps.schema.impl.osl;

import java.util.Iterator;
import net.ontopia.topicmaps.utils.TypeHierarchyUtils;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TypedIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.schema.core.TMObjectMatcherIF;

/**
 * INTERNAL: Represents the allowed type of a topic map object.
 */
public class TypeSpecification implements TMObjectMatcherIF {
  protected TMObjectMatcherIF matcher;
  protected boolean subclasses;

  // --- TypeSpecification methods
  
  /**
   * INTERNAL: Creates a new type specification.
   */
  public TypeSpecification() {
    this.subclasses = true;
  }
  
  /**
   * INTERNAL: Returns the matcher used to match the allowed type.
   */
  public TMObjectMatcherIF getClassMatcher() {
    return matcher;
  }
  
  /**
   * INTERNAL: Sets the object used to match the type.
   */
  public void setClassMatcher(TMObjectMatcherIF matcher) {
    this.matcher = matcher;
  }

  /**
   * INTERNAL: If true, subclasses of the matched topic are allowed.
   */
  public boolean getSubclasses() {
    return subclasses;
  }
  
  /**
   * INTERNAL: Controls whether subclasses of the matched topic are
   * accepted.
   */
  public void setSubclasses(boolean subclasses) {
    this.subclasses = subclasses;
  }

  /**
   * INTERNAL: Returns true if the given topic defines an acceptable type.
   * @param type A candidate acceptable type.
   */
  public boolean matchType(TopicIF type) {
    if (matcher == null)
      return type == null;
    if (matcher.matches(type))
      return true;

    if (subclasses && type != null) {
      TypeHierarchyUtils typeutils = new TypeHierarchyUtils();
      Iterator it = typeutils.getSuperclasses(type).iterator();
      
      while (it.hasNext())
        if (matcher.matches((TopicIF) it.next()))
          return true;
    }
    
    return false;
  }
  
  // --- TMObjectMatcherIF methods
  
  /**
   * INTERNAL: Matches the type of the given object, which must implement
   * the TypedIF interface.
   */
  public boolean matches(TMObjectIF object) {
    if (object instanceof TypedIF)
      return matchType( ((TypedIF) object).getType() );
    else if (object instanceof TopicIF) {
      Iterator it = ((TopicIF) object).getTypes().iterator();

      if (!it.hasNext()) 
        return matchType(null);
      
      while (it.hasNext()) {
        if (matchType( (TopicIF) it.next() ))
          return true;
      }
    }

    return false;
  }

  public boolean equals(TMObjectMatcherIF object) {
    return false;
  }

}
