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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapWriterIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.topicmaps.utils.PSI;
import net.ontopia.topicmaps.utils.deciders.TMExporterDecider;
import net.ontopia.utils.OntopiaRuntimeException;
import org.apache.jena.rdf.model.AnonId;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdfxml.xmlinput.ALiteral;
import org.apache.jena.rdfxml.xmlinput.AResource;
import org.apache.jena.rdfxml.xmlinput.StatementHandler;

/**
 * PUBLIC: A topic map writer that can convert topic maps to RDF.  The
 * conversion may result in an RDF event stream, an RDF model, or RDF
 * serialized into the RDF/XML format.
 *
 * @since 2.0
 */
public class RDFTopicMapWriter implements TopicMapWriterIF {
  public static final String PROPERTY_PRESERVE_REIFICATION = "preserveReification";
  public static final String PROPERTY_PRESERVE_SCOPE = "preserveScope";
  public static final String PROPERTY_FILTER = "filter";
  protected StatementHandler handler;
  protected Model model;
  protected Writer writer;
  protected Map namepreds;
  protected Map preferred_roles;
  protected boolean preserve_scope = true;
  protected boolean preserve_reification = true; 
  protected Predicate filter;
  
  private static final String NS_RDF  = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
  private static final String NS_RDFS = "http://www.w3.org/2000/01/rdf-schema#";
  private static final String NS_OWL  = "http://www.w3.org/2002/07/owl#";
  private static final String NS_TM   = "http://psi.ontopia.net/tminrdf/#";
  private static final String NS_TM2RDF = "http://psi.ontopia.net/tm2rdf/#";

  /// constructors

  /**
   * PUBLIC: Creates a writer that writes the RDF representation to the
   * given StatementHandler.
   */
  public RDFTopicMapWriter(StatementHandler handler) {
    this.handler = handler;
  }

  /**
   * PUBLIC: Creates a writer that writes the RDF representation to
   * the given OutputStream serialized to RDF/XML and using the UTF-8
   * character encoding.
   */
  public RDFTopicMapWriter(OutputStream stream) throws IOException {
    this(stream, "utf-8");
  }

  /**
   * PUBLIC: Creates a writer that writes the RDF representation to
   * the given OutputStream serialized to RDF/XML and using the given
   * character encoding.
   * @since 5.1.3
   */
  public RDFTopicMapWriter(OutputStream stream, String encoding) 
    throws IOException {
    this(new OutputStreamWriter(stream, "utf-8"));
  }
  
  /**
   * PUBLIC: Creates a writer that writes the RDF representation to
   * the given OutputStream serialized to RDF/XML.
   */
  public RDFTopicMapWriter(Writer writer) {
    this.writer = writer;
    model = ModelFactory.createDefaultModel();
    handler = new ModelBuildingHandler(model);    
  }

  /**
   * PUBLIC: Creates a writer that builds an RDF representation of the
   * topic map in the given Jena RDF model.
   */
  public RDFTopicMapWriter(Model model) {
    this.model = model;
    this.handler = new ModelBuildingHandler(model);    
  }

  /// options

  /**
   * PUBLIC: Controls whether the writer will use RDF reification to
   * preserve the scopes in the topic map.
   */
  public void setPreserveScope(boolean preserve_scope) {
    this.preserve_scope = preserve_scope;
  }
  
  /**
   * PUBLIC: Returns true if the writer will use RDF reification to
   * preserve the scopes in the topic map.
   */
  public boolean getPreserveScope() {
    return preserve_scope;
  }

  /**
   * PUBLIC: Controls whether the writer will use RDF reification to
   * preserve reification in the topic map.
   */
  public void setPreserveReification(boolean preserve_reification) {
    this.preserve_reification = preserve_reification;
  }

  /**
   * PUBLIC: Returns true if the writer will use RDF reification to
   * preserve reification in the topic map.
   */
  public boolean getPreserveReification() {
    return preserve_reification;
  }

