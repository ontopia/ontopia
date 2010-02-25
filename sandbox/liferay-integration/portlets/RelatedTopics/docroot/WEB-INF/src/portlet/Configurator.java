
package portlet;

import com.liferay.portal.SystemException;
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
 * Ths class now provides the methods to get configuration information from URLs.
 * It also provides access to some methods of the integration.
 * 
 * @author mfi
 */
public class Configurator {

  /**
   * Parses the topic id out of the URL
   *
   * @param renderRequest The request containing information on the context of the portlet and the URL
   * @return A String representing the topic id as provided in the URL or null if none could be found
   */
  public String getTopicIdFromUrl(RenderRequest renderRequest) {
      // manualy parsing the topic id from the url which is in the addressline of the browser. Not the renderURL of this portlet.
      String queryString = (String)renderRequest.getAttribute("javax.servlet.forward.query_string");
      if(queryString != null && (queryString.lastIndexOf("topic=") != -1)) {
          String result = queryString.substring(queryString.lastIndexOf("topic=")+"topic=".length()); // get to the topic id number
          if(result.indexOf("&") != -1) { // there are more parameters
              result = result.substring(0, result.indexOf("&")); // the next ampersand is the delimiter for the topic id
              return result;
          } else {
            // no more parameters, that means that we are at the end of the url
            return result;
          }
      } else {
        // indexOf() returned -1. No topic information in the url available.
        return null;
      }
  }

  /**
   * Parses the article id out of the URL, retrieves a topic that represents this article and returns its object id.
   *
   * @param renderRequest The request containing information on the context of this portlet and the URL
   * @return A String representing the topic id for the article id from the URL or null if whether article id or the id of the topic could not be found.
   */
  public String getTopicIdFromUrlByArticleId(RenderRequest renderRequest) {
  // see above. Not very reusable I reckon. Again using the URL "official" URL (see above) and not the renderURL of this portlet.
    String queryString = (String)renderRequest.getAttribute("javax.servlet.forward.query_string");
      if(queryString != null && (queryString.lastIndexOf("article=") != -1)) {
        String result = queryString.substring(queryString.lastIndexOf("article=")+"article=".length()); // get to the topic id number
        if(result.indexOf("&") != -1) { // there are more parameters
          result = result.substring(0, result.indexOf("&")); // the next ampersand is the delimiter for the topic id
          return OntopiaAdapter.instance.getCurrentObjectIdForArticleId(result);
        } else { // no more parameters, that means we are at the end of the url
          return OntopiaAdapter.instance.getCurrentObjectIdForArticleId(result);
        }
      } else {
        // indexOf() returned -1. No article information in the url available.
        return null;
      }
  }


  /**
   * Returns a topic id. It tries to read an article id out of the config of a (presumable existent) WebContentDisplay, it then tries to resolve the article id to a topic id.
   *
   * @param renderRequest The request containing information on the context of this portlet (like the layout in which it is viewed)
   * @return A String representing the object id of a topic, or null if no WebContentDisplay could be found or no object id could be resolved
   */
  public String findTopicIdFromNextWCD(RenderRequest renderRequest) {
    Layout layout = (Layout) renderRequest.getAttribute(WebKeys.LAYOUT);
    if(layout == null){
      throw new OntopiaRuntimeException("Configurator: Unable to find layout for this page!");
    }

    String typesettings = layout.getTypeSettings(); // a string containing information about the portlets displayed on the page
    String portletId="";

    try{
      // this brings us the first WebContentDisplay Portlet (identified by 56_INSTANCE_") on that page. Returns instance-id.
      portletId = typesettings.substring(typesettings.indexOf("56_INSTANCE_"), typesettings.indexOf("56_INSTANCE_")+"56_INSTANCE_".length()+4);

      try{
        // These objects contain useful information like the article id as a string
        List pref = PortletPreferencesLocalServiceUtil.getPortletPreferences(layout.getPlid(), portletId);
        // TODO: hardwire the first hit is maybe not a good idea for the future.
        PortletPreferences pPrefs = (PortletPreferences) pref.get(0);
        // unfortunately there is no getter for the article id
        String preferences = pPrefs.getPreferences();
        // that's why I parse it out like this.
        String articleId = preferences.substring(preferences.lastIndexOf("article-id</name><value>")+"article-id</name><value>".length(), preferences.lastIndexOf("</value>")).trim();

        return OntopiaAdapter.instance.getCurrentObjectIdForArticleId(articleId);

        } catch (SystemException se) {
          // not sure how to handle this SystemException so throwing it at runtime
          throw new OntopiaRuntimeException(se);
        }
    } catch(StringIndexOutOfBoundsException siobe) {
      // this is expected and not too unusual really. No WebContentDisplay there, to indicate the failure to produce useful information we return null.
      return null;
    }
  }

  /**
   * Gets a TopicMapIF object from ontopia after asking the integration for the id of the topicmap in use
   *
   * @return a TopicMapIF object
   */
  public TopicMapIF getTopicmap() {
      String tmId = OntopiaAdapter.instance.getTopicMapId();
      TopicMapStoreIF store = TopicMaps.createStore(tmId, false);
      TopicMapIF topicmap = store.getTopicMap();
      return topicmap;
  }

}
