package listener;

import tm.OntopiaAdapter;

import com.liferay.portal.ModelListenerException;
import com.liferay.portal.model.ModelListener;
import com.liferay.portlet.journal.model.JournalArticle;



public class WebcontentListener implements ModelListener<JournalArticle> {


  public void onAfterAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException { 
  }

  public void onAfterCreate(JournalArticle article) throws ModelListenerException {
    System.out.println("### OnAfterCreateArticle ###");
    OntopiaAdapter.instance.addWebContent(article);
  }

  
  public void onAfterRemove(JournalArticle article) throws ModelListenerException {
    System.out.println("### OnAfterRemoveArticle ###");
    OntopiaAdapter.instance.deleteWebContent(article.getUuid());
  }

  public void onAfterRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onAfterUpdate(JournalArticle article) throws ModelListenerException {
    System.out.println("### OnAfterUpdateArticle ###");
    OntopiaAdapter.instance.updateWebContent(article);
  }

  public void onBeforeAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onBeforeCreate(JournalArticle article) throws ModelListenerException {
  }

  public void onBeforeRemove(JournalArticle article) throws ModelListenerException {
  }

  public void onBeforeRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onBeforeUpdate(JournalArticle article) throws ModelListenerException {
  }
  
  private void printArticle(JournalArticle article){
    System.out.println("Approved: " + article.getApproved());
    System.out.println("App from: "+ article.getApprovedByUserId());
    System.out.println("App from Name: "+ article.getApprovedByUserName());
    System.out.println("ArticleID: "+article.getArticleId());
    System.out.println("CreateDate: "+article.getCreateDate());
    System.out.println("DisplayDate: "+article.getDisplayDate());
    System.out.println("Expiration Date: "+article.getExpirationDate());
    System.out.println("GroupID: "+article.getGroupId());
    System.out.println("ID: "+article.getId());
    System.out.println("Modified Date: "+article.getModifiedDate());
    System.out.println("PrimaryKey: "+article.getPrimaryKey());
    System.out.println("RescPrimaryKey: "+article.getResourcePrimKey());
    System.out.println("ReviewDate: "+article.getReviewDate());
    System.out.println("StructureID: "+article.getStructureId());
    System.out.println("TemplateID: "+article.getTemplateId());
    System.out.println("Title: "+article.getTitle());
    System.out.println("Type: "+article.getType());
    System.out.println("URLTitle: " + article.getUrlTitle());
    System.out.println("UserID: " + article.getUserId());
    System.out.println("Username: " + article.getUserName());
    System.out.println("UUID: " + article.getUuid());
    System.out.println("Version: " + article.getVersion());
   }
  private void printAssociation(Object arg0, String arg1, Object arg2){
    System.out.println(arg0.getClass().toString() + ": " + arg0 + "-" + arg1.getClass().toString() + ": " + arg1 + "-" + arg2.getClass().toString() + ": "+ arg2);
  }
  
}
