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

package net.ontopia.topicmaps.utils.rdf;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.topicmaps.utils.AssociationBuilder;
import net.ontopia.topicmaps.utils.MergeUtils;
import net.ontopia.topicmaps.utils.PSI;
import net.ontopia.utils.OntopiaRuntimeException;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.rdfxml.xmlinput.ALiteral;
import org.apache.jena.rdfxml.xmlinput.AResource;
import org.apache.jena.rdfxml.xmlinput.StatementHandler;
import org.apache.jena.shared.JenaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EXPERIMENTAL: Converts an RDF model to a topic map using a
 * schema-specific mapping defined using RDF.
 */
public class RDFToTopicMapConverter {
  private TopicMapIF topicmap;
  private Map mappings;
  private TopicMapBuilderIF builder;
  private boolean lenient;

  private static Logger logger = LoggerFactory.getLogger(RDFToTopicMapConverter.class.getName());

  protected static final String RTM_PREFIX = "http://psi.ontopia.net/rdf2tm/#";
  public static final String RTM_MAPSTO             = RTM_PREFIX + "maps-to";
  public static final String RTM_BASENAME           = RTM_PREFIX + "basename";
  public static final String RTM_INSTANCE_OF        = RTM_PREFIX + "instance-of";
  public static final String RTM_OCCURRENCE         = RTM_PREFIX + "occurrence";
  public static final String RTM_ASSOCIATION        = RTM_PREFIX + "association";
  public static final String RTM_SUBJECT_ROLE       = RTM_PREFIX + "subject-role";
  public static final String RTM_OBJECT_ROLE        = RTM_PREFIX + "object-role";
  public static final String RTM_IN_SCOPE           = RTM_PREFIX + "in-scope";
  public static final String RTM_SUBJECT_URI        = RTM_PREFIX + "subject-uri";
  public static final String RTM_OBJECT_URI         = RTM_PREFIX + "object-uri";
  public static final String RTM_SOURCE_LOCATOR     = RTM_PREFIX + "source-locator";
  public static final String RTM_SUBJECT_IDENTIFIER = RTM_PREFIX + "subject-identifier";
  public static final String RTM_SUBJECT_LOCATOR    = RTM_PREFIX + "subject-locator";
  public static final String RTM_TYPE               = RTM_PREFIX + "type";
  public static final String RTM_GENERATED_NAME     = RTM_PREFIX + "generated-name";

  /**
   * EXPERIMENTAL: Converts an RDF model into the topic map using the
   * given mapping.
   * @param infileurl the URL to read the input data from
   * @param syntax the syntax of the input data. Values are "RDF/XML", "N3",
   *               and "N-TRIPLE". Defaults to "RDF/XML" if null.
   * @param mappingurl the URL to read the mapping from. If null the mapping
   *                   is taken from the input data.
   * @param mappingsyntax the syntax of the mapping. Values are "RDF/XML", "N3",
   *                   and "N-TRIPLE". Defaults to "RDF/XML" if null.
   * @param topicmap The topic map to add the converted data to.
   * @param lenient When false, errors are thrown if the RDF data cannot be
   *        correctly mapped (for example, a statement type is mapped to a
   *        topic name, but has a URI value).
   */
  public static void convert(URL infileurl, String syntax,
                             String mappingurl, String mappingsyntax,
                             TopicMapIF topicmap, boolean lenient)
    throws JenaException, IOException, URISyntaxException {

    RDFToTopicMapConverter converter =
      new RDFToTopicMapConverter(mappingurl, mappingsyntax, topicmap);
    converter.setLenient(lenient);
    converter.doConversion(infileurl, syntax);

  }

  /**
   * EXPERIMENTAL: Converts an RDF model into the topic map using the
   * given mapping.
   * @param input the InputStream to read the input data from
   * @param syntax the syntax of the input data. Values are "RDF/XML", "N3",
   *               and "N-TRIPLE". Defaults to "RDF/XML" if null.
   * @param mappingurl the URL to read the mapping from. If null the mapping
   *                   is taken from the input data.
   * @param mappingsyntax the syntax of the mapping. Values are "RDF/XML", "N3",
   *                   and "N-TRIPLE". Defaults to "RDF/XML" if null.
   * @param topicmap The topic map to add the converted data to.
   * @param lenient When false, errors are thrown if the RDF data cannot be
   *        correctly mapped (for example, a statement type is mapped to a
   *        topic name, but has a URI value).
   */
  public static void convert(InputStream input, String syntax,
                             String mappingurl, String mappingsyntax,
                             TopicMapIF topicmap, boolean lenient)
    throws JenaException, IOException, URISyntaxException {

    RDFToTopicMapConverter converter =
      new RDFToTopicMapConverter(mappingurl, mappingsyntax, topicmap);
    converter.setLenient(lenient);
    converter.doConversion(input, syntax);

  }

