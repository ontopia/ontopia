// $Id: Cardinality.java,v 1.1 2008/10/23 05:18:36 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.webapps.ontopoly.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.utils.OntopolyModelUtils;

/**
 * Represents a cardinality that can be assigned to a field.
 */
public class Cardinality extends Topic {

  /**
   * Creates a new Cardinality object.
   */
  public Cardinality(TopicIF topic, TopicMap tm) {
    super(topic, tm);
  }
  
  public boolean equals(Object obj) {
    if (!(obj instanceof Cardinality))
      return false;

    Cardinality cardinality = (Cardinality) obj;
    return (getTopicIF().equals(cardinality.getTopicIF()));
  }

  /**
   * True if cardinality is 0..1 or 1..1.
   */
  public boolean isMaxOne() {
    return (getTopicIF().getSubjectIdentifiers().contains(PSI.ON_CARDINALITY_0_1) || 
            getTopicIF().getSubjectIdentifiers().contains(PSI.ON_CARDINALITY_1_1));
  }

  /**
   * True if cardinality is 0..* or 1..*.
   */
  public boolean isMaxInfinite() {
    return (getTopicIF().getSubjectIdentifiers().contains(PSI.ON_CARDINALITY_0_M) || 
            getTopicIF().getSubjectIdentifiers().contains(PSI.ON_CARDINALITY_1_M));
  }

  /**
   * True if cardinality is 0..* or 0..1.
   */
  public boolean isMinZero() {
    return (getTopicIF().getSubjectIdentifiers().contains(PSI.ON_CARDINALITY_0_M) || 
            getTopicIF().getSubjectIdentifiers().contains(PSI.ON_CARDINALITY_0_1));
  }

  /**
   * True if cardinality is 1..* or 1..1.
   */
  public boolean isMinOne() {
    return (getTopicIF().getSubjectIdentifiers().contains(PSI.ON_CARDINALITY_1_M) || 
            getTopicIF().getSubjectIdentifiers().contains(PSI.ON_CARDINALITY_1_1));
  }

  /**
   * Returns the default cardinality (zero or more)
   */
  public static Cardinality getDefaultCardinality(TopicMap tm) {
    return new Cardinality(tm.getTopicMapIF().getTopicBySubjectIdentifier(PSI.ON_CARDINALITY_0_M), tm);
  }

  /**
   * Returns all available cardinalities.
   * 
   * @return A list containing Cardinality objects of all available
   *         cardinalities.
   */
  public static List getCardinalityTypes(TopicMap tm) {
    String query = "instance-of($d, on:cardinality)?";

    Collection result = tm.getQueryWrapper().queryForList(query,
        OntopolyModelUtils.getRowMapperOneColumn());

    if (result.isEmpty())
      return Collections.EMPTY_LIST;

    List cardinalityTypes = new ArrayList();
    Iterator it = result.iterator();
    while (it.hasNext()) {
      cardinalityTypes.add(new Cardinality((TopicIF) it.next(), tm));
    }
    return cardinalityTypes;
  }

}