
// $Id: AbstractScopedTest.java,v 1.10 2008/01/14 12:52:28 geir.gronmo Exp $

package net.ontopia.topicmaps.core;

public abstract class AbstractScopedTest extends AbstractTMObjectTest {
  protected ScopedIF scoped;
  
  public AbstractScopedTest(String name) {
    super(name);
  }

  // --- Test cases

  public void testScope() {
    // STATE 1: empty scope
    assertTrue("scope initially not empty", scoped.getScope().size() == 0);
    
    TopicIF theme = builder.makeTopic();
    scoped.removeTheme(theme); // just checking that this works, is all
        
    // STATE 2: one topic in scope
    TopicIF topic = builder.makeTopic();
    scoped.addTheme(topic);
    assertTrue("theme not added", scoped.getScope().size() == 1);

    scoped.addTheme(topic);
    assertTrue("duplicate not rejected", scoped.getScope().size() == 1);

    // STATE 3: empty scope again
    scoped.removeTheme(topic);
    assertTrue("theme not removed", scoped.getScope().size() == 0);

    scoped.removeTheme(topic); // removing theme that is not present
  }

  /**
   * Verify that methods handle null arguments the way they should.
   */
  public void testNullScopedArguments() {
    try {
      scoped.addTheme(null);
      fail("was allowed to add null theme");
    } catch (NullPointerException e) {
      assertEquals("Wrong error message in NPE", MSG_NULL_ARGUMENT, e.getMessage());
    }

    try {
      scoped.removeTheme(null);
      fail("was allowed to remove null theme");
    } catch (NullPointerException e) {
      assertEquals("Wrong error message in NPE", MSG_NULL_ARGUMENT, e.getMessage());
    }
  }

}