  /**
   * EXPERIMENTAL: Converts an RDF model into the topic map using the
   * mapping found within the RDF model.
   */
  public static void convert(Model model, TopicMapIF topicmap)
    throws JenaException, IOException, URISyntaxException {

    RDFToTopicMapConverter converter = new RDFToTopicMapConverter(model, topicmap);
    converter.doConversion(model);

  }

  /**
   * EXPERIMENTAL: Automatically generates names for nameless topics
   * based on their subject identifiers.
   * @since 2.0.4
   */
  public static void generateNames(TopicMapIF topicmap) {
    TopicMapBuilderIF builder = topicmap.getBuilder();

    QueryProcessorIF processor = QueryUtils.getQueryProcessor(topicmap);
    QueryResultIF result, generatedNameQuery;

    try {
      result = processor.execute("select $TOPIC, $SI from " +
                                 "  subject-identifier($TOPIC, $SI), " +
                                 "  not(topic-name($TOPIC, $TN))?");

      // Check if a topic has already been created for scoping generated names.
      generatedNameQuery = processor.execute("select $TOPIC from " +
                                             "  subject-identifier($TOPIC, \""
                                             + RTM_GENERATED_NAME + "\")?");
    } catch (InvalidQueryException e) {
      throw new OntopiaRuntimeException(e); // impossible error
    }

    // If it exists, use old rtm:generated-name topic. Otherwise, create one.
    TopicIF generatedNameTopic = (generatedNameQuery.next())
            ? (TopicIF) generatedNameQuery.getValue("TOPIC")
            : builder.makeTopic();
    try {
      generatedNameTopic.addSubjectIdentifier(new URILocator(RTM_GENERATED_NAME));
      builder.makeTopicName(generatedNameTopic, "Generated Name");
    } catch (URISyntaxException e) {
      throw new OntopiaRuntimeException(e); // impossible error
    }

    int tix = result.getIndex("TOPIC");
    int six = result.getIndex("SI");
    while (result.next()) {
      TopicIF topic = (TopicIF) result.getValue(tix);
      String indicator = (String) result.getValue(six);

      int hash = indicator.indexOf('#');
      int slash = indicator.lastIndexOf('/');
      String name;
      if (hash != -1) {
        name = indicator.substring(slash + 1, hash) + ":" +
                indicator.substring(hash + 1);
      } else {
        name = indicator.substring(slash + 1);
      }

      if (name.length() > 0) {
        TopicNameIF bn = builder.makeTopicName(topic, name);
        bn.addTheme(generatedNameTopic);
      }

    }
    result.close();
  }

  // --- Internal

  private RDFToTopicMapConverter(String mappingurl, String syntax, TopicMapIF topicmap)
    throws JenaException, URISyntaxException {

    this.topicmap = topicmap;
    this.builder = topicmap.getBuilder();

    if (mappingurl != null) {
      Model model = ModelFactory.createDefaultModel();
      model.read(mappingurl, syntax);
      buildMappings(model);
    }
  }

  private RDFToTopicMapConverter(Model model, TopicMapIF topicmap)
    throws JenaException, URISyntaxException {

    this.topicmap = topicmap;
    this.builder = topicmap.getBuilder();

    buildMappings(model);
  }

  private void setLenient(boolean lenient) {
    this.lenient = lenient;
  }

  private void doConversion(URL url, String syntax)
    throws JenaException, IOException, URISyntaxException {

    if (mappings != null && (syntax == null || syntax.equals("RDF/XML"))) {
      RDFUtils.parseRDFXML(url, new ToTMStatementHandler());
    } else {
      Model model = ModelFactory.createDefaultModel();
      model.read(url.openStream(), url.toString(), syntax);
      if (mappings == null) {
        buildMappings(model);
      }

      doConversion(model);
    }
  }

  private void doConversion(InputStream input, String syntax)
    throws JenaException, IOException, URISyntaxException {

    if (mappings != null && (syntax == null || syntax.equals("RDF/XML"))) {
      RDFUtils.parseRDFXML(input, new ToTMStatementHandler());
    } else {
      Model model = ModelFactory.createDefaultModel();
      model.read(input, input.toString(), syntax);
      if (mappings == null) {
        buildMappings(model);
      }

      doConversion(model);
    }
  }

