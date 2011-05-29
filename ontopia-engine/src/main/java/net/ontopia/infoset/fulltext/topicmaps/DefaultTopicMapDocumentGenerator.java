
package net.ontopia.infoset.fulltext.topicmaps;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.fulltext.core.DocumentIF;
import net.ontopia.infoset.fulltext.core.GenericField;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.utils.ObjectUtils;

/**
 * INTERNAL: The default topic map document generator that generates
 * DocumentIFs for topic map objects.<p>
 *
 * All documents get an "object_id" field, containing the object id of
 * the topic map object, and a "class" field indicating the class of
 * object. The default class values are:<p>
 *
 * AssociationIF: 'A', AssociationRoleIF: 'R', TopicNameIF: 'B',
 * OccurrenceIF: 'O', TopicIF: 'T', TopicMapIF: 'M' and VariantNameIF:
 * 'N'.<p>
 */

public class DefaultTopicMapDocumentGenerator implements TopicMapDocumentGeneratorIF {

  public static final DefaultTopicMapDocumentGenerator INSTANCE = new DefaultTopicMapDocumentGenerator();

  protected static final String _object_id = "object_id";
  protected static final String _class = "class";
  
  protected void addObjectFields(DocumentIF doc, TMObjectIF tmobject, String klass) {
    // Add fields
    doc.addField(GenericField.createKeywordField(_object_id, tmobject.getObjectId()));
    doc.addField(GenericField.createTextField(_class, klass));
  }

  protected void addContentField(DocumentIF doc, String value) {
    if (value != null)
      doc.addField(GenericField.createTextField("content", value));
  }

  protected void addLocatorField(DocumentIF doc, LocatorIF locator) {
    if (locator != null) {
			String notation = locator.getNotation();
      String address = locator.getAddress();      
      if (notation != null) doc.addField(GenericField.createTextField("notation", notation));
      if (address != null) doc.addField(GenericField.createTextField("address", address));
    }
  }
    
  public DocumentIF generate(AssociationIF assoc) {
    // Create document
    DocumentIF doc = createDocument();
    // Add fields
    addObjectFields(doc, assoc, "A");
    return doc;
  }
  
  public DocumentIF generate(AssociationRoleIF assocrl) {
    // Create document
    DocumentIF doc = createDocument();
    // Add fields
    addObjectFields(doc, assocrl, "R");
    return doc;
  }

  public DocumentIF generate(TopicNameIF basename) {
    // Create document
    DocumentIF doc = createDocument();
    // Add fields
    addObjectFields(doc, basename, "B");
    addContentField(doc, basename.getValue());
    return doc;
  }

  public DocumentIF generate(OccurrenceIF occur) {
    // Create document
    DocumentIF doc = createDocument();
    // Add fields
    addObjectFields(doc, occur, "O");
    if (ObjectUtils.equals(occur.getDataType(), DataTypes.TYPE_URI))
			addLocatorField(doc, occur.getLocator());
		else
			addContentField(doc, occur.getValue());
    return doc;
  }

  public DocumentIF generate(VariantNameIF variant) {
    // Create document
    DocumentIF doc = createDocument();
    // Add fields
    addObjectFields(doc, variant, "N");
    if (ObjectUtils.equals(variant.getDataType(), DataTypes.TYPE_URI))
			addLocatorField(doc, variant.getLocator());
		else
    addContentField(doc, variant.getValue());
    return doc;
  }

  public DocumentIF generate(TopicIF topic) {
    // Create document
    DocumentIF doc = createDocument();
    // Add fields
    addObjectFields(doc, topic, "T");
    return doc;
  }
  
  public DocumentIF generate(TopicMapIF topicmap) {
    // Create document
    DocumentIF doc = createDocument();
    // Add fields
    addObjectFields(doc, topicmap, "M");
    return doc;
  }

  protected DocumentIF createDocument() {
    return new TopicMapDocument();
  }
  
}
