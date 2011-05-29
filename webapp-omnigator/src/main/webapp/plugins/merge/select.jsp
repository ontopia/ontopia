<%@ page language="java" 
    import="
    java.util.*,
    net.ontopia.topicmaps.entry.*,
    net.ontopia.topicmaps.nav2.core.*,
    net.ontopia.topicmaps.nav2.utils.NavigatorUtils,
    net.ontopia.topicmaps.nav.utils.comparators.TopicMapReferenceComparator" 
%>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tmvalue'   prefix='tm'        %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/framework' prefix='framework' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic'     prefix='logic' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/output'    prefix='output' %>
<framework:response/>

<%-- Topicmap Selection for Merge Process --%>

<logic:context tmparam="tm" settm="topicmap">
<%
// ---------------------------------------------------------------
// get the topic maps
NavigatorApplicationIF navApp = NavigatorUtils.getNavigatorApplication(pageContext);
TopicMapRepositoryIF repository = navApp.getTopicMapRepository();

String current_id = request.getParameter("tm");
TopicMapReferenceIF current = repository.getReferenceByKey(current_id);
%>

  <template:insert template='/views/template_%view%.jsp'>
    <template:put name='title' body='true'>[Omnigator] Merging and Duplicate Suppression</template:put>

    <template:put name='heading' body='true'>
      <h1 class="boxed">Merging and Duplicate Suppression</h1>
    </template:put>

    <template:put name='plugins' body='true'>
      <framework:pluginList separator=" | " group="topicmap"/>
    </template:put>

    <template:put name='navigation' body='true'>
      <%
      Collection refs = repository.getReferences();
      List sortedRefs = new ArrayList(refs);
      Collections.sort(sortedRefs, new TopicMapReferenceComparator());
      %>

     <h3>Merge Another Topic Map</h3>

      <form method="post" action="merge.jsp">
      <input type="hidden" name="tm1" value='<%= current_id %>'>

      <select name="tm2" size="12" tabindex="1">
      <%
      Iterator iter = sortedRefs.iterator();
      while (iter.hasNext()) {
          TopicMapReferenceIF reference = (TopicMapReferenceIF) iter.next();
          String _id = reference.getId();
          if (_id.equals(current_id))
            continue;
          String _title = reference.getTitle(); %>

          <option value="<%= _id %>"><%= _title %></option>

      <% } %>
      </select>

      <p><input type="submit" value="Merge" tabindex="2"></p>

      </form>

      <hr/>
      <h3>Suppress Duplicates</h3>

      <p>By default, the Omnigator does not suppress duplicate names,
      occurrences, and associations when loading a topic map. If you
      want to make sure that your topic map does not contain any such
      duplicates, click on the button below.</p>

      <form method="post" action="suppress.jsp">
      <input type="hidden" name="tm" value='<%= current_id %>'>
      <input type="submit" value="Suppress" tabindex="4">
      </form>

    </template:put>

    <template:put name='content' body='true'>

      <%
        // check for error message from merge.jsp
        String error = request.getParameter("error");
        if (error != null) {
      %><h3>Error</h3> <p><b><%= error %></b></p> <hr> <%
        } %>

      <p>This page allows you to merge another topic map with the current
      one (<b><%= current.getTitle() %></b>) or to suppress any duplicates
      that it might contain. To merge another topic map, select it from the
      box on the left and click the <b>Merge</b> button. To remove duplicates,
      click on the <b>Suppress</b> button.</p>

      <h4>Principles of Merging</h4>

      <p>When merging topic maps, the goal is to ensure that multiple topics
      representing the same subject are merged into a single topic. When two
      topics are merged, the resulting topic should have the union of the
      "characteristics" (names, occurrences, and association roles) and
      other properties (e.g., subject identifiers and source locators) of
      the original two topics.</p>

      <h4>Establishing Identity</h4>

      <p>There are various ways in which an application can determine whether two
      topics represent the same subject. Two of these are defined in the Topic
      Maps standard: Subject-based merging and Name-based merging. (Other
      methods, such as the uniqueness of other properties, e.g. email
      addresses, are currently left up to the application.)</p>

      <p>Subject-based merging is the most robust method: If two topics have
      the same <i>subject identity</i> (specified using &lt;subjectIdentity&gt;
      elements in XTM), they must be merged. The Omnigator always performs
      subject-based merging.</p>

      <h4>Duplicate suppression</h4>

      <p>During merging, duplicate properties that arise <i>as a result of the
      merge</i> will be removed automatically. Duplicates in the source topic
      maps will be treated in accordance with the settings for performing
      duplicate suppression when loading a topic map. The default is for no
      duplicate suppression to be performed. (This allows the Omnigator to be
      used as a debugger and also speeds up loading.) The default can be
      overridden by modifying the 'duplicateSuppression' properties in the
      file 'tm-sources.xml'.</p>

    </template:put>

    <template:put name='outro' body='true'></template:put>

    <%-- ============== Outsourced application wide standards ============== --%>
    <template:put name='application' content='/fragments/application.jsp'/>
    <template:put name='header-tagline' content='/fragments/tagline-header.jsp'/>
    <template:put name='footer-tagline' content='/fragments/tagline-footer.jsp'/>

  </template:insert>
</logic:context>
