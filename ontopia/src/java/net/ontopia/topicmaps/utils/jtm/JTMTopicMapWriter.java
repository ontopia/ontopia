package net.ontopia.topicmaps.utils.jtm;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapWriterIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.utils.PSI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PUBLIC: Exports topic maps to the JTM 1.0 interchange format.
 * 
 * @since 5.1
 */
public class JTMTopicMapWriter implements TopicMapWriterIF {
  static Logger log = LoggerFactory
      .getLogger(JTMTopicMapWriter.class.getName());

  private final static String VERSION = "1.0";
  private final static int INDENT = 3;
  
  protected String encoding; // the encoding reported on the first line

  protected Writer out;
  protected String base;

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
    this(new OutputStreamWriter(stream, encoding), encoding);
  }

  /**
   * PUBLIC: Create an LTMTopicMapWriter that writes to a given Writer.
   * 
   * @param out Where the output should be written.
   * @param encoding The encoding used by the writer. This is the encoding that
   *          will be declared on the first line of the LTM file. It must be
   *          reported, because there is no way for the LTMTopicMapWriter to
   *          know what encoding the writer uses.
   * @since 4.0
   */
  public JTMTopicMapWriter(Writer out, String encoding) {
    this.encoding = encoding;
    this.out = out;
  }

  /**
   * PUBLIC: Writes out the given topic map.
   */
  public void write(TopicMapIF tm) throws IOException {
    writeTopicMap(tm, true);
    out.flush();
  }

  /**
   * PUBLIC: Writes out the given topic map object.
   */
  public void write(TMObjectIF object) throws IOException {
    if (object instanceof TopicMapIF) {
      writeTopicMap((TopicMapIF) object, true);
    } else if (object instanceof TopicIF) {
      writeTopic((TopicIF) object, true, 0);
    } else if (object instanceof TopicNameIF) {
      writeName((TopicNameIF) object, true, 0);
    } else if (object instanceof VariantNameIF) {
      writeVariant((VariantNameIF) object, true, 0);
    } else if (object instanceof OccurrenceIF) {
      writeOccurrence((OccurrenceIF) object, true, 0);
    } else if (object instanceof AssociationIF) {
      writeAssociation((AssociationIF) object, true, 0);
    } else if (object instanceof AssociationRoleIF) {
      writeRole((AssociationRoleIF) object, true, 0);
    }
    
    out.flush();
  }

  @SuppressWarnings("unchecked")
  private void writeTopicMap(TopicMapIF tm, boolean isStartElement)
      throws IOException {
    LocatorIF baseLocator = tm.getStore().getBaseAddress();
    base = (baseLocator == null) ? null : baseLocator.getExternalForm();

    writeHeader("topicmap", isStartElement, 0);
    
    // ----------------- Topics --------------------
    Collection<TopicIF> topics = tm.getTopics();
    if (!topics.isEmpty()) {
      out.write("\"topics\":[\n");
      int cnt = 0;
      for (TopicIF topic : topics) {
        writeTopic(topic, false, 1);
        if (++cnt < topics.size()) {
          out.write(",\n");
        }
      }
      out.write("]");
    }

    // ----------------- Associations --------------
    Collection<AssociationIF> assocs = tm.getAssociations();
    if (!assocs.isEmpty()) {
      writeLF(0);
      out.write("\"associations\":[\n");
      int cnt = 0;
      for (AssociationIF assoc : assocs) {
        writeAssociation(assoc, false, 1);
        if (++cnt < assocs.size()) {
          out.write(",\n");
        }
      }
      out.write("]");
    }
    
    out.write("}");
  }
  
  // --------------------------------------------------------------------
  // Methods used on Topics
  // --------------------------------------------------------------------

  /**
   * Write the given topic. 
   */
  @SuppressWarnings("unchecked")
  private void writeTopic(TopicIF topic, boolean isStartElement, int level)
      throws IOException {
    
    writeHeader("topic", isStartElement, level);

    boolean added = false;
    added = writeIdentifiers("item_identifiers", topic.getItemIdentifiers());
    if (added && !topic.getSubjectIdentifiers().isEmpty()) {
      writeLF(level);
    }
    added = writeIdentifiers("subject_identifiers", topic.getSubjectIdentifiers());
    if (added && !topic.getSubjectLocators().isEmpty()) {
      writeLF(level);
    }
    writeIdentifiers("subject_locators", topic.getSubjectLocators());

    Collection<TopicNameIF> names = topic.getTopicNames();
    if (!names.isEmpty()) {
      writeLF(level);
      out.write("\"names\":[\n");
      int cnt = 0;
      for (TopicNameIF name : names) {
        writeName(name, false, level+1);
        if (++cnt < names.size()) {
          out.write(",\n");
        }
      }
      out.write("]");
    }

    Collection<OccurrenceIF> occurrences = topic.getOccurrences();
    if (!occurrences.isEmpty()) {
      if (added) {
        writeLF(level);
      }
      out.write("\"occurrences\":[\n");
      int cnt = 0;
      for (OccurrenceIF oc : occurrences) {
        writeOccurrence(oc, false, level+1);
        if (++cnt < occurrences.size()) {
          out.write(",\n");
        }
      }
      out.write("]");
    }
    
    writeFooter();
  }

  /**
   * Write the given association.
   */
  @SuppressWarnings("unchecked")
  private void writeAssociation(AssociationIF association,
      boolean isStartElement, int level) throws IOException {

    writeHeader("association", isStartElement, level);
    
    writePair("type", association.getType());
    
    Collection<AssociationRoleIF> roles = association.getRoles();
    if (!roles.isEmpty()) {
      writeLF(level);
      out.write("\"roles\":[\n");
      int cnt = 0;
      for (AssociationRoleIF role : roles) {
        writeRole(role, false, level+1);
        if (++cnt < roles.size()) {
          out.write(",\n");
        }
      }
      out.write("]");
    }
    
    if (!association.getScope().isEmpty()) {
      Collection<TopicIF> scopes = association.getScope();
      writeLF(level);
      writeRefArray("scope", scopes);
    }
    
    if (association.getReifier() != null) {
      writeLF(level);
      writePair("reifier", association.getReifier());
    }

    if (!association.getItemIdentifiers().isEmpty()) {
      writeLF(level);
      writeIdentifiers("item_identifiers", association.getItemIdentifiers());
    }
    
    writeFooter();
  }

  @SuppressWarnings("unchecked")
  private void writeRole(AssociationRoleIF role, boolean isStartElement,
      int level) throws IOException {

    writeHeader("role", isStartElement, level);
    
    writePair("player", role.getPlayer());
    writeLF(level);
    writePair("type", role.getType());
    
    if (role.getReifier() != null) {
      writeLF(level);
      writePair("reifier", role.getReifier());
    }

    if (!role.getItemIdentifiers().isEmpty()) {
      writeLF(level);
      writeIdentifiers("item_identifiers", role.getItemIdentifiers());
    }
    
    writeFooter();
  }
  
  /**
   * Write the given TopicNameIF to the given Writer, after line breaks with the
   * given indentString.
   */
  @SuppressWarnings("unchecked")
  private void writeName(TopicNameIF name, boolean isStartElement, int level)
      throws IOException {
    
    writeHeader("name", isStartElement, level);
    
    writePair("value", name.getValue());
    
    if (name.getType() != null) {
      writeLF(level);
      writePair("type", name.getType());
    }
    
    Collection<VariantNameIF> variants = name.getVariants();
    if (!variants.isEmpty()) {
      writeLF(level);
      out.write("\"variants\":[\n");
      int cnt = 0;
      for (VariantNameIF var : variants) {
        writeVariant(var, false, level+1);
        if (++cnt < variants.size()) {
          out.write(",\n");
        }
      }
      out.write("]");
    }
    
    if (!name.getScope().isEmpty()) {
      Collection<TopicIF> scopes = name.getScope();
      writeLF(level);
      writeRefArray("scope", scopes);
    }
    
    if (name.getReifier() != null) {
      writeLF(level);
      writePair("reifier", name.getReifier());
    }

    if (!name.getItemIdentifiers().isEmpty()) {
      writeLF(level);
      writeIdentifiers("item_identifiers", name.getItemIdentifiers());
    }

    writeFooter();
  }

  /**
   * Write the given TopicNameIF to the given Writer, after line breaks with the
   * given indentString.
   */
  @SuppressWarnings("unchecked")
  private void writeVariant(VariantNameIF variant, boolean isStartElement,
      int level) throws IOException {
    
    writeHeader("variant", isStartElement, level);
   
    writePair("value", variant.getValue());

    writeDataType(variant.getDataType(), level);
    
    if (!variant.getScope().isEmpty()) {
      Collection<TopicIF> scopes = variant.getScope();
      writeLF(level);
      writeRefArray("scope", scopes);
    }
    
    if (variant.getReifier() != null) {
      writeLF(level);
      writePair("reifier", variant.getReifier());
    }

    if (!variant.getItemIdentifiers().isEmpty()) {
      writeLF(level);
      writeIdentifiers("item_identifiers", variant.getItemIdentifiers());
    }
    
    writeFooter();
  }

  /**
   * Write one given occurrence.
   */
  @SuppressWarnings("unchecked")
  private void writeOccurrence(OccurrenceIF occurrence, boolean isStartElement,
      int level) throws IOException {
    
    writeHeader("occurrence", isStartElement, level);
    
    writePair("value", occurrence.getValue());
    
    writeLF(level);
    writePair("type", occurrence.getType());
    
    writeDataType(occurrence.getDataType(), level);
    
    if (!occurrence.getScope().isEmpty()) {
      Collection<TopicIF> scopes = occurrence.getScope();
      writeLF(level);
      writeRefArray("scope", scopes);
    }
    if (occurrence.getReifier() != null) {
      writeLF(level);
      writePair("reifier", occurrence.getReifier());
    }

    if (!occurrence.getItemIdentifiers().isEmpty()) {
      writeLF(level);
      writeIdentifiers("item_identifiers", occurrence.getItemIdentifiers());
    }
    
    writeFooter();
  }

  private boolean writeIdentifiers(String key, Collection<LocatorIF> ids)
      throws IOException {
    if (!ids.isEmpty()) {
      out.write("\"" + key + "\":[");
      int cnt = 0;
      for (LocatorIF id : ids) {
        writeLocator(id);
        if (++cnt < ids.size()) {
          out.write(",");
        }
      }
      out.write("]");
      return true;
    } else {
      return false;
    }
  }
  
  private void writeLocator(LocatorIF locator) throws IOException {
    out.write("\"");
    out.write(locator.getExternalForm());
    out.write("\"");
  }
  
  private void writeDataType(LocatorIF type, int level) throws IOException {
    if (type != null && !PSI.getXSDString().equals(type)) {
      writeLF(level);
      writePair("datatype", type.getExternalForm());
    }
  }
  
  /**
   * Writes a simple json key-value pair to the output stream.
   */
  private void writePair(String key, String value) throws IOException {
    out.write("\"");
    out.write(key);
    out.write("\":\"");
    out.write(value);
    out.write("\"");
  }

  private void writePair(String key, TopicIF ref) throws IOException {
    out.write("\"");
    out.write(key);
    out.write("\":");
    writeTopicRef(ref);
  }

  private void writeTopicRef(TopicIF ref) throws IOException {
    out.write("\"");
    if (!ref.getItemIdentifiers().isEmpty()) {
      out.write("ii:");
      LocatorIF loc = (LocatorIF) ref.getItemIdentifiers().iterator().next();
      out.write(loc.getExternalForm());
    } else if (!ref.getSubjectIdentifiers().isEmpty()) {
      out.write("si:");
      LocatorIF loc = (LocatorIF) ref.getSubjectIdentifiers().iterator().next();
      out.write(loc.getExternalForm());
    } else if (!ref.getSubjectLocators().isEmpty()) {
      out.write("sl:");
      LocatorIF loc = (LocatorIF) ref.getSubjectLocators().iterator().next();
      out.write(loc.getExternalForm());
    } else {
      // TODO: throw an error
    }
    out.write("\"");
  }
  
  private void writeRefArray(String key, Collection<TopicIF> coll) throws IOException {
    out.write("\"");
    out.write(key);
    out.write("\":[");
    int cnt = 0;
    for (TopicIF ref : coll) {
      writeTopicRef(ref);
      if (++cnt < coll.size()) {
        out.write(",");
      }
    }
    out.write("]");
  }
  
  private void writeHeader(String type, boolean isStartElement, int level)
      throws IOException {
    addIndentation(level);
    out.write("{");
    if (isStartElement) {
      writePair("version", VERSION);
      out.write(",\n");
      addIndentation(level);
      out.write(" ");
      writePair("item_type", type);
      out.write(",\n");
      addIndentation(level);
      out.write(" ");
    }
  }
  
  private void writeFooter() throws IOException {
    out.write("}");
  }

  private void writeLF(int level) throws IOException {
    out.write(",\n");
    addIndentation(level);
    out.write(" ");
  }
  
  private void addIndentation(int level) throws IOException {
    if (level > 0) {
      int indentation = level * INDENT + level;
      out.write(String.format("%1$" + indentation + "s", ' '));
    }
  }
}
