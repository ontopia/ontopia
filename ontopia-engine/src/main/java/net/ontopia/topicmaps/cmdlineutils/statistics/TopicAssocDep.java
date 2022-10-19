/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.topicmaps.cmdlineutils.statistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.utils.TopicStringifiers;

/**
 * Class used for locating associations and their types.
 */ 
public class TopicAssocDep {
  private static final String NULL = "null";

  private HashMap associations;

  public TopicAssocDep(TopicMapIF tm) throws NullPointerException {
    associations = new HashMap();
    traverse(tm.getAssociations());
  }

  /**
   * Returns a set of keys for the associations
   */
  public Collection getAssociations() {
    return associations.keySet();
  }

  /**
   * Gets the id for the association type.
   */
  public String getAssociationTypeId(String key) {
    if (key == null || !associations.containsKey(key))
      return NULL;
    return ((InternalAssociation)associations.get(key)).getAssociationTypeId();
  }

  /**
   * Gets the name of the association.
   */
  public String getAssociationType(String key) {
    if (key == null || !associations.containsKey(key))
      return NULL;
    return ((InternalAssociation)associations.get(key)).getAssociationType();
  }


  /**
   * Gets the number of times this association occurres in the topicmap.
   */
  public int getNumberOfOccurrences(String key) {
    return ((InternalAssociation)associations.get(key)).getNumberOfOccurrences();
  }


  /**
   * Returns a hashmap containg the id of the assocrltype, and the name of assocrltype
   */
  public HashMap getAssociationRoleTypes(String key) {
    if (key == null || !associations.containsKey(key))
      return null;
    return ((InternalAssociation)associations.get(key)).getAssociationRoleTypes();
  }
  
  /**
   * Return a hashmap containg the id and the name of all the topictypes for all the 
   * topics that play in this association.
   */
  public HashMap getAssociationRoles(String key) {
    if (key == null || !associations.containsKey(key))
      return null;
    return ((InternalAssociation)associations.get(key)).getAssociationRoles();
  }

  /**
   * Returns a Collection of the different associations that have this associationtype.
   */
  public Collection getAssociationDetails(String key) {
    if (key == null || !associations.containsKey(key))
      return null;
    return ((InternalAssociation)associations.get(key)).getAssociationDetails();
  }

  /**
   * Gets the association roles ordered aplhabetically
   */
  public String[] getAssociationRoleTypesOrdered(String key) {
    if (key == null || !associations.containsKey(key))
      return null;
    return ((InternalAssociation)associations.get(key)).getAssociationRoleTypesOrdered();
  }


  public String[] getAssociationDetails(String key, AssociationIF association) {
    if (key == null || !associations.containsKey(key))
      return null;
    return ((InternalAssociation)associations.get(key)).getAssociationDetails(association);
  }

  

  //----------------------------------------------------
  // Internal methods
  //----------------------------------------------------


  //Traverses the Topic Map.
  private void traverse(Collection assocs) {
    Iterator it = assocs.iterator();
    while (it.hasNext()) {
      
      AssociationIF temp = (AssociationIF)it.next();
      String key = createKey(temp);
      
      InternalAssociation assoc;
      if (associations.containsKey(key)) {
        assoc = (InternalAssociation)associations.get(key);
        assoc.increment();
      }
      else {
        assoc = new InternalAssociation(temp);
        associations.put(key, assoc);
      }
      assoc.addAssociation(temp);
    }
  }


  //Creates a string key for the association.
  private String createKey(AssociationIF aif) {
    String assocname = "";
    try {
      assocname  = TopicStringifiers.toString(aif.getType());
    } catch (NullPointerException e) {
      assocname = NULL;
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
      if (arif.getType() != null)
        rolenames[i] = getName(arif.getType());
      else
        rolenames[i] = NULL;
      i++;
    }
    //Sorts the rolenames in lexiographical order.
    sortKey(rolenames);
    //Places the assocname first in the return string.
    String retur = assocname;
    //Adds the sorted rolenames using a '$'as a delimiter
    for (i = 0; i < rolenames.length; i++) {
      retur += "$" + rolenames[i];
    }
    return retur;
  }


  /**
   * Insert Sort, which sorts an array of strings in lexiographical order
   */
  private void sortKey(String [] names) {
    for (int i = 0; i+1 < names.length; i++) {
      if (names[i].compareTo(names[i+1]) > 0){
        //Found one, shuffle it to the lowest index possible.
        String temp = names[i]; names[i] = names[i+1]; names[i+1] = temp;
        int j = i; boolean done = false;
        while (j != 0 && !done) {

          if (names[j].compareTo(names[j-1]) < 0) {
            temp = names[j];
            names[j] = names[j-1];
            names[j-1] = temp;
          } else done = true;

          j--;
        }//end of while.
      }//end of if
    }//end of for
  }//end of method



