
package net.ontopia.topicmaps.cmdlineutils.statistics;

import java.util.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.core.index.*;
import net.ontopia.utils.*;
import net.ontopia.topicmaps.utils.*;

/**
 * Topic Map count methods
 */

public class TopicCounter {

  private TopicMapIF tm;
  private StringifierIF ts = TopicStringifiers.getDefaultStringifier();
  private int numberOfOccurrences, numberOfAssociations, numberOfTopics;
  private HashMap topicDetails, assocDetails, occurDetails;

  public TopicCounter(TopicMapIF topicmap) {
    numberOfOccurrences = 0;
    numberOfAssociations = 0;
    numberOfTopics = 0;
    tm = topicmap;
  }



  public void count() throws NullPointerException {
    Collection topics, assocs, occurs;
    topics = tm.getTopics();
    assocs = tm.getAssociations();
    
    Iterator it = topics.iterator();
    while (it.hasNext()) {
      TopicIF topic =  (TopicIF)it.next();
      occurs = topic.getOccurrences();
      numberOfOccurrences += occurs.size();
    }
    
    numberOfAssociations = assocs.size();
    numberOfTopics = topics.size();

  } 



  /**
   * Returns the number of topics in a topic map.
   */
  public int getNumberOfTopics() {
    return numberOfTopics;
  }

  /**
   * Returns the number of Associations in a topic map.
   */
  public int getNumberOfAssociations() {
    return numberOfAssociations;
  }

  /**
   * Returns the number of Occurrences in a topic map.
   */
  public int getNumberOfOccurrences() {
    return numberOfOccurrences;
  }

  public HashMap getTopicDetails() {
    return topicDetails;
  }

  public TopicIF getTopicDetails(String key) {
    return (TopicIF)topicDetails.get(key);
  }

  public HashMap getAssocDetails() {
    return assocDetails;
  }

  public TopicIF getAssocDetails(String key) {
    return (TopicIF)assocDetails.get(key);
  }

  public HashMap getOccurDetails() {
    return occurDetails;
  }

  public TopicIF getOccurDetails(String key) {
    return (TopicIF)occurDetails.get(key);
  }

  /**
   * Returns a HashMap which contains the topictype as the keyset and the 
   * number of times it occurs as the valueset.
   */
  public HashMap getTopicTypes() {
    Collection topictypes;
    HashMap retur = new HashMap();
    topicDetails = new HashMap();
    ClassInstanceIndexIF tindex = (ClassInstanceIndexIF)tm.getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");

    topictypes  = tindex.getTopicTypes();

    Iterator itt = topictypes.iterator();
    //This part checks the different types of Topics.
    while (itt.hasNext()) {
      TopicIF t_temp = (TopicIF)itt.next();
      Collection c_temp = tindex.getTopics(t_temp);
      retur.put(ts.toString(t_temp), new Integer(c_temp.size()));
      topicDetails.put(ts.toString(t_temp), t_temp);
    }
    return retur;
  }


  /**
   * Returns a HashMap which contains the associationtype as the keyset and the 
   * number of times it occurs as the valueset.
   */
  public HashMap getAssociationTypes() {
    Collection assoctypes;
    HashMap retur = new HashMap();
    assocDetails = new HashMap();
    ClassInstanceIndexIF tindex = (ClassInstanceIndexIF)
        tm.getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");

    assoctypes  = tindex.getAssociationTypes();

    Iterator ita = assoctypes.iterator();
    //This part checks the different types of Associations.
    while (ita.hasNext()) {
      TopicIF t_temp = (TopicIF)ita.next();
      Collection c_temp = tindex.getAssociations(t_temp);
      retur.put(ts.toString(t_temp), new Integer(c_temp.size()));
      assocDetails.put(ts.toString(t_temp), t_temp);
    }
    return retur;
  }


  /**
   * Returns a HashMap which contains the occurrencetype as the keyset and the 
   * number of times it occurs as the valueset.
   */
  public HashMap getOccurrenceTypes() {
    Collection occurstypes;
    HashMap retur = new HashMap();
    occurDetails = new HashMap();
    ClassInstanceIndexIF tindex = (ClassInstanceIndexIF)
        tm.getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");
    
    occurstypes  = tindex.getOccurrenceTypes();

    Iterator ito = occurstypes.iterator();
    //This part checks the different types of Occurrences.
    while (ito.hasNext()) {
      TopicIF t_temp = (TopicIF)ito.next();
      Collection c_temp = tindex.getOccurrences(t_temp);
      if (!c_temp.isEmpty() && t_temp != null) {
        retur.put(ts.toString(t_temp), new Integer(c_temp.size()));
        occurDetails.put(ts.toString(t_temp), t_temp);
      }
    }
    return retur;
  }

  public String getName(TopicIF topic) {
    return ts.toString(topic);
  }

  public Collection makeStrings(Collection topics) {
    Collection result = new ArrayList();
    Iterator it = topics.iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF)it.next();
      result.add(getName(topic));
    }
    return result;
  }


  public String[] sortAlpha(Collection collection) {
    String[] retur = new String[collection.size()];
    Iterator it = collection.iterator();
    int k = 0;
    while (it.hasNext()) {
      String temp = (String)it.next();
      retur[k] = temp;
      k++;
    }
    
    //Starting at the first index in the array.
    for (int i = 0; i+1 < retur.length; i++) {
      
      if (retur[i].compareTo(retur[i+1]) > 0){
        //Found one, shuffle it to the lowest index possible.
        String temp = retur[i];
        retur[i] = retur[i+1];
        retur[i+1] = temp;
        
        int j = i;
        boolean done = false;
        while (j != 0 && !done) {
          if (retur[j].compareTo(retur[j-1]) < 0) {
            temp = retur[j];
            retur[j] = retur[j-1];
            retur[j-1] = temp;
          } else done = true;
          j--;
        }//end of while.
      }//end of if
    }//end of for
    return retur;
  }
}//end of class TopicCount





