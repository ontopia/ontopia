
// $Id: AssociationBuilder.java,v 1.16 2008/05/29 10:54:59 geir.gronmo Exp $

package net.ontopia.topicmaps.utils;

import java.util.Collection;
import java.util.Iterator;
import net.ontopia.topicmaps.core.*;

/**
 * PUBLIC: A helper class that makes it easier to build associations.
 * @since 1.2
 */
public class AssociationBuilder {
  protected TopicMapIF topicmap;
  protected TopicMapBuilderIF builder;
  protected TopicIF assoctype;
  protected TopicIF role1type;
  protected TopicIF role2type;
  protected TopicIF role3type;
  protected TopicIF role4type;
  protected Collection scope;

  /**
   * PUBLIC: Creates a new AssociationBuilder for unary associations.
   * @param assoctype The type of the created associations
   * @param roletype The role type.
   * @since 4.0
   */
  public AssociationBuilder(TopicIF assoctype, TopicIF roletype) {
    this.assoctype = assoctype;
    this.role1type = roletype;
    this.topicmap = assoctype.getTopicMap();
    this.builder = topicmap.getBuilder();
  }

  /**
   * PUBLIC: Creates a new AssociationBuilder for binary associations.
   * @param assoctype The type of the created associations
   * @param role1type The first role type.
   * @param role2type The second role type.
   */
  public AssociationBuilder(TopicIF assoctype, 
                            TopicIF role1type,
                            TopicIF role2type) {
    this.assoctype = assoctype;
    this.role1type = role1type;
    this.role2type = role2type;
    this.topicmap = assoctype.getTopicMap();
    this.builder = topicmap.getBuilder();
  }

  /**
   * PUBLIC: Creates a new AssociationBuilder for ternary associations.
   * @param assoctype The type of the created associations
   * @param role1type The first role type.
   * @param role2type The second role type.
   * @param role3type The third role type.
   * @since 1.3
   */
  public AssociationBuilder(TopicIF assoctype, 
                            TopicIF role1type,
                            TopicIF role2type,
                            TopicIF role3type) {
    this.assoctype = assoctype;
    this.role1type = role1type;
    this.role2type = role2type;
    this.role3type = role3type;
    this.topicmap = assoctype.getTopicMap();
    this.builder = topicmap.getBuilder();
  }

  /**
   * PUBLIC: Creates a new AssociationBuilder for quad associations.
   * @param assoctype The type of the created associations
   * @param role1type The first role type.
   * @param role2type The second role type.
   * @param role3type The third role type.
   * @param role4type The fourth role type.
   * @since 1.3
   */
  public AssociationBuilder(TopicIF assoctype, 
                            TopicIF role1type,
                            TopicIF role2type,
                            TopicIF role3type,
                            TopicIF role4type) {
    this.assoctype = assoctype;
    this.role1type = role1type;
    this.role2type = role2type;
    this.role3type = role3type;
    this.role4type = role4type;
    this.topicmap = assoctype.getTopicMap();
    this.builder = topicmap.getBuilder();
  }
  
  /**
   * PUBLIC: Returns the scope added to all associations created by
   * this builder.
   */   
  public Collection getScope() {
    return scope;
  }

  /**
   * PUBLIC: Sets the scope added to all associations created by
   * this builder.
   */
  public void setScope(Collection scope) {
    this.scope = scope;
  }

  /**
   * PUBLIC: Create a unary association of the configured type, where
   * player is the role player.
   * @since 4.0
   */   
  public AssociationIF makeAssociation(TopicIF player) {
    AssociationIF assoc = builder.makeAssociation(assoctype);
    AssociationRoleIF role =
      builder.makeAssociationRole(assoc, role1type, player);

    if (scope != null) {
      Iterator it = scope.iterator();
      while (it.hasNext())
        assoc.addTheme((TopicIF) it.next());
    }
    
    return assoc;
  }
  
  /**
   * PUBLIC: Create a binary association of the configured type, where
   * player1 plays the first role and player2 the second.
   */   
  public AssociationIF makeAssociation(TopicIF player1, TopicIF player2) {
    AssociationIF assoc = builder.makeAssociation(assoctype);

    AssociationRoleIF role = builder.makeAssociationRole(assoc, role1type, player1);

    role = builder.makeAssociationRole(assoc, role2type, player2);

    if (scope != null) {
      Iterator it = scope.iterator();
      while (it.hasNext())
        assoc.addTheme((TopicIF) it.next());
    }
    
    return assoc;
  }

  /**
   * PUBLIC: Create a ternary association of the configured type, where
   * player1 plays the first role, player2 the second, and player3 the
   * third.
   * @throws IllegalArgumentException if the builder is only configured
   * for binary associations.
   * @since 1.3
   */   
  public AssociationIF makeAssociation(TopicIF player1,
                                       TopicIF player2,
                                       TopicIF player3) {
    if (role3type == null)
      throw new IllegalArgumentException("Builder only configured for binary associations!");
    
    AssociationIF assoc = builder.makeAssociation(assoctype);

    AssociationRoleIF role = builder.makeAssociationRole(assoc, role1type, player1);

    role = builder.makeAssociationRole(assoc, role2type, player2);

    role = builder.makeAssociationRole(assoc, role3type, player3);
    
    if (scope != null) {
      Iterator it = scope.iterator();
      while (it.hasNext())
        assoc.addTheme((TopicIF) it.next());
    }
    
    return assoc;
  }

  /**
   * PUBLIC: Create a quad association of the configured type, where
   * player1 plays the first role, and player2 the second, and player3
   * the third, and player4 the fourth.
   * @throws IllegalArgumentException if the builder is only configured
   * for binary or ternary associations.
   * @since 4.0
   */   
  public AssociationIF makeAssociation(TopicIF player1,
                                       TopicIF player2,
                                       TopicIF player3,
                                       TopicIF player4) {
    if (role3type == null)
      throw new IllegalArgumentException("Builder only configured for binary associations!");
    if (role4type == null)
      throw new IllegalArgumentException("Builder only configured for ternary associations!");
    
    AssociationIF assoc = builder.makeAssociation(assoctype);

    AssociationRoleIF role = builder.makeAssociationRole(assoc, role1type, player1);

    role = builder.makeAssociationRole(assoc, role2type, player2);

    role = builder.makeAssociationRole(assoc, role3type, player3);

    role = builder.makeAssociationRole(assoc, role4type, player4);
    
    if (scope != null) {
      Iterator it = scope.iterator();
      while (it.hasNext())
        assoc.addTheme((TopicIF) it.next());
    }
    
    return assoc;
  }

  /**
   * PUBLIC: Returns the type of associations the builder creates.
   * @since 2.0
   */
  public TopicIF getAssociationType() {
    return assoctype;
  }
  
}
