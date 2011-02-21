
package net.ontopia.topicmaps.xml;

import java.io.Reader;
import java.io.Writer;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.xml.sax.AttributeList;
import org.xml.sax.helpers.AttributeListImpl;

import net.ontopia.xml.CanonicalPrinter;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.utils.DuplicateSuppressionUtils;
import net.ontopia.topicmaps.utils.PSI;
import net.ontopia.utils.IteratorComparator;
import net.ontopia.utils.ObjectUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.CompactHashSet;

/**
 * PUBLIC: A topic map writer that writes topic maps out to the format
 * defined in ISO 13250-4: Topic Maps -- Canonicalization.  The format
 * is also known as Canonical XTM, but should not be confused with
 * that defined by Ontopia. The current implementation conforms to the
 * final standard (ISO 13250-4:2009).
 *
 * @since 2.0.3
 */
public class CanonicalXTMWriter implements TopicMapWriterIF {
  private CanonicalPrinter out;
  private AttributeListImpl EMPTY;
  private Map tmIndex; // Maps TMObjectIFs to corresponding index within parent
  private Map extraRoles; // TopicIF -> List<AssocRoleIFs for type-instance>
  private String base;
  private String strippedBase;

  private TopicIF typeInstance; // possibly fake
  private TopicIF instance; // possibly fake
  private TopicIF type; // possibly fake
  private static TopicMapIF tmForFake; 
          // should only be used by fake nested classes.

  private final AssociationComparator associationComparator = 
          new AssociationComparator();
  private final AssociationRoleComparator associationRoleComparator = 
          new AssociationRoleComparator();
  private final NameComparator nameComparator = 
          new NameComparator();
  private final OccurrenceComparator occurrenceComparator = 
          new OccurrenceComparator();
  private final LocatorComparator locatorComparator = 
          new LocatorComparator();
  private final TopicComparator topicComparator = new TopicComparator();
  private final VariantComparator variantComparator = 
          new VariantComparator();

  private Comparator indexComparator;
  private Set startNewlineElem;
  
  private static final char[] LINEBREAK = { (char) 0x0A };

  public CanonicalXTMWriter(OutputStream out)
    throws UnsupportedEncodingException {
    this.out = new CanonicalPrinter(out);
    init();
  }

  /**
   * PUBLIC: Creates a canonicalizer that writes to the given Writer
   * in whatever encoding that Writer uses. <b>Warning:</b> Canonical
   * XTM requires the output encoding to be UTF-8, so for correct
   * results the given Writer <i>must</i> produce UTF-8. Using this
   * method is <b>not</b> recommended.
   */
  public CanonicalXTMWriter(Writer out) {
    this.out = new CanonicalPrinter(out);
    init();
  }

  private void init() {
    this.EMPTY = new AttributeListImpl();
    this.startNewlineElem = new CompactHashSet(12);
    this.extraRoles = new HashMap();
    startNewlineElem.add("topicMap");
    startNewlineElem.add("topic");
    startNewlineElem.add("name");
    startNewlineElem.add("variant");
    startNewlineElem.add("occurrence");
    startNewlineElem.add("association");
    startNewlineElem.add("role");
    startNewlineElem.add("scope");
    startNewlineElem.add("itemIdentifiers");
    startNewlineElem.add("subjectIdentifiers");
    startNewlineElem.add("subjectLocators");
  }
  
  public void write(TopicMapIF topicmap) {
    DuplicateSuppressionUtils.removeDuplicates(topicmap);
    tmForFake = topicmap;
  
    base = topicmap.getStore().getBaseAddress().getAddress();
    strippedBase = stripLocator(base);

    Object[] topics = getTopics(topicmap);
    Object[] associations = getAssociations(topicmap);
    recordIndexes(topics, associations);
        
    out.startDocument();
    startElement("topicMap", reifier(topicmap));
    writeLocators(topicmap.getItemIdentifiers(), "itemIdentifiers");
    
    for (int ix = 0; ix < topics.length; ix++)
      write((TopicIF) topics[ix]);

    for (int ix = 0; ix < associations.length; ix++)
      write((AssociationIF) associations[ix], ix + 1);

    endElement("topicMap");
    out.endDocument();
  }
 
