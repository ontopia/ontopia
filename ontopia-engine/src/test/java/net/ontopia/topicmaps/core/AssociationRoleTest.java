
package net.ontopia.topicmaps.core;

public abstract class AssociationRoleTest extends AbstractTypedTest {
  protected AssociationRoleIF role;
  
  public AssociationRoleTest(String name) {
    super(name);
  }
    
  // --- Test cases

  public void testReification() {
    TopicIF reifier = builder.makeTopic();
    ReifiableIF reifiable = role;
    
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

  public void testPlayer() {
    assertTrue("player null initially", role.getPlayer() != null);

    TopicIF player = builder.makeTopic();
    role.setPlayer(player);
    assertTrue("player not set properly", role.getPlayer().equals(player));

    try {
      role.setPlayer(null);
      fail("player could be set to null");
    } catch (NullPointerException e) {
    }
    assertTrue("player not retained", role.getPlayer().equals(player));
  }

  public void testParentAssociation() {
    assertTrue("parent not set to right object",
               role.getAssociation().equals(parent));
  }

  // --- Internal methods

  public void setUp() throws Exception {
    super.setUp();
    AssociationIF assoc = builder.makeAssociation(builder.makeTopic());
    parent = assoc;
    role = builder.makeAssociationRole(assoc, builder.makeTopic(), builder.makeTopic());
    object = role;
    typed = role;
  }

  protected TMObjectIF makeObject() {
    AssociationIF assoc = builder.makeAssociation(builder.makeTopic());
    return builder.makeAssociationRole(assoc, builder.makeTopic(), builder.makeTopic());
  }
    
}
