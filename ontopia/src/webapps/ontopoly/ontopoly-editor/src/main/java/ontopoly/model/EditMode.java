// $Id: EditMode.java,v 1.1 2008/10/23 05:18:36 geir.gronmo Exp $

package ontopoly.model;

import java.util.Collection;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;

/**
 * Represents the edit mode of a field.
 */
public class EditMode extends Topic {

  /**
   * Creates a new EditMode object.
   */
  public EditMode(TopicIF topic, TopicMap tm) {
    super(topic, tm);
  }
  
  public boolean equals(Object obj) {
    if (!(obj instanceof EditMode))
      return false;

    EditMode cardinality = (EditMode) obj;
    return (getTopicIF().equals(cardinality.getTopicIF()));
  }

  public LocatorIF getLocator() {
    Collection<LocatorIF> subjectIdentifiers = getTopicIF().getSubjectIdentifiers();
    if (subjectIdentifiers.contains(PSI.ON_EDIT_MODE_EXISTING_VALUES_ONLY)) {
      return PSI.ON_EDIT_MODE_EXISTING_VALUES_ONLY;
    } else if (subjectIdentifiers.contains(PSI.ON_EDIT_MODE_NEW_VALUES_ONLY)) {
      return PSI.ON_EDIT_MODE_NEW_VALUES_ONLY;
    } else if (subjectIdentifiers.contains(PSI.ON_EDIT_MODE_NO_EDIT)) {
      return PSI.ON_EDIT_MODE_NO_EDIT;
    } else if (subjectIdentifiers.contains(PSI.ON_EDIT_MODE_NORMAL)) {
      return PSI.ON_EDIT_MODE_NORMAL;
    } else if (subjectIdentifiers.contains(PSI.ON_EDIT_MODE_OWNED_VALUES)) {
      return PSI.ON_EDIT_MODE_OWNED_VALUES;
    } else {
      return null;
    }
  }

  public boolean isExistingValuesOnly() {
    return (getTopicIF().getSubjectIdentifiers().contains(PSI.ON_EDIT_MODE_EXISTING_VALUES_ONLY));
  }

  public boolean isNewValuesOnly() {
    return (getTopicIF().getSubjectIdentifiers().contains(PSI.ON_EDIT_MODE_NEW_VALUES_ONLY));
  }

  public boolean isOwnedValues() {
    return (getTopicIF().getSubjectIdentifiers().contains(PSI.ON_EDIT_MODE_OWNED_VALUES));
  }

  public boolean isNoEdit() {
    return (getTopicIF().getSubjectIdentifiers().contains(PSI.ON_EDIT_MODE_NO_EDIT));
  }

  /**
   * Returns the default edit mode (normal)
   */
  public static EditMode getDefaultEditMode(TopicMap tm) {
    return new EditMode(tm.getTopicMapIF().getTopicBySubjectIdentifier(PSI.ON_EDIT_MODE_NORMAL), tm);
  }

}
