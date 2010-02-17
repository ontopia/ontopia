
package portlet;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.PortletPreferences;
import com.liferay.portal.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.portlet.RenderRequest;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.TopicMaps;
import net.ontopia.utils.OntopiaRuntimeException;
import tm.OntopiaAdapter;

/**
 * This class provides some basic in memo storage for configuration parameters of its portlet.
 * Some of the stuff in here should go into PortletPreferences rather soon. Not everything will fit in there though.
 * 
 * @author mfi
 */
public class Configurator {
    
    public Configurator(){
    }

    private String topicId = null;
    
    private Set<String> assocOids = new HashSet<String>();

    // determine in what way the set of associations should be interpreted.
    private boolean blacklist = true;

    public void setTopicId(String tid, RenderRequest renderRequest){
      PortletPreferences pref = (PortletPreferences) renderRequest.getPreferences();
      
        topicId = tid;
        System.out.println("Configurator: TopicId has been set to " + topicId);
    }

    public boolean isAssocsWhitelist(){
      return !blacklist;
    }

    public boolean isAssocsBlacklist(){
      return blacklist;
    }
    
    public String getTopicId(){
        return topicId;
    }

    public String getTopicIdFromUrl(RenderRequest renderRequest){
        // manualy parsing the topic id from the url *brrrr*
        String queryString = (String)renderRequest.getAttribute("javax.servlet.forward.query_string");
        if(queryString != null && (queryString.lastIndexOf("topic=") != -1)){
            String result = queryString.substring(queryString.lastIndexOf("topic=")+"topic=".length()); // get to the topic id number
            if(result.indexOf("&") != -1){ // there are more parameters
                result = result.substring(0, result.indexOf("&")); // the next ampersand is the delimiter for the topic id
                System.out.println("Configurator, TopicId from Url : " + result);
                return result;
            } else { // no more parameters, that means we are at the end of the url
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

    public void addAssoctype(String oid){
      assocOids.add(oid);
      System.out.println("Configurator: AssocId has been added " + oid);
    }

    public Set getAssocOids(){
        return assocOids;
    }

    public void resetAssoctypes(){
      assocOids.clear();
    }

    public void resetTopicId(){
      topicId = null;
      System.out.println("Configurator: Reset TopicID!");
    }

    public String findTopicIdFromNextWCD(RenderRequest renderRequest){
        Layout layout = (Layout) renderRequest.getAttribute(WebKeys.LAYOUT);
        if(layout == null){
            throw new OntopiaRuntimeException("Configurator: Unable to find layout for this page!");
        }

        String typesettings = layout.getTypeSettings(); // a string containing information about the portlets displayed on the page
        String portletId="";

        try{
          portletId = typesettings.substring(typesettings.indexOf("56_INSTANCE_"), typesettings.indexOf("56_INSTANCE_")+"56_INSTANCE_".length()+4); // this brings us the first WebContentDisplay Portlet (identified by 56_INSTANCE_") on that page. Returns instance-id
          System.out.println("PortletId: '" + portletId + "'");
          System.out.println("plid: " + String.valueOf(layout.getPlid()));

          try{
              List pref = PortletPreferencesLocalServiceUtil.getPortletPreferences(layout.getPlid(), portletId); // These objects contain useful information like the article id as a string

              PortletPreferences pPrefs = (PortletPreferences) pref.get(0); // TODO:there can only be one hit at the moment (on this page there only is one wcd)
              String preferences = pPrefs.getPreferences(); // unfortunately there is no getter for the article id
              String articleId = preferences.substring(preferences.lastIndexOf("article-id</name><value>")+"article-id</name><value>".length(), preferences.lastIndexOf("</value>")).trim();  // that's why I parse it out like this. Awkward!
              System.out.println("ArticleID : " + articleId);
              // Now we need to get the topicId for this article Id!
              return OntopiaAdapter.instance.getCurrentObjectIdForArticleId(articleId);

              } catch (Exception e){
                  throw new OntopiaRuntimeException(e);
              }
          } catch(StringIndexOutOfBoundsException siobe){
            System.out.println("configurator tries to find WCD but there is none!");
            return null;
          }
    }

    public TopicMapIF getTopicmap(){
        String tmId = OntopiaAdapter.instance.getTopicMapId();
        TopicMapStoreIF store = TopicMaps.createStore(tmId, false);
        TopicMapIF topicmap = store.getTopicMap();
        return topicmap;
    }

    public String getPortalUrl(RenderRequest renderRequest){
        ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
        String portalUrl = themeDisplay.getPortalURL(); // needed to build the url for the links in view.jsp
        return portalUrl;
    }

}
