// $Id: Cardinality.java,v 1.1 2008/10/23 05:18:36 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.webapps.ontopoly.model;

import java.util.List;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;

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
  public static Cardinality getDefaultCardinality(FieldDefinition fieldDefinition) {
    TopicMap tm = fieldDefinition.getTopicMap();
    LocatorIF cardPSI = PSI.ON_CARDINALITY_0_M; 
    switch (fieldDefinition.getFieldType()) {
      case FieldDefinition.FIELD_TYPE_IDENTITY: {
        IdentityField identityField = (IdentityField)fieldDefinition;
        if (identityField.isSubjectLocator())
          cardPSI = PSI.ON_CARDINALITY_1_1;
        else
          cardPSI = PSI.ON_CARDINALITY_0_M;
        break;
      }
      case FieldDefinition.FIELD_TYPE_NAME: {
        cardPSI = PSI.ON_CARDINALITY_1_1;
        break;
      }
      case FieldDefinition.FIELD_TYPE_OCCURRENCE: {
        cardPSI = PSI.ON_CARDINALITY_0_1;
        break;
      }
      case FieldDefinition.FIELD_TYPE_ROLE: {
        cardPSI = PSI.ON_CARDINALITY_0_M;
        break;
      }
    }
    return new Cardinality(tm.getTopicMapIF().getTopicBySubjectIdentifier(cardPSI), tm);
  }

  /**
   * Returns all available cardinalities.
   * 
   * @return A list containing Cardinality objects of all available
   *         cardinalities.
   */
  public static List<Cardinality> getCardinalityTypes(TopicMap tm) {
    String query = "instance-of($d, on:cardinality)?";

    QueryMapper<Cardinality> qm = tm.newQueryMapper(Cardinality.class);
    return qm.queryForList(query);
  }

}
