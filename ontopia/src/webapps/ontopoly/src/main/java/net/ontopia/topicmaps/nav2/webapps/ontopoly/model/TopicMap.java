
// $Id: TopicMap.java,v 1.11 2009/04/30 09:53:42 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.webapps.ontopoly.model;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.sysmodel.OntopolyRepository;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.sysmodel.TopicMapReference;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.utils.OntopolyModelUtils;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.QueryWrapper;
import net.ontopia.topicmaps.query.utils.RowMapperIF;
import net.ontopia.topicmaps.utils.MergeUtils;
import net.ontopia.topicmaps.utils.TopicStringifiers;
import net.ontopia.topicmaps.xml.XTMTopicMapReference;
import net.ontopia.utils.CollectionUtils;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Represents an Ontopoly topic map.
 */
public class TopicMap {

  static final String ON = "http://psi.ontopia.net/ontology/";
  static final String XTM = "http://www.topicmaps.org/xtm/1.0/core.xtm#";
  static final String TEST = "http://psi.example.org/test/";
  static final String TECH = "http://www.techquila.com/psi/hierarchy/#";
  static final String DC = "http://purl.org/dc/elements/1.1/";
  static final String XSD = "http://www.w3.org/2001/XMLSchema#";

  private static final String declarations = 
		  "using xtm for i\"" + XTM + "\" "
		+ "using on for i\"" + ON + "\" " 
		+ "using test for i\"" + TEST + "\" "
		+ "using tech for i\"" + TECH + "\" " 
		+ "using dc for i\"" + DC + "\" ";

  private OntopolyRepository repository;

  private TopicMapIF topicMapIF;

  private QueryWrapper queryWrapper;

  private String topicMapId;

  public TopicMap(TopicMapReference topicMapReference) {
    this.repository = topicMapReference.getRepository();
    this.topicMapId = topicMapReference.getId();
    try {
      this.topicMapIF = repository.getTopicMapRepository().getReferenceByKey(
          topicMapId).createStore(false).getTopicMap();
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }

    // initialize query wrapper
    initQueryWrapper();
  }

  public TopicMap(OntopolyRepository repository, TopicMapIF topicMapIF,
      String topicMapId) {
    this.repository = repository;
    this.topicMapIF = topicMapIF;
    this.topicMapId = topicMapId;

    // initialize query wrapper
    initQueryWrapper();
  }

  private void initQueryWrapper() {
    // initialize query wrapper
    queryWrapper = new QueryWrapper(getTopicMapIF());

    // load built-in declarations
    queryWrapper.setDeclarations(declarations);

    // load custom declarations
    TopicIF typeIf = OntopolyModelUtils.getTopicIF(this, PSI.ON, "tolog-declarations", false);
    if (typeIf != null) {
      TopicIF topicIf = getTopicMapIF().getReifier();
      if (topicIf != null) {
        OccurrenceIF occ = OntopolyModelUtils.findOccurrence(typeIf, topicIf);
        if (occ != null)
          queryWrapper.setDeclarations(occ.getValue());
      }
    }
  }

  public boolean containsOntology() {
    return (getTopicMapIF().getTopicBySubjectIdentifier(PSI.ON_ONTOLOGY_VERSION) != null);
  }

  public boolean isDeleteable() {
    return getTopicMapIF().getStore().getReference().getSource()
        .supportsDelete();
  }

  public TopicMapIF getTopicMapIF() {
    return topicMapIF;
  }

  public QueryWrapper getQueryWrapper() {
    return queryWrapper;
  }

  public OntopolyRepository getOntopolyRepository() {
    return repository;
  }

  public TopicIF getTopicIFById(String id) {
    // look up topic by object id or subject identifier
    TopicIF topic = (TopicIF)getTopicMapIF().getObjectById(id);
    if (topic == null) {
      try {
        return topicMapIF.getTopicBySubjectIdentifier(URILocator.create(id));
      } catch (Exception e) {
        return null;
      }
    }
    return topic;
  }

