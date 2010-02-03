package tm;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.utils.DeciderIF;

public class UserDecider implements DeciderIF{


  public boolean ok(Object arg0) {
    if(arg0.getClass().toString().equals(net.ontopia.topicmaps.impl.basic.Association.class.toString())){
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
