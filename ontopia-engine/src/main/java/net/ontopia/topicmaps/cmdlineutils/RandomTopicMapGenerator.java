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

package net.ontopia.topicmaps.cmdlineutils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.utils.CollectionUtils;

/**
 * INTERNAL: Utility for randomly populating topic maps.
 */
public class RandomTopicMapGenerator {

  private TopicMapBuilderIF b;
  private Random r = new Random();
  
  // ontology
  private int topicTypes = 20;
  private int nameTypes = 4;
  private int occurrenceTypes = 40;
  private int associationTypes = 30;
  
  
  // instances
  private int topics;

  class InstanceConfig {
    private int identitiesMin = 0;
    private int identitiesAvg = 1;
    private int identitiesMax = 2;
    
    private int namesMin = 0;
    private int namesAvg = 1;
    private int namesMax = 3;
    
    private int occurrencesMin = 0;
    private int occurrencesAvg = 3;
    private int occurrencesMax = 15;
    
    private int rolesMin = 0;
    private int rolesAvg = 3;
    private int rolesMax = 10;

  }

  class DataPool {
    private List topics;
    private List topicTypes;
    private List nameTypes;
    private List occurrenceTypes;    
    private List associationTypes;
    private Map roleTypes;
  }
  
  public RandomTopicMapGenerator(TopicMapIF tm) {
    this.b = tm.getBuilder();
  }

  public void populateTopicMap() {
    InstanceConfig ic = new InstanceConfig();
    DataPool dp = new DataPool();
    double avgChars = ic.identitiesAvg +
                      ic.namesAvg + ic.occurrencesAvg + ic.rolesAvg;
    System.out.println("T: " + topics + " C: " + avgChars);
    
    // ontology
    dp.topics = new ArrayList(100);
    dp.topicTypes = makeTopics("tt", topicTypes);
    dp.nameTypes = makeTopics("nt", nameTypes);
    dp.occurrenceTypes = makeTopics("ot", occurrenceTypes);
    dp.associationTypes = makeTopics("at", associationTypes);
    dp.roleTypes = new HashMap(associationTypes);
    for (int i=0; i < associationTypes; i++) {
      List roleTypes = makeTopics("rt", 2); // binary only for now
      dp.roleTypes.put(dp.associationTypes.get(i), roleTypes);
    }
    System.out.println("TT: " + dp.topicTypes.size() +
                       " NT: " + dp.nameTypes.size() +
                       " OT: " + dp.occurrenceTypes.size() +
                       " AT: " + dp.associationTypes.size());
    
    for (int i=0; i < topics+1; i++) {
      if (i % 100 == 0 && i != 0) {
        System.out.println("I: " + i + " " + dp.topics.size());
      }
      TopicIF t = makeTopic("i");
      t.addType((TopicIF)CollectionUtils.getRandom(dp.topicTypes));
      
      if (dp.topics.size() < 100) {
        dp.topics.add(t);
      } else {
        // remove random
        int chosen = r.nextInt(dp.topics.size());
        dp.topics.set(chosen, t);
      }
      addCharacteristics(t, ic, dp);
    }
    System.out.println("T: " + topics);
  }

  private TopicIF makeTopic(String prefix) {
    TopicIF topic = b.makeTopic();
    // default name
    b.makeTopicName(topic, prefix + "-" + topic.getObjectId());
    return topic;
  }
  
  private List makeTopics(String prefix, int count) {
    List result = new ArrayList(count);
    for (int i=0; i < count; i++) {
    result.add(makeTopic(prefix));
    }
    return result;
  }

  private int getCount(int min, int avg, int max) {
    int x = r.nextInt(2*(max-avg));
    if (x < min) {
      return min;
    } else if (x > max) {
      return max;
    } else {
      return x;
    }
  }
  
  private void addCharacteristics(TopicIF t, InstanceConfig ic, DataPool dp) {
    int c;
    // identities
    c = getCount(ic.identitiesMin, ic.identitiesAvg, ic.identitiesMax);
    addIdentities(t, dp, c);
    // names
    c = getCount(ic.namesMin, ic.namesAvg, ic.namesMax);
    addNames(t, dp, c);
    // occurrences
    c = getCount(ic.occurrencesMin, ic.occurrencesAvg, ic.occurrencesMax);
    addOccurrences(t, dp, c);
    // associations
    c = getCount(ic.rolesMin, ic.rolesAvg, ic.rolesMax);
    addRoles(t, dp, c);
  }

  private void addIdentities(TopicIF t, DataPool dp, int count) {
    for (int i=0; i < count; i++) {
      t.addSubjectIdentifier(URILocator.create("http://example.org/foo/" + t.getObjectId() + "/" + (i+1)));
    }
  }

  private void addNames(TopicIF t, DataPool dp, int count) {
    // typed name
    for (int i=0; i < count; i++) {
      b.makeTopicName(t, (TopicIF)CollectionUtils.getRandom(dp.nameTypes),
																		 "topicname-" + (i+1));
    }
  }

  private void addOccurrences(TopicIF t, DataPool dp, int count) {
    for (int i=0; i < count; i++) {
      b.makeOccurrence(t, (TopicIF)CollectionUtils.getRandom(dp.occurrenceTypes), 
																					"occurrence-" + (i+1));
    }
  }

  private void addRoles(TopicIF t, DataPool dp, int count) {
    for (int i=0; i < count; i++) {
      TopicIF atype = (TopicIF)CollectionUtils.getRandom(dp.associationTypes); 
      AssociationIF a = b.makeAssociation(atype);

      List rtypes = (List)dp.roleTypes.get(atype);
      Iterator iter = rtypes.iterator();
      while (iter.hasNext()) {
        TopicIF rtype = (TopicIF)iter.next();
        TopicIF player = (TopicIF)CollectionUtils.getRandom(dp.topics);
        b.makeAssociationRole(a, rtype, player);
      }
    }
  }
  
  public static void main(String[] args) throws Exception {

    String tmuri = args[0];
    
    TopicMapIF tm = ImportExportUtils.getReader(tmuri).read();
    TopicMapStoreIF store = tm.getStore();
    try {
      RandomTopicMapGenerator rg = new RandomTopicMapGenerator(tm);
      rg.topics = Integer.parseInt(args[1]);
      rg.populateTopicMap();

      if (args.length > 2) {
        ImportExportUtils.getWriter(new File(args[2])).write(tm);
      }
      store.commit();
    } finally {
      store.close();
    }
  }
  
}
