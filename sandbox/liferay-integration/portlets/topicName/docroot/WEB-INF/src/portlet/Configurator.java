
package portlet;

import java.util.Map;
import java.util.List;
import javax.portlet.RenderRequest;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.PortletPreferences;
import com.liferay.portal.service.PortletPreferencesLocalServiceUtil;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.TopicMaps;
import net.ontopia.utils.OntopiaRuntimeException;

import tm.OntopiaAdapter;

/**
 * Storage of configuration for the TopicName portlet and logic for
 * looking up information in the topic map.
 * 
 * @author mfi
 */
public class Configurator {
  private String topicId;

  /**
   * Stores a topic id String in Memory.
   * Obsolete once <code>PortletPreferences</code> are used.
   *
   * @param tid The topic id to set
   */
  public void setTopicId(String tid){
    topicId = tid;
  }

  /**
   * Returns a (previously set) topic id.
   * Obsolete once <code>PortletPreferences</code> are used.
   *
   * @return The topic id which has been set, or null if none has been set.
   */
  public String getTopicId(){
    return topicId;
  }

  /**
   * Parses the topic id out of the URL if it has been provided like
   * ...?topicid=1234 for example
   *
   * @param renderRequest The request containing information about the
   * context of the portlet and the URL
   * @return A string containing the parsed topic id, or null if non
   * could be found
   */
  public String getTopicIdFromUrl(RenderRequest renderRequest){
    String queryString = (String) renderRequest.getAttribute("javax.servlet.forward.query_string");
    Map<String, String> params = util.PortletUtils.parseQueryString(queryString);
    return params.get("topic");
  }

  public String getTopicIdFromUrlByArticleId() {
    // Not yet implemented but should be quite similar to the one in
    // RelatedTopics. The only pain is if an article has more than
    // one "tag" attached to it. Which one should be displayed?  Or
    // should this show information about the topic representing the
    // article?
    return null;
  }

  /**
   * Returns the topic id of the topic representing the
   * <strong>article<strong> that's shown in the first available
   * WebContentDisplay on the same page.  Again, an article might have
   * associations to more than one "concept" topic (tag). Which one
   * should be displayed?  Method needs be changed to handle this.
   *
   * If the topic representing the article itself should be displayed
   * no further changes to this method are required.
   *
   * @param renderRequest The request containing information about the
   * context of the portlet and the URL
   * @return A String value containing the topic id of the topic
   * representing the article that's being shown in the first
   * available WebContentDisplay
   */
  public String findTopicIdFromNextWCD(RenderRequest renderRequest){
    Layout layout = (Layout) renderRequest.getAttribute(WebKeys.LAYOUT);
    if (layout == null)
      throw new OntopiaRuntimeException("Unable to find layout for this page!");

    String typesettings = layout.getTypeSettings(); // a string containing information about the portlets displayed on the page
    String portletId="";
    // this brings us the first WebContentDisplay Portlet (identified
    // by 56_INSTANCE_) on that page. Returns instance-id
    portletId = typesettings.substring(typesettings.indexOf("56_INSTANCE_"), typesettings.indexOf("56_INSTANCE_")+"56_INSTANCE_".length()+4);

    try{
      // These objects contain useful information like the article id
      // as a string
      List pref = PortletPreferencesLocalServiceUtil.getPortletPreferences(layout.getPlid(), portletId); 
      // TODO:there can only be one hit at the moment (on this page
      // there only is one wcd)
      // FIXME FIXME FIXME: this must be made more robust
      PortletPreferences pPrefs = (PortletPreferences) pref.get(0);
      // unfortunately there is no getter for the article id
      String preferences = pPrefs.getPreferences();
      // that's why I parse it out like this
      String articleId = preferences.substring(preferences.lastIndexOf("article-id</name><value>")+"article-id</name><value>".length(), preferences.lastIndexOf("</value>")).trim();

      // Now we need to get the topicId for this article Id! and
      // resolve the tags for it
      return OntopiaAdapter.getInstance(true).getCurrentObjectIdForArticleId(articleId);

    } catch (SystemException se) {
      // this SystemException is a liferay I don't know how to
      // handle properly.
      throw new OntopiaRuntimeException(se);
    }
  }

  /**
   * Asks the integration for the name of the topicmap which is in use
   * and returns a reference to it.
   *
   * @return A TopicMapIF object representing the topicmap which is
   * used by the integration.
   */
  public TopicMapIF getTopicmap(){
    String tmId = OntopiaAdapter.getInstance(true).getTopicMapId();
    TopicMapStoreIF store = TopicMaps.createStore(tmId, true);
    return store.getTopicMap();
  }

}
