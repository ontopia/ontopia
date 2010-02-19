package listener;

import tm.OntopiaAdapter;

import com.liferay.portal.ModelListenerException;
import com.liferay.portal.model.ModelListener;
import com.liferay.portal.model.User;


public class UserListener implements ModelListener<User>{

  public void onAfterAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onAfterCreate(User user) throws ModelListenerException {
    System.out.println("### User Created! ###");
    OntopiaAdapter.instance.addUser(user);
  }

  public void onAfterRemove(User user) throws ModelListenerException {
   System.out.println("### User Removed! ###");
   OntopiaAdapter.instance.deleteUser(user.getUuid());
  }

  public void onAfterRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onAfterUpdate(User user) throws ModelListenerException {
    System.out.println("### User updated! ###");
    OntopiaAdapter.instance.updateUser(user);
  }

  public void onBeforeAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onBeforeCreate(User user) throws ModelListenerException {
  }

  public void onBeforeRemove(User user) throws ModelListenerException {
  }

  public void onBeforeRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onBeforeUpdate(User user) throws ModelListenerException {
  }
  
  private void printUserInfo(User user){
    System.out.println("User's full name: " + user.getFirstName() + " " + user.getLastName());
    System.out.println("Username/Emailaddress: " + user.getEmailAddress());
    System.out.println("UserID: " + user.getUserId());
    System.out.println("User's primary key: " + user.getPrimaryKey());
    System.out.println("User's UUID: " + user.getUuid());
    
  }

}
