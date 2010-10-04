
package ontopoly.model;

import java.util.Collection;
import java.util.Iterator;

import ontopoly.utils.OntopolyModelUtils;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.utils.CollectionUtils;

/**
 * Represents both subject locator and subject identifier fields.
 */
public interface IdentityFieldIF extends FieldDefinitionIF {

  /**
   * Gets the identity type.
   * 
   * @return the identity type.
   */
  public IdentityTypeIF getIdentityType();

  /**
   * True if this is the subject locator field type.
   */
  public boolean isSubjectLocator();

  /**
   * True if this is the subject identifier field type.
   */
  public boolean isSubjectIdentifier();

  /**
   * True if this is the item identifier field type.
   */
  public boolean isItemIdentifier();

  /**
   * Returns the assigned height of the identity text field.
   */
  public int getHeight();

  /**
   * Returns the assigned width of the identity text field.
   */
  public int getWidth();

}
