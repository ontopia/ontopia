<%@ page language="java"
    import="
    java.io.*,
    java.util.*,
    net.ontopia.utils.*,
    net.ontopia.topicmaps.core.*,
    net.ontopia.topicmaps.utils.*,
    net.ontopia.topicmaps.entry.*,
    net.ontopia.topicmaps.nav2.core.*,
    net.ontopia.topicmaps.nav2.utils.ContextUtils,
    net.ontopia.topicmaps.nav2.utils.NavigatorUtils,
    net.ontopia.infoset.fulltext.core.*,
    net.ontopia.topicmaps.nav2.plugins.PluginIF,
    net.ontopia.infoset.fulltext.impl.lucene.*"
%>

<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/framework' prefix='framework' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/output'    prefix='output'    %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic'     prefix='logic'     %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/value'     prefix='value'     %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tmvalue'   prefix='tm'        %>
<framework:response/>

<logic:context tmparam="tm" settm="topicmap">

  <template:insert template='/views/template_%view%.jsp'>
    <template:put name='title' body='true'>[Omnigator] Fulltext search</template:put>

    <template:put name='heading' body='true'>
      <h1 class="boxed">Fulltext search</h1>
    </template:put>

    <template:put name='plugins' body='true'>
      <framework:pluginList separator=" | " group="topicmap"/>
    </template:put>

    <%
     String query = "";
     if (request.getParameter("query") != null) {
       query = request.getParameter("query");
       query = net.ontopia.utils.StringUtils.transcodeUTF8(query);
     }
     String tmid = request.getParameter("tm");
     String fullpath = application.getRealPath("/") + "WEB-INF/indexes/" + tmid;

     TopicMapRepositoryIF rep = NavigatorUtils.getTopicMapRepository(pageContext);
     TopicMapReferenceIF ref = rep.getReferenceByKey(tmid);
     if (ref instanceof AbstractOntopolyURLReference) {
       AbstractOntopolyURLReference oref = (AbstractOntopolyURLReference) ref;
       fullpath = oref.getIndexDirectory() + File.separator + tmid;
     }

     TopicMapIF topicmap = (TopicMapIF)ContextUtils.getSingleValue("topicmap", pageContext);
     StringifierIF topic_stringifier = TopicStringifiers.getTopicNameStringifier(Collections.EMPTY_SET);
    %>

  <template:put name="navigation" body="true">

    <form method=post action="search.jsp">
      Enter your query here:<br>
      <input type=hidden name=tm value='<%= tmid %>'>
      <input type="text" value="<%= (query == null ? "" : StringUtils.replace(query, "\"", "&quot;")) %>" name="query" size="48"></textarea><br>
      <input type=submit value="search">
    </form>

<p>This plug-in allows you to search your topic map using the full-text
search engine Lucene. Indexing is performed on names, the contents of
internal occurrences, and the locators of external occurrences.</p>

<p>By default, searching is perfomed on names and the contents of
internal occurrences, but this can be restricted to the one or the
other, or extended to locators, using the syntax described below.
Searching is case-insensitive, but keywords (AND, OR, NOT) must be given
in upper-case.</p>

<h4>Examples</h4>

<dl>
<dt><tt>tosca</tt></dt>
<dd>Find objects that contain the string 'tosca'.</dd>
<dt><tt>floria tosca</tt></dt>
<dd>Find objects that contain the string 'floria' <i>or</i> the string
'tosca'. (Equivalent: <tt>floria OR tosca</tt>)</dd>
<dt><tt>floria AND tosca</tt></dt>
<dd>Find objects that contain the string 'floria' <i>and</i> the string
'tosca'. (Equivalent: <tt>tosca + floria</tt>)</dd>
<dt><tt>"floria tosca"</tt></dt>
<dd>Find objects that contain the string 'floria tosca'.</dd>
<dt><tt>tosca AND NOT floria</tt></dt>
<dd>Find objects that contain the string 'tosca' and do <i>not</i>
contain the string 'floria'. (Equivalent: <tt>tosca -floria</tt>)</dd>
<dt><tt>tosca AND NOT (rome OR floria)</tt></dt>
<dd>Find objects that contain the string 'tosca' and do <i>not</i>
contain <i>either</i> the string 'floria' <i>or</i> the string 'rome'.
(Equivalent: <tt>tosca -floria -rome</tt>)</dd>
<dt><tt>tosca AND class:B</tt></dt>
<dd>Restrict search to base names.</dd>
<dt><tt>tosca AND class:O</tt></dt>
<dd>Restrict search to (internal) occurrences.</dd>
<dt><tt>address:www.metopera.org</tt></dt>
<dd>Search in locators of external occurrences.</dd>
<dd></dd>
</dl>

