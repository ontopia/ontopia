
package ontopoly.model.ontopoly;

import ontopoly.model.PSI;
import ontopoly.model.CreateActionIF;
import ontopoly.model.OntopolyTopicMapIF;
import net.ontopia.topicmaps.core.TopicIF;

/**
 * Represents the edit mode of a field.
 */
public class CreateAction extends Topic implements CreateActionIF {

  /**
   * Creates a new CreateAction object.
   */
  public CreateAction(TopicIF topic, OntopolyTopicMapIF tm) {
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
  public static CreateActionIF getDefaultCreateAction(OntopolyTopicMapIF tm) {
    return new CreateAction(tm.getTopicMapIF().getTopicBySubjectIdentifier(PSI.ON_CREATE_ACTION_POPUP), tm);
  }

}
