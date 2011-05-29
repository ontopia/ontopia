
package net.ontopia.topicmaps.core;

public abstract class AbstractTypedTest extends AbstractTMObjectTest {
  protected TypedIF typed;
  
  public AbstractTypedTest(String name) {
    super(name);
  }

    // --- Test cases

    public void testType() {
        assertTrue("type null initially", typed.getType() != null);

        TopicIF type = builder.makeTopic();
        typed.setType(type);
        assertTrue("type identity not retained", typed.getType().equals(type));

				try {
					typed.setType(null);
					fail("type could be set to null");
				} catch (NullPointerException e) {
				}
    }

}





