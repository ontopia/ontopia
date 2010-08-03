package listener;

import com.liferay.portal.ModelListenerException;
import com.liferay.portal.model.BaseModelListener;
import com.liferay.portlet.wiki.model.WikiPage;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import tm.OntopiaAdapter;

/**
 * This class is notified by liferay whenever changes in WikiPage objects occur.
 * It passes these information on to the integration.
 *
 * WikiPages are contained inside Wikis, which liferay calls "WikiNodes".
 * @see WikiNodeListener
 *
 * @author mfi
 */

public class WikiPageListener extends BaseModelListener<WikiPage>{

  private static Logger log = LoggerFactory.getLogger(WikiPageListener.class);

  public void onAfterCreate(WikiPage page) throws ModelListenerException {
    log.debug("### OnAfterCreate WikiPage ###");
    OntopiaAdapter.getInstance().addWikiPage(page);
  }

  public void onAfterRemove(WikiPage page) throws ModelListenerException {
    log.debug("### OnAfterRemove WikiPage ###");
    OntopiaAdapter.getInstance().deleteWikiPage(page.getUuid());
  }

  public void onAfterUpdate(WikiPage page) throws ModelListenerException {
    log.debug("### OnAfterUpdate WikiPage ###");
    OntopiaAdapter.getInstance().updateWikiPage(page);
  }
}
