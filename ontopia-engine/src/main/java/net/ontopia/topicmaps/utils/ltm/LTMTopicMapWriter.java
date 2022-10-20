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

package net.ontopia.topicmaps.utils.ltm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Predicate;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.DataTypes;
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
import net.ontopia.topicmaps.utils.TopicStringifiers;
import net.ontopia.topicmaps.utils.deciders.TMExporterDecider;
import net.ontopia.utils.IteratorComparator;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PUBLIC: Exports topic maps to the LTM 1.3 interchange format.
 * @since 2.2
 */
public class LTMTopicMapWriter implements TopicMapWriterIF {
  private static final String COLON = " : ";
  public static final String PROPERTY_PREFIXES = "prefixes";
  public static final String PROPERTY_FILTER = "filter";
  public static final String PROPERTY_PRESERVE_IDS = "preserveIds";
  
  private static final Logger log = LoggerFactory.getLogger(LTMTopicMapWriter.class.getName());

  protected String encoding; // the encoding reported on the first line
    
  protected boolean preserveIds;

  protected Map<String, Integer> roleCounter;
  protected Map<String, Boolean> rolesCounted;
  protected Writer out;
  protected boolean closeWriter = false;
  protected Calendar calendar;
  protected String base;

  protected Predicate<Object> filter;
  // Constrains which topic map constructs should be included in the exported
  // Ltm file.

  // Compares associations for correct output order.
  protected Comparator<AssociationIF> associationComparator;
  // Compares base names by their scope and then by their value.
  protected Comparator<TopicNameIF> baseNameComparator;
  // Compares TMObjects by their elementId.
  protected Comparator<TopicIF> elementIdComparator;
  // Compares supertype-subtype associations for correct output order.
  protected Comparator<AssociationIF> supersubComparator;
  // Compares supertype-subtype association roles for correct output order.
  protected Comparator<AssociationRoleIF> supersubRoleComparator;
  // Compares occurrences for correct output order.
  protected Comparator<OccurrenceIF> occurrenceComparator;
  // Compares collections of reifying topics by the element ids(in order).
  protected Comparator<Collection<TopicIF>> reifierComparator;
  // Compares association roles for correct output order.
  protected Comparator<AssociationRoleIF> roleComparator;
  // Compares collections of scoping topics by the element ids(in order).
  protected Comparator<Collection<TopicIF>> scopeComparator;
  // Compares topics for correct output order.
  protected Comparator<TopicIF> topicComparator;
  // Compares variant names for correct output order.
  protected Comparator<VariantNameIF> variantComparator;

  protected IdManager idManager;

  protected String groupString1;

  private Map<String, String> prefixes = new HashMap<String, String>();

  /**
   * PUBLIC: Create an LTMTopicMapWriter that writes to a given
   * File in UTF-8.
   * @param file Where the output should be written to.
   */
  public LTMTopicMapWriter(File file) throws IOException {
    this(new FileOutputStream(file), "utf-8");
  }

  /**
   * PUBLIC: Create an LTMTopicMapWriter that writes to a given
   * File in specified encoding.
   * @param file Where the output should be written to.
   * @param encoding The desired character encoding.
   */
  public LTMTopicMapWriter(File file, String encoding) throws IOException {
    this(new FileOutputStream(file), encoding);
    closeWriter = true;
  }

  /**
   * PUBLIC: Create an LTMTopicMapWriter that writes to a given
   * OutputStream in UTF-8. <b>Warning:</b> Use of this method is
   * discouraged, as it is very easy to get character encoding errors
   * with this method.
   * Note: Caller is responsible for closing the stream!
   * @param stream Where the output should be written.
   */
  public LTMTopicMapWriter(OutputStream stream) throws IOException {
    this(stream, "utf-8");
  }

  /**
   * PUBLIC: Create an LTMTopicMapWriter that writes to a given
   * OutputStream in the given encoding.
   * Note: Caller is responsible for closing the stream!
   * @param stream Where the output should be written.
   * @param encoding The desired character encoding.
   */
  public LTMTopicMapWriter(OutputStream stream, String encoding)
    throws IOException {
    this(new OutputStreamWriter(stream, encoding), encoding);
  }

  /**
   * PUBLIC: Create an LTMTopicMapWriter that writes to a given Writer.
   * Note: Caller is responsible for closing the writer!
   * @param out Where the output should be written.
   * @param encoding The encoding used by the writer. This is the encoding
   *   that will be declared on the first line of the LTM file. It must be
   *   reported, because there is no way for the LTMTopicMapWriter to know
   *   what encoding the writer uses.
   * @since 4.0
   */
  public LTMTopicMapWriter(Writer out, String encoding) {
    this.encoding = encoding;
    this.out = out;
    calendar = new GregorianCalendar();

    associationComparator = new AssociationComparator();
    roleComparator = new AssociationRoleFrequencyComparator();
    baseNameComparator = new TopicNameComparator();
    elementIdComparator = new ElementIdComparator();
    supersubComparator = new SupersubComparator();
    supersubRoleComparator = new SupersubRoleComparator();
    occurrenceComparator = new OccurrenceComparator();
    reifierComparator = new CollectionComparator<TopicIF>(new ElementIdComparator());
    scopeComparator = new CollectionComparator<TopicIF>(new ElementIdComparator());
    topicComparator = new TopicComparator();
    variantComparator = new VariantComparator();
    this.preserveIds = true;
    this.filter = null;
  }

  /**
   * PUBLIC: Set whether IDs should be preserved or generated.
   * @param preserveIds Should be set to true if IDs should be preserved.
   */
  public void setPreserveIds(boolean preserveIds) {
    this.preserveIds = preserveIds;
  }

  /**
   * PUBLIC: Sets the filter that decides which topic map constructs
   * are accepted in the exprted ltm. Uses 'filter' to identify
   * individual topic constructs as allowed or disallowed. TM
   * constructs that depend on the disallowed topics are also
   * disallowed.   
   * @param filter Places constraints on individual topicmap constructs.
   */
  public void setFilter(Predicate<Object> filter) {
    this.filter = new TMExporterDecider(filter);
  }

  /**
   * Determines whether collection contains at least one element that is
   * accepted by 'filter'.
   * @param collection The collection to search.
   * @return true iff 'collectino' contains at least one element that is
   *         accepted by filter.
   */
  private boolean hasUnfiltered(Collection<? extends TMObjectIF> collection) {
    Iterator<? extends TMObjectIF> it = collection.iterator();
    while (it.hasNext()) {
      if (filterOk(it.next())) {
        return true;
      }
    }
    return false;
  }

  public boolean addPrefix(String key, String prefix) {
    if (key == null) {
      log.warn("Attempting to add prefix with null key for prefix '" + prefix + "'");
      return false;
    }
    if (prefix == null) {
      log.warn("Attempting to add prefix with null value for key '" + key + "'");
      return false;
    }
    if (prefixes.containsKey(key) && (!prefixes.get(key).equals(prefix))) {
      log.warn("Attempting to re-add prefix key '" + key + "' for prefix '" + prefix + "', sticking to original prefix '" + prefixes.get(key));
      return false;
    }
    if (prefixes.containsValue(prefix)) {
      log.warn("Attempting to re-add prefix '" + prefix + "' for key '" + key + "', ignoring key for this prefix");
      return false;
    }
    prefixes.put(key, prefix);
    return true;
  }

