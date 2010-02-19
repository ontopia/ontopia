package listener;

import com.liferay.portal.ModelListenerException;
import com.liferay.portal.SystemException;
import com.liferay.portal.model.ModelListener;
import com.liferay.portlet.blogs.model.BlogsEntry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BlogsEntryListener implements ModelListener<BlogsEntry>{

  public void onAfterAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onAfterCreate(BlogsEntry entry) throws ModelListenerException {
  }

  public void onAfterRemove(BlogsEntry entry) throws ModelListenerException {
  }

  public void onAfterRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onAfterUpdate(BlogsEntry entry) throws ModelListenerException {
  }

  public void onBeforeAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onBeforeCreate(BlogsEntry entry) throws ModelListenerException {
  }

  public void onBeforeRemove(BlogsEntry entry) throws ModelListenerException {
  }

  public void onBeforeRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onBeforeUpdate(BlogsEntry entry) throws ModelListenerException {
  }

  private void printBlogsEntry(BlogsEntry entry){
    System.out.println("Company ID: " + entry.getCompanyId());
    System.out.println("Title: " + entry.getTitle());
    System.out.println("URL Title: " + entry.getUrlTitle());
    System.out.println("Username: " + entry.getUserName());
    System.out.println("User ID: " + entry.getUserId());
    System.out.println("Uuid: " + entry.getUuid());
    System.out.println("Create Date: " + entry.getCreateDate());
    System.out.println("Display Date: " + entry.getDisplayDate());
    System.out.println("Modified Date: " + entry.getModifiedDate());
    System.out.println("Entry ID: " + entry.getEntryId());

    try {
      System.out.println("User's Uuid: " + entry.getUserUuid());
    } catch (SystemException ex) {
      ex.printStackTrace();
    }

    // there are more infos available. Check out the BlogsEntry Classes JavaDoc.
  }

}
