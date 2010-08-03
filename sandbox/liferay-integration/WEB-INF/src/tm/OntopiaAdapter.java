package tm;

import java.io.InputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import com.liferay.portal.model.Group;
import com.liferay.portal.model.User;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.model.JournalStructure;
import com.liferay.portlet.wiki.model.WikiNode;
import com.liferay.portlet.wiki.model.WikiPage;
import com.liferay.portal.kernel.exception.SystemException;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.GenericLocator;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.entry.TopicMaps;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.topicmaps.query.utils.QueryWrapper;
import net.ontopia.topicmaps.utils.TopicMapSynchronizer;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import util.DateFormatter;

/**
 * The Liferay/Ontopia data integration, which builds a topic map
 * reflecting the data in Liferay, allowing web content, wiki pages,
 * and so on to be annotated in the topic map.
 */
public class OntopiaAdapter implements OntopiaAdapterIF {
  private static Logger log = LoggerFactory.getLogger(OntopiaAdapter.class);

  // this prefix is pretty much the same in all tolog queries
  private static final String PSI_PREFIX = "http://psi.ontopia.net/liferay/";

  // these are referenced from other classes, but not in this one
  public static final String ASSOC_CREATED_BY_PSI = PSI_PREFIX + "created_by";
  public static final String ASSOC_USER_APPROVING_PSI = PSI_PREFIX + "approved_by";
  public static final String ASSOC_HAS_WORKFLOW_STATE_PSI = PSI_PREFIX + "has_workflow_state";
  public static final String SUB_SUPERTYPE_PSI = "http://psi.topicmaps.org/iso13250/model/supertype-subtype";
  public static final String ASSOC_CONTAINS_PSI = PSI_PREFIX + "contains";
  public static final String ASSOC_PARENT_CHILD_PSI = PSI_PREFIX + "parent-child";
  public static final String ASSOC_IS_ABOUT_PSI = PSI_PREFIX + "is-about";

  // constants
  private static final String TOPICMAPNAMEKEY = "topicmapname";
  private static final String PROPERTYFILENAME ="ontopia.properties";

  // static members
  private static String tmName; // ID of the Liferay TM in the TM registry

  // dynamic members
  private TopicMapStoreIF store;
  private TopicMapIF topicmap;
  private QueryWrapper queryWrapper;
  
  /**
   * The constructor is private, so that instances can only be
   * produced via the getInstance() method. Note that the class is not
   * a singleton.
   */
  private OntopiaAdapter(boolean readonly) {
    configure();
    store = TopicMaps.createStore(tmName, readonly);
    topicmap = store.getTopicMap();
    queryWrapper = new QueryWrapper(topicmap);
    queryWrapper.setDeclarations("using lr for i\"" + PSI_PREFIX + "\" ");
  }

  /**
   * A special constructor used to build temporary in-memory topic maps for
   * the TMSync-based updates.
   */
  private OntopiaAdapter() {
    store = new InMemoryTopicMapStore();
    topicmap = store.getTopicMap();
    queryWrapper = new QueryWrapper(topicmap);
    queryWrapper.setDeclarations("using lr for i\"" + PSI_PREFIX + "\" ");
  }
  
  private void configure() {
    if (tmName != null)
      return;
    
    Properties props = new Properties();
    // read topicmapName from properties file
    try {
      ClassLoader cloader = OntopiaAdapter.class.getClassLoader();
      InputStream istream = cloader.getResourceAsStream(PROPERTYFILENAME);
      if (istream == null) 
        throw new OntopiaRuntimeException("Couldn't load property file " +
                                          PROPERTYFILENAME + " from classpath");
      props.load(istream);
    } catch (IOException e) {
      log.warn("Couldn't load " + PROPERTYFILENAME, e);
      throw new OntopiaRuntimeException("Couldn't load " + PROPERTYFILENAME, e);
    }

    tmName = (String) props.getProperty(TOPICMAPNAMEKEY);
  }

  /**
   * Use this method to get modifiable instances of the adapter.
   */
  public static synchronized OntopiaAdapterIF getInstance() {
    return getInstance(false);
  }

  /**
   * Use this method to get modifiable or readonly instances of the
   * adapter.  Used by the JSPs, since they only need readonly access.
   */
  public static synchronized OntopiaAdapterIF getInstance(boolean readonly) {
    return new OntopiaAdapter(readonly);
  }
  
  // -----------------------------------
  // Implementing OntopiaAdapterIF
  // -----------------------------------

