
// $Id: TopicClass.java,v 1.9 2008/06/12 14:37:22 geir.gronmo Exp $

package net.ontopia.topicmaps.schema.impl.osl;

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import net.ontopia.topicmaps.utils.TypeHierarchyUtils;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.schema.core.ConstraintIF;

/**
 * INTERNAL: Represents a topic class definition.
 */
public class TopicClass extends TopicConstraintCollection
                        implements TypedConstraintIF, ConstraintIF {
  
  protected boolean strict;
  protected Collection otherClasses;
  protected TypeSpecification typespec;
  protected TopicClass superclass;
  protected Collection subclasses;

  /**
   * INTERNAL: Creates a topic class belonging to the given schema.
   * @param id The id of the topic class. May be null.
   */
  public TopicClass(OSLSchema schema, String id) {
    super(schema, id);
    strict = false;
    otherClasses = new ArrayList();
    subclasses = new ArrayList();
  }

  // --- TopicClass methods

  /**
   * INTERNAL: True if strict matching is used; false otherwise.
   */
  public boolean isStrict() {
    return strict;
  }

  /**
   * INTERNAL: Sets whether the class uses strict matching or not.
   */
  public void setIsStrict(boolean strict) {
    this.strict = strict;
  }

  /**
   * INTERNAL: Returns the specification of the topic typing the class
   * defined by this object.
   */
  public TypeSpecification getTypeSpecification() {
    return typespec;
  }

  /**
   * INTERNAL: Sets the specification of the topic typing the class
   * defined by this object.
   */
  public void setTypeSpecification(TypeSpecification typespec) {
    this.typespec = typespec;
  }

  /**
   * INTERNAL: Add a specification of a class to which instances of
   * this class may belong.
   */
  public void addOtherClass(TypeSpecification typespec) {
    otherClasses.add(typespec);
  }

  /**
   * INTERNAL: Remove the specification of a class to which instances
   * of this class may no longer belong.
   */
  public void removeOtherClass(TypeSpecification typespec) {
    otherClasses.remove(typespec);
  }

  /**
   * INTERNAL: Returns the collection of other classes to which instances
   * of this class may belong.
   * @return A collection of TypeSpecification objects.
   */
  public Collection getOtherClasses() {
    return otherClasses;
  }

  /**
   * INTERNAL: Returns the superclass of this class.
   * @return The superclass. May be null.
   */
  public TopicClass getSuperclass() {
    return superclass;
  }

  /**
   * INTERNAL: Sets the superclass of this class.
   * @param superclass The superclass of this class. May be null.
   */
  public void setSuperclass(TopicClass superclass) {
    if (this.superclass == superclass)
      return;
    if (this.superclass != null)
      superclass.removeSubclass(this);
    this.superclass = superclass;
    this.superclass.addSubclass(this);
  }

  /**
   * INTERNAL: Returns the (possibly empty) collection of subclasses of
   * this class.
   * @return A collection of TopicClass objects.
   */
  public Collection getSubclasses() {
    return subclasses;
  }

  // --- Overrides
  
  public Collection getAllTopicNameConstraints() {
    Collection constraints = super.getAllTopicNameConstraints();
    if (superclass != null)
      constraints.addAll(superclass.getAllTopicNameConstraints());
    return constraints;
  }

  public Collection getAllOccurrenceConstraints() {
    Collection constraints = super.getAllOccurrenceConstraints();
    if (superclass != null)
      constraints.addAll(superclass.getAllOccurrenceConstraints());
    return constraints;
  }

  public Collection getAllRoleConstraints() {
    Collection constraints = super.getAllRoleConstraints();
    if (superclass != null)
      constraints.addAll(superclass.getAllRoleConstraints());
    return constraints;
  }
  
  // --- ConstraintIF

  public boolean matches(TMObjectIF object) {
    return typespec.matches(object);
  }
    
  // --- Object methods
  
  public String toString() {
    return "<TopicClass>"; // FIXME!
  }

  // --- Package-internal methods

  void addSubclass(TopicClass subclass) {
    subclasses.add(subclass);
  }

  void removeSubclass(TopicClass subclass) {
    subclasses.remove(subclass);
  }
}
