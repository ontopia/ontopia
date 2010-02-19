package listener;

import com.liferay.portal.ModelListenerException;
import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.model.ModelListener;
import com.liferay.portlet.tags.model.TagsEntry;



public class TagsEntryListener implements ModelListener<TagsEntry> {
  
  
  private void printAssociation(Object arg0, String arg1, Object arg2){
    System.out.println(arg0.getClass().toString() + ": " + arg0 + "-" + arg1.getClass().toString() + ": " + arg1 + "-" + arg2.getClass().toString() + ": "+ arg2);
  }
  
  private void printEntry(TagsEntry tEntry){
    
    System.out.println("Entry ID: " + tEntry.getEntryId());
    System.out.println("Parent EntryID: " + tEntry.getParentEntryId());
    System.out.println("Name: " + tEntry.getName());
    System.out.println("Create Date: "+ tEntry.getCreateDate());
    try {
      System.out.println("Vocabulary: " + tEntry.getVocabulary().getName());
    } catch (PortalException e) {
      System.out.println("PortalException while fetching Vocabulary: " + e.getLocalizedMessage());
      //e.printStackTrace();
    } catch (SystemException e) {
      System.out.println("SystemException while fetching Vocabulary: " + e.getLocalizedMessage());
      //e.printStackTrace();
    }
    
    System.out.println("PrimaryKey: " + tEntry.getPrimaryKey());
    System.out.println("Username: " + tEntry.getUserName());
  }

  public void onAfterAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
    System.out.println("### TagsEntryListener: onAfterAddAssociation ###");  
  }

  public void onAfterCreate(TagsEntry tEntry) throws ModelListenerException {
    System.out.println("### onAfterCreatTagsEntry ###");
  }

  public void onAfterRemove(TagsEntry tEntry) throws ModelListenerException {
    System.out.println("### onAfterRemoveTagsEntry ###");
  }

  public void onAfterRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onAfterUpdate(TagsEntry tEntry) throws ModelListenerException {
    System.out.println("### onAfterUpdateTagsEntry ###");   
  }

  public void onBeforeAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onBeforeCreate(TagsEntry tEntry) throws ModelListenerException {
  }

  public void onBeforeRemove(TagsEntry tEntry) throws ModelListenerException {
  }

  public void onBeforeRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onBeforeUpdate(TagsEntry tEntry) throws ModelListenerException {
  }
}
  