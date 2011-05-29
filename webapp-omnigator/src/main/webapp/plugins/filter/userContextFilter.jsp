<%@ page language="java"
  import="java.util.Collection,
          net.ontopia.topicmaps.core.*,
          net.ontopia.topicmaps.core.index.*,
          net.ontopia.topicmaps.nav.context.*,
          net.ontopia.topicmaps.nav2.core.*,
          net.ontopia.topicmaps.nav2.utils.FrameworkUtils,
          net.ontopia.topicmaps.nav2.utils.ContextUtils"
%>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic'     prefix='logic'     %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/output'    prefix='output'    %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/value'     prefix='value'     %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tmvalue'   prefix='tm'        %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/framework' prefix='framework' %>
<framework:response/>

<%-- User context filter selection page --%>

<logic:context tmparam="tm" settm="topicmap">

  <template:insert template='/views/template_%view%.jsp'>

    <template:put name='title' body='true'>[Omnigator] Configure User Context Filter</template:put>

    <template:put name='plugins' body='true'>
      <framework:pluginList separator=" | " group="topicmap"/>
    </template:put>

    <template:put name='heading' body='true'>
      <h1 class="boxed">Set Context Filter</h1>
    </template:put>

    <%-- ====================== Description ============================ --%>
    <template:put name='navigation' body='true'>

      <p>Customise the Omnigator by selecting the following filter
      options. These preferences are only valid for the current session.</p>

      <p>By specifying the themes you are interested in, you can
      customise your view of the topic map. Only the most relevant
      themes are available for selection on this page. For details of
      what constitutes a "relevant theme" in this context, see the <a
      href="../../docs/userguide.html#set-context-page"
      target="_blank">Omnigator User Guide</a>. (You can see a
      complete list of themes by selecting "Themes" from the drop-down
      list on the topic map's entry page.)</p>

      <p>Themes are grouped, first according to whether they are used to scope
      names, associations, or occurrences, and then by axis. Axes are
      determined automatically based on the classes to which themes belong.
      (Themes that don't belong to any class are grouped into the axis
      <b>[unspecified]</b>.)</p>

      <p>Context themes are currently used for two purposes: selecting (i.e.,
      choosing the most relevant name), and filtering (i.e., removing unwanted
      associations and occurrences).</p>

      <p>Since name context is used for selection, it usually only makes sense
      to select one name theme (at most) from each axis. In the case of
      association and occurrence context, the more themes you specify, the
      more associations and occurrences will be shown.</p>

    </template:put>

    <%-- ================== Form with scope selection ==================== --%>
    <template:put name='content' body='true'>
      <%
        // Get User Filter Context out of session
        UserIF user = FrameworkUtils.getUser(pageContext);
        UserFilterContextStore userFilterContext = user.getFilterContext();
        if (userFilterContext == null)
          userFilterContext = new UserFilterContextStore();

        // retrieve TopicMap object out of context
        TopicMapIF topicmap = (TopicMapIF)ContextUtils.getSingleValue("topicmap", pageContext);

        // initialize scope index and theme categorizer
        ScopeIndexIF scopeIndex = (ScopeIndexIF) topicmap
          .getIndex("net.ontopia.topicmaps.core.index.ScopeIndexIF");
        ThemeCategorizer categorizer =
          new ThemeCategorizer(topicmap,
                               userFilterContext.getScopeTopicNames(topicmap));

        // setting up filter for suppression of themes which are not useful
        // presenting to the user and from which the user context is selected
        BasenameUserThemeFilter basenamefilter = new BasenameUserThemeFilter( topicmap );
      %>

      <form method="post" action="userfilterAction.jsp">
        <input type="hidden" name="tm" value="<%= request.getParameter("tm") %>" />
        <input type="hidden" name="redirect" value="<%= request.getParameter("redirect") %>" />

        <table>

              <tr valign="top">
                <td colspan="2" align="right">
                  <input type="submit" name="action" value="Activate">
                  <input type="submit" name="action" value="Reset">
                  <input type="submit" name="action" value="Cancel">
                </td>
              </tr>

              <tr><td colspan="2"><hr /></td></tr>
              <!-- .................... Base Name Context selection ........................ -->
              <tr valign="top">
                <td align="left" width="20%"><strong>Base Name Context</strong><br />
                  <span class="small">Select the base name themes you are interested in.</span>
                </td>
                <td width="80%">
                  <table>
              <%= generateSelectThemeList("basename",
                                  userFilterContext.getScopeTopicNames(topicmap),
                                        basenamefilter.filterThemes(scopeIndex.getTopicNameThemes()),
                                              categorizer) %>
                  </table>
                </td>
              </tr>
              <tr><td colspan="2"><hr /></td></tr>
              <!-- .................... Variant Name Context selection ..................... -->
              <tr valign="top">
                <td align="left" width="20%"><strong>Variant Name Context</strong><br />
                  <span class="small">Select the variant name themes you are interested in.</span>
                </td>
                <td width="80%">
                  <table>
              <%= generateSelectThemeList("variantname",
                                  userFilterContext.getScopeVariantNames(topicmap),
                                        scopeIndex.getVariantThemes(),
                                              categorizer) %>
                  </table>
                </td>
              </tr>
              <tr><td colspan="2"><hr /></td></tr>
              <!-- .................... Association Context selection ...................... -->
              <tr valign="top">
                <td align="left" width="20%"><strong>Association Context</strong><br />
                  <span class="small">Select the association themes you are interested in.</span>
                </td>
                <td width="80%">
                 <table>
             <%= generateSelectThemeList("association",
                                 userFilterContext.getScopeAssociations(topicmap),
                                 scopeIndex.getAssociationThemes(),
                                 categorizer) %>
                 </table>
                </td>
              </tr>
              <tr><td colspan="2"><hr /></td></tr>
              <!-- .................... Occurrence Context selection ....................... -->
              <tr valign="top">
                <td align="left" width="20%"><strong>Occurrence Context</strong><br />
                  <span class="small">Select the occurrence themes you are interested in.</span>
                </td>
                <td width="80%">
                 <table>
                   <%= generateSelectThemeList("occurrence",
                                 userFilterContext.getScopeOccurrences(topicmap),
                                 scopeIndex.getOccurrenceThemes(),
                                     categorizer) %>
                 </table>
                </td>
              </tr>
              <tr><td colspan="2"><hr /></td></tr>
              <tr valign="top">
                <td colspan="2" align="right">
                  <input type="submit" name="action" value="Activate">
                  <input type="submit" name="action" value="Reset">
                  <input type="submit" name="action" value="Cancel">
                </td>
              </tr>

            </table>
      </form>


    </template:put>

    <%-- ============== Outsourced application wide standards ============== --%>
    <template:put name='application' content='/fragments/application.jsp'/>
    <template:put name='header-tagline' content='/fragments/tagline-header.jsp'/>
    <template:put name='footer-tagline' content='/fragments/tagline-footer.jsp'/>

  </template:insert>
</logic:context>


<%!

  /**
   * generate input-form elements for selection of the available
   * themes, the themes are displayed in a categorized and sorted way
   * for easier navigation.
   *
   * @param inputName - String which identifies the name of the input form element
   * @param selectedThemes - Collection of already selected themes
   * @param listThemes - Collection of all themes that should be presented
   * @param categorizer - categorizes the listThemes in accordance to their type
   */
  protected String generateSelectThemeList(String inputName,
                                 Collection selectedThemes,
                                 Collection listThemes,
                                 ThemeCategorizer categorizer) {
    if (listThemes.size() < 1)
      return "<tr></td><i>No themes available.</i></td></tr>";
    else
      return categorizer
      .generateThemeList( categorizer.getThemeClasses(listThemes), selectedThemes,
                    "<tr><td colspan='2'><nobr>axis: <b>%className%</b></nobr></td></tr>\n",
                    "<tr><td>&nbsp;</td><td><input type='checkbox' name='" + inputName +
                    "' value='%themeId%' %selected% >%themeName%</input></td></tr>\n",
                          "checked='checked'" );

  }

%>
