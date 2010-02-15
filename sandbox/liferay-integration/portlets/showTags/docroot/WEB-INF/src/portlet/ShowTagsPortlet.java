/**
 * Copyright (c) 2000-2009 Liferay, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package portlet;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.theme.ThemeDisplay;
import java.io.IOException;

import java.util.HashSet;
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
 * <a href="ShowTagsPortlet.java.html"><b><i>View Source</i></b></a>
 *
 * @author Brian Wing Shun Chan
 *
 */
public class ShowTagsPortlet extends GenericPortlet {

	public void init() throws PortletException {
		editJSP = getInitParameter("edit-jsp");
		helpJSP = getInitParameter("help-jsp");
		viewJSP = getInitParameter("view-jsp");
        config = Configurator.instance;
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
            String friendlyUrl;

            ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
            friendlyUrl = themeDisplay.getPathFriendlyURLPublic();

            String renderUrl = renderResponse.createActionURL().toString();
            
            System.out.println("render url: " + renderUrl);
            renderRequest.setAttribute("renderUrl", renderUrl);
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

        System.out.println("## ShowTagsPortlet.doView(), RenderResponse resource URL: " + renderResponse.createRenderURL().toString());
        String queryString = (String)renderRequest.getAttribute("javax.servlet.forward.query_string");
        System.out.println("## query_string:" + queryString );
        /* this is the place where the stuff goes that uses the renderRequest/Response */
        
        // 1. try to find out what topic to use by asking the configurator
        String topicId = config.getTopicId();
        if(topicId == null){
          System.out.println("1 fail");
            // 2. try tp parse topicId from Url
            topicId = config.getTopicIdFromUrl(renderRequest);
            if(topicId == null){
              System.out.println("2 fail");
                // 3. try to parse the articleId from the url and resolve it into a topic id
                topicId = config.getTopicIdFromUrlByArticleId(renderRequest);
                if(topicId == null){
                  System.out.println("3 fail");
                    // 4. try to find the next WCD on this page and return the topic Id of the article that's being displayed
                    topicId = config.findTopicIdFromNextWCD(renderRequest);
                    if(topicId == null){
                      System.out.println("4 fail");
                    throw new OntopiaRuntimeException("Unable to find Topic ID!");
                    }
                }
            }
        }

        // this can be done as elaborately as above in the future.
        String associationTypeId = config.getAssocOid();

        TopicMapIF topicmap = config.getTopicmap();
        TopicIF topic = (TopicIF) topicmap.getObjectById(topicId);
        TopicIF associationType = (TopicIF) topicmap.getObjectById(associationTypeId);
        System.out.println("topic: " + topic.toString());
        System.out.println("assocType: " + associationType.toString());

        // make up a set for the association(s)
        Set assocs = new HashSet();
        assocs.add(associationType);

        // pass the objects on to the JSP
        renderRequest.setAttribute("topic", topic);
        renderRequest.setAttribute("assocTypes", assocs);

		include(viewJSP, renderRequest, renderResponse);
	}

	public void processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException, PortletException {
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