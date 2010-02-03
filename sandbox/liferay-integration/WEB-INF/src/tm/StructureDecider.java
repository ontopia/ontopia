package tm;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.utils.DeciderIF;

public class StructureDecider implements DeciderIF{

  public boolean ok(Object arg0) {
    if(arg0.getClass().toString().equals(net.ontopia.topicmaps.impl.basic.Association.class.toString())){
      AssociationIF assoc = (AssociationIF) arg0;
      if(OntopiaAdapter.isInAssociation(OntopiaAdapter.SUB_SUPERTYPE_PSI, assoc)){
        return true;
      }
      return false; // something different than the above? Do not update!
    }
    return true; // update everything else except association
  }

}
