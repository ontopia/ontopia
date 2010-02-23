package listener;

import tm.OntopiaAdapter;

import com.liferay.portal.ModelListenerException;
import com.liferay.portal.model.BaseModelListener;
import com.liferay.portlet.journal.model.JournalStructure;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * This class is notified by liferay whenever changes in JournalStructure objects occur.
 * * It passes these information on to the integration.
 * 
 * Structures are used by liferay whenever a user wants to design her own forms. 
 * In liferay structures require templates to be used. Templates define the visual appeal of a structure. There is no template listener yet.
 *
 * @author mfi
 */

public class JournalStructureListener extends BaseModelListener<JournalStructure>{

  private static Logger log = LoggerFactory.getLogger(JournalStructureListener.class);

  public void onAfterCreate(JournalStructure structure)
      throws ModelListenerException {
    log.debug("### onAfterCreateJournalStructure ###");
    OntopiaAdapter.instance.addStructure(structure);
  }

  public void onAfterRemove(JournalStructure structure)
      throws ModelListenerException {
    log.debug("### onAfterRemoveJournalStructure ###");
    OntopiaAdapter.instance.deleteStructure(structure.getUuid());
  }

  public void onAfterUpdate(JournalStructure structure)
      throws ModelListenerException {
    log.debug("### onAfterUpdateJournalStructure ###");
    OntopiaAdapter.instance.updateStructure(structure);
  }
}
