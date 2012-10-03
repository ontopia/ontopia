
package net.ontopia.topicmaps.utils;

import java.util.HashSet;
import java.util.Set;
import junit.framework.Assert;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import org.junit.Test;

public class Issue409Test {

  /**
   * Merges a topicmap with occurrences into an empty topicmap. The occurrences have a type with a
   * name, but no psi. During merging, a new empty topic is created as type for each of the 
   * occurrences in the source.
   */
  @Test
  public void testOccsWithoutPSI() {
   
    mergeTypedIF(new TypedIFCreator() {

      public void createTypedIFs(TopicMapBuilderIF builder, TopicIF type) {
        builder.makeOccurrence(builder.makeTopic(), type, "foo1");
        builder.makeOccurrence(builder.makeTopic(), type, "foo2");
        builder.makeOccurrence(builder.makeTopic(), type, "foo3");
      }

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

     mergeTypedIF(new TypedIFCreator() {

      public void createTypedIFs(TopicMapBuilderIF builder, TopicIF type) {
        
        type.addSubjectIdentifier(URILocator.create("foo:bar"));
        
        builder.makeOccurrence(builder.makeTopic(), type, "foo1");
        builder.makeOccurrence(builder.makeTopic(), type, "foo2");
        builder.makeOccurrence(builder.makeTopic(), type, "foo3");
      }

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

    mergeTypedIF(new TypedIFCreator() {

      public void createTypedIFs(TopicMapBuilderIF builder, TopicIF type) {
        builder.makeTopicName(builder.makeTopic(), type, "foo1");
        builder.makeTopicName(builder.makeTopic(), type, "foo2");
        builder.makeTopicName(builder.makeTopic(), type, "foo3");
      }

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

     mergeTypedIF(new TypedIFCreator() {

      public void createTypedIFs(TopicMapBuilderIF builder, TopicIF type) {
        
        type.addSubjectIdentifier(URILocator.create("foo:bar"));
        
        builder.makeTopicName(builder.makeTopic(), type, "foo1");
        builder.makeTopicName(builder.makeTopic(), type, "foo2");
        builder.makeTopicName(builder.makeTopic(), type, "foo3");
      }

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
  
  private void mergeTypedIF(TypedIFCreator creator) {
    
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
