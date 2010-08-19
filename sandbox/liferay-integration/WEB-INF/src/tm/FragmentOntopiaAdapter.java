package tm;

import java.io.InputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import com.liferay.portal.model.Group;
import com.liferay.portal.model.User;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.model.JournalStructure;
import com.liferay.portlet.wiki.model.WikiNode;
import com.liferay.portlet.wiki.model.WikiPage;
import com.liferay.portal.kernel.exception.SystemException;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.GenericLocator;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.entry.TopicMaps;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.topicmaps.query.utils.QueryWrapper;
import net.ontopia.topicmaps.utils.TopicMapSynchronizer;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import util.DateFormatter;

/**
 * This is a specialized version of the adapter which is used to
 * create an in-memory fragment representing some Liferay object. That
 * fragment is then used synchronized into the main topic map using
 * TMSync. This approach greatly simplifies updates.
 */
public class FragmentOntopiaAdapter extends OntopiaAdapter {
  private OntopiaAdapter base;
  
  protected FragmentOntopiaAdapter(OntopiaAdapter base) {
    // since we don't have a reference to the main topic map we cannot
    // look up needed information, and so we delegate to the base
    // adapter for these methods.
    this.base = base;

    // the main difference with the normal adapter is the next line, which
    // ensures modifications go into the fragment TM instead of the main TM
    setup(new InMemoryTopicMapStore());
  }

  protected TopicIF getTopicByGroupId(String groupId) {
    return base.getTopicByGroupId(groupId);
  }

  protected String findStructureUrnByStructureId(String structureId) {
    return base.findStructureUrnByStructureId(structureId);
  }
  
}
