
// $Id: CanonicalTopicMapWriter.java,v 1.37 2008/06/13 08:17:57 geir.gronmo Exp $

package net.ontopia.topicmaps.xml;

import java.io.*;
import java.util.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.infoset.core.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.utils.*;
import net.ontopia.xml.*;

/**
 * PUBLIC: A topic map writer that writes topic maps out to Ontopia's
 * Canonical XTM topic map format. This format is generally used for
 * testing and not for other purposes.
 *
 * <p><b>Note:</b> this is the format defined in <a
 * href="http://www.ontopia.net/topicmaps/materials/cxtm.html">an
 * Ontopia technical report</a>, not the upcoming standard format.
 * For new code, please use the standard format.
 */
public class CanonicalTopicMapWriter implements TopicMapWriterIF {

  protected DocumentHandler out;
  
  // If stream is instantiated here we'll close it when we're done.
  protected OutputStream stream;

  protected LocatorIF baseloc;

  // constants
  private AttributeListImpl empty = new AttributeListImpl();

  /**
   * Creates a topic map writer bound to the file given in the arguments.
   * @param filename The name of the file to which the topic map is to
   * be written.
   */
  
  public CanonicalTopicMapWriter(String filename) throws IOException {
    this(new File(filename));
  }

  /**
   * Creates a topic map writer bound to the file given in the arguments.
   * @param file The file object to which the topic map is to be written.
   */
  public CanonicalTopicMapWriter(File file) throws IOException {
    this.stream = new FileOutputStream(file);
    try {
      this.out = new CanonicalXTMPrinter(stream);
    } catch (UnsupportedEncodingException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  /**
   * Creates a topic map writer bound to the output stream given in
   * the arguments.
   * @param stream The output stream to which the topic map is to be
   * written.
   */
    
  public CanonicalTopicMapWriter(OutputStream stream) {
    try {
      this.out = new CanonicalXTMPrinter(stream);
    } catch (UnsupportedEncodingException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  public void write(TopicMapIF topicmap) throws IOException {
    try {
      export(topicmap, out);
      if (stream != null) stream.close();
    }
    catch (SAXException e) {
      if (e.getException() instanceof IOException)
        throw (IOException) e.getException();
      throw new IOException("XML writing problem: " + e.toString());
    }
  }

  /**
   * INTERNAL: Gets the base locator used to resolve relative locators.
   */  
  public LocatorIF getBaseLocator() {
    return baseloc;
  }

  /**
   * INTERNAL: Sets the base locator used to resolve relative locators.
   */  
  public void setBaseLocator(LocatorIF baseloc) {
    this.baseloc = baseloc;
  }

  // ===== THE EXPORT CODE =================================================

  /**
   * PUBLIC: Exports the topic map to the given DocumentHandler.
   */
  
  public void export(TopicMapIF topicmap, DocumentHandler dh)
    throws IOException, SAXException {

    dh.startDocument();

    AttributeListImpl atts = new AttributeListImpl();
    atts.addAttribute("xmlns", "CDATA",
                      "http://www.topicmaps.org/cxtm/1.0/");
    dh.startElement("topicMap", atts);
    atts.clear();

    // topics
    ContextHolder context = createContext(topicmap);
    Iterator it = context.topicsInOrder(topicmap.getTopics());
    while (it.hasNext()) 
      writeTopic((TopicIF) it.next(), dh, context);

    // associations
    it = context.assocsInOrder(topicmap.getAssociations());
    while (it.hasNext()) 
      writeAssociation((AssociationIF) it.next(), dh, context);
        
    dh.endElement("topicMap");
    dh.endDocument();
  }
    
  private ContextHolder createContext(TopicMapIF topicmap) {
    HashMap topicIds = new HashMap();
    ContextHolder context = new ContextHolder(topicIds);
        
    Iterator it = context.topicsInOrder(topicmap.getTopics());
    int counter = 1;
    while (it.hasNext())
      topicIds.put(it.next(), "id" + Integer.toString(counter++));
        
    return context;
  }

  private void writeTopic(TopicIF topic, DocumentHandler dh,
                          ContextHolder context) throws SAXException {
    AttributeListImpl atts = new AttributeListImpl();
    atts.addAttribute("id", "ID", context.getTopicId(topic));
    dh.startElement("topic", atts);
    atts.clear();

    Iterator it;
    
    // instanceOf
    if (topic.getTypes().size() != 0) {
      it = context.topicRefsInOrder(topic.getTypes());
      while (it.hasNext())
        writeInstanceOf((TopicIF) it.next(), dh, context);
    }
        
    // subjectIdentity
    if (topic.getSubjectLocators().size() >0 || 
        topic.getSubjectIdentifiers().size() > 0) {
      dh.startElement("subjectIdentity", empty);

      it = orderedIterator(topic.getSubjectLocators(),
                           new StringifierComparator(new LocatorStringifier()));
      if (it.hasNext()) // NOTE: exporting only one
        writeResourceRef((LocatorIF)it.next(), dh);

      it = orderedIterator(topic.getSubjectIdentifiers(),
                           new StringifierComparator(new LocatorStringifier()));
      while (it.hasNext()) {
        LocatorIF loc = (LocatorIF) it.next();
        atts.addAttribute("href", "CDATA", resolveRelative(loc));
        dh.startElement("subjectIndicatorRef", atts);
        atts.clear();
        dh.endElement("subjectIndicatorRef");
      }
                
      dh.endElement("subjectIdentity");
    }
        
    // baseName
    if (topic.getTopicNames().size() > 0) {
      it = context.baseNamesInOrder(topic.getTopicNames());
      while (it.hasNext())
        writeTopicName((TopicNameIF) it.next(), dh, context);
    }
        
    // occurrences
    it = orderedIterator(topic.getOccurrences(),
                         new StringifierComparator(new OccurrenceStringifier()));
    while (it.hasNext()) {
      OccurrenceIF occ = (OccurrenceIF) it.next();
      dh.startElement("occurrence", empty);
      if (occ.getType() != null)
        writeInstanceOf(occ.getType(), dh, context);
      writeScope(occ, dh, context);
      if (occ.getLocator() != null)
        writeResourceRef(occ.getLocator(), dh);
      else
        writeResourceData(occ.getValue(), dh);
      dh.endElement("occurrence");
    }
            
    dh.endElement("topic");
  }

  private void writeInstanceOf(TopicIF topic, DocumentHandler dh,
                               ContextHolder context) throws SAXException {
    AttributeListImpl atts = new AttributeListImpl();
    atts.addAttribute("href", "CDATA", "#" + context.getTopicId(topic));
    dh.startElement("instanceOf", atts);
    dh.endElement("instanceOf");
  }

  private void writeScope(ScopedIF scoped, DocumentHandler dh,
                          ContextHolder context) throws SAXException {
    if (scoped.getScope().size() > 0) {
      dh.startElement("scope", empty);
        
      Iterator it = context.topicRefsInOrder(scoped.getScope());
      while (it.hasNext())
        writeTopicRef((TopicIF) it.next(), dh, context);

      dh.endElement("scope");
    }
  }

  private void writeTopicRef(TopicIF topic, DocumentHandler dh,
                             ContextHolder context) throws SAXException {
    AttributeListImpl atts = new AttributeListImpl();
    atts.addAttribute("href", "CDATA", "#" + context.getTopicId(topic));
    dh.startElement("topicRef", atts);
    dh.endElement("topicRef");
  }
    
  private void writeResourceRef(LocatorIF loc, DocumentHandler dh)
    throws SAXException {
    AttributeListImpl atts = new AttributeListImpl();
    atts.addAttribute("href", "CDATA", resolveRelative(loc));
    dh.startElement("resourceRef", atts);
    dh.endElement("resourceRef");
  }

  private void writeResourceData(String data, DocumentHandler dh)
    throws SAXException {
    dh.startElement("resourceData", empty);
    if (data != null) {
      char[] chars = data.toCharArray();
      dh.characters(chars, 0, chars.length);
    }
    dh.endElement("resourceData");
  }
    
  private void writeTopicName(TopicNameIF basename, DocumentHandler dh,
                             ContextHolder context) throws SAXException {
    dh.startElement("baseName", empty);
    if (basename.getType() != null)
      writeInstanceOf(basename.getType(), dh, context);
    writeScope(basename, dh, context);

    dh.startElement("baseNameString", empty);
    if (basename.getValue() != null) {
      char[] chars = basename.getValue().toCharArray();
      dh.characters(chars, 0, chars.length);
    }
    dh.endElement("baseNameString");

    if (basename.getVariants().size() > 0) {
      Iterator it = context.variantsInOrder(basename.getVariants());
      while (it.hasNext()) 
        writeVariant((VariantNameIF) it.next(), dh, context);
    }
    dh.endElement("baseName");
  }

  private void writeVariant(VariantNameIF variant, DocumentHandler dh,
                            ContextHolder context) throws SAXException {
    dh.startElement("variant", empty);
    writeScope(variant, dh, context);

    dh.startElement("variantName", empty);
    if (variant.getLocator() == null) 
      writeResourceData(variant.getValue(), dh);
    else
      writeResourceRef(variant.getLocator(), dh);
    dh.endElement("variantName");    
    dh.endElement("variant");
  }

  private void writeAssociation(AssociationIF assoc, DocumentHandler dh,
                                ContextHolder context) throws SAXException {
    dh.startElement("association", empty);
    if (assoc.getType() != null)
      writeInstanceOf(assoc.getType(), dh, context);
    writeScope(assoc, dh, context);

    Iterator it = context.rolesInOrder(assoc.getRoles());
    while (it.hasNext()) {
      AssociationRoleIF role = (AssociationRoleIF) it.next();
      dh.startElement("member", empty);
      if (role.getType() != null)
        writeInstanceOf(role.getType(), dh, context);
      if (role.getPlayer() != null)
        writeTopicRef(role.getPlayer(), dh, context);
      dh.endElement("member");
    }
        
    dh.endElement("association");
  }

  // --- Utility methods

  private Iterator orderedIterator(Collection coll, Comparator comparator) {
    List list = new ArrayList(coll);
    Collections.sort(list, comparator);
    return list.iterator();
  }

  private String resolveRelative(LocatorIF locator) {
    // resolve locator relatively to base locator
    if (baseloc == null)
      return locator.getExternalForm();
    else {
      // HACK: replace this code with baseloc.resolveRelative(locator);      
      String base = baseloc.getExternalForm();
      String address = locator.getExternalForm();

      String pbase = null;
      int lix = base.lastIndexOf("/");
      if (lix > 0) pbase = base.substring(0, lix+1);

      // TODO: walk up the entire path this way

      if (address.startsWith(base))
        return address.substring(base.length());
      else if (pbase != null && address.startsWith(pbase))
        return address.substring(pbase.length());
      else
        return address;
    }
  }

  // --- Comparators

  static abstract class AbstractComparator implements Comparator {

    protected int compareObjects(Comparable obj1, Comparable obj2) {
      // Compares two objects; null values means lower ordering
      if (obj1 == null) {
        if (obj2 != null) return -1;
        return 0;
      } else {
        if (obj2 == null) return 1;
        return obj1.compareTo(obj2);
      }
    }
    
    protected int compareObjects(Object obj1, Object obj2, Comparator comparator) {
      // Compares two objects; null values means lower ordering
      if (obj1 == null) {
        if (obj2 != null) return -1;
        return 0;
      } else {
        if (obj2 == null) return 1;
        return comparator.compare(obj1, obj2);
      }
    }

    protected int compareCollections(Collection coll1, Collection coll2, Comparator comparator) {
      // Convert collections to arrays
      Object[] array1 = coll1.toArray();
      Object[] array2 = coll2.toArray();
      // Sort the arrays
      Arrays.sort(array1, comparator);
      Arrays.sort(array2, comparator);
      // Compare individual items
      int length = (array1.length < array2.length ? array1.length : array2.length);
      for (int i=0; i < length; i++) {
        int cval = comparator.compare(array1[i], array2[i]);
        if (cval != 0) return cval;
      }
      // Compare array sizes
      if (array1.length > array2.length)
        return 1;
      else if (array1.length < array2.length)
        return -1;
      else
        return 0;
    }
  }
  
  static class TopicComparator extends AbstractComparator {
    protected static TopicComparator instance;
    public static TopicComparator getInstance() {
      if (instance == null) instance = new TopicComparator();
      return instance;      
    }    
    public int compare(Object obj1, Object obj2) {
      if (obj1 == obj2) return 0;
      TopicIF topic1 = (TopicIF)obj1;
      TopicIF topic2 = (TopicIF)obj2;

      // Compare the subject
      int cval0 = compareCollections(topic1.getSubjectLocators(), topic2.getSubjectLocators(),
                                     LocatorComparator.getInstance());
      if (cval0 != 0) return cval0;

      // Compare subject indicators
      int cval1 = compareCollections(topic1.getSubjectIdentifiers(), topic2.getSubjectIdentifiers(),
                                     LocatorComparator.getInstance());
      if (cval1 != 0) return cval1;

      // Compare basenames
      int cval2 = compareCollections(topic1.getTopicNames(), topic2.getTopicNames(),
                                     TopicNameComparator.getInstance());
      if (cval2 != 0) return cval2;

      // Compare occurrences
      int cval3 = compareCollections(topic1.getOccurrences(), topic2.getOccurrences(),
                                     OccurrenceComparator.getInstance());
      if (cval3 != 0) return cval3;

      // Compare types
      int cval4 = compareCollections(topic1.getTypes(), topic2.getTypes(),
                                     TopicComparator.getInstance());
      if (cval4 != 0) return cval4;

      // Compare source locators
      int cval5 = compareCollections(topic1.getItemIdentifiers(), topic2.getItemIdentifiers(),
                                     LocatorComparator.getInstance());
      if (cval5 != 0) return cval5;
      
      // Compare object ids
      return topic1.getObjectId().compareTo(topic2.getObjectId());
    }
  }

  static class LocatorComparator extends AbstractComparator {
    protected static LocatorComparator instance;
    public static LocatorComparator getInstance() {
      if (instance == null) instance = new LocatorComparator();
      return instance;      
    }
    public int compare(Object obj1, Object obj2) {
      if (obj1 == obj2) return 0;
      LocatorIF loc1 = (LocatorIF)obj1;
      LocatorIF loc2 = (LocatorIF)obj2;

      // Compare address
      int c_address = loc1.getExternalForm().compareTo(loc2.getExternalForm());
      if (c_address != 0) return c_address;

      // Compare notation
      return loc1.getNotation().compareTo(loc2.getNotation());
    }
  }
  
  static class TopicNameComparator extends AbstractComparator {
    protected static TopicNameComparator instance;
    public static TopicNameComparator getInstance() {
      if (instance == null) instance = new TopicNameComparator();
      return instance;      
    }
    public int compare(Object obj1, Object obj2) {
      if (obj1 == obj2) return 0;
      TopicNameIF bn1 = (TopicNameIF)obj1;
      TopicNameIF bn2 = (TopicNameIF)obj2;
      
      // Compare basename values
      int cval1 = compareObjects(bn1.getValue(), bn2.getValue());
      if (cval1 != 0) return cval1;
      
      // Compare scope
      int cval2 = compareCollections(bn1.getScope(), bn2.getScope(),
                                     TopicComparator.getInstance());
      if (cval2 != 0) return cval2;
      
      // Compare variant names
      return compareCollections(bn1.getVariants(), bn2.getVariants(),
                                VariantNameComparator.getInstance());
    }
  }
  
  static class VariantNameComparator extends AbstractComparator {
    protected static VariantNameComparator instance;
    public static VariantNameComparator getInstance() {
      if (instance == null) instance = new VariantNameComparator();
      return instance;      
    }
    public int compare(Object obj1, Object obj2) {
      if (obj1 == obj2) return 0;
      VariantNameIF vn1 = (VariantNameIF)obj1;
      VariantNameIF vn2 = (VariantNameIF)obj2;
      
      // Compare variant name values
      int cval1 = compareObjects(vn1.getValue(), vn2.getValue());
      if (cval1 != 0) return cval1;
      
      // Compare variant name locators
      int cval2 = compareObjects(vn1.getLocator(), vn2.getLocator(),
                                 LocatorComparator.getInstance());
      if (cval2 != 0) return cval2;
      
      // Compare scope
      return compareCollections(vn1.getScope(), vn2.getScope(),
                                TopicComparator.getInstance());
    }
  }
  
  static class OccurrenceComparator extends AbstractComparator {
    protected static OccurrenceComparator instance;
    public static OccurrenceComparator getInstance() {
      if (instance == null) instance = new OccurrenceComparator();
      return instance;      
    }
    public int compare(Object obj1, Object obj2) {
      if (obj1 == obj2) return 0;
      OccurrenceIF occ1 = (OccurrenceIF)obj1;
      OccurrenceIF occ2 = (OccurrenceIF)obj2;
      
      // Compare occurrence values
      int cval1 = compareObjects(occ1.getValue(), occ2.getValue());
      if (cval1 != 0) return cval1;
      
      // Compare occurrence locators
      int cval2 = compareObjects(occ1.getLocator(), occ2.getLocator(),
                                 LocatorComparator.getInstance());
      if (cval2 != 0) return cval2;
      
      // Compare type
      int cval3 = compareObjects(occ1.getType(), occ2.getType(),
                                 TopicComparator.getInstance());
      if (cval3 != 0) return cval3;
      
      // Compare scope
      return compareCollections(occ1.getScope(), occ2.getScope(),
                                TopicComparator.getInstance());
    }
  }
  
  static class AssociationComparator extends AbstractComparator {
    protected static AssociationComparator instance;
    public static AssociationComparator getInstance() {
      if (instance == null) instance = new AssociationComparator();
      return instance;      
    }
    public int compare(Object obj1, Object obj2) {
      if (obj1 == obj2) return 0;
      AssociationIF assoc1 = (AssociationIF)obj1;
      AssociationIF assoc2 = (AssociationIF)obj2;
      
      // Compare type
      int cval1 = compareObjects(assoc1.getType(), assoc2.getType(),
                                 TopicComparator.getInstance());
      if (cval1 != 0) return cval1;
      
      // Compare scope
      int cval2 = compareCollections(assoc1.getScope(), assoc2.getScope(),
                                     TopicComparator.getInstance());
      if (cval2 != 0) return cval2;
      
      // Compare roles
      return compareCollections(assoc1.getRoles(), assoc2.getRoles(),
                                AssociationRoleComparator.getInstance());
    }
  }
  
  static class AssociationRoleComparator extends AbstractComparator {
    protected static AssociationRoleComparator instance;
    public static AssociationRoleComparator getInstance() {
      if (instance == null) instance = new AssociationRoleComparator();
      return instance;      
    }
    public int compare(Object obj1, Object obj2) {
      if (obj1 == obj2) return 0;
      AssociationRoleIF role1 = (AssociationRoleIF)obj1;
      AssociationRoleIF role2 = (AssociationRoleIF)obj2;
      
      // Compare types
      int cval2 = compareObjects(role1.getType(), role2.getType(),
                                 TopicComparator.getInstance());
      if (cval2 != 0) return cval2;
      
      // Compare players
      return compareObjects(role1.getPlayer(), role2.getPlayer(),
                            TopicComparator.getInstance());
    }
  }
  
  // --- Sort key stringifiers

  class TopicRefStringifier implements StringifierIF {
    private Map topicIds;
        
    public TopicRefStringifier(Map topicIds) {
      this.topicIds = topicIds;
    }
                              
    public String toString(Object topic) {
      return (String) topicIds.get(topic);
    }
  }
    
  // --- Other utilities

  class ContextHolder {
    private Map topicIds;
    private Comparator topicComparator;
    private Comparator topicRefComparator;
    private Comparator baseNameComparator;
    private Comparator variantNameComparator;
    private Comparator assocComparator;
    private Comparator roleComparator;

    public ContextHolder(Map topicIds) {
      this.topicIds = topicIds;
      topicRefComparator = new StringifierComparator(new TopicRefStringifier(topicIds));
      
      topicComparator = TopicComparator.getInstance();
      baseNameComparator = TopicNameComparator.getInstance();
      variantNameComparator = VariantNameComparator.getInstance();
      
      assocComparator = AssociationComparator.getInstance();
      roleComparator = AssociationRoleComparator.getInstance();
    }

    public String getTopicId(TopicIF topic) {
      return (String) topicIds.get(topic);
    }

    public Iterator topicsInOrder(Collection topics) {
      return orderedIterator(topics, topicComparator);
    }

    public Iterator topicRefsInOrder(Collection topics) {
      return orderedIterator(topics, topicRefComparator);
    }

    public Iterator variantsInOrder(Collection variants) {
      return orderedIterator(variants, variantNameComparator);
    }

    public Iterator baseNamesInOrder(Collection basenames) {
      return orderedIterator(basenames, baseNameComparator);
    }

    public Iterator assocsInOrder(Collection assocs) {
      return orderedIterator(assocs, assocComparator);
    }

    public Iterator rolesInOrder(Collection roles) {
      return orderedIterator(roles, roleComparator);
    }
  }
    
  class TopicRefComparator implements Comparator {
    private Map topicIds;
        
    public TopicRefComparator(Map topicIds) {
      this.topicIds = topicIds;
    }

    public int compare(Object o1, Object o2) {
      String s1 = (String) topicIds.get(o1);
      String s2 = (String) topicIds.get(o2);
      return s1.compareTo(s2);
    }
  }

  class FirstGrabber implements GrabberIF {
    private Comparator comparator;
        
    public FirstGrabber(Comparator comparator) {
      this.comparator = comparator;
    }
            
    public Object grab(Object coll) {
      return orderedIterator((Collection) coll, comparator).next();
    }
  }

  class StringComparator implements Comparator {
    public int compare(Object o1, Object o2) {
      String s1 = (String) o1;
      String s2 = (String) o2;
      return s1.compareTo(s2);
    }
  }
    
  class LocatorStringifier implements StringifierIF {
    public String toString(Object obj) {
      LocatorIF loc = (LocatorIF) obj;
      return loc.getExternalForm();
    }
  }

  class OccurrenceStringifier implements StringifierIF {
    public String toString(Object obj) {
      OccurrenceIF occ = (OccurrenceIF) obj;
      if (occ.getLocator() != null)
        return occ.getLocator().getExternalForm();
      else
        return "$" + occ.getValue();
    }
  }

  // --- XML writer

  public class CanonicalXTMPrinter extends CanonicalPrinter {
  
    public CanonicalXTMPrinter(OutputStream stream) throws UnsupportedEncodingException {
      super(stream);
    }

    public void startElement(String name, AttributeList atts) {
      super.startElement(name, atts);
      if (name != "baseNameString" && 
          name != "resourceData" && name != "topicRef" &&
          name != "instanceOf" && name != "resourceRef" &&
          name != "subjectIndicatorRef")
        writer.print("\n");
    }

    public void endElement(String name) {
      super.endElement(name);
      writer.print("\n");
    }
    
  }
}
