package tm;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.utils.DeciderIF;

/**
 * This class provides a way to find out whether this should be part of an update or not.
 * All associations shall not be updated except for sub/supertype.
 * Everythings that's not an association shall be updated.
 *
 * @author mfi
 */

public class StructureDecider implements DeciderIF{

  public boolean ok(Object arg0) {
    if(arg0 instanceof AssociationIF ){
      AssociationIF assoc = (AssociationIF) arg0;
      if(OntopiaAdapter.isInAssociation(OntopiaAdapter.SUB_SUPERTYPE_PSI, assoc)){
        // If this structure is moved to an other superclass we would want to know that
        return true;
      }
      return false; // Within associations: Only the above shall be update. All other associations shall not be updated.
    }
    return true; // The complement of associations might be updated
  }

}
