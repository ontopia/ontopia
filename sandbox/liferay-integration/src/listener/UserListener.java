package listener;
import tm.OntopiaAdapter;

import com.liferay.portal.ModelListenerException;
import com.liferay.portal.model.ModelListener;
import com.liferay.portal.model.User;


public class UserListener implements ModelListener<User>{

  public void onAfterAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
    // TODO Auto-generated method stub
    
  }

  public void onAfterCreate(User arg0) throws ModelListenerException {
    System.out.println("### User Created! ###");
    OntopiaAdapter.instance.addUser(arg0);
    //printUserInfo(arg0);
  }

  public void onAfterRemove(User arg0) throws ModelListenerException {
   System.out.println("### User Removed! ###");
   OntopiaAdapter.instance.deleteUser(arg0.getUuid());
   //printUserInfo(arg0); 
  }

  public void onAfterRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
    // TODO Auto-generated method stub
    
  }

  public void onAfterUpdate(User arg0) throws ModelListenerException {
    System.out.println("### User updated! ###");
    OntopiaAdapter.instance.updateUser(arg0);
    //printUserInfo(arg0);
  }

  public void onBeforeAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
    // System.out.println("### OnBeforeCreateAssociation called with " + arg0.toString() + " - " + arg1 + " - " + arg1.toString());

  }

  public void onBeforeCreate(User arg0) throws ModelListenerException {
    // TODO Auto-generated method stub
    
  }

  public void onBeforeRemove(User arg0) throws ModelListenerException {
    // TODO Auto-generated method stub
    
  }

  public void onBeforeRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
    // TODO Auto-generated method stub
    
  }

  public void onBeforeUpdate(User arg0) throws ModelListenerException {
    // TODO Auto-generated method stub
    
  }
  
  private void printUserInfo(User user){
    System.out.println("User's full name: " + user.getFirstName() + " " + user.getLastName());
    System.out.println("Username/Emailaddress: " + user.getEmailAddress());
    System.out.println("UserID: " + user.getUserId());
    System.out.println("User's primary key: " + user.getPrimaryKey());
    System.out.println("User's UUID: " + user.getUuid());
    
  }

}
