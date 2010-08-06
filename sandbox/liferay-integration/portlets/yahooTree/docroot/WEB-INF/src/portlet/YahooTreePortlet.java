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
import javax.portlet.PortletPreferences;
 
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * The logic for displaying information on  a concept to the user.
 * view.jsp is the place where most of the magic happens.
 *
 * @author mfi
 */
public class YahooTreePortlet extends GenericPortlet {
  protected String editJSP;
  protected String helpJSP;
  protected String viewJSP;
  private Configurator config;

  private static Log _log = LogFactoryUtil.getLog(YahooTreePortlet.class);

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

    if (jspPage != null)
      include(jspPage, renderRequest, renderResponse);
    else
      super.doDispatch(renderRequest, renderResponse);
  }

  public void doEdit(RenderRequest renderRequest,
                     RenderResponse renderResponse)
    throws IOException, PortletException {

    if (renderRequest.getPreferences() == null)
      super.doEdit(renderRequest, renderResponse);
    else
      include(editJSP, renderRequest, renderResponse);
  }

  public void doHelp(RenderRequest renderRequest,
                     RenderResponse renderResponse)
    throws IOException, PortletException {

    include(helpJSP, renderRequest, renderResponse);
  }

  public void doView(RenderRequest request,
                     RenderResponse renderResponse)
    throws IOException, PortletException {

    // transfer preferences into request attributes
    PortletPreferences prefs = request.getPreferences();
    request.setAttribute("topquery", prefs.getValue("topquery", ""));
    request.setAttribute("childquery", prefs.getValue("childquery", ""));
    request.setAttribute("columns", prefs.getValue("columns", ""));
  
    include(viewJSP, request, renderResponse);
  }

  public void processAction(ActionRequest actionRequest,
                            ActionResponse actionResponse)
    throws IOException, PortletException {

    String topquery = actionRequest.getParameter("topquery");
    String childquery = actionRequest.getParameter("childquery");
    String columns = actionRequest.getParameter("columns");
    
    PortletPreferences prefs = actionRequest.getPreferences();
    prefs.setValue("topquery", topquery);
    prefs.setValue("childquery", childquery);
    prefs.setValue("columns", columns);

    prefs.store();
  }
  
  protected void include(String path, RenderRequest renderRequest,
                         RenderResponse renderResponse)
    throws IOException, PortletException {

    PortletRequestDispatcher portletRequestDispatcher = getPortletContext().getRequestDispatcher(path);

    if (portletRequestDispatcher == null)
      _log.error(path + " is not a valid include");
    else
      portletRequestDispatcher.include(renderRequest, renderResponse);
  }
}