  public void addWebContent(JournalArticle content) {
    try {
      addWebContent(WrapperFactory.wrap(content));
      store.commit();
    } catch (Exception e) {
      store.abort();
      throw new OntopiaRuntimeException("Couldn't add article", e);
    } finally {
      store.close();
    }
  }

  public void deleteWebContent(String uuid) {
    try {
      deleteByUuid(uuid);
      store.commit();
    } catch (Exception e) {
      store.abort();
      throw new OntopiaRuntimeException("Couldn't delete article", e);
    } finally {
      store.close();
    }
  }

  public void updateWebContent(JournalArticle content) {
    try {
      update(WrapperFactory.wrap(content));
      store.commit();
    } catch (Exception e) {
      store.abort();
      throw new OntopiaRuntimeException("Couldn't update article", e);
    } finally {
      store.close();
    }
  }


  public void addUser(User user) {
    try {
      addUser_(user);
      store.commit();
    } catch (Exception e) {
      store.abort();
      throw new OntopiaRuntimeException("Couldn't add user", e);
    } finally {
      store.close();
    }
  }

  public void deleteUser(String uuid) {
    try {
      deleteByUuid(uuid);
      store.commit();
    } catch (Exception e) {
      store.abort();
      throw new OntopiaRuntimeException("Couldn't delete user", e);
    } finally {
      store.close();
    }
  }

  public void updateUser(User user) {
    try {
      update(user);
      store.commit();
    } catch (Exception e) {
      store.abort();
      throw new OntopiaRuntimeException("Couldn't update user", e);
    } finally {
      store.close();
    }
  }


  public void addStructure(JournalStructure structure) {
    try {
      // TODO: Shall structures have information on their group as well?
      addStructure_(structure); 
      store.commit();
    } catch (Exception e) {
      store.abort();
      throw new OntopiaRuntimeException("Couldn't add structure", e);
    } finally {
      store.close();
    }
  }

  public void deleteStructure(String uuid) {
    try {
      deleteByUuid(uuid);
      store.commit();
    } catch (Exception e) {
      store.abort();
      throw new OntopiaRuntimeException("Couldn't delete structure", e);
    } finally {
      store.close();
    }
  }

  public void updateStructure(JournalStructure structure) {
    try {
      update(structure);
      store.commit();
    } catch (Exception e) {
      store.abort();
      throw new OntopiaRuntimeException("Couldn't update structure", e);
    } finally {
      store.close();
    }
  }
  

  public void addWikiNode(WikiNode node) {
    try {
      addWikiNode_(node);
      store.commit();
    } catch (Exception e) {
      store.abort();
      throw new OntopiaRuntimeException("Couldn't add wiki node", e);
    } finally {
      store.close();
    }
  }

  public void deleteWikiNode(String uuid) {
    try {
      deleteByUuid(uuid);
      store.commit();
    } catch (Exception e) {
      store.abort();
      throw new OntopiaRuntimeException("Couldn't delete wiki node", e);
    } finally {
      store.close();
    }
  }

  public void updateWikiNode(WikiNode wikinode) {
    try {
      update(wikinode);
      store.commit();
    } catch (Exception e) {
      store.abort();
      throw new OntopiaRuntimeException("Couldn't update wiki node", e);
    } finally {
      store.close();
    }
  }


  public void addWikiPage(WikiPage wikipage) {
    try {
      addWikiPage_(wikipage);
      store.commit();
    } catch (Exception e) {
      store.abort();
      throw new OntopiaRuntimeException("Couldn't add wiki page", e);
    } finally {
      store.close();
    }
  }

  public void deleteWikiPage(String uuid) {
    try {
      deleteByUuid(uuid);
      store.commit();
    } catch (Exception e) {
      store.abort();
      throw new OntopiaRuntimeException("Couldn't delete wiki page", e);
    } finally {
      store.close();
    }
  }

  public void updateWikiPage(WikiPage wikipage) {
    try {
      update(wikipage);
      store.commit();
    } catch (Exception e) {
      store.abort();
      throw new OntopiaRuntimeException("Couldn't update wiki page", e);
    } finally {
      store.close();
    }
  }


  public void addGroup(Group group) {
    if (!group.isCommunity())
      return;
    
    try {
      // TODO: Handling of parent-groups?
      addCommunity_(group);
      store.commit();
    } catch (Exception e) {
      store.abort();
      throw new OntopiaRuntimeException("Couldn't add group", e);
    } finally {
      store.close();
    }
  }

