package net.ontopia.topicmaps.utils.jtm;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapWriterIF;
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
 * @since 5.1
 */
public class JTMTopicMapWriter implements TopicMapWriterIF {
  static Logger log = LoggerFactory
      .getLogger(JTMTopicMapWriter.class.getName());

  private final static String VERSION = "1.0";
  
  private JSONWriter writer;
  private LocatorIF baseLoc;
  
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
      writeTopicMap((TopicMapIF) object);
    } else if (object instanceof TopicIF) {
      writer.pair(key, "topic");
      writeTopic((TopicIF) object);
    } else if (object instanceof TopicNameIF) {
      writer.pair(key, "name");
      writeName((TopicNameIF) object);
    } else if (object instanceof VariantNameIF) {
      writer.pair(key, "variant");
      writeVariant((VariantNameIF) object);
    } else if (object instanceof OccurrenceIF) {
      writer.pair(key, "occurrence");
      writeOccurrence((OccurrenceIF) object);
    } else if (object instanceof AssociationIF) {
      writer.pair(key, "association");
      writeAssociation((AssociationIF) object);
    } else if (object instanceof AssociationRoleIF) {
      writer.pair(key, "role");
      writeRole((AssociationRoleIF) object);
    }
    
    writer.finish();
  }

  @SuppressWarnings("unchecked")
  private void writeTopicMap(TopicMapIF tm) throws IOException {
    // ----------------- Topics --------------------
    Collection<TopicIF> topics = tm.getTopics();
    if (!topics.isEmpty()) {
      writer.key("topics").array();
      for (TopicIF topic : topics) {
        writeTopic(topic);
      }
      writer.endArray();
    }

    // ----------------- Associations --------------
    ClassInstanceIndexIF classIndex = (ClassInstanceIndexIF) tm
      .getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");
    
    Collection<TopicIF> topicTypes = classIndex.getTopicTypes();
    Collection<AssociationIF> assocs = tm.getAssociations();
    
    if (!assocs.isEmpty() || !topicTypes.isEmpty()) {
      writer.key("associations").array();
      
      for (TopicIF type : topicTypes) {
        Collection<TopicIF> instances = classIndex.getTopics(type);
        for (TopicIF instance : instances) {
          writeTypeInstanceAssociation(type, instance);
        }
      }
      for (AssociationIF assoc : assocs) {
        writeAssociation(assoc);
      }
      writer.endArray();
    }

    writeReifier(tm);
    writeIdentifiers("item_identifiers", tm.getItemIdentifiers());

    writer.endObject();
  }
  
  // --------------------------------------------------------------------
  // Methods used on Topics
  // --------------------------------------------------------------------

  @SuppressWarnings("unchecked")
  private void writeTopic(TopicIF topic) throws IOException {
    writer.object();

    writeIdentifiers("item_identifiers", topic.getItemIdentifiers());
    writeIdentifiers("subject_identifiers", topic.getSubjectIdentifiers());
    writeIdentifiers("subject_locators", topic.getSubjectLocators());

    Collection<TopicNameIF> names = topic.getTopicNames();
    if (!names.isEmpty()) {
      writer.key("names").array();
      for (TopicNameIF name : names) {
        writeName(name);
      }
      writer.endArray();
    }

    Collection<OccurrenceIF> occurrences = topic.getOccurrences();
    if (!occurrences.isEmpty()) {
      writer.key("occurrences").array();
      for (OccurrenceIF oc : occurrences) {
        writeOccurrence(oc);
      }
      writer.endArray();
    }

    writer.endObject();
  }

  /**
   * Write the given association.
   */
  @SuppressWarnings("unchecked")
  private void writeAssociation(AssociationIF association) throws IOException {
    writer.object().pair("type", getTopicRef(association.getType()));
    
    Collection<AssociationRoleIF> roles = association.getRoles();
    if (!roles.isEmpty()) {
      writer.key("roles").array();
      for (AssociationRoleIF role : roles) {
        writeRole(role);
      }
      writer.endArray();
    }
    
    writeRefArray("scope", association.getScope());
    writeReifier(association);
    writeIdentifiers("item_identifiers", association.getItemIdentifiers());
    
    writer.endObject();
  }

  @SuppressWarnings("unchecked")
  private void writeRole(AssociationRoleIF role) throws IOException {
    writer.object().
      pair("player", getTopicRef(role.getPlayer())).
      pair("type", getTopicRef(role.getType()));
    
    writeReifier(role);
    writeIdentifiers("item_identifiers", role.getItemIdentifiers());

    writer.endObject();
  }
  
  private void writeTypeInstanceAssociation(TopicIF type, TopicIF instance)
      throws IOException {
    writer.object().pair("type", "si:" + PSI.getSAMTypeInstance().getExternalForm());
    writer.key("roles").array();
    
    // Type Role
    writer.object();
    writer.pair("player", getTopicRef(type));
    writer.pair("type", "si:" + PSI.getSAMType().getExternalForm());
    writer.endObject();

    // Instance Role
    writer.object();
    writer.pair("player", getTopicRef(instance));
    writer.pair("type", "si:" + PSI.getSAMInstance().getExternalForm());
    writer.endObject();
    
    writer.endArray();
    writer.endObject();
  }
  
  @SuppressWarnings("unchecked")
  private void writeName(TopicNameIF name) throws IOException {
    writer.object().pair("value", name.getValue());
    
    if (name.getType() != null) {
      writer.pair("type", getTopicRef(name.getType()));
    }
    
    Collection<VariantNameIF> variants = name.getVariants();
    if (!variants.isEmpty()) {
      writer.key("variants").array();
      for (VariantNameIF var : variants) {
        writeVariant(var);
      }
      writer.endArray();
    }
    
    writeRefArray("scope", name.getScope());
    writeReifier(name);
    writeIdentifiers("item_identifiers", name.getItemIdentifiers());

    writer.endObject();
  }

  /**
   * Write the given TopicNameIF to the given Writer, after line breaks with the
   * given indentString.
   */
  @SuppressWarnings("unchecked")
  private void writeVariant(VariantNameIF variant) throws IOException {
    writer.object().pair("value", variant.getValue());
    writeDataType(variant.getDataType());
    
    writeRefArray("scope", variant.getScope());
    writeReifier(variant);
    writeIdentifiers("item_identifiers", variant.getItemIdentifiers());
    
    writer.endObject();
  }

  @SuppressWarnings("unchecked")
  private void writeOccurrence(OccurrenceIF occurrence) throws IOException {
    writer.object().
      pair("value", occurrence.getValue()).
      pair("type", getTopicRef(occurrence.getType()));
    
    writeDataType(occurrence.getDataType());
    writeRefArray("scope", occurrence.getScope());
    writeReifier(occurrence);
    writeIdentifiers("item_identifiers", occurrence.getItemIdentifiers());
    
    writer.endObject();
  }

  private void writeIdentifiers(String key, Collection<LocatorIF> ids)
      throws IOException {
    if (!ids.isEmpty()) {
      writer.key(key).array();
      for (LocatorIF id : ids) {
        writer.value(getIdentifier(id));
      }
      writer.endArray();
    }
  }
  
  private String getIdentifier(LocatorIF loc) {
    String base = baseLoc.getAddress();
    String id = null;
    if (loc.getAddress().startsWith(base)) {
      String addr = loc.getAddress();
      int pos = addr.indexOf('#');
      if (pos != -1) {
        id = addr.substring(pos + 1);
      }
    }
    
    if (id == null) {
      id = loc.getExternalForm();
    }
    return id;
  }
  
  private void writeRefArray(String key, Collection<TopicIF> coll)
      throws IOException {
    if (!coll.isEmpty()) {
      writer.key(key).array();
      for (TopicIF ref : coll) {
        writer.value(getTopicRef(ref));
      }
      writer.endArray();
    }
  }
  
  private void writeDataType(LocatorIF type) throws IOException {
    if (type != null && !PSI.getXSDString().equals(type)) {
      writer.pair("datatype", type.getExternalForm());
    }
  }
  
  private void writeReifier(ReifiableIF obj) throws IOException {
    if (obj.getReifier() != null) {
      writer.pair("reifier", getTopicRef(obj.getReifier()));
    }
  }

  /**
   * Get the reference to a topic in JTM notation. When a topic is referred to
   * by means of a locator L, the string is to be constructed as follows: 
   * 
   * <ul>
   *   <li>L is a subject identifier: si:L
   *   <li>L is a subject locator: sl:L
   *   <li>L is an item identifier: ii:L
   * </ul>
   * 
   * @param ref The topic to be referenced.
   * @return A reference to this topic in JTM notation.
   */
  private String getTopicRef(TopicIF ref) {
    StringBuilder sb = new StringBuilder();
    if (!ref.getItemIdentifiers().isEmpty()) {
      sb.append("ii:");
      LocatorIF loc = (LocatorIF) ref.getItemIdentifiers().iterator().next();
      sb.append("#" + getIdentifier(loc));
    } else if (!ref.getSubjectIdentifiers().isEmpty()) {
      sb.append("si:");
      LocatorIF loc = (LocatorIF) ref.getSubjectIdentifiers().iterator().next();
      sb.append(loc.getExternalForm());
    } else if (!ref.getSubjectLocators().isEmpty()) {
      sb.append("sl:");
      LocatorIF loc = (LocatorIF) ref.getSubjectLocators().iterator().next();
      sb.append(loc.getExternalForm());
    } else {
      // TODO: throw an error
    }
    return sb.toString();
  }
}
