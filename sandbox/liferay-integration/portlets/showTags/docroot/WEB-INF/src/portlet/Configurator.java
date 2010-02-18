
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
 * Ths class now provides the methods to get configuration information from URLs.
 * It also provides access to some methods of the integration.
 * 
 * @author mfi
 */
public class Configurator {

    public String getTopicIdFromUrl(RenderRequest renderRequest){
        // manualy parsing the topic id from the url
        String queryString = (String)renderRequest.getAttribute("javax.servlet.forward.query_string");
        if(queryString != null && (queryString.lastIndexOf("topic=") != -1)){
            String result = queryString.substring(queryString.lastIndexOf("topic=")+"topic=".length()); // get to the topic id number
            if(result.indexOf("&") != -1){ // there are more parameters
                result = result.substring(0, result.indexOf("&")); // the next ampersand is the delimiter for the topic id
                System.out.println("Configurator, TopicId from Url : " + result);
                return result;
            } else {
              // no more parameters, that means that we are at the end of the url
              System.out.println("Configurator, TopicId from Url : " + result);
              return result;
            }
        } else {
          // indexOf() returned -1. No topic information in the url available.
          return null;
        }
    }

    public String getTopicIdFromUrlByArticleId(RenderRequest renderRequest){
    // see above. Not very reusable I reckon.
      String queryString = (String)renderRequest.getAttribute("javax.servlet.forward.query_string");
        if(queryString != null && (queryString.lastIndexOf("article=") != -1)){
            String result = queryString.substring(queryString.lastIndexOf("article=")+"article=".length()); // get to the topic id number
            if(result.indexOf("&") != -1){ // there are more parameters
                result = result.substring(0, result.indexOf("&")); // the next ampersand is the delimiter for the topic id
                System.out.println("Configurator, ArticleId from Url : " + result);
                return OntopiaAdapter.instance.getCurrentObjectIdForArticleId(result);
            } else { // no more parameters, that means we are at the end of the url
              System.out.println("Configurator, ArticleId from Url : " + result);
                return OntopiaAdapter.instance.getCurrentObjectIdForArticleId(result);
            }
        } else {
          // indexOf() returned -1. No article information in the url available.
          return null;
        }
    }


    public String findTopicIdFromNextWCD(RenderRequest renderRequest){
        Layout layout = (Layout) renderRequest.getAttribute(WebKeys.LAYOUT);
        if(layout == null){
            throw new OntopiaRuntimeException("Configurator: Unable to find layout for this page!");
        }

        String typesettings = layout.getTypeSettings(); // a string containing information about the portlets displayed on the page
        String portletId="";

        try{
          // this brings us the first WebContentDisplay Portlet (identified by 56_INSTANCE_") on that page. Returns instance-id.
          portletId = typesettings.substring(typesettings.indexOf("56_INSTANCE_"), typesettings.indexOf("56_INSTANCE_")+"56_INSTANCE_".length()+4); 
          System.out.println("PortletId: '" + portletId + "'");
          System.out.println("plid: " + String.valueOf(layout.getPlid()));

          try{
            // These objects contain useful information like the article id as a string
            List pref = PortletPreferencesLocalServiceUtil.getPortletPreferences(layout.getPlid(), portletId);
            // TODO: hardwire the first hit is maybe not a good idea for the future.
            PortletPreferences pPrefs = (PortletPreferences) pref.get(0);
            // unfortunately there is no getter for the article id
            String preferences = pPrefs.getPreferences();
            // that's why I parse it out like this.
            String articleId = preferences.substring(preferences.lastIndexOf("article-id</name><value>")+"article-id</name><value>".length(), preferences.lastIndexOf("</value>")).trim();  
            System.out.println("ArticleID : " + articleId);

            return OntopiaAdapter.instance.getCurrentObjectIdForArticleId(articleId);

            } catch (Exception e){
                throw new OntopiaRuntimeException(e);
            }
          } catch(StringIndexOutOfBoundsException siobe){
            System.out.println("Configurator tries to find WCD but there is none!");
            return null;
          }
    }

    public TopicMapIF getTopicmap(){
        String tmId = OntopiaAdapter.instance.getTopicMapId();
        TopicMapStoreIF store = TopicMaps.createStore(tmId, false);
        TopicMapIF topicmap = store.getTopicMap();
        return topicmap;
    }

}
