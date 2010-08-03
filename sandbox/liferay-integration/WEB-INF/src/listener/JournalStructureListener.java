package listener;

import tm.OntopiaAdapter;

import com.liferay.portal.ModelListenerException;
import com.liferay.portal.model.BaseModelListener;
import com.liferay.portlet.journal.model.JournalStructure;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * This class is notified by Liferay whenever changes in
 * JournalStructure objects occur. It passes these events on to
 * the integration.
 * 
 * <p>Structures are used by Liferay whenever a user wants to design
 * her own forms.  In Liferay structures require templates to be used.
 * Templates define the visual appeal of a structure. There is no
 * template listener yet.
 *
 * @author mfi
 */

public class JournalStructureListener extends BaseModelListener<JournalStructure>{

  private static Logger log = LoggerFactory.getLogger(JournalStructureListener.class);

  public void onAfterCreate(JournalStructure structure)
      throws ModelListenerException {
    log.debug("### onAfterCreateJournalStructure ###");
    OntopiaAdapter.getInstance().addStructure(structure);
  }

  public void onAfterRemove(JournalStructure structure)
      throws ModelListenerException {
    log.debug("### onAfterRemoveJournalStructure ###");
    OntopiaAdapter.getInstance().deleteStructure(structure.getUuid());
  }

  public void onAfterUpdate(JournalStructure structure)
      throws ModelListenerException {
    log.debug("### onAfterUpdateJournalStructure ###");
    OntopiaAdapter.getInstance().updateStructure(structure);
  }
}
