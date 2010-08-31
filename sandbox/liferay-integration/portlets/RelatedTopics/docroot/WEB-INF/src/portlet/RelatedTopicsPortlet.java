/**
 * Some logic for displaying the appropriate topics on the JSPs
 */

package portlet;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * RelatedTopics will display the tags of an article. It will try to
 * look up the topic for that article in the topic map and check what
 * concepts are associated with it. This class dispatches the incoming
 * requests to the right JSPs and reads and writes some config from
 * and to the <code>PortletPreferences</code>.
 *
 * @author Matthias Fischer
 *
 */
public class RelatedTopicsPortlet extends GenericPortlet {

  public void init() throws PortletException {
    editJSP = getInitParameter("edit-jsp");
    helpJSP = getInitParameter("help-jsp");
    viewJSP = getInitParameter("view-jsp");
    config = new Configurator();
  }

  public void doDispatch(RenderRequest renderRequest,
                         RenderResponse renderResponse)
    throws IOException, PortletException {
    String jspPage = renderRequest.getParameter("jspPage");

    if (jspPage != null) {
      include(jspPage, renderRequest, renderResponse);
    } else {
      super.doDispatch(renderRequest, renderResponse);
    }
  }

  public void doEdit(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {

    if (renderRequest.getPreferences() == null) {
      super.doEdit(renderRequest, renderResponse);
    }
    else {
      /*
       * Nothing to do here, it all went into the jsp !
       */

      include(editJSP, renderRequest, renderResponse);
    }
  }

  public void doHelp(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {

    include(helpJSP, renderRequest, renderResponse);
  }

  /**
   * Prepares values to pass to the <code>view.jsp</code> for displaying.
   * Tries to obtain information on the topic (representing an article) it shall display.
   *
   * @param renderRequest The request to render the <code>view.jsp</code> page
   *
   * @throws java.io.IOException Thrown by <code>include(viewJSP, renderRequest, renderResponse);</code>
   * @throws javax.portlet.PortletException Thrown by <code>include(viewJSP, renderRequest, renderResponse);</code>
   */
  public void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {
    // trying logger here instead of stdout just to see how it works out
    _log.debug("ShowTagsPortlet.doView: Entering method.");
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
            // We didn't find a topic, but we're not going to complain here.
            // Instead the portlet can display a warning.
          }
        }
      }
    }

    TopicMapIF topicmap = config.getTopicmap();
    TopicIF topic = null;
    if (topicId != null)
      topic = (TopicIF) topicmap.getObjectById(topicId);

    Set assocs = new HashSet();

    // Transform oid's into TopicIF Objects
    String[] assocOids = renderRequest.getPreferences().getValues("associds", null);
    if(assocOids != null){
      for(String s : assocOids) {
        TopicIF associationType = (TopicIF) topicmap.getObjectById(s);
        assocs.add(associationType);
      }
    }

    // has a filterquery been provided?
    String filterQuery = renderRequest.getPreferences().getValue("filterquery", null);
    if(filterQuery != null){
      renderRequest.setAttribute("filterquery", filterQuery);
    }

    // has the user made a choice whether she wants the associations
    // to be included or excluded?
    String assocMode = renderRequest.getPreferences().getValue("assocmode", null);
    if(assocMode != null) {
      if(assocMode.equalsIgnoreCase("include")) {
        renderRequest.setAttribute("mode", "include");
      } else {
        renderRequest.setAttribute("mode", "exclude");
      }
    } else {
      // if no choice has been made presume "exclude" as default
      renderRequest.setAttribute("mode", "exclude");
    }
    
    // these two are needed in any case, although assocs may be an empty set
    if (topic != null)
      renderRequest.setAttribute("topic", topic);
    renderRequest.setAttribute("assocTypes", assocs);

    // forwarding to the jsp
    include(viewJSP, renderRequest, renderResponse);
  }

  /**
   * Handles <code>ActionRequests</code> as produced by calling
   * <code>PortletURL actionUrl =
   * portletResponse.createActionURL();</code> in
   * <code>edit.jsp</code>.  In this portlet this method is only used
   * for setting preferences through the portlet's
   * <code>edit.jsp</code>.
   *
   * @see ActionRequest
   * @see ActionResponse
   *
   * @param actionRequest The request from the <code>edit.jsp</code> containing parameters which shall be set
   * @param actionResponse Not used but presumably the answer of this portlet
   * 
   * @throws java.io.IOException Might be thrown by <code>actionRequest.getPreferences().store()</code>.
   * @throws javax.portlet.PortletException Might be thrown by any of the <code>actionRequest.getPreferences()</code> methods.
   */
  public void processAction(ActionRequest actionRequest,
                            ActionResponse actionResponse)
    throws IOException, PortletException {

    // read the users input
    String serializedAssocIds = actionRequest.getParameter("associd");
    String topicId = actionRequest.getParameter("topicid");
    String assocMode = actionRequest.getParameter("assocmode");
    String filterQuery = actionRequest.getParameter("filterquery");

    // and set it to the PortletPreferences for this portlet
    PortletPreferences prefs = actionRequest.getPreferences();
    if (serializedAssocIds != null) {
      String[] assocIdArray = serializedAssocIds.split(",");

      for (int count = 0; count < assocIdArray.length; count++)
        assocIdArray[count] = assocIdArray[count].trim();

      prefs.setValues("associds", assocIdArray);
    }

    if (topicId != null)
      prefs.setValue("topicid", topicId);

    if (assocMode != null)
      prefs.setValue("assocmode", assocMode);

    if (filterQuery != null)
      prefs.setValue("filterquery", filterQuery);

    // persist changes
    prefs.store();
  }

  protected void include(String path, RenderRequest renderRequest,RenderResponse renderResponse) throws IOException, PortletException {

    PortletRequestDispatcher portletRequestDispatcher = getPortletContext().getRequestDispatcher(path);

    if (portletRequestDispatcher == null) {
      _log.error(path + " is not a valid include");
    } else {
      portletRequestDispatcher.include(renderRequest, renderResponse);
    }
  }


  protected String editJSP;
  protected String helpJSP;
  protected String viewJSP;

  private static Log _log = LogFactoryUtil.getLog(RelatedTopicsPortlet.class);

  private Configurator config;
}