  /**
   * Maps topics, topic names, variant names, occurrences, associations and
   * association roles to an index value (given as a string).
   * Index value is the canonically ordered position in the parent object.
   * @param topics The topic (with names and occurrences) make indexes for.
   * @param associations (with roles) to make indexes for.
   * Post: the paramaters 'topics' and 'associations are in canonical order.
   */
  private void recordIndexes(Object[] topics, Object[] associations) {
    // Create necessary objects
    tmIndex = new HashMap();
    indexComparator = new IndexComparator(tmIndex);
    
    // Sort the topics in canonical order.
    Arrays.sort(topics, topicComparator);
    
    // Map each topic to its canonical position within the topic map.
    for (int i = 0; i < topics.length; i++)
      tmIndex.put(topics[i], new Integer(i + 1));

    // Sort associations in canonical order
    Arrays.sort(associations, associationComparator);

    // For each association (in canonical order) of the topic map
    for (int i = 0; i < associations.length; i++) {
      AssociationIF assoc = (AssociationIF) associations[i];
      // Map the association to it's position within the topic map.
      tmIndex.put(assoc, new Integer(i + 1));
      
      Object roles[] = assoc.getRoles().toArray();
      Arrays.sort(roles, associationRoleComparator);
      
      // For each association role (in canonical order) of the association
      for (int j = 0; j < roles.length; j++)
        // Map the role to it's position within the association.
        tmIndex.put(roles[j], new Integer(j + 1));
    }    
  }

  private void write(TopicIF topic) {
    AttributeListImpl attributes = new AttributeListImpl();
    attributes.addAttribute("number", null, "" + tmIndex.get(topic));
    
    startElement("topic", attributes);
    attributes.clear();
    writeLocators(topic.getSubjectIdentifiers(), "subjectIdentifiers");
    writeLocators(topic.getSubjectLocators(), "subjectLocators");
    writeLocators(topic.getItemIdentifiers(), "itemIdentifiers");

    Object[] names = topic.getTopicNames().toArray();
    Arrays.sort(names, nameComparator);
    for (int ix = 0; ix < names.length; ix++)
      write((TopicNameIF) names[ix], ix + 1);

    Object[] occurrences = makeFakes(topic.getOccurrences().toArray());
    Arrays.sort(occurrences, occurrenceComparator);
    for (int ix = 0; ix < occurrences.length; ix++)
      write((OccurrenceIF) occurrences[ix], ix + 1);

    Collection r = new ArrayList(topic.getRoles());
    Collection extras = (Collection) extraRoles.get(topic);
    if (extras != null)
      r.addAll(extras);
    Object[] roles = r.toArray();
    Arrays.sort(roles, associationRoleComparator);
    for (int ix = 0; ix < roles.length; ix++) {
      AssociationRoleIF currentRole = (AssociationRoleIF)roles[ix];
      AssociationIF currentAssociation = currentRole.getAssociation();
      AttributeListImpl roleAttributes = new AttributeListImpl();
      String refValue = "association." 
              + tmIndex.get(currentAssociation)
              + ".role."
              + tmIndex.get(currentRole);
      roleAttributes.addAttribute("ref", null, refValue);
      startElement("rolePlayed", roleAttributes);
      endElement("rolePlayed");
    }
    
    endElement("topic");
  }

  private void write(TopicNameIF basename, int number) {
    AttributeListImpl attributes = reifier(basename);
    attributes.addAttribute("number", null, "" + number);
    
    startElement("name", attributes);
    attributes.clear();
    write(basename.getValue());
    writeType(basename);
    write(basename.getScope());

    Object[] variants = basename.getVariants().toArray();
    Arrays.sort(variants, variantComparator);
    for (int ix = 0; ix < variants.length; ix++)
      write((VariantNameIF) variants[ix], ix + 1);

    writeLocators(basename.getItemIdentifiers(), "itemIdentifiers");

    endElement("name");
  }
  
  private void write(VariantNameIF variant, int number) {
    AttributeListImpl attributes = reifier(variant);
    attributes.addAttribute("number", null, "" + number);
    
    startElement("variant", attributes);
    attributes.clear();

    if (ObjectUtils.equals(variant.getDataType(), DataTypes.TYPE_URI)) {
      LocatorIF locator = variant.getLocator();
      if (locator != null)
        write(normaliseLocatorReference(locator.getAddress()));
    } else {
      String value = variant.getValue();
      if (value != null)
        write(value);
    }
    write(variant.getDataType(), "datatype");
    write(variant.getScope());
    writeLocators(variant.getItemIdentifiers(), "itemIdentifiers");
    
    endElement("variant");
  }

  private Object[] makeFakes(Object[] occs) {
    for (int ix = 0; ix < occs.length; ix++) {
      OccurrenceIF original = (OccurrenceIF) occs[ix];
      occs[ix] = new FakeOccurrence(original);
    }
    return occs;
  }
  
  private void write(OccurrenceIF occurrence, int number) {
    AttributeListImpl attributes = reifier(occurrence);
    attributes.addAttribute("number", null, "" + number);
    
    startElement("occurrence", attributes);
    attributes.clear();

    write(occurrence.getValue()); // normalized in FakeOccurrence below
    write(occurrence.getDataType(), "datatype");
    writeType(occurrence);
    write(occurrence.getScope());
    writeLocators(occurrence.getItemIdentifiers(), "itemIdentifiers");

    endElement("occurrence");
  }

