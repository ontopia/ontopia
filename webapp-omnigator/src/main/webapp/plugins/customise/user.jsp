<%@ page language="java"
    import="
    java.util.*,
    net.ontopia.topicmaps.nav2.core.*,
    net.ontopia.topicmaps.nav2.utils.NavigatorUtils,
    net.ontopia.topicmaps.nav2.utils.FrameworkUtils,
    net.ontopia.topicmaps.nav2.impl.framework.MVSConfig"
%>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic'     prefix='logic'     %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/output'    prefix='output'    %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/value'     prefix='value'     %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/framework' prefix='framework' %>
<framework:response/>

<logic:context tmparam="tm" settm="topicmap">

<%-- Selection of the user MVS preferences --%>

  <template:insert template='/views/template_%view%.jsp'>

    <template:put name='title' body='true'>[Omnigator] Customise</template:put>

    <template:put name='heading' body='true'>
      <h1 class="boxed">Customise</h1>
    </template:put>

    <template:put name='plugins' body='true'>
      <framework:pluginList separator=" | " group="topicmap"/>
    </template:put>

    <template:put name='intro' body='true'><%-- unused --%></template:put>

    <%-- =================================================================== --%>
    <template:put name='navigation' body='true'>
      <p>Customise the Omnigator by selecting the following
      options. These preferences will remain active for the current
      session and will override the application defaults.</p>
      <p>Implementors will most likely want to define their own models
      and views for their users. These preferences demonstrate how
      easy this is to do.</p>
    </template:put>

    <%-- =================================================================== --%>
    <template:put name='content' body='true'>
      <%
        // retrieve configuration
        NavigatorApplicationIF navApp = NavigatorUtils.getNavigatorApplication(pageContext);
        NavigatorConfigurationIF navConf = navApp.getConfiguration();
        MVSConfig mvs = navConf.getMVSConfig();

        // retrieve current settings in this session
        UserIF user = FrameworkUtils.getUser(pageContext);
        String sessionModel = user.getModel();
        String sessionView = user.getView();
        String sessionSkin = user.getSkin();
      %>
      <form method="post" action="userAction.jsp">
        <input type="hidden" name="redirect"
         value="<%= request.getParameter("redirect") %>">
        <input type="hidden" name="currentModel"
         value="<%= sessionModel %>">
      <table>
      <!-- .................... Model selection ........................ -->
      <tr valign="top">
        <td align="right" width="50%"><strong>Model</strong><br />
          <span class="small">Select the model for the data returned. The
          Nontopoly model removes Ontopoly-specific system data from the model.</span>
        </td>
        <td width="50%">
          <%
      Iterator it = mvs.getModels().iterator();
      while (it.hasNext()) {
        ModelIF model = (ModelIF) it.next();
        out.println(formElement("radio", model.getTitle(), "model",
              model.getId(), sessionModel));
      }
          %>
        </td>
      </tr>
      <tr>
      <td colspan="2"><hr/></td>
      </tr>
      <!-- .................... View selection ........................ -->
      <tr valign="top">
        <td align="right"><strong>View</strong><br />
          <span class="small">Select the presentation. This determines how
          the information is presented. Different models can be presented
        with the same view and different views can present the same
        model.</span>
        </td>
        <td>
        <%
      it = mvs.getViews().iterator();
      while (it.hasNext()) {
        ViewIF view = (ViewIF) it.next();
        out.println(formElement("radio", view.getTitle(), "view",
              view.getId(), sessionView));
      }
        %>
        </td>
      </tr>
      <tr>
      <td colspan="2"><hr/></td>
      </tr>
      <!-- .................... Skin selection ........................ -->
      <tr valign="top">
        <td align="right"><strong>Skin</strong><br />
          <span class="small">Select your skin. Style the page with a CSS
          "skin". The default is Ontopia Colours; the other skins
          are included for demonstration purposes only. Purchasers of
          the Ontopia Knowledge Suite can create their own skins as
          required.</span>
        </td>
        <td>
        <%
      it = mvs.getSkins().iterator();
      while (it.hasNext()) {
        SkinIF skin = (SkinIF) it.next();
        out.println(formElement("radio", skin.getTitle(), "skin",
              skin.getId(), sessionSkin));
      }
        %>
        </td>
      </tr>
      <tr valign="top">
        <td>&nbsp;</td>
        <td>
          <input type="submit" name="submit" value="Customise">
        </td>
      </tr>
      </table>
      </form>
    </template:put>

    <template:put name='outro' body='true'></template:put>

    <%-- ============== Outsourced application wide standards ============== --%>
    <template:put name='application' content='/fragments/application.jsp'/>
    <template:put name='header-tagline' content='/fragments/tagline-header.jsp'/>
    <template:put name='footer-tagline' content='/fragments/tagline-footer.jsp'/>

  </template:insert>

<%!
/**
 * generate form element string
 *
 * @param itemType - supported input types are "option" and "radio"
 * @param itemTitle - title of input element
 * @param itemName - name of input element
 * @param itemValue - value of input element
 * @param itemValueCheckSource - string source to look for a match
 */
protected String formElement(String itemType, String itemTitle, String itemName,
           String itemValue, String itemValueCheckSource) {
  boolean active =
    itemValueCheckSource!=null && itemValueCheckSource.equals(itemValue);

  StringBuffer sb = new StringBuffer(128);
  if (itemType.equals("option")) {
    sb.append("<option value='" + itemValue + "'");
    if (active)
      sb.append(" selected='selected'");
    sb.append("/>");
    sb.append(itemTitle);
    sb.append("</option>");
  } else if (itemType.equals("radio")) {
    sb.append("<table><tr><td><input type='radio' name='" + itemName);
    sb.append("' value='" + itemValue + "'");
    if (active)
      sb.append(" checked='checked'");
    sb.append("/>");
    sb.append(itemTitle);
    sb.append("</td></tr></table>");
  }
  return sb.toString();
}
%>
</logic:context>