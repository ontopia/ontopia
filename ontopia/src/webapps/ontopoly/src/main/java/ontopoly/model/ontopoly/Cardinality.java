
package ontopoly.model.ontopoly;

import java.util.List;

import ontopoly.model.CardinalityIF;
import ontopoly.model.OntopolyTopicMapIF;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;

/**
 * Represents a cardinality that can be assigned to a field.
 */
public class Cardinality extends Topic implements CardinalityIF {

  /**
   * Creates a new Cardinality object.
   */
  public Cardinality(TopicIF topic, OntopolyTopicMapIF tm) {
    super(topic, tm);
  }
  
  public boolean equals(Object obj) {
    if (!(obj instanceof Cardinality))
      return false;

    CardinalityIF cardinality = (CardinalityIF) obj;
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
  public static CardinalityIF getDefaultCardinality(FieldDefinitionIF fieldDefinition) {
    OntopolyTopicMapIF tm = fieldDefinition.getTopicMap();
    LocatorIF cardPSI = PSI.ON_CARDINALITY_0_M; 
    switch (fieldDefinition.getFieldType()) {
      case FieldDefinitionIF.FIELD_TYPE_IDENTITY: {
        IdentityFieldIF identityField = (IdentityFieldIF)fieldDefinition;
        if (identityField.isSubjectLocator())
          cardPSI = PSI.ON_CARDINALITY_1_1;
        else
          cardPSI = PSI.ON_CARDINALITY_0_M;
        break;
      }
      case FieldDefinitionIF.FIELD_TYPE_NAME: {
        cardPSI = PSI.ON_CARDINALITY_1_1;
        break;
      }
      case FieldDefinitionIF.FIELD_TYPE_OCCURRENCE: {
        cardPSI = PSI.ON_CARDINALITY_0_1;
        break;
      }
      case FieldDefinitionIF.FIELD_TYPE_ROLE: {
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
  public static List<CardinalityIF> getCardinalityTypes(OntopolyTopicMapIF tm) {
    String query = "instance-of($d, on:cardinality)?";

    QueryMapper<CardinalityIF> qm = tm.newQueryMapper(Cardinality.class);
    return qm.queryForList(query);
  }

}
