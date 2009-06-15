// $Id: CreateAction.java,v 1.1 2008/10/23 05:18:36 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.webapps.ontopoly.model;

import net.ontopia.topicmaps.core.TopicIF;

/**
 * Represents the edit mode of a field.
 */
public class CreateAction extends Topic {

  /**
   * Creates a new CreateAction object.
   */
  public CreateAction(TopicIF topic, TopicMap tm) {
    super(topic, tm);
  }
  
  public boolean equals(Object obj) {
    if (!(obj instanceof CreateAction))
      return false;

    CreateAction cardinality = (CreateAction) obj;
    return (getTopicIF().equals(cardinality.getTopicIF()));
  }

  public boolean isNone() {
    return (getTopicIF().getSubjectIdentifiers().contains(PSI.ON_CREATE_ACTION_NONE));
  }

  public boolean isPopup() {
    return (getTopicIF().getSubjectIdentifiers().contains(PSI.ON_CREATE_ACTION_POPUP));
  }

  public boolean isNavigate() {
    return (getTopicIF().getSubjectIdentifiers().contains(PSI.ON_CREATE_ACTION_NAVIGATE));
  }

  /**
   * Returns the default createa action (popup)
   */
  public static CreateAction getDefaultCreateAction(TopicMap tm) {
    return new CreateAction(tm.getTopicMapIF().getTopicBySubjectIdentifier(PSI.ON_CREATE_ACTION_POPUP), tm);
  }

//!   /**
//!    * Returns all available cardinalities.
//!    * 
//!    * @return A list containing CreateAction objects of all available
//!    *         cardinalities.
//!    */
//!   public static List getCreateActionTypes(TopicMap tm) {
//!     String query = "instance-of($d, on:edit-mode)?";
//! 
//!     Collection result = tm.getQueryWrapper().queryForList(query,
//!         OntopolyModelUtils.getRowMapperOneColumn());
//! 
//!     if (result.isEmpty())
//!       return Collections.EMPTY_LIST;
//! 
//!     List cardinalityTypes = new ArrayList();
//!     Iterator it = result.iterator();
//!     while (it.hasNext()) {
//!       cardinalityTypes.add(new CreateAction((TopicIF) it.next(), tm));
//!     }
//!     return cardinalityTypes;
//!   }

}