package tm;

import com.liferay.portal.SystemException;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.User;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.model.JournalStructure;
import com.liferay.portlet.wiki.model.WikiNode;
import com.liferay.portlet.wiki.model.WikiPage;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
import util.DateFormater;

/**
 * This class provides control to alter a topicmap in ontopia according to changes in Liferay.
 */

public class OntopiaAdapter implements OntopiaAdapterIF{
  
  public static OntopiaAdapterIF instance = new OntopiaAdapter();

  private static final String PSI_PREFIX = "http://psi.ontopia.net/liferay/";

  public static final String ASSOC_CREATED_BY_PSI = PSI_PREFIX + "created_by";
  public static final String ASSOC_USER_APPROVING_PSI = PSI_PREFIX + "approved_by";
  public static final String ASSOC_HAS_WORKFLOW_STATE_PSI = PSI_PREFIX + "has_workflow_state";
  public static final String SUB_SUPERTYPE_PSI = "http://psi.topicmaps.org/iso13250/model/supertype-subtype";
  public static final String ASSOC_CONTAINS_PSI = PSI_PREFIX + "contains";
  public static final String ASSOC_PARENT_CHILD_PSI = PSI_PREFIX + "parent-child";
  public static final String ASSOC_PARENT_IS_ABOUT_PSI = PSI_PREFIX + "is-about";

  public static final String NULL = "null";
  
  private static final String WEBCONTENT_TYPE = "webcontent";
  private static final String USER_TYPE = "user";
  private static final String WIKIPAGE_TYPE = "wikipage";
  private static final String WIKINODE_TYPE = "wikinode";
  private static final String STRUCTURE_TYPE = "structure";
  private static final String COMMUNITY_TYPE = "community";

  private static final String TMNAME = "liferay_v47.ltm";
  
  private TopicMapIF topicmap;
  
  
  private OntopiaAdapter(){
    super();
    prepareTopicmap();
  }
  
  private void prepareTopicmap(){
    TopicMapStoreIF store = TopicMaps.createStore(TMNAME, false);
    topicmap  = store.getTopicMap();
    System.out.println("### Ontopia: store is readonly? -> " + store.isReadOnly() + " ###");
    System.out.println("### Ontopia: store is transactional? -> " + store.isTransactional() + " ###");
    System.out.println("### Ontopia: store uses implementation: " + store.getImplementation() + " ###");
  }

  // Webcontent
  public void addWebContent(JournalArticle content){
    addWebContent(content, topicmap);
    setGroupContains(String.valueOf(content.getGroupId()), content.getUuid(), topicmap); // TODO: For the time being this cannot be used within the private method, because the update will fail
  }
  
  private void addWebContent(JournalArticle content, TopicMapIF tm){
    createWebContent(content,tm);
    setWorkflowstate(content,tm);
    
    try {
      String creatorUuid = content.getUserUuid();
      setCreator(content.getUuid(), creatorUuid, tm);
    } catch (SystemException ex) {
      throw new OntopiaRuntimeException(ex);
    }
    
    if(content.isApproved()){
      setUserApproving(content, tm);
    }
    
    
  }
  
  public void deleteWebContent(String uuid){
    deleteByUuid(uuid);
  }
  
