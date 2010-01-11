package listener;
import com.liferay.portal.ModelListenerException;
import com.liferay.portal.model.ModelListener;
import com.liferay.portal.model.Layout;



public class LayoutListener implements ModelListener<Layout>{

  public void onAfterAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
    // TODO Auto-generated method stub
    
  }

  public void onAfterCreate(Layout arg0) throws ModelListenerException {
    System.out.println("### onAfterCreateLayout ###");
    printLayout(arg0);
    
  }

  public void onAfterRemove(Layout arg0) throws ModelListenerException {
    System.out.println("### onAfterRemoveLayout ###");
    printLayout(arg0);
    
  }

  public void onAfterRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
    // TODO Auto-generated method stub
    
  }

  public void onAfterUpdate(Layout arg0) throws ModelListenerException {
    System.out.println("### onAfterUpdateLayout ###");
    printLayout(arg0);
    
  }

  public void onBeforeAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
    // TODO Auto-generated method stub
    
  }

  public void onBeforeCreate(Layout arg0) throws ModelListenerException {
    // TODO Auto-generated method stub
    
  }

  public void onBeforeRemove(Layout arg0) throws ModelListenerException {
    // TODO Auto-generated method stub
    
  }

  public void onBeforeRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
    // TODO Auto-generated method stub
    
  }

  public void onBeforeUpdate(Layout arg0) throws ModelListenerException {
    // TODO Auto-generated method stub
    
  }
  
  private void printLayout(Layout l){
    //System.out.println(l.toString());
    System.out.println("FriendlyURL: " + l.getFriendlyURL());
    System.out.println("TypeSettings: " + l.getLayoutType().getTypeSettingsProperties());
  }

}