  private void doConversion(Model model) throws JenaException {
    StatementHandler totm = new ToTMStatementHandler();
    AResourceWrapper subjw = new AResourceWrapper();
    AResourceWrapper propw = new AResourceWrapper();
    AResourceWrapper objtw = new AResourceWrapper();
    ALiteralWrapper litlw = new ALiteralWrapper();

    ResIterator it = model.listSubjects();
    while (it.hasNext()) {
      Resource subject = (Resource) it.next();

      StmtIterator it2 = subject.listProperties(); // get all statements
      while (it2.hasNext()) {
        Statement stmt = (Statement) it2.next();

        subjw.resource = stmt.getSubject();
        propw.resource = stmt.getPredicate();

        RDFNode obj = stmt.getObject();
        if (obj instanceof Resource) {
          objtw.resource = (Resource) obj;
          totm.statement(subjw, propw, objtw);
        } else {
          litlw.literal = (Literal) obj;
          totm.statement(subjw, propw, litlw);
        }
      }
    }
  }

  private void buildMappings(Model model) throws URISyntaxException {
    mappings = new HashMap();
    Property mapsTo = model.createProperty(RTM_MAPSTO);
    StmtIterator it = model.listStatements(null, mapsTo, (RDFNode) null);
    while (it.hasNext()) {
      Statement stmt = (Statement) it.next();
      StatementHandler mapper = getMapper(stmt.getSubject(), stmt.getObject(), model);
      mappings.put(stmt.getSubject().getURI(), mapper);
    }
    it.close();
  }

  // --- Configuration interface

  // --- Internal

  private StatementHandler getMapper(Resource subject, RDFNode node, Model model)
    throws JenaException, URISyntaxException {
    String uri = node.toString();
    if (RTM_BASENAME.equals(uri)) {
      return new TopicNameMapper(getScope(subject, model));
    } else if (RTM_INSTANCE_OF.equals(uri)) {
      Collection scope = getScope(subject, model);
      if (scope.isEmpty()) {
        return new InstanceMapper();
      } else {
        return new ScopedInstanceMapper(scope);
      }
    } else if (RTM_SUBJECT_IDENTIFIER.equals(uri)) {
      return new SubjectIdentifierMapper();
    } else if (RTM_SOURCE_LOCATOR.equals(uri)) {
      return new SourceLocatorMapper();
    } else if (RTM_SUBJECT_LOCATOR.equals(uri)) {
      return new SubjectLocatorMapper();
    } else if (RTM_OCCURRENCE.equals(uri)) {
      return new OccurrenceMapper(getType(subject, model), getScope(subject, model));
    } else if (RTM_ASSOCIATION.equals(uri)) {
      LocatorIF srole = getTopicIndicator(subject, RTM_SUBJECT_ROLE, model);
      if (srole == null) {
        throw new RDFMappingException("No rtm:subject-role for " + subject);
      }
      LocatorIF orole = getTopicIndicator(subject, RTM_OBJECT_ROLE, model);
      if (orole == null) {
        throw new RDFMappingException("No rtm:object-role for " + subject);
      }
      return new AssociationMapper(srole, orole, getType(subject, model),
                                   getScope(subject, model));
    } else {
      throw new RDFMappingException("Unknown value for rtm:maps-to: " + uri);
    }
  }

  /**
   * Finds all RTM_IN_SCOPE properties for this property and returns a
   * collection containing the RDF URIs of the values as URILocators.
   */
  private Collection getScope(RDFNode rdfprop, Model model)
    throws JenaException, URISyntaxException {

    Resource subject = (Resource) rdfprop;
    Property prop = model.getProperty(RTM_IN_SCOPE);
    NodeIterator it = model.listObjectsOfProperty(subject, prop);
    ArrayList scope = new ArrayList();

    while (it.hasNext()) {
      Object o = it.next();

      if (!(o instanceof Resource)) {
        throw new RDFMappingException("Scoping topic must be specified by a resource, not by " + o);
      }

      Resource obj = (Resource) o;
      LocatorIF loc = new URILocator(obj.getURI());
      scope.add(loc);
    }

    return scope;
  }