  public void updateWebContent(JournalArticle content){
    try {
      update(content, "webcontent");
    } catch (MalformedURLException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  // Users
  public void addStructure(JournalStructure structure) {
    addStructure(structure, topicmap);    
  }

  public void addUser(User user) {
    addUser(user, topicmap);    
  }
  
  private void addUser(User user, TopicMapIF tm){
    System.out.println("*** addUser ***"); //DEBUG
    HashMap valueMap = getUserHashMap(user);
    
    String query = "insert " + urnifyCtm(user.getUuid()) +" isa " + PSI_PREFIX + "user;\n" +
    		"- $username. from \n" +
        "$username = %username%";
    runQuery(query, tm, valueMap);
  }
  
  public void deleteUser(String uuid){
    deleteByUuid(uuid); 
  }
  
  public void updateUser(User user){
    try {
      update(user, USER_TYPE);
    } catch (MalformedURLException e) {
        throw new OntopiaRuntimeException(e);    }
  }

  // Structures
  private void addStructure(JournalStructure structure, TopicMapIF tm){
    System.out.println("*** addStructure ***");
    HashMap valueMap = getStructureHashMap(structure);
    String structureUrn = urnifyCtm(structure.getUuid());
    String parentStructureUrn = findStructureUrnByStructureId(structure.getParentStructureId());

    String parent;
    if(parentStructureUrn.equals(NULL)){
      System.out.println("*** No parentstructure provided. Using webcontent instead ***");
      parent = PSI_PREFIX + "webcontent";
    } else {
      parent = parentStructureUrn;
    }
    
    String query ="insert " + structureUrn + " ako " + parent + ";\n" +
    		"- $id . from" +
        "$id = %structureId%";
    
    runQuery(query, tm, valueMap); // is it considered cheating to use the ID as a Name?
  }
  
  public void deleteStructure(String uuid){
    deleteByUuid(uuid);
  }
  
  public void updateStructure(JournalStructure structure){
    try {
      update(structure, STRUCTURE_TYPE);
    } catch (MalformedURLException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  private void deleteByUuid(String uuid){
    System.out.println("*** delete " + uuid + " ***");
    String psi = urnify(uuid);
    String query = "delete i\"" + psi +"\"";
    try{
      runQuery(query);
    } catch (OntopiaRuntimeException ore){
      // log exception and go on if topic to be deleted can not be found. It's gone already obviously.
      ore.printStackTrace();
    }
  }
  
  private void createWebContent(JournalArticle content, TopicMapIF tm){
    System.out.println("*** addArticle ***");
    HashMap<String, String> valueMap = getWebContentHashmap(content);
    String classname;
    if(content.getStructureId().equals("")){
      classname = "lr:article";
    } else {
      classname = findStructureUrnByStructureId(content.getStructureId());
      if(classname.equals(NULL)){
        throw new OntopiaRuntimeException("Structure with id + " + content.getStructureId() + " not found!");
      }
    }

    String approvedDateString = "";
    if(content.isApproved()){
      approvedDateString = "lr:approved_date : $approvedDate; \n";
    }
    
    String reviewDateString = "";
    if(!valueMap.get("reviewDate").equals(NULL)){
      reviewDateString = "lr:review_date : $reviewDate; \n";
    }
    
    String expiryDateString = "";
    if(!valueMap.get("expiryDate").equals(NULL)){
      expiryDateString = "lr:expiry_date : $expiryDate; \n";
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
        "$approvedDate = %approvedDate%, \n" +
        "$reviewDate = %reviewDate%, \n" +
        "$expiryDate = %expiryDate%, \n" +
        "$createDate = %createDate%, \n" +
        "$modifyDate = %modifyDate%, \n" +
        "$displayDate = %displayDate%, \n" +
        "$version = %version%, \n" +
        "$title = %title%, \n" +
        "$articleId = %articleId%";
    runQuery(query, tm, valueMap);
  }
  
  
  private void setCreator(String workUuid, String creatorUuid, TopicMapIF tm){
    System.out.println("*** setCreator ***");
    String workUrn = urnifyCtm(workUuid);
    String creatorUrn = urnifyCtm(creatorUuid);
    
    String query = "using lr for i\"" + PSI_PREFIX + "\"\n" +
        "insert lr:created_by( lr:creator : " + creatorUrn + " , lr:work : " + workUrn + " )";
    System.out.println(query);
    runQuery(query, tm);
  }
  
  private void setUserApproving(JournalArticle content, TopicMapIF tm){
    System.out.println("*** setUserApproving ***");
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
  
  
  private void setWorkflowstate(JournalArticle content, TopicMapIF tm){
    System.out.println("*** setWorkflowState ***");
    
    String workplayerUrn = urnifyCtm(content.getUuid());
    String state;
    
    if(content.isExpired()){
      state = "workflow_expired";
    } else if(content.isApproved()){
      state = "workflow_approved";
    } else {
      state ="workflow_new";
    }
    
      String query = "using lr for i\"" + PSI_PREFIX + "\"\n" +
          "insert lr:has_workflow_state( lr:state : lr:"+ state +" , lr:work : " + workplayerUrn + " )";
      runQuery(query,tm);
  }
  
  private void runQuery(String query, TopicMapIF tm){
    HashMap map = new HashMap();
    runQuery(query, tm, map);
  }

   private synchronized void runQuery(String query, TopicMapIF tm, Map map){
    QueryProcessorIF proc = QueryUtils.createQueryProcessor(tm);
    TopicMapStoreIF store = tm.getStore();
    store.open();
    try {
          int number = proc.update(query, map);
          System.out.println("*** Query processed successfully # "+ number + " ***");
          store.commit();
        } catch (Exception e) {
            System.err.println("*** Error while processing query: " + e.getLocalizedMessage() + " ***");
            System.out.println(query);
            throw new OntopiaRuntimeException(e);
        } finally{
          store.close();
        }
  }
  
  private void runQuery(String query){
    runQuery(query, topicmap);
  }
  
  private String urnify(String uuid){
    return "urn:uuid:" + uuid;
  }
  
  private String urnifyCtm(String uuid){
    return "<" + urnify(uuid) + ">";
  }
  
  private String findStructureUrnByStructureId(String structureId){
    String query ="select $PSI from\n" +
    		"subject-identifier($TOPIC, $PSI),\n" +
    		"topic-name($TOPIC, $BASENAME),\n" +
    		"value($BASENAME,\"" + structureId +"\")?"; // TODO: I think this passes for cheating using the oldName to store the structureId ... not sure.. ?

    String retval = getSingleStringFromQuery(query, topicmap);
    return retval;
    }


  private String getSingleStringFromQuery(String query, TopicMapIF tm){
    QueryResultIF result = executeQuery(query, tm);
     while(result.next()){
        Object[] results = new Object[result.getWidth()];
        results = result.getValues(results);
        Object retval = results[0];
        return (String) retval ;
      }
    return NULL;
  }
  
  private QueryResultIF executeQuery(String query, TopicMapIF tm){ 
    QueryProcessorIF proc = QueryUtils.createQueryProcessor(tm);
    try {
      QueryResultIF result = proc.execute(query);
     return result;
    } catch (InvalidQueryException e) {
      System.err.println("*** Error executing query  ***");
      System.err.println(query);
      throw new OntopiaRuntimeException(e);
    }
  }
  
  private void update(Object obj, String type) throws MalformedURLException{
    System.out.println("*** update called  for " + type +" ***");
    TopicMapStoreIF sourceStore = new InMemoryTopicMapStore();
    TopicMapIF sourceTm = sourceStore.getTopicMap(); // temporary topicmap f. sync

    // the deciders are filters for features not to be updated

    if(type.equalsIgnoreCase(WEBCONTENT_TYPE)){
      JournalArticle article = (JournalArticle) obj;
      addWebContent(article,sourceTm); // I think I could simply call createWebContent() instead and leave the deciders be empty?
      TopicIF source = retrieveTopicByUuid(article.getUuid(), sourceTm);
      TopicMapSynchronizer.update(topicmap, source , new WebContentDecider(), new WebContentDecider());
    } else if(type.equalsIgnoreCase(STRUCTURE_TYPE)){
      JournalStructure structure = (JournalStructure) obj;
      addStructure(structure,sourceTm);
      TopicIF source = retrieveTopicByUuid(structure.getUuid(), sourceTm);
      TopicMapSynchronizer.update(topicmap, source , new StructureDecider(), new StructureDecider());
    } else if(type.equalsIgnoreCase(USER_TYPE)){
      User user = (User) obj;
      addUser(user, sourceTm);
      TopicIF source = retrieveTopicByUuid(user.getUuid(), sourceTm);
      TopicMapSynchronizer.update(topicmap, source , new UserDecider(), new UserDecider());
    } else if(type.equals(WIKINODE_TYPE)){
      WikiNode node = (WikiNode) obj;
      createWikiNode(node, sourceTm);
      TopicIF source = retrieveTopicByUuid(node.getUuid(), sourceTm);
      TopicMapSynchronizer.update(topicmap, source , new WikiNodeDecider(), new WikiNodeDecider());
    } else if(type.equals(WIKIPAGE_TYPE)){
      WikiPage page = (WikiPage) obj;
      addWikiPage(page, sourceTm);
      TopicIF source = retrieveTopicByUuid(page.getUuid(), sourceTm);
      TopicMapSynchronizer.update(topicmap, source , new WikiPageDecider(), new WikiPageDecider());
    } else if(type.equals(COMMUNITY_TYPE)){ // TODO: See if the update code should go into an extra method
      Group group = (Group) obj;
      String objectId = getObjectIdByGroupId(String.valueOf(group.getGroupId()), topicmap);
      TopicIF topic = (TopicIF) topicmap.getObjectById(objectId);
      if(topic != null){
        TopicNameIF oldName = (TopicNameIF) topic.getTopicNames().iterator().next();
        HashMap<String, String> valueMap = new HashMap();
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
    System.out.println("*** update ended! ***");
  }

  private TopicIF retrieveTopicByUuid(String uuid, TopicMapIF tm){
    LocatorIF identifiablePsiLocator =  new GenericLocator("uri",urnify(uuid));
    TopicIF source = tm.getTopicBySubjectIdentifier(identifiablePsiLocator);
    return source;
  }

// for deciders
  public static boolean isInAssociation(String psi, AssociationIF assoc){
    TopicIF type = assoc.getType();
      try {
        LocatorIF locator = new URILocator(psi);
        Collection locators = type.getSubjectIdentifiers();
        if(locators.contains(locator)){
          return true;
        }

      } catch (MalformedURLException ex) {
        throw new OntopiaRuntimeException(ex);
      }
    return false;
  }

  public void addWikiNode(WikiNode node){
    addWikiNode(node, topicmap);
  }

  private void addWikiNode(WikiNode wikinode, TopicMapIF tm){
    createWikiNode(wikinode, tm);

    try {
      setCreator(wikinode.getUuid(), wikinode.getUserUuid(), tm);
    } catch (SystemException ex) {
      throw new OntopiaRuntimeException(ex);
    }
    setGroupContains(String.valueOf(wikinode.getGroupId()), wikinode.getUuid(), tm);
  }

  private void createWikiNode(WikiNode wikinode, TopicMapIF tm) {
    String nodeUrn = urnifyCtm(wikinode.getUuid());

    String lastPostDateString = "";
    if(wikinode.getLastPostDate() != null){
      lastPostDateString = "lr:lastpostdate : $lastPostDate; \n";
    }

    HashMap valueMap = getWikiNodeHashMap(wikinode);
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
      "$lastPostDate = %lastPostDate%, \n" +
      "$nodeId = %nodeId%";

    runQuery(query, tm, valueMap);
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

  public void addWikiPage(WikiPage wikipage){
    addWikiPage(wikipage, topicmap);
  }
  
  private void addWikiPage(WikiPage wikipage, TopicMapIF tm){
    createWikiPage(wikipage, tm);
    if(!wikipage.getParentPages().isEmpty()){
      for(WikiPage wpd : wikipage.getParentPages()){
        setParentChild(wpd.getUuid(), wikipage.getUuid(), tm);
      }
    }

    if(!wikipage.getChildPages().isEmpty()){
      for(WikiPage wpd : wikipage.getChildPages()){
        setParentChild(wikipage.getUuid(), wpd.getUuid(), tm);
      }
    }
    
    try {
      setCreator(wikipage.getUuid(), wikipage.getUserUuid(), tm);
    } catch (SystemException ex) {
      throw new OntopiaRuntimeException(ex);
    }

    setContains(wikipage.getNode().getUuid(), wikipage.getUuid(), tm);
  }
  
  private void createWikiPage(WikiPage wikipage, TopicMapIF tm) {
    String pageUrn = urnifyCtm(wikipage.getUuid());
    HashMap valueMap = getWikiPageHashMap(wikipage);
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

    System.out.println(query);
    runQuery(query, tm, valueMap);
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

  private void setParentChild(String parentUuid, String childUuid, TopicMapIF tm){
    String parentUrn = urnifyCtm(parentUuid);
    String childUrn = urnifyCtm(childUuid);

    String query = "using lr for i\"" + PSI_PREFIX + "\" \n" +
            "insert lr:parent-child( lr:parent : " + parentUrn + ", lr:child : " + childUrn +" )";

    System.out.println(query);
    runQuery(query, tm);
  }

    public void addGroup(Group group) {
    if(group.isCommunity()){
      addCommunity(group, topicmap);
    }
    // TODO: Handling of parent-groups?
  }
    
    private void addCommunity(Group group, TopicMapIF tm){
      System.out.println("*** addCommunity ***");
      HashMap valueMap = getGroupHashMap(group);
      String query ="using lr for i\"" + PSI_PREFIX  + "\"\n" +
               "insert ?group isa " + PSI_PREFIX + "community; \n" +
               "- $name; \n" +
               "lr:groupid : $groupId . from \n" +
               "$name = %name%, \n" +
               "$groupId = %groupId%";

       System.out.println(query);
       runQuery(query, tm, valueMap);
    }

  public void deleteGroup(Group group) {
    System.out.println("*** deleteGroup ***");
        String query ="using lr for i\"" + PSI_PREFIX  + "\"\n" +
            "delete $TOPIC from \n" +
            "value($OCCURRENCE, \"" + group.getGroupId() + "\")," +
            "type($OCCURRENCE, lr:groupid)," +
            "occurrence($TOPIC, $OCCURRENCE)," +
            "instance-of($TOPIC, lr:community)";
    System.out.println(query);
    runQuery(query);
  }

  public void updateGroup(Group group) {
    if(group.isCommunity()){
      try {
        update(group, COMMUNITY_TYPE);
      } catch (MalformedURLException ex) {
        throw new OntopiaRuntimeException(ex);
      }
    }
  }

  private void setContains(String containerUuid, String containeeUuid, TopicMapIF tm){
    String containeeUrn = urnifyCtm(containeeUuid);
    String containerUrn = urnifyCtm(containerUuid);
    String query ="using lr for i\"" + PSI_PREFIX  + "\"\n" +
            "insert lr:contains( lr:container : " + containerUrn + " , lr:containee : " + containeeUrn + " )";

    runQuery(query, tm);
  }

  private void setGroupContains(String groupId, String uuid, TopicMapIF tm){ //TODO: Could expect a long value instead of string for groupId then be renamed to setContains()
    System.out.println("*** setGroupContains ***");
    String groupObjectId = getObjectIdByGroupId(groupId, tm);
    TopicIF topic = (TopicIF) tm.getObjectById(groupObjectId);
    HashMap<String, TopicIF> map = new HashMap();
    map.put("topic", topic);

    String query ="using lr for i\"" + PSI_PREFIX  + "\"\n" +
            "insert lr:contains( lr:container : $topic  , lr:containee : " + urnifyCtm(uuid) + " ) from \n" +
            "$topic = %topic%";

    System.out.println(query);
    runQuery(query, tm, map);
  }

  private String getObjectIdByGroupId(String groupId, TopicMapIF tm){
    System.out.println("*** getTmIdByGroupId ***");
    String query ="using lr for i\"" + PSI_PREFIX  + "\"\n" +
            "select $ID from \n" +
            "object-id($TOPIC,$ID)," +
            "value($OCCURRENCE, \"" + groupId + "\")," +
            "type($OCCURRENCE, lr:groupid)," +
            "occurrence($TOPIC, $OCCURRENCE)," +
            "instance-of($TOPIC, lr:community)?";

    System.out.println(query);
    String retval = getSingleStringFromQuery(query, tm);
    System.out.println("*** ObjectId = " + retval + " ***");
    return retval;
  }

  

  // Hashmap methods following
  private HashMap getWebContentHashmap(JournalArticle content){
    HashMap<String, String> retval = new HashMap();
    retval.put("title", content.getTitle());
    retval.put("uuid", content.getUuid());
    retval.put("createDate", DateFormater.format(content.getCreateDate()));
    retval.put("displayDate", DateFormater.format(content.getDisplayDate()));
    retval.put("modifyDate", DateFormater.format(content.getModifiedDate()));
    retval.put("reviewDate", DateFormater.format(content.getReviewDate()));
    retval.put("expiryDate", DateFormater.format(content.getExpirationDate()));
    retval.put("articleId", content.getArticleId());
    retval.put("version", String.valueOf(content.getVersion()));
    retval.put("userId", String.valueOf(content.getUserId()));
    retval.put("structureId", content.getStructureId());
    retval.put("approvingUserId", String.valueOf(content.getApprovedByUserId()));
    retval.put("approvingUserName", content.getApprovedByUserName());
    retval.put("approvedDate", DateFormater.format(content.getApprovedDate()));

    try {
      retval.put("approvingUserUuid", content.getApprovedByUserUuid());
    } catch (SystemException ex) {
      throw new OntopiaRuntimeException(ex);
      //retval.put("approvingUserUuid", NULL);
    }
    
    try {
      retval.put("useruuid", content.getUserUuid());
    } catch (SystemException ex) {
      throw new OntopiaRuntimeException(ex);
      //retval.put("useruuid", NULL);
    }
    return retval;
  }

  private HashMap getUserHashMap(User user){
    HashMap<String, String> retval = new HashMap();

    retval.put("uuid", user.getUuid());
    retval.put("username", user.getEmailAddress());
    retval.put("urnCtm", urnify(user.getUuid()));
    return retval;
  }

  private HashMap getStructureHashMap(JournalStructure structure){
    HashMap<String, String> retval = new HashMap();

    retval.put("uuid", structure.getUuid());
    retval.put("structureId", structure.getStructureId());
    retval.put("parentId", structure.getParentStructureId());
    retval.put("name", structure.getName());
    return retval;
  }

  private HashMap getGroupHashMap(Group group){
    HashMap<String, String> retval = new HashMap();

    retval.put("groupId", String.valueOf(group.getGroupId()));
    retval.put("parentGroupId", String.valueOf(group.getParentGroupId()));
    retval.put("name", group.getName());
    return retval;
  }

  private HashMap getWikiNodeHashMap(WikiNode node){
    HashMap<String, String> retval = new HashMap();

    retval.put("uuid", node.getUuid());
    retval.put("nodeId", String.valueOf(node.getNodeId()));
    retval.put("name", node.getName());
    retval.put("createDate", DateFormater.format(node.getCreateDate()));
    retval.put("modifiedDate", DateFormater.format(node.getModifiedDate()));
    retval.put("lastPostDate", DateFormater.format(node.getLastPostDate()));
    try {
      retval.put("userUuid", node.getUserUuid());
    } catch (SystemException ex) {
      throw new OntopiaRuntimeException(ex);
      //retval.put("userUuid", NULL);
    }
    return retval;
  }

  private HashMap getWikiPageHashMap(WikiPage page){
    HashMap retval = new HashMap();

    retval.put("uuid", page.getUuid());
    retval.put("pageId", String.valueOf(page.getPageId()));
    retval.put("title", page.getTitle());
    retval.put("version", String.valueOf(page.getVersion()));
    retval.put("createDate", DateFormater.format(page.getCreateDate()));
    retval.put("modifyDate", DateFormater.format(page.getModifiedDate()));

    if(page.getParentPage() != null){
      retval.put("parentPageUuid", page.getParentPage().getUuid());
    }

    try {
      retval.put("userUuid", page.getUserUuid());
    } catch (SystemException ex) {
      throw new OntopiaRuntimeException(ex);
      //retval.put("userUuid", NULL);
    }
    return retval;
  }

  public String getObjectIdForUuid(String uuid) {
    TopicIF topic = retrieveTopicByUuid(uuid, topicmap); // may raise exception if topic can not be found. That's ok.
    if(topic != null){
      System.out.println("Topic to return : " + topic);
      return topic.getObjectId();
    } else {
      return NULL;
    }
  }

  public String getTopicMapId() {
    return TMNAME;
  }

  public String getTopicTypeIdForUuid(String uuid) {
    TopicIF topic = retrieveTopicByUuid(uuid, topicmap);
    if(topic != null){
      Collection collection = topic.getTypes();
      if(!collection.isEmpty()){
        Iterator collIt = collection.iterator();
        while(collIt.hasNext()){
          TopicIF type = (TopicIF) collIt.next(); //TODO: returns only the first type. What to do if there are more? Can there be more?!
          return type.getObjectId();
        }
      }
    }
    return NULL;
  }

  public String getConceptViewId(){
    TopicIF conceptView = topicmap.getTopicBySubjectIdentifier(new GenericLocator("uri", "http://psi.ontopia.net/liferay/conceptview"));
    return conceptView.getObjectId();

  }
  

}
