
package net.ontopia.infoset.fulltext.topicmaps;

import net.ontopia.infoset.fulltext.core.DocumentIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.VariantNameIF;
  
/**
 * INTERNAL: A document generator interface that can be implemented to
 * generate DocumentIFs for topic map objects.<p>
 */

public interface TopicMapDocumentGeneratorIF {

  /**
   * INTERNAL: Generate a document for the given association.
   */
  public DocumentIF generate(AssociationIF assoc);
  
  /**
   * INTERNAL: Generate a document for the given association role.
   */
  public DocumentIF generate(AssociationRoleIF assocrl);

  /**
   * INTERNAL: Generate a document for the given basename.
   */
  public DocumentIF generate(TopicNameIF basename);

  /**
   * INTERNAL: Generate a document for the given occurrence.
   */
  public DocumentIF generate(OccurrenceIF occurs);

  /**
   * INTERNAL: Generate a document for the given topic.
   */
  public DocumentIF generate(TopicIF topic);

  /**
   * INTERNAL: Generate a document for the given topic map.
   */
  public DocumentIF generate(TopicMapIF topicmap);

  /**
   * INTERNAL: Generate a document for the given variant name.
   */
  public DocumentIF generate(VariantNameIF variant);
  
}
