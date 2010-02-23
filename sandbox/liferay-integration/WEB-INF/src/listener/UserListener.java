package listener;

import tm.OntopiaAdapter;

import com.liferay.portal.ModelListenerException;
import com.liferay.portal.model.BaseModelListener;
import com.liferay.portal.model.User;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * This class is notified by liferay whenever changes in User objects occur.
 * It passes these information on to the integration.
 *
 * @author mfi
 */

public class UserListener extends BaseModelListener<User>{

  private static Logger log = LoggerFactory.getLogger(UserListener.class);
  
  public void onAfterCreate(User user) throws ModelListenerException {
    log.debug("### User Created! ###");
    OntopiaAdapter.instance.addUser(user);
  }

  public void onAfterRemove(User user) throws ModelListenerException {
   log.debug("### User Removed! ###");
   OntopiaAdapter.instance.deleteUser(user.getUuid());
  }

  public void onAfterUpdate(User user) throws ModelListenerException {
    log.debug("### User updated! ###");
    OntopiaAdapter.instance.updateUser(user);
  }
}