  private void write(AssociationIF association, int number) {
    AttributeListImpl attributes = reifier(association);
    attributes.addAttribute("number", null, "" + number);
    
    startElement("association", attributes);
    attributes.clear();
    writeType(association);
    
    Object[] roles = association.getRoles().toArray();
    Arrays.sort(roles, associationRoleComparator);
    for (int ix = 0; ix < roles.length; ix++)
      write((AssociationRoleIF) roles[ix], ix + 1);

    write(association.getScope());
    writeLocators(association.getItemIdentifiers(), "itemIdentifiers");

    endElement("association");
  }

  private void write(AssociationRoleIF role, int number) {
    AttributeListImpl attributes = reifier(role);
    attributes.addAttribute("number", null, "" + number);
    
    startElement("role", attributes);
    attributes.clear();

    startElement("player", topicRef(role.getPlayer()));
    endElement("player");

    writeType(role);
    writeLocators(role.getItemIdentifiers(), "itemIdentifiers");

    endElement("role");
  }

  private void write(Collection scope) {
    if (scope.isEmpty())
      return;

    startElement("scope", EMPTY);
    Object[] topics = scope.toArray();
    Arrays.sort(topics, indexComparator);
    for (int ix = 0; ix < topics.length; ix++) {
      startElement("scopingTopic", topicRef((TopicIF) topics[ix]));
      endElement("scopingTopic");
    }

    endElement("scope");
  }

  private void writeType(TypedIF object) {
    TopicIF topic = object.getType();
    if (topic == null) {
      throw new OntopiaRuntimeException("TypedIF had null type: " + object);
    }
    startElement("type", topicRef(topic));
    endElement("type");
  }

  private void write(String value) {
    if (value == null)
      throw new OntopiaRuntimeException("Object had null value");
    startElement("value", EMPTY);
    out.characters(value.toCharArray(), 0, value.length());
    endElement("value");
  }

  private void write(LocatorIF uri, String element) {
    startElement(element, EMPTY);
    String value = uri.getAddress();
    out.characters(value.toCharArray(), 0, value.length());
    endElement(element);
  }

  private void write(LocatorIF locator) {
    String address = normaliseLocatorReference(locator.getAddress());
    startElement("locator", EMPTY);
    out.characters(address.toCharArray(), 0, address.length());
    endElement("locator");
  }

  private void writeLocators(Collection locators, String elementName) {
    Object locs[] = locators.toArray();
    Arrays.sort(locs, locatorComparator);
    
    if (locs.length > 0) {
      startElement(elementName, EMPTY);
    
      for (int i = 0; i < locs.length; i++) {
        LocatorIF loc = (LocatorIF) locs[i];
        write(loc);
      }
    
      endElement(elementName);
    }
  }

  // --- XML handling
   
  private void startElement(String element, AttributeList atts) {
    out.startElement(element, atts);
    if (startNewlineElem.contains(element))
      writeln();
  }

  private void endElement(String element) {
    out.endElement(element);
    writeln();
  }

  private void writeln() {
    out.characters(LINEBREAK, 0, 1);
  }
  
  // --- Helpers

  private AttributeListImpl reifier(ReifiableIF reified) {
    TopicIF reifier = reified.getReifier();
    if (reifier == null)
      return EMPTY;

    AttributeListImpl atts = new AttributeListImpl();
    atts.addAttribute("reifier", null,
        String.valueOf(tmIndex.get(reifier)));
    return atts;
  }

  /**
   * @return an attribute list with a reference to a given topic.
   */
  private AttributeList topicRef(TopicIF topic) {
    AttributeListImpl atts = new AttributeListImpl();
    atts.addAttribute("topicref", null, "" + tmIndex.get(topic));
    return atts;
  }

  /**
   * @return an array with all the topics of a given topic map.
   */
  private Object[] getTopics(TopicMapIF topicmap) {
    Collection topics = new ArrayList(topicmap.getTopics().size() + 4);
    topics.addAll(topicmap.getTopics());

    // add the type-instance PSI topics, if necessary
    ClassInstanceIndexIF index = (ClassInstanceIndexIF) topicmap
            .getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");
    if (!index.getTopicTypes().isEmpty()) {
      typeInstance = getTopic(topicmap, PSI.getSAMTypeInstance(), topics);
      instance = getTopic(topicmap, PSI.getSAMInstance(), topics);
      type = getTopic(topicmap, PSI.getSAMType(), topics);
    }

    return topics.toArray();
  }