  public void deleteGroup(Group group) {
    try {
      deleteGroup_(group);
      store.commit();
    } catch (Exception e) {
      store.abort();
      throw new OntopiaRuntimeException("Couldn't delete group", e);
    } finally {
      store.close();
    }
  }

  public void updateGroup(Group group) {
    if (!group.isCommunity())
      return;
    
    try {
      update(group);
      store.commit();
    } catch (Exception e) {
      store.abort();
      throw new OntopiaRuntimeException("Couldn't update group", e);
    } finally {
      store.close();
    }
  }

  // ----------------------------
  // static method
  // ----------------------------

  /**
   * Is called by DeciderIF implementations to check if the PSI
   * provided is the type of the Association provided
   *
   * @param psi The PSI as a String
   * @param assoc The association to check (as AssociationIF)
   * @return true/false
   */
  public static boolean isInAssociation(String psi, AssociationIF assoc) {
    TopicIF type = assoc.getType();
    try {
      LocatorIF locator = new URILocator(psi);
      Collection locators = type.getSubjectIdentifiers();
      if (locators.contains(locator))
        return true;

    } catch (MalformedURLException ex) {
      throw new OntopiaRuntimeException(ex);
    }
    return false;
  }


  // -------------------------------------------------------------------
  // The following methods are used by some of the JSP's in the portlets
  // -------------------------------------------------------------------
  
  /**
   * Returns the ObjectId for the topic identified by the uuid provided.
   *
   * @param uuid The UUID identifying the Object from Liferay
   * @return A String representing the objectId of the topic in the tm
   */
  public String getObjectIdForUuid(String uuid) {
    // May raise exception if topic can not be found. That's ok.
    TopicIF topic = retrieveTopicByUuid(uuid); 
    if (topic != null)
      return topic.getObjectId();
    else
      return null;
  }

  /**
   * Returns the identifier of the topicmap
   *
   * @return a String containing the identifier of the topicmap
   */
  public String getTopicMapId() {
    return tmName;
  }

  /**
   * Returns the objectId of the *first* type of a topic which is
   * identified by a uuid that it finds.
   *
   * @param uuid The uuid of the topic in question
   * @return A String containing the objectId for the type of the
   * topic in question
   */
  public String getTopicTypeIdForUuid(String uuid) {
    TopicIF topic = retrieveTopicByUuid(uuid);
    if (topic != null)
      for (TopicIF type : topic.getTypes())
        return type.getObjectId();
    return null;
  }

  /**
   * Returns the objectId of the "conceptView" topic by trying to look
   * up the PSI: http://psi.ontopia.net/liferay/conceptview
   * Will throw an exception if the PSI can not be found.
   *
   * @return The objectId of the "conceptView" topic
   */
  public String getConceptViewId() {
    TopicIF conceptView = topicmap.getTopicBySubjectIdentifier(new GenericLocator("uri", "http://psi.ontopia.net/liferay/conceptview"));
    return conceptView.getObjectId();
  }

  /**
   * Returns the object id for the topic that represents the article
   * that will be displayed, providing an articleId.  The rule is: The
   * topic, with the highest version AND which has an "approved"
   * state, is returned.
   *
   * @param articleId the article id from liferay
   * @return The object ID for the topic
   */
  public String getCurrentObjectIdForArticleId(String articleId) {
    String query =
      "select $id, $number from \n" +
      "object-id($topic, $id), \n"+
      "occurrence($topic , $aid), \n" +
      "type($aid, lr:article_id), \n" +
      "value($aid, \"" + articleId +"\"), \n" +
      "lr:has_workflow_state($topic : lr:work, lr:workflow_approved : lr:state), \n" +
      "occurrence($topic, $version), \n" +
      "type($version, lr:version), \n" +
      "value($version, $number) \n" +
      "order by $number desc?";

    String id = queryWrapper.queryForString(query);
    if (id == null)
      throw new OntopiaRuntimeException("No article with articleId " +
                                        articleId  + " found!");
    return id;
  }


  // ------------------------------------------
  // some non-public methods acting as helpers
  // ------------------------------------------

  /**
   * Creates an article and sets created_by and approved_by associations.
   */
  private void addWebContent(JournalArticleWrapper content)
    throws SystemException {
    createWebContent(content);
    setWorkflowstate(content);

    String creatorUuid = content.getUserUuid();
    setCreator(content.getUuid(), creatorUuid);

    if (content.isApproved())
      setUserApproving(content);

    setGroupContains(String.valueOf(content.getGroupId()), content.getUuid()); 
  }


