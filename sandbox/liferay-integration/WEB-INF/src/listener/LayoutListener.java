package listener;

import com.liferay.portal.ModelListenerException;
import com.liferay.portal.model.ModelListener;
import com.liferay.portal.model.Layout;



public class LayoutListener implements ModelListener<Layout>{

  public void onAfterAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onAfterCreate(Layout layout) throws ModelListenerException {
    System.out.println("### onAfterCreateLayout ###");
    
  }

  public void onAfterRemove(Layout layout) throws ModelListenerException {
    System.out.println("### onAfterRemoveLayout ###");
    
  }

  public void onAfterRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onAfterUpdate(Layout layout) throws ModelListenerException {
    System.out.println("### onAfterUpdateLayout ###");
  }

  public void onBeforeAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onBeforeCreate(Layout layout) throws ModelListenerException {
  }

  public void onBeforeRemove(Layout layout) throws ModelListenerException {
  }

  public void onBeforeRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onBeforeUpdate(Layout layout) throws ModelListenerException {
  }
  
  private void printLayout(Layout l){
    System.out.println("FriendlyURL: " + l.getFriendlyURL());
    System.out.println("TypeSettings: " + l.getLayoutType().getTypeSettingsProperties());
  }

}
