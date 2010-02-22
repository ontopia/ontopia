package tm;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.utils.DeciderIF;

/**
 * This class provides a way to find out whether this should be part of an update or not.
 * No associations shall be updated except:
 * - Created By
 * - Has_workflow_state
 * - approving
 *
 * "is-about" shall explicitly NOT be updated, because that would break user's tagging on any WebContent whenever it is changed in Liferay.
 *
 * @author mfi
 */

public class WebContentDecider implements DeciderIF {

  public boolean ok(Object obj) {
  if(obj instanceof AssociationIF){
    AssociationIF assoc = (AssociationIF) obj;
    if(OntopiaAdapter.isInAssociation(OntopiaAdapter.ASSOC_CREATED_BY_PSI, assoc)){
      return true;
    }
    if(OntopiaAdapter.isInAssociation(OntopiaAdapter.ASSOC_HAS_WORKFLOW_STATE_PSI, assoc)){
      return true;
    }
    if(OntopiaAdapter.isInAssociation(OntopiaAdapter.ASSOC_USER_APPROVING_PSI, assoc)){
      return true;
    }
    if(OntopiaAdapter.isInAssociation(OntopiaAdapter.ASSOC_PARENT_IS_ABOUT_PSI, assoc)){
        return false;
      }

    return false; // it is an association of none of the above mentioned types. Do not update!

  } else {
    return true; // it is not an association. It has to be updated!
  }
  }
}


