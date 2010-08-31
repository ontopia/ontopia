/**
 * Some logic for displaying the appropriate topics on the JSPs
 */

package portlet;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.IOException;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletPreferences;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * Stuff.
 */
public class ArticleListPortlet extends GenericPortlet {

  public void init() throws PortletException {
    editJSP = getInitParameter("edit-jsp");
    helpJSP = getInitParameter("help-jsp");
    viewJSP = getInitParameter("view-jsp");
    //config = new Configurator();
  }

  public void doDispatch(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {
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
    } else {
      include(editJSP, renderRequest, renderResponse);
    }
  }

  public void doHelp(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {

    include(helpJSP, renderRequest, renderResponse);
  }

  public void doView(RenderRequest renderRequest,
                     RenderResponse renderResponse)
    throws IOException, PortletException {
    String queryString = (String) renderRequest.getAttribute("javax.servlet.forward.query_string");
    Map<String, String> params = util.PortletUtils.parseQueryString(queryString);

    TopicMapIF topicmap = tm.OntopiaAdapter.getInstance(true).getTopicMap();
    if (params.containsKey("topic")) {
      TopicIF topic = (TopicIF) topicmap.getObjectById(params.get("topic"));
      renderRequest.setAttribute("topic", topic);
    }

    // transfer preferences into request attributes
    PortletPreferences prefs = renderRequest.getPreferences();
    renderRequest.setAttribute("query", prefs.getValue("query", ""));
    renderRequest.setAttribute("templateid", prefs.getValue("templateid", ""));
    
    // forwarding to the jsp
    include(viewJSP, renderRequest, renderResponse);
  }

  public void processAction(ActionRequest actionRequest,
                            ActionResponse actionResponse)
    throws IOException, PortletException {

    String query = actionRequest.getParameter("query");
    String templateid = actionRequest.getParameter("templateid");
    
    PortletPreferences prefs = actionRequest.getPreferences();
    prefs.setValue("query", query);
    prefs.setValue("templateid", templateid);

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

  private static Log _log = LogFactoryUtil.getLog(ArticleListPortlet.class);

  //private Configurator config;
}