  /**
   * PUBLIC: Writes out the given topic map.
   */
  @Override
  public void write(TopicMapIF tm) throws IOException {
    LocatorIF baseLocator = tm.getStore().getBaseAddress();
    base = (baseLocator == null) ? null : baseLocator.getExternalForm();
    idManager = new IdManager();

    ClassInstanceIndexIF classIndex = (ClassInstanceIndexIF)tm.getIndex(
            "net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");

    // Check if there are any untyped associations, association roles or
    // occurrences
    boolean existsUntyped = (hasUnfiltered(classIndex.getAssociationRoles(null))
        || hasUnfiltered(classIndex.getAssociations(null)) || hasUnfiltered(classIndex
        .getOccurrences(null)));

    Collection<TopicIF> topics = tm.getTopics();

    recordIds(topics);

    // Get all relevant topics sorted.
    topics = sort(filterCollection(topics), topicComparator);

    // Get the topic(s) that reifies the topicmap.
    TopicIF reifier = tm.getReifier();
    // FIXME: no need to treat this as a collection anymore
    Collection<TopicIF> tmReifiers = (reifier == null ? Collections.<TopicIF>emptySet() : Collections.singleton(reifier));
    tmReifiers = filterCollection(tmReifiers);
    topics.removeAll(tmReifiers);

    // Get all associations.
    Collection<AssociationIF> allAssociations = filterCollection(tm.getAssociations());
    boolean existsUnspecified = existsUnspecifiedRolePlayer(allAssociations);

    // Sort all the topics.
    Collection<TopicIF> topicInstances = sort(topics, topicComparator);

    // Filter out the topics that are used to type other topics.
    Collection<TopicIF> topicTypes = topicTypes(topics, topicInstances);

    // Filter out the topics that are used to type association roles.
    Collection<TopicIF> roleTypes = roleTypes(allAssociations, topicInstances);

    // Filter out the topics that are used to type associations.
    Collection<TopicIF> associationTypes = associationTypes(allAssociations,
        topicInstances);

    // Filter out the topics that are used to type occurrences.
    Collection<TopicIF> occurrenceTypes = occurrenceTypes(topics, topicInstances);

    // Count up the number of associations that a particular role, e.g.
    // "player : type" takes part in.
    countRoles(allAssociations);

    // Get all the associations in correct output order.
    allAssociations = sort(allAssociations, associationComparator);

    // Filter out all superclass/subclass associations
    TopicIF supersubtype = tm.getTopicBySubjectIdentifier(PSI
        .getXTMSuperclassSubclass());
    Collection<AssociationIF> supersubAssociations = classIndex.getAssociations(supersubtype);
    supersubAssociations = filterCollection(supersubAssociations);
    allAssociations.removeAll(supersubAssociations);

    // Filter out the associations that have roles reifying the topic map.
    Collection<AssociationIF> tmReifierAssociations = playerAssociations(tmReifiers,
        allAssociations);

    // Output preamble.
    if (encoding != null) {
      out.write("@\"" + encoding + "\"\n");
    }
    out.write("#VERSION \"1.3\"\n");
    out.write("/*\n   Generator: Ontopia");
    out.write("\n   Date:      ");
    out.write(minLengthString(calendar.get(Calendar.YEAR), 4));
    out.write('-');
    out.write(minLengthString(calendar.get(Calendar.MONTH)+1, 2));
    out.write('-');
    out.write(minLengthString(calendar.get(Calendar.DAY_OF_MONTH), 2));
    out.write(' ');
    out.write(minLengthString(calendar.get(Calendar.HOUR_OF_DAY), 2));
    out.write(':');
    out.write(minLengthString(calendar.get(Calendar.MINUTE), 2));
    out.write("\n*/\n");

    for (String key : prefixes.keySet()) {
      out.write("#PREFIX " + key + " @\"" + prefixes.get(key) + "\"\n");
    }

    // If necessary, output the prefix for untyped topic map constructs.
    if (existsUntyped) {
      out.write("\n#PREFIX untyped @\"http://psi.ontopia.net/ltm/untyped#\"\n");
    }

    // If necessary, output prefix for unspecified topic map constructs(roles).
    if (existsUnspecified) {
      out.write("\n#PREFIX unspecified");
      out.write(" @\"http://psi.ontopia.net/ltm/unspecified#\"\n");
    }

    // If necessary, output the TOPICMAP directive with any topic reification.
    Iterator<TopicIF> tmReifiersIt = tmReifiers.iterator();
    if (!tmReifiers.isEmpty()) {
      out.write("\n/* ----------------- TOPIC MAP ----------------- */\n");
      out.write("\n#TOPICMAP");
      writeReifiers(tm, out);
      out.write("\n");

      // Output the topic map reifier(s)(usually one).
      groupString1 = "";
      tmReifiersIt = tmReifiers.iterator();

      // Output the reifiers
      while (tmReifiersIt.hasNext()) {
        writeTopic(tmReifiersIt.next(), out, false);
      }
    }

    // Output all associations that the tm reifier(s) are directly involved in.
    groupString1 = "";
    Iterator<AssociationIF> tmReifierAssociationsIt = tmReifierAssociations.iterator();
    while (tmReifierAssociationsIt.hasNext()) {
      writeAssociation(tmReifierAssociationsIt.next(), out,
          false);
    }

    out.write("\n/* ----------------- ONTOLOGY ------------------ */\n");

    out.write("\n/* ----------------- Topic Types --------------- */\n");

    // Output all the topic types.
    writeTopics(topicTypes);

    out.write("\n/* ----------------- Type Hierarchy ------------ */\n");

    // Write all supertype subtype associations
    groupString1 = "";
    Iterator<AssociationIF> hierarchyIt = sort(supersubAssociations, supersubComparator)
        .iterator();
    if (hierarchyIt.hasNext()) {
      out.write("\n");
    }
    while (hierarchyIt.hasNext()) {
      AssociationIF currentAssociation = hierarchyIt.next();
      writeSupersub(currentAssociation, out);
    }

    out.write("\n/* ----------------- Role Types ---------------- */\n");
    // Write all remaining association role types.
    writeTopics(roleTypes);

    out.write("\n/* ----------------- Association Types --------- */\n");
    // Write all remaining association types
    writeTopics(associationTypes);

    out.write("\n/* ----------------- Occurrence Types ---------- */\n");
    // Write all remaining occurrence types
    writeTopics(occurrenceTypes);

    out.write("\n/* ----------------- INSTANCES ----------------- */\n");

    out.write("\n/* ----------------- Topics -------------------- */\n");
    // Write all remaining instance topics
    writeTopics(topicInstances);

    out.write("\n/* ----------------- Associations -------------- */\n");
    // Write all remaining associations
    groupString1 = "";
    Iterator<AssociationIF> associationsIt = allAssociations.iterator();
    while (associationsIt.hasNext()) {
      writeAssociation(associationsIt.next(), out);
    }

    out.flush();
    
    if (closeWriter) {
      out.close();
    }
  }

  /**
   * Write a collection of topics.
   */
  private void writeTopics(Collection<TopicIF> topics) throws IOException {
    groupString1 = "";
    Iterator<TopicIF> topicsIt = topics.iterator();
    if (topicsIt.hasNext()
        && filterOk(topics.iterator().next().getTypes())) {
      out.write("\n");
    }
    while (topicsIt.hasNext()) {
      writeTopic(topicsIt.next(), out);
    }
  }

  /**
   * Get all the associations of the roles played by 'rolePlayers'. Check if
   * each of those associations is in 'associations'. Return those that are, and
   * remove them from 'associations'.
   * @param rolePlayers The role playing topics of the associations to return.
   * @param associations All available associations that may be returned.
   * @return The associations of the roles played by 'rolePlayers' and that are
   *         in 'associations'.
   */
  private SortedSet<AssociationIF> playerAssociations(Collection<TopicIF> rolePlayers,
      Collection<AssociationIF> associations)
  {
    SortedSet<AssociationIF> retVal = new TreeSet<AssociationIF>(associationComparator);
    Iterator<TopicIF> rolePlayersIt = rolePlayers.iterator();
    while (rolePlayersIt.hasNext()) {
      Iterator<AssociationRoleIF> rolesIt = rolePlayersIt.next().getRoles().iterator();
      while (rolesIt.hasNext()) {
        AssociationIF assoc = rolesIt.next().getAssociation();
        if (assoc != null && associations.remove(assoc)) {
          retVal.add(assoc);
        }
      }
    }
    return retVal;
  }