  /**
   * PUBLIC: Sets the filter that decides which topic map constructs
   * are accepted and exported. Uses 'filter' to identify individual
   * topic constructs as allowed or disallowed. TM constructs that
   * depend on the disallowed topics are also disallowed.
   *
   * @param filter Places constraints on individual topicmap constructs.
   */  
  public void setFilter(Predicate filter) {
    this.filter = new TMExporterDecider(filter);
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
   * Filter a whole collection of objects.
   * @param unfiltered The objects to filter.
   * @return A new collection containing all objects accepted by the filter, or
   *         if this.filter is null, returns the original collection.
   */
  private Collection filterCollection(Collection unfiltered) {
    if (filter == null) {
      return unfiltered;
    }
    Collection retVal = new ArrayList();
    Iterator unfilteredIt = unfiltered.iterator();

    while (unfilteredIt.hasNext()) {
      Object current = unfilteredIt.next();
      if (filter.test(current)) {
        retVal.add(current);
      }
    }
    return retVal;
  }
  
  /// the actual writer
  
  @Override
  public void write(TopicMapIF topicmap) {
    // http://www.ilrt.bris.ac.uk/discovery/chatlogs/rdfig/2003-12-17#T12-14-33
    setup(topicmap);
        
    // topics
    Collection topics = topicmap.getTopics();
    topics = filterCollection(topics);
    Iterator it = topics.iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF) it.next();
      write(topic);
    }

    // associations
    Collection associations = topicmap.getAssociations();
    associations = filterCollection(associations);
    it = associations.iterator();
    while (it.hasNext()) {
      AssociationIF assoc = (AssociationIF) it.next();
      write(assoc);
    }

