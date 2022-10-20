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
package net.ontopia.topicmaps.utils.jtm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapWriterIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.TypedIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.utils.PSI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PUBLIC: Exports topic maps to the JTM 1.0 interchange format. See the <a
 * href="http://www.cerny-online.com/jtm/1.0/">JTM homepage</a> for a
 * specification of the JTM 1.0 exchange format for topic map fragments.
 * 
 * @since 5.1.0
 */
public class JTMTopicMapWriter implements TopicMapWriterIF {
  private static final Logger log = LoggerFactory
      .getLogger(JTMTopicMapWriter.class.getName());

  private static final String TYPE = "type";
  private static final String SI = "si:";
  private final static String VERSION = "1.0";

  private JSONWriter writer;
  private LocatorIF baseLoc;

  private enum LOCATOR_TYPE {
    IID,
    SID,
    SL
  }
  
  /**
   * PUBLIC: Create an JTMTopicMapWriter that writes to a given File in
   * UTF-8. <b>Warning:</b> Use of this method is discouraged, as it is very
   * easy to get character encoding errors with this method.
   * 
   * @param file Where the output should be written.
   */
  public JTMTopicMapWriter(File file) throws IOException {
    this(new FileOutputStream(file), "utf-8");
  }
  
  /**
   * PUBLIC: Create an JTMTopicMapWriter that writes to a given File in
   * the given encoding.
   * 
   * @param file Where the output should be written.
   * @param encoding The desired character encoding.
   */
  public JTMTopicMapWriter(File file, String encoding) throws IOException {
    this(new FileOutputStream(file), encoding);
    writer.setCloseWriter(true);
  }
  
  /**
   * PUBLIC: Create an JTMTopicMapWriter that writes to a given OutputStream in
   * UTF-8. <b>Warning:</b> Use of this method is discouraged, as it is very
   * easy to get character encoding errors with this method.
   * 
   * @param stream Where the output should be written.
   */
  public JTMTopicMapWriter(OutputStream stream) throws IOException {
    this(stream, "utf-8");
  }

  /**
   * PUBLIC: Create an JTMTopicMapWriter that writes to a given OutputStream in
   * the given encoding.
   * 
   * @param stream Where the output should be written.
   * @param encoding The desired character encoding.
   */
  public JTMTopicMapWriter(OutputStream stream, String encoding)
      throws IOException {
    this(new OutputStreamWriter(stream, encoding));
  }

  /**
   * PUBLIC: Create an JTMTopicMapWriter that writes to a given Writer.
   * 
   * @param out Where the output should be written.
   */
  public JTMTopicMapWriter(Writer out) {
    writer = new JSONWriter(out);
  }

  /**
   * PUBLIC: Writes out the given topic map.
   * 
   * @param tm The topic map to be serialized as JTM.
   */
  @Override
  public void write(TopicMapIF tm) throws IOException {
    write((TMObjectIF) tm);
  }

  /**
   * PUBLIC: Write the given topic map construct as a JTM fragment.
   * 
   * @param object The topic map construct to be serialized as JTM fragment.
   */
  public void write(TMObjectIF object) throws IOException {
    // store the base address for this map
    baseLoc = object.getTopicMap().getStore().getBaseAddress();
    
    writer.object().pair("version", VERSION);
    
    String key = "item_type";
    if (object instanceof TopicMapIF) {
      writer.pair(key, "topicmap");
      serializeTopicMap((TopicMapIF) object);
    } else if (object instanceof TopicIF) {
      writer.pair(key, "topic");
      serializeTopic((TopicIF) object, true);
    } else if (object instanceof TopicNameIF) {
      writer.pair(key, "name");
      serializeName((TopicNameIF) object, true);
    } else if (object instanceof VariantNameIF) {
      writer.pair(key, "variant");
      serializeVariant((VariantNameIF) object, true);
    } else if (object instanceof OccurrenceIF) {
      writer.pair(key, "occurrence");
      serializeOccurrence((OccurrenceIF) object, true);
    } else if (object instanceof AssociationIF) {
      writer.pair(key, "association");
      serializeAssociation((AssociationIF) object, true);
    } else if (object instanceof AssociationRoleIF) {
      writer.pair(key, "role");
      serializeRole((AssociationRoleIF) object, true);
    }
    
    writer.finish();
  }