  /**
   * Filter out the topics that are used to type other topics.
   */
  private SortedSet<TopicIF> topicTypes(Collection<TopicIF> topics, Collection<TopicIF> topicInstances) {
    SortedSet<TopicIF> typingTopics = new TreeSet<TopicIF>(topicComparator);

    Iterator<TopicIF> it = topics.iterator();
    while (it.hasNext()) {
      TopicIF currentTopic = it.next();
      Iterator<TopicIF> typesIt = currentTopic.getTypes().iterator();

      while (typesIt.hasNext()) {
        TopicIF currentType = typesIt.next();

        if (topicInstances.remove(currentType)) {
          typingTopics.add(currentType);
        }
      }
    }
    return typingTopics;
  }

  /**
   * Filter out the topics that are used to type associations.
   */
  private SortedSet<TopicIF> associationTypes(Collection<AssociationIF> associations,
      Collection<TopicIF> topicInstances)
  {
    SortedSet<TopicIF> associationTypes = new TreeSet<TopicIF>(topicComparator);

    Iterator<AssociationIF> it = associations.iterator();
    while (it.hasNext()) {
      TopicIF type = it.next().getType();

      if (type != null && topicInstances.remove(type)) {
        associationTypes.add(type);
      }
    }
    return associationTypes;
  }

  /**
   * Filter out the topics that are used to type association roles.
   */
  private SortedSet<TopicIF> roleTypes(Collection<AssociationIF> associations, Collection<TopicIF> topicInstances)
  {
    SortedSet<TopicIF> roleTypes = new TreeSet<TopicIF>(topicComparator);

    Iterator<AssociationIF> it = associations.iterator();
    while (it.hasNext()) {
      Iterator<TopicIF> typesIt = it.next().getRoleTypes().iterator();
      while (typesIt.hasNext()) {
        TopicIF currentType = typesIt.next();
        if (topicInstances.remove(currentType)) {
          roleTypes.add(currentType);
        }
      }
    }
    return roleTypes;
  }

  /**
   * Filter out the topics that are used to type occurrences. Ignore topics
   * whose occurrence instances are not accepted by filterOk().
   */
  private SortedSet<TopicIF> occurrenceTypes(Collection<TopicIF> topics, Collection<TopicIF> topicInstances)
  {
    SortedSet<TopicIF> occurrenceTypes = new TreeSet<TopicIF>(topicComparator);

    Iterator<TopicIF> it = topics.iterator();
    while (it.hasNext()) {
      TopicIF currentTopic = it.next();

      Collection<OccurrenceIF> occurrences = currentTopic.getOccurrences();

      // Get only occurrences that are accepted by filterOk().
      occurrences = filterCollection(occurrences);
      Iterator<OccurrenceIF> occurrencesIt = occurrences.iterator();
      while (occurrencesIt.hasNext()) {
        OccurrenceIF occurrence = occurrencesIt.next();
        TopicIF type = occurrence.getType();

        if (type != null && filterOk(type) && topicInstances.remove(type)) {
          occurrenceTypes.add(type);
        }
      }
    }
    return occurrenceTypes;
  }

  /**
   * Sort the given collection with the given comparator.
   */
  private <E> SortedSet<E> sort(Collection<E> collection, Comparator<? super E> comparator) {
    SortedSet<E> sorted = new TreeSet<E>(comparator);
    sorted.addAll(collection);
    return sorted;
  }

  private String getElementId(TopicIF topic) {
    return idManager.getId(topic);
  }

  // --------------------------------------------------------------------
  // Methods used on Topics
  // --------------------------------------------------------------------

  /**
   * Write the given topic to 'out'. Write a header for each new topic type.
   */
  private void writeTopic(TopicIF topic, Writer out) throws IOException {
    writeTopic(topic, out, true);
  }

  /**
   * Write the given topic to 'out'. If writeHeaders is true then write a header
   * for each new topic type.
   */
  private void writeTopic(TopicIF topic, Writer out, boolean writeHeaders)
      throws IOException
  {
    if (filterOk(topic)) {
      Collection<TopicIF> types = topic.getTypes();
      types = sort(types, elementIdComparator);
      types = filterCollection(types);
      Iterator<TopicIF> typesIt = types.iterator();

      String typeString = "";
      String headerType = "";

      if (typesIt.hasNext()) {
        headerType = getElementId(typesIt.next());
        typeString += COLON + headerType;
      }
      while (typesIt.hasNext()) {
        typeString += " " + getElementId(typesIt.next());
      }

      // IF this is the first time a topic of the current type is written
      // then write a header comment for this topic type.
      if (writeHeaders && !headerType.equals(groupString1)) {
        out.write("\n/* -- TT: ");
        if (headerType.isEmpty()) {
          out.write("(untyped)");
        } else {
          out.write(headerType);
        }
        out.write(" -- */\n");
      }

      String id = getElementId(topic);
      String idString = "[" + id + typeString;
      String baseString = "\n" + createSpaces(idString.length());

      // Get, filter and sort the basenames.
      Collection<TopicNameIF> baseNames = filterCollection(topic.getTopicNames());
      baseNames = filterCollection(baseNames);
      baseNames = sort(baseNames, baseNameComparator);
      Iterator<TopicNameIF> baseNamesIt = baseNames.iterator();

      // Write the base names indented according to topic id and types.
      if (baseNamesIt.hasNext()) {
        out.write(idString);
        out.write(" = ");
        writeTopicName(baseNamesIt.next(), out, baseString);
      } else {
        out.write(idString);
      }
      while (baseNamesIt.hasNext()) {
        out.write(baseString);
        out.write(" = ");
        writeTopicName(baseNamesIt.next(), out, baseString);
      }

      // Write the subject locator(if any).
      Collection<LocatorIF> subjectLocators = topic.getSubjectLocators();
      subjectLocators = filterCollection(subjectLocators);
      Iterator<LocatorIF> subjectLocatorsIt = subjectLocators.iterator();
      while (subjectLocatorsIt.hasNext()) {
        String externalForm = subjectLocatorsIt.next().getExternalForm();
        out.write("\n    ");
        out.write("%\"");
        out.write(escapeString(externalForm));
        out.write('"');
      }

      // Write subject indicators, one per line.
      Collection<LocatorIF> subjectIndicators = topic.getSubjectIdentifiers();
      subjectIndicators = filterCollection(subjectIndicators);
      Iterator<LocatorIF> subjectIndicatorsIt = subjectIndicators.iterator();
      while (subjectIndicatorsIt.hasNext()) {
        String externalForm = subjectIndicatorsIt.next().getExternalForm();

        boolean skip = false;
        if (prefixes.size() > 0) {
          int colonIndex = id.indexOf(':');
          if (colonIndex > -1) {
            String key = id.substring(0, colonIndex);
            String prefix = prefixes.get(key);
            String suffix = id.substring(colonIndex + 1);
            if (prefix != null) {
              skip = externalForm.equals(prefix + suffix);
            }
          }
        }

        if (!skip && (base == null || !externalForm.startsWith(base))) {
          out.write("\n    ");
          out.write("@\"");
          out.write(escapeString(externalForm));
          out.write('"');
        }
      }

      out.write("]\n");

      // Write the occurrences of this topic.
      Collection<OccurrenceIF> occurrences = topic.getOccurrences();
      occurrences = filterCollection(occurrences);
      occurrences = sort(occurrences, occurrenceComparator);
      Iterator<OccurrenceIF> occurrencesIt = occurrences.iterator();
      while (occurrencesIt.hasNext()) {
        writeOccurrence(occurrencesIt.next(), out);
      }

      groupString1 = headerType;
    }
  }