  /**
   * Assembles one rather big tolog query to create a topic for a
   * <code>JournalArticle</code>
   */
  private void createWebContent(JournalArticleWrapper content)
    throws SystemException {
    Map<String, String> valueMap = getWebContentMap(content);
    String classname;
    if (content.getStructureId().equals(""))
      classname = "lr:article";
    else {
      classname = findStructureUrnByStructureId(content.getStructureId());
      if (classname == null)
        throw new OntopiaRuntimeException("Structure with id + " +
                                          content.getStructureId() +
                                          " not found!");
    }

    String approvedDateString = "";
    String approvedMapString = "";
    if(content.isApproved()) {
      approvedDateString = "lr:approved_date : $approvedDate; \n";
      approvedMapString = "$approvedDate = %approvedDate%, \n";
    }

    String reviewDateString = "";
    String reviewMapString = "";
    if(valueMap.get("reviewDate") != null) {
      reviewDateString = "lr:review_date : $reviewDate; \n";
      reviewMapString = "$reviewDate = %reviewDate%, \n";
    }

    String expiryDateString = "";
    String expiryMapString ="";
    if(valueMap.get("expiryDate") != null) {
      expiryDateString = "lr:expiry_date : $expiryDate; \n";
      expiryMapString = "$expiryDate = %expiryDate%, \n";
    }

    String urn = urnifyCtm(valueMap.get("uuid"));

    String query =
      "insert " + urn + " isa " + classname + "; \n" +
      "- $title ;\n" +
      "lr:create_date : $createDate; \n" +
      approvedDateString +
      reviewDateString +
      expiryDateString +
      "lr:modified_date : $modifyDate; \n" +
      "lr:display_date : $displayDate; \n" +
      "lr:version : $version; \n" +
      "lr:article_id : $articleId . from \n" +
      approvedMapString +
      reviewMapString +
      expiryMapString +
      "$createDate = %createDate%, \n" +
      "$modifyDate = %modifyDate%, \n" +
      "$displayDate = %displayDate%, \n" +
      "$version = %version%, \n" +
      "$title = %title%, \n" +
      "$articleId = %articleId%";
    queryWrapper.update(query, valueMap);
  }

  /**
   * Adds a <code>JournalStructure</code> to the topicmap by
   * assembling and running a tolog query.
   * <code>JournalStructure</code>s are inserted as subclasses of
   * whether "Liferay_WebContent" or another
   * <code>JournalStructure</code> if the latter is set as parent
   * structure inside die <code>JournalStructure</code> object.
   *
   * @param structure The <code>JournalStructure</code> from Liferay
   */
  private void addStructure_(JournalStructure structure) {
    Map valueMap = getStructureMap(structure);
    String structureUrn = urnifyCtm(structure.getUuid());
    String parentStructureUrn =
      findStructureUrnByStructureId(structure.getParentStructureId());

    String parent;
    if (parentStructureUrn != null) {
      log.debug("*** No parentstructure provided. Using webcontent instead ***");
      parent = PSI_PREFIX + "webcontent";
    } else
      parent = parentStructureUrn;

    String query ="insert " + structureUrn + " ako " + parent + ";\n" +
      "  - $name; lr:id: $id . " +
      "from" +
      "  $id = %structureId%, " +
      "  $name = %name%";
    queryWrapper.update(query, valueMap);
  }


  /**
   * Adds a <code>User</code> to the topicmap by assembling and
   * running a tolog query.
   *
   * @param user The <code>User</code> object from Liferay
   */
  private void addUser_(User user) {
    Map valueMap = getUserMap(user);

    String query = "insert " + urnifyCtm(user.getUuid()) +" isa lr:user;\n" +
      "- $username. from \n" +
        "$username = %username%";
    queryWrapper.update(query, valueMap);
  }


  /**
   * Adds a <code>WikiNode</code> into the topicmap by calling createWikiNode.
   * Also sets "created_by" association.
   *
   * @param wikinode The <code>WikiNode</code> from Liferay
   */
  private void addWikiNode_(WikiNode wikinode) throws SystemException {
    createWikiNode(wikinode);

    setCreator(wikinode.getUuid(), wikinode.getUserUuid());

    setGroupContains(String.valueOf(wikinode.getGroupId()), wikinode.getUuid());
  }


