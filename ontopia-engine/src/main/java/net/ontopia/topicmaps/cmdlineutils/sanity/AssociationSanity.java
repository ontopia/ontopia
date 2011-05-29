
package net.ontopia.topicmaps.cmdlineutils.sanity;

import java.util.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.utils.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.infoset.core.*;

/**
 * Topic map sanity controller.
 */


public class AssociationSanity {

  private StringifierIF ts = TopicStringifiers.getDefaultStringifier();


  /**
   * This class finds all duplicate associations.
   */

  private HashMap checkedAssociations, duplicateAssociations;
  private HashMap numberOfDuplicates, assocTypes;
  private Collection assocs;
  private TopicMapIF tm;

  public AssociationSanity(TopicMapIF tm) {
    this.tm = tm;
    checkedAssociations = new HashMap();
    duplicateAssociations = new HashMap();
    numberOfDuplicates = new HashMap();
    assocTypes = new HashMap();
  }

  /**
   * Returns a HashMap containing the duplicate Associations
   */
  public HashMap getDuplicateAssociations() {
    return duplicateAssociations;
  }


  /**
   * Returns a HashMap containing the number of duplicate Associations.
   */
  public HashMap getNumberOfDuplicates() {
    return numberOfDuplicates;
  }

  /**
   * Returns a HashMap of all the duplicate association types, using the string key
   * as the keyvalue.
   */
  public HashMap getAssociationTypes() {
    return assocTypes;
  }



  //Traverses the Topic Map.
  public void traverse() throws NullPointerException {
    assocs = tm.getAssociations();    
    Iterator it = assocs.iterator();

    while (it.hasNext()) {
      
      AssociationIF temp = (AssociationIF)it.next();

      String key = createKey(temp);
     
      if (checkedAssociations.containsKey(key)) {
        //This association already exist.
        //Update number of occurrences and put it back in the
        //HashMap.
        Integer tempint = (Integer)numberOfDuplicates.get(key);
        if (tempint == null){
          //first occurrences of this duplicate Association.
          numberOfDuplicates.put(key, new Integer(2));
        } else {
          //update the number of occurrences for this duplicate assoc.
          int number = tempint.intValue() + 1;
          numberOfDuplicates.put(key, new Integer(number));
        }
        //add the duplicate assoc to a HashMap.
        duplicateAssociations.put(key, temp);
      } else { 
        //This association has no duplicates - (yet).
        checkedAssociations.put(key, temp);
      }
      
    }

  }

  //Creates a string key for the association.
  //This key is made from the association and it's roles.
  private String createKey(AssociationIF aif) {
    //Creates a key for the association, and its roles.
    HashMap types = new HashMap();
    String assocname = "";
    //assoc name as a single string.
    try {
      assocname  = getTopicId(aif.getType());
    } catch (NullPointerException e) {
      assocname = "null";
    }
    //Role names as an array of strings.
    Collection roles = aif.getRoles();
    Iterator it = roles.iterator();
    int size = roles.size();
    int i = 0;
    String [] rolenames = new String[size];
  
    //Places the rolesnames in the array.
    while (it.hasNext()) {
      AssociationRoleIF arif = (AssociationRoleIF)it.next();

      //rolenames[i] = getTopicId(arif.getPlayer());
      rolenames[i] = ts.toString(arif.getPlayer());
      types.put(rolenames[i], arif.getType());
      i++;
    }
    
    //Sorts the rolenames in lexiographical order.
    java.util.Arrays.sort(rolenames);
    //Places the assocname first in the return string.
    String retur = assocname;
    //Adds the sorted rolenames using a '$'as a delimiter
    for (i = 0; i < rolenames.length; i++) {
      if (types.get(rolenames[i]) != null) {
        retur += "$" +rolenames[i] + "$" + getTopicId((TopicIF)types.get(rolenames[i]));  
      } else {
        retur += "$" +rolenames[i] + "$null";
      }  
    }
    assocTypes.put(retur, aif.getType());
    return retur;
  }

  private String getTopicId(TopicIF topic) {
    String id = null;
    if (topic.getTopicMap().getStore().getBaseAddress() != null) {
      String base = topic.getTopicMap().getStore().getBaseAddress().getAddress();
      Iterator it = topic.getItemIdentifiers().iterator();
      while (it.hasNext()) {
        LocatorIF sloc = (LocatorIF) it.next();
        if (sloc.getAddress().startsWith(base)) {
          String addr = sloc.getAddress();
          id = addr.substring(addr.indexOf('#') + 1);
          break;
        }
      }
    }
    if (id == null)
      id = "id" + topic.getObjectId();
    return id;
  }



}