  /**
   * Write the given association to 'out'. Write headers for every new
   * association type.
   */
  private void writeAssociation(AssociationIF association, Writer out)
      throws IOException
  {
    writeAssociation(association, out, true);
  }

  /**
   * Write the given association to 'out'. If writeHeaders is true, write
   * headers for every new association type.
   */
  private void writeAssociation(AssociationIF association, Writer out,
      boolean writeHeaders) throws IOException
  {
    if (filterOk(association)) {
      String elementId = lazyTypeElementId(association);
      if (writeHeaders && !elementId.equals(groupString1)) {
        out.write("\n/* -- AT: ");
        out.write(elementId);
        out.write(" */\n");
      }

      out.write(elementId);
      out.write("( ");

      // Get and sort the roles of this association.
      Collection<AssociationRoleIF> roles = sort(association.getRoles(), roleComparator);
      Iterator<AssociationRoleIF> rolesIt = roles.iterator();

      // Write the roles of this association.
      if (rolesIt.hasNext()) {
        writeAssociationRole(rolesIt.next(), out);
      }
      if (maxRolesOf(association) == 2 && rolesIt.hasNext()) {
        out.write(", ");
        writeAssociationRole(rolesIt.next(), out);
      }
      while (rolesIt.hasNext()) {
        out.write(",\n" + repeatString(" ", elementId.length() + 2));
        writeAssociationRole(rolesIt.next(), out);
      }

      out.write(" )");

      // Write the names of the scoping topics of this association.
      writeScope(association, out);

      // Write the names of the reifying topics of this association.
      writeReifiers(association, out);
      out.write("\n");

      groupString1 = elementId;
    }
  }

  private String repeatString(String string, int length) {
    return (length == 0) ? "" : string + repeatString(string, length - 1);
  }

  /**
   * Write the given superclass-subclass association to 'out'.
   */
  private void writeSupersub(AssociationIF association, Writer out)
      throws IOException
  {
    String elementId = lazyTypeElementId(association);

    out.write(elementId);
    out.write("( ");

    // Get and sort the roles of this association.
    Collection<AssociationRoleIF> roles = association.getRoles();
    roles = sort(roles, supersubRoleComparator);
    Iterator<AssociationRoleIF> rolesIt = roles.iterator();

    // Write the association roles.
    if (rolesIt.hasNext()) {
      writeAssociationRole(rolesIt.next(), out);
    }
    while (rolesIt.hasNext()) {
      out.write(", ");
      writeAssociationRole(rolesIt.next(), out);
    }

    out.write(" )");

    // Write the names of the scoping topics of this association.
    writeScope(association, out);

    // Write the names of the reifying topics of this association.
    writeReifiers(association, out);
    out.write("\n");

    groupString1 = elementId;
  }

  /**
   * Write the given association role to 'out'.
   */
  private void writeAssociationRole(AssociationRoleIF role, Writer out)
      throws IOException
  {
    out.write(lazyPlayerElementId(role));
    out.write(COLON + lazyTypeElementId(role));

    // Write the names of the reifying topics of this association role.
    writeReifiers(role, out);
  }

  /**
   * Filter a whole collection of objects.
   * @param unfiltered The objects to filter.
   * @return A new collection containing all objects accepted by the filter, or
   *         if this.filter is null, returns the original collection.
   */
  private <E> Collection<E> filterCollection(Collection<E> unfiltered) {
    if (filter == null) {
      return unfiltered;
    }
    Collection<E> retVal = new ArrayList<E>();
    Iterator<E> unfilteredIt = unfiltered.iterator();
    while (unfilteredIt.hasNext()) {
      E current = unfilteredIt.next();
      if (filter.test(current)) {
        retVal.add(current);
      }
    }
    return retVal;
  }

  /**
   * Filter a single object..
   * @param unfiltered The object to filter.
   * @return True if the object is accepted by the filter or the filter is null.
   *         False otherwise.
   */
  private boolean filterOk(Object unfiltered) {
    if (filter == null) {
      return true;
    }
    return filter.test(unfiltered);
  }

  /**
   * Get the first VariantNameIF that is scoped by a single topic, having the
   * given subject indicator.
   * @param variants The variants to search through.
   * @param si The subject indicator to search for.
   * @return The first matching VariantNameIF, or null if none is found.
   */
  private VariantNameIF firstNameWithScopingPSI(Collection<VariantNameIF> variants,
      LocatorIF si)
  {
    VariantNameIF firstName = null;

    Iterator<VariantNameIF> it = variants.iterator();
    while (firstName == null && it.hasNext()) {
      VariantNameIF currentVariant = it.next();

      Collection<TopicIF> scope = filterCollection(currentVariant.getScope());
      if (scope.size() == 1) {
        TopicIF scopingTopic = scope.iterator().next();
        Collection<LocatorIF> scopingPSIs = scopingTopic.getSubjectIdentifiers();
        if (scopingPSIs.contains(si)) {
          firstName = currentVariant;
        }
      }
    }
    return firstName;
  }

  /**
   * Write the given TopicNameIF to the given Writer, after line breaks with the
   * given indentString.
   */
  private void writeTopicName(TopicNameIF baseName, Writer out,
      String indentString) throws IOException
  {
    // Write the base name itself.
    out.write('"');
    out.write(escapeString(baseName.getValue()));
    out.write('"');

    // Get and sort the variants of this base name.
    Collection<VariantNameIF> variants = baseName.getVariants();
    variants = filterCollection(variants);
    variants = sort(variants, variantComparator);

    VariantNameIF sortName = firstNameWithScopingPSI(variants, PSI.getXTMSort());
    VariantNameIF displayName = firstNameWithScopingPSI(variants, PSI
        .getXTMDisplay());
    if (sortName != null) {
      variants.remove(sortName);
      out.write("; \"");
      out.write(escapeString(sortName.getValue()));
      out.write('"');
    }
    if (displayName != null) {
      if (sortName == null) {
        out.write("; ");
      }
      variants.remove(displayName);
      out.write("; \"");
      out.write(escapeString(displayName.getValue()));
      out.write('"');
    }

    // Write the names of the scoping topics of this topic basename.
    writeScope(baseName, out);

    Iterator<VariantNameIF> variantIt = variants.iterator();

    // Write the variants.
    while (variantIt.hasNext()) {
      out.write(indentString);
      out.write("  ");
      writeVariant(variantIt.next(), out);
    }
  }

  /**
   * Write the given TopicNameIF to the given Writer, after line breaks with the
   * given indentString.
   */
  private void writeVariant(VariantNameIF variant, Writer out)
      throws IOException {
    if (Objects.equals(variant.getDataType(), DataTypes.TYPE_STRING)) {
      String value = variant.getValue();
      if (value != null) {
        out.write("(\"");
        out.write(escapeString(value));
        out.write('"');

        // Write the names of the scoping topics of this variant.
        writeScope(variant, out);

        out.write(')');
      }
    } else {
      out.write("/* WARNING: LTM 1.3 cannot represent variant names that");
      out.write(" are not of xsd:string type, hence cannot represent object [");
      out.write(variant.getObjectId());
      out.write("] */");
    }
  }
    

