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

package net.ontopia.topicmaps.utils;

import java.util.HashSet;
import java.util.Set;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.TypedIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import org.junit.Assert;
import org.junit.Test;

public class Issue409Test {

  /**
   * Merges a topicmap with occurrences into an empty topicmap. The occurrences have a type with a
   * name, but no psi. During merging, a new empty topic is created as type for each of the 
   * occurrences in the source.
   */
  @Test
  public void testOccsWithoutPSI() {
   
    assertMergeTypedIF(new TypedIFCreator() {

      @Override
      public void createTypedIFs(TopicMapBuilderIF builder, TopicIF type) {
        builder.makeOccurrence(builder.makeTopic(), type, "foo1");
        builder.makeOccurrence(builder.makeTopic(), type, "foo2");
        builder.makeOccurrence(builder.makeTopic(), type, "foo3");
      }

      @Override
      public TypedIF getTyped(TopicIF topic) {
        if (topic.getOccurrences().size() == 0) {
          return null;
        }
        return topic.getOccurrences().iterator().next();
      }
    });
  }
  
  /**
   * Same as testOccsWithoutPSI, except this time the occurrence type has a psi. This test passes
   */
  @Test
  public void testOccsWithPSI() {

     assertMergeTypedIF(new TypedIFCreator() {

      @Override
      public void createTypedIFs(TopicMapBuilderIF builder, TopicIF type) {
        
        type.addSubjectIdentifier(URILocator.create("foo:bar"));
        
        builder.makeOccurrence(builder.makeTopic(), type, "foo1");
        builder.makeOccurrence(builder.makeTopic(), type, "foo2");
        builder.makeOccurrence(builder.makeTopic(), type, "foo3");
      }

      @Override
      public TypedIF getTyped(TopicIF topic) {
        if (topic.getOccurrences().size() == 0) {
          return null;
        }
        return topic.getOccurrences().iterator().next();
      }
    });   
  }

  /**
   * Same as testOccsWithoutPSI, but now with a name and a nametype.
   */
  @Test
  public void testNamesWithoutPSI() {

    assertMergeTypedIF(new TypedIFCreator() {

      @Override
      public void createTypedIFs(TopicMapBuilderIF builder, TopicIF type) {
        builder.makeTopicName(builder.makeTopic(), type, "foo1");
        builder.makeTopicName(builder.makeTopic(), type, "foo2");
        builder.makeTopicName(builder.makeTopic(), type, "foo3");
      }

      @Override
      public TypedIF getTyped(TopicIF topic) {
        if (topic.getTopicNames().size() == 0) {
          return null;
        }
        TopicNameIF name = topic.getTopicNames().iterator().next();
        if (name.getType().getSubjectIdentifiers().contains(PSI.getSAMNameType())) {
          return null;
        }
        return name;
      }
    });
  }

  /**
   * Same as testOccsWithPSI, but now with a name and a nametype.
   */
  @Test
  public void testNamesWithPSI() {

     assertMergeTypedIF(new TypedIFCreator() {

      @Override
      public void createTypedIFs(TopicMapBuilderIF builder, TopicIF type) {
        
        type.addSubjectIdentifier(URILocator.create("foo:bar"));
        
        builder.makeTopicName(builder.makeTopic(), type, "foo1");
        builder.makeTopicName(builder.makeTopic(), type, "foo2");
        builder.makeTopicName(builder.makeTopic(), type, "foo3");
      }

      @Override
      public TypedIF getTyped(TopicIF topic) {
        if (topic.getTopicNames().size() == 0) {
          return null;
        }
        TopicNameIF name = topic.getTopicNames().iterator().next();
        if (name.getType().getSubjectIdentifiers().contains(PSI.getSAMNameType())) {
          return null;
        }
        return name;
      }
    }); 
  }
  
  private void assertMergeTypedIF(TypedIFCreator creator) {
    
    TopicMapIF source = new InMemoryTopicMapStore().getTopicMap();
    TopicMapBuilderIF builder = source.getBuilder();
    
    TopicIF type = builder.makeTopic();
    builder.makeTopicName(type, "Type");
    
    creator.createTypedIFs(builder, type);
    
    TopicMapIF target = new InMemoryTopicMapStore().getTopicMap();
    MergeUtils.mergeInto(target, source);

    // collect the types after the merge. The bug causes duplication of types as no-name topics
    Set<TopicIF> types = new HashSet<TopicIF>();
    for (TopicIF topic : target.getTopics()) {
      TypedIF typed = creator.getTyped(topic);
      if (typed != null) {
         types.add(typed.getType());
      }
    }
    
    // if correct, there should be 1 type
    Assert.assertEquals("There were duplicate types created during merge", 1, types.size());
    
    // ... and that type should have a name
    for (TopicIF t : types) {
        Assert.assertEquals("OT did not have a name post-merge", 1, t.getTopicNames().size());
    }
  }
  
  interface TypedIFCreator {
    void createTypedIFs(TopicMapBuilderIF builder, TopicIF type);
    TypedIF getTyped(TopicIF topic);
  }
}
