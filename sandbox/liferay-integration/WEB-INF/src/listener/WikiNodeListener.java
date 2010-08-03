package listener;

import com.liferay.portal.ModelListenerException;
import com.liferay.portal.model.BaseModelListener;
import com.liferay.portlet.wiki.model.WikiNode;


import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import tm.OntopiaAdapter;

/**
 * This class is notified by liferay whenever changes in WikiNode
 * objects occur.  It passes these information on to the integration.
 *
 * WikiNodes is a liferay term for a Wiki as such (containing WikiPages).
 * @see WikiPageListener
 * 
 * Whenever a user creates/updates/deletes a Wiki in liferay, this
 * listener will be triggered.  This listener will also be triggered
 * whenever a WikiPage inside a WikiNode has been updated, as the
 * WikiNode contains information about the last change to that wiki.
 *
 * @author mfi
 */

public class WikiNodeListener extends BaseModelListener<WikiNode>{

  private static Logger log = LoggerFactory.getLogger(WikiNodeListener.class);

  public void onAfterCreate(WikiNode node) throws ModelListenerException {
    log.debug("### onAfterCreate WikiNode ###");
    OntopiaAdapter.getInstance().addWikiNode(node);
  }

  public void onAfterRemove(WikiNode node) throws ModelListenerException {
    log.debug("### onAfterRemove WikiNode ###");
    OntopiaAdapter.getInstance().deleteWikiNode(node.getUuid());
  }

  public void onAfterUpdate(WikiNode node) throws ModelListenerException {
    log.debug("### onAfterUpdate WikiNode ###");
    OntopiaAdapter.getInstance().updateWikiNode(node);
  }
}