  /**
   * @return an array with all the associations a given topic map.
   */
  private Object[] getAssociations(TopicMapIF topicmap) {
    ClassInstanceIndexIF index = (ClassInstanceIndexIF) topicmap
            .getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");
    if (index.getTopicTypes().isEmpty())
      return topicmap.getAssociations().toArray();

    Collection assocs = new ArrayList(topicmap.getAssociations());
    Iterator it = index.getTopicTypes().iterator();
    while (it.hasNext()) {
      TopicIF thetype = (TopicIF) it.next();
      Iterator it2 = index.getTopics(thetype).iterator();
      while (it2.hasNext()) {
        TopicIF theinstance = (TopicIF) it2.next();
        AssociationIF assoc = new FakeAssociation(thetype, theinstance);
        recordRole(thetype, assoc.getRolesByType(type));
        recordRole(theinstance, assoc.getRolesByType(instance));
        assocs.add(assoc);
      }
    }

    return assocs.toArray();
  }

  private void recordRole(TopicIF topic, Collection roles) {
    Collection extra = (Collection) extraRoles.get(topic);
    if (extra == null) {
      extra = new ArrayList();
      extraRoles.put(topic, extra);
    }
    extra.addAll(roles);
  }

  /**
   * Return the topic with a given PSI. Create a fake topic for it if
   * does not exist, and add it to the topics collection.
   */
  private TopicIF getTopic(TopicMapIF tm, LocatorIF indicator, 
                           Collection topics) {
    TopicIF topic = tm.getTopicBySubjectIdentifier(indicator);
    if (topic == null) {
      topic = new FakeTopic(indicator, tmForFake);
      topics.add(topic);
    }
    return topic;
  }

  // --- Datatype normalisation

  private String normalizeNumber(String number) {
    if (number.indexOf('.') > -1)
      return normalizeDecimal(number);
    else
      return normalizeInteger(number);
   }
  
  // NOTE: The following two methods are copied from tinyTiM, donated
  // by Lars Heuer
  private static String normalizeInteger(final String value) {
    final String val = value.trim();
    int len = val.length();
    if (len == 0)
      throw new IllegalArgumentException("Illegal integer value: " + value);

    int idx = 0;
    boolean negative = false;
    switch (val.charAt(idx)) {
    case '-':
      idx++;
      negative = true;
      break;
    case '+':
      idx++;
      break;
    }
    // Skip leading zeros if any
    while (idx < len && val.charAt(idx) == '0') {
      idx++;
    }
    if (idx == len) {
      return "0";
    }
    final String normalized = val.substring(idx);
    len = normalized.length();
    // Check if everything is a digit
    for (int i = 0; i < len; i++) {
      if (!Character.isDigit(normalized.charAt(i))) {
        throw new IllegalArgumentException("Illegal integer value: " + value);
      }
    }
    return negative && normalized.charAt(0) != 0 ? '-' + normalized : normalized;
  }

  private static String normalizeDecimal(final String value) {
    final String val = value.trim();
    int len = val.length();
    if (len == 0)
      throw new IllegalArgumentException("Illegal decimal value: " + value);

    int idx = 0;
    boolean negative = false;
    switch (val.charAt(idx)) {
    case '-':
      idx++;
      negative = true;
      break;
    case '+':
      idx++;
      break;
    }
    // Skip leading zeros if any
    while (idx < len && val.charAt(idx) == '0') {
      idx++;
    }
    if (idx == len) {
      return "0.0";
    }
    StringBuilder normalized = new StringBuilder(len);
    if (val.charAt(idx) == '.') {
      normalized.append('0');
    }
    else {
      while (idx < len && val.charAt(idx) != '.') {
        char c = val.charAt(idx);
        if (!Character.isDigit(c)) {
          throw new IllegalArgumentException("Illegal decimal value: " + value);
        }
        normalized.append(c);
        idx++;
      }
    }
    normalized.append('.');
    len--;
    while (len >= idx && val.charAt(len) == '0') {
      len--;
    }
    if (len <= idx) {
      normalized.append('0');
      if (normalized.charAt(0) == '0') {
        return "0.0";
      }
    }
    else {
      // idx points to the '.', increment it
      idx++;
      while (idx <= len) {
        char c = val.charAt(idx);
        if (!Character.isDigit(c)) {
          throw new IllegalArgumentException("Illegal decimal value: " + value);
        }
        normalized.append(c);
        idx++;
      }
    }
    return negative ? '-' + normalized.toString() : normalized.toString();
  }
  
  /**
   * Normalise a given locator reference according to CXTM spec.
   */
  private String normaliseLocatorReference(String reference) {
    String retVal = reference.substring(longestCommonPath(reference,
            strippedBase).length());
    if (retVal.startsWith("/"))
      retVal = retVal.substring(1);
    
    return retVal;
  }
  
  /**
   * Returns the longest common path of two Strings.
   * The longest common path is the longest common prefix that ends with a '/'.
   * If one string is a prefix of the other, the the longest common path is
   * the shortest (i.e. the one that is a prefix of the other).
   */
  private String longestCommonPath(String source1, String source2) {
    String retVal = "";    

    if (source1.startsWith(source2))
      retVal = source2;
      
    else if (source2.startsWith(source1))
      retVal = source1;
    
    else {
      int i = 0;
      int lastSlashIndex = 0;
      
      while (i < source1.length() && i < source2.length() 
              && source1.charAt(i) == source2.charAt(i)) {
        if (source1.charAt(i) == '/')
          lastSlashIndex = i;
        i++;
      }
  
      if (lastSlashIndex == -1)
        retVal = "";
      else 
        retVal = source1.substring(0, lastSlashIndex);
    }
       
    return retVal;
  }
  
  /**
    * Remove the fragment- and query-parts of a given locatorString.
    * @param locatorString The string from which to remove parts.
    * @return The string after the necessary removing parts.
    */
  private String stripLocator(String locatorString) {
    String retVal = locatorString;
    int queryIndex = retVal.indexOf('?');
    
    if (queryIndex > 0)
      retVal = retVal.substring(0, queryIndex);
    
    int hashIndex = retVal.indexOf('#');
    
    if (hashIndex > 0)
      retVal = retVal.substring(0, hashIndex);
      
    return retVal;
  }
  
  // --- Comparators

  abstract class AbstractComparator implements Comparator {
    protected int compareLocatorSet(Collection c1, Collection c2) {

      if (c1.size() < c2.size())
        return -1;
        
      if (c1.size() > c2.size())
        return 1;
      
      // INV: locator sets must now be of equal size.
        
      Object locators1[] = c1.toArray();
      Object locators2[] = c2.toArray();
      Arrays.sort(locators1, locatorComparator);
      Arrays.sort(locators2, locatorComparator);
      
      for (int i = 0; i < locators1.length; i++) {
        int currentCmp = compareLocator((LocatorIF) locators1[i],
                (LocatorIF) locators2[i]);  
        if (currentCmp != 0)
          return currentCmp;
      }
      
      return 0;
    }

    protected int compareTopicSet(Collection c1, Collection c2) {
      int cmp = c1.size() - c2.size();

      Iterator it1 = c1.iterator();
      Iterator it2 = c2.iterator();
      while (cmp == 0 && it1.hasNext()) {
        TopicIF t1 = (TopicIF) it1.next();
        TopicIF t2 = (TopicIF) it2.next();
        cmp = compareTopic(t1, t2);
      }

      return cmp;
    }

    protected int compareSet(Collection c1, Collection c2, Comparator comp) {
      int cmp = c1.size() - c2.size();

      Iterator it1 = c1.iterator();
      Iterator it2 = c2.iterator();
      while (cmp == 0 && it1.hasNext()) {
        cmp = comp.compare(it1.next(), it2.next());
      }

      return cmp;
    }

    protected int compareString(String s1, String s2) {
      if (s1 == s2) return 0;

      if (s1 == null) return -1;
      if (s2 == null) return 1;

      return s1.compareTo(s2);
    }

    protected int compareLocator(LocatorIF l1, LocatorIF l2) {
      if (l1 == l2) return 0;
      if (l1 == null) return -1;
      if (l2 == null) return 1;

      int cmp = normaliseLocatorReference(l1.getAddress())
              .compareTo(normaliseLocatorReference(l2.getAddress()));
      if (cmp == 0)
        cmp = l1.getNotation().compareTo(l2.getNotation());
      return cmp;
    }

    protected int compareTopic(TopicIF t1, TopicIF t2) {
      if (t1 == t2) return 0;
      if (t1 == null) return -1;
      if (t2 == null) return 1;
      
      int pos1 = ((Integer)tmIndex.get(t1)).intValue();
      int pos2 = ((Integer)tmIndex.get(t2)).intValue();
      return pos1 - pos2;
    }
    
    protected int compareAssociation(AssociationIF a1, AssociationIF a2) {
      if (a1 == a2) return 0;
      if (a1 == null) return -1;
      if (a2 == null) return 1;

      int pos1 = ((Integer)tmIndex.get(a1)).intValue();
      int pos2 = ((Integer)tmIndex.get(a2)).intValue();
      return pos1 - pos2;
    }

  }

  public static class IndexComparator implements Comparator {
    private Map indexMap;
    
    public IndexComparator(Map indexMap) {
      this.indexMap = indexMap;
    }
    
    public int compare(Object o1, Object o2) {
      Integer index1 = (Integer)indexMap.get(o1);
      Integer index2 = (Integer)indexMap.get(o2);
      
      if (index1 == null) {
        if (index2 == null)
          return 0;
        return -1;
      }
      if (index2 == null)
        return 1;
      return index1.intValue() - index2.intValue();
        
    }
  }
  
  class LocatorComparator extends AbstractComparator {

