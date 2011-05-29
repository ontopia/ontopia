<%@ page language="java" 
    import="
    java.io.*,
    java.util.*,
    java.text.NumberFormat,
    java.text.DecimalFormat,
    net.ontopia.topicmaps.entry.*,
    net.ontopia.topicmaps.core.*,
    net.ontopia.topicmaps.nav2.core.*,
    net.ontopia.topicmaps.nav2.utils.*,
    net.ontopia.topicmaps.nav.utils.comparators.TopicMapReferenceComparator,
    net.ontopia.infoset.fulltext.core.*,
    net.ontopia.infoset.fulltext.impl.lucene.*,
    net.ontopia.infoset.fulltext.topicmaps.*" 
%>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic'     prefix='logic'     %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/framework' prefix='framework' %>

<%@page import="org.apache.commons.fileupload.servlet.ServletFileUpload"%>
<%@page import="org.apache.commons.fileupload.disk.DiskFileItemFactory"%>
<%@page import="org.apache.commons.fileupload.FileItem"%>
<framework:response/>

<script type="text/javascript">
  function hide(rowIndex) {
    id = 'row' + rowIndex;
    document.getElementById(id).style.display="none";
    hideId = 'hideRow' + rowIndex
    document.getElementById(hideId).style.display="none";
    showId = 'showRow' + rowIndex
    document.getElementById(showId).style.display="";
  }

  function show(rowIndex) {
    id = 'row' + rowIndex;
    document.getElementById(id).style.display="";
    hideId = 'hideRow' + rowIndex
    document.getElementById(hideId).style.display="";
    showId = 'showRow' + rowIndex
    document.getElementById(showId).style.display="none";
  }
</script>

<%-- Manage Page --%>

<logic:context>
<%
// ---------------------------------------------------------------
// retrieve configuration
NavigatorApplicationIF navApp = NavigatorUtils.getNavigatorApplication(pageContext);
NavigatorConfigurationIF navConf = navApp.getConfiguration();
TopicMapRepositoryIF repository = navApp.getTopicMapRepository();

UserIF user = FrameworkUtils.getUser(pageContext);
String model = "complete";
if (user != null)
  model = user.getModel();

// ---------------------------------------------------------------
// deal with actions
String report = "";
String action = (String) request.getParameter("action");
String id = (String) request.getParameter("id");
boolean update = false;

boolean readonly = true;

// IMPORT TOPICMAP
if (ServletFileUpload.isMultipartContent(request)) {
  ServletFileUpload servletFileUpload = 
    new ServletFileUpload(new DiskFileItemFactory());
  List fileItemsList = servletFileUpload.parseRequest(request);

  String optionalFileName = "";
  FileItem fileItem = null;

  Iterator it = fileItemsList.iterator();
  while (it.hasNext()){
    FileItem fileItemTemp = (FileItem)it.next();
    if (fileItemTemp.isFormField()){
      if (fileItemTemp.getFieldName().equals("tmfilename"))
        optionalFileName = fileItemTemp.getString();
    }
    else
      fileItem = fileItemTemp;
  }

  if (fileItem != null){
    String fileName = fileItem.getName();

    /* Save the uploaded file if its size is greater than 0. */
    if (fileItem.getSize() > 0){
      if (optionalFileName.trim().equals(""))
        fileName = new File(fileName).getName();
      else
        fileName = optionalFileName;

      String dirName = getServletContext().getRealPath("/") + "../omnigator/WEB-INF/topicmaps/";

      File saveTo = new File(dirName + fileName);
      try {
        fileItem.write(saveTo);
        report = "Successfully uploaded topic map: <br/>" +
        	"File name: " + fileName + "<br/>" + 
        	"File size: " + fileItem.getSize();
        repository.refresh();
      } catch (Exception e) {
        report = "Could not upload topic map: <br/>" + e.toString();
      }
    }
  }
}

// check action
if (action != null) {
  // Registry-based actions
  if (id != null && !id.equals("")) {
    TopicMapReferenceIF ref = repository.getReferenceByKey(id);
    if (action.equals("load")) {
      try {
        synchronized (ref) {
          if (ref.isOpen()) ref.close();
            ref.open();
        }
        report = "Loaded Topic Map: " + id + ".<br>";
      }
      catch (Throwable e) {
        report = "Could not load topic map: " + id + "<br>" +
                 "<span class=error>" + e.getMessage() + "</span>";
      }
      update = true;
    } else if (action.equals("drop")) {
      synchronized (ref) {
        if (ref.isOpen()) ref.close();
      }
      report = "Dropped Topic Map: " + id + ".<br>";
      update = true;
    } else if (action.equals("reload")) {
      try {
        synchronized (ref) {
          if (ref.isOpen()) ref.close();
            ref.open();
        }

        // === Reindex when fulltext index exists
        String fullpath = application.getRealPath("/") + "../omnigator/WEB-INF/indexes/" + id;
        File file = new File(fullpath);
        if (file.exists()) {
          // === delete index
          try {
            // Delete all the files in the directory.
            File[] files = file.listFiles();
            if (files != null) {
              for (int i = 0; i < files.length; i++) {
                files[i].delete();
              }
            }
            // Delete the index directory
            if (!file.delete()) 
              report += "Could not delete index.<br>";
            else 
              report += "Deleted the index for: " + id + ".<br>";
          } catch (Exception e){
            report += "Failed to delete index: " + id + ".<br>" +
              "<span class=error>" + e.getMessage() + "</span>";
          }
          
          // === create new index

          // Create a Lucene indexer
          IndexerIF lucene_indexer = new LuceneIndexer(fullpath, true);
          try {
            // Creates an instance of the default topic map indexer.
            DefaultTopicMapIndexer imanager = new DefaultTopicMapIndexer(lucene_indexer, false, "");
            
            // Indexes the topic map
            TopicMapIF topicmap = navApp.getTopicMapById(id);
            try {
              imanager.index(topicmap);
              imanager.close();
            } finally {
              navApp.returnTopicMap(topicmap);
            }
            report += "Indexed " + id + ".<br>";
          } catch (java.lang.Exception e) {
            e.printStackTrace();
            report += "Failed to index: " + id + ".<br>" +
              "<span class=error>" + e.getMessage() + "</span>";
          } finally {
            lucene_indexer.close();
          }
        }

        // === Redirect to another page
        String redirect_url = request.getParameter("redirect");
        if (redirect_url != null) {
          // Verify that referenced object still exists before redirecting
          String objectid = (String) request.getParameter("objectid");
          TopicMapStoreIF store = null;
          try {
            store = ref.createStore(true);
            TMObjectIF tmobject = NavigatorUtils.stringID2Topic(store.getTopicMap(), objectid);
            if (tmobject != null) {
              // Object exists, so we can redirect
              response.sendRedirect(redirect_url);
            } else {
              // Oops, object no longer exists. Redirect to topic map page instead.
              response.sendRedirect("/omnigator/models/topicmap_" + model + ".jsp?tm=" + id);
            }
          } finally {
            if (store != null) store.close();
          }
        }
        report += "Topic Map " + id + " has been reloaded.<br>";
      }
      catch (Throwable e) {
        report += "Could not reload topic map: " + id + "<br>" +
                 "<span class=error>" + e.getMessage() + "</span>";
      }
      update = true;
    } else {
      report = "Unrecognized action for an ID.<br>";
    }
  } else {
    report = "Id not supplied for action.<br>";
  }

  if (update) {
    report += "The store registry has been updated in APPLICATION_SCOPE.<br>";
  }

  // REFRESH SOURCES
  if (action.equals("refresh_sources")) {
    repository.refresh();
    report = "The topic map sources were refreshed.<br>";
  }
}
// ---------------------------------------------------------------
%>

      
  <template:insert template='/views/template_%view%.jsp'>
    <template:put name='title' body='true'>[Omnigator] Administration Console</template:put>

    <template:put name='heading' body='true'>
      <h1 class="boxed">Administration Console</h1>
    </template:put>

    <%-- No plugins in this version of the admin console --%>
    <%-- template:put name='plugins' body='true'>
      <framework:pluginList separator=" | " group="manage" exclude="manage"/>
    </template:put --%>

    <%-- =========================== NAVIGATION ====================== --%>
    <template:put name='navigation' body='true'>
      <%
        // get the refs
        Collection refs = repository.getReferences();
        if (!refs.isEmpty()) {
          List sortedRefs = new ArrayList(refs);
          Collections.sort(sortedRefs, new TopicMapReferenceComparator());
      %>
      <table class=shboxed cellpadding="5" cellspacing="0" border="0">
      <tr>
        <td colspan="3"><h3>Registry Items</h3></td>
        <td colspan="2" align="center" valign="middle">
          <form method="post" action="manage.jsp">
            <input type="submit" value=" Refresh Sources ">
            <input type="hidden" name="action" value="refresh_sources">
          </form>
        </td>
        <td id="showRowImport">
          <form method="post" action="manage.jsp">
            <input type="button" value=">>" onClick="show('Import')">
          </form>
        </td>
        <td id="hideRowImport">
          <form method="post" action="manage.jsp">
            <input type="button" value="<<" onClick="hide('Import')">
          </form>
        </td>
      </tr>
	  <tr id="rowImport">
        <td colspan="7">
          <form method="post" action="manage.jsp" enctype="multipart/form-data">
			<input type="file" name="tmfilename" size="25" accept="text/*">
            <input type="submit" value=" Import ">
            <input type="hidden" name="action" value="import_topicmap">
		  </form>
        </td>
	  </tr>      
      <script type="text/javascript">
        hide('Import');
      </script>
    
      <%
      Iterator iter = sortedRefs.iterator();
      int rowIndex = 0;
      while (iter.hasNext()) {
        TopicMapReferenceIF reference = (TopicMapReferenceIF)iter.next();
        String _id=repository.getReferenceKey(reference);
        String _title=reference.getTitle();
    
        if (reference.isOpen()) { %>
          <tr valign="middle">
          <td><img src="./images/check_on.gif" alt="Loaded" hspace="2"/></td>
          <td class="text"><strong><a href="/omnigator/models/topicmap_<%= model %>.jsp?tm=<%=_id%>"><%= _title %></a></strong></td>
          <td>&nbsp;</td>
          <td>
            <form method="post" action="manage.jsp">
              <input type="submit" value="Drop">
              <input type="hidden" name="action" value="drop">
              <input type="hidden" name="id" value="<%=_id%>">
            </form>
          </td>
          <td>
            <form method="post" action="manage.jsp">
              <input type="submit" value="Reload">
              <input type="hidden" name="action" value="reload">
              <input type="hidden" name="id" value="<%=_id%>"> 
            </form>
          </td>
          <td id="hideRow<%= rowIndex %>">
            <form method="post" action="manage.jsp">
              <input type="button" value="<<" onClick="hide('<%= rowIndex %>')">
            </form>
          </td>
          <td id="showRow<%= rowIndex %>">
            <form method="post" action="manage.jsp">
              <input type="button" value=">>" onClick="show('<%= rowIndex %>')">
            </form>
          </td>
          <td width="100%">&nbsp;</td>
          </tr>
          <tr id="row<%= rowIndex %>">
            <td></td>

            <td>
              <form method="post" action="manage.jsp">
                <% if (reference.getClass().getName().equals("net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapReference")) { %>
                  <a href="oksreport.jsp?tm=<%=_id %>">
                    <input type="button" value="Cache">
                  </a>
                  <a href="tmstats.jsp?tm=<%=_id %>">
                    <input type="button" value="Stats">
                  </a>
                <% } %>
                <a href="plugins/tolog/form.jsp?tm=<%=_id %>&id=null">
                  <input type="button" value="Query">
                </a>
              </form>
            </td>
            <td width="100%">&nbsp;</td>
          </tr>
          <script type="text/javascript">
            hide('<%= rowIndex %>');
          </script>
        <% } else { %>
          <tr valign="middle">
          <td><img src="./images/check_off.gif" alt="Not loaded" hspace="2" /></td>
          <td class="small"><strong><%= _title %></strong></td>
          <td>&nbsp;</td>
          <td class="small">
            <form method="post" action="manage.jsp">
              <input type="submit" value="Load">
              <input type="hidden" name="action" value="load">
              <input type="hidden" name="id" value="<%=_id%>">
            </form>
          </td>
          </tr>
        <% } %>
          <tr><td colspan="5"></td></tr>
       <% 
         rowIndex++;
       } %>
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
    <template:put name='content' body='true'>
      <% if (!report.equals("")) { %>
        <table border="1" width="100%" cellpadding="10"><tr><td>
          <h3>Report</h3>
          <%= report %>
        </td></tr></table>
      <% } %>
      
      &nbsp;
      <h3>Full-text Indexes</h3>
      <table>
      <tr valign="top">
      <td>
      <p>
        Ontopia maintains a full text index on disk for each in-memory topic map in
        the repository. Using this page, you can manage these indexes
        yourself.
      </p>
      </td>
      <td>
      <form method="post" action="plugins/ftadmin/index_admin.jsp">
      <input type="submit" value="Indexes">
      </form>
      </td>
      </tr>
      </table>
<%
try {
  Class.forName("net.ontopia.persistence.jdbcspy.SpyDriver");
%>        
      &nbsp;
      <h3>jdbcspy</h3>
      <table>
      <tr valign="top">
      <td>
      <p>
        jdbcspy is a SQL profiler which can tell you how much time is spent on
        each of the SQL queries run by Ontopia. This is useful for analyzing
        performance problems with the RDBMS backend.
      </p>
      </td>
      <td>
      <form method="post" action="jdbcspy.jsp">
      <input type="submit" value="Report">
      </form>
      </td>
      </tr>
      </table>
<%
} catch (Exception e) {
}
%>      
       
<%!
  private final static NumberFormat formatter =
    new DecimalFormat("###,###,###,###");
%>
     <p/>&nbsp;<p/>
     <hr />
     <p class="comment">Memory used by the Java Virtual Machine in
        which this web application is running:</p>
     <table cellspacing="5">
       <%
         long free = Runtime.getRuntime().freeMemory();
         long tot = Runtime.getRuntime().totalMemory();
       %>
       <tr class="comment">
         <td rowspan="2">&nbsp; &nbsp;</td>
         <td>Free Memory:</td>
         <td align="right"><%= formatter.format(free) %> bytes</td>
       </tr>
       <tr class="comment">
         <td>Allocated Memory:</td>
         <td align="right"><%= formatter.format(tot) %> bytes</td>
       </tr>
     </table>
  
    </template:put>

    <template:put name='outro' body='true'></template:put>
      
    <%-- ============== Outsourced application wide standards ============== --%>
    <template:put name='application' content='/fragments/application.jsp'/>
    <template:put name='header-tagline' content='/fragments/tagline-header.jsp'/>
    <template:put name='footer-tagline' content='/fragments/tagline-footer.jsp'/>
      
  </template:insert> 
</logic:context>