  /**
   * Write one given occurrence to 'out'.
   */
  private void writeOccurrence(OccurrenceIF occurrence, Writer out)
      throws IOException
  {

    if (filterOk(occurrence)) {
      String value = occurrence.getValue();

      if (occurrence.getDataType().equals(DataTypes.TYPE_URI)) {
        out.write("   {");
        out.write(getElementId(occurrence.getTopic()));
        out.write(", ");
        out.write(lazyTypeElementId(occurrence));
        out.write(", ");
        out.write("\"");
        out.write(occurrence.getLocator().getExternalForm());
        out.write("\"");
        out.write('}');
      } else if (occurrence.getDataType().equals(DataTypes.TYPE_STRING)) {
        out.write("   {");
        out.write(getElementId(occurrence.getTopic()));
        out.write(", ");
        out.write(lazyTypeElementId(occurrence));
        out.write(", ");
        out.write("[[");
        out.write(escapeInternalOccurrence(value));
        out.write("]]");
        out.write('}');
      } else {
        out.write("/* WARNING: LTM 1.3 cannot represent occurrences that");
        out.write(" are not of xsd:string or xsd:anyURI type, hence cannot represent object [");
        out.write(occurrence.getObjectId());
        out.write("] */");
      }

      // Write the names of the scoping topics of this occurrence.
      writeScope(occurrence, out);

      // Write the names of the reifying topics of this occurrence.
      writeReifiers(occurrence, out);
      out.write("\n");
    }
  }

  private void writeReifiers(ReifiableIF reifiable, Writer out)
      throws IOException
  {
    // Write the reifier.
    TopicIF reifier = reifiable.getReifier();
    if (reifier != null) {
      out.write("~ " + getElementId(reifier));
    }
  }

  /**
   * Writes the names of the scoping topics of a given tmObject. Generates
   * output like e.g. " / topic1 topic2 topic3".
   */
  private void writeScope(ScopedIF tmObject, Writer out) throws IOException {
    // Get and sort the scoping topics of this occurrence.
    Collection<TopicIF> scope = tmObject.getScope();

    // No need to filter scope. All of scope must be filterOk() to reach this
    // method.

    scope = sort(scope, elementIdComparator);
    Iterator<TopicIF> scopeIt = scope.iterator();

    // Write the scoping topics.
    if (scopeIt.hasNext()) {
      out.write(" /");
    }
    while (scopeIt.hasNext()) {
      out.write(" " + getElementId(scopeIt.next()));
    }
  }

  /**
   * Counts one association + role combination(increment counter by 1).
   */
  private void count(AssociationIF association, AssociationRoleIF role) {
    String longkey = lazyTypeElementId(association) + COLON
        + lazyPlayerElementId(role) + COLON + lazyTypeElementId(role);
    if (rolesCounted.get(longkey) == null) {
      Integer value = getCount(association, role);
      String key = lazyTypeElementId(association) + COLON
          + lazyTypeElementId(role);

      roleCounter.put(key, value + 1);
      rolesCounted.put(longkey, Boolean.FALSE);
    }
  }

  private void countMaxRolesOf(AssociationIF association) {
    int newCount = association.getRoles().size();
    int oldCount = maxRolesOf(association);
    if (oldCount < newCount) {
      roleCounter.put(lazyTypeElementId(association), newCount);
    }
  }

  private int maxRolesOf(AssociationIF association) {
    String key = lazyTypeElementId(association);
    Integer count = roleCounter.get(key);
    return (count == null) ? 0 : count.intValue();
  }

  /**
   * Counts the number of times each role appears within a particular type of
   * associations, i.e. how often a triple like "associationType : rolePlayer :
   * roleType" is repeated
   */
  private void countRoles(Collection<AssociationIF> associations) {
    roleCounter = new HashMap<String, Integer>();
    rolesCounted = new HashMap<String, Boolean>();

    Iterator<AssociationIF> associationsIt = associations.iterator();
    while (associationsIt.hasNext()) {
      AssociationIF association = associationsIt.next();
      countMaxRolesOf(association);

      Iterator<AssociationRoleIF> rolesIt = association.getRoles().iterator();
      while (rolesIt.hasNext()) {
        count(association, rolesIt.next());
      }
    }
  }