  public String[] sortAlpha(Collection collection) {
    String[] retur = new String[collection.size()];
    Iterator it = collection.iterator();
    
    for (int i = 0; i < collection.size(); i++) {
      retur[i] = (String)it.next();
    }
    
    //Starting at the first index in the array.
    for (int i = 0; i < retur.length - 1; i++) {
      
      if (retur[i].compareTo(retur[i+1]) > 0){
        //Found one, shuffle it to the lowest index possible.
        String temp = retur[i]; retur[i] = retur[i+1]; retur[i+1] = temp;
        
        int j = i;
        boolean done = false;
        while (j != 0 && !done) {
          if (retur[j].compareTo(retur[j-1]) < 0) {
            temp = retur[j]; retur[j] = retur[j-1]; retur[j-1] = temp;
          } else done = true;
          j--;
        }//end of while.
      }//end of if
    }//end of for
    return retur;
  }


  //Gets the name of the topic
  private String getName(TopicIF topic) {
    if (topic == null)
      return NULL;
    else {
      String name = TopicStringifiers.toString(topic);
      if ("[No name]".equalsIgnoreCase(name))
        name = "{topicid: " + topic.getObjectId() + "}";
      return name;
    }
  }


  /**
   * Innerclass that is used to represent each type of association, and
   * also keep track of all the different assocations of this type.
   */
  private class InternalAssociation {

    private AssociationIF association;
    private TopicIF associationtype;
    private HashMap roleTypes;
    private HashMap assocRoles;
    private int number_of_assocs;
    private Collection associations;
    private String[] roles;
    
    InternalAssociation(AssociationIF association) {
      this.associationtype = association.getType();
      this.association = association;
      roleTypes = addAssocRoleTypes(association);
      number_of_assocs = 1;
      assocRoles = new HashMap();
      associations = new ArrayList();
      //roles = getAssociationRoleTypesOrdered();
    }

    /**
     * Returns the object id for the association type
     */
    protected String getAssociationTypeId() {
      return (associationtype == null ? NULL : associationtype.getObjectId());
    }


    /**
     * Returns the association type.
     */
    protected String getAssociationType() {
      return getName(associationtype);
    }

    
    /**
     * Returns the number of occurrences for this association
     */
    protected int getNumberOfOccurrences() {
      return number_of_assocs;
    }

    /**
     * Returns a hashmap containg the id of the assocrltype, and the name of assocrltype
     */
    protected HashMap getAssociationRoleTypes() {
      return roleTypes;
    }

    /**
     * Return a hashmap containg the id and the name of all the topictypes for all the 
     * topics that play in this association.
     */
    protected HashMap getAssociationRoles() {
      return assocRoles;
    }

    /**
     * Returns all the associations of this type.
     */
    protected Collection getAssociationDetails() {
      return associations;
    }

    
    /**
     * Returns the players of of this association ordred by the role types.
     */
    protected String[] getAssociationDetails(AssociationIF association) {
      String[] result = new String[association.getRoles().size()];
      Iterator it = association.getRoles().iterator();
      while (it.hasNext()) {
        AssociationRoleIF role = (AssociationRoleIF)it.next(); 
        String name = getName(role.getType());
        roles = getAssociationRoleTypesOrdered();
        for (int i = 0; i < roles.length; i++) {
          // Inserts the correct player name and id in the correct alphabetical order.
          if (name.equals(roles[i]) && result[i] == null) {
            result[i] = getName(role.getPlayer()) + "$" + role.getPlayer().getObjectId();
          }
        }
      }
      return result;
    }
    
    /**
     * Gets the assoc types ordered.
     */
    protected String[] getAssociationRoleTypesOrdered() {
      Collection roletypes = new ArrayList();
      Iterator it = association.getRoles().iterator();
      while (it.hasNext()) {
        String name = getName(((AssociationRoleIF)it.next()).getType());
        roletypes.add(name);
      }
      return sortAlpha(roletypes);
    }


    /**
     * Increments the number of associations with the same role types.
     */
    protected void increment() {
      number_of_assocs++;
    }
    
    /**
     * Adds an association to the collection of assocation with the same
     * types, and roles.
     */
    protected void addAssociation(AssociationIF association) {
      associations.add(association);
      addAssociationRoles(association);
    }


    /**
     * Gets the id for the assocrltypes, and their names.
     */
    private HashMap addAssocRoleTypes(AssociationIF association) {
      HashMap temp = new HashMap();
      Iterator it = association.getRoles().iterator();
      while (it.hasNext()) {
        AssociationRoleIF assocrl = (AssociationRoleIF) it.next();
        TopicIF type = assocrl.getType();
        if (type != null)
          temp.put(type.getObjectId(), getName(type));
        else 
          temp.put(NULL, getName(null));
      }
      return temp;
    }

    /**
     * Adds the id for all the different topic types that play a role 
     * in this association.
     */
    private void addAssociationRoles(AssociationIF association) {
      Iterator it = association.getRoles().iterator();
      while (it.hasNext()) {
        AssociationRoleIF assocrl = (AssociationRoleIF)it.next();
        if (assocrl.getPlayer() == null) {
          if (!assocRoles.containsValue(getName(null))) assocRoles.put(NULL, getName(null));
        } else {
          Iterator iter = assocrl.getPlayer().getTypes().iterator();
          while (iter.hasNext()) {
            TopicIF temp_topic = (TopicIF)iter.next();
            assocRoles.put(temp_topic.getObjectId(), getName(temp_topic));
          }
        }
      }
    }
  } // inner class
}//end of class.
