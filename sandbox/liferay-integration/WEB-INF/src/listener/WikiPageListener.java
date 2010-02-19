package listener;

import com.liferay.portal.ModelListenerException;
import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.model.ModelListener;
import com.liferay.portlet.wiki.model.WikiPage;

import tm.OntopiaAdapter;

public class WikiPageListener implements ModelListener<WikiPage>{

  public void onAfterAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onAfterCreate(WikiPage page) throws ModelListenerException {
    System.out.println("### OnAfterCreate WikiPage ###");  
    OntopiaAdapter.instance.addWikiPage(page);
  }

  public void onAfterRemove(WikiPage page) throws ModelListenerException {
    System.out.println("### OnAfterRemove WikiPage ###");
    OntopiaAdapter.instance.deleteWikiPage(page.getUuid());
  }

  public void onAfterRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onAfterUpdate(WikiPage page) throws ModelListenerException {
    System.out.println("### OnAfterUpdate WikiPage ###");   
    OntopiaAdapter.instance.updateWikiPage(page);
  }

  public void onBeforeAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onBeforeCreate(WikiPage page) throws ModelListenerException {
  }

  public void onBeforeRemove(WikiPage page) throws ModelListenerException {
  }

  public void onBeforeRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onBeforeUpdate(WikiPage page) throws ModelListenerException {
  }
  
  private void printWikiPage(WikiPage page){
    System.out.println("Uuid: " + page.getUuid());
    System.out.println("Title: " +page.getTitle());
    System.out.println("CreateDate: " + page.getCreateDate());
    System.out.println("PageId: " + page.getPageId());
    System.out.println("ParentPages: " + page.getParentPages());
    System.out.println("ParentNodesUuid: " + page.getNode().getUuid());
    
    System.out.println("ModifiedDate: " + page.getModifiedDate());
    System.out.println("GroupId: " + page.getGroupId());
    System.out.println("ChildPages: " + page.getChildPages());
    
    System.out.println("UserID: " + page.getUserId());
    System.out.println("Username: " + page.getUserName());
    System.out.println("Version: " + page.getVersion());
    
    System.out.println("IsNew: " + page.isNew());
    try {
      System.out.println("Attachments: ");
      for(String s : page.getAttachmentsFiles()){
        System.out.println("File: " + s);
      }
    } catch (PortalException e1) {
      e1.printStackTrace();
    } catch (SystemException e1) {
      e1.printStackTrace();
    }
    
    try {
      System.out.println("UsersUuid: " + page.getUserUuid());
    } catch (SystemException e) {
      e.printStackTrace();
    }
  }

}
