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
      
      /*
       * Nothing to do here, it is all gone into the jsp !
       */

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

    // TODO: Maybe the order of these should be rearranged and #1 should come last?
    // 1. try to find out what topic to use by checking the PortletPreferences
    String topicId = renderRequest.getPreferences().getValue("topicid", null);
    if(topicId == null || topicId.equalsIgnoreCase("")){
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
                  // too bad, but w/o a topic id this portlet does not know what to display.
                  // TODO: Maybe some default content should be created.
                throw new OntopiaRuntimeException("Unable to find Topic ID!");
                }
            }
        }
    }


    TopicMapIF topicmap = config.getTopicmap();

    TopicIF topic = (TopicIF) topicmap.getObjectById(topicId);

    Set assocs = new HashSet();

    // Transform oid's into TopicIF Objects
    String[] assocOids = renderRequest.getPreferences().getValues("associds", null);
    System.out.println("ShowTagsPortlet.doView: Contents of assocOids = " + assocOids );

    if(assocOids != null){
      for(String s : assocOids){
        System.out.println("ShowTagsPortlet.doView: Looking up Assoc Oid " + s + " in the topicmap.");
        TopicIF associationType = (TopicIF) topicmap.getObjectById(s);
        assocs.add(associationType);
      }
    }

    // pass the objects on to the JSP
    renderRequest.setAttribute("topic", topic);
    renderRequest.setAttribute("assocTypes", assocs);
    renderRequest.setAttribute("blacklist", "true");
    //TODO: Set Filterquery for relatedTopics portlet

  include(viewJSP, renderRequest, renderResponse);
	}

	public void processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException, PortletException {

    System.out.println("ShowTagsPortlet.processAction: entering method.");

    // read the users input
    String serializedAssocIds = actionRequest.getParameter("associd");
    String topicId = actionRequest.getParameter("topicid");
    String assocMode = actionRequest.getParameter("assocmode");
    String filterQuery = actionRequest.getParameter("filterquery");

    // and set it to the PortletPreferences for this portlet
    if(serializedAssocIds != null){
      System.out.println("ShowTagsPortlet.processAction: associd received.");
      String[] assocIdArray = serializedAssocIds.split(",");

      for(int count = 0; count < assocIdArray.length; count++){
        assocIdArray[count] = assocIdArray[count].trim();
      }

      actionRequest.getPreferences().setValues("associds", assocIdArray);
    }

    if(topicId != null){
      System.out.println("ShowTagsPortlet.processAction: topicid received.");
      actionRequest.getPreferences().setValue("topicid", topicId);
    }

    if(assocMode != null){
      System.out.println("ShowTagsPortlet.processAction: assocmode received.");
      actionRequest.getPreferences().setValue("assocmode", assocMode);
    }

    if(filterQuery != null){
      System.out.println("ShowTagsPortlet.processAction: filterquery received.");
      actionRequest.getPreferences().setValue("filterquery", filterQuery);
    }

    // persist changes
    System.out.println("ShowTagsPortlet.processAction: persisting changes to preferences.");
    actionRequest.getPreferences().store();
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