    public int compare(Object o1, Object o2) {
      return compareLocator((LocatorIF)o1, (LocatorIF)o2);
    }
  }

  class TopicComparator extends AbstractComparator {

    public int compare(Object o1, Object o2) {
      TopicIF t1 = (TopicIF) o1;
      TopicIF t2 = (TopicIF) o2;

      int cmp = compareLocatorSet(t1.getSubjectIdentifiers(),
                                  t2.getSubjectIdentifiers());
      if (cmp == 0)
        cmp = compareLocatorSet(t1.getSubjectLocators(), 
                                t2.getSubjectLocators());
      if (cmp == 0)
        cmp = compareLocatorSet(t1.getItemIdentifiers(),
                                t2.getItemIdentifiers());
      return cmp;
    }

  }

  class NameComparator extends AbstractComparator {

    public int compare(Object o1, Object o2) {
      TopicNameIF bn1 = (TopicNameIF) o1;
      TopicNameIF bn2 = (TopicNameIF) o2;

      int cmp = compareString(bn1.getValue(), bn2.getValue());
      // FIXME: Compare by type here when we can!
      if (cmp == 0)
        cmp = compareTopicSet(bn1.getScope(), bn2.getScope());
      return cmp;
    }

  }

  class SetComparator extends AbstractComparator {
  
    private Comparator elementComparator;
    
    public SetComparator(Comparator elementComparator) {
      this.elementComparator = elementComparator;
    }  
  
    public int compare(Object o1, Object o2) {
      Collection c1 = (Collection) o1;
      Collection c2 = (Collection) o2;
      
      int cmp = c1.size() - c2.size();

      Iterator it1 = c1.iterator();
      Iterator it2 = c2.iterator();
      while (cmp == 0 && it1.hasNext()) {
        cmp = elementComparator.compare(it1.next(), it2.next());
      }

      return cmp;
    }
  
  }

  class VariantComparator extends AbstractComparator {

    public int compare(Object o1, Object o2) {
      VariantNameIF vn1 = (VariantNameIF) o1;
      VariantNameIF vn2 = (VariantNameIF) o2;

      int cmp = compareString(vn1.getValue(), vn2.getValue());
      if (cmp == 0)
        cmp = compareLocator(vn1.getLocator(), vn2.getLocator());
      if (cmp == 0)
        cmp = compareTopicSet(vn1.getScope(), vn2.getScope());
      return cmp;
    }

  }

  class OccurrenceComparator extends AbstractComparator {

    public int compare(Object o1, Object o2) {
      OccurrenceIF occ1 = (OccurrenceIF) o1;
      OccurrenceIF occ2 = (OccurrenceIF) o2;

      int cmp = compareString(occ1.getValue(), occ2.getValue());
      if (cmp == 0)
        cmp = compareLocator(occ1.getDataType(), occ2.getDataType());
      if (cmp == 0)
        cmp = compareTopic(occ1.getType(), occ2.getType());
      if (cmp == 0)
        cmp = compareTopicSet(occ1.getScope(), occ2.getScope());
      return cmp;
    }

  }

  class AssociationComparator extends AbstractComparator {
    private Comparator collectionComparator;
    
    public AssociationComparator() {
      collectionComparator = new CollectionSizeFirstComparator(
          new RoleInAssociationComparator());
    }
    
    public int compare(Object o1, Object o2) {
      AssociationIF assoc1 = (AssociationIF) o1;
      AssociationIF assoc2 = (AssociationIF) o2;

      int cmp = compareTopic(assoc1.getType(), assoc2.getType());
      if (cmp == 0)
        cmp = collectionComparator.compare(assoc1.getRoles(), 
            assoc2.getRoles());
      if (cmp == 0)
        cmp = compareTopicSet(assoc1.getScope(), assoc2.getScope());
      return cmp;
    }
  }

  class RoleInAssociationComparator extends AbstractComparator {
    
    public int compare(Object o1, Object o2) {
      AssociationRoleIF role1 = (AssociationRoleIF) o1;
      AssociationRoleIF role2 = (AssociationRoleIF) o2;
      
      int cmp = compareTopic(role1.getPlayer(), role2.getPlayer());
      if (cmp == 0)
        cmp = compareTopic(role1.getType(), role2.getType());
      // No need to compare the parent assocaitions since this comparator only
      // compares roles within one assocaition.
      return cmp;
    }
  }

  class AssociationRoleComparator extends AbstractComparator {

    public int compare(Object o1, Object o2) {
      AssociationRoleIF role1 = (AssociationRoleIF) o1;
      AssociationRoleIF role2 = (AssociationRoleIF) o2;
    
      int cmp = compareTopic(role1.getPlayer(), role2.getPlayer());
      if (cmp == 0)
        cmp = compareTopic(role1.getType(), role2.getType());
      if (cmp == 0)
        cmp = compareAssociation(role1.getAssociation(), 
                role2.getAssociation());
      return cmp;
    }
  }

