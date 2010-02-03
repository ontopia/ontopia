package listener;

import tm.OntopiaAdapter;

import com.liferay.portal.ModelListenerException;
import com.liferay.portal.model.ModelListener;
import com.liferay.portlet.journal.model.JournalArticle;



public class WebcontentListener implements ModelListener<JournalArticle> {


  public void onAfterAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException { 
  }

  public void onAfterCreate(JournalArticle arg0) throws ModelListenerException {
    
    System.out.println("### OnAfterCreateArticle ###");
    //System.out.println("Passed argument:" + arg0);
    OntopiaAdapter.instance.addWebContent(arg0);
    
    //printArticle(arg0);   
    
  }

  
  public void onAfterRemove(JournalArticle arg0) throws ModelListenerException {
    System.out.println("### OnAfterRemoveArticle ###");
    OntopiaAdapter.instance.deleteWebContent(arg0.getUuid());
    //printArticle(arg0);
    
  }

  public void onAfterRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onAfterUpdate(JournalArticle arg0) throws ModelListenerException {
    System.out.println("### OnAfterUpdateArticle ###");
    //printArticle(arg0);    
    OntopiaAdapter.instance.updateWebContent(arg0);
  }

  public void onBeforeAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
    
  }

  public void onBeforeCreate(JournalArticle arg0) throws ModelListenerException {
    //System.out.println("### OnBeforeCreateArticle ###");
  }

  public void onBeforeRemove(JournalArticle arg0) throws ModelListenerException {
    //System.out.println("### OnBeforeRemoveArticle ###");    
  }

  public void onBeforeRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
    //System.out.println("### onBeforeRemoveAssociation ###");
  }

  public void onBeforeUpdate(JournalArticle arg0) throws ModelListenerException {
    //System.out.println("### OnBeforeUpdateArticle ###");
  }
  
  /* ==================== private methods ========================*/
  private void printArticle(JournalArticle arg0){
    System.out.println("Approved: " + arg0.getApproved());
    System.out.println("App from: "+ arg0.getApprovedByUserId());
    System.out.println("App from Name: "+ arg0.getApprovedByUserName());
    System.out.println("ArticleID: "+arg0.getArticleId());
    System.out.println("CreateDate: "+arg0.getCreateDate());
    System.out.println("DisplayDate: "+arg0.getDisplayDate());
    System.out.println("Expiration Date: "+arg0.getExpirationDate());
    System.out.println("GroupID: "+arg0.getGroupId());
    System.out.println("ID: "+arg0.getId());
    System.out.println("Modified Date: "+arg0.getModifiedDate());
    System.out.println("PrimaryKey: "+arg0.getPrimaryKey());
    System.out.println("RescPrimaryKey: "+arg0.getResourcePrimKey());
    System.out.println("ReviewDate: "+arg0.getReviewDate());
    System.out.println("StructureID: "+arg0.getStructureId());
    System.out.println("TemplateID: "+arg0.getTemplateId());
    System.out.println("Title: "+arg0.getTitle());
    System.out.println("Type: "+arg0.getType());
    System.out.println("URLTitle: " + arg0.getUrlTitle());
    System.out.println("UserID: " + arg0.getUserId());
    System.out.println("Username: " + arg0.getUserName());
    System.out.println("UUID: " + arg0.getUuid());
    System.out.println("Version: " + arg0.getVersion());
   }
  private void printAssociation(Object arg0, String arg1, Object arg2){
    System.out.println(arg0.getClass().toString() + ": " + arg0 + "-" + arg1.getClass().toString() + ": " + arg1 + "-" + arg2.getClass().toString() + ": "+ arg2);
  }
  
}
