package listener;
import com.liferay.portal.ModelListenerException;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.ModelListener;


public class GroupListener implements ModelListener<Group>{

  public void onAfterAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
    System.out.println("### GroupListener: onAfterAddAssociation ###");
    
  }

  public void onAfterCreate(Group arg0) throws ModelListenerException {
    System.out.println("### onAfterCreateGroup ###");
    printGroup(arg0);
  }

  public void onAfterRemove(Group arg0) throws ModelListenerException {
    System.out.println("### onAfterRemoveGroup ###");
    printGroup(arg0);
  }

  public void onAfterRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
    System.out.println("### GroupListener: onAfterRemoveAssociation ###");
    
  }

  public void onAfterUpdate(Group arg0) throws ModelListenerException {
    System.out.println("### onAfterUpdateGroup ###");
    printGroup(arg0);
    
  }

  public void onBeforeAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
    // TODO Auto-generated method stub
    
  }

  public void onBeforeCreate(Group arg0) throws ModelListenerException {
    // TODO Auto-generated method stub
    
  }

  public void onBeforeRemove(Group arg0) throws ModelListenerException {
    // TODO Auto-generated method stub
    
  }

  public void onBeforeRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
    // TODO Auto-generated method stub
    
  }

  public void onBeforeUpdate(Group arg0) throws ModelListenerException {
    // TODO Auto-generated method stub
    
  }

  private void printGroup(Group g){
    g.getParentGroupId(); // for Communities this seems not to be of need
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
    
  }
}
