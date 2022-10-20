/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.infoset.fulltext.topicmaps;

import java.util.Objects;
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
    if (value != null) {
      doc.addField(GenericField.createTextField("content", value));
    }
  }

  protected void addLocatorField(DocumentIF doc, LocatorIF locator) {
    if (locator != null) {
			String notation = locator.getNotation();
      String address = locator.getAddress();      
      if (notation != null) {
        doc.addField(GenericField.createTextField("notation", notation));
      }
      if (address != null) {
        doc.addField(GenericField.createTextField("address", address));
      }
    }
  }
    
  @Override
  public DocumentIF generate(AssociationIF assoc) {
    // Create document
    DocumentIF doc = createDocument();
    // Add fields
    addObjectFields(doc, assoc, "A");
    return doc;
  }
  
  @Override
  public DocumentIF generate(AssociationRoleIF assocrl) {
    // Create document
    DocumentIF doc = createDocument();
    // Add fields
    addObjectFields(doc, assocrl, "R");
    return doc;
  }

  @Override
  public DocumentIF generate(TopicNameIF basename) {
    // Create document
    DocumentIF doc = createDocument();
    // Add fields
    addObjectFields(doc, basename, "B");
    addContentField(doc, basename.getValue());
    return doc;
  }

  @Override
  public DocumentIF generate(OccurrenceIF occur) {
    // Create document
    DocumentIF doc = createDocument();
    // Add fields
    addObjectFields(doc, occur, "O");
    if (Objects.equals(occur.getDataType(), DataTypes.TYPE_URI)) {
      addLocatorField(doc, occur.getLocator());
    } else {
      addContentField(doc, occur.getValue());
    }
    return doc;
  }

  @Override
  public DocumentIF generate(VariantNameIF variant) {
    // Create document
    DocumentIF doc = createDocument();
    // Add fields
    addObjectFields(doc, variant, "N");
    if (Objects.equals(variant.getDataType(), DataTypes.TYPE_URI)) {
      addLocatorField(doc, variant.getLocator());
    } else {
      addContentField(doc, variant.getValue());
    }
    return doc;
  }

  @Override
  public DocumentIF generate(TopicIF topic) {
    // Create document
    DocumentIF doc = createDocument();
    // Add fields
    addObjectFields(doc, topic, "T");
    return doc;
  }
  
  @Override
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