  /**
   * Returns true iff there exists an association role with no player.
   */
  private boolean existsUnspecifiedRolePlayer(Collection<AssociationIF> associations) {
    Iterator<AssociationIF> associationsIt = associations.iterator();
    while (associationsIt.hasNext()) {
      AssociationIF association = associationsIt.next();

      Iterator<AssociationRoleIF> rolesIt = association.getRoles().iterator();
      while (rolesIt.hasNext()) {
        if (rolesIt.next().getPlayer() == null) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Returns an id string for a given topic, as it should appear when preserving
   * IDs.
   */
  private String generateId(TopicIF topic) {
    String name = TopicStringifiers.toString(topic);

    String retVal;
    if ("[No name]".equals(name)) {
      retVal = idManager.makeId(topic, "id", true);
    } else {
      String generatedId = StringUtils.normalizeId(name);
      if (generatedId == null || isReservedId(generatedId)
          || allSameAs(generatedId, '_')) {
        retVal = idManager.makeId(topic, "id", true);
      } else {
        retVal = idManager.makeId(topic, generatedId, false);
      }
    }
    return retVal;
  }

  /**
   * Get the count for a given association + role combination.
   */
  private Integer getCount(AssociationIF association, AssociationRoleIF role) {
    String key = lazyTypeElementId(association) + COLON
        + lazyTypeElementId(role);
    Integer retVal = roleCounter.get(key);
    if (retVal == null) {
      return 0;
    }
    return retVal;
  }

  /**
   * Returns the element id of the player of a given associaiton role. For roles
   * with no specified player, returns a default string.
   */
  private String lazyPlayerElementId(AssociationRoleIF role) {
    TopicIF player = role.getPlayer();
    if (player == null) {
      return "unspecified:player";
    }
    return getElementId(player);
  }

  /**
   * Returns the element id of the type of a given topicmap object. For untyped
   * occurrences, associations and roles, returns a default string for untyped
   * topicmap objects.
   * @throws OntopiaRuntimeException for other untyped topic map objects.
   */
  private String lazyTypeElementId(TypedIF tmObject) {
    TopicIF type = tmObject.getType();
    if (type == null) {
      if (tmObject instanceof AssociationIF) {
        return "untyped:association";
      }
      if (tmObject instanceof AssociationRoleIF) {
        return "untyped:role";
      }
      if (tmObject instanceof OccurrenceIF) {
        return "untyped:occurrence";
      }

      throw new OntopiaRuntimeException("lazyTypeElementId can only be"
          + " applied to associations, association roles or" + " occurrences.");
    }
    return getElementId(type);
  }

  /**
   * Returns an id string for a given topic, as it should appear when preserving
   * IDs.
   */
  private String preserveId(TopicIF topic) {
    Iterator<LocatorIF> sourceLocators = topic.getItemIdentifiers().iterator();
    while (sourceLocators.hasNext()) {
      LocatorIF sourceLocator = sourceLocators.next();
      String fragmentId = getFragment(sourceLocator);
      if (!(fragmentId == null || isReservedId(fragmentId))) {
        // If fragmentId ends with _n for some integer n
        int lastPos = fragmentId.lastIndexOf('_');
        String remaining = fragmentId.substring(lastPos + 1);
        if (lastPos > 0 && remaining.length() > 0 && allDigits(remaining)) {
          return idManager.makeId(topic, fragmentId.substring(0, lastPos + 1),
              true);
        }
        if (fragmentId.length() > 0 &&
            !(fragmentId.startsWith("id") &&
              fragmentId.length() > 2 &&
              allDigits(fragmentId.substring(2)))) {
          return idManager.makeId(topic, fragmentId, false);
        }
      }
    } // Found no appropriate source locator.
    return idManager.makeId(topic, "id", true);
  }
  
  /**
   * Creates ids for all the topics in 'topics'.
   */
  private void recordIds(Collection<TopicIF> topics) {
    Iterator<TopicIF> it = topics.iterator();

    if (preserveIds) {
      while (it.hasNext()) {
        preserveId(it.next());
      }
    } else {
      while (it.hasNext()) {
        generateId(it.next());
      }
    }
  }

  /**
   * Test whether source is a string that only contains digits.
   */
  private static boolean allDigits(String source) {
    for (int i = 0; i < source.length(); i++) {
      if (!Character.isDigit(source.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns true iff all characters in 'source' are the same as in 'sameAs'.
   */
  private static boolean allSameAs(String source, char sameAs) {
    for (int i = 0; i < source.length(); i++) {
      if (source.charAt(i) != sameAs) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns a new string of a given length, containing all spaces.
   */
  private static String createSpaces(int length) {
    String retVal = "";
    for (int i = 0; i < length; i++) {
      retVal += " ";
    }
    return retVal;
  }

  /**
   * Replace any characters that can couse problems in strings by their
   * corresponding unicode escape sequence.
   */
  private static String escapeString(String source) {
    String retVal = "";
    for (int i = 0; i < source.length(); i++) {
      char c = source.charAt(i);
      if (c == '"') {
        retVal += "\\u000022";
      } else {
        retVal += c;
      }
    }
    return retVal;
  }

  /**
   * Replace any characters that can couse problems in external occurrences by
   * their corresponding unicode escape sequence.
   */
  private static String escapeInternalOccurrence(String source) {
    String retVal = "";
    for (int i = 0; i < source.length(); i++) {
      char c = source.charAt(i);
      if (c == ']') {
        retVal += "\\u00005D";
      } else {
        retVal += c;
      }
    }
    return retVal;
  }

  /**
   * Get the fragment identifier from a locator.
   */
  private String getFragment(LocatorIF locator) {
    String retVal = locator.getAddress();
    int lastHashIndex = retVal.lastIndexOf('#') + 1;
    if (lastHashIndex != 0) {
      retVal = retVal.substring(lastHashIndex);
    } else {
      retVal = "";
    }
    if (!validate((retVal))) {
      return null;
    }
    return retVal;
  }
  
  /** 
   * Checks whether the given id is a valid LTM ID, matching
   * [A-Za-z_][-A-Za-z_0-9.]*
   */
  private boolean validate(String id) {
    if (id.length() == 0) {
      return false;
    }
    
    char ch = id.charAt(0);
    if (!((ch >= 'A' && ch <= 'Z') ||
          (ch >= 'a' && ch <= 'z') ||
          ch == '_')) {
      return false;
    }
    
    for (int ix = 1; ix < id.length(); ix++) {
      ch = id.charAt(ix);
      if (!((ch >= 'A' && ch <= 'Z') ||
            (ch >= 'a' && ch <= 'z') ||
            (ch >= '0' && ch <= '9') ||
            ch == '_' || ch == '-' || ch == '.')) {
        return false;
      }
    }
    return true;
  }
  
  /**
   * @return true iff at least one of the subject indicators of the given topic
   *         matches the given uri
   */
  private static boolean lazyHasLocator(TopicIF topic, LocatorIF uri) {
    return topic != null && topic.getSubjectIdentifiers().contains(uri);
  }

  /**
   * Test whether a given string is a reserved systematic ID, in other words,
   * whether it's on the form "id"("_" | X) {0, .., 9}*, where X is an upper
   * case character.
   */
  private static boolean isReservedId(String fragmentId) {
    return fragmentId.startsWith("id")
        && fragmentId.length() >= 3
        && (Character.isDigit(fragmentId.charAt(2)) || inRange('A', fragmentId
            .charAt(2), 'Z')) && allDigits(fragmentId.substring(3));
  }

  private static boolean inRange(char lowerBound, char it, char upperBound) {
    return lowerBound <= it && it <= upperBound;
  }

  /**
   * Compare two strings(using .compareTo()). null strings are ordered before
   * non-null strings.
   */
  private static int lazyStringCompare(String source1, String source2) {
    if (source1 == null) {
      if (source2 == null) {
        return 0;
      }
      return -1;
    }
    if (source2 == null) {
      return 1;
    }
    int retVal = source1.compareToIgnoreCase(source2);
    if (retVal == 0) {
      return source1.compareTo(source2);
    }
    return retVal;
  }

  /**
   * Converts a given string to a string of some given minimum length.
   */
  private static String minLengthString(int source, int length) {
    String retVal = String.valueOf(source);
    while (retVal.length() < length) {
      retVal = "0" + retVal;
    }
    return retVal;
  }

  /**
   * Helps manage the creation of systematic and unique ids.
   */
  private class IdManager {
    private Map<String, Integer> counters;
    private Map<TopicIF, String> ids;

    private IdManager() {
      counters = new HashMap<String, Integer>();
      ids = new HashMap<TopicIF, String>();
    }

    /**
     * Get the ID of a given topic. Will return null if no id has been created.
     */
    private String getId(TopicIF topic) {
      return ids.get(topic);
    }

    /**
     * Create ID for given topic, adding an automtic number suffix if necessary
     * @param topic The topic for which to create an id.
     * @param baseId the id to be given. If already used, a suffix is added.
     * @param forceSuffix A suffix is always added if forceSuffix is true.
     */
    private String makeId(TopicIF topic, String baseId, boolean forceSuffix) {

      if (prefixes.size() > 0) {
        for (LocatorIF psi : topic.getSubjectIdentifiers()) {
          String externalForm = psi.getExternalForm();
          for (String key : prefixes.keySet()) {
            String prefix = prefixes.get(key);
            if (externalForm.startsWith(prefix)) {
              String suffix = externalForm.substring(prefix.length());
              if ((suffix.length() > 0) && !suffix.contains("/")) {
                String result = key + ":" + suffix;
                ids.put(topic, result);
                return result;
              }
            }
          }
        }
      }

      String retVal = baseId;
      Integer suffix = counters.get(baseId);
      if (suffix == null) {
        if (forceSuffix) {
          retVal = baseId + "1";
          suffix = 2;
        } else {
          retVal = baseId;
          suffix = 2;
        }
      } else {
        retVal = baseId + suffix;
        suffix = suffix + 1;
      }

      counters.put(baseId, suffix);
      ids.put(topic, retVal);
      return retVal;
    }
  }

  /**
   * Comparator for Objects of type TopicIF.
   */
  private class TopicComparator implements Comparator<TopicIF> {

    @Override
    public int compare(TopicIF t1, TopicIF t2) {
      int retVal = 0;

      Iterator<TopicIF> t1TypesIt = sort(t1.getTypes(), elementIdComparator).iterator();
      Iterator<TopicIF> t2TypesIt = sort(t2.getTypes(), elementIdComparator).iterator();

      if (t1TypesIt.hasNext()) {
        TopicIF t1Type = t1TypesIt.next();

        if (t2TypesIt.hasNext()) {
          TopicIF t2Type = t2TypesIt.next();
          retVal = lazyStringCompare(getElementId(t1Type), getElementId(t2Type));
        } else {
          retVal = -1; // Out of types -> ordered first.
        }
      } else {
        if (t2TypesIt.hasNext()) {
          retVal = 1; // Out of types -> ordered first.
        } else {
          retVal = 0; // Equal types -> equal(w.r.t. types).
        }
      }

      if (retVal == 0) {
        retVal = lazyStringCompare(getElementId(t1), getElementId(t2));
      }

      return retVal;
    }
  }

  /**
   * Order TMObjects by their elementId.
   */
  private class ElementIdComparator implements Comparator<TopicIF> {

    @Override
    public int compare(TopicIF o1, TopicIF o2) {
      return lazyStringCompare(getElementId(o1),
          getElementId(o2));
    }
  }

  /**
   * Comparator for Objects of type AssociationIF.
   */
  private class AssociationComparator implements Comparator<AssociationIF> {

    private Comparator<Collection<AssociationRoleIF>>
    // Compares collections of association roles by role type ids.
        associationRoleTypeComparator,
        // Compares collections of association roles by role player ids.
        associationRolePlayerComparator,
        // Compare collections of association roles with deafault comparator.
        associationRolesComparator;

    public AssociationComparator() {
      associationRoleTypeComparator = new CollectionNoSizeComparator<AssociationRoleIF>(
          new RoleTypeComparator(), new AssociationRoleFrequencyComparator());
      associationRolePlayerComparator = new CollectionComparator<AssociationRoleIF>(
          new RolePlayerComparator(), new AssociationRoleFrequencyComparator());
      associationRolesComparator = new CollectionComparator<AssociationRoleIF>(
          new AssociationRoleComparator(),
          new AssociationRoleFrequencyComparator());
    }

    @Override
    public int compare(AssociationIF assoc1, AssociationIF assoc2) {
      int retVal = lazyStringCompare(lazyTypeElementId(assoc1),
          lazyTypeElementId(assoc2));

      // Compare the type of each of the roles in turn.
      if (retVal == 0) {
        retVal = associationRoleTypeComparator.compare(assoc1.getRoles(),
            assoc2.getRoles());
      }

      // Compare the player of each of the roles in turn.
      if (retVal == 0) {
        retVal = associationRolePlayerComparator.compare(assoc1.getRoles(),
            assoc2.getRoles());
      }

      // Compare the each of the roles in turn.
      if (retVal == 0) {
        retVal = associationRolesComparator.compare(assoc1.getRoles(), assoc2
            .getRoles());
      }

      // Compare the scoping topics(if any) of the association.
      if (retVal == 0) {
        retVal = scopeComparator.compare(filterCollection(assoc1.getScope()),
            filterCollection(assoc2.getScope()));
      }

      // Compare the reifier(if there is one) of the association.
      if (retVal == 0) {
        TopicIF reifier1 = assoc1.getReifier();
        Collection<TopicIF> reifiers1 = (reifier1 == null ? Collections.<TopicIF>emptySet() : Collections.singleton(reifier1));
        TopicIF reifier2 = assoc2.getReifier();
        Collection<TopicIF> reifiers2 = (reifier2 == null ? Collections.<TopicIF>emptySet() : Collections.singleton(reifier2));
        reifiers1 = filterCollection(reifiers1);
        reifiers2 = filterCollection(reifiers2);
        retVal = reifierComparator.compare(reifiers1, reifiers2);
      }

      if (retVal == 0 && !assoc1.equals(assoc2)) {
        retVal = -1;
      }

      return retVal;
    }
  }

  /**
   * Compares roles by comparing their types.
   */
  private class RoleTypeComparator implements Comparator<AssociationRoleIF> {

    @Override
    public int compare(AssociationRoleIF ar1, AssociationRoleIF ar2) {
      int retVal = lazyStringCompare(lazyTypeElementId(ar1),
          lazyTypeElementId(ar2));

      return retVal;
    }
  }

  /**
   * Compares roles by comparing their players.
   */
  private class RolePlayerComparator implements Comparator<AssociationRoleIF> {

    @Override
    public int compare(AssociationRoleIF ar1, AssociationRoleIF ar2) {
      int retVal = lazyStringCompare(lazyPlayerElementId(ar1),
          lazyPlayerElementId(ar2));

      return retVal;
    }
  }

  /**
   * Compares association roles by comparing how frequently each
   * (association-type, role-player, role-type) - triple occurs within this
   * topicmap. Then compares using AssociationRoleComparator. Then checks for
   * equality(.equals) and, if not equal, orders arbitrarily.
   */
  private class AssociationRoleFrequencyComparator implements Comparator<AssociationRoleIF> {
    private AssociationRoleComparator associationRoleComparator;

    public AssociationRoleFrequencyComparator() {
      associationRoleComparator = new AssociationRoleComparator();
    }

    @Override
    public int compare(AssociationRoleIF ar1, AssociationRoleIF ar2) {
      // Lookup how many times the current combination of association,
      // role-type and role-player occurrs in this topic map.
      Integer count1 = getCount(ar1.getAssociation(), ar1);
      Integer count2 = getCount(ar2.getAssociation(), ar2);

      // Compare frequencies of(association-type, role-type and role-player).
      int retVal = count1.compareTo(count2);

      if (retVal == 0) {
        retVal = associationRoleComparator.compare(ar1, ar2);
      }

      // If topics are the same in all other ways, check if they're equal.
      if (retVal == 0 && !ar1.equals(ar2)) {
        // If they are not equal, arbitrarily order 1st role first.
        retVal = -1;
      }

      return retVal;
    }
  }

  /**
   * Compares association roles by comparing the IDs of their types, players and
   * reifiers respectively.
   */
  private class AssociationRoleComparator implements Comparator<AssociationRoleIF> {

    @Override
    public int compare(AssociationRoleIF ar1, AssociationRoleIF ar2) {
      // Compare the IDs of the role types.
      int retVal = lazyStringCompare(lazyTypeElementId(ar1),
          lazyTypeElementId(ar2));

      // Compare the IDs of the roles themselves.
      if (retVal == 0) {
        retVal = lazyStringCompare(lazyPlayerElementId(ar1),
            lazyPlayerElementId(ar2));
      }

      // Compare the reifier(if any) of the association roles.
      if (retVal == 0) {
        TopicIF reifier1 = ar1.getReifier();
        Collection<TopicIF> reifiers1 = (reifier1 == null ? Collections.<TopicIF>emptySet() : Collections.singleton(reifier1));
        TopicIF reifier2 = ar2.getReifier();
        Collection<TopicIF> reifiers2 = (reifier2 == null ? Collections.<TopicIF>emptySet() : Collections.singleton(reifier2));
        reifiers1 = filterCollection(reifiers1);
        reifiers2 = filterCollection(reifiers2);
        retVal = reifierComparator.compare(reifiers1, reifiers2);
      }

      return retVal;
    }
  }

  /**
   * Compares topic base names for correct output order.
   */
  private class TopicNameComparator implements Comparator<TopicNameIF> {

    @Override
    public int compare(TopicNameIF bn1, TopicNameIF bn2) {
      if (Objects.equals(bn1, bn2)) {
        return 0;
      }

      int retVal = scopeComparator.compare(filterCollection(bn1.getScope()),
          filterCollection(bn2.getScope()));
      if (retVal == 0) {
        retVal = lazyStringCompare(bn1.getValue(), bn2.getValue());
      }
      return retVal;
    }

  }

  /**
   * Compares occurrences for correct output order.
   */
  private class OccurrenceComparator implements Comparator<OccurrenceIF> {

    @Override
    public int compare(OccurrenceIF occ1, OccurrenceIF occ2) {
      if (occ1 == occ2) {
        return 0;
      }

      int retVal = lazyStringCompare(lazyTypeElementId(occ1),
          lazyTypeElementId(occ2));

      if (retVal == 0) {
        retVal = lazyStringCompare(occ1.getValue(), occ2.getValue());
      }

      // Compare the scoping topics(if any) of the occurrences.
      if (retVal == 0) {
        retVal = scopeComparator.compare(filterCollection(occ1.getScope()),
            filterCollection(occ2.getScope()));
      }

      // Compare the reifier(if there is one) of the occurrences.
      if (retVal == 0) {
        TopicIF reifier1 = occ1.getReifier();
        Collection<TopicIF> reifiers1 = (reifier1 == null ? Collections.<TopicIF>emptySet() : Collections.singleton(reifier1));
        TopicIF reifier2 = occ2.getReifier();
        Collection<TopicIF> reifiers2 = (reifier2 == null ? Collections.<TopicIF>emptySet() : Collections.singleton(reifier2));
        reifiers1 = filterCollection(reifiers1);
        reifiers2 = filterCollection(reifiers2);
        retVal = reifierComparator.compare(reifiers1, reifiers2);
      }

      if (retVal == 0) {
        retVal = -1;
      }

      return retVal;
    }

  }

  /**
   * Compares variant names for correct output order.
   */
  private class VariantComparator implements Comparator<VariantNameIF> {

    @Override
    public int compare(VariantNameIF vn1, VariantNameIF vn2) {
      if (Objects.equals(vn1, vn2)) {
        return 0;
      }

      int retVal = scopeComparator.compare(filterCollection(vn1.getScope()),
          filterCollection(vn2.getScope()));
      if (retVal == 0) {
        retVal = lazyStringCompare(vn1.getValue(), vn2.getValue());
      }
      return retVal;
    }

  }

  /**
   * Comparator for Collections that first compares the elements, and then the
   * size of the collection. The Collecitons are sorted, and then compared
   * element-wise. If the Collections are of equal size, the one with fewer
   * elements is ordered first.
   */
  private class CollectionComparator<E> implements Comparator<Collection<E>> {
    private Comparator<? super E> betweenComp; // Compares elements within collection.
    private Comparator<? super E> withinComp; // Compares elements between two
    // collections.
    private IteratorComparator<E> iteratorComparator; // Compares elements.

    /**
     * Constructs a CollectionComparator that uses elementComparator for
     * comparison.
     * @param elementComparator Compares individual elements, both within a
     *        colleciton and for elements in two different collections.
     */
    public CollectionComparator(Comparator<? super E> elementComparator) {
      this(elementComparator, elementComparator);
    }

    /**
     * Constructs a CollectionComparator that uses withinComparator and
     * betweenComparator for comparison.
     * @param withinComparator Compares individual elements within a collection.
     * @param betweenComparator Compares individual elements between two
     *        collections.
     */
    public CollectionComparator(Comparator<? super E> betweenComparator,
        Comparator<? super E> withinComparator)
    {
      this.betweenComp = betweenComparator;
      this.withinComp = withinComparator;
      iteratorComparator = new IteratorComparator<E>(betweenComp);
    }

    @Override
    public int compare(Collection<E> c1, Collection<E> c2) {
      if (c1 == c2) {
        return 0;
      }

      return iteratorComparator.compare(sort(c1, withinComp).iterator(), sort(
          c2, withinComp).iterator());
    }
  }

  /**
   * Comparator for Collections that the each pair of elements until it reaches
   * the end of one of the collection, in which case the collections will be
   * regarded as _equal_.
   */
  private class CollectionNoSizeComparator<E> implements Comparator<Collection<E>> {
    private Comparator<? super E> betweenComp; // Compares elements within collection.
    private Comparator<? super E> withinComp; // Compares elements between two

    // collections.

    /**
     * Constructs a CollectionComparator that uses elementComparator for
     * comparison.
     * @param elementComparator Compares individual elements, both within a
     *        colleciton and for elements in two different collections.
     */
    public CollectionNoSizeComparator(Comparator<? super E> elementComparator) {
      this(elementComparator, elementComparator);
    }

    /**
     * Constructs a CollectionComparator that uses withinComparator and
     * betweenComparator for comparison.
     * @param withinComparator Compares individual elements within a collection.
     * @param betweenComparator Compares individual elements between two
     *        collections.
     */
    public CollectionNoSizeComparator(Comparator<? super E> betweenComparator,
        Comparator<? super E> withinComparator)
    {
      this.betweenComp = betweenComparator;
      this.withinComp = withinComparator;
    }

    @Override
    public int compare(Collection<E> o1, Collection<E> o2) {
      if (o1 == o2) {
        return 0;
      }

      Collection<E> c1 = sort(o1, withinComp);
      Collection<E> c2 = sort(o2, withinComp);

      Iterator<E> i1 = c1.iterator();
      Iterator<E> i2 = c2.iterator();

      // NOTE: Deliberately does not take size into account.
      int retVal = 0;
      while (retVal == 0 && i1.hasNext() && i2.hasNext()) {
        retVal = betweenComp.compare(i1.next(), i2.next());
      }

      return retVal;
    }
  }

  /**
   * Comparator for superclass-subclass associations.
   */
  private class SupersubComparator implements Comparator<AssociationIF> {
    private Comparator<Iterator<AssociationRoleIF>> iteratorComparator;
    private Comparator<AssociationRoleIF> supersubRoleComparator;

    public SupersubComparator() {
      supersubRoleComparator = new SupersubRoleComparator();
      iteratorComparator = new IteratorComparator<AssociationRoleIF>(supersubRoleComparator);

    }

    @Override
    public int compare(AssociationIF assoc1, AssociationIF assoc2) {
      // Compare the sortes sets of roles element-wise.
      return iteratorComparator.compare(sort(assoc1.getRoles(),
          supersubRoleComparator).iterator(), sort(assoc2.getRoles(),
          supersubRoleComparator).iterator());
    }
  }

  /**
   * Compares association roles in supertype-subtype associations correct output
   * order.
   */
  private class SupersubRoleComparator implements Comparator<AssociationRoleIF> {

    @Override
    public int compare(AssociationRoleIF ar1, AssociationRoleIF ar2) {
      int retVal = 0;

      // Get the types.
      TopicIF type1 = ar1.getType();
      TopicIF type2 = ar2.getType();

      if (lazyHasLocator(type1, PSI.getXTMSuperclass())) {
        if (!lazyHasLocator(type2, PSI.getXTMSuperclass())) {
          retVal = -1;
        }
      } else {
        if (lazyHasLocator(type2, PSI.getXTMSuperclass())) {
          retVal = 1;
        }
      }

      if (retVal == 0) {
        if (lazyHasLocator(type1, PSI.getXTMSubclass())) {
          if (!lazyHasLocator(type2, PSI.getXTMSubclass())) {
            retVal = -1;
          }
        } else {
          if (lazyHasLocator(type2, PSI.getXTMSubclass())) {
            retVal = 1;
          }
        }
      }

      if (retVal == 0) {
        retVal = lazyStringCompare(lazyPlayerElementId(ar1),
            lazyPlayerElementId(ar2));
      }

      return retVal;
    }
  }

  /**
   * Sets additional properties for LTMTopicMapWriter. Accepted properties:
   * <ul><li>'preserveIds' (Boolean), corresponds to {@link #setPreserveIds(boolean)}</li>
   * <li>'filter' (DeciderIF), corresponds to {@link #setFilter(net.ontopia.utils.DeciderIF)}</li>
   * <li>'prefixes' (Map), each key-value pair is passed to 
   * {@link #addPrefix(java.lang.String, java.lang.String)} as Strings.</li>
   * </ul>
   * @param properties 
   */
  @SuppressWarnings("unchecked")
  @Override
  public void setAdditionalProperties(Map<String, Object> properties) {
    Object value = properties.get(PROPERTY_PRESERVE_IDS);
    if ((value != null) && (value instanceof Boolean)) {
      setPreserveIds((Boolean) value);
    }
    value = properties.get(PROPERTY_FILTER);
    if ((value != null) && (value instanceof Predicate)) {
      setFilter((Predicate) value);
    }
    value = properties.get(PROPERTY_PREFIXES);
    if ((value != null) && (value instanceof Map)) {
      Map _prefixes = (Map) value;
      for (Object k : _prefixes.entrySet()) {
        addPrefix(k.toString(), _prefixes.get(k).toString());
      }
    }
  }
}
