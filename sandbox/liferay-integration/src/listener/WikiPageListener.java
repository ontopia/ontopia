package listener;

import com.liferay.portal.ModelListenerException;
import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.model.ModelListener;
import com.liferay.portlet.wiki.model.WikiPage;

public class WikiPageListener implements ModelListener<WikiPage>{

  public void onAfterAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onAfterCreate(WikiPage arg0) throws ModelListenerException {
    System.out.println("### OnAfterCreate WikiPage ###");  
    printWikiPage(arg0);
  }

  public void onAfterRemove(WikiPage arg0) throws ModelListenerException {
    System.out.println("### OnAfterRemove WikiPage ###");    
    printWikiPage(arg0);
  }

  public void onAfterRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onAfterUpdate(WikiPage arg0) throws ModelListenerException {
    System.out.println("### OnAfterUpdate WikiPage ###");   
    printWikiPage(arg0);
  }

  public void onBeforeAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onBeforeCreate(WikiPage arg0) throws ModelListenerException {
  }

  public void onBeforeRemove(WikiPage arg0) throws ModelListenerException {
  }

  public void onBeforeRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onBeforeUpdate(WikiPage arg0) throws ModelListenerException {
  }
  
  private void printWikiPage(WikiPage arg0){
    System.out.println("Uuid: " + arg0.getUuid());
    System.out.println("Title: " +arg0.getTitle());
    System.out.println("CreateDate: " + arg0.getCreateDate());
    System.out.println("PageId: " + arg0.getPageId());
    
    System.out.println("ParentPages: " + arg0.getParentPages());
    System.out.println("ParentNodesUuid: " + arg0.getNode().getUuid());
    
    System.out.println("ModifiedDate: " + arg0.getModifiedDate());
    System.out.println("GroupId: " + arg0.getGroupId());
    System.out.println("ChildPages: " + arg0.getChildPages());
    
    System.out.println("UserID: " + arg0.getUserId());
    System.out.println("Username: " + arg0.getUserName());
    System.out.println("Version: " + arg0.getVersion());
    
    System.out.println("IsNew: " + arg0.isNew()); 
    try {
      System.out.println("Attachments: " + arg0.getAttachmentsFiles());
    } catch (PortalException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    } catch (SystemException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    
    try {
      System.out.println("UsersUuid: " + arg0.getUserUuid());
    } catch (SystemException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