  /**
   * Adds a new <code>WikiNode</code> to the topicmap by assembling
   * and running a tolog query.
   *
   * @param wikinode The <code>WikiNode</code> from Liferay
   */
  private void createWikiNode(WikiNode wikinode) {
    String nodeUrn = urnifyCtm(wikinode.getUuid());

    String lastPostDateString = "";
    String lastPostDateMapString = "";
    if(wikinode.getLastPostDate() != null){
      lastPostDateString = "lr:lastpostdate : $lastPostDate; \n";
      lastPostDateMapString = "$lastPostDate = %lastPostDate%, \n";
    }

    Map valueMap = getWikiNodeMap(wikinode);
    String query = 
      "insert " + nodeUrn + " isa lr:wikinode; \n" +
      "- $name; \n" +
      "lr:create_date : $createDate; \n" +
      "lr:modified_date : $modifiedDate; \n" +
      lastPostDateString +
      "lr:wikinodeid : $nodeId . from \n" +
      "$name = %name%, \n" +
      "$createDate = %createDate%, \n" +
      "$modifiedDate = %modifiedDate%, \n" +
      lastPostDateMapString +
      "$nodeId = %nodeId%";
    queryWrapper.update(query, valueMap);
  }


  /**
   * Calls <code>createWikiPage</code> to create a topic for a
   * wikipage and then sets all necessary associations if applicable.
   * @param wikipage The wikipage containing the information
   */
  private void addWikiPage_(WikiPage wikipage) throws SystemException {
    createWikiPage(wikipage);
    for (WikiPage wpd : wikipage.getParentPages())
        setParentChild(wpd.getUuid(), wikipage.getUuid());

    // setting the parent-child assocs in both ways is needed when
    // updating a single page.
    for(WikiPage wpd : wikipage.getChildPages())
      setParentChild(wikipage.getUuid(), wpd.getUuid());

    setCreator(wikipage.getUuid(), wikipage.getUserUuid());

    setContains(wikipage.getNode().getUuid(), wikipage.getUuid());
  }

  /**
   * Creates a topic representing a WikiPage in liferay.
   *
   * @param wikipage The wikipage from liferay containing the information
   */
  private void createWikiPage(WikiPage wikipage) {
    String pageUrn = urnifyCtm(wikipage.getUuid());
    Map valueMap = getWikiPageMap(wikipage);
    String query = 
      "insert " + pageUrn + " isa lr:wikipage; \n" +
      "- $title; \n" +
      "lr:wikipageid : $pageId; \n"+
      "lr:create_date : $createDate; \n" +
      "lr:modified_date : $modifyDate; \n" +
      "lr:version : $version . from \n" +
      "$title = %title%, \n" +
      "$pageId = %pageId%, \n" +
      "$createDate = %createDate%, \n" +
      "$modifyDate = %modifyDate%, \n" +
      "$version = %version%";
    queryWrapper.update(query, valueMap);
  }


  /**
   * Adds a community to the topicmap by assembling and running a tolog query
   *
   * @param group The <code>Group</code> object from Liferay
   */
  private void addCommunity_(Group group) {
    Map valueMap = getGroupMap(group);
    String query =
      "insert ?group isa lr:community; \n" +
      "- $name; \n" +
      "lr:groupid : $groupId . from \n" +
      "$name = %name%, \n" +
      "$groupId = %groupId%";
    queryWrapper.update(query, valueMap);
  }

  public void deleteGroup_(Group group) {
    String query =
      "delete $TOPIC from \n" +
      "value($OCCURRENCE, \"" + group.getGroupId() + "\")," +
      "type($OCCURRENCE, lr:groupid)," +
      "occurrence($TOPIC, $OCCURRENCE)," +
      "instance-of($TOPIC, lr:community)";
    queryWrapper.update(query);
  }
  

  /**
   * This methods delets a topic that is identified by a uuid
   * @param uuid The UUID identifying the topic which is to be deleted
   */
  private void deleteByUuid(String uuid) {
    queryWrapper.update("delete i\"" + urnify(uuid) +"\"");
  }


