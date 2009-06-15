
// $Id: CopyUtils.java,v 1.11 2008/06/12 14:37:23 geir.gronmo Exp $

package net.ontopia.topicmaps.utils;

import java.io.Reader;
import java.util.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.utils.*;

/**
 * INTERNAL: Utilities for copying topic map data.
 *
 * @since 3.4
 */
public class CopyUtils {

  /**
   * INTERNAL: Creates a new topic and copies all the characteristics
   * from the source topic to this new topic.
   *
   * @return the new topic with a copy of all the characteristics.
   */
  public static TopicIF copyCharacteristics(TopicIF source) {
    TopicMapBuilderIF builder = source.getTopicMap().getBuilder();
    TopicIF n = builder.makeTopic();
    copyCharacteristics(n, source);
    return n;
  }
  
  /**
   * INTERNAL: Copies all the characteristics from the source topic to
   * the target topic.
   *
   * @return the new topic with a copy of all the characteristics.
   */
  public static void copyCharacteristics(TopicIF target, TopicIF source) {
    copyTypes(target, source);
    copyTopicNames(target, source);
    copyOccurrences(target, source);
    copyAssociations(target, source);
  }
  
  // --- occurrences

  private static void copyOccurrences(TopicIF target, TopicIF source) {
    Iterator it = source.getOccurrences().iterator();
    while (it.hasNext()) {
      OccurrenceIF o = (OccurrenceIF)it.next();
      copyOccurrence(target, o);
    }
  }

  /**
   * INTERNAL: Copies the occurrence from the source topic to the
   * target topic.
   *
   * @since 4.0
   */
  public static OccurrenceIF copyOccurrence(TopicIF target, OccurrenceIF source) {
    TopicMapBuilderIF builder = target.getTopicMap().getBuilder();
    OccurrenceIF o = builder.makeOccurrence(target, source.getType(), ""); // HACK: needs improvement
		CopyUtils.copyOccurrenceData(o, source);
    copyScope(o, source);
    return o;
  }

  /**
   * INTERNAL: Copies the occurrence value and datatype from the
   * source topic to the target topic.
   *
   * @since 4.0
   */
  public static void copyOccurrenceData(OccurrenceIF target, OccurrenceIF source) {
    if (source.getLength() > DataTypes.SIZE_THRESHOLD) {
			Reader r = source.getReader();
			try {
				target.setReader(r, source.getLength(), source.getDataType());
			} catch (Exception e) {
				try {
					r.close();
				} catch (Exception e2) {
				}
				throw new OntopiaRuntimeException(e);
			}
    } else {
      target.setValue(source.getValue(), source.getDataType());
		}
  }

  // --- base names

  private static void copyTopicNames(TopicIF target, TopicIF source) {
    Iterator it = source.getTopicNames().iterator();
    while (it.hasNext()) {
      TopicNameIF o = (TopicNameIF)it.next();
      copyTopicName(target, o);
    }
  }
  
  /**
   * INTERNAL: Copies the name from the source topic to the
   * target topic.
   *
   * @since 4.0
   */
  public static TopicNameIF copyTopicName(TopicIF target, TopicNameIF source) {
    TopicMapBuilderIF builder = target.getTopicMap().getBuilder();
    TopicNameIF n = builder.makeTopicName(target, source.getType(), source.getValue());
    copyScope(n, source);
    copyVariants(n, source);
    return n;
  }

  // --- variants

  private static void copyVariants(TopicNameIF target, TopicNameIF source) {
    Iterator it = source.getVariants().iterator();
    while (it.hasNext()) {
      VariantNameIF o = (VariantNameIF)it.next();
      copyVariant(target, o);
    }
  }
  
  /**
   * INTERNAL: Copies the variant from the source topic to the
   * target topic.
   *
   * @since 4.0
   */
  public static VariantNameIF copyVariant(TopicNameIF target, VariantNameIF source) {
    TopicMapBuilderIF builder = target.getTopicMap().getBuilder();
    VariantNameIF n = builder.makeVariantName(target, ""); // HACK: needs improvement
    copyScope(n, source);
    copyVariantData(n, source);
    return n;
  }

  /**
   * INTERNAL: Copies the variant value and datatype from the
   * source topic to the target topic.
   *
   * @since 4.0
   */
  public static void copyVariantData(VariantNameIF target, VariantNameIF source) {
    if (source.getLength() > DataTypes.SIZE_THRESHOLD)
      target.setReader(source.getReader(), source.getLength(), source.getDataType());
    else
      target.setValue(source.getValue(), source.getDataType());
  }

  // --- associations

  private static void copyAssociations(TopicIF target, TopicIF source) {
    Set uniqueAssocs = new HashSet();

    Iterator it = source.getRoles().iterator();
    while (it.hasNext()) {
      AssociationRoleIF o = (AssociationRoleIF)it.next();
      uniqueAssocs.add(o.getAssociation());
    }
    it = uniqueAssocs.iterator();
    while (it.hasNext()) {
      AssociationIF o = (AssociationIF)it.next();
      copyAssociation(target, o, source);
    }
  }
  
  /**
   * INTERNAL: Copies the variant from the source topic to the
   * target topic.
   *
   * @since 4.0
   */
  public static AssociationIF copyAssociation(TopicIF targetPlayer, AssociationIF source, TopicIF sourcePlayer) {
    TopicMapBuilderIF builder = targetPlayer.getTopicMap().getBuilder();
    AssociationIF n = builder.makeAssociation(source.getType());
    copyScope(n, source);

    Iterator it = source.getRoles().iterator();
    while (it.hasNext()) {
      AssociationRoleIF o = (AssociationRoleIF)it.next();
      TopicIF player = o.getPlayer();
      if (player != null && player.equals(sourcePlayer))
        player = targetPlayer;
      AssociationRoleIF nr = builder.makeAssociationRole(n, o.getType(), player);
    }
    return n;
  }
  
  // --- scope

  private static void copyScope(ScopedIF target, ScopedIF source) {
    Iterator it = source.getScope().iterator();
    while (it.hasNext())
      target.addTheme((TopicIF) it.next());
  }

  // --- types

  private static void copyType(TypedIF target, TypedIF source) {
    target.setType(source.getType());
  }

  private static void copyTypes(TopicIF target, TopicIF source) {
    Iterator it = source.getTypes().iterator();
    while (it.hasNext())
      target.addType((TopicIF) it.next());
  }

}
