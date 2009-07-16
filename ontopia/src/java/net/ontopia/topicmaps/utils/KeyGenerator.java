
// $Id: KeyGenerator.java,v 1.22 2008/06/13 12:31:32 geir.gronmo Exp $

package net.ontopia.topicmaps.utils;

import java.util.*;
import net.ontopia.utils.StringUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.*;

/**
 * PUBLIC: Utilities for generating keys from complex topic map
 * objects. The keys from two different objects in the same topic map
 * will be equal if the two objects are equal according to the
 * equality rules of the TMDM.
 */
public class KeyGenerator {

  /**
   * PUBLIC: Makes a key for an occurrence. The key is made up of 
   * a scope key, a type key, and a data key.
   *
   * @return string containing key
   */ 
  public static String makeOccurrenceKey(OccurrenceIF occ) {
    return makeScopeKey(occ) + "$" + makeTypedKey(occ) + makeDataKey(occ);
  }
    

  /**
   * PUBLIC: Makes a key for a topic name. The key is made up of 
   * a scope key and a value key.
   *
   * @return string containing key
   */ 
  public static String makeTopicNameKey(TopicNameIF bn) {
    return makeScopeKey(bn) + "$" + makeTypedKey(bn) + "$$" + bn.getValue();
  }
  
  /**
   * PUBLIC: Makes a key for a variant name. The key is made up of 
   * a scope key and a value/locator key.
   *
   * @since 1.2.0
   * @return string containing key
   */ 
  public static String makeVariantKey(VariantNameIF vn) {
		return makeScopeKey(vn) + makeDataKey(vn);
  }
    
  /**
   * PUBLIC: Makes a key for an association. The key is made up from
   * the type and for each role its type and player.
   * @param assoc The association to make a key for
   * @return string containing key
   */
  public static String makeAssociationKey(AssociationIF assoc) {
    StringBuffer sb = new StringBuffer();

    // asssociation type key fragment
    sb.append(makeTypedKey(assoc));
    sb.append("$");
    sb.append(makeScopeKey(assoc));
    sb.append("$");
    
    List roles = new ArrayList(assoc.getRoles());
    String[] rolekeys = new String[roles.size()];
    for (int i = 0; i < rolekeys.length; i++) 
      rolekeys[i] = makeAssociationRoleKey((AssociationRoleIF) roles.get(i));

    Arrays.sort(rolekeys);
    sb.append(StringUtils.join(rolekeys, "$"));
    return sb.toString();
  }
    
  /**
   * PUBLIC: Makes a key for an association, but does not include
   * the player of the given role. The key is made up from the type
   * and for each role its type and player (except the given role for
   * which only the role type is included). This method is used to
   * create a signature for the association without taking one of the
   * roles into account.
   * @param assoc The association to make a key for
   * @param role The association role whose role player is to be left
   * out of the key.
   * @return string containing key
   * @since 3.4.2
   */
  public static String makeAssociationKey(AssociationIF assoc,
                                          AssociationRoleIF role) {
    StringBuffer sb = new StringBuffer();

    // asssociation type key fragment
    sb.append(makeTypedKey(assoc));
    sb.append("$");
    sb.append(makeScopeKey(assoc));
    sb.append("$");
    
    List roles = new ArrayList(assoc.getRoles());
    roles.remove(role);
    String[] rolekeys = new String[roles.size()];
    for (int i = 0; i < rolekeys.length; i++) 
      rolekeys[i] = makeAssociationRoleKey((AssociationRoleIF) roles.get(i));
    
    sb.append(makeTypedKey(role));
    sb.append("$");
    Arrays.sort(rolekeys);
    sb.append(StringUtils.join(rolekeys, "$"));
    return sb.toString();
  }
  
  /**
   * PUBLIC: Makes a key for an association role. The key is made up
   * of the role and the player.
   * @since 1.2.0
   * @param role The association role.
   * @return The key.
   */
  public static String makeAssociationRoleKey(AssociationRoleIF role) {
    return makeTypedKey(role) + ":" +
      (role.getPlayer() != null ? role.getPlayer().getObjectId() : "");
  }

  /**
   * PUBLIC: Makes a key for any reifiable object, using the other
   * methods in this class.
   * @since %NEXT%
   */
  public static String makeKey(ReifiableIF object) {
    if (object instanceof TopicNameIF)
      return makeTopicNameKey((TopicNameIF) object);
    else if (object instanceof OccurrenceIF)
      return makeOccurrenceKey((OccurrenceIF) object);
    else if (object instanceof AssociationIF)
      return makeAssociationKey((AssociationIF) object);
    else if (object instanceof AssociationRoleIF)
      return makeAssociationRoleKey((AssociationRoleIF) object);
    else
      throw new OntopiaRuntimeException("Cannot make key for: " + object);
  }

  // --- Helper methods

  protected static String makeTypedKey(TypedIF typed) {
    if (typed.getType() == null)
      return "";
    else
      return typed.getType().getObjectId();
  }

  protected static String makeTopicKey(TopicIF topic) {
    if (topic == null)
      return "";
    else
      return topic.getObjectId();
  }  
    
  protected static String makeScopeKey(ScopedIF scoped) {
    return makeScopeKey(scoped.getScope());
  }

  protected static String makeScopeKey(Collection scope) {
    Iterator it = scope.iterator();
    String[] ids = new String[scope.size()];
    int ix = 0;
    while (it.hasNext()) {
      TopicIF theme = (TopicIF) it.next();
      ids[ix++] = theme.getObjectId();
    }

    Arrays.sort(ids);
    return StringUtils.join(ids, " ");
  }

  protected static String makeDataKey(OccurrenceIF occ) {
    return "$$" + occ.getValue() + "$" + occ.getDataType();
  }

  protected static String makeDataKey(VariantNameIF variant) {
		return "$$" + variant.getValue() + "$" + variant.getDataType();
  }
  
}
