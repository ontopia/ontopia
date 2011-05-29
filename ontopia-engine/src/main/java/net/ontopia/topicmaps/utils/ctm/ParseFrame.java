
package net.ontopia.topicmaps.utils.ctm;

import net.ontopia.topicmaps.core.*;

/**
 * INTERNAL: Carrier for variables used during parsing to keep track
 * of context such as current topic, current topic name, current
 * statement (scoped/reifiable) etc. Moved out to a separate class
 * since the existence of embedded topics makes it necessary to be
 * able to stack these frames.
 */
public class ParseFrame {

  // the topic of the current topic block, *or* the current embedded topic
  protected TopicIF topic;

  // the topic name currently being parsed (needed for variant names)  
  protected TopicNameIF name;

  // the statement currently being parsed (used by the scope parsing code)
  protected ScopedIF scoped;

  // the statement currently being parsed (used by the reifier parsing code)
  protected ReifiableIF reifiable;

  // the association currently being parsed (needed for roles)
  protected AssociationIF association;

  // constructor
  public ParseFrame(TopicIF topic,
                    TopicNameIF name,
                    ScopedIF scoped,
                    ReifiableIF reifiable,
                    AssociationIF association) {
    this.topic = topic;
    this.name = name;
    this.scoped = scoped;
    this.reifiable = reifiable;
    this.association = association;
  }
}
