/**
 * Some logic for displaying the appropriate topics on the JSPs
 */

package portlet;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * ShowTags will display the tags of an article. It will try to look up the topic for that article in the topic map and check
 * what concepts are associated with it. This class dispatches the incoming requests to the right JSPs and reads and writes some config.
 *
 * @author Matthias Fischer
 *
 */
public class ShowTagsPortlet extends GenericPortlet {

	public void init() throws PortletException {
		editJSP = getInitParameter("edit-jsp");
		helpJSP = getInitParameter("help-jsp");
		viewJSP = getInitParameter("view-jsp");
        config = new Configurator();
	}

	public void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		String jspPage = renderRequest.getParameter("jspPage");

		if (jspPage != null) {
			include(jspPage, renderRequest, renderResponse);
		}
		else {
			super.doDispatch(renderRequest, renderResponse);
		}
	}

	public void doEdit(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		if (renderRequest.getPreferences() == null) {
			super.doEdit(renderRequest, renderResponse);
		}
		else {
            // Most of this will have to be rethought as it should use PortletPreferences to store customizations.
            // This requires a change in the edit.jsp and the actuel storing takes place in processAction() in this class
            // An ActionURL is required to trigger the processAction() method. actionUrls can be obtained through taglibs or code.
            // some parts of the Configurator will become parts of the PortletPreferences, but probably not everything (like finding the next wcd)
            // only stuff that needs static storage seems to belong into the PortletPreferences
      
            String topicid = renderRequest.getParameter("topicid");
            String associds = renderRequest.getParameter("associd");

            // associd contains a comma separated list of oids for associations -> deserialize
            if(associds != null){
              config.resetAssoctypes(); // clear the previous set of associds
              String[] assocArray = associds.split(",");
              for(String s : assocArray){
                s = s.trim();
                Integer.parseInt(s); // provoke an Exception here to make it easier to track down problems with providing letters i.e.
                config.addAssoctype(s);
              }
            }

            if(topicid != null){
              config.setTopicId(topicid, renderRequest);
            } else {
              // failed? try attributes
              String topicAtt = (String) renderRequest.getAttribute("topicid");

              if(topicAtt != null){
                config.setTopicId(topicAtt, renderRequest);
              }
            }


      // Get the topic id from the config and pass them to the edit page to display them to the user
      String topicParam = config.getTopicId();
      renderRequest.setAttribute("topicid", topicParam);
      renderRequest.setAttribute("associd", config.getAssocOids());
			include(editJSP, renderRequest, renderResponse);
		}
	}

	public void doHelp(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		include(helpJSP, renderRequest, renderResponse);
	}

	public void doView(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

    // trying logger here instead of stdout just to see how it works out

      _log.debug("## ShowTagsPortlet.doView(), RenderResponse resource URL: " + renderResponse.createRenderURL().toString());
        String queryString = (String)renderRequest.getAttribute("javax.servlet.forward.query_string");
        _log.debug("## query_string:" + queryString );
        /* this is the place where the stuff goes that uses the renderRequest/Response */

        // TODO: Maybe the order of these should be rearranged and #1 should come last
        // 1. try to find out what topic to use by asking the configurator
        String topicId = config.getTopicId();
        if(topicId == null){
          _log.debug("1 fail");
            // 2. try tp parse topicId from Url
            topicId = config.getTopicIdFromUrl(renderRequest);
            if(topicId == null){
              _log.debug("2 fail");
                // 3. try to parse the articleId from the url and resolve it into a topic id
                topicId = config.getTopicIdFromUrlByArticleId(renderRequest);
                if(topicId == null){
                  _log.debug("3 fail");
                    // 4. try to find the next WCD on this page and return the topic Id of the article that's being displayed
                    topicId = config.findTopicIdFromNextWCD(renderRequest);
                    if(topicId == null){
                      _log.debug("4 fail");
                    throw new OntopiaRuntimeException("Unable to find Topic ID!");
                    }
                }
            }
        }
        

        TopicMapIF topicmap = config.getTopicmap();
        TopicIF topic = (TopicIF) topicmap.getObjectById(topicId);

        // Transform oid's into TopicIF Objects
        Set associationTypeOids = config.getAssocOids();
        Set assocs = new HashSet();

        Iterator associationIterator = associationTypeOids.iterator();
        while(associationIterator.hasNext()){
          String associationTypeId = (String) associationIterator.next();
          TopicIF associationType = (TopicIF) topicmap.getObjectById(associationTypeId);
          assocs.add(associationType);
        }
        
        // pass the objects on to the JSP
        renderRequest.setAttribute("topic", topic);
        renderRequest.setAttribute("assocTypes", assocs);
        renderRequest.setAttribute("blacklist", "true");

		include(viewJSP, renderRequest, renderResponse);
	}

	public void processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException, PortletException {
    actionRequest.getPreferences();
	}

	protected void include(
			String path, RenderRequest renderRequest,
			RenderResponse renderResponse)
		throws IOException, PortletException {

		PortletRequestDispatcher portletRequestDispatcher =
			getPortletContext().getRequestDispatcher(path);

		if (portletRequestDispatcher == null) {
			_log.error(path + " is not a valid include");
		}
		else {
			portletRequestDispatcher.include(renderRequest, renderResponse);
		}
	}


	protected String editJSP;
	protected String helpJSP;
	protected String viewJSP;

	private static Log _log = LogFactoryUtil.getLog(ShowTagsPortlet.class);


    Configurator config;
}