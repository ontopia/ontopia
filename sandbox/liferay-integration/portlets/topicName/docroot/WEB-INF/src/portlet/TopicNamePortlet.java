/**
 * This is controller for the view/edit/help jsps.
 * It provides access to the portals api and provides the jsps with parameters
 */

package portlet;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.utils.OntopiaRuntimeException;


/**
 * The logic for displaying information on  a concept to the user.
 * view.jsp is the place where most of the magic happens.
 *
 * @author mfi
 */

public class TopicNamePortlet extends GenericPortlet {

  public void init() throws PortletException {
    editJSP = getInitParameter("edit-jsp");
    helpJSP = getInitParameter("help-jsp");
    viewJSP = getInitParameter("view-jsp");
    config = new Configurator();
  }

  public void doDispatch(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {

    String jspPage = renderRequest.getParameter("jspPage");

    if (jspPage != null) {
      include(jspPage, renderRequest, renderResponse);
    }
    else {
      super.doDispatch(renderRequest, renderResponse);
    }
  }

  public void doEdit(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {

    if (renderRequest.getPreferences() == null) {
      super.doEdit(renderRequest, renderResponse);
    }
    else {
      include(editJSP, renderRequest, renderResponse);
    }
  }

  public void doHelp(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {

    include(helpJSP, renderRequest, renderResponse);
}

  public void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {

    // 1. ask the configurator for the topic id
    String topicId = config.getTopicId();
    if(topicId == null){
      // 2. try to parse topicId from Url
      topicId = config.getTopicIdFromUrl(renderRequest);
      if(topicId == null){
        // 3. Not sure if this makes any sense in this portlet, therefore commented out.
        //topicId = config.getTopicIdFromUrlByArticleId(renderRequest);
        if(topicId == null){
          // 4. Also not useful I think? Is there a use case where this would be of use?
          //topicId = config.findTopicIdFromNextWCD(renderRequest);
          if(topicId == null){
            throw new OntopiaRuntimeException("Unable to find Topic ID!");
          }
        }
      }
    }

    renderRequest.setAttribute("topic", topicId);
        
    include(viewJSP, renderRequest, renderResponse);
  }

  public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws IOException, PortletException {
    /* Here goes code for the handling of requests that are being made by using an actionURL. */
  }

	protected void include(String path, RenderRequest renderRequest,RenderResponse renderResponse) throws IOException, PortletException {

    PortletRequestDispatcher portletRequestDispatcher = getPortletContext().getRequestDispatcher(path);

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
  private Configurator config;

  private static Log _log = LogFactoryUtil.getLog(TopicNamePortlet.class);

}