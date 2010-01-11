package listener;

import com.liferay.portal.ModelListenerException;
import com.liferay.portal.SystemException;
import com.liferay.portal.model.ModelListener;
import com.liferay.portlet.blogs.model.BlogsEntry;

public class BlogsEntryListener implements ModelListener<BlogsEntry>{

  public void onAfterAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onAfterCreate(BlogsEntry arg0) throws ModelListenerException {
    // these info might be of use .. 
    arg0.getUuid();
    arg0.getUserId();
    arg0.getUserName();
    arg0.getTitle();
    arg0.isNew();
    arg0.isDraft();
    arg0.getGroupId();
    arg0.getCreateDate();
    arg0.getDisplayDate();
    arg0.getModifiedDate();
    arg0.getEntryId();

    try {
      arg0.getUserUuid();
    } catch (SystemException e) {
      e.printStackTrace();
    }
  }

  public void onAfterRemove(BlogsEntry arg0) throws ModelListenerException {
    // TODO Auto-generated method stub
    
  }

  public void onAfterRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onAfterUpdate(BlogsEntry arg0) throws ModelListenerException {
    // TODO Auto-generated method stub
    
  }

  public void onBeforeAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onBeforeCreate(BlogsEntry arg0) throws ModelListenerException {
  }

  public void onBeforeRemove(BlogsEntry arg0) throws ModelListenerException {
  }

  public void onBeforeRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onBeforeUpdate(BlogsEntry arg0) throws ModelListenerException {
  }

}