  /**
   * Comparator for Collections.
   * Collections of fewer elements are ordered before Collections with more.
   * Collecitons of equal size are sorted, and then compared element-wise.
   */
  class CollectionSizeFirstComparator extends CollectionComparator {
    
    public CollectionSizeFirstComparator (Comparator elementComparator) {
      super(elementComparator);
    }

    public CollectionSizeFirstComparator (Comparator betweenComparator, 
            Comparator withinComparator) {
      super(betweenComparator, withinComparator);
    }

    public int compare(Object o1, Object o2) {
      if (o1 == o2) return 0;

      Collection c1 = (Collection)o1;
      Collection c2 = (Collection)o2;
      
      // Order Collection in increasing order by size.
      if (c1.size() > c2.size())
        return 1;
      if (c1.size() < c2.size())
        return -1;
      
      return super.compare(c1, c2);
    }
  }

  /**
    * Comparator for Collections that first compares the elements, and then
    * the size of the collection.
    * The Collecitons are sorted, and then compared element-wise.
    * If the Collections are of equal size, the one with fewer elements is
    * ordered first.
    */
  private class CollectionComparator implements Comparator {
    
    // Compares elements within collection.
    private Comparator betweenComp; 
        
    // Compares elements between two collections.
    private Comparator withinComp; 
    private IteratorComparator iteratorComparator; // Compares elements.

    /** 
      * Constructs a CollectionComparator that uses elementComparator for 
      * comparison.
      * @param elementComparator Compares individual elements, both within a
      * colleciton and for elements in two different collections.
      */
    public CollectionComparator (Comparator elementComparator) {
      this(elementComparator, elementComparator);
    }

    /** 
      * Constructs a CollectionComparator that uses withinComparator and 
      * betweenComparator for comparison.
      * @param withinComparator Compares individual elements within a
      * collection.
      * @param betweenComparator Compares individual elements between two
      * collections.
      */
    public CollectionComparator (Comparator betweenComparator, 
            Comparator withinComparator) {
      this.betweenComp = betweenComparator;
      this.withinComp = withinComparator;
      iteratorComparator = new IteratorComparator(betweenComp);
    }

    public int compare(Object o1, Object o2) {
      if (o1 == o2) return 0;

      Collection c1 = (Collection)o1;
      Collection c2 = (Collection)o2;
      
      return iteratorComparator.compare(sort(c1, withinComp).iterator(),
              sort(c2, withinComp).iterator());
    }
  }

  /** 
    * Sort the given collection with the given comparator.
    */
  private SortedSet sort(Collection collection, Comparator comparator) {
    SortedSet sorted = new TreeSet(comparator);
    Iterator it = collection.iterator();
    while (it.hasNext()) {
      sorted.add(it.next());
    }
    return sorted;
  }
  
  // --- Fake wrappers

  abstract class FakeScoped implements ScopedIF {
    public Collection getScope() {
      return Collections.EMPTY_SET;
    }

    public void addTheme(TopicIF theme) {}
    public void removeTheme(TopicIF theme) {}

    public String getObjectId() {
      return null;
    }

    public boolean isReadOnly() {
      return true;
    }

    public TopicMapIF getTopicMap() {
      return null;
    }

    public Collection getItemIdentifiers() {
      return Collections.EMPTY_SET;
    }

    public void addItemIdentifier(LocatorIF source_locator) {}
    public void removeItemIdentifier(LocatorIF source_locator) {}

    public Collection getTypes() {
      return Collections.EMPTY_SET;
    }

    public void addType(TopicIF type) {}
    public void removeType(TopicIF type) {}

    public void remove() {}    
  }

  class FakeTopic extends FakeScoped implements TopicIF {
    private Collection indicator;
    private TopicMapIF tmForFake;
    
    public FakeTopic(LocatorIF indicator, TopicMapIF tmForFake) {
      this.tmForFake = tmForFake;
      this.indicator = Collections.singleton(indicator);
    }

    public Collection getSubjectIdentifiers() {
      return indicator;
    }

    public Collection getSubjectLocators() {
      return Collections.EMPTY_SET;
    }

    public Collection getTopicNames() {
      return Collections.EMPTY_SET;
    }

    public Collection getOccurrences() {
      return Collections.EMPTY_SET;
    }

    public Collection getRoles() {
      return Collections.EMPTY_SET;
    }

    public Collection getRolesByType(TopicIF roletype) {
      return Collections.EMPTY_SET;
    }

    public Collection getRolesByType(TopicIF roletype, TopicIF assoc_type) {
      return Collections.EMPTY_SET;
    }
    
    public TopicMapIF getTopicMap() {
      return tmForFake;
    }

