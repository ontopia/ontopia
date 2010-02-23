package tm;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.utils.DeciderIF;

/**
 * This class provides a way to find out whether this should be part of an update or not.
 * No associations shall be updated.
 * Those that might be included in the future are listed anyway. Just change the returnvalue to true to enable updates.
 *
 * @see DeciderIF
 * @author mfi
 */

public class UserDecider implements DeciderIF{


  public boolean ok(Object arg0) {
    if(arg0 instanceof AssociationIF){
      AssociationIF assoc = (AssociationIF) arg0;
      if(OntopiaAdapter.isInAssociation(OntopiaAdapter.ASSOC_CREATED_BY_PSI, assoc)){
        return false;
      }
      if(OntopiaAdapter.isInAssociation(OntopiaAdapter.ASSOC_USER_APPROVING_PSI, assoc)){
        return false;
      }

      return false; // It is an association that does not match any of the above. Do not update!
      
    } else {
      return true; // it is not an association. It has to be updated!
    }

  }

}