    // finishing up
    if (model != null && writer != null) {
      model.write(writer);
    }
  }

  protected void write(TopicIF topic) {
    AResource namedef = new AResourceWrapper(NS_RDFS + "label");
    AResource type = new AResourceWrapper(NS_RDF + "type");
    AResource sameas = new AResourceWrapper(NS_OWL + "sameAs");
    AResource subject = getResource(topic);

    // subject indicators
    Iterator it2 = topic.getSubjectIdentifiers().iterator();
    while (it2.hasNext()) {
      AResource other = getResource((LocatorIF) it2.next());
      if (!other.equals(subject)) {
        handler.statement(subject, sameas, other);
      }
    }
      
    // types
    Collection types = topic.getTypes();
    types = filterCollection(types);
    it2 = types.iterator();
    AResource topictype = null;
    while (it2.hasNext()) {
      topictype = getResource((TopicIF) it2.next());
      handler.statement(subject, type, topictype);
    }

    // base names
    Collection baseNames = topic.getTopicNames();
    baseNames = filterCollection(baseNames);
    it2 = baseNames.iterator();
    while (it2.hasNext()) {
      TopicNameIF bn = (TopicNameIF) it2.next();
      AResource namepred;

      if (bn.getType().getSubjectIdentifiers().contains(PSI.getSAMNameType())) {
        namepred = (AResource) namepreds.get(topictype);
        if (namepred == null) {
          namepred = namedef;
        }
      } else {
        namepred = getResource(bn.getType());
      }
      
      statement(subject, namepred, getLiteral(bn.getValue()), bn);
    }

    // occurrences
    Collection occurrences = topic.getOccurrences();
    occurrences = filterCollection(occurrences);
    it2 = occurrences.iterator();
    while (it2.hasNext()) {
      OccurrenceIF occ = (OccurrenceIF) it2.next();
      
      if (Objects.equals(occ.getDataType(), DataTypes.TYPE_URI)) {
        statement(subject, getResource(occ.getType()),
                getResource(occ.getLocator()), occ);
      } else {
        statement(subject, getResource(occ.getType()),
                  getLiteral(occ.getValue()), occ);
      }
      // else don't make a statement, since there is no value (bug #1797)
    }
  }

  protected void write(AssociationIF assoc) {    
    if (assoc.getRoles().size() == 1) {
      // unary
      AResource trueres = new AResourceWrapper(NS_TM + "true");
      AResource pred = getResource(assoc.getType());
      AssociationRoleIF role = (AssociationRoleIF)
        assoc.getRoles().iterator().next();
      statement(getResource(role.getPlayer()), pred, trueres, assoc);
      
    } else if (assoc.getRoles().size() == 2) {
      // binary
      TopicMapIF topicmap = assoc.getTopicMap();
      TopicIF preferredRoleType = getTopic(topicmap, "preferred-role");
      TopicIF namePropertyType = getTopic(topicmap, "name-property");
      TopicIF assoctype = assoc.getType();
      if (assoctype != null &&
          (assoctype.equals(namePropertyType) ||
           assoctype.equals(preferredRoleType))) {
        return; // don't export mapping
      }
      TopicIF preferred = (TopicIF) preferred_roles.get(assoctype);
      TopicIF subject = null;
      TopicIF object = null;
      
      Iterator it2 = assoc.getRoles().iterator();
      AssociationRoleIF role = (AssociationRoleIF) it2.next();
      if (preferred == null || preferred.equals(role.getType())) {
        subject = role.getPlayer();
        if (preferred == null) {
          preferred_roles.put(assoctype, role.getType());
        }
      } else {
        object = role.getPlayer();
      }
      
      role = (AssociationRoleIF) it2.next();
      if (preferred != null && preferred.equals(role.getType()) &&
          subject == null) {
        subject = role.getPlayer();
        if (preferred == null) {
          preferred_roles.put(assoctype, role.getType());
        }
      } else {
        object = role.getPlayer();
      }

      if (subject != null && assoctype != null && object != null) {
        statement(getResource(subject),
                  getResource(assoctype),
                  getResource(object),
                  assoc);
      }
    } else {
      // lotsary
      
      AResource type = new AResourceWrapper(NS_RDF + "type");
      AResource subject;
      TopicIF reifier = assoc.getReifier();
      if (reifier == null || !filterOk(reifier)) {
        subject = getResource();
      } else {
        subject = getResource(reifier);
      }
      
      handler.statement(subject, type, getResource(assoc.getType()));
      assertScope(subject, assoc.getScope());
      
      Iterator it2 = assoc.getRoles().iterator();
      while (it2.hasNext()) {
        AssociationRoleIF role = (AssociationRoleIF) it2.next();
        handler.statement(subject,
                          getResource(role.getType()),
                          getResource(role.getPlayer()));
      }
    }
  }

  // --- Actual RDF generation

  private void statement(AResource subj, AResource pred, Object obj,
                         ScopedIF tmconstruct) {
    boolean assert_unreified = true;
    AResource statement = null;
    
    if (preserve_reification) {
      if (tmconstruct instanceof ReifiableIF) {
        TopicIF reifier = ((ReifiableIF)tmconstruct).getReifier();
        if (reifier != null && filterOk(reifier)) {
          statement = getResource(reifier);
          assertReified(statement, subj, pred, obj);
        }
      }
    }

    if (preserve_scope) {
      Collection scope = tmconstruct.getScope();
      if (!scope.isEmpty()) {
        if (statement == null) {
          statement = getResource();
          assertReified(statement, subj, pred, obj);
        }
        assertScope(statement, scope);
        assert_unreified = false; // it has scope, so don't assert it
      }
    }

    if (assert_unreified) {
      if (obj instanceof AResource) {
        handler.statement(subj, pred, (AResource) obj);
      } else {
        handler.statement(subj, pred, (ALiteral) obj);
      }
    }
  }

  private void assertReified(AResource statement,
                             AResource subject,
                             AResource predicate,
                             Object object) {
    handler.statement(statement, getResource(NS_RDF + "subject"), subject);
    handler.statement(statement, getResource(NS_RDF + "predicate"), predicate);
    handler.statement(statement, 
                      getResource(NS_RDF + "type"),
                      getResource(NS_RDF + "Statement"));

    if (object instanceof AResource) {
      handler.statement(statement, getResource(NS_RDF + "object"), (AResource)object);
    } else {
      handler.statement(statement, getResource(NS_RDF + "object"), (ALiteral)object);
    }
  }

  private void assertScope(AResource statement, Collection scope) {
    Iterator it = scope.iterator();
    AResource inscope = getResource(NS_TM + "inscope");
    while (it.hasNext()) {
      handler.statement(statement, inscope, getResource((TopicIF) it.next()));
    }
  }

  // --- Internal methods

  protected void setup(TopicMapIF topicmap) {
    QueryProcessorIF proc = QueryUtils.getQueryProcessor(topicmap);
    
    try {
      namepreds = new HashMap();
      
      QueryResultIF result = proc.execute(
        "using tm for i\"http://psi.ontopia.net/tm2rdf/#\" " +
        "tm:name-property($TYPE : tm:type, $PROP : tm:property)?");

      while (result.next()) {
        namepreds.put(getResource((TopicIF) result.getValue("TYPE")),
                      getResource((TopicIF) result.getValue("PROP")));
      }

      result.close();
    } catch (InvalidQueryException e) {
    }

    try {
      preferred_roles = new HashMap();
      
      QueryResultIF result = proc.execute(
        "using tm for i\"http://psi.ontopia.net/tm2rdf/#\" " +
        "tm:preferred-role($ATYPE : tm:association-type, $RTYPE : tm:role-type)?");

      while (result.next()) {
        preferred_roles.put(result.getValue("ATYPE"), result.getValue("RTYPE"));
      }

      result.close();
    } catch (InvalidQueryException e) {
    }    
  }
  
  private AResource getResource(TopicIF topic) {
    LocatorIF locator = null;
    if (locator == null && !topic.getSubjectLocators().isEmpty()) {
      locator = (LocatorIF) topic.getSubjectLocators().iterator().next();
    }
    if (locator == null && !topic.getSubjectIdentifiers().isEmpty()) {
      locator = (LocatorIF) topic.getSubjectIdentifiers().iterator().next();
    }
    if (locator != null) {
      return new AResourceWrapper(locator.getExternalForm());
    }

    return makeAnonymousNode(topic);
  }

  private AResource getResource(LocatorIF locator) {
    return new AResourceWrapper(locator.getExternalForm());
  }

  private AResource getResource() {
    return new AnonymousResource(Integer.toString(System.identityHashCode(new Object())));
  }

  private AResource getResource(String uri) {
    return new AResourceWrapper(uri);
  }

  private ALiteral getLiteral(String value) {
    return new ALiteralWrapper(value);
  }

  private AResource makeAnonymousNode(TopicIF topic) {
    return new AnonymousResource(Integer.toString(System.identityHashCode(topic)));
  }

  private TopicIF getTopic(TopicMapIF topicmap, String fragment) {
    try {
      LocatorIF loc = new URILocator(NS_TM2RDF + fragment);
      return topicmap.getTopicBySubjectIdentifier(loc);
    } catch (URISyntaxException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  // --- Resource wrapper
  
  static class AResourceWrapper implements AResource {
    public String uri;

    public AResourceWrapper(String uri) {
      this.uri = uri;
    }
    
    @Override
    public boolean isAnonymous() {
      return false;
    }

    @Override
    public String getAnonymousID() {
      return null;
    }

    @Override
    public String getURI() {
      return uri;
    }

    @Override
    public Object getUserData() {
      return null;
    }

    @Override
    public void setUserData(Object d) {
      // no-op
    }

    @Override
    public int hashCode() {
      return uri.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof AResource) {
        return uri.equals(((AResource) obj).getURI());
      } else {
        return false;
      }
    }

    @Override
    public String toString() {
      return "<" + uri + ">";
    }

    @Override
    public boolean hasNodeID() {
      return false;
    }    
  }

  static class AnonymousResource implements AResource {
    public String anonid;

    public AnonymousResource(String anonid) {
      this.anonid = anonid;
    }
    
    @Override
    public boolean isAnonymous() {
      return true;
    }

    @Override
    public String getAnonymousID() {
      return anonid;
    }

    @Override
    public String getURI() {
      return null;
    }

    @Override
    public Object getUserData() {
      return null;
    }

    @Override
    public void setUserData(Object d) {
      // no-op
    }

    @Override
    public int hashCode() {
      return anonid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof AResource) {
        return anonid.equals(((AResource) obj).getAnonymousID());
      } else {
        return false;
      }
    }

    @Override
    public String toString() {
      return "<<<" + anonid + ">>>";
    }

    @Override
    public boolean hasNodeID() {
      return true;
    }    
  }  

  // --- Literal wrapper

  class ALiteralWrapper implements ALiteral {
    private String value;
    private boolean tainted;

    public ALiteralWrapper(String value) {
      this.value = value;
    }
    
    @Override
    public boolean isWellFormedXML() {
      return false;
    }

    @Override
    public String toString() {
      return value;
    }

    @Override
    public String getLang() {
      return null;
    }

    @Override
    public String getDatatypeURI() {
      return null;
    }

    @Override
    public void taint() {
      tainted = true;
    }
  
    @Override
    public boolean isTainted() {
      return tainted;
    }
  }

  // --- Jena model-building StatementHandler

  static class ModelBuildingHandler implements StatementHandler {
    private Model model;

    public ModelBuildingHandler(Model model) {
      this.model = model;
    }
    
    @Override
    public void statement(AResource subj, AResource pred, AResource obj) {
      model.add(convert(subj), convertPred(pred), convert(obj));
    }
    
    @Override
    public void statement(AResource subj, AResource pred, ALiteral lit) {
      model.add(convert(subj), convertPred(pred), convert(lit));
    }

    private Literal convert(ALiteral lit) {
      String dt = lit.getDatatypeURI();
      if (dt == null) {
        return model.createLiteral(lit.toString(), lit.getLang());
      } else {
        return model.createTypedLiteral(lit.toString(), dt);
      }
    }

    private Resource convert(AResource r) {
      if (r.isAnonymous()) {
        return model.createResource(new StringAnonId(r.getAnonymousID()));
      } else {
        return model.createResource(r.getURI());
      }
    }

    private Property convertPred(AResource r) {
      if (r.isAnonymous()) {
        return model.createProperty("http://anonymous.ontopia.net/#id" +
                r.getAnonymousID());
      } else {
        return model.createProperty(r.getURI());
      }
    }
    
  }

  // --- AnonId implementation

  static class StringAnonId extends AnonId {
    private String id;

    public StringAnonId(String id) {
      this.id = id;
    }

    @Override
    public int hashCode() {
      return id.hashCode();
    }
    
    @Override
    public boolean equals(Object object) {
      return (object instanceof StringAnonId &&
              object.toString().equals(id));
    }

    @Override
    public String toString() {
      return id;
    }
    
    // required to avoid RDF/XML from creating multiple anonymous nodes for the same topic in jena > 2
    @Override
    public String getLabelString() {
      return id;
    }
  }
  
  /**
   * Sets additional properties for the RDFTopicMapWriter. Accepted properties:
   * <ul><li>'preserveReification' (Boolean), corresponds to 
   * {@link #setPreserveReification(boolean)}</li>
   * <li>'preserveScope' (Boolean), corresponds to {@link #setPreserveScope(boolean)}</li>
   * <li>'filter' (DeciderIF), corresponds to {@link #setFilter(net.ontopia.utils.DeciderIF)}</li>
   * </ul>
   * @param properties 
   */
  @Override
  public void setAdditionalProperties(Map<String, Object> properties) {
    Object value = properties.get(PROPERTY_PRESERVE_REIFICATION);
    if ((value != null) && (value instanceof Boolean)) {
      setPreserveReification((Boolean) value);
    }
    value = properties.get(PROPERTY_PRESERVE_SCOPE);
    if ((value != null) && (value instanceof Boolean)) {
      setPreserveScope((Boolean) value);
    }
    value = properties.get(PROPERTY_FILTER);
    if ((value != null) && (value instanceof Predicate)) {
      setFilter((Predicate) value);
    }
  }
}