  /**
   * EXPERIMENTAL: Write out a collection of topics and associations
   * as a JTM fragment, represented as a complete topic map. The
   * identities, names, variants, occurrences, and types of the topics
   * are output, as are the complete associations. Note that the
   * associations of topics in the topics collection are not output,
   * unless they are contained in the assocs collection.
   */
  public void write(Collection<TopicIF> topics,
                    Collection<AssociationIF> assocs) throws IOException {
    baseLoc = null;
    writer.object().pair("version", VERSION);
    writer.pair("item_type", "topicmap");

    writer.key("topics").array();
    for (TopicIF topic : topics) {
      if (baseLoc == null) {
        baseLoc = topic.getTopicMap().getStore().getBaseAddress();
      }
      serializeTopic(topic, false);
    }
    writer.endArray();

    writer.key("associations").array();
    for (AssociationIF assoc : assocs) {
      serializeAssociation(assoc, false);
    }
    for (TopicIF instance : topics) {
      for (TopicIF type : (Collection<TopicIF>) instance.getTypes()) {
        serializeTypeInstanceAssociation(type, instance);
      }
    }

    writer.endArray();

    writer.endObject();
    writer.finish();
  }

  /**
   * INTERNAL: Serializes a complete topic map to the JTM output stream.
   * 
   * @param tm the topic map to be serialized as JTM.
   */
  private void serializeTopicMap(TopicMapIF tm) throws IOException {
    // ----------------- Topics --------------------
    Collection<TopicIF> topics = tm.getTopics();
    if (!topics.isEmpty()) {
      writer.key("topics").array();
      for (TopicIF topic : topics) {
        serializeTopic(topic, false);
      }
      writer.endArray();
    }

    // ----------------- Associations --------------
    ClassInstanceIndexIF classIndex = (ClassInstanceIndexIF) tm
      .getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");

    Collection<AssociationIF> assocs = tm.getAssociations();
    
    // type-instance associations have to be retrieved from the index,
    // as they are not returned by tm.getAssociations()
    Collection<TopicIF> topicTypes = classIndex.getTopicTypes();
    
    // only write the "associations" key if there are any associations defined.
    if (!assocs.isEmpty() || !topicTypes.isEmpty()) {
      writer.key("associations").array();
      
      // first, write all type-instance associations
      for (TopicIF type : topicTypes) {
        Collection<TopicIF> instances = classIndex.getTopics(type);
        for (TopicIF instance : instances) {
          serializeTypeInstanceAssociation(type, instance);
        }
      }
      
      // and now write the remaining associations
      for (AssociationIF assoc : assocs) {
        serializeAssociation(assoc, false);
      }
      
      writer.endArray();
    }

    serializeItemIdentifiers(tm);
    serializeReifier(tm);

    writer.endObject();
  }
  
  /**
   * INTERNAL: Serialize a topic to the underlying JTM stream.
   *  
   * @param topic the topic to be serialized.
   * @param topLevel if the element is serialized as top-level element.
   */
  private void serializeTopic(TopicIF topic, boolean topLevel)
      throws IOException {
    if (!topLevel) {
      writer.object();
    }

    serializeItemIdentifiers(topic);
    serializeSubjectIdentifiers(topic);
    serializeSubjectLocators(topic);

    // ------------------- Names -----------------
    Collection<TopicNameIF> names = topic.getTopicNames();
    if (!names.isEmpty()) {
      writer.key("names").array();
      for (TopicNameIF name : names) {
        serializeName(name, false);
      }
      writer.endArray();
    }

    // ----------------- Occurrences --------------
    Collection<OccurrenceIF> occurrences = topic.getOccurrences();
    if (!occurrences.isEmpty()) {
      writer.key("occurrences").array();
      for (OccurrenceIF oc : occurrences) {
        serializeOccurrence(oc, false);
      }
      writer.endArray();
    }

    writer.endObject();
  }

  /**
   * INTERNAL: Serialize an association to the underlying JTM stream.
   * 
   * @param association the association to be serialized.
   * @param topLevel if the element is serialized as top-level element.
   */
  private void serializeAssociation(AssociationIF association, boolean topLevel)
      throws IOException {
    if (!topLevel) {
      writer.object();
    }
    
    serializeType(association);

    // ----------------- Roles --------------
    Collection<AssociationRoleIF> roles = association.getRoles();
    if (!roles.isEmpty()) {
      writer.key("roles").array();
      for (AssociationRoleIF role : roles) {
        serializeRole(role, false);
      }
      writer.endArray();
    }

    serializeScope(association);
    serializeItemIdentifiers(association);
    serializeReifier(association);

    writer.endObject();
  }

