
package tm;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.utils.DeciderIF;

/**
 * This class provides a way to find out whether this should be part of an update or not.
 * Do not update any associations except for:
 * - parent-child
 * - created-by
 * 
 * Should the future require to change "contains" just change the returnvalue.
 * Again "is-about" is explicitly not updated because that would break user's tagging of the wikipage whenever the wikipage changes.
 * 
 * @author mfi
 */

public class WikiPageDecider implements DeciderIF {

  public boolean ok(Object arg0) {
   if(arg0 instanceof AssociationIF ){
      AssociationIF assoc = (AssociationIF) arg0;
      if(OntopiaAdapter.isInAssociation(OntopiaAdapter.ASSOC_PARENT_CHILD_PSI, assoc)){
        return true;
      }
      if(OntopiaAdapter.isInAssociation(OntopiaAdapter.ASSOC_CREATED_BY_PSI, assoc)){
        return true;
      }
      if(OntopiaAdapter.isInAssociation(OntopiaAdapter.ASSOC_CONTAINS_PSI, assoc)){
        return false;
      }
      if(OntopiaAdapter.isInAssociation(OntopiaAdapter.ASSOC_PARENT_IS_ABOUT_PSI, assoc)){
        return false;
      }

      return false; // do not update if you are anything but one of the above mentioned association types (i.e. 'is-about')

      }
   return true;
  }

}