  public Topic getTopicById(String id) {
    TopicIF topic = getTopicIFById(id);
    return new Topic(topic, this);
  }

  public Topic getReifier() {
    return new Topic(makeReifier(), this);
  }
  
  protected TopicIF makeReifier() {
    TopicIF reifier = getTopicMapIF().getReifier();
    if (reifier == null) {
      // IMPORTANT: check old-style reification
      TopicMapIF tm = getTopicMapIF();
      Iterator iter = tm.getItemIdentifiers().iterator();
      while (iter.hasNext()) {
        LocatorIF srcloc = (LocatorIF) iter.next();
        TopicIF _reifier = tm.getTopicBySubjectIdentifier(srcloc);
        if (_reifier != null) {
          if (reifier != null)
            MergeUtils.mergeInto(reifier, _reifier);
          else
            reifier = _reifier;
        }
      }
      if (reifier == null)
        reifier = tm.getBuilder().makeTopic();
      tm.setReifier(reifier);
      reifier.addType(tm.getTopicBySubjectIdentifier(PSI.ON_TOPIC_MAP));
    }
    return reifier;
  }

  /**
   * Returns the name of the topic map, or null if it has none.
   */
  public String getName() {
    TopicIF reifier = getTopicMapIF().getReifier();
    return reifier == null ? null : TopicStringifiers.toString(reifier);
  }

  /**
   * Sets the name of the topic map, creating a new base name object if
   * necessary. Also updates the name in the system topic map and saves that
   * topic map.
   */
  public void setName(String _value) {
		String value = (_value == null ? "" : _value);
		TopicIF topic = makeReifier();
		// update existing new name or create a new one
		Collection names = OntopolyModelUtils.findTopicNames(null, topic, value,
																												 Collections.EMPTY_SET);
		Iterator iter = names.iterator();
		if (iter.hasNext()) {
			TopicNameIF bn = (TopicNameIF) iter.next();
			bn.setValue(value);
		} else {
			getTopicMapIF().getBuilder().makeTopicName(topic, value);
		}
		// remove superflous names
		while (iter.hasNext())
			((TopicNameIF) iter.next()).remove();
		// update ontopoly repository if there is one
		if (repository != null) {
			TopicMapReference ref = repository.getReference(topicMapId);
			ref.setName(value);
		}
  }