  /**
   * Creates an "created_by" association between a topic playing the
   * "work" role and a topic playing the "creator" role
   *
   * @param workUuid The UUID of the topic that shall be the player of
   * the "work" role
   * @param creatorUuid The UUID of the topic that shall be the player
   * of the "creator" role
   */
  private void setCreator(String workUuid, String creatorUuid) {
    String workUrn = urnifyCtm(workUuid);
    String creatorUrn = urnifyCtm(creatorUuid);
    
    String query =
      "insert lr:created_by( lr:creator : " + creatorUrn + " , lr:work : " + workUrn + " )";
    queryWrapper.update(query);
  }


  /**
   * Creates an "approved_by" association between a
   * <code>JournalArticle></code> and a User using the approver's uuid
   * as contained in the <code>JournalArticle</code> object.
   */
  private void setUserApproving(JournalArticleWrapper content)
    throws SystemException {
    String workUrn = urnifyCtm(content.getUuid());
    
    String approverUrn = urnifyCtm(content.getStatusByUserUuid());
    String query = "insert lr:approved_by( lr:approver : " + approverUrn + " , lr:work : " + workUrn + " )";
    queryWrapper.update(query);
  }
  

  /**
   * Creates an "has_workflow_state" association between a
   * <code>JournalArticle</code> and an instance of the
   * "Workflow_state" class according to the workflow state in
   * Liferay.
   */
  private void setWorkflowstate(JournalArticleWrapper content) {
    String workplayerUrn = urnifyCtm(content.getUuid());
    String state;
    
    if (content.isExpired()) {
      state = "workflow_expired";
    } else if(content.isApproved()) {
      state = "workflow_approved";
    } else {
      state ="workflow_new";
    }
    
    String query =
      "insert lr:has_workflow_state( lr:state : lr:"+ state +" , lr:work : " + workplayerUrn + " )";
    queryWrapper.update(query);
  }

  /**
   * Returns the UUID as a URN as described in RFC XXXX
   *
   * @param uuid The UUID to use
   * @return A String containing the urn made from the UUID
   */
  private String urnify(String uuid) {
    return "urn:uuid:" + uuid;
  }

  /**
   * Creates a ctm-compatible urn from a UUID
   *
   * @param uuid The UUID to use
   * @return A String, containing the URN made from the UUID, inside
   * '<' and '>' to make it processable by ctm.
   */
  private String urnifyCtm(String uuid) {
    // in ctm uuid's need to be put into < > in order to process them
    return "<" + urnify(uuid) + ">";
  }

  /**
   * Returns the URN of a <code>JournalStructure</code> by looking it
   * up using its structure id
   *
   * @param structureId The structure's id
   * @return The Urn of the structure if lookup succeeded, null otherwise.
   */
  private String findStructureUrnByStructureId(String structureId) {
    Map params = new HashMap();
    params.put("id", structureId);
    
    String query ="select $PSI from\n" +
      "subject-identifier($TOPIC, $PSI),\n" +
      "lr:id($TOPIC, %id%)?";

    return queryWrapper.queryForString(query, params);
  }

  /**
   * Carries out updates for different types of objects using TmSync
   *
   * @param obj The updated object from Liferay
   * @param topicmap The topic map to update
   */
  private void update(Object obj) 
    throws MalformedURLException, SystemException {
    // the deciders are filters for features not to be updated
    log.debug("*** update called  for {} ***", obj.getClass());

    // making a temporary in-memory topicmap for sync inside adapter
    OntopiaAdapter adapter = new OntopiaAdapter();

    if (obj instanceof JournalArticleWrapper) {
        JournalArticleWrapper article = (JournalArticleWrapper) obj;
        adapter.addWebContent(article); 
        TopicIF source = adapter.retrieveTopicByUuid(article.getUuid());
        TopicMapSynchronizer.update(topicmap, source,
                                    new WebContentDecider(),
                                    new WebContentDecider());

    } else if (obj instanceof JournalStructure) {
        JournalStructure structure = (JournalStructure) obj;
        adapter.addStructure_(structure);
        TopicIF source = adapter.retrieveTopicByUuid(structure.getUuid());
        TopicMapSynchronizer.update(topicmap, source,
                                    new StructureDecider(),
                                    new StructureDecider());

    } else if (obj instanceof User) {
        User user = (User) obj;
        adapter.addUser_(user);
        TopicIF source = adapter.retrieveTopicByUuid(user.getUuid());
        TopicMapSynchronizer.update(topicmap, source,
                                    new UserDecider(), new UserDecider());
        
    } else if (obj instanceof WikiNode) {
        WikiNode node = (WikiNode) obj;
        adapter.addWikiNode_(node);
        TopicIF source = adapter.retrieveTopicByUuid(node.getUuid());
        TopicMapSynchronizer.update(topicmap, source,
                                    new WikiNodeDecider(),
                                    new WikiNodeDecider());
        
    } else if (obj instanceof WikiPage) {
        WikiPage page = (WikiPage) obj;
        adapter.addWikiPage(page);
        TopicIF source = adapter.retrieveTopicByUuid(page.getUuid());
        TopicMapSynchronizer.update(topicmap, source, new WikiPageDecider(),
                                    new WikiPageDecider());

    } else if (obj instanceof Group) {
      // TODO: See if the update code should go into an extra method
      Group group = (Group) obj;
      String objectId = getObjectIdByGroupId("" + group.getGroupId());
      TopicIF topic = (TopicIF) topicmap.getObjectById(objectId);
      if (topic != null) {
        TopicNameIF oldName = (TopicNameIF) topic.getTopicNames().iterator().next();
        Map<String, Object> valueMap = new HashMap();
        valueMap.put("name", group.getName());
        valueMap.put("oldName", oldName);
        
        // This changes names only because as of now this is the only
        // value that can be changed by users
        String query = "update value($oldName, $name) from " +
          "$oldName = %oldName%," +
          "$name = %name%";
        
        queryWrapper.update(query, valueMap);
      } else
        throw new OntopiaRuntimeException("Object of unknown type: " + obj);
    }
    log.debug("*** update ended! ***");
  }

