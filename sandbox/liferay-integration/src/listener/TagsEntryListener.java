package listener;
import com.liferay.portal.ModelListenerException;
import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.model.ModelListener;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.tags.model.TagsAsset;
import com.liferay.portlet.tags.model.TagsEntry;



public class TagsEntryListener implements ModelListener<TagsEntry> {
  
  
  private void printAssociation(Object arg0, String arg1, Object arg2){
    System.out.println(arg0.getClass().toString() + ": " + arg0 + "-" + arg1.getClass().toString() + ": " + arg1 + "-" + arg2.getClass().toString() + ": "+ arg2);
  }
  
  private void printEntry(TagsEntry arg0){
    
    System.out.println("Entry ID: " + arg0.getEntryId());
    System.out.println("Parent EntryID: " + arg0.getParentEntryId());
    System.out.println("Name: " + arg0.getName());
    System.out.println("Create Date: "+ arg0.getCreateDate());
    try {
      System.out.println("Vocabulary: " + arg0.getVocabulary().getName());
    } catch (PortalException e) {
      System.out.println("PortalException while fetching Vocabulary: " + e.getLocalizedMessage());
      //e.printStackTrace();
    } catch (SystemException e) {
      System.out.println("SystemException while fetching Vocabulary: " + e.getLocalizedMessage());
      //e.printStackTrace();
    }
    
    System.out.println("PrimaryKey: " + arg0.getPrimaryKey());
    System.out.println("Username: " + arg0.getUserName());
  }

  public void onAfterAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
    System.out.println("### TagsEntryListener: onAfterAddAssociation ###");
    printAssociation(arg0, arg1, arg2);    
  }

  public void onAfterCreate(TagsEntry arg0) throws ModelListenerException {
    System.out.println("### onAfterCreatTagsEntry ###");
    printEntry(arg0);
  }

  public void onAfterRemove(TagsEntry arg0) throws ModelListenerException {
    System.out.println("### onAfterRemoveTagsEntry ###");
    printEntry(arg0);    
  }

  public void onAfterRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
    // TODO Auto-generated method stub
    
  }

  public void onAfterUpdate(TagsEntry arg0) throws ModelListenerException {
    System.out.println("### onAfterUpdateTagsEntry ###");
    printEntry(arg0);    
  }

  public void onBeforeAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
    // TODO Auto-generated method stub
    
  }

  public void onBeforeCreate(TagsEntry arg0) throws ModelListenerException {
    // TODO Auto-generated method stub
    
  }

  public void onBeforeRemove(TagsEntry arg0) throws ModelListenerException {
    // TODO Auto-generated method stub
    
  }

  public void onBeforeRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
    // TODO Auto-generated method stub
    
  }

  public void onBeforeUpdate(TagsEntry arg0) throws ModelListenerException {
    // TODO Auto-generated method stub
    
  }
}
  