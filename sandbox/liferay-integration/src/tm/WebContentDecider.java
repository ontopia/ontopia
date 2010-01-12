package tm;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.utils.DeciderIF;

public class WebContentDecider implements DeciderIF {

  public boolean ok(Object obj) {
  if(obj.getClass().toString().equals(net.ontopia.topicmaps.impl.basic.Association.class.toString())){
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

    return false; // it is an association of none of the above mentioned types. Do not update!

  } else {
    return true; // it is not an association. It has to be updated!
  }
  }
}


