
package tm;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.utils.DeciderIF;

public class WikiPageDecider implements DeciderIF {

  public boolean ok(Object arg0) {
   if(arg0.getClass().toString().equals(net.ontopia.topicmaps.impl.basic.Association.class.toString())){
      AssociationIF assoc = (AssociationIF) arg0;
      if(OntopiaAdapter.isInAssociation(OntopiaAdapter.ASSOC_PARENT_CHILD_PSI, assoc)){
        return true;
      }
      if(OntopiaAdapter.isInAssociation(OntopiaAdapter.ASSOC_CREATED_BY_PSI, assoc)){
        return true;
      }
      return false; // do not update if you are anything but one of the above mentioned association types (i.e. 'is-about')

      }
   return true;
  }

}