  private TopicIF getType(RDFNode rdfprop, Model model)
    throws JenaException, URISyntaxException {

    Resource subject = (Resource) rdfprop;
    Property prop = model.getProperty(RTM_TYPE);
    NodeIterator it = model.listObjectsOfProperty(subject, prop);
    while (it.hasNext()) {
      Resource obj = (Resource) it.next();
      LocatorIF loc = new URILocator(obj.getURI());
      TopicIF topic = topicmap.getTopicBySubjectIdentifier(loc);
      if (topic == null) {
        topic = builder.makeTopic();
        topic.addSubjectIdentifier(loc);
      }
      return topic;
    }
    return null;
  }

  private TopicIF getTopic(LocatorIF loc) {
    TopicIF topic = topicmap.getTopicBySubjectIdentifier(loc);
    if (topic == null) {
      topic = builder.makeTopic();
      topic.addSubjectIdentifier(loc);
    }
    return topic;
  }

  private LocatorIF getTopicIndicator(Resource subject, String property,
                                      Model model)
    throws JenaException, URISyntaxException {
    Property prop = model.getProperty(property);
    NodeIterator it = model.listObjectsOfProperty(subject, prop);
    while (it.hasNext()) {
      Resource obj = (Resource) it.next();
      if (obj.isAnon()) {
        continue; // FIXME: is this really ok?
      }
      return new URILocator(obj.getURI());
    }
    return null;
  }

  // --- Individual mappers

  abstract class AbstractMapper implements StatementHandler {
    protected Collection scope;
    protected String construct;
    /**
     * INTERNAL: If false, the scope collection contains unresolved
     * URILocator objects. If true, the scope collection contains the
     * topics identified by the URILocator objects. This is to avoid
     * bug #1317, ie: to avoid creating topics for scope topics
     * attached to unused properties when using an external mapping
     * file.
     */
    protected boolean translated;

    public AbstractMapper(String construct) {
      this.construct = construct;
    }

    public AbstractMapper(String construct, Collection scope) {
      this.scope = scope;
      this.construct = construct;
    }

    public TopicIF getSubject(AResource sub) {
      try {
        TopicIF topic;
        if (sub.isAnonymous()) {
          // next 5 lines solve bug #1339 by working around
          // http://sourceforge.net/tracker/index.php?func=detail&aid=1082269&group_id=40417&atid=430288
          String id;
          if (sub.hasNodeID()) {
            id = sub.getAnonymousID();
          } else {
            id = sub.toString();
          }
          
          // cleanup the id: x-anon:<-24324d21:15bda0c7eee:-7fff> is not a legal uri
          id = id.replaceAll("<", "").replaceAll(">", "");
          
          // we don't want the pseudo-URIs of anonymous nodes as
          // subject identifiers
          LocatorIF loc = new URILocator("x-anon:" + id);
          topic = (TopicIF) topicmap.getObjectByItemIdentifier(loc);
          if (topic == null) {
            topic = builder.makeTopic();
            topic.addItemIdentifier(loc);
          }
        } else {
          LocatorIF loc = new URILocator(sub.getURI());
          topic = topicmap.getTopicBySubjectIdentifier(loc);
          if (topic == null) {
            topic = builder.makeTopic();
            topic.addSubjectIdentifier(loc);
          }
        }
        return topic;
      } catch (URISyntaxException e) {
        throw new OntopiaRuntimeException(e);
      }
    }

    public TopicIF getPredicate(AResource obj) {
      return getSubject(obj);
    }

    public TopicIF getObject(AResource obj) {
      return getSubject(obj);
    }

    public void addScope(ScopedIF scoped) {
      if (!translated) {
        resolveScope();
      }

      Iterator it = scope.iterator();
      while (it.hasNext()) {
        scoped.addTheme((TopicIF) it.next());
      }
    }

    @Override
    public void statement(AResource sub, AResource pred, ALiteral lit) {
      String msg = "Statements mapped to " + construct + " cannot have literal " +
                   "objects. Found (" + sub + ", " + pred + ", " + lit + ")";
      logger.warn(msg);
      if (!lenient) {
        throw new RDFMappingException(msg);
      }
    }

    @Override
    public void statement(AResource sub, AResource pred, AResource obj) {
      String msg = "Statements mapped to " + construct + " cannot have URI " +
        "reference objects. Found (" + sub + ", " + pred + ", " + obj + ")";
      logger.warn(msg);
      if (!lenient) {
        throw new RDFMappingException(msg);
      }
    }

    // Internal methods