<p>For each object found (whether it be a name, occurrence, or locator),
what is returned is the <i>topic</i> to which the object belongs, along
with the topic type (for purposes of disambiguation), the kind of object
in which the match was found, and the relevance of the hit. Hits are
ranked by relevance.</p>

  </template:put>

  <template:put name="content" body="true">
  <%
    if (fullpath == null) {
  %>
      The fulltext index file '<%= fullpath %>' is not accessible.
  <%
    } else if (!query.equals("")) {
      SearcherIF sengine;
      if (topicmap.getStore().getProperty("net.ontopia.infoset.fulltext.impl.rdbms.RDBMSSearcher.type") != null)
        sengine = new net.ontopia.infoset.fulltext.impl.rdbms.RDBMSSearcher(topicmap);
      else
        sengine = new LuceneSearcher(fullpath);
      try {
        SearchResultIF result = null;
        String errmsg = null;
        try {
          result = sengine.search(query);
        } catch (org.apache.lucene.queryParser.TokenMgrError e1) {
          // Note: this error shouldn't really be thrown. It might be
          // fixed in upcoming Lucene releases.
          errmsg = e1.getMessage();
        } catch (IOException e2) {
          errmsg = e2.getMessage();
        }

        if (errmsg != null) {
          %> Error: '<i><%= errmsg %></i>'.<%
        } else {
          Set topicsFound = new HashSet();
        %>

        <%-- p>Your search found <%= result.hits() %> hits.</p --%>

        <table width='100%' class=text>
        <tr>
          <th align='left' width='40%'>Topic</th>
            <th align='left'>Type</th>
            <th align='left'>Match in</th>
            <th align='left'>Score</th>
        </tr>
        <%
          int size = result.hits();
          for (int i=0; i < size; i++) {
            DocumentIF doc = result.getDocument(i);
            String klass = doc.getField("class").getValue();
            String object_id = doc.getField("object_id").getValue();

            TMObjectIF tmobject = topicmap.getObjectById(object_id);

            String type = "";
            TopicIF topic = null;
            if (tmobject instanceof TopicNameIF) {
              topic = ((TopicNameIF)tmobject).getTopic();
              type = "basename";
            }
            else if (tmobject instanceof VariantNameIF) {
              topic = ((VariantNameIF)tmobject).getTopic();
              type = "variant";
            }
            else if (tmobject instanceof OccurrenceIF) {
              topic = ((OccurrenceIF)tmobject).getTopic();
              type = "occurrence";
              // }
              // else if (tmobject instanceof TopicIF) {
              //   topic = ((TopicIF)tmobject);
              //   type = "topic";
            } else {
              type = klass;
            }

            if (topicsFound.contains(topic) || topic == null)
              continue;
            ContextUtils.setSingleValue("topic", pageContext, topic);

            topicsFound.add(topic);

            String output_type = "&lt;none&gt;";
            if (topic != null && topic.getTypes().size() > 0) {
              ContextUtils.setValue("topictypes", pageContext, topic.getTypes());
            } else {
              ContextUtils.setValue("topictypes", pageContext, Collections.EMPTY_SET);
            }

            String content = "";
            if (doc.getField("content") == null &&
                doc.getField("address") != null) {
              String address = doc.getField("address").getValue();
              String _title = "<none>";

              if (tmobject instanceof OccurrenceIF) {
                if (((OccurrenceIF)tmobject).getType() != null)
                  _title = topic_stringifier.toString(((OccurrenceIF)tmobject).getType());
              }
              content = "<a href='" + address + "'>" + _title + "</a>";
            }

            int score = (int)(result.getScore(i) * 100);

  %>
  <tr><td>
    <a href="
      <output:link of="topicmap"
       template="../../models/topic_%model%.jsp?tm=%topicmap%&id="
       generator="modelLinkGenerator"/><output:id of="topic"/>">
               <output:name of="topic"/>
    </a>&nbsp;&nbsp;&nbsp;</td>
  <td>
    <logic:foreach name="topictypes" separator="|">
      <a href="
        <output:link of="topicmap"
           template="../../models/topic_%model%.jsp?tm=%topicmap%&id="
           generator="modelLinkGenerator"/><output:id/>">
                 <output:name/>
      </a>
    </logic:foreach>
  </td>
  <td><i><%= type %>&nbsp;<%= content %></i></td>
  <td><%= score %>%</td></tr>
  <%
        }
      %></table><%
      }
    } finally {
      sengine.close();
    }
  } // if query
  %>
  </template:put>

    <%-- ============== Outsourced application wide standards ============== --%>
    <template:put name='application' content='/fragments/application.jsp'/>
    <template:put name='header-tagline' content='/fragments/tagline-header.jsp'/>
    <template:put name='footer-tagline' content='/fragments/tagline-footer.jsp'/>

  </template:insert>
</logic:context>
