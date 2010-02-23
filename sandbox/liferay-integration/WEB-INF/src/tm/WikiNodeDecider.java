
package tm;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.utils.DeciderIF;

/**
 * This class provides a way to find out whether this should be part of an update or not.
 * Do not update any associations. 
 * Should the future require one of the explicitly named ones here to be updated, just set the returnvalue to true.
 *
 * @see DeciderIF
 * @author mfi
 */

public class WikiNodeDecider implements DeciderIF {

  public boolean ok(Object arg0) {
    if(arg0 instanceof AssociationIF){
      AssociationIF assoc = (AssociationIF) arg0;
      if(OntopiaAdapter.isInAssociation(OntopiaAdapter.ASSOC_CONTAINS_PSI, assoc)){
        return false;
      } 
      if(OntopiaAdapter.isInAssociation(OntopiaAdapter.ASSOC_CREATED_BY_PSI, assoc)){
        return false;
      }

      return false; // == return false if you are an association
      
  } else {
      return true; // == return true if you are anything but an association
  }
  }
}