    private void resolveScope() {
      translated = true; // avoid multiple calls here

      Collection scope2 = new ArrayList(scope.size());
      Iterator it = scope.iterator();
      while (it.hasNext()) {
        LocatorIF loc = (LocatorIF) it.next();
        TopicIF topic = topicmap.getTopicBySubjectIdentifier(loc);
        if (topic == null) {
          topic = builder.makeTopic();
          topic.addSubjectIdentifier(loc);
        }
        scope2.add(topic);
      }
      scope = scope2; // now we've translated the scope
    }
  }

  class InstanceMapper extends AbstractMapper {
    public InstanceMapper() {
      super("rtm:instance-of");
    }

    @Override
    public void statement(AResource sub, AResource pred, AResource obj) {
      TopicIF topic = getSubject(sub);
      TopicIF type = getObject(obj);
      topic.addType(type);
    }
  }

  class ScopedInstanceMapper extends AbstractMapper {
    private AssociationBuilder abuilder;

    public ScopedInstanceMapper(Collection scope) {
      super("rtm:instance-of", scope);

      TopicIF assoc = getTopic(PSI.getXTMClassInstance());
      TopicIF role1 = getTopic(PSI.getXTMClass());
      TopicIF role2 = getTopic(PSI.getXTMInstance());

      abuilder = new AssociationBuilder(assoc, role1, role2);
    }

    @Override
    public void statement(AResource sub, AResource pred, AResource obj) {
      TopicIF topic = getSubject(sub);
      TopicIF type = getObject(obj);

      AssociationIF assoc = abuilder.makeAssociation(type, topic); // (class, instance)
      addScope(assoc);
    }

    private TopicIF getTopic(LocatorIF indicator) {
      TopicIF topic = topicmap.getTopicBySubjectIdentifier(indicator);
      if (topic == null) {
        topic = builder.makeTopic();
        topic.addSubjectIdentifier(indicator);
      }
      return topic;
    }
  }

  class SubjectIdentifierMapper extends AbstractMapper {
    public SubjectIdentifierMapper() {
      super("rtm:subject-identifier");
    }

    @Override
    public void statement(AResource sub, AResource pred, AResource obj) {
      if (obj.isAnonymous()) {
        logger.warn("Blank nodes cannot be subject identifiers; " +
                     "subject: " + sub.getURI() + "; " +
                     "property: " + pred.getURI());
        throw new RDFMappingException("Blank nodes cannot be subject identifiers",
                                      sub.getURI(), pred.getURI());
      }

      TopicIF topic = getSubject(sub);
      LocatorIF loc = null;
      try {
        loc = new URILocator(obj.getURI());
      } catch (URISyntaxException e) {
        throw new OntopiaRuntimeException("INTERNAL ERROR", e);
      }

      TopicIF other = topicmap.getTopicBySubjectIdentifier(loc);
      if (other != null && !other.equals(topic)) {
        MergeUtils.mergeInto(other, topic);
      } else {
        topic.addSubjectIdentifier(loc);
      }
    }
  }

  class SourceLocatorMapper extends AbstractMapper {
    public SourceLocatorMapper() {
      super("rtm:source-locator");
    }

    @Override
    public void statement(AResource sub, AResource pred, AResource obj) {
      if (obj.isAnonymous()) {
        logger.warn("Blank nodes cannot be source locators; " +
                     "subject: " + sub.getURI() + "; " +
                     "property: " + pred.getURI());
        if (!lenient) {
          throw new RDFMappingException("Blank nodes cannot be source locators",
                                        sub.getURI(), pred.getURI());
        }
      }

      TopicIF topic = getSubject(sub);
      LocatorIF loc = null;
      try {
        loc = new URILocator(obj.getURI());
      } catch (URISyntaxException e) {
        throw new OntopiaRuntimeException("INTERNAL ERROR", e);
      }

      TMObjectIF other = topicmap.getObjectByItemIdentifier(loc);
      if (other instanceof TopicIF) {
        TopicIF othert = (TopicIF) other;
        if (othert != null && !othert.equals(topic)) {
          MergeUtils.mergeInto(othert, topic);
        } else {
          topic.addItemIdentifier(loc);
        }
      } // else FIXME: what to do?
    }
  }

  class SubjectLocatorMapper extends AbstractMapper {
    public SubjectLocatorMapper() {
      super("rtm:subject-locator");
    }

