package listener;

import com.liferay.portal.ModelListenerException;
import com.liferay.portal.SystemException;
import com.liferay.portal.model.ModelListener;
import com.liferay.portlet.wiki.model.WikiNode;

import tm.OntopiaAdapter;

public class WikiNodeListener implements ModelListener<WikiNode>{

  public void onAfterAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onAfterCreate(WikiNode node) throws ModelListenerException {
    System.out.println("### onAfterCreate WikiNode ###");
    OntopiaAdapter.instance.addWikiNode(node);
  }

  public void onAfterRemove(WikiNode node) throws ModelListenerException {
    System.out.println("### onAfterRemove WikiNode ###");
    OntopiaAdapter.instance.deleteWikiNode(node.getUuid());
  }

  public void onAfterRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onAfterUpdate(WikiNode node) throws ModelListenerException {
    System.out.println("### onAfterUpdate WikiNode ###");
    OntopiaAdapter.instance.updateWikiNode(node);
  }

  public void onBeforeAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onBeforeCreate(WikiNode node) throws ModelListenerException {
  }

  public void onBeforeRemove(WikiNode node) throws ModelListenerException {
  }

  public void onBeforeRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onBeforeUpdate(WikiNode node) throws ModelListenerException {
  }
  
  private void printNode(WikiNode node){
    System.out.println("Uuid: " + node.getUuid());
    System.out.println("Group: " + node.getGroupId());
    System.out.println("Name: " + node.getName());
    System.out.println("NodeId: " + node.getNodeId());
    System.out.println("CreateDate: " + node.getCreateDate());
    System.out.println("LastPost: " + node.getLastPostDate());
    System.out.println("Modified: " + node.getModifiedDate());
    System.out.println("Username: " + node.getUserName());
    try {
      System.out.println("UsersUuid: " + node.getUserUuid());
    } catch (SystemException ex) {
      ex.printStackTrace();
    }
  }

}
