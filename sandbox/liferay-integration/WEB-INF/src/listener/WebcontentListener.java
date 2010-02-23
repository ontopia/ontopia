package listener;

import tm.OntopiaAdapter;

import com.liferay.portal.ModelListenerException;
import com.liferay.portal.model.BaseModelListener;
import com.liferay.portlet.journal.model.JournalArticle;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * This class is notified by liferay whenever changes in JournalArticle objects occur.
 * It passes these information on to the integration.
 *
 * In Liferay JournalArticle is everything that is called "Webcontent" in the Controlpanel (with the obvious exception of structures and templates).
 * Whenever content for a WebcontentDisplay or a page is created/updated/deleted, this listener will be triggered.
 * The name JournalArticle is there for historical reasons in liferay, as "Webcontent" was formerly called "Journal".
 *
 * @author mfi
 */

public class WebcontentListener extends BaseModelListener<JournalArticle> {

  private static Logger log = LoggerFactory.getLogger(WebcontentListener.class);

  public void onAfterCreate(JournalArticle article) throws ModelListenerException {
    log.debug("### OnAfterCreateArticle ###");
    OntopiaAdapter.instance.addWebContent(article);
  }
  
  public void onAfterRemove(JournalArticle article) throws ModelListenerException {
    log.debug("### OnAfterRemoveArticle ###");
    OntopiaAdapter.instance.deleteWebContent(article.getUuid());
  }

  public void onAfterUpdate(JournalArticle article) throws ModelListenerException {
    log.debug("### OnAfterUpdateArticle ###");
    OntopiaAdapter.instance.updateWebContent(article);
  }
}
