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

package net.ontopia.topicmaps.impl.basic.index;

import java.util.Iterator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ClassInstanceTest {
    protected ClassInstanceIndexIF index;
    protected TopicMapBuilderIF builder;
    protected TopicMapIF topicmap;
    
    @Before
    public void setUp() {
        topicmap = makeTopicMap();
        index = (ClassInstanceIndexIF) topicmap.getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");
    }

    // intended to be overridden
    protected TopicMapIF makeTopicMap() {
        InMemoryTopicMapStore store = new InMemoryTopicMapStore();
        builder = store.getTopicMap().getBuilder();
        return store.getTopicMap();
    }
    
    // --- Test cases

    @Test
    public void testAssociationRoles() {
        // STATE 1: empty topic map
        Assert.assertTrue("index finds role types in empty topic map",
               index.getAssociationRoleTypes().size() == 0);
        

        // STATE 2: topic map has some topics in it
        TopicIF at = builder.makeTopic();
        TopicIF art1 = builder.makeTopic();
        TopicIF art2 = builder.makeTopic();
        TopicIF art3 = builder.makeTopic();
        TopicIF t1 = builder.makeTopic();
        TopicIF t2 = builder.makeTopic();

        AssociationIF a1 = builder.makeAssociation(at);
        AssociationRoleIF r1 = builder.makeAssociationRole(a1, art1, t1);
        AssociationRoleIF r2 = builder.makeAssociationRole(a1, art2, t2);

        AssociationIF a2 = builder.makeAssociation(at);
        builder.makeAssociationRole(a2, art3, t1);
        builder.makeAssociationRole(a2, art3, t2);
        
        Assert.assertTrue("role type not found",
               index.getAssociationRoles(art1).size() == 1);

        Assert.assertTrue("roles not found via type",
               index.getAssociationRoles(art1).iterator().next().equals(r1));

        Assert.assertTrue("index claims role type not used",
               index.usedAsAssociationRoleType(art1));

        
        Assert.assertTrue("role type not found",
               index.getAssociationRoles(art2).size() == 1);

        Assert.assertTrue("roles not found via type",
               index.getAssociationRoles(art2).iterator().next().equals(r2));

        Assert.assertTrue("index claims role type not used",
               index.usedAsAssociationRoleType(art2));

        
        Assert.assertTrue("spurious association roles found",
               index.getAssociationRoles(t1).size() == 0);

        Assert.assertTrue("index claims ordinary topic used as role type",
               !index.usedAsAssociationRoleType(t1));

        
        Assert.assertTrue("roles with art3 types not found",
               index.getAssociationRoles(art3).size() == 2);

        Assert.assertTrue("index claims art3 not used as role type",
               index.usedAsAssociationRoleType(art3));


        Assert.assertTrue("index loses or invents role types",
               index.getAssociationRoleTypes().size() == 3);

        Assert.assertTrue("index forgets that topic is used as a type",
               index.usedAsType(art1));

        // STATE 3: topic map has duplicates
        AssociationIF a3 = builder.makeAssociation(at);
        AssociationRoleIF r5 = builder.makeAssociationRole(a3, art1, t1);
        builder.makeAssociationRole(a3, art2, t2);
        
        Assert.assertTrue("role type not found",
               index.getAssociationRoles(art1).size() == 2);
        Assert.assertTrue("roles not found via type",
               index.getAssociationRoles(art1).contains(r5));
        Assert.assertTrue("duplicate role types not suppressed",
               index.getAssociationRoleTypes().size() == 3);
    }

    @Test
    public void testAssociations() {
        // STATE 1: empty topic map
        Assert.assertTrue("index finds association types in empty topic map",
               index.getAssociationTypes().size() == 0);
        

        // STATE 2: topic map has some topics in it
        TopicIF at1 = builder.makeTopic();
        TopicIF at2 = builder.makeTopic();
        TopicIF t1 = builder.makeTopic();
        TopicIF t2 = builder.makeTopic();

        AssociationIF a1 = builder.makeAssociation(at1);
        builder.makeAssociationRole(a1, builder.makeTopic(), t1);
        builder.makeAssociationRole(a1, builder.makeTopic(), t2);

        AssociationIF a2 = builder.makeAssociation(at2);
        builder.makeAssociationRole(a2, builder.makeTopic(), t1);
        builder.makeAssociationRole(a2, builder.makeTopic(), t2);
        
        Assert.assertTrue("association type not found",
               index.getAssociations(at1).size() == 1);

        Assert.assertTrue("associations not found via type",
               index.getAssociations(at1).iterator().next().equals(a1));

        Assert.assertTrue("index claims association type not used",
               index.usedAsAssociationType(at1));

        
        Assert.assertTrue("spurious association types found",
               index.getAssociations(t1).size() == 0);

        Assert.assertTrue("index claims ordinary topic used as association type",
               !index.usedAsAssociationType(t1));

        
        Assert.assertTrue("associations with at2 type not found",
               index.getAssociations(at2).size() == 1);

        Assert.assertTrue("associations not found via at2 type",
               index.getAssociations(at2).iterator().next().equals(a2));
        
        Assert.assertTrue("index claims at2 not used as association type",
               index.usedAsAssociationType(at2));


        Assert.assertTrue("index loses or invents association types",
               index.getAssociationTypes().size() == 2);

        Assert.assertTrue("index forgets that topic is used as a type",
               index.usedAsType(at1));
        
        // STATE 3: topic map has duplicates
        AssociationIF a3 = builder.makeAssociation(at1);
        
        Assert.assertTrue("association type not found",
               index.getAssociations(at1).size() == 2);
        Assert.assertTrue("associations not found via type",
               index.getAssociations(at1).contains(a3));
        Assert.assertTrue("duplicate association types not suppressed",
               index.getAssociationTypes().size() == 2);
    }

    @Test
    public void testOccurrences() {
        // STATE 1: empty topic map
        Assert.assertTrue("index finds occurrence types in empty topic map",
               index.getOccurrenceTypes().size() == 0);
        

        // STATE 2: topic map has some topics in it
        TopicIF ot1 = builder.makeTopic();
        TopicIF t1 = builder.makeTopic();

        OccurrenceIF o1 = builder.makeOccurrence(t1, ot1, "");

        Assert.assertTrue("occurrence type not found",
               index.getOccurrences(ot1).size() == 1);

        Assert.assertTrue("occurrence not found via type",
               index.getOccurrences(ot1).iterator().next().equals(o1));

        Assert.assertTrue("index claims occurrence type not used",
               index.usedAsOccurrenceType(ot1));

        
        Assert.assertTrue("spurious occurrence types found",
               index.getOccurrences(t1).size() == 0);

        Assert.assertTrue("index claims ordinary topic used as occurrence type",
               !index.usedAsOccurrenceType(t1));

        
        Assert.assertTrue("index forgets that topic is used as a type",
               index.usedAsType(ot1));
        
        // STATE 3: topic map has duplicates
        OccurrenceIF o3 = builder.makeOccurrence(t1, ot1, "");
        
        Assert.assertTrue("occurrence type not found",
               index.getOccurrences(ot1).size() == 2);
        Assert.assertTrue("occurrence not found via type",
               index.getOccurrences(ot1).contains(o3));
        Assert.assertTrue("duplicate occurrence types not suppressed",
               index.getOccurrenceTypes().size() == 1); // Bug #93: 2 if null is included
    }

    @Test
    public void testTopics() {
        // STATE 1: empty topic map
        Assert.assertTrue("index finds spurious topic types",
               index.getTopics(null).size() == 0);

        Assert.assertTrue("null used as topic type in empty topic map",
               !index.usedAsTopicType(null));

        Assert.assertTrue("index finds topic types in empty topic map",
               index.getTopicTypes().size() == 0);
        

        // STATE 2: topic map has some topics in it
        TopicIF tt1 = builder.makeTopic();
        TopicIF tt2 = builder.makeTopic();
        TopicIF t1 = builder.makeTopic();
        builder.makeTopic();
        t1.addType(tt1);
        t1.addType(tt2);

        Assert.assertTrue("topic type not found",
               index.getTopics(tt1).size() == 1);

        Assert.assertTrue("topic not found via type",
               index.getTopics(tt1).iterator().next().equals(t1));

        Assert.assertTrue("index claims topic type not used",
               index.usedAsTopicType(tt1));

        
        Assert.assertTrue("topic type not found",
               index.getTopics(tt2).size() == 1);

        Assert.assertTrue("topic not found via type",
               index.getTopics(tt2).iterator().next().equals(t1));

        Assert.assertTrue("index claims topic type not used",
               index.usedAsTopicType(tt2));
        
        
        Assert.assertTrue("spurious topic types found",
               index.getTopics(t1).size() == 0);

        Assert.assertTrue("index claims ordinary topic used as topic type",
               !index.usedAsTopicType(t1));


        // -- The following two are somewhat bogus, since we don't
        // keep track of all created topics, some of which have no
        // types.
        Assert.assertTrue("topic with null type found",
               index.getTopics(null).size() == 3);

        Assert.assertFalse("index claims null used as topic type",
               index.usedAsTopicType(null));
        // --

        Assert.assertTrue("index loses or invents topic types",
               index.getTopicTypes().size() == 2);

        Assert.assertTrue("index forgets that topic is used as a type",
               index.usedAsType(tt1));
        
        Assert.assertTrue("index forgets that topic is used as a type",
               index.usedAsType(tt2));
        
        // STATE 3: topic map has duplicates
        TopicIF t3 = builder.makeTopic();
        t3.addType(tt1);
        
        Assert.assertTrue("topic type not found",
               index.getTopics(tt1).size() == 2);
        Assert.assertTrue("topic not found via type",
               index.getTopics(tt1).contains(t3));
        Assert.assertTrue("duplicate topic types not suppressed",
               index.getTopicTypes().size() == 2);
    }

    // --- Dynamic tests
    
    @Test
    public void testTopicsDynamic() {
        // create some topics for the topic map
        TopicIF tt1 = builder.makeTopic();
        TopicIF t1 = builder.makeTopic();
        t1.addType(tt1);
        Assert.assertTrue("TopicMapIF.addTopic does not update index",
               index.getTopics(tt1).size() == 1);

        t1.removeType(tt1);
        Assert.assertTrue("TopicIF.removeType does not update index",
               index.getTopics(tt1).size() == 0);

        t1.addType(tt1);
        Assert.assertTrue("TopicIF.addType does not update index",
               index.getTopics(tt1).size() == 1);
        
        t1.remove();
        Assert.assertTrue("TopicMapIF.removeTopic does not update index",
               index.getTopics(tt1).size() == 0);       
    }

    @Test
    public void testAssociationsDynamic() {
        TopicIF at1 = builder.makeTopic();
        TopicIF at2 = builder.makeTopic();
        AssociationIF a = builder.makeAssociation(at1);
        Assert.assertTrue("TopicMapIF.addAssociation does not update index",
               index.getAssociations(at1).size() == 1);

        a.setType(at2);
        Assert.assertTrue("AssociationIF.setType(at2) does not update index",
               index.getAssociations(at1).size() == 0);

        a.setType(at1);
        Assert.assertTrue("AssociationIF.addType does not update index",
               index.getAssociations(at1).size() == 1);
        
        a.remove();
        Assert.assertTrue("TopicMapIF.removeAssociation does not update index",
               index.getAssociations(at1).size() == 0); 
    }

    @Test
    public void testAssociationRolesDynamic() {
        TopicIF at1 = builder.makeTopic();
        TopicIF art1 = builder.makeTopic();
        TopicIF art2 = builder.makeTopic();
        TopicIF player = builder.makeTopic();
        AssociationIF a = builder.makeAssociation(at1);
        AssociationRoleIF r1 = builder.makeAssociationRole(a, art1, player);
        Assert.assertTrue("TopicMapIF.addAssociation does not update role type index",
               index.getAssociationRoles(art1).size() == 1);

        r1.setType(art2);
        Assert.assertTrue("AssociationRoleIF.setType(art2) does not update index",
               index.getAssociationRoles(art1).size() == 0);
        Assert.assertTrue("AssociationRoleIF.setType(art2) does not update index",
               index.getAssociationRoles(art2).size() == 1);

        r1.setType(art1);
        Assert.assertTrue("AssociationRoleIF.setType does not update index",
               index.getAssociationRoles(art1).size() == 1);
        Assert.assertTrue("AssociationRoleIF.setType does not update index",
               index.getAssociationRoles(art2).size() == 0);
        
        a.remove();
        Assert.assertTrue("TopicMapIF.removeAssociation does not update role type index",
               index.getAssociationRoles(art1).size() == 0);
    }   

    @Test
    public void testOccurrencesDynamic() {
        TopicIF ot1 = builder.makeTopic();
        TopicIF ot2 = builder.makeTopic();
        TopicIF t1 = builder.makeTopic();
        OccurrenceIF o1 = builder.makeOccurrence(t1, ot1, "");
        Assert.assertTrue("OccurrenceIF.setType does not update index",
               index.getOccurrences(ot1).size() == 1);

        o1.setType(ot2);
        Assert.assertTrue("OccurrenceIF.setType(ot2) does not update index",
               index.getOccurrences(ot1).size() == 0);

        builder.makeOccurrence(t1, ot1, "");
        Assert.assertTrue("TopicIF.addOccurrence does not update index",
               index.getOccurrences(ot1).size() == 1);
        
        t1.remove();
        Assert.assertTrue("TopicMapIF.removeTopic does not update occurrence index",
               index.getOccurrences(ot1).size() == 0);
    }

    @Test
    public void testConcurrentModification() {
        // create some topics for the topic map
        TopicIF ot1 = builder.makeTopic();
        TopicIF ot2 = builder.makeTopic();
        TopicIF t1 = builder.makeTopic();
        OccurrenceIF occ = builder.makeOccurrence(t1, ot1, "");

        try {
            Iterator it = index.getOccurrences(ot1).iterator();
            occ.setType(ot2);
            it.next();
        }
        catch (java.util.ConcurrentModificationException e) {
            Assert.fail("ClassInstanceIndex returns live collections");
        }
    }
    
}
