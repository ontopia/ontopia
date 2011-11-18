package net.ontopia.topicmaps.utils;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

public class TopicNameComparatorTest {
  
  private TopicMapIF topicmap;
  private TopicIF topic;
  
  private TopicNameIF untyped;
  private TopicNameIF untyped_scoped;
  private TopicNameIF typed;
  private TopicNameIF typed_scoped;
  
  private TopicIF scope;
  
  @Before
  public void setUp() {
    
    topicmap = new InMemoryTopicMapStore().getTopicMap();
    TopicMapBuilderIF builder = topicmap.getBuilder();
    
    topic = builder.makeTopic();
    TopicIF type = builder.makeTopic();
    scope = builder.makeTopic();
    
    untyped = builder.makeTopicName(topic, "Untyped name");
    typed = builder.makeTopicName(topic, type, "Typed");
    
    untyped_scoped = builder.makeTopicName(topic, "Untyped, scoped");
    typed_scoped = builder.makeTopicName(topic, type, "Typed, scoped");
    
    untyped_scoped.addTheme(scope);
    typed_scoped.addTheme(scope);
  }
  
  @Test
  public void testIssue307NoScope() {
    
    TopicNameComparator noScopeComp = new TopicNameComparator(new ArrayList());
    
    List<TopicNameIF> names = new ArrayList<TopicNameIF>();
    // add in wrong order
    names.add(typed_scoped);
    names.add(typed);
    names.add(untyped_scoped);
    names.add(untyped);
    
    Collections.sort(names, noScopeComp);
    
    TopicNameIF[] expected = new TopicNameIF[] { untyped, untyped_scoped, typed, typed_scoped };
    Assert.assertArrayEquals("Incorrect unscoped name ordering", expected, names.toArray());
  }
  
  @Test
  public void testIssue307Scoped() {
    
    TopicNameComparator noScopeComp = new TopicNameComparator(Collections.singletonList(scope));
    
    List<TopicNameIF> names = new ArrayList<TopicNameIF>();
    // add in wrong order
    names.add(typed_scoped);
    names.add(typed);
    names.add(untyped_scoped);
    names.add(untyped);
    
    Collections.sort(names, noScopeComp);
    
    TopicNameIF[] expected = new TopicNameIF[] { untyped_scoped, untyped, typed_scoped, typed };
    Assert.assertArrayEquals("Incorrect scoped name ordering", expected, names.toArray());
  }
}
