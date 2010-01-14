package tm;

import java.net.MalformedURLException;
import java.util.Collection;
import util.StructureData;
import util.UserData;
import util.WebContentData;
import util.UuidIdentifiableIF;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.GenericLocator;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.TopicMaps;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.topicmaps.utils.TopicMapSynchronizer;
import net.ontopia.utils.OntopiaRuntimeException;
import util.WikiNodeData;
import util.WikiPageData;

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

  public static final String NULL = "null";

  private static final String TMNAME = "liferay_v39.ltm";
  
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
  public void addWebContent(WebContentData content){
    addWebContent(content, topicmap);
  }
  
  private void addWebContent(WebContentData content, TopicMapIF tm){
    addArticle(content,tm); // handles also structures
    setWorkflowstate(content,tm);
    String creatorUrn = urnifyCtm(content.getUserUuid());
    setCreator(content, creatorUrn, tm);
    if(content.getIsApproved()){
      setUserApproving(content, tm);
    }
  }
  
  public void deleteWebContent(String uuid){
    deleteByUuid(uuid);
  }
  
  public void updateWebContent(WebContentData content){
    // using tmsync to implement update
    try {
      updateIdentifiable(content);
    } catch (MalformedURLException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  // Users
  public void addStructure(StructureData structure) {
    addStructure(structure, topicmap);    
  }

  public void addUser(UserData user) {
    addUser(user, topicmap);    
  }
  
  private void addUser(UserData user, TopicMapIF tm){
    System.out.println("*** addUser ***"); //DEBUG
    String uuidUrn = urnifyCtm(user.getUuid());
    
    String query = "insert " + uuidUrn + " isa " + PSI_PREFIX + "user;\n" +
    		"- \"" + user.getUsername() + "\"."; 
    runQuery(query, tm);
  }
  
  public void deleteUser(String uuid){
    deleteByUuid(uuid); 
  }
  
  public void updateUser(UserData user){
    try {
      updateIdentifiable(user);
    } catch (MalformedURLException e) {
        throw new OntopiaRuntimeException(e);    }
  }

  // Structures
  private void addStructure(StructureData structure, TopicMapIF tm){
    System.out.println("*** addStructure ***");
    String structureUrn = urnifyCtm(structure.getUuid());
    String parentStructureUrn = findStructureUrnByStructureId(structure.getParentId());
    String id = structure.getStructureId();
    
    String parent;
    //System.out.println("*** DEBUG: ParentStructureUrn: '" + parentStructureUrn + "' ***");
    if(parentStructureUrn.equals(urnifyCtm(""))){
      System.out.println("*** No parentstructure provided. Using webcontent instead ***");
      parent = PSI_PREFIX + "webcontent";
    } else {
      parent = parentStructureUrn;
    }
    
    String query ="insert " + structureUrn + " ako " + parent + ";\n" +
    		"- \"" + id + "\" .";
    runQuery(query, tm); 
  }
  
  public void deleteStructure(String uuid){
    deleteByUuid(uuid);
  }
  
  public void updateStructure(StructureData structure){
    try {
      updateIdentifiable(structure);
    } catch (MalformedURLException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  

  // private methods
  private void deleteByUuid(String uuid){
    System.out.println("*** delete " + uuid + " ***");
    String psi = urnify(uuid);
    String query = "delete i\"" + psi +"\"";
    runQuery(query);
  }
  
  private void addArticle(WebContentData content, TopicMapIF tm){
    System.out.println("*** addArticle ***");
    String classname;
    if(content.getStructureId().equals("")){
      classname = "lr:article";
    } else {
      classname = findStructureUrnByStructureId(content.getStructureId()); // may return "" -> will cause query to throw exception
    }

    String approvedDateString = "";
    if(content.getIsApproved()){
      approvedDateString = "lr:approved_date : \"" + content.getApprovedDate() + "\"; \n";
    }
    
    String reviewDateString = "";
    if(!content.getReviewDate().equalsIgnoreCase(NULL)){
      reviewDateString = "lr:review_date : \"" + content.getReviewDate() + "\"; \n";
    }
    
    String expiryDateString = "";
    if(!content.getExpiryDate().equalsIgnoreCase(NULL)){
      expiryDateString = "lr:expiry_date : \"" + content.getExpiryDate() + "\"; \n";
    }

    String urn = urnifyCtm(content.getUuid());
    
    String query ="using lr for i\"" + PSI_PREFIX  + "\"\n" +
    		"insert " + urn + " isa " + classname + "; \n" +
        "- \"" + content.getTitle() + "\" ;\n" +
    		"lr:create_date : \"" + content.getCreateDate() + "\"; \n" +
    		approvedDateString +
    		reviewDateString +
    		expiryDateString +
    		"lr:modified_date : \"" + content.getModifyDate() + "\"; \n" +
    		"lr:display_date : \"" + content.getDisplayDate() + "\"; \n" +
    		"lr:version : \"" + content.getVersion() + "\"; \n" +
    		"lr:article_id : \"" + content.getArticleId() + "\" .";
    runQuery(query, tm);
  }
  
  
  private void setCreator(UuidIdentifiableIF content, String creatorUrn, TopicMapIF tm){
    // TODO: creatorUrn might be "" in case of exception! Handling here
    System.out.println("*** setCreator ***");
    String workUrn = urnifyCtm(content.getUuid());
    
    String query = "using lr for i\"" + PSI_PREFIX + "\"\n" +
        "insert lr:created_by( lr:creator : " + creatorUrn + " , lr:work : " + workUrn + " )";
    runQuery(query, tm);
  }
  
  private void setUserApproving(WebContentData content, TopicMapIF tm){
    System.out.println("*** setUserApproving ***");
    String workUrn = urnifyCtm(content.getUuid());
    String approverUrn = urnifyCtm(content.getApprovingUserUuid()); // TODO: What if approving User UUID is "" ? Handling here.
    
    String query = "using lr for i\"" + PSI_PREFIX + "\"\n" +
    "insert lr:approved_by( lr:approver : " + approverUrn + " , lr:work : " + workUrn + " )";
    runQuery(query,tm);
  }
  
  
  private void setWorkflowstate(WebContentData content, TopicMapIF tm){
    System.out.println("*** setWorkflowState ***");
    
    String workplayerUrn = urnifyCtm(content.getUuid());
    String state;
    
    if(content.getIsExpired()){ 
      state = "workflow_expired";
    } else if(content.getIsApproved()){
      state = "workflow_approved";
    } else {
      state ="workflow_new";
    }
    
      String query = "using lr for i\"" + PSI_PREFIX + "\"\n" +
          "insert lr:has_workflow_state( lr:state : lr:"+ state +" , lr:work : " + workplayerUrn + " )";
      runQuery(query,tm);
  }
  
  private synchronized void runQuery(String query, TopicMapIF tm){
    QueryProcessorIF proc = QueryUtils.createQueryProcessor(tm);
    TopicMapStoreIF store = tm.getStore();
    store.open();
    try {
          int number = proc.update(query);
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
    QueryProcessorIF proc = QueryUtils.createQueryProcessor(topicmap);
    String query ="select $PSI from\n" +
    		"subject-identifier($TOPIC, $PSI),\n" +
    		"topic-name($TOPIC, $BASENAME),\n" +
    		"value($BASENAME,\"" + structureId +"\")?";
    
    try {
      QueryResultIF result = proc.execute(query);// this might go into a separate method in the end. We may need it more often
      System.out.println("*** Looking up structure by structureId, found: " + result.getWidth() + " ***");
      while(result.next()){
        Object[] results = new Object[result.getWidth()];
        results = result.getValues(results);
        Object retval = results[0];
        System.out.println("retval in findStructure is: " + (String) retval);
        return "<" + (String) retval + ">"; // append <> to make it understandable for ctm
      }
    } catch (InvalidQueryException e) {
      System.err.println("*** Error executing query to findStructureByStructureId! ***");
      System.err.println(query);
      throw new OntopiaRuntimeException(e);
    }
    return urnifyCtm(""); // this may change to return "";
  }
  
  private void updateIdentifiable(UuidIdentifiableIF identifiable) throws MalformedURLException{
    System.out.println("*** updateIdentifiable called for " + identifiable.getUuid() + " ***");
    TopicMapStoreIF sourceStore = new InMemoryTopicMapStore();
    TopicMapIF sourceTm = sourceStore.getTopicMap(); // temporary topicmap f. sync

    String classname = identifiable.getClass().toString();
    
    // find out which class lies beneath and act accordingly
    // the deciders are filters for features not to be updated
    if(classname.equalsIgnoreCase(WebContentData.class.toString())){
      WebContentData article = (WebContentData) identifiable;
      addWebContent(article,sourceTm); // I think I could simply call addArticle() instead and leave the deciders be empty?
      TopicIF source = retrieveTopicByUuid(identifiable, sourceTm);
      TopicMapSynchronizer.update(topicmap, source , new WebContentDecider(), new WebContentDecider());
    } else if(classname.equalsIgnoreCase(StructureData.class.toString())){
      StructureData structure = (StructureData) identifiable;
      addStructure(structure,sourceTm);
      TopicIF source = retrieveTopicByUuid(identifiable, sourceTm);
      TopicMapSynchronizer.update(topicmap, source , new StructureDecider(), new StructureDecider());
    } else if(classname.equalsIgnoreCase(UserData.class.toString())){
      UserData user = (UserData) identifiable;
      addUser(user, sourceTm);
      TopicIF source = retrieveTopicByUuid(identifiable, sourceTm);
      TopicMapSynchronizer.update(topicmap, source , new UserDecider(), new UserDecider());
    } // other classes to be considered go here 
     
  }

  private TopicIF retrieveTopicByUuid(UuidIdentifiableIF identifiable, TopicMapIF tm){
    LocatorIF identifiablePsiLocator =  new GenericLocator("uri",urnify(identifiable.getUuid()));
    TopicIF source = tm.getTopicBySubjectIdentifier(identifiablePsiLocator);
    return source;
  }


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

  public void addWikiNode(WikiNodeData wikinode){
    addWikiNode(wikinode, topicmap);
    setCreator(wikinode, urnifyCtm(wikinode.getUserUuid()), topicmap);
    //may need assoc to connect to community
  }

  private void addWikiNode(WikiNodeData wikinode, TopicMapIF tm) {
    String nodeUrn = urnifyCtm(wikinode.getUuid());
    String query = "using lr for i\"" + PSI_PREFIX + "\" \n" +
      "insert " + nodeUrn + " isa lr:wikinode; \n" +
      "- \"" + wikinode.getName() + "\"; \n" +
      "lr:create_date : \"" + wikinode.getCreateDate() + "\"; \n" +
      "lr:modified_date : \"" + wikinode.getModifiedDate() + "\"; \n" +
      "lr:lastpostdate : \"" + wikinode.getLastPostDate() + "\"; \n" +
      "lr:wikinodeid : \"" + wikinode.getNodeId() + "\" .";

    runQuery(query, tm);
  }

  public void deleteWikiNode(String uuid) {
    deleteByUuid(uuid);
  }

  public void updateWikiNode(WikiNodeData wikinode) {
    try {
      updateIdentifiable(wikinode);
    } catch (MalformedURLException ex) {
      throw new OntopiaRuntimeException(ex);
    }
  }

  public void addWikiPage(WikiPageData wikipage){
    addWikiPage(wikipage, topicmap);
    if(!wikipage.getParentPages().isEmpty()){
      System.out.println("DEBUG: Parentpages for this wikipage!");
      for(WikiPageData wpd : wikipage.getParentPages()){
        setParentChild(wpd, wikipage, topicmap);
      }
    }
    setCreator(wikipage, urnifyCtm(wikipage.getUserUuid()), topicmap);
  }
  
  private void addWikiPage(WikiPageData wikipage, TopicMapIF tm) {
    String pageUrn = urnifyCtm(wikipage.getUuid());
    String query = "using lr for i\"" + PSI_PREFIX + "\" \n" +
             "insert " + pageUrn + " isa lr:wikipage; \n" +
             "- \"" + wikipage.getTitle() + "\"; \n" +
             "lr:wikipageid : \"" + wikipage.getPageId() + "\"; \n"+
             "lr:create_date : \"" + wikipage.getCreateDate() + "\"; \n" +
             "lr:modified_date : \"" + wikipage.getModifyDate() + "\"; \n" +
             "lr:version : \"" + wikipage.getVersion() + "\" .";

    System.out.println(query);
     runQuery(query, tm);
  }

  public void deleteWikiPage(String uuid) {
    deleteByUuid(uuid);
  }

  public void updateWikiPage(WikiPageData wikipage) {
    try {
      updateIdentifiable(wikipage);
    } catch (MalformedURLException ex) {
      throw new OntopiaRuntimeException(ex);
    }
  }

  private void setParentChild(UuidIdentifiableIF parent, UuidIdentifiableIF child, TopicMapIF tm){
    String parentUrn = urnifyCtm(parent.getUuid());
    String childUrn = urnifyCtm(child.getUuid());

    String query = "using lr for i\"" + PSI_PREFIX + "\" \n" +
            "insert lr:parent-child( lr:parent : " + parentUrn + ", lr:child : " + childUrn +" )";

    System.out.println(query);
    runQuery(query, tm);
  }

    public void finalize(){
  }
  
}
