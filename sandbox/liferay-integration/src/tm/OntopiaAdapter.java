package tm;

import java.net.MalformedURLException;
import util.StructureData;
import util.UserData;
import util.WebContentData;
import util.UuidIdentifiableIF;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.GenericLocator;
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

/**
 * This class provides control to alter a topicmap in ontopia according to changes in Liferay.
 */

public class OntopiaAdapter implements OntopiaAdapterIF{
  
  public static OntopiaAdapterIF instance = new OntopiaAdapter();
  
  private static final String TMNAME = "liferay_v30.ltm";
  
  private TopicMapIF topicmap;
  
  
  private OntopiaAdapter(){
    super();
    prepareTopicmap();
  }
  
  // pub2
  
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
    setCreator(content,tm);
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
    
    String query = "insert " + uuidUrn + " isa http://psi.ontopia.net/liferay/user;\n" +
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
      parent = "http://psi.ontopia.net/liferay/webcontent";
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
    
    String urn = urnifyCtm(content.getUuid());
    
    String query ="using lr for i\"http://psi.ontopia.net/liferay/\"\n" +
    		"insert " + urn + " isa " + classname + "; \n" +
        "- \"" + content.getTitle() + "\" ;\n" +
    		"lr:create_date : \"" + content.getCreateDate() + "\"; \n" +
    		"lr:approved_date : \"" + content.getApprovedDate() + "\"; \n" +
    		"lr:review_date : \"" + content.getReviewDate() + "\"; \n" +
    		"lr:expiry_date : \"" + content.getExpiryDate() + "\"; \n" +
    		"lr:modified_date : \"" + content.getModifyDate() + "\"; \n" +
    		"lr:display_date : \"" + content.getDisplayDate() + "\"; \n" +
    		"lr:version : \"" + content.getVersion() + "\"; \n" +
    		"lr:article_id : \"" + content.getArticleId() + "\" .";
    runQuery(query, tm);
  }
  
  
  private void setCreator(WebContentData content, TopicMapIF tm){
    System.out.println("*** setCreator ***");
    String creatorUrn = urnifyCtm(content.getUserUuid()); // TODO: might return "" in case of exception! Handling here
    String workUrn = urnifyCtm(content.getUuid());
    
    String query = "using lr for i\"http://psi.ontopia.net/liferay/\"\n" + 
        "insert lr:created_by( lr:creator : " + creatorUrn + " , lr:work : " + workUrn + " )";
    runQuery(query, tm);
  }
  
  private void setUserApproving(WebContentData content, TopicMapIF tm){
    System.out.println("*** setUserApproving ***");
    String workUrn = urnifyCtm(content.getUuid());
    String approverUrn = urnifyCtm(content.getApprovingUserUuid()); // TODO: What if approving User UUID is "" ? Handling here.
    
    String query = "using lr for i\"http://psi.ontopia.net/liferay/\"\n" + 
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
    
      String query = "using lr for i\"http://psi.ontopia.net/liferay/\"\n" + 
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
        return urnifyCtm((String) retval); // this is the expected case.
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
    TopicMapIF sourceTm = sourceStore.getTopicMap();

    String classname = identifiable.getClass().toString();
    
    // find out which class lies beneath and act accordingly
    // the deciders are filters for features not to be updated
    if(classname.equalsIgnoreCase(WebContentData.class.toString())){
      WebContentData article = (WebContentData) identifiable;
      addWebContent(article,sourceTm); 
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

  public void finalize(){
  }
  
}
