// $Id: AbstractTypingTopic.java,v 1.4 2009/04/21 06:23:52 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.webapps.ontopoly.model;

import java.util.*;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.utils.*;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.*;

/**
 * INTERNAL: Common superclass for all typing topics, like association types,
 * topic types, role types, etc. FIXME: Should there be another subtype for
 * isHidden, isReadOnly etc?
 */
public abstract class AbstractTypingTopic extends Topic {

  /**
   * Constructor. 
   * 
   * @param topicIF the TopicIF object associated with this topic.
   * @param tm the TopicMap this topic belongs to.
   */ 
  public AbstractTypingTopic(TopicIF topicIF, TopicMap tm) {
    super(topicIF, tm);
  }

  // ! /**
  // ! * Returns true if the topic is a system topic.
  // ! */
  // ! public boolean isSystemTopic() {
  // ! // for ontology annotation support, check if the topic is part of
  // ! // the system ontology
  // ! Iterator it = tm.getSystemTopicTypes().iterator();
  // ! while(it.hasNext()) {
  // ! if(((TopicType)it.next()).getTopicIF().equals(topicIF)) {
  // ! return true;
  // ! }
  // ! }
  // ! return false;
  // ! }

  /**
   * Gets the LocatorIF for this typing topic. The locator is the PSI used by the ontology topic map model.p
   * 
   * @return the LocatorIF for this typing topic.
   */
  public abstract LocatorIF getLocatorIF();

  /**
   * Returns true if this typing topic is read-only. If the topic type is read-only it cannot be edited or deleted.
   * 
   * @return true if read only is turned on.
   */
  public boolean isReadOnly() {
    TopicMap tm = getTopicMap();
    TopicIF aType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "is-readonly-type");
    TopicIF rType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "ontology-type");
    TopicIF player = getTopicIF();
    return OntopolyModelUtils.isUnaryPlayer(tm, aType, player, rType);
  }

//  /**
//   * Makes this field type either read only or turn it off.
//   * 
//   * @param value
//   *            value indicates whether this field type is going to
//   *            be read only.
//   */
//  public void setReadOnly(boolean value) {
//    TopicMap tm = getTopicMap();
//    TopicIF aType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "is-readonly-type");
//    TopicIF rType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "ontology-type");
//
//    TopicIF topicIF = getTopicIF();
//    AssociationIF assoc = OntopolyModelUtils.findUnaryAssociation(tm, aType, topicIF, rType);
//
//    if (value && assoc == null)
//      OntopolyModelUtils.makeUnaryAssociation(aType, topicIF, rType);
//    else if (assoc != null)
//      assoc.remove();
//  }

  /**
   * Returns true if this typing topic is hidden. NOTE: this feature is not yet supported.
   * 
   * @return true if hidden is turned on.
   */
  public boolean isHidden() {
    TopicMap tm = getTopicMap();
    TopicIF aType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "is-hidden-type");
    TopicIF rType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "ontology-type");
    TopicIF player = getTopicIF();
    return OntopolyModelUtils.isUnaryPlayer(tm, aType, player, rType);
  }

//  /**
//   * Makes this field type either hidden or turn it off.
//   * 
//   * @param value
//   *            value indicates whether this field type is going to
//   *            be hidden.
//   */
//  public void setHidden(boolean value) {
//    TopicMap tm = getTopicMap();
//    TopicIF aType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "is-hidden-type");
//    TopicIF rType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "ontology-type");
//
//    TopicIF topicIF = getTopicIF();
//    AssociationIF assoc = OntopolyModelUtils.findUnaryAssociation(tm, aType, topicIF, rType);
//
//    if (value && assoc == null)
//      OntopolyModelUtils.makeUnaryAssociation(aType, topicIF, rType);
//    else if (assoc != null)
//      assoc.remove();
//  }

  /**
   * Returns the field definitions that are declared for this typing topic.
   * 
   * @return a list of FieldDefinitions.
   */
  public abstract Collection getDeclaredByFields();

//  public abstract Collection getUsedBy();

}