  /**
   * Returns a TopicIF for a UUID
   *
   * @param uuid The UUID identifying the topic
   * @return A TopicIF implementation of the topic which is identified
   * by the UUID
   */
  private TopicIF retrieveTopicByUuid(String uuid) {
    LocatorIF identifiablePsiLocator =  new GenericLocator("uri",urnify(uuid));
    TopicIF source = topicmap.getTopicBySubjectIdentifier(identifiablePsiLocator);
    return source;
  }


  /**
   * Creates a "parent-child" association between two topics
   *
   * @param parentUuid The topic playing the "parent" role identified
   * by this UUID
   * @param childUuid The topic playing the "child" role identified by
   * this UUID
   */
  private void setParentChild(String parentUuid, String childUuid) {
    String parentUrn = urnifyCtm(parentUuid);
    String childUrn = urnifyCtm(childUuid);

    String query = "insert lr:parent-child(lr:parent : " + parentUrn + ", lr:child : " + childUrn + ")";
    queryWrapper.update(query);
  }


  /**
   * Creates a "contains" association between two topics
   *
   * @param containerUuid The topic playing the role "container"
   * identified by this UUID
   * @param containeeUuid The topic playing the role "containee"
   * identified by this UUID
   */
  private void setContains(String containerUuid, String containeeUuid) {
    String containeeUrn = urnifyCtm(containeeUuid);
    String containerUrn = urnifyCtm(containerUuid);
    String query =
      "insert lr:contains( lr:container : " + containerUrn + " , lr:containee : " + containeeUrn + " )";
    queryWrapper.update(query);
  }

  /**
   * Creates a "contains" association between two topics, one of which
   * being the group.
   * 
   * @param groupId The topic playing the role "container" identified
   * by this group id
   * @param uuid The topic playing the role "containee" identified by this UUID
   */
  private void setGroupContains(String groupId, String uuid) {
    //TODO: Could expect a long value instead of string for groupId
    //then be renamed to setContains()
    
    String groupObjectId = getObjectIdByGroupId(groupId);
    if (groupObjectId == null) {
      // this means there is no topic for the group. it's tempting to create
      // one automatically (would be nice for the default group, for example),
      // but there appears to be no way to find the name of the group. so we
      // stick with the hardcoded approach for now.
      throw new OntopiaRuntimeException("No topic for group with id " +
                                        groupId);
    }
    
    TopicIF topic = (TopicIF) topicmap.getObjectById(groupObjectId);
    Map<String, TopicIF> params = new HashMap();
    params.put("topic", topic);

    String query =
      "insert lr:contains( lr:container : $topic  , lr:containee : " + urnifyCtm(uuid) + " ) from \n" +
      "$topic = %topic%";
    queryWrapper.update(query, params);
  }


  /**
   * Returns to object id of a <code>Group</code> which is identified
   * by its group id.
   *
   * @param groupId The group id of the group to look up
   * @return A String containing the object id of the group, or null
   * if no group with that id could be found.
   */
  private String getObjectIdByGroupId(String groupId) {
    String query =
      "select $ID from \n" +
      "object-id($TOPIC,$ID)," +
      "value($OCCURRENCE, \"" + groupId + "\")," +
      "type($OCCURRENCE, lr:groupid)," +
      "occurrence($TOPIC, $OCCURRENCE)," +
      "instance-of($TOPIC, lr:community)?";
    return queryWrapper.queryForString(query);
  }


  // ---------------------------------------------------------------------------
  // Maps methods follow
  //
  // The Maps methods job is to fill a Map with values from the
  // Liferay Objects.  While about it, it also converts the values to
  // other types if nessecary.  Through doing this it is possible to
  // pass the map to the tolog QueryProcessor and to substitute String
  // in tolog queries with values from the map (using their keys).
  // See the tolog documentation and the JavaDoc of the
  // QueryProcessorIF for more details.
  // ---------------------------------------------------------------------------

  private Map getWebContentMap(JournalArticleWrapper content)
    throws SystemException {
    Map<String, String> retval = new HashMap();
    retval.put("title", content.getTitle());
    retval.put("uuid", content.getUuid());
    retval.put("createDate", DateFormatter.format(content.getCreateDate()));
    retval.put("displayDate", DateFormatter.format(content.getDisplayDate()));
    retval.put("modifyDate", DateFormatter.format(content.getModifiedDate()));
    retval.put("reviewDate", DateFormatter.format(content.getReviewDate()));
    retval.put("expiryDate", DateFormatter.format(content.getExpirationDate()));
    retval.put("articleId", content.getArticleId());
    retval.put("version", String.valueOf(content.getVersion()));
    retval.put("userId", String.valueOf(content.getUserId()));
    retval.put("structureId", content.getStructureId());
    retval.put("approvingUserId", String.valueOf(content.getStatusByUserId()));
    retval.put("approvingUserName", content.getStatusByUserName());
    retval.put("approvedDate", DateFormatter.format(content.getStatusDate()));
    retval.put("approvingUserUuid", content.getStatusByUserUuid());
    retval.put("useruuid", content.getUserUuid());
    return retval;
  }

  private Map getUserMap(User user) {
    Map<String, String> retval = new HashMap();

    retval.put("uuid", user.getUuid());
    retval.put("username", user.getEmailAddress());
    retval.put("urnCtm", urnify(user.getUuid()));
    return retval;
  }

  private Map getStructureMap(JournalStructure structure) {
    Map<String, String> retval = new HashMap();

    retval.put("uuid", structure.getUuid());
    retval.put("structureId", structure.getStructureId());
    retval.put("parentId", structure.getParentStructureId());
    retval.put("name", structure.getName());
    return retval;
  }

  private Map getGroupMap(Group group) {
    Map<String, String> retval = new HashMap();

    retval.put("groupId", String.valueOf(group.getGroupId()));
    retval.put("parentGroupId", String.valueOf(group.getParentGroupId()));
    retval.put("name", group.getName());
    return retval;
  }

  private Map getWikiNodeMap(WikiNode node) {
    Map<String, String> retval = new HashMap();

    retval.put("uuid", node.getUuid());
    retval.put("nodeId", String.valueOf(node.getNodeId()));
    retval.put("name", node.getName());
    retval.put("createDate", DateFormatter.format(node.getCreateDate()));
    retval.put("modifiedDate", DateFormatter.format(node.getModifiedDate()));
    retval.put("lastPostDate", DateFormatter.format(node.getLastPostDate()));
    try {
      retval.put("userUuid", node.getUserUuid());
    } catch (SystemException ex) {
      throw new OntopiaRuntimeException(ex);
    }
    return retval;
  }

  private Map getWikiPageMap(WikiPage page) {
    Map retval = new HashMap();

    retval.put("uuid", page.getUuid());
    retval.put("pageId", String.valueOf(page.getPageId()));
    retval.put("title", page.getTitle());
    retval.put("version", String.valueOf(page.getVersion()));
    retval.put("createDate", DateFormatter.format(page.getCreateDate()));
    retval.put("modifyDate", DateFormatter.format(page.getModifiedDate()));

    if(page.getParentPage() != null) {
      retval.put("parentPageUuid", page.getParentPage().getUuid());
    }

    try {
      retval.put("userUuid", page.getUserUuid());
    } catch (SystemException ex) {
      throw new OntopiaRuntimeException(ex);
    }
    return retval;
  }
}
