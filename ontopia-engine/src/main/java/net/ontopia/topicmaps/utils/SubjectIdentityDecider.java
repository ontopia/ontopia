
package net.ontopia.topicmaps.utils;

import java.util.Iterator;
import net.ontopia.topicmaps.core.*;
import net.ontopia.utils.DeciderIF;
import net.ontopia.infoset.core.LocatorIF;

/**
 * INTERNAL: Decider that decides whether the object is an instance of a
 * topic with the given subject identifier.</p>
 *
 * The decider returns true when the object has the given subject
 * identifier, or it is an instance of a topic with the given subject
 * identifier.</p>
 */

public class SubjectIdentityDecider implements DeciderIF {
  /**
   * PROTECTED: the given subject identifier.
   */ 
  protected LocatorIF subject_identifier;

  /**
   * INTERNAL: Creates a decider which uses the given subject identifier.
   *
   * @param subject_identifier locatorIF which is the given subject identifier
   */ 
  
  public SubjectIdentityDecider(LocatorIF subject_identifier) {
    this.subject_identifier = subject_identifier;
  }

  /**
   * INTERNAL: Decides whether an object (directly or indirectly) has a
   * given subject identifier.
   *
   * @param object an object which must be a TypedIF or TopicIF
   * @return boolean; true iff the given object has the given subject
   * identifier (directly or indirectly)
   */ 

  public boolean ok(Object object) {
    if (object instanceof TopicIF) {
      TopicIF topic = (TopicIF) object;
      if (topic.getSubjectIdentifiers().contains(subject_identifier))
        return true;
    }
      
    if (object instanceof TypedIF) {
      TopicIF topic = ((TypedIF) object).getType();
      if (topic == null)
        return false;
      return topic.getSubjectIdentifiers().contains(subject_identifier);
      
    } else if (object instanceof TopicIF) {
      Iterator it = ((TopicIF) object).getTypes().iterator();
      while (it.hasNext()) {
        TopicIF topic = (TopicIF) it.next();
        if (topic.getSubjectIdentifiers().contains(subject_identifier))
          return true;
      }
    } 

    return false;
  }

}
