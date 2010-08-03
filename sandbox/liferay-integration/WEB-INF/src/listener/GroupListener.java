package listener;

import com.liferay.portal.ModelListenerException;
import com.liferay.portal.model.BaseModelListener;
import com.liferay.portal.model.Group;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import tm.OntopiaAdapter;

/**
 * This class is notified by liferay whenever changes in Group objects
 * occur.  It passes these information on to the integration. Groups
 * in Liferay comprehend user groups and communities among others.
 * Check group.is* methods for an idea what else there is.
 *
 * @author mfi
 */

public class GroupListener extends BaseModelListener<Group>{

  private static Logger log = LoggerFactory.getLogger(GroupListener.class);

  public void onAfterCreate(Group group) throws ModelListenerException {
    log.debug("### onAfterCreateGroup ###");
    OntopiaAdapter.getInstance().addGroup(group);
  }

  public void onAfterRemove(Group group) throws ModelListenerException {
    log.debug("### onAfterRemoveGroup ###");
    OntopiaAdapter.getInstance().deleteGroup(group);
  }


  public void onAfterUpdate(Group group) throws ModelListenerException {
    log.debug("### onAfterUpdateGroup ###");
    OntopiaAdapter.getInstance().updateGroup(group); 
  }
}