  /**
   * INTERNAL: Serialize a role to the underlying JTM stream.
   * 
   * @param role the role to be serialized.
   * @param topLevel if the element is serialized as top-level element.
   */
  private void serializeRole(AssociationRoleIF role, boolean topLevel)
      throws IOException {
    if (!topLevel) {
      writer.object();
    }
    
    writer.
      pair("player", getTopicRef(role.getPlayer())).
      pair(TYPE, getTopicRef(role.getType()));
    
    serializeItemIdentifiers(role);
    serializeReifier(role);

    writer.endObject();
  }
  
  /**
   * INTERNAL: Serialize a type-instance association.
   * 
   * @param type the given type topic.
   * @param instance the given instance topic.
   */
  private void serializeTypeInstanceAssociation(TopicIF type, TopicIF instance)
      throws IOException {
    writer.object().pair(TYPE, SI + PSI.getSAMTypeInstance().getExternalForm());
    writer.key("roles").array();
    
    // Type Role
    writer.object();
    writer.pair("player", getTopicRef(type));
    writer.pair(TYPE, SI + PSI.getSAMType().getExternalForm());
    writer.endObject();

    // Instance Role
    writer.object();
    writer.pair("player", getTopicRef(instance));
    writer.pair(TYPE, SI + PSI.getSAMInstance().getExternalForm());
    writer.endObject();
    
    writer.endArray();
    writer.endObject();
  }

  /**
   * INTERNAL: Serialize a topic name to the underlying JTM stream.
   * 
   * @param name the name to be serialized.
   * @param topLevel if the element is serialized as top-level element.
   */
  private void serializeName(TopicNameIF name, boolean topLevel)
      throws IOException {
    if (!topLevel) {
      writer.object();
    } else {
      serializeParent(name.getTopic());
    }

    writer.pair("value", name.getValue());
    serializeType(name);
    
    // ----------------- Variants --------------
    Collection<VariantNameIF> variants = name.getVariants();
    if (!variants.isEmpty()) {
      writer.key("variants").array();
      for (VariantNameIF var : variants) {
        serializeVariant(var, false);
      }
      writer.endArray();
    }
    
    serializeScope(name);
    serializeItemIdentifiers(name);
    serializeReifier(name);

    writer.endObject();
  }

  /**
   * INTERNAL: Serialize a variant to the underlying JTM stream.
   * 
   * @param variant the variant to be serialized.
   * @param topLevel if the element is serialized as top-level element.
   */
  private void serializeVariant(VariantNameIF variant, boolean topLevel)
      throws IOException {
    if (!topLevel) {
      writer.object();
    }
    
    serializeValue(variant.getLocator(), variant.getValue());
    serializeDataType(variant.getDataType());
    serializeScope(variant);
    serializeItemIdentifiers(variant);
    serializeReifier(variant);
    
    writer.endObject();
  }

  /**
   * INTERNAL: Serialize an occurrence to the underlying JTM stream.
   * 
   * @param occurrence the occurrence to be serialized.
   * @param topLevel if the element is serialized as top-level element.
   */
  private void serializeOccurrence(OccurrenceIF occurrence, boolean topLevel)
      throws IOException {
    if (!topLevel) {
      writer.object();
    } else {
      serializeParent(occurrence.getTopic());
    }
    
    serializeValue(occurrence.getLocator(), occurrence.getValue());
    serializeType(occurrence);
    serializeDataType(occurrence.getDataType());
    serializeScope(occurrence);
    serializeItemIdentifiers(occurrence);
    serializeReifier(occurrence);
    
    writer.endObject();
  }

  /**
   * INTERNAL: Serialize the parent topic of a topic map construct. The parent
   * is serialized by merging all his item/subject identifiers and subject
   * locators together. If the parent is null, nothing will be serialized.
   * 
   * @param parent the parent topic to be serialized.
   */
  private void serializeParent(TopicIF parent)
      throws IOException {
    if (parent != null) {
      Collection<String> ids = new LinkedList<String>();
      
      for (Object loc : parent.getItemIdentifiers()) {
        ids.add(getJTMTopicRef(LOCATOR_TYPE.IID, (LocatorIF) loc));
      }

      for (Object loc : parent.getSubjectIdentifiers()) {
        ids.add(getJTMTopicRef(LOCATOR_TYPE.SID, (LocatorIF) loc));
      }

      for (Object loc : parent.getSubjectLocators()) {
        ids.add(getJTMTopicRef(LOCATOR_TYPE.SL, (LocatorIF) loc));
      }
      
      if (!ids.isEmpty()) {
        writer.key("parent").array();
        for (String id : ids) {
          writer.value(id);
        }
        writer.endArray();
      }      
    }
  }