    public void addSubjectLocator(LocatorIF subject_locator) throws ConstraintViolationException {}
    public void removeSubjectLocator(LocatorIF subject_locator) {}

    public void addSubjectIdentifier(LocatorIF subject_indicator) {}
    public void removeSubjectIdentifier(LocatorIF subject_indicator) {}
    public void merge(TopicIF topic) {}
    
    public ReifiableIF getReified() {
      return null;
    }
  }
  
  class FakeAssociation extends FakeScoped implements AssociationIF {
    private Collection roles;

    public FakeAssociation(TopicIF t, TopicIF i) {
      roles = new ArrayList(2);
      roles.add(new FakeRole(this, type, t));
      roles.add(new FakeRole(this, instance, i));
    }

    public Collection getRoles() {
      return roles;
    }

    public Collection getRoleTypes() {
      return null;
    }

    public Collection getRolesByType(TopicIF roletype) {
      Collection rolesoftype = new ArrayList();
      Iterator it = roles.iterator();
      while (it.hasNext()) {
        AssociationRoleIF role = (AssociationRoleIF) it.next();
        if (roletype.equals(role.getType()))
          rolesoftype.add(role);
      }
      return rolesoftype;
    }

    public TopicIF getType() {
      return typeInstance;
    }

    public void setType(TopicIF type) {}
    
    public TopicIF getReifier() {
      return null;
    }
  
    public void setReifier(TopicIF reifier) {}

  }

  class FakeRole extends FakeScoped implements AssociationRoleIF {
    private AssociationIF association;
    private TopicIF type;
    private TopicIF player;

    public FakeRole(AssociationIF association, TopicIF type, TopicIF player) {
      this.association = association;
      this.type = type;
      this.player = player;
    }

    public TopicIF getType() {
      return type;
    }

    public AssociationIF getAssociation() {
      return association;
    }

    public TopicIF getPlayer() {
      return player;
    }

    public void setType(TopicIF type) {}
    public void setPlayer(TopicIF player) {}

    public TopicIF getReifier() {
      return null;
    }
  
    public void setReifier(TopicIF reifier) {}

  }

  // we need this class because occurrences are output ordered by normalized
  // value, and not by the literal value
  class FakeOccurrence implements OccurrenceIF {
    private OccurrenceIF occ;
    private String value;

    public FakeOccurrence(OccurrenceIF occ) {
      this.occ = occ;

      LocatorIF datatype = occ.getDataType();
      if (datatype.equals(DataTypes.TYPE_URI)) {
        LocatorIF locator = occ.getLocator();
        this.value = normaliseLocatorReference(locator.getAddress());
      } else if (datatype.equals(DataTypes.TYPE_INTEGER) ||
                 datatype.equals(DataTypes.TYPE_DECIMAL))
        this.value = normalizeNumber(occ.getValue());
      else
        this.value = occ.getValue();
    }
    
    public TopicIF getTopic() {
      return occ.getTopic();
    }
  
    public LocatorIF getDataType() {
      return occ.getDataType();
    }

    public String getValue() {
      return value;
    }

    public Reader getReader() {
      throw new UnsupportedOperationException();
    }

    public void setValue(String value) {
      throw new UnsupportedOperationException();
    }
  
    public LocatorIF getLocator() {
      throw new UnsupportedOperationException();
    }
  
    public void setLocator(LocatorIF locator) {
      throw new UnsupportedOperationException();
    }
  
    public void setValue(String value, LocatorIF datatype) {
      throw new UnsupportedOperationException();
    }
  
    public void setReader(Reader value, long length, LocatorIF datatype) {
      throw new UnsupportedOperationException();
    }
  
    public long getLength() {
      throw new UnsupportedOperationException();
    }

    public TopicIF getType() {
      return occ.getType();
    }

    public void setType(TopicIF type) {
      throw new UnsupportedOperationException();
    }

    public TopicIF getReifier() {
      return occ.getReifier();
    }
  
    public void setReifier(TopicIF reifier) {
      throw new UnsupportedOperationException();
    }

    public Collection getScope() {
      return occ.getScope();
    }

    public void addTheme(TopicIF theme) {
      throw new UnsupportedOperationException();
    }
    
    public void removeTheme(TopicIF theme) {
      throw new UnsupportedOperationException();
    }

    public String getObjectId() {
      return occ.getObjectId();
    }

    public boolean isReadOnly() {
      return true;
    }

    public TopicMapIF getTopicMap() {
      return occ.getTopicMap();
    }

    public Collection getItemIdentifiers() {
      return occ.getItemIdentifiers();
    }

    public void addItemIdentifier(LocatorIF source_locator) {
      throw new UnsupportedOperationException();
    }
    
    public void removeItemIdentifier(LocatorIF source_locator) {
      throw new UnsupportedOperationException();
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
}
