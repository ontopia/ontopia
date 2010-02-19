package listener;
import tm.OntopiaAdapter;

import com.liferay.portal.ModelListenerException;
import com.liferay.portal.model.ModelListener;
import com.liferay.portlet.journal.model.JournalStructure;

public class JournalStructureListener implements ModelListener<JournalStructure>{

  public void onAfterAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException { 
  }

  public void onAfterCreate(JournalStructure structure)
      throws ModelListenerException {
    System.out.println("### onAfterCreateJournalStructure ###");
    OntopiaAdapter.instance.addStructure(structure);
  }

  public void onAfterRemove(JournalStructure structure)
      throws ModelListenerException {
    System.out.println("### onAfterRemoveJournalStructure ###");
    OntopiaAdapter.instance.deleteStructure(structure.getUuid());
    
  }

  public void onAfterRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onAfterUpdate(JournalStructure structure)
      throws ModelListenerException {
    System.out.println("### onAfterUpdateJournalStructure ###");
    OntopiaAdapter.instance.updateStructure(structure);
    
  }

  public void onBeforeAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onBeforeCreate(JournalStructure structure)
      throws ModelListenerException {
  }

  public void onBeforeRemove(JournalStructure structure)
      throws ModelListenerException {
  }

  public void onBeforeRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onBeforeUpdate(JournalStructure structure)
      throws ModelListenerException {
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
