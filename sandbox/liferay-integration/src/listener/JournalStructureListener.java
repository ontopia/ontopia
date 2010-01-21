package listener;
import tm.OntopiaAdapter;

import com.liferay.portal.ModelListenerException;
import com.liferay.portal.model.ModelListener;
import com.liferay.portlet.journal.model.JournalStructure;

public class JournalStructureListener implements ModelListener<JournalStructure>{

  public void onAfterAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
    // System.out.println("### JournalStructure: onAfterAddAssociation ###");    
  }

  public void onAfterCreate(JournalStructure arg0)
      throws ModelListenerException {
    System.out.println("### onAfterCreateJournalStructure ###");
    OntopiaAdapter.instance.addStructure(arg0);
    //printStructure(arg0);
  }

  public void onAfterRemove(JournalStructure arg0)
      throws ModelListenerException {
    System.out.println("### onAfterRemoveJournalStructure ###");
    OntopiaAdapter.instance.deleteStructure(arg0.getUuid());
    //printStructure(arg0);
    
  }

  public void onAfterRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
    // System.out.println("### JournalStructure: onAfterRemoveAssociation ###");
    
  }

  public void onAfterUpdate(JournalStructure arg0)
      throws ModelListenerException {
    System.out.println("### onAfterUpdateJournalStructure ###");
    //printStructure(arg0);
    OntopiaAdapter.instance.updateStructure(arg0);
    
  }

  public void onBeforeAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
    // TODO Auto-generated method stub
    
  }

  public void onBeforeCreate(JournalStructure arg0)
      throws ModelListenerException {
    // TODO Auto-generated method stub
    
  }

  public void onBeforeRemove(JournalStructure arg0)
      throws ModelListenerException {
    // TODO Auto-generated method stub
    
  }

  public void onBeforeRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
    // TODO Auto-generated method stub
    
  }

  public void onBeforeUpdate(JournalStructure arg0)
      throws ModelListenerException {
    // TODO Auto-generated method stub
    
  }
  
  private void printStructure(JournalStructure structure){
   System.out.println("UUID: " + structure.getUuid());
   System.out.println("Parent StructureID: " + structure.getParentStructureId());
   System.out.println("ID: " + structure.getId());
   System.out.println("Name: " + structure.getName());
   System.out.println("StructureID: " + structure.getStructureId());
   System.out.println("PrimaryKey: " + structure.getPrimaryKey());
   System.out.println("GroupID: "+ structure.getGroupId());
   System.out.println("UserID: " + structure.getUserId());
   System.out.println("Username: " + structure.getUserName());
  }

}
