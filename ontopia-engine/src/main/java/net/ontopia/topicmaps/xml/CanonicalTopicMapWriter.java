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

package net.ontopia.topicmaps.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapWriterIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.utils.GrabberIF;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.StringifierComparator;
import net.ontopia.utils.StringifierIF;
import net.ontopia.xml.CanonicalPrinter;
import org.xml.sax.AttributeList;
import org.xml.sax.DocumentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributeListImpl;

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
    Iterator<TopicIF> it = context.topicsInOrder(topicmap.getTopics());
    while (it.hasNext()) 
      writeTopic(it.next(), dh, context);

    // associations
    Iterator<AssociationIF> ait = context.assocsInOrder(topicmap.getAssociations());
    while (ait.hasNext()) 
      writeAssociation(ait.next(), dh, context);
        
    dh.endElement("topicMap");
    dh.endDocument();
  }
    
  private ContextHolder createContext(TopicMapIF topicmap) {
    HashMap<TopicIF, String> topicIds = new HashMap<TopicIF, String>();
    ContextHolder context = new ContextHolder(topicIds);
        
    Iterator<TopicIF> it = context.topicsInOrder(topicmap.getTopics());
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

    // instanceOf
    if (topic.getTypes().size() != 0) {
      Iterator<TopicIF> it = context.topicRefsInOrder(topic.getTypes());
      while (it.hasNext())
        writeInstanceOf(it.next(), dh, context);
    }
        
    // subjectIdentity
    if (topic.getSubjectLocators().size() > 0 || 
        topic.getSubjectIdentifiers().size() > 0) {
      dh.startElement("subjectIdentity", empty);

      Iterator<LocatorIF> it = orderedIterator(topic.getSubjectLocators(),
                           new StringifierComparator<LocatorIF>(new LocatorStringifier()));
      while (it.hasNext())
        writeResourceRef(it.next(), dh);

      it = orderedIterator(topic.getSubjectIdentifiers(),
                           new StringifierComparator<LocatorIF>(new LocatorStringifier()));
      while (it.hasNext()) {
        LocatorIF loc = it.next();
        atts.addAttribute("href", "CDATA", resolveRelative(loc));
        dh.startElement("subjectIndicatorRef", atts);
        atts.clear();
        dh.endElement("subjectIndicatorRef");
      }
                
      dh.endElement("subjectIdentity");
    }
        
    // baseName
    if (topic.getTopicNames().size() > 0) {
      Iterator<TopicNameIF> it = context.baseNamesInOrder(topic.getTopicNames());
      while (it.hasNext())
        writeTopicName(it.next(), dh, context);
    }
        
    // occurrences
    Iterator<OccurrenceIF> it = orderedIterator(topic.getOccurrences(),
                         new StringifierComparator<OccurrenceIF>(new OccurrenceStringifier()));
    while (it.hasNext()) {
      OccurrenceIF occ = it.next();
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
        
      Iterator<TopicIF> it = context.topicRefsInOrder(scoped.getScope());
      while (it.hasNext())
        writeTopicRef(it.next(), dh, context);

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
      Iterator<VariantNameIF> it = context.variantsInOrder(basename.getVariants());
      while (it.hasNext()) 
        writeVariant(it.next(), dh, context);
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

    Iterator<AssociationRoleIF> it = context.rolesInOrder(assoc.getRoles());
    while (it.hasNext()) {
      AssociationRoleIF role = it.next();
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

  private <T> Iterator<T> orderedIterator(Collection<T> coll, Comparator<? super T> comparator) {
    List<T> list = new ArrayList<T>(coll);
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

  /**
   * CanonicalTopicMapWriter has no additional properties.
   * @param properties 
   */
  public void setAdditionalProperties(Map<String, Object> properties) {
    // no-op
  }

  // --- Comparators

  static abstract class AbstractComparator<T> implements Comparator<T> {

    protected <O> int compareObjects(Comparable<O> obj1, O obj2) {
      // Compares two objects; null values means lower ordering
      if (obj1 == null) {
        if (obj2 != null) return -1;
        return 0;
      } else {
        if (obj2 == null) return 1;
        return obj1.compareTo(obj2);
      }
    }
    
    protected <O> int compareObjects(O obj1, O obj2, Comparator<? super O> comparator) {
      // Compares two objects; null values means lower ordering
      if (obj1 == null) {
        if (obj2 != null) return -1;
        return 0;
      } else {
        if (obj2 == null) return 1;
        return comparator.compare(obj1, obj2);
      }
    }

    @SuppressWarnings("unchecked")
    protected <O> int compareCollections(Collection<O> coll1, Collection<O> coll2, Comparator<? super O> comparator) {
      // Convert collections to arrays
      Object[] array1 = coll1.toArray();
      Object[] array2 = coll2.toArray();
      // Sort the arrays
      Arrays.sort((O[])array1, comparator);
      Arrays.sort((O[])array2, comparator);
      // Compare individual items
      int length = (array1.length < array2.length ? array1.length : array2.length);
      for (int i=0; i < length; i++) {
        int cval = comparator.compare((O)array1[i], (O)array2[i]);
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
  
  static class TopicComparator extends AbstractComparator<TopicIF> {
    protected static TopicComparator instance;
    public static TopicComparator getInstance() {
      if (instance == null) instance = new TopicComparator();
      return instance;      
    }    
    public int compare(TopicIF topic1, TopicIF topic2) {
      if (topic1 == topic2) return 0;

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

  static class LocatorComparator extends AbstractComparator<LocatorIF> {
    protected static LocatorComparator instance;
    public static LocatorComparator getInstance() {
      if (instance == null) instance = new LocatorComparator();
      return instance;      
    }
    public int compare(LocatorIF loc1, LocatorIF loc2) {
      if (loc1 == loc2) return 0;

      // Compare address
      int c_address = loc1.getExternalForm().compareTo(loc2.getExternalForm());
      if (c_address != 0) return c_address;

      // Compare notation
      return loc1.getNotation().compareTo(loc2.getNotation());
    }
  }
  
  static class TopicNameComparator extends AbstractComparator<TopicNameIF> {
    protected static TopicNameComparator instance;
    public static TopicNameComparator getInstance() {
      if (instance == null) instance = new TopicNameComparator();
      return instance;      
    }
    public int compare(TopicNameIF bn1, TopicNameIF bn2) {
      if (bn1 == bn2) return 0;
      
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
  
  static class VariantNameComparator extends AbstractComparator<VariantNameIF> {
    protected static VariantNameComparator instance;
    public static VariantNameComparator getInstance() {
      if (instance == null) instance = new VariantNameComparator();
      return instance;      
    }
    public int compare(VariantNameIF vn1, VariantNameIF vn2) {
      if (vn1 == vn2) return 0;
      
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
  
  static class OccurrenceComparator extends AbstractComparator<OccurrenceIF> {
    protected static OccurrenceComparator instance;
    public static OccurrenceComparator getInstance() {
      if (instance == null) instance = new OccurrenceComparator();
      return instance;      
    }
    public int compare(OccurrenceIF occ1, OccurrenceIF occ2) {
      if (occ1 == occ2) return 0;
      
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
  
  static class AssociationComparator extends AbstractComparator<AssociationIF> {
    protected static AssociationComparator instance;
    public static AssociationComparator getInstance() {
      if (instance == null) instance = new AssociationComparator();
      return instance;      
    }
    public int compare(AssociationIF assoc1, AssociationIF assoc2) {
      if (assoc1 == assoc2) return 0;
      
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
  
  static class AssociationRoleComparator extends AbstractComparator<AssociationRoleIF> {
    protected static AssociationRoleComparator instance;
    public static AssociationRoleComparator getInstance() {
      if (instance == null) instance = new AssociationRoleComparator();
      return instance;      
    }
    public int compare(AssociationRoleIF role1, AssociationRoleIF role2) {
      if (role1 == role2) return 0;
      
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

  class TopicRefStringifier implements StringifierIF<TopicIF> {
    private Map<TopicIF, String> topicIds;
        
    public TopicRefStringifier(Map<TopicIF, String> topicIds) {
      this.topicIds = topicIds;
    }
                              
    public String toString(TopicIF topic) {
      return topicIds.get(topic);
    }
  }
    
  // --- Other utilities

  class ContextHolder {
    private Map<TopicIF, String> topicIds;
    private Comparator<TopicIF> topicComparator;
    private Comparator<TopicIF> topicRefComparator;
    private Comparator<TopicNameIF> baseNameComparator;
    private Comparator<VariantNameIF> variantNameComparator;
    private Comparator<AssociationIF> assocComparator;
    private Comparator<AssociationRoleIF> roleComparator;

    public ContextHolder(Map<TopicIF, String> topicIds) {
      this.topicIds = topicIds;
      topicRefComparator = new StringifierComparator<TopicIF>(new TopicRefStringifier(topicIds));
      
      topicComparator = TopicComparator.getInstance();
      baseNameComparator = TopicNameComparator.getInstance();
      variantNameComparator = VariantNameComparator.getInstance();
      
      assocComparator = AssociationComparator.getInstance();
      roleComparator = AssociationRoleComparator.getInstance();
    }

    public String getTopicId(TopicIF topic) {
      return topicIds.get(topic);
    }

    public Iterator<TopicIF> topicsInOrder(Collection<TopicIF> topics) {
      return orderedIterator(topics, topicComparator);
    }

    public Iterator<TopicIF> topicRefsInOrder(Collection<TopicIF> topics) {
      return orderedIterator(topics, topicRefComparator);
    }

    public Iterator<VariantNameIF> variantsInOrder(Collection<VariantNameIF> variants) {
      return orderedIterator(variants, variantNameComparator);
    }

    public Iterator<TopicNameIF> baseNamesInOrder(Collection<TopicNameIF> basenames) {
      return orderedIterator(basenames, baseNameComparator);
    }

    public Iterator<AssociationIF> assocsInOrder(Collection<AssociationIF> assocs) {
      return orderedIterator(assocs, assocComparator);
    }

    public Iterator<AssociationRoleIF> rolesInOrder(Collection<AssociationRoleIF> roles) {
      return orderedIterator(roles, roleComparator);
    }
  }
    
  class TopicRefComparator implements Comparator<TopicIF> {
    private Map<TopicIF, String> topicIds;
        
    public TopicRefComparator(Map<TopicIF, String> topicIds) {
      this.topicIds = topicIds;
    }

    public int compare(TopicIF o1, TopicIF o2) {
      String s1 = topicIds.get(o1);
      String s2 = topicIds.get(o2);
      return s1.compareTo(s2);
    }
  }

  class FirstGrabber<T> implements GrabberIF<Collection<T>, T> {
    private Comparator<? super T> comparator;
        
    public FirstGrabber(Comparator<? super T> comparator) {
      this.comparator = comparator;
    }
            
    public T grab(Collection<T> coll) {
      return orderedIterator(coll, comparator).next();
    }
  }

  class StringComparator implements Comparator<String> {
    public int compare(String s1, String s2) {
      return s1.compareTo(s2);
    }
  }
    
  class LocatorStringifier implements StringifierIF<LocatorIF> {
    public String toString(LocatorIF loc) {
      return loc.getExternalForm();
    }
  }

  class OccurrenceStringifier implements StringifierIF<OccurrenceIF> {
    public String toString(OccurrenceIF occ) {
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