    @Override
    public void statement(AResource sub, AResource pred, AResource obj) {
      if (obj.isAnonymous()) {
        logger.warn("Blank nodes cannot be subject locators; " +
                     "subject: " + sub.getURI() + "; " +
                     "predicate: " + pred.getURI());
        throw new RDFMappingException("Blank nodes cannot be subject locators",
                                      sub.getURI(), pred.getURI());
      }

      TopicIF topic = getSubject(sub);
      LocatorIF loc = null;
      try {
        loc = new URILocator(obj.getURI());
      } catch (URISyntaxException e) {
        throw new OntopiaRuntimeException("INTERNAL ERROR", e);
      }

      TopicIF other = topicmap.getTopicBySubjectLocator(loc);
      if (other != null && other != topic) {
        MergeUtils.mergeInto(other, topic);
      } else {
        topic.addSubjectLocator(loc);
      }
    }
  }

  class TopicNameMapper extends AbstractMapper {

    public TopicNameMapper(Collection scope) {
      super("rtm:basename", scope);
    }

    @Override
    public void statement(AResource sub, AResource pred, ALiteral lit) {
      TopicIF topic = getSubject(sub); // FIXME: support xml:lang here?
      TopicNameIF bn = builder.makeTopicName(topic, lit.toString());
      addScope(bn);
    }
  }

  class OccurrenceMapper extends AbstractMapper {
    private TopicIF type;

    public OccurrenceMapper(TopicIF type, Collection scope) {
      super("rtm:occurrence", scope);
      this.type = type;
    }

    @Override
    public void statement(AResource sub, AResource pred, AResource obj) {
      if (obj.isAnonymous()) {
        logger.warn("Blank node cannot be occurrence value; " +
                     "subject: " + sub.getURI() + "; " +
                     "predicate: " + pred.getURI());
        if (!lenient) {
          throw new RDFMappingException("Blank node cannot be occurrence value",
                                        sub.getURI(), pred.getURI());
        }
      }

      String uri = obj.getURI();
      if (uri == null) {
        return; // this happens; not sure why, but it does, so we work around it
      }

      try {
        TopicIF topic = getSubject(sub);
        TopicIF ourtype = type;
        if (ourtype == null) {
          ourtype = getPredicate(pred);
        }
        OccurrenceIF occ = builder.makeOccurrence(topic, ourtype, new URILocator(uri));
        addScope(occ);
      } catch (URISyntaxException e) {
        throw new OntopiaRuntimeException(e);
      }
    }

    @Override
    public void statement(AResource sub, AResource pred, ALiteral lit) {
      TopicIF topic = getSubject(sub);
      TopicIF ourtype = type;
      if (ourtype == null) {
        ourtype = getPredicate(pred);
      }
      OccurrenceIF occ = builder.makeOccurrence(topic, ourtype, lit.toString());
      addScope(occ);
    }
  }

  class AssociationMapper extends AbstractMapper {
    private LocatorIF sroleloc; // avoids creating roles if not needed
    private LocatorIF oroleloc; // ditto
    private TopicIF srole;
    private TopicIF orole;
    private TopicIF type;

    public AssociationMapper(LocatorIF sroleloc, LocatorIF oroleloc,
                             TopicIF type, Collection scope) {
      super("rtm:association", scope);
      this.type = type;
      this.sroleloc = sroleloc;
      this.oroleloc = oroleloc;
    }

    @Override
    public void statement(AResource sub, AResource pred, AResource obj) {
      TopicIF topic = getSubject(sub);
      TopicIF object = getObject(obj);

      TopicIF ourtype = type;
      if (ourtype == null) {
        ourtype = getPredicate(pred);
      }
      if (srole == null) {
        srole = getTopic(sroleloc);
        orole = getTopic(oroleloc);
      }

      AssociationIF assoc = builder.makeAssociation(ourtype);
      builder.makeAssociationRole(assoc, srole, topic);
      builder.makeAssociationRole(assoc, orole, object);
      addScope(assoc);
    }
  }

  // --- Mapping statement handler

  class ToTMStatementHandler implements StatementHandler {

    @Override
    public void statement(AResource sub, AResource pred, ALiteral lit) {
      StatementHandler handler = (StatementHandler) mappings.get(pred.getURI());
      if (handler != null) {
        handler.statement(sub, pred, lit);
      }
    }

    @Override
    public void statement(AResource sub, AResource pred, AResource obj) {
      StatementHandler handler = (StatementHandler) mappings.get(pred.getURI());
      if (handler != null) {
        handler.statement(sub, pred, obj);
      }
    }

  }

}
