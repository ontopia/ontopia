
// $Id: TopicNameTest.java,v 1.1 2008/06/12 14:37:13 geir.gronmo Exp $

package net.ontopia.topicmaps.core.test;

import net.ontopia.topicmaps.core.*;

public class TopicNameTest extends AbstractTypedScopedTest {
  protected TopicNameIF basename;
  
  public TopicNameTest(String name) {
    super(name);
  }
    
  // --- Test cases

  public void testReification() {
    TopicIF reifier = builder.makeTopic();
    ReifiableIF reifiable = basename;
    
    assertTrue("Object reified by the reifying topic was found",
               reifier.getReified() == null);
    assertTrue("Topic reifying the reifiable was found",
               reifiable.getReifier() == null);

    reifiable.setReifier(reifier);
    assertTrue("No topic reifying the reifiable was found",
               reifiable.getReifier() == reifier);
    assertTrue("No object reified by the reifying topic was found",
               reifier.getReified() == reifiable);
    
    reifiable.setReifier(null);
    assertTrue("Object reified by the reifying topic was found",
               reifier.getReified() == null);
    assertTrue("Topic reifying the first reifiable was found",
               reifiable.getReifier() == null);
  }

  public void testValue() {
    assertTrue("initial name value not \"\"", "".equals(basename.getValue()));

    basename.setValue("testfaen");
    assertTrue("name not set correctly",
               basename.getValue().equals("testfaen"));

    try {
      basename.setValue(null);
      fail("value could be set to null");
    } catch (NullPointerException e) {
    }
		
    basename.setValue("foo");
    assertTrue("name value not set to foo",
               "foo".equals(basename.getValue()));
  }

  public void testTopic() {
    if (parent instanceof TopicIF) {
      // we are now testing a basename
      assertTrue("parent is not right object",
                 parent.equals(basename.getTopic()));
    } else {
      VariantNameIF variant = (VariantNameIF) basename;
      assertTrue("parent and grandparent do not agree",
                 variant.getTopicName().getTopic().equals(basename.getTopic()));
    }
  }
    
  public void testVariants() {
    // STATE 1: no child variants
    assertTrue("initial variant child set not empty",
               basename.getVariants().size() == 0);

    // STATE 2: one child variant
    VariantNameIF variant = builder.makeVariantName(basename, "");
    // builder adds the child for us

    assertTrue("variant child of " + basename + " not added",
               basename.getVariants().size() == 1);

    assertTrue("variant child identity lost",
               basename.getVariants().iterator().next().equals(variant));

    // STATE 3: no children again
    variant.remove();
    assertTrue("variant child not removed",
               basename.getVariants().size() == 0);

    // removing non-existent variant to check that it does not complain
    variant.remove();
  }
    
  // --- Internal methods

  public void setUp() {
    super.setUp();
    TopicIF topic = builder.makeTopic();
    parent = topic;
    basename = builder.makeTopicName(topic, "");
    scoped = basename;
    typed = basename;
    object = basename;
  }

  protected TMObjectIF makeObject() {
    TopicIF topic = builder.makeTopic();
    return builder.makeTopicName(topic, "");
  }
}
