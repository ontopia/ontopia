<%@ page language="java" 
    import="java.util.*,
            java.io.*,
            net.ontopia.topicmaps.entry.*,
            net.ontopia.topicmaps.core.*,
            net.ontopia.topicmaps.nav2.core.*,
            net.ontopia.topicmaps.nav2.utils.NavigatorUtils,
            net.ontopia.topicmaps.nav2.utils.FrameworkUtils,
            net.ontopia.infoset.fulltext.core.*,
            net.ontopia.infoset.fulltext.impl.lucene.*,
            net.ontopia.infoset.fulltext.topicmaps.*,
			org.apache.commons.io.FileUtils,
            net.ontopia.topicmaps.nav.utils.comparators.TopicMapReferenceComparator" %>

<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic'     prefix='logic'     %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/framework' prefix='framework' %>
<framework:response/>

<%-- Index AdminPage --%>

<logic:context>
<%

  NavigatorApplicationIF navApp = NavigatorUtils.getNavigatorApplication(pageContext);
  TopicMapRepositoryIF repository = navApp.getTopicMapRepository();

  UserIF user = FrameworkUtils.getUser(pageContext);
  String model = user.getModel();
  
  // ---------------------------------------------------------------
  // retrieve request parameter
  String report = "";
  String action = (String) request.getParameter("action");
  String id = (String) request.getParameter("id");

  // check action
  if (action != null) {
    if (id != null && !id.equals("")) {
      String fullpath = application.getRealPath("/") + "../omnigator/WEB-INF/indexes/" + id;
      TopicMapReferenceIF _reference = repository.getReferenceByKey(id);
      if (_reference instanceof AbstractOntopolyURLReference) {
        AbstractOntopolyURLReference aref = (AbstractOntopolyURLReference)_reference;
        if (aref.getIndexDirectory() != null) {
          fullpath = aref.getIndexDirectory() + File.separator + id;
        }
        // === delete index
        if (action.equals("delete index")) {
          try {
            aref.deleteFullTextIndex();
            
            File alternative = new File(fullpath);
            if (alternative.exists()) {
              FileUtils.deleteDirectory(alternative);
            }
            
            report = "Deleted index of " + id + ". ";
          } catch (Exception e){
            e.printStackTrace();
            report = "Failed to delete index: " + id + "<br>" +
            "<span class=error>" + e.getMessage() + "</span>";
          }
        }

        if (action.equals("reindex")) {
          try {
            aref.reindexFulltextIndex();
            report = "Reindexed " + id + ".";
          } catch (Exception e){
            report = "Failed to reindex: " + id + "<br>" +
            "<span class=error>" + e.getMessage() + "</span>";
          }
        }

        // === create new index
        if (action.equals("create index")) {
          if (!aref.getMaintainFulltextIndexes()) {
            aref.setMaintainFulltextIndexes(true);
          }
          if (aref.getIndexDirectory() == null) {
            aref.setIndexDirectory(fullpath);
          }
          try {
            aref.reindexFulltextIndex();
            report = "Indexed " + id + ".";
          } catch (Exception e) {
            e.printStackTrace();
            report = "Failed to index: " + id + "<br>" +
            "<span class=error>" + e.getMessage() + "</span>";
          }
        }
      } else {
        report = "Failed to index: " + id + "<br>" +
                "<span class=error>" + _reference.getClass().getName() + "</span>";
      }

      // === check that action is valid
      if (!action.equals("delete index") &&
          !action.equals("create index") &&
          !action.equals("reindex")) {
        report = "Unrecognized action for an ID.";
      }
    } else {
      report = "Id not supplied for action.";
    }
  }

  %>

      
  <template:insert template='/views/template_no_frames.jsp'>
    <template:put name='title' body='true'>[Omnigator] Full-text Indexes</template:put>

    <template:put name='heading' body='true'>
      <h1 class="boxed">Full-text Indexes</h1>
    </template:put>

    <template:put name='plugins' body='true'>
      <framework:pluginList separator=" | " group="manage" exclude="manage"/>
    </template:put>

    <template:put name='manageLinks' body='true'>
      <tr valign="top">
        <td class="plugins" colspan=2>
          <a href="/manage/manage.jsp">Manage</a>
        </td>
      </tr>
    </template:put>

    <%-- =========================== NAVIGATION ====================== --%>
    <template:put name='content' body='true'>

      <table cellpadding="5" cellspacing="0" border="0">
      <tr>
        <td colspan="3"></td>
        <td colspan="2" align="center" valign="middle"></td>
      </tr>
    
      <tr><td colspan="5"></td></tr>
     
      <%
        // Get the indexed topicmaps.
        String fullpathstr = application.getRealPath("/") + "../omnigator/WEB-INF/indexes";
        // get the refs
        Collection refs = repository.getReferences();
        if (!refs.isEmpty()) {
          List sortedRefs = new ArrayList(refs);
          Collections.sort(sortedRefs, new TopicMapReferenceComparator());

          Iterator iter = sortedRefs.iterator();
          while (iter.hasNext()) {
            TopicMapReferenceIF reference = (TopicMapReferenceIF) iter.next();
            String path = fullpathstr;
            if (reference instanceof AbstractOntopolyURLReference) {
              AbstractOntopolyURLReference aref = (AbstractOntopolyURLReference) reference;
              if (aref.getIndexDirectory() != null) {
                path = aref.getIndexDirectory();
              }
            }
            path += File.separator + reference.getId();
            File indexpath = new File(path);
            String _id=reference.getId();
            String _title=reference.getTitle();
    
            if (indexpath.exists()) { %>
          <tr valign="middle">
          <td><img src="../../images/indexed.gif" alt="[Indexed]" title="Indexed" hspace="2"/></td>
          <td class="text"><strong><a href="/omnigator/models/topicmap_<%= model %>.jsp?tm=<%=_id%>"><%= _title %></a></strong></td>
          <td>&nbsp;</td>
          <td>
            <form method="post" action="index_admin.jsp">
              <input type="submit" value=" Delete index " style="font-size:10px">
              <input type="hidden" name="action" value="delete index">
              <input type="hidden" name="id" value="<%=_id%>">
            </form>
          </td>
          <td>&nbsp;</td>
          <td>
            <form method="post" action="index_admin.jsp">
              <input type="submit" value=" Reindex " style="font-size:10px">
              <input type="hidden" name="action" value="reindex">
              <input type="hidden" name="id" value="<%=_id%>">
            </form>
          </td>
          <td width="100%">&nbsp;</td>
          </tr>
        <% } else { %>
          <tr valign="middle">
            <td><img src="../../images/index-missing.gif" alt="[No index]" title="No index" hspace="2" /></td>
            <td class="small"><strong><%= _title %></strong></td>
            <td>&nbsp;</td>
            <td class="small">
              <form method="post" action="index_admin.jsp">
                <input type="submit" value=" Create index " style="font-size:10px">
                <input type="hidden" name="action" value="create index">
                <input type="hidden" name="id" value="<%=_id%>">
              </form>
            </td>
          </tr>
        <% } %>
          <tr><td colspan="5"></td></tr>
       <% } %>
      </table>
      <% } else { %>
          <p>No topic maps found.</p>      
          <p align="center">The directory does not contain any topic
          maps or pointers to any topic maps. Put your topic maps
          (with the file extension .xtm, .iso or .ltm) in this
          directory or update the configuration in the
          &lt;web-application&gt;/WEB-INF/config/tm-sources.xml file.</p>
      <% } %>

    </template:put>

  
    <%-- =========================== CONTENT ====================== --%>
    <template:put name='navigation' body='true'>
      <p>
      This page can be used to administrate the full-text indexes of your
      topic maps. Here you can create new indexes for topic maps, delete 
      already existing indexes, and re-index topic maps. Below are
      explained the icons used to represent the states of the indexes.
      </p>

      <table>
      <tr><td><img src="../../images/indexed.gif">
          <td>Indexed
      <!--tr><td><img src="../../images/index-not-in-sync.gif">
          <td>Index out of date-->
      <tr><td><img src="../../images/index-missing.gif">
          <td>Not indexed
      </table>

      <br>

      <% if (!report.equals("")) { %>
        <table class="shboxed" width="100%" cellpadding="10"><tr><td>
          <h3>Report</h3>
          <%= report %>
        </td></tr></table>
      <% } else { %>
        <hr />
      <% } %>
            
    </template:put>

    <template:put name='outro' body='true'></template:put>
      
    <%-- ============== Outsourced application wide standards ============== --%>
    <template:put name='application' content='/fragments/application.jsp'/>
    <template:put name='header-tagline' content='/fragments/tagline-header.jsp'/>
    <template:put name='footer-tagline' content='/fragments/tagline-footer.jsp'/>
      
  </template:insert> 
</logic:context>
