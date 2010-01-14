package listener;

import com.liferay.portal.ModelListenerException;
import com.liferay.portal.SystemException;
import com.liferay.portal.model.ModelListener;
import com.liferay.portlet.wiki.model.WikiNode;
import tm.OntopiaAdapter;
import util.WikiNodeData;

public class WikiNodeListener implements ModelListener<WikiNode>{

  public void onAfterAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onAfterCreate(WikiNode arg0) throws ModelListenerException {
    System.out.println("### onAfterCreate WikiNode ###");
    printNode(arg0);
    OntopiaAdapter.instance.addWikiNode(new WikiNodeData(arg0));
  }

  public void onAfterRemove(WikiNode arg0) throws ModelListenerException {
    System.out.println("### onAfterRemove WikiNode ###");
    printNode(arg0);
    OntopiaAdapter.instance.deleteWikiNode(arg0.getUuid());
  }

  public void onAfterRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onAfterUpdate(WikiNode arg0) throws ModelListenerException {
    System.out.println("### onAfterUpdate WikiNode ###");
    printNode(arg0);
    OntopiaAdapter.instance.updateWikiNode(new WikiNodeData(arg0));
  }

  public void onBeforeAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onBeforeCreate(WikiNode arg0) throws ModelListenerException {
    
  }

  public void onBeforeRemove(WikiNode arg0) throws ModelListenerException {
    
  }

  public void onBeforeRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onBeforeUpdate(WikiNode arg0) throws ModelListenerException {
  }
  
  private void printNode(WikiNode arg0){
    System.out.println("Uuid: " + arg0.getUuid());
    System.out.println("Group: " + arg0.getGroupId());
    System.out.println("Name: " + arg0.getName());
    System.out.println("NodeId: " + arg0.getNodeId());
    System.out.println("CreateDate: " + arg0.getCreateDate());
    System.out.println("LastPost: " + arg0.getLastPostDate());
    System.out.println("Modified: " + arg0.getModifiedDate());
    System.out.println("Username: " + arg0.getUserName());
    try {
      System.out.println("UsersUuid: " + arg0.getUserUuid());
    } catch (SystemException ex) {
      ex.printStackTrace();
    }
  }

}