  /**
   * INTERNAL: Serialize the item identifiers of a {@link TMObjectIF}. If the
   * object does not have an item identifier, nothing will be serialized.
   * 
   * @param obj the {@link TMObjectIF} to be serialized.
   */
  private void serializeItemIdentifiers(TMObjectIF obj)
      throws IOException {
    Collection<LocatorIF> ids = obj.getItemIdentifiers();
    serializeIdentifiers("item_identifiers", ids);
  }

  /**
   * INTERNAL: Serialize the subject identifiers of a {@link TopicIF}. If the
   * object does not have a subject identifier, nothing will be serialized.
   * 
   * @param topic the {@link TopicIF} to be serialized.
   */
  private void serializeSubjectIdentifiers(TopicIF topic)
      throws IOException {
    Collection<LocatorIF> sids = topic.getSubjectIdentifiers();
    serializeIdentifiers("subject_identifiers", sids);
  }

  /**
   * INTERNAL: Serialize the subject locators of a {@link TopicIF}. If the
   * object does not have a subject locator, nothing will be serialized.
   * 
   * @param topic the {@link TopicIF} to be serialized.
   */
  private void serializeSubjectLocators(TopicIF topic)
      throws IOException {
    Collection<LocatorIF> slocs = topic.getSubjectLocators();
    serializeIdentifiers("subject_locators", slocs);
  }
  
  /**
   * INTERNAL: Serialize a collection of {@link LocatorIF} objects.
   * 
   * @param key the key to be used for serialization.
   * @param ids the collection of ids to be serialized.
   */
  private void serializeIdentifiers(String key, Collection<LocatorIF> ids)
      throws IOException {
    if (!ids.isEmpty()) {
      writer.key(key).array();
      for (LocatorIF id : ids) {
        writer.value(normaliseLocatorReference(id));
      }
      writer.endArray();
    }
  }

  /**
   * INTERNAL: Serialize the scopes for a given topic map construct. If the
   * construct is not scoped, nothing will be serialized.
   * 
   * @param obj the scoped object to be used.
   */
  private void serializeScope(ScopedIF obj)
      throws IOException {
    Collection<TopicIF> scopes = obj.getScope();
    if (!scopes.isEmpty()) {
      writer.key("scope").array();
      for (TopicIF ref : scopes) {
        writer.value(getTopicRef(ref));
      }
      writer.endArray();
    }
  }

  /**
   * INTERNAL: Serialize the type for a given typed topic map construct. If the
   * construct is not typed, nothing will be serialized.
   * 
   * @param obj the typed topic map construct to be used.
   */
  private void serializeType(TypedIF obj)
      throws IOException {
    TopicIF type = obj.getType();
    if (type != null) {
      writer.pair(TYPE, getTopicRef(type));
    }
  }

  /**
   * INTERNAL: Serialize the given datatype for a variant or occurrence
   * construct. If the datatype is equal to the default datatype (
   * {@link PSI#XSD_STRING}), nothing will be serialized.
   * 
   * @param type a locator defining the datatype.
   * @see PSI#XSD_STRING
   */
  private void serializeDataType(LocatorIF type) throws IOException {
    if (type != null && !PSI.getXSDString().equals(type)) {
      writer.pair("datatype", type.getExternalForm());
    }
  }

  /**
   * INTERNAL: Serialize the value for a given typed topic map construct. If the
   * locator representation is not null or empty, write a normalised version of
   * it (relative to the base locator of the topic map), otherwise serialize the
   * string version of the value.
   * 
   * @param loc the value as locator.
   * @param value the value in string representation.
   */
  private void serializeValue(LocatorIF loc, String value)
      throws IOException {
    String v = value;
    if (loc != null) {
      v = normaliseLocatorReference(loc);
    }
    writer.pair("value", v);
  }
  
