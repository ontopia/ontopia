package listener;
import com.liferay.portal.ModelListenerException;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.ModelListener;
import tm.OntopiaAdapter;



public class GroupListener implements ModelListener<Group>{

  public void onAfterAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onAfterCreate(Group group) throws ModelListenerException {
    System.out.println("### onAfterCreateGroup ###");
    OntopiaAdapter.instance.addGroup(group);
  }

  public void onAfterRemove(Group group) throws ModelListenerException {
    System.out.println("### onAfterRemoveGroup ###");
    OntopiaAdapter.instance.deleteGroup(group);
  }

  public void onAfterRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onAfterUpdate(Group group) throws ModelListenerException {
    System.out.println("### onAfterUpdateGroup ###");
    OntopiaAdapter.instance.updateGroup(group);
    
  }

  public void onBeforeAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onBeforeCreate(Group group) throws ModelListenerException {
  }

  public void onBeforeRemove(Group group) throws ModelListenerException {
  }

  public void onBeforeRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onBeforeUpdate(Group group) throws ModelListenerException {
  }

  private void printGroup(Group g){
    System.out.println("ParentGroup ID: " + g.getParentGroupId()); // for Communities this seems not to be of need
    System.out.println("FriendlyURL: " + g.getFriendlyURL()); // might serve as a psi
    System.out.println("Normalized URL: " );
    System.out.println("Name: " + g.getName()); // important
    System.out.println("GroupID: " + g.getGroupId()); // important
    System.out.println("PrimaryKey: " + g.getPrimaryKey());
    System.out.println("isOrg: " + g.isOrganization());
    System.out.println("isUser: " + g.isUser());
    System.out.println("isUserGroup: " + g.isUserGroup());
    System.out.println("isStagingGroup:" + g.isStagingGroup());
    System.out.println("isCommunity:" + g.isCommunity()); // this one is for difi's 'workspaces' probably. important
    System.out.println("isNew: " + g.isNew());
    System.out.println("isActive: " + g.isActive()); // maybe important?
    System.out.println("WfRoleNames: " + g.getWorkflowRoleNames());
    System.out.println("Wf Stages: " + g.getWorkflowStages());
    System.out.println("Default Private Plid: " + g.getDefaultPrivatePlid());
    System.out.println("Default Public Plid:" + g.getDefaultPublicPlid());
    System.out.println("Class: " + g.getClassName());
    g.getCreatorUserId();
    
  }
}
