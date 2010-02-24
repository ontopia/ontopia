package tm;

import com.liferay.portal.SystemException;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.User;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.model.JournalStructure;
import com.liferay.portlet.wiki.model.WikiNode;
import com.liferay.portlet.wiki.model.WikiPage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

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
import net.ontopia.topicmaps.utils.TopicMapSynchronizer;
import net.ontopia.utils.OntopiaRuntimeException;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import util.DateFormatter;

/**
 * Provides control to alter a topicmap in ontopia according to changes in Liferay.
 * It is implemented as an "eager init" singleton. It shall be created along will all class objects and be removed once the runtime environment shuts down.
 * It also contains public static Strings to be used as PSI's for association types that may occur, these are mostly used inside the DeciderIF implementations.
 *
 * It makes heavy use of the tolog QueryProcessor:
 * @see QueryProcessorIF
 *
 * TODO: Make topicmap name configurable from the outside using i.e. properties
 * TODO: Find all \t and replace them with spaces
 */

public class OntopiaAdapter implements OntopiaAdapterIF {

  private static Logger log = LoggerFactory.getLogger(OntopiaAdapter.class);
  // This is an eager init singleton that is being created when the class objects are created
  // It is being removed along with all other objects when the container is shutdown
  public static OntopiaAdapterIF instance = new OntopiaAdapter();

  // this prefix is pretty much the same in all tolog queries
  private static final String PSI_PREFIX = "http://psi.ontopia.net/liferay/";

  public static final String ASSOC_CREATED_BY_PSI = PSI_PREFIX + "created_by";
  public static final String ASSOC_USER_APPROVING_PSI = PSI_PREFIX + "approved_by";
  public static final String ASSOC_HAS_WORKFLOW_STATE_PSI = PSI_PREFIX + "has_workflow_state";
  public static final String SUB_SUPERTYPE_PSI = "http://psi.topicmaps.org/iso13250/model/supertype-subtype";
  public static final String ASSOC_CONTAINS_PSI = PSI_PREFIX + "contains";
  public static final String ASSOC_PARENT_CHILD_PSI = PSI_PREFIX + "parent-child";
  public static final String ASSOC_IS_ABOUT_PSI = PSI_PREFIX + "is-about";

  // type information for updates
  private static final String WEBCONTENT_TYPE = "webcontent";
  private static final String USER_TYPE = "user";
  private static final String WIKIPAGE_TYPE = "wikipage";
  private static final String WIKINODE_TYPE = "wikinode";
  private static final String STRUCTURE_TYPE = "structure";
  private static final String COMMUNITY_TYPE = "community";

  // By what name the topicmap can be retrieved from ontopia
  private String tmName = "liferay.ltm";
  private static final String TOPICMAPNAMEKEY = "topicmapname";
  private static final String PROPERTYFILEPATH ="../ontopia.properties";

  // The topicmap that is being worked on
  private TopicMapIF topicmap;
  
  /**
   * Only contstructor is private in order to avoid multiple instances of this class.
   */
  private OntopiaAdapter() {
    super();
    prepareTopicmap();
  }


  // -----------------------------------
  // Implementing OntopiaAdapterIF
  // -----------------------------------


  public void addWebContent(JournalArticle content) {
    addWebContent(content, topicmap);
    try {
      // TODO: For the time being this cannot be used within the private method, because the update will fail due to the group association (no group in update tm)
      setGroupContains(String.valueOf(content.getGroupId()), content.getUuid(), topicmap); 
    } catch (Exception e) {
      deleteByUuid(content.getUuid()); // do not allow liferay to create a webcontent w/o attaching it to a group. if group can't be found, throw exception and don't create webcontent. See addWikiNode()
      throw new OntopiaRuntimeException(e); // when throwing exception, liferay does not save the webcontent the user has been working on - therefore no topic must be created.
    }
  }

  public void deleteWebContent(String uuid) {
    deleteByUuid(uuid);
  }

  public void updateWebContent(JournalArticle content) {
    try {
      update(content, "webcontent");
    } catch (MalformedURLException e) {
      throw new OntopiaRuntimeException(e);
    }
  }