  /**
   * INTERNAL: Serialize the reference to the reifier of a topic map construct,
   * if there is one present.
   * 
   * @param obj a reifiable topic map construct.
   */
  private void serializeReifier(ReifiableIF obj) throws IOException {
    if (obj.getReifier() != null) {
      writer.pair("reifier", getTopicRef(obj.getReifier()));
    }
  }

  /**
   * INTERNAL: Normalise a given locator reference according to the CXTM spec, i.e. make
   * it relative to the base locator of the topic map.
   * 
   * @param reference the reference locator address.
   */
  private String normaliseLocatorReference(LocatorIF loc) {
    String reference = loc.getAddress();
    String retVal = reference.substring(longestCommonPath(reference,
            baseLoc.getAddress()).length());
    if (retVal.startsWith("/")) {
      retVal = retVal.substring(1);
    }
    
    return retVal;
  }
  
  /**
   * INTERNAL: Returns the longest common path of two Strings.
   * The longest common path is the longest common prefix that ends with a '/'.
   * If one string is a prefix of the other, the the longest common path is
   * the shortest (i.e. the one that is a prefix of the other).
   */
  private String longestCommonPath(String source1, String source2) {
    String retVal = "";    

    if (source1.startsWith(source2)) {
      retVal = source2;
    } else if (source2.startsWith(source1)) {
      retVal = source1;
    } else {
      int i = 0;
      int lastSlashIndex = 0;
      
      while (i < source1.length() && i < source2.length() 
              && source1.charAt(i) == source2.charAt(i)) {
        if (source1.charAt(i) == '/') {
          lastSlashIndex = i;
        }
        i++;
      }
  
      if (lastSlashIndex == -1) {
        retVal = "";
      } else { 
        retVal = source1.substring(0, lastSlashIndex);
      }
    }
       
    return retVal;
  }

  /**
   * INTERNAL: Returns the reference to a topic in JTM notation. 
   * 
   * The order of preference for constructing a topic reference is as follows:
   * 
   * <ul>
   * <li>subject identifier
   * <li>subject locator
   * <li>item identifier
   * </ul>
   * 
   * @param ref the topic to be referenced.
   * @return a reference to this topic in JTM notation.
   * @see #getJTMTopicRef(LOCATOR_TYPE, LocatorIF)
   */
  private String getTopicRef(TopicIF ref) {
    // prefer subject identifiers if present
    if (!ref.getSubjectIdentifiers().isEmpty()) {
      return getJTMTopicRef(LOCATOR_TYPE.SID, (LocatorIF) ref
          .getSubjectIdentifiers().iterator().next());
    } else if (!ref.getSubjectLocators().isEmpty()) {
      return getJTMTopicRef(LOCATOR_TYPE.SL, (LocatorIF) ref
          .getSubjectLocators().iterator().next());
    } else if (!ref.getItemIdentifiers().isEmpty()) {
      return getJTMTopicRef(LOCATOR_TYPE.IID, (LocatorIF) ref
          .getItemIdentifiers().iterator().next());
    } else {
      // should not happen, as every topic needs to have one of them
      log.warn("Topic with objectID:" + ref.getObjectId()
          + " has not a single item/subject identifier or locator.");
      return "";
    }
  }
  
  /**
   * INTERNAL: Returns a locator in JTM notation. When a construct is
   * referred to by means of a locator L, the string is to be constructed as
   * follows:
   * 
   * <ul>
   * <li>L is a subject identifier: si:L
   * <li>L is a subject locator: sl:L
   * <li>L is an item identifier: ii:L
   * </ul>   
   * 
   * @param type the type of the locator.
   * @param loc the internal locator to be converted.
   * @return a string representation of the internal locator in JTM notation.
   */
  private String getJTMTopicRef(LOCATOR_TYPE type, LocatorIF loc) {
    StringBuilder sb = new StringBuilder();
    switch (type) {
    case IID:
      sb.append("ii:");
      String id = normaliseLocatorReference(loc);
      if (!id.startsWith("http://") && !id.startsWith("#")) {
        sb.append('#');
      }
      sb.append(id);
      break;
    case SID:
      sb.append(SI);
      sb.append(loc.getAddress());
      break;
    case SL:
      sb.append(SI);
      sb.append(loc.getAddress());
      break;
    }
    return sb.toString();
  }

  /**
   * JTMTopicMapWriter has no additional properties.
   * @param properties 
   */
  @Override
  public void setAdditionalProperties(Map<String, Object> properties) {
    // no-op
  }
}
