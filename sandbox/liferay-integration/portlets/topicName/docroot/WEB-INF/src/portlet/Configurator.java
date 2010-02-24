
package portlet;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.PortletPreferences;
import com.liferay.portal.service.PortletPreferencesLocalServiceUtil;

import java.util.List;
import javax.portlet.RenderRequest;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.TopicMaps;
import net.ontopia.utils.OntopiaRuntimeException;

import tm.OntopiaAdapter;

/**
 * Storage of configuration for the TopicName portlet +
 * logic for looking up things in the tm.
 * 
 * @author mfi
 */
public class Configurator {

  private String topicId = null;

  public void setTopicId(String tid){
    topicId = tid;
  }

  public String getTopicId(){
    return topicId;
  }

  public String getTopicIdFromUrl(RenderRequest renderRequest){
    // manualy parsing the topic id from the url
    String queryString = (String)renderRequest.getAttribute("javax.servlet.forward.query_string");
    if(queryString != null){
      String result = queryString.substring(queryString.lastIndexOf("topic=")+"topic=".length()); // get to the topic id number
      if(result.indexOf("&") != -1){ // there are more parameters
        result = result.substring(0, result.indexOf("&")); // the next ampersand is the delimiter for the topic id
        return result;
      } else { // no more parameters, that means we are at the end of the url
        return result;
      }
    } else {
      return null;
    }
  }

  public String getTopicIdFromUrlByArticleId() {
    // Not yet implemented
    return null;
  }


  public String findTopicIdFromNextWCD(RenderRequest renderRequest){
    Layout layout = (Layout) renderRequest.getAttribute(WebKeys.LAYOUT);
    if(layout == null){
        throw new OntopiaRuntimeException("Unable to find layout for this page!");
    }

    String typesettings = layout.getTypeSettings(); // a string containing information about the portlets displayed on the page
    String portletId="";
    // this brings us the first WebContentDisplay Portlet (identified by 56_INSTANCE_) on that page. Returns instance-id
    portletId = typesettings.substring(typesettings.indexOf("56_INSTANCE_"), typesettings.indexOf("56_INSTANCE_")+"56_INSTANCE_".length()+4);

    try{
      List pref = PortletPreferencesLocalServiceUtil.getPortletPreferences(layout.getPlid(), portletId); // These objects contain useful information like the article id as a string
      PortletPreferences pPrefs = (PortletPreferences) pref.get(0); // TODO:there can only be one hit at the moment (on this page there only is one wcd)
      String preferences = pPrefs.getPreferences(); // unfortunately there is no getter for the article id
      // that's why I parse it out like this
      String articleId = preferences.substring(preferences.lastIndexOf("article-id</name><value>")+"article-id</name><value>".length(), preferences.lastIndexOf("</value>")).trim();

      // Now we need to get the topicId for this article Id! and resolve the tags for it
      return OntopiaAdapter.instance.getCurrentObjectIdForArticleId(articleId);

      } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
      }
  }

  public TopicMapIF getTopicmap(){
      String tmId = OntopiaAdapter.instance.getTopicMapId();
      TopicMapStoreIF store = TopicMaps.createStore(tmId, false);
      TopicMapIF topicmap = store.getTopicMap();
      return topicmap;
  }

}
