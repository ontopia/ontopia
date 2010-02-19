package tm;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.utils.DeciderIF;

public class StructureDecider implements DeciderIF{

  public boolean ok(Object arg0) {
    if(arg0.getClass().toString().equals(net.ontopia.topicmaps.impl.basic.Association.class.toString())){
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