  public void addUser(User user) {
    addUser(user, topicmap);
  }

  public void deleteUser(String uuid) {
    deleteByUuid(uuid);
  }

  public void updateUser(User user) {
    try{
      update(user, USER_TYPE);
    } catch (MalformedURLException e) {
        throw new OntopiaRuntimeException(e);
    }
  }


  public void addStructure(JournalStructure structure) {
    addStructure(structure, topicmap); //TODO: Shall structures have information on their group as well?
  }

  public void deleteStructure(String uuid) {
    deleteByUuid(uuid);
  }

  public void updateStructure(JournalStructure structure) {
    try {
      update(structure, STRUCTURE_TYPE);
    } catch (MalformedURLException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  

  public void addWikiNode(WikiNode node) {
    addWikiNode(node, topicmap);

    try {
      // this cannot be used inside the private method because update() calls it
      // setGroupContains fails inside an empty inMemo topicmap (like there is in update())
      setGroupContains(String.valueOf(node.getGroupId()), node.getUuid(), topicmap);
    } catch (OntopiaRuntimeException ore) {
      deleteByUuid(node.getUuid()); // if there is no group, alert and don't create topic!
      throw new OntopiaRuntimeException(ore);
    }
  }

  public void deleteWikiNode(String uuid) {
    deleteByUuid(uuid);
  }

  public void updateWikiNode(WikiNode wikinode) {
    try {
      update(wikinode, WIKINODE_TYPE);
    } catch (MalformedURLException ex) {
      throw new OntopiaRuntimeException(ex);
    }
  }


  public void addWikiPage(WikiPage wikipage) {
    addWikiPage(wikipage, topicmap);
  }

  public void deleteWikiPage(String uuid) {
    deleteByUuid(uuid);
  }

  public void updateWikiPage(WikiPage wikipage) {
    try {
      update(wikipage, WIKIPAGE_TYPE);
    } catch (MalformedURLException ex) {
      throw new OntopiaRuntimeException(ex);
    }
  }


  public void addGroup(Group group) {
    if(group.isCommunity()) {
      addCommunity(group, topicmap);
    }
    // TODO: Handling of parent-groups?
  }

  public void deleteGroup(Group group) {
    String query ="using lr for i\"" + PSI_PREFIX  + "\"\n" +
        "delete $TOPIC from \n" +
        "value($OCCURRENCE, \"" + group.getGroupId() + "\")," +
        "type($OCCURRENCE, lr:groupid)," +
        "occurrence($TOPIC, $OCCURRENCE)," +
        "instance-of($TOPIC, lr:community)";
    runQuery(query);
  }

  public void updateGroup(Group group) {
    if(group.isCommunity()) {
      try {
        update(group, COMMUNITY_TYPE);
      } catch (MalformedURLException ex) {
        throw new OntopiaRuntimeException(ex);
      }
    }
  }

  // ----------------------------
  // static method
  // ----------------------------

  /**
   * Is called by DeciderIF implementations to check if the PSI provided is the type of the Association provided
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
        if(locators.contains(locator)) {
          return true;
        }

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
    TopicIF topic = retrieveTopicByUuid(uuid, topicmap); // may raise exception if topic can not be found. That's ok.
    if(topic != null) {
      return topic.getObjectId();
    } else {
      return null;
    }
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
   * Returns the objectId of the *first* type of a topic which is identified by a uuid that it finds.
   *
   * @param uuid The uuid of the topic in question
   * @return A String containing the objectId for the type of the topic in question
   */
  public String getTopicTypeIdForUuid(String uuid) {
    TopicIF topic = retrieveTopicByUuid(uuid, topicmap);
    if(topic != null) {
      Collection collection = topic.getTypes();
      if(!collection.isEmpty()) {
        Iterator collIt = collection.iterator();
        while(collIt.hasNext()) {
          TopicIF type = (TopicIF) collIt.next(); //TODO: returns only the first type. What to do if there are more? Can there be more?!
          return type.getObjectId();
        }
      }
    }
    return null;
  }

  /**
   * Returns the objectId of the "conceptView" topic by trying to look up the PSI:
   * http://psi.ontopia.net/liferay/conceptview
   * Will throw an exception if the PSI can not be found.
   *
   * @return The objectId of the "conceptView" topic
   */
  public String getConceptViewId() {
    TopicIF conceptView = topicmap.getTopicBySubjectIdentifier(new GenericLocator("uri", "http://psi.ontopia.net/liferay/conceptview"));
    return conceptView.getObjectId();
  }

  /**
   * Returns the object id for the topic that represents the article that will be displayed, providing an articleId.
   * The rule is: The topic, with the highest version AND which has an "approved" state, is returned
   *
   * @param articleId the article id from liferay
   * @return The object for the topic
   */
  public String getCurrentObjectIdForArticleId(String articleId) {
    String query ="using lr for i\"http://psi.ontopia.net/liferay/\" \n" +
            "select $id, $number from \n" +
            "object-id($topic, $id), \n"+
            "occurrence($topic , $aid), \n" +
            "type($aid, lr:article_id), \n" +
            "value($aid, \"" + articleId +"\"), \n" +
            "lr:has_workflow_state( $topic :  lr:work , lr:workflow_approved : lr:state ), \n" +
            "occurrence($topic, $version), \n" +
            "type($version, lr:version), \n" +
            "value($version, $number) \n" +
            "order by $number desc?";

    String retval = getSingleStringFromQuery(query, topicmap);
    if(retval != null) {
      return retval;
    } else {
      log.error("Error! Query was: {}", query);
      throw new OntopiaRuntimeException("No Article with articleId " + articleId  + " found!");
    }
  }


  // ------------------------------------------
  // some non-public methods acting as helpers
  // ------------------------------------------

  private void prepareTopicmap() {
    Properties props = new Properties();
    // read topicmapName from properties file
    try {
      props.load(new FileInputStream(PROPERTYFILEPATH));
    } catch (IOException ex) {
      log.error(ex.getMessage());
      throw new OntopiaRuntimeException(ex);
    }

    tmName = (String) props.getProperty(TOPICMAPNAMEKEY);
    
    TopicMapStoreIF store = TopicMaps.createStore(tmName, false);
    topicmap  = store.getTopicMap();
  }

  /**
   * Creates an article and sets created_by and approved_by associations.
   *
   * @param content The <code>JournalArticle</code> from Liferay
   * @param tm The topicmap to work on
   */
  private void addWebContent(JournalArticle content, TopicMapIF tm) {
    createWebContent(content, tm);
    setWorkflowstate(content, tm);

    try {
      String creatorUuid = content.getUserUuid();
      setCreator(content.getUuid(), creatorUuid, tm);
    } catch (SystemException ex) {
      // No idea what might trigger the SystemException
      throw new OntopiaRuntimeException(ex);
    }

    if(content.isApproved()) {
      setUserApproving(content, tm);
    }
  }


  /**
   * Assembles one rather big tolog query to create a topic for a <code>JournalArticle</code>
   * 
   * @param content The <code>JournalArticle</code> from Liferay
   * @param tm The topicmap to work on
   */
  private void createWebContent(JournalArticle content, TopicMapIF tm) {
    Map<String, String> valueMap = getWebContentMap(content);
    String classname;
    if(content.getStructureId().equals("")) {
      classname = "lr:article";
    } else {
      classname = findStructureUrnByStructureId(content.getStructureId());
      if(classname.equals(null)) {
        throw new OntopiaRuntimeException("Structure with id + " + content.getStructureId() + " not found!");
      }
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

    String query ="using lr for i\"" + PSI_PREFIX  + "\"\n" +
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
    runQuery(query, tm, valueMap);
  }

  /**
   * Adds a <code>JournalStructure</code> to the topicmap by assembling and running a tolog query.
   * <code>JournalStructure</code>s are inserted as subclasses of whether "Liferay_WebContent" or another <code>JournalStructure</code>
   * if the latter is set as parent structure inside die <code>JournalStructure</code> object.
   *
   * @param structure The <code>JournalStructure</code> from Liferay
   * @param tm The topicmap to work on
   */
  private void addStructure(JournalStructure structure, TopicMapIF tm) {
    Map valueMap = getStructureMap(structure);
    String structureUrn = urnifyCtm(structure.getUuid());
    String parentStructureUrn = findStructureUrnByStructureId(structure.getParentStructureId());

    String parent;
    if(parentStructureUrn != null) {
      log.debug("*** No parentstructure provided. Using webcontent instead ***");
      parent = PSI_PREFIX + "webcontent";
    } else {
      parent = parentStructureUrn;
    }

    String query ="insert " + structureUrn + " ako " + parent + ";\n" +
    		"- $id . from" +
        "$id = %structureId%";
    runQuery(query, tm, valueMap); // is it considered cheating to use the ID as a Name?
  }


  /**
   * Adds a <code>User</code> to the topicmap by assembling and running a tolog query
   *
   * @param user The <code>User</code> object from Liferay
   * @param tm The topicmap to work on
   */
  private void addUser(User user, TopicMapIF tm) {
    Map valueMap = getUserMap(user);

    String query = "insert " + urnifyCtm(user.getUuid()) +" isa " + PSI_PREFIX + "user;\n" +
    		"- $username. from \n" +
        "$username = %username%";
    runQuery(query, tm, valueMap);
  }


  /**
   * Adds a <code>WikiNode</code> into the topicmap by calling createWikiNode.
   * Also sets "created_by" association.
   *
   * @param wikinode The <code>WikiNode</code> from Liferay
   * @param tm The topicmap to work on
   */
  private void addWikiNode(WikiNode wikinode, TopicMapIF tm) {
    createWikiNode(wikinode, tm);

    try {
      setCreator(wikinode.getUuid(), wikinode.getUserUuid(), tm);
    } catch (SystemException ex) {
      throw new OntopiaRuntimeException(ex);
    }

  }


  /**
   * Adds a new <code>WikiNode</code> to the topicmap by assembling and running a tolog query.
   *
   * @param wikinode The <code>WikiNode</code> from Liferay
   * @param tm The topicmap to work on
   */
  private void createWikiNode(WikiNode wikinode, TopicMapIF tm) {
    String nodeUrn = urnifyCtm(wikinode.getUuid());

    String lastPostDateString = "";
    String lastPostDateMapString = "";
    if(wikinode.getLastPostDate() != null){
      lastPostDateString = "lr:lastpostdate : $lastPostDate; \n";
      lastPostDateMapString = "$lastPostDate = %lastPostDate%, \n";
    }

    Map valueMap = getWikiNodeMap(wikinode);
    String query = "using lr for i\"" + PSI_PREFIX + "\" \n" +
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
    runQuery(query, tm, valueMap);
  }


  /**
   * Calls <code>createWikiPage</code> to create a topic for a wikipage and then sets all necessary associations if applicable.
   * @param wikipage The wikipage containing the information
   * @param tm The topicmap to work on
   */
  private void addWikiPage(WikiPage wikipage, TopicMapIF tm) {
    createWikiPage(wikipage, tm);
    if(!wikipage.getParentPages().isEmpty()) {
      for(WikiPage wpd : wikipage.getParentPages()) {
        setParentChild(wpd.getUuid(), wikipage.getUuid(), tm);
      }
    }
    // setting the parent-child assocs in both ways is needed when updating a single page.
    if(!wikipage.getChildPages().isEmpty()) {
      for(WikiPage wpd : wikipage.getChildPages()) {
        setParentChild(wikipage.getUuid(), wpd.getUuid(), tm);
      }
    }

    try {
      setCreator(wikipage.getUuid(), wikipage.getUserUuid(), tm);
    } catch (SystemException ex) {
      throw new OntopiaRuntimeException(ex);
    }

    try {
      setContains(wikipage.getNode().getUuid(), wikipage.getUuid(), tm);
    } catch (OntopiaRuntimeException ore){
      /* if there is no group, alert and don't add page to tm, to avoid running out of sync.
       * As of now there is imho no other way to do it. A rollback won't help because the transaction that actually creates the topic for the wikipage
       * has been already processed and presumably can't be rolled back now.
       */
      deleteByUuid(wikipage.getUuid());
      // This is not supposed to happen. Throwing Exception to alert the user.
      throw new OntopiaRuntimeException(ore);
    }
  }

/**
 * Creates a topic representing a WikiPage in liferay.
 *
 * @param wikipage The wikipage from liferay containing the information
 * @param tm The topicmap on which to work
 */
  private void createWikiPage(WikiPage wikipage, TopicMapIF tm) {
    String pageUrn = urnifyCtm(wikipage.getUuid());
    Map valueMap = getWikiPageMap(wikipage);
    String query = "using lr for i\"" + PSI_PREFIX + "\" \n" +
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
    runQuery(query, tm, valueMap);
  }


  /**
   * Adds a community to the topicmap by assembling and running a tolog query
   *
   * @param group The <code>Group</code> object from Liferay
   * @param tm The topicmap ob which to work
   */
  private void addCommunity(Group group, TopicMapIF tm) {
    Map valueMap = getGroupMap(group);
    String query ="using lr for i\"" + PSI_PREFIX  + "\"\n" +
             "insert ?group isa " + PSI_PREFIX + "community; \n" +
             "- $name; \n" +
             "lr:groupid : $groupId . from \n" +
             "$name = %name%, \n" +
             "$groupId = %groupId%";
     runQuery(query, tm, valueMap);
  }


  /**
   * This methods delets a topic that is identified by a uuid
   * @param uuid The UUID identifying the topic which is to be deleted
   */
  private void deleteByUuid(String uuid) {
    log.debug("*** delete " + uuid + " ***");
    String psi = urnify(uuid);
    String query = "delete i\"" + psi +"\"";
    runQuery(query);
  }


  /**
   * Creates an "created_by" association between a topic playing the "work" role and a topic playing the "creator" role
   *
   * @param workUuid The UUID of the topic that shall be the player of the "work" role
   * @param creatorUuid The UUID of the topic that shall be the player of the "creator" role
   * @param tm The topicmap on which to work
   */
  private void setCreator(String workUuid, String creatorUuid, TopicMapIF tm) {
    String workUrn = urnifyCtm(workUuid);
    String creatorUrn = urnifyCtm(creatorUuid);
    
    String query = "using lr for i\"" + PSI_PREFIX + "\"\n" +
        "insert lr:created_by( lr:creator : " + creatorUrn + " , lr:work : " + workUrn + " )";
    runQuery(query, tm);
  }


  /**
   * Creates an "approved_by" association between a <code>JournalArticle></code> and a User using
   * the approver's uuid as contained in the <code>JournalArticle</code> object.
   *
   * @param content The <code>JournalArticle</code> object that has been approved.
   * @param tm The topicmap on which to work
   */
  private void setUserApproving(JournalArticle content, TopicMapIF tm) {
    String workUrn = urnifyCtm(content.getUuid());
    
    String approverUrn;
    try {
      approverUrn = urnifyCtm(content.getApprovedByUserUuid());
    } catch (SystemException ex) {
      throw new OntopiaRuntimeException(ex);
    }

    String query = "using lr for i\"" + PSI_PREFIX + "\"\n" +
    "insert lr:approved_by( lr:approver : " + approverUrn + " , lr:work : " + workUrn + " )";
    runQuery(query,tm);
  }
  

  /**
   * Creates an "has_workflow_state" association between a <code>JournalArticle</code> and an instance of
   * the "Workflow_state" class according to the workflow state in Liferay.
   *
   * @param content The <code>JournalArticle</code> object from Liferay
   * @param tm The topicmap on which to work
   */
  private void setWorkflowstate(JournalArticle content, TopicMapIF tm) {
    String workplayerUrn = urnifyCtm(content.getUuid());
    String state;
    
    if(content.isExpired()) {
      state = "workflow_expired";
    } else if(content.isApproved()) {
      state = "workflow_approved";
    } else {
      state ="workflow_new";
    }
    
    String query = "using lr for i\"" + PSI_PREFIX + "\"\n" +
        "insert lr:has_workflow_state( lr:state : lr:"+ state +" , lr:work : " + workplayerUrn + " )";
    runQuery(query,tm);
  }


  /**
   * Convenience method to call runQuery w/o having to provide a Map
   *
   * @param query The query to run
   * @param tm The topicmap on which to work
   */
  private void runQuery(String query, TopicMapIF tm) {
    HashMap map = new HashMap();
    runQuery(query, tm, map);
  }

  /**
   * Central method that runs all the tolog-update queries
   *
   * @param query The query to runb
   * @param tm The topicmap to work on
   * @param map A Map containing values for variable substitution (see tolog documentation)
   */
  private synchronized void runQuery(String query, TopicMapIF tm, Map map) {
    QueryProcessorIF proc = QueryUtils.createQueryProcessor(tm);
    TopicMapStoreIF store = tm.getStore();
    store.open();
    try {
      int number = proc.update(query, map);
      log.debug("*** Query processed successfully # {} ***", number);
      store.commit();
    } catch (Exception e) {
        log.error("*** Error while processing query: ");
        log.error(query);
        //rollback the transaction
        store.abort();
        throw new OntopiaRuntimeException(e);
    } finally {
      store.close();
    }
  }

  /**
   * Convenience method only requiring a query. Defaulting the topicmap with the global topicmap.
   *
   * @param query The tolog query to run
   */
  private void runQuery(String query) {
    runQuery(query, topicmap);
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
   * @return A String containing the URN made from the UUID inside '<' and '>' to make the processable by ctm.
   */
  private String urnifyCtm(String uuid) {
    // in ctm uuid's need to be put into < > in order to process them
    return "<" + urnify(uuid) + ">";
  }

  /**
   * Returns the URN of a <code>JournalStructure</code> by looking it up using its structure id
   *
   * @param structureId The structure's id
   * @return The Urn of the structure if lookup succeeded, null otherwise.
   */
  private String findStructureUrnByStructureId(String structureId) {
    String query ="select $PSI from\n" +
    		"subject-identifier($TOPIC, $PSI),\n" +
    		"topic-name($TOPIC, $BASENAME),\n" +
    		"value($BASENAME,\"" + structureId +"\")?"; // TODO: I think this passes for cheating using the oldName to store the structureId ... not sure.. ?

    String retval = getSingleStringFromQuery(query, topicmap);
    return retval;
  }

  /**
   * Returns only the first result in the first row of a resultset produced by a tolog query
   *
   * TODO: May be not return a String but simply an Object.
   * 
   * @param query The query to produce the ResultSet
   * @param tm The topicmap on which to work
   * @return The value in the first cell of the first row inside the resultset, NULL if no results are returned by the query
   */
  private String getSingleStringFromQuery(String query, TopicMapIF tm) {
    QueryResultIF result = executeQuery(query, tm);
    while(result.next()){
      Object[] results = new Object[result.getWidth()];
      results = result.getValues(results);
      Object retval = results[0];
      return (String) retval ;
    }
    return null;
  }

  /**
   * Runs a select query on the topicmap and returns the ResultSet
   *
   * @param query The query to run
   * @param tm The topicmap to work on
   * @return A ResultSet
   */
  private QueryResultIF executeQuery(String query, TopicMapIF tm) {
    QueryProcessorIF proc = QueryUtils.createQueryProcessor(tm);
    try {
      QueryResultIF result = proc.execute(query);
      return result;
    } catch (InvalidQueryException e) {
      log.error("*** Error executing query:  ***");
      log.error(query);
      throw new OntopiaRuntimeException(e);
    }
  }


  /**
   * Carries out updates for different types of objects using TmSync
   *
   * @param obj The updated object from Liferay
   * @param type A String containing information on the type of this object. Will be used to cast obj to the correct type.
   * @throws java.net.MalformedURLException
   */
  private void update(Object obj, String type) throws MalformedURLException {
    log.debug("*** update called  for {} ***", type);
    TopicMapStoreIF sourceStore = new InMemoryTopicMapStore();
    TopicMapIF sourceTm = sourceStore.getTopicMap(); // temporary topicmap f. sync

    // the deciders are filters for features not to be updated

    if(type.equalsIgnoreCase(WEBCONTENT_TYPE)) {
        JournalArticle article = (JournalArticle) obj;
        addWebContent(article,sourceTm); // I think I could simply call createWebContent() instead and leave the deciders be empty?
        TopicIF source = retrieveTopicByUuid(article.getUuid(), sourceTm);
        TopicMapSynchronizer.update(topicmap, source , new WebContentDecider(), new WebContentDecider());
    } else if(type.equalsIgnoreCase(STRUCTURE_TYPE)) {
        JournalStructure structure = (JournalStructure) obj;
        addStructure(structure,sourceTm);
        TopicIF source = retrieveTopicByUuid(structure.getUuid(), sourceTm);
        TopicMapSynchronizer.update(topicmap, source , new StructureDecider(), new StructureDecider());
    } else if(type.equalsIgnoreCase(USER_TYPE)) {
        User user = (User) obj;
        addUser(user, sourceTm);
        TopicIF source = retrieveTopicByUuid(user.getUuid(), sourceTm);
        TopicMapSynchronizer.update(topicmap, source , new UserDecider(), new UserDecider());
    } else if(type.equals(WIKINODE_TYPE)) {
        WikiNode node = (WikiNode) obj;
        addWikiNode(node, sourceTm);
        TopicIF source = retrieveTopicByUuid(node.getUuid(), sourceTm);
        TopicMapSynchronizer.update(topicmap, source , new WikiNodeDecider(), new WikiNodeDecider());
    } else if(type.equals(WIKIPAGE_TYPE)) {
        WikiPage page = (WikiPage) obj;
        addWikiPage(page, sourceTm);
        TopicIF source = retrieveTopicByUuid(page.getUuid(), sourceTm);
        TopicMapSynchronizer.update(topicmap, source , new WikiPageDecider(), new WikiPageDecider());
    } else if(type.equals(COMMUNITY_TYPE)) { // TODO: See if the update code should go into an extra method
        Group group = (Group) obj;
        String objectId = getObjectIdByGroupId(String.valueOf(group.getGroupId()), topicmap);
        TopicIF topic = (TopicIF) topicmap.getObjectById(objectId);
        if(topic != null) {
          TopicNameIF oldName = (TopicNameIF) topic.getTopicNames().iterator().next();
          Map<String, String> valueMap = new HashMap();
          valueMap.put("name", group.getName());

          // This changes names only because as of now this is the only value that can be changed by users
          String query = "update value($NAME, $name) from " +
                  "topic-name($TOPIC, $NAME), \n" +
                  "instance-of($TOPIC, lr:community)" +
                  "value($NAME, \"" + oldName.getValue() + "\")," +
                  "$name = %name%";

          runQuery(query, topicmap, valueMap);
        }
      }
    log.debug("*** update ended! ***");
  }


  /**
   * Returns a TopicIF implementation for a UUID
   *
   * @param uuid The UUID identifying the topic
   * @param tm The topicmap on which to work
   * @return A TopicIF implementation of the topic which is identified by the UUID
   */
  private TopicIF retrieveTopicByUuid(String uuid, TopicMapIF tm) {
    LocatorIF identifiablePsiLocator =  new GenericLocator("uri",urnify(uuid));
    TopicIF source = tm.getTopicBySubjectIdentifier(identifiablePsiLocator);
    return source;
  }


  /**
   * Creates a "parent-child" association between two topics
   *
   * @param parentUuid The topic playing the "parent" role identified by this UUID
   * @param childUuid The topic playing the "child" role identified by this UUID
   * @param tm The topicmap to work on
   */
  private void setParentChild(String parentUuid, String childUuid, TopicMapIF tm) {
    String parentUrn = urnifyCtm(parentUuid);
    String childUrn = urnifyCtm(childUuid);

    String query = "using lr for i\"" + PSI_PREFIX + "\" \n" +
            "insert lr:parent-child( lr:parent : " + parentUrn + ", lr:child : " + childUrn +" )";
    runQuery(query, tm);
  }


  /**
   * Creates a "contains" association between two topics
   *
   * @param containerUuid The topic playing the role "container" identified by this UUID
   * @param containeeUuid The topic playing the role "containee" identified by this UUID
   * @param tm The topicmap to work on
   */
  private void setContains(String containerUuid, String containeeUuid, TopicMapIF tm) {
    String containeeUrn = urnifyCtm(containeeUuid);
    String containerUrn = urnifyCtm(containerUuid);
    String query ="using lr for i\"" + PSI_PREFIX  + "\"\n" +
            "insert lr:contains( lr:container : " + containerUrn + " , lr:containee : " + containeeUrn + " )";
    runQuery(query, tm);
  }


  /**
   * Creates a "contains" association between two topics, one of which being the group.
   * 
   * @param groupId The topic playing the role "container" identified by this group id
   * @param uuid The topic playing the role "containee" identified by this UUID
   * @param tm The topicmap to work on
   */
  private void setGroupContains(String groupId, String uuid, TopicMapIF tm) {
    //TODO: Could expect a long value instead of string for groupId then be renamed to setContains()
    String groupObjectId = getObjectIdByGroupId(groupId, tm);
    TopicIF topic = (TopicIF) tm.getObjectById(groupObjectId);
    Map<String, TopicIF> map = new HashMap();
    map.put("topic", topic);

    String query ="using lr for i\"" + PSI_PREFIX  + "\"\n" +
            "insert lr:contains( lr:container : $topic  , lr:containee : " + urnifyCtm(uuid) + " ) from \n" +
            "$topic = %topic%";
    runQuery(query, tm, map);
  }


  /**
   * Returns to object id of a <code>Group</code> which is identified by its group id.
   *
   * @param groupId The group id of the group to look up
   * @param tm The topicmap on which to worl
   * @return A String containing the object id of the group, or null if no group with that id could be found.
   */
  private String getObjectIdByGroupId(String groupId, TopicMapIF tm) {
    String query ="using lr for i\"" + PSI_PREFIX  + "\"\n" +
            "select $ID from \n" +
            "object-id($TOPIC,$ID)," +
            "value($OCCURRENCE, \"" + groupId + "\")," +
            "type($OCCURRENCE, lr:groupid)," +
            "occurrence($TOPIC, $OCCURRENCE)," +
            "instance-of($TOPIC, lr:community)?";
    String retval = getSingleStringFromQuery(query, tm);
    return retval;
  }

  /**
   * Before this object gets removed it has to save the current topicmap name to the properties file.
   * In the future the name might be set through the Liferay UI and hence can change at runtime.
   */
  public void finalize() {
    // save properties to file before being removed
    Properties props = new Properties();

    props.setProperty(TOPICMAPNAMEKEY, tmName);
    try {
      props.store(new FileOutputStream(PROPERTYFILEPATH), null);
    } catch (IOException ex) {
      log.error(ex.getMessage());
      throw new OntopiaRuntimeException(ex);
    }
  }


  // -------------------------------------------------------------------------------------------
  // Maps methods follow
  //
  // The Maps methods job is to fill a Map with values from the Liferay Objects.
  // While about it, it also converts the values to other types if nessecary.
  // Through doing this it is possible to pass the map to the tolog QueryProcessor
  // and to substitute String in tolog queries with values from the map (using their keys).
  // See the tolog documentation and the JavaDoc of the QueryProcessorIF for more details.
  // --------------------------------------------------------------------------------------------

  private Map getWebContentMap(JournalArticle content) {
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
    retval.put("approvingUserId", String.valueOf(content.getApprovedByUserId()));
    retval.put("approvingUserName", content.getApprovedByUserName());
    retval.put("approvedDate", DateFormatter.format(content.getApprovedDate()));

    try {
      retval.put("approvingUserUuid", content.getApprovedByUserUuid());
    } catch (SystemException ex) {
      throw new OntopiaRuntimeException(ex);
    }
    
    try {
      retval.put("useruuid", content.getUserUuid());
    } catch (SystemException ex) {
      throw new OntopiaRuntimeException(ex);
    }
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
