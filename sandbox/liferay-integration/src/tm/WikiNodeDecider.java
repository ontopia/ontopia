
package tm;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.utils.DeciderIF;

public class WikiNodeDecider implements DeciderIF {

  public boolean ok(Object arg0) {
    if(arg0.getClass().toString().equals(net.ontopia.topicmaps.impl.basic.Association.class.toString())){
      AssociationIF assoc = (AssociationIF) arg0;
      if(OntopiaAdapter.isInAssociation(OntopiaAdapter.ASSOC_CONTAINS_PSI, assoc)){
        return false;
      } else {
        return false; // == return false if you are an association
      }
  } else {
      return true; // == return true if you are anything but an association
  }
  }
}