  /**
   * Returns the version of the Ontopoly meta-ontology used in this topic map.
   */
  public float getOntologyVersion() {
    TopicIF reifier = getTopicMapIF().getReifier();
    if (reifier == null) return 0;
    TopicIF ontologyVersion = getTopicMapIF().getTopicBySubjectIdentifier(PSI.ON_ONTOLOGY_VERSION);
    Collection occs = OntopolyModelUtils.findOccurrences(ontologyVersion,
        reifier, Collections.EMPTY_SET);
    if (occs.isEmpty())
      return 0;
 
    String versionNumber = ((OccurrenceIF) occs.iterator().next()).getValue();
    try {
      return Float.parseFloat(versionNumber);
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  /**
   * INTERNAL: Sets the version of the Ontopoly meta-ontology used in this topic map. Used by code that performs upgrades.
   */
  public void setOntologyVersion(float value) {
    String versionNumber = Float.toString(value);
    TopicIF reifier = makeReifier();
    TopicIF ontologyVersion = getTopicMapIF().getTopicBySubjectIdentifier(PSI.ON_ONTOLOGY_VERSION);
    Collection occs = OntopolyModelUtils.findOccurrences(ontologyVersion,
        reifier, Collections.EMPTY_SET);
    Iterator iter = occs.iterator();
    if (iter.hasNext()) {
      OccurrenceIF occ = (OccurrenceIF) iter.next();
      occ.setValue(versionNumber);
    } else {
      getTopicMapIF().getBuilder().makeOccurrence(reifier, ontologyVersion, versionNumber);
    }
    // remove superflous occurrences
    while (iter.hasNext())
      ((OccurrenceIF) iter.next()).remove();
  }

  /**
   * Returns the Id of the topic map reference in the topic map registry.
   */
  public String getId() {
    return topicMapId;
  }

//  /* get Types */
//
//  public List getSystemTopicTypes() {
//    String query = "select $type from "
//        + "{ instance-of($type, on:ontology-type) | $type = on:ontology-type }?";
//
//    List result = getQueryWrapper().queryForList(query,
//        OntopolyModelUtils.getRowMapperOneColumn());
//
//    return castResultObjects(result, TopicType.class);
//  }

  /**
   * Returns a list of the topic types that is not a system topic type.
   */
  public List getTopicTypes() {
    String query = "select $type from "
			+ "instance-of($type, on:topic-type)"
			//! + ", not (instance-of($type, on:ontology-type)), $type /= on:ontology-type"
			+ " order by $type?";

    List result = getQueryWrapper().queryForList(query,
        OntopolyModelUtils.getRowMapperOneColumn());

    return castResultObjects(result, TopicType.class);
  }

  /**
   * Returns a list of the topic types that is not a system topic type.
   */
  public List getTopicTypesWithLargeInstanceSets() {
    String query = "select $type from on:has-large-instance-set($type : on:topic-type)?";

    List result = getQueryWrapper().queryForList(query,
        OntopolyModelUtils.getRowMapperOneColumn());

    return castResultObjects(result, TopicType.class);
  }

  public List getOccurrenceTypes() {
    String query = "select $type from "
			+ "instance-of($type, on:occurrence-type)"
			//! + ", not (instance-of($type, on:system-topic))"
			+ " order by $type?";

    List result = getQueryWrapper().queryForList(query,
        OntopolyModelUtils.getRowMapperOneColumn());

    List types = new ArrayList();
    for (int i = 0; i < result.size(); i++) {
      types.add(new OccurrenceType((TopicIF) result.get(i), this));
    }
    return types;
  }

  public List getOccurrenceFields() {
    String query = "select $field from direct-instance-of($field, on:occurrence-field) order by $field?";

    List result = getQueryWrapper().queryForList(query,
        OntopolyModelUtils.getRowMapperOneColumn());

    List fields = new ArrayList();
    for (int i = 0; i < result.size(); i++) {
      fields.add(new OccurrenceField((TopicIF) result.get(i), this));
    }
    return fields;
  }

  public List getAssociationTypes() {
    String query = "select $type from "
			+ "instance-of($type, on:association-type)"
			//! + ", not(instance-of($type, on:system-topic))" 
			+ " order by $type?";

    List result = getQueryWrapper().queryForList(query,
        OntopolyModelUtils.getRowMapperOneColumn());

    List types = new ArrayList();
    for (int i = 0; i < result.size(); i++) {
      types.add(new AssociationType((TopicIF) result.get(i), this));
    }
    return types;
  }

  public List getRoleTypes(boolean includeSystemTopics) {
    String query = "";
    if (includeSystemTopics)
      query = "select $type from direct-instance-of($type, on:role-type) order by $type?";
    else
      query = "select $type from "
				+ "direct-instance-of($type, on:role-type)"
				+ ", not(instance-of($type, on:system-topic))"
				+ " order by $type?";

    List result = getQueryWrapper().queryForList(query,
        OntopolyModelUtils.getRowMapperOneColumn());

    List types = new ArrayList();
    for (int i = 0; i < result.size(); i++) {
      types.add(new RoleType((TopicIF) result.get(i), this));
    }
    return types;
  }

  public List getRoleFields() {
    String query = "select $field from direct-instance-of($field, on:role-field) order by $field?";

    List result = getQueryWrapper().queryForList(query,
        OntopolyModelUtils.getRowMapperOneColumn());

    List fields = new ArrayList();
    for (int i = 0; i < result.size(); i++) {
      fields.add(new RoleField((TopicIF) result.get(i), this));
    }
    return fields;
  }

  public List getNameTypes() {
    String query = "select $type from direct-instance-of($type, on:name-type) order by $type?";

    List result = getQueryWrapper().queryForList(query,
        OntopolyModelUtils.getRowMapperOneColumn());

    List types = new ArrayList();
    for (int i = 0; i < result.size(); i++) {
      types.add(new NameType((TopicIF) result.get(i), this));
    }
    return types;
  }

  public List getNameFields() {
    String query = "select $field from direct-instance-of($field, on:name-field) order by $field?";

    List result = getQueryWrapper().queryForList(query,
        OntopolyModelUtils.getRowMapperOneColumn());

    List fields = new ArrayList();
    for (int i = 0; i < result.size(); i++) {
      fields.add(new NameField((TopicIF) result.get(i), this));
    }
    return fields;
  }

  public List getIdentityTypes() {
    String query = "select $type from instance-of($type, on:identity-type) order by $type?";

    List result = getQueryWrapper().queryForList(query,
        OntopolyModelUtils.getRowMapperOneColumn());

    List types = new ArrayList();
    for (int i = 0; i < result.size(); i++) {
      types.add(new IdentityType((TopicIF) result.get(i), this));
    }
    return types;
  }

  public List getIdentityFields() {
    String query = "select $field from instance-of($field, on:identity-field) order by $field?";

    List result = getQueryWrapper().queryForList(query,
        OntopolyModelUtils.getRowMapperOneColumn());

    List fields = new ArrayList();
    for (int i = 0; i < result.size(); i++) {
      fields.add(new IdentityField((TopicIF) result.get(i), this));
    }
    return fields;
  }

  public boolean isSaveable() {
    TopicMapReferenceIF ref = getTopicMapIF().getStore().getReference();
    return (ref instanceof XTMTopicMapReference);
  }

  public void save() {
    TopicMapReferenceIF ref = getTopicMapIF().getStore().getReference();
    if (ref instanceof XTMTopicMapReference) {
      try {
        ((XTMTopicMapReference) ref).save();
      } catch (IOException e) {
        throw new OntopiaRuntimeException(e);
      }
    }
  }

  TopicIF createNamedTopic(String name, TopicIF type) {
    TopicMapBuilderIF builder = getTopicMapIF().getBuilder();
    TopicIF topic = builder.makeTopic(type);

    if (name != null && !name.equals(""))
      builder.makeTopicName(topic, name);

    return topic;
  }

  public TopicType createTopicType(String name) {

    TopicIF topicTypeIf= OntopolyModelUtils.getTopicIF(this, PSI.ON, "topic-type");
    TopicIF topicIf = createNamedTopic(name, topicTypeIf);

    //! // make has_field association
    //! TopicIF aType = OntopolyModelUtils.getTopicIF(this, PSI.ON, "has-field");
    //! TopicIF type1 = OntopolyModelUtils.getTopicIF(this, PSI.ON, "field-owner");
    //! TopicIF type2 = OntopolyModelUtils.getTopicIF(this, PSI.ON, "field-definition");
    //! TopicIF untypedName = OntopolyModelUtils.getTopicIF(this, PSI.ON, "untyped-name");
    //! OntopolyModelUtils.makeBinaryAssociation(aType, topic, type1, untypedName,
    //!     type2);
		//! 
    //! // add field-order occurrence with theme (scope)
    //! TopicMapBuilderIF builder = getTopicMapIF().getBuilder();
    //! OccurrenceIF occurrenceIf = builder.makeOccurrence(topic,
    //!     OntopolyModelUtils.getTopicIF(this, PSI.ON, "field-order"), "000000001");
    //! occurrenceIf.addTheme(untypedName);

    //! // make has-cardinality association
    //! TopicIF hasCardinality = OntopolyModelUtils.getTopicIF(this, PSI.ON, "has-cardinality");
    //! TopicIF fieldOwner = OntopolyModelUtils.getTopicIF(this, PSI.ON, "field-owner");
    //! TopicIF fieldDefinition = OntopolyModelUtils.getTopicIF(this, PSI.ON, "field-definition");
    //! TopicIF cardinality = OntopolyModelUtils.getTopicIF(this, PSI.ON, "cardinality");
    //! TopicIF cardinality11 = OntopolyModelUtils.getTopicIF(this, PSI.ON, "cardinality-1-1");
    //! OntopolyModelUtils.makeTernaryAssociation(hasCardinality, 
		//! 																					topic, fieldOwner, 
		//! 																					untypedName, fieldDefinition, 
		//! 																					cardinality11, cardinality);

    TopicType topicType = new TopicType(topicIf, this);
    topicType.addField(getDefaultNameField());
    return topicType;
  }

  protected NameField getDefaultNameField() {
    TopicMap tm = this;
    NameType nameType = new NameType(OntopolyModelUtils.getTopicIF(tm, PSI.ON_UNTYPED_NAME), tm);
    Collection nameFields = nameType.getDeclaredByFields();
    return (NameField)CollectionUtils.getFirstElement(nameFields);
  }
  
	public IdentityField getIdentityField(IdentityType identityType) {
		String query = "select $FD from on:has-identity-type(%type% : on:identity-type, $FD : on:identity-field) limit 1?";
		Map params = Collections.singletonMap("type", identityType.getTopicIF());

		TopicIF fieldTopic = (TopicIF)getQueryWrapper().queryForObject(query, params);
		if (fieldTopic == null) 
			throw new OntopolyModelRuntimeException("Could not find identity field for " + identityType);

    return new IdentityField(fieldTopic, this, identityType);
	}

  public NameType createNameType(String name) {
    TopicMap tm = this;
    TopicMapBuilderIF builder = tm.getTopicMapIF().getBuilder();

    // create name type
    TopicIF nameTypeTopic = createNamedTopic(name, OntopolyModelUtils.getTopicIF(tm, PSI.ON_NAME_TYPE));
    NameType nameType = new NameType(nameTypeTopic, tm);

    // create name field
    TopicIF nameFieldType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "name-field");
    TopicIF nameFieldTopic = builder.makeTopic(nameFieldType);
    
    // on:has-name-type($TT : on:name-type, $FD : on:name-field)
    final TopicIF HAS_NAME_TYPE = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "has-name-type");
    final TopicIF NAME_TYPE = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "name-type");
    final TopicIF NAME_FIELD = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "name-field");
    OntopolyModelUtils.makeBinaryAssociation(HAS_NAME_TYPE, 
        nameTypeTopic, NAME_TYPE,
        nameFieldTopic, NAME_FIELD);

    // TODO: add default cardinality

    return nameType;
  }

	public NameField getNameField(NameType nameType) {
		String query = "select $FD from on:has-name-type(%type% : on:name-type, $FD : on:name-field) limit 1?";
		Map params = Collections.singletonMap("type", nameType.getTopicIF());

		TopicIF fieldTopic = (TopicIF)getQueryWrapper().queryForObject(query, params);
		if (fieldTopic == null) 
			throw new OntopolyModelRuntimeException("Could not find name field for " + nameType);

    return new NameField(fieldTopic, this, nameType);
	}

  public OccurrenceType createOccurrenceType(String name) {
    TopicMap tm = this;
    TopicMapBuilderIF builder = tm.getTopicMapIF().getBuilder();

    // create occurrence type
    TopicIF occurrenceTypeTopic = createNamedTopic(name, OntopolyModelUtils.getTopicIF(tm, PSI.ON, "occurrence-type"));
    OccurrenceType occurrenceType = new OccurrenceType(occurrenceTypeTopic, tm);

    // create occurrence field
    TopicIF occurrenceFieldType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "occurrence-field");
    TopicIF occurrenceFieldTopic = builder.makeTopic(occurrenceFieldType);
    
    // on:has-occurrence-type($TT : on:occurrence-type, $FD : on:occurrence-field)
    final TopicIF HAS_OCCURRENCE_TYPE = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "has-occurrence-type");
    final TopicIF OCCURRENCE_TYPE = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "occurrence-type");
    final TopicIF OCCURRENCE_FIELD = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "occurrence-field");
    OntopolyModelUtils.makeBinaryAssociation(HAS_OCCURRENCE_TYPE, 
        occurrenceTypeTopic, OCCURRENCE_TYPE,
        occurrenceFieldTopic, OCCURRENCE_FIELD);

    // TODO: add default datatype and cardinality

    //! TopicIF aType = OntopolyModelUtils.getTopicIF(this, PSI.ON, "has-datatype");
    //! TopicIF type1 = OntopolyModelUtils.getTopicIF(this, PSI.ON, "datatype");
    //! TopicIF type2 = OntopolyModelUtils.getTopicIF(this, PSI.ON, "field-definition");
		//! 
    //! OntopolyModelUtils.makeBinaryAssociation(aType, ((DataType) DataType
    //!     .getDefaultDataType(this)).getTopicIF(), type1, topicIF, type2);

    return occurrenceType;
  }

	public OccurrenceField getOccurrenceField(OccurrenceType occurrenceType) {
		String query = "select $FD from on:has-occurrence-type(%type% : on:occurrence-type, $FD : on:occurrence-field) limit 1?";
		Map params = Collections.singletonMap("type", occurrenceType.getTopicIF());

		TopicIF fieldTopic = (TopicIF)getQueryWrapper().queryForObject(query, params);
		if (fieldTopic == null) 
			throw new OntopolyModelRuntimeException("Could not find occurrence field for " + occurrenceType);

    return new OccurrenceField(fieldTopic, this, occurrenceType);
	}

  public AssociationType createAssociationType(String name) {
		TopicMap tm = this;
    TopicMapBuilderIF builder = tm.getTopicMapIF().getBuilder();

    // create association type
    TopicIF associationTypeTopic = createNamedTopic(name, OntopolyModelUtils.getTopicIF(tm, PSI.ON, "association-type"));
    AssociationType associationType = new AssociationType(associationTypeTopic, tm);

    // create association field
    TopicIF associationFieldType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "association-field");
    TopicIF associationFieldTopic = builder.makeTopic(associationFieldType);
    
    // on:has-association-type($TT : on:association-type, $FD : on:association-field)
    final TopicIF HAS_ASSOCIATION_TYPE = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "has-association-type");
    final TopicIF ASSOCIATION_TYPE = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "association-type");
    final TopicIF ASSOCIATION_FIELD = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "association-field");
    OntopolyModelUtils.makeBinaryAssociation(HAS_ASSOCIATION_TYPE, 
        associationType.getTopicIF(), ASSOCIATION_TYPE,
        associationFieldTopic, ASSOCIATION_FIELD);

    final TopicIF HAS_ASSOCIATION_FIELD = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "has-association-field");
    final TopicIF ROLE_FIELD = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "role-field");
    TopicIF roleFieldType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "role-field");

    // create first role field
    TopicIF roleFieldTopic1 = builder.makeTopic(roleFieldType);
    
    // on:has-association-field($AF : on:association-field, $FD : on:role-field)
    OntopolyModelUtils.makeBinaryAssociation(HAS_ASSOCIATION_FIELD, 
				roleFieldTopic1, ROLE_FIELD,
        associationFieldTopic, ASSOCIATION_FIELD);

    // create second role field
    TopicIF roleFieldTopic2 = builder.makeTopic(roleFieldType);
    
    // on:has-association-field($AF : on:association-field, $FD : on:role-field)
    OntopolyModelUtils.makeBinaryAssociation(HAS_ASSOCIATION_FIELD, 
				roleFieldTopic2, ROLE_FIELD,
        associationFieldTopic, ASSOCIATION_FIELD);

    // TODO: add default cardinality

    return associationType;
  }

  public AssociationField getAssociationField(AssociationType atype) {

		String query = "select $AF from "
			+ "on:has-association-type(%atype% : on:association-type, $AF : on:association-field) limit 1?";
		Map params = Collections.singletonMap("atype", atype.getTopicIF());
		TopicIF fieldTopic = (TopicIF)getQueryWrapper().queryForObject(query, params);
		if (fieldTopic == null) 
			throw new OntopolyModelRuntimeException("Could not find association field for " + atype);

    return new AssociationField(fieldTopic, this, atype);
  }

  public RoleType createRoleType(String name) {
    TopicIF type = OntopolyModelUtils.getTopicIF(this, PSI.ON, "role-type");
    return new RoleType(createNamedTopic(name, type), this);
  }

  public RoleField getRoleField(final AssociationType atype, final RoleType rtype) {

		String query = "select $AF, $RF from "
			+ "on:has-association-type(%atype% : on:association-type, $AF : on:association-field), " 
			+ "on:has-association-field($AF : on:association-field, $RF : on:role-field), " 
			+ "on:has-role-type($RF : on:role-field, %rtype% : on:role-type) limit 1?";
		Map params = new HashMap(2);
		params.put("atype", atype.getTopicIF());
		params.put("rtype", rtype.getTopicIF());

    RoleField roleField = (RoleField) getQueryWrapper().queryForObject(query,
        new RowMapperIF() {
          public Object mapRow(QueryResultIF result, int rowno) {
						TopicIF associationFieldTopic = (TopicIF)result.getValue(0);
						TopicIF roleFieldTopic = (TopicIF)result.getValue(1);
						return new RoleField(roleFieldTopic, TopicMap.this, rtype, new AssociationField(associationFieldTopic, TopicMap.this, atype));
					}
				}, params);

		if (roleField == null) 
			throw new OntopolyModelRuntimeException("Could not find field for " + atype + " and " + rtype);

    return roleField;
  }

  private List castResultObjects(List result, Class castObjectClass) {
    List castedResultObjects = new ArrayList();

    Constructor[] constructors = castObjectClass.getConstructors();
    Constructor constructor = null;

    // Find correct constructor; constructor with two arguments
    for (int i = 0; i < constructors.length; i++) {
      if (constructors[i].getParameterTypes().length == 2) {
        constructor = constructors[i];
        break;
      }
    }

    if (constructor == null) {
      throw new OntopolyModelRuntimeException("Couldn't find constructor for the class: " + castObjectClass);
    }

    // Cast result object to desired class
    Iterator it = result.iterator();
    while (it.hasNext()) {
      try {
        castedResultObjects.add((constructor.newInstance(new Object[] {
            (TopicIF) it.next(), this })));

      } catch (Exception e) {
        throw new OntopolyModelRuntimeException(e);
      }
    }

    return castedResultObjects;
  }

  /**
   * Returns the topics that matches the given search term. Only topics of
   * allowed player types are returned.
   * 
   * @return a collection of Topic objects
   */
  public Collection searchAll(String searchTerm) {
    String query = "select $topic, $score from "
        + "topic-name($topic, $tn), value-like($tn, %searchTerm%, $score) "
        + "order by $score desc, $topic?";

    Map params = new HashMap();
    params.put("searchTerm", searchTerm);

    Collection rows = getQueryWrapper().queryForList(query,
        OntopolyModelUtils.getRowMapperOneColumn(), params);

    Iterator it = rows.iterator();
    Collection results = new ArrayList(rows.size());
    Collection duplicateChecks = new HashSet(rows.size());
    while (it.hasNext()) {
      TopicIF topic = (TopicIF) it.next();
      if (duplicateChecks.contains(topic))
        continue; // avoid duplicates
      results.add(new Topic(topic, this));
      duplicateChecks.add(topic);
    }

    return results;
  }

}
