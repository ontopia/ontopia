/*
 * #!
 * Ontopia Navigator
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.topicmaps.nav2.utils;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.nav2.core.NavigatorPageIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.ParsedQueryIF;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.topicmaps.utils.TopicTreeNode;
import net.ontopia.utils.OntopiaRuntimeException;
import org.apache.commons.lang3.StringUtils;

/**
 * EXPERIMENTAL: This class can output a nice collapsing/expanding
 * tree view of a topic map implemented with DHTML, which uses tolog
 * queries to produce the tree. The class is configurable in various
 * ways, and can also be subclassed to further fine-tune the
 * rendering.
 * 
 * @since 3.0
 */
public class DynamicTreeWidget {
  protected static final int OPEN = 1;
  protected static final int CLOSE = 2;
  protected static final int EXPAND_ALL = 3;
  protected static final int CLOSE_ALL = 4;

  protected static final int WINDOW_SIZE = 100;

  protected QueryProcessorIF processor;
  protected String nodepage;
  protected String staticurl;
  protected String imageurl = "";
  protected int windowSize = WINDOW_SIZE;
  protected NavigatorPageIF context;

  private String name = "ONTOPIA-WIDGET-ATTRIBUTE";
  private ParsedQueryIF query;
  private TopicMapIF topicmap;
  private Map map = new HashMap();
  private String topquery;
  protected String ownpage;
  private String querystr; // string repr of query, parsed into 'query'
  private String nodeFrame;

  private String tablequery;
  private java.util.Comparator childrenComparator = new java.util.Comparator() {
      private java.util.Comparator c = net.ontopia.topicmaps.utils.TopicComparators.getTopicNameComparator(java.util.Collections.EMPTY_SET);
      @Override
      public int compare(Object o1, Object o2) {
        return c.compare(((net.ontopia.topicmaps.utils.TopicTreeNode)o1).getTopic(),
                         ((net.ontopia.topicmaps.utils.TopicTreeNode)o2).getTopic());
      }
    };

  private Collection pNodes = new HashSet();
  private String[] dependentWidgets;
  private HttpServletRequest request;
  private boolean debug;

  // --- External interface

  /**
   * PUBLIC: Sets up the widget ready for use.
   * 
   * @param topicmap The topic map being displayed.
   * @param tablequery A tolog query that generates the entire tree. The
   * query must have at least two columns, where the first column
   * contains all the parent nodes and the second column contains the
   * children of those parents. Any further columns will be used to
   * populate the data attribute of the tree nodes.
   * @param ownpage The URL of the page the widget is on. The widget
   * will append request parameters in the form "a=b&c=d&e=f..."
   * @param nodepage The URL of of the page that shows the nodes.
   */
  public DynamicTreeWidget(TopicMapIF topicmap,
                           String tablequery,
                           String ownpage, String nodepage) throws InvalidQueryException {
    setTopicMap(topicmap);
    this.tablequery = tablequery;
    this.ownpage = ownpage;
    this.nodepage = nodepage;
  }

  /**
   * PUBLIC: Sets up the widget ready for use.
   */
  public DynamicTreeWidget() {
  }
  
  /**
   * PUBLIC: Sets the topic map used by the widget.
   */
  public void setTopicMap(TopicMapIF topicmap) {
    this.topicmap = topicmap;
    this.processor = QueryUtils.getQueryProcessor(topicmap);
  }

  /**
   * PUBLIC: Sets the tolog query that generates the entire tree. The
   * query must have at least two columns, where the first column
   * contains all the parent nodes and the second column contains the
   * children of those parents. Any further columns will be used to
   * populate the data attribute of the tree nodes.
   */
  public void setTableQueryString(String tablequery) {
    this.tablequery = tablequery;
  }

  /**
   * PUBLIC: The URL of the page the widget is on. The widget will
   * append request parameters in the form "a=b&c=d&e=f..."
   */
  public void setOwnPageUrl(String ownPageUrl) {
    this.ownpage = ownPageUrl;
  }

  /**
   * PUBLIC: The URL of of the page that shows the nodes.
   */
  public void setNodePageUrl(String nodePageUrl) {
    this.nodepage = nodePageUrl;
  }
  
  /**
   * INTERNAL: Debug flag. Used only for debugging purposes.
   */
  public void setDebug(boolean debug) {
    this.debug = debug;
  }

  /**
   * PUBLIC: The name of the session key in which the set of open nodes is
   * stored. Using the same session key for different widgets will make them
   * share open/closed information.
   */
  public void setWidgetName(String name) {
    this.name = escapeName(name);
  }

  /**
   * PUBLIC: The name of the session key in which the set of open nodes is
   * stored. Using the same session key for different widgets will make them
   * share open/closed information.
   */
  public void setDependentWidgets(String[] widget_names) {
    this.dependentWidgets = new String[widget_names.length];
    for (int i=0; i < dependentWidgets.length; i++) {
      dependentWidgets[i] = escapeName(widget_names[i]);
    }
  }

  protected String escapeName(String name) {
    return StringUtils.replace(name, "-", "_");
  }

  /**
   * PUBLIC: The URL at which the graphics used by the widget are found. The
   * widget will produce HTML like &lt;img src="[imageurl]spacer.gif"&gt; to
   * refer to the graphics.
   */
  public void setImageUrl(String imageurl) {
    this.imageurl = imageurl;
  }

  /**
   * PUBLIC: Sets the maximum number of nodes displayed by the widget at once.
   * If the number of nodes to display exceeds the maximum the widget will break
   * the display into multiple "pages".
   */
  public void setWindowSize(int windowSize) {
    this.windowSize = windowSize;
  }

  /**
   * PUBLIC: The name of the HTML frame in which to open links to nodes.
   */
  public void setNodeFrame(String nodeFrame) {
    this.nodeFrame = nodeFrame;
  }

  /**
   * PUBLIC: Runs the widget, producing the output.
   */
  public void run(PageContext ctxt, Writer writer) throws IOException,
      InvalidQueryException, NavigatorRuntimeException {
    run((HttpServletRequest) ctxt.getRequest(), writer);
  }

  /**
   * EXPERIMENTAL: The name of the HTML frame in which to open links
   * to nodes.
   *
   * @since 2.0.3
   */
  public void setTableQuery(String tablequery) {
    this.tablequery = tablequery;
  }
  public void setChildrenComparator(java.util.Comparator childrenComparator) {
    this.childrenComparator = childrenComparator;
  }
  public void setTopQuery(String topquery) {
    if (topquery != null && "".equals(topquery.trim())) {
      this.topquery = null;
    } else {
      this.topquery = topquery;
    }
  }

  /**
   * PUBLIC: Runs the widget, producing the output.
   * 
   * @since 2.2.1
   */
  public void run(HttpServletRequest request, Writer writer)
      throws IOException, InvalidQueryException, NavigatorRuntimeException {

    initializeContext(request);
    Map parameters = request.getParameterMap();

    this.request = request;

    // check that query has been parsed
    if (query == null && querystr != null) {
      query = processor.parse(querystr, context.getDeclarationContext());
    }

    int topline = 0;
    if (parameters.containsKey("topline")) {
      try {
        topline = Integer.parseInt(get(parameters, "topline"));
      } catch (NumberFormatException e) {}
    }

    try {
      doQuery(topline, writer);
    } catch (InvalidQueryException e) {
      throw new net.ontopia.utils.OntopiaRuntimeException(e);
    }

    this.request = null;
  }

  protected void initializeContext(HttpServletRequest request) {

    if (context == null) {
      context = FrameworkUtils.getContextTag(request);
    }
  }

  private void doQuery(int topline, Writer writer) throws IOException,
      InvalidQueryException {

    TopicTreeNode tree = buildTree();
    writeHTML(tree, topline, writer);
  }

  // --- Logic

  private TopicTreeNode buildTree() throws InvalidQueryException {
    TopicTreeNode root = new TopicTreeNode(null);
      
    if (tablequery != null) {
      
      // child of root if no parent; leaf if no children
      Map pmap = new HashMap(); // { pt : pn }
      Map cmap = new HashMap(); // { ct : { cn : pn } }

      // execute table query
      QueryResultIF qr1 = processor.execute(tablequery);
      while (qr1.next()) {        
        TopicIF parent = (TopicIF) qr1.getValue(0);
        TopicIF child = (TopicIF)qr1.getValue(1); 
        
        Object[] data = null;
        if (qr1.getWidth() > 2) {
          data = new Object[qr1.getWidth()-2];
          for (int i=0; i < data.length; i++) {
            data[i] = qr1.getValue(2+i);
          }
        }
        // use parentless node as top node if no top query given
        if (topquery == null && parent == null) {
          registerNodes(root, child, data, pmap, cmap);
        } else {
          registerNodes(parent, child, data, pmap, cmap);
        }
      }

      // execute top query
      if (topquery != null) {
        QueryResultIF qr2 = processor.execute(topquery);
        while (qr2.next()) {        
          TopicIF child = (TopicIF)qr2.getValue(0); 
          
          Object[] data = null;
          if (qr2.getWidth() > 1) {
            data = new Object[qr2.getWidth()-1];
            for (int i=0; i < data.length; i++) {
              data[i] = qr2.getValue(1+i);
            }
          }    
          registerNodes(root, child, data, pmap, cmap);
        }
      }

      // loop over parents and attach to root
      Iterator iter = pmap.values().iterator();
      while (iter.hasNext()) {
        TopicTreeNode node = (TopicTreeNode)iter.next();
        if (node.getParent() == null) {
          continue;
          //! node.setParent(root);         
          //! // run node data query because root nodes have no data attached
          //! if (this.dataquery != null) {
          //!   Map params = Collections.singletonMap("topic", node.getTopic());
          //!   QueryResultIF qr3 = processor.execute(dataquery, params);
          //!   if (qr3.next()) {
          //!     Object[] data = new Object[qr3.getWidth()];
          //!     for (int i=0; i < data.length; i++) {
          //!       data[i] = qr3.getValue(i);
          //!     }
          //!     node.setAttribute("data", data);
          //!   }
          //! }
        }
        if (node.getChildren().isEmpty()) {
          node.setAttribute("action", null);
        } else {
          node.setAttribute("action", "close");
          // sort children
          if (childrenComparator != null) {
            java.util.Collections.sort(node.getChildren(), childrenComparator);
          }
        }
      }

      // sort children of root node
      if (childrenComparator != null) {
        java.util.Collections.sort(root.getChildren(), childrenComparator);
      }

    } else {

      QueryResultIF result = processor.execute(topquery, context
                                          .getDeclarationContext());
      while (result.next()) {
       TopicIF topic = (TopicIF) result.getValue(0);
       TopicTreeNode group = new TopicTreeNode(topic);
       group.setParent(root);
       group.setAttribute("id", getId(topic));
       
       process(group);
       if (group.getChildren().isEmpty()) {
         group.setAttribute("action", null);
       } else {
         group.setAttribute("action", "close");
       }
      }
    }
    return root;
  }

  private void registerNodes(TopicIF parent, TopicIF child, Object[] cndata, Map pmap, Map cmap) {
    // parent node
    TopicTreeNode pn = null;
    if (parent != null) {
      if (pmap.containsKey(parent)) {           
        pn = (TopicTreeNode)pmap.get(parent);
      } else {
        if (cmap.containsKey(parent)) {
          // hoist from cmap
          List cl = (List)cmap.get(parent);
          pn = (TopicTreeNode)cl.get(0);
        } else {
          // create new pn
          pn = new TopicTreeNode(parent);
          pn.setAttribute("id", getId(parent));
        }
        pmap.put(parent, pn);         
      }
    }
    registerNodes(pn, child, cndata, pmap, cmap);
  }

  private void registerNodes(TopicTreeNode pn, TopicIF child, Object[] cndata, Map pmap, Map cmap) {
    // child node
    TopicIF parent = (pn == null ? null : pn.getTopic());
    if (child != null) {
      TopicTreeNode cn = null;
      if (pmap.containsKey(child)) {
        cn = (TopicTreeNode)pmap.get(child);
      } else {
        List cl = (List)cmap.get(child);
        if (cl == null) {
          cl = new ArrayList();
          cmap.put(child, cl);
        }
        for (int i=0; i < cl.size(); i=i+2) {
          TopicTreeNode c = (TopicTreeNode)cl.get(i);
          TopicTreeNode p = (TopicTreeNode)cl.get(i+1);
          if (Objects.equals(p.getTopic(), parent)) {
            cn = c;
            pmap.put(parent, p);
            break;
          }
        }
        if (cn == null) {
          cn = new TopicTreeNode(child);
          cn.setAttribute("id", getId(child));
          cl.add(cn);
          cl.add(pn);
        }
      }
         
      // other columns retrieved as data array
      if (cndata != null) {
        cn.setAttribute("data", cndata);
      }

      if (pn != null) {
        cn.setParent(pn);
      }
    }
  }

  private QueryResultIF getChildren(TopicIF topic) throws InvalidQueryException {
    map.put("parent", topic);
    return query.execute(map);
  }

  // --- Reusable

  private void process(TopicTreeNode parent) throws InvalidQueryException {
    TopicIF topic = parent.getTopic();
    QueryResultIF children = getChildren(topic);

    while (children.next()) {
      TopicIF childtopic = (TopicIF) children.getValue(0);
      TopicTreeNode child = new TopicTreeNode(childtopic);
      child.setAttribute("id", getId(childtopic));
      child.setParent(parent);

      process(child);
      if (!child.getChildren().isEmpty()) {
        child.setAttribute("action", "close");
      }
    }
  }

  private void writeHTML(TopicTreeNode node, int topline, Writer writer)
      throws IOException {

    if (request.getAttribute("DynamicTreeWidget_javascriptWritten") == null) {

      request.setAttribute("DynamicTreeWidget_javascriptWritten", Boolean.TRUE);

      writer.write("<script>\n" +
                   "  function expand_all(pnodes, widgetname) {\n" +
                   "    for (i=0; i < pnodes.length; i++) {\n" +
                   "      var id = widgetname + \"_\" + pnodes[i];\n" + 
                   "      var img = document.getElementById(id);\n" +
                   "      if (img != null && img.src != null && img.src.indexOf(\"expand.gif\") != -1) {\n" +
                   "        var ix = img.src.indexOf(\"expand.gif\");\n" +
                   "        img.src = img.src.substring(0, ix) + \"collapse.gif\";\n" +
                   "      }" +
                   "      var span = document.getElementById(id + \"span\");\n" +
                   "      if (span != null) { display_on(span); }\n" +
                   "    }\n" + 
                   "  }\n" + 

                   "  function close_all(pnodes, widgetname) {\n" +
                   "    for (i=0; i < pnodes.length; i++) {\n" +
                   "      var id = widgetname + \"_\" + pnodes[i];\n" + 
                   "      var img = document.getElementById(id);\n" +
                   "      if (img != null && img.src != null && img.src.indexOf(\"collapse.gif\") != -1) {\n" +
                   "        var ix = img.src.indexOf(\"collapse.gif\");\n" +
                   "        img.src = img.src.substring(0, ix) + \"expand.gif\";\n" +
                   "      }" + 
                   "      var span = document.getElementById(id + \"span\");\n" +
                   "      if (span != null) { display_off(span); }\n" +
                   "    }\n" + 
                   "  }\n" + 

                   "  function open_close(elementid, widgetname) {\n" +
                   "    var id = widgetname + \"_\" + elementid;\n" + 
                   "    element = document.getElementById(id);\n" +
                   "    if (element != null) {\n" +
                   "      if (element.src.indexOf(\"expand.gif\") != -1) {\n" +
                   "        var ix = element.src.indexOf(\"expand.gif\");\n" +
                   "        element.src = element.src.substring(0, ix) + \"collapse.gif\";\n" +
                   "      } else {\n" +
                   "        var ix = element.src.indexOf(\"collapse.gif\");\n" +
                   "        element.src = element.src.substring(0, ix) + \"expand.gif\";\n" +
                   "      }\n" +
                   "    }\n" +
                   "    switch_display(id + 'span');\n" +
                   "  }\n" +

                   "  function switch_display(elementid) {\n" +
                   "    element = document.getElementById(elementid);\n" +
                   "    if (element != null) {\n" +
                   "      if (element.style.display == \"none\")\n" +
                   "        element.style.display = \"\";\n" +
                   "      else\n" +
                   "        element.style.display = \"none\";\n" +
                   "    }\n" +
                   "  }\n" +
                   "  function display_on(element) {\n" +
                   "    element.style.display = \"\";\n" +
                   "  }\n" +
                   "  function display_off(element) {\n" +
                   "    element.style.display = \"none\";\n" +
                   "  }\n" +
                   "</script>\n");
    }
    
    writer.write("<script>\n" +
                 "  var pNodes_" + name + ";\n");

    // expand_all_*
    writer.write("  function expand_all_" + name + "() {\n" +
                 "    expand_all(pNodes_" + name + ", '" + name + "');\n");
    if (dependentWidgets != null) {
      for (int i=0; i < dependentWidgets.length; i++) {
        writer.write("    expand_all(pNodes_" + dependentWidgets[i] + ", '" + dependentWidgets[i] + "');\n");
      }
    }
    writer.write("  }\n");

    // close_all_*
    writer.write("  function close_all_" + name + "() {\n" +
                 "    close_all(pNodes_" + name + ", '" + name + "');\n");
    if (dependentWidgets != null) {
      for (int i=0; i < dependentWidgets.length; i++) {
        writer.write("    close_all(pNodes_" + dependentWidgets[i] + ", '" + dependentWidgets[i] + "');\n");
      }
    }
    writer.write("  }\n");

    // open_close_*
    writer.write("  function open_close_" + name + "(elementid) {\n" +
                 "    open_close(elementid, '" + name + "');\n");
    if (dependentWidgets != null) {
      for (int i=0; i < dependentWidgets.length; i++) {
        writer.write("    open_close(elementid, '" + dependentWidgets[i] + "');\n");
      }
    }
    writer.write("  }\n");

    writer.write("</script>\n");
    
    int nodes = countNodes(node);
    staticurl = ownpage + "topline=";
    startRender(writer);

    if (topline > 1) {
      renderBackButton(writer, topline);
    }
    renderExpandAllButton(writer, topline);
    renderCloseAllButton(writer, topline);
    if (topline + windowSize < nodes) {
      renderForwardButton(writer, topline);
    }
    writer.write("<br><br>\n");
    writer.write("<span id='" + name + "'>\n");
    List children = node.getChildren();
    int lineno = 0;
    for (int ix = 0; ix < children.size(); ix++) {
      lineno = writeNode((TopicTreeNode) children.get(ix), topline, writer, 0,
                         lineno, false);
    }
    writer.write("</span>");
    writer.write("<br>");
    if (topline > 1) {
      renderBackButton(writer, topline);
    }
    renderExpandAllButton(writer, topline);
    renderCloseAllButton(writer, topline);
    if (topline + windowSize < nodes) {
      renderForwardButton(writer, topline);
    }

    endRender(writer);

    writer.write("<script>\n");
    
    writer.write("  pNodes_" + name + " = new Array(");
    Iterator iter = pNodes.iterator();
    while (iter.hasNext()) {
      writer.write('"');
      String v = (String)iter.next();
      writer.write(v);
      writer.write('"');
      if (iter.hasNext()) {
        writer.write(',');
      }
    }
    writer.write(");\n");
    writer.write("</script>\n");
  }

  private int writeNode(TopicTreeNode node, int topline, Writer writer,
                        int level, int lineno, boolean indoc) throws IOException {

    boolean nextIndoc = indoc;
    String id = (String) node.getAttribute("id");
    String action = (String) node.getAttribute("action");
    if (action != null && action.equals("close")) {
      action = "open";
    }
    boolean isopen = action != null && action.equals("open");

    lineno++;
    if (lineno >= topline && lineno <= topline + windowSize) {
      if (action == null) {
        writer.write("<img border=0 src=" + imageurl + "spacer.gif width="
            + (level * 30) + " height=5>" + "<img border=0 src=" + imageurl
            + "boxed.gif>");
      } else {
        renderNodeButton(topline, level, isopen ? OPEN : CLOSE, id, writer);
      }

      writer.write("<a name=" + id + "></a>");

      renderNode(node, writer);

      writer.write("<br>\n");
    }

    if (lineno < topline + windowSize) {
      List children = node.getChildren();
      if (!children.isEmpty()) {
        pNodes.add(id);
        writer.write("<span class=pnode id=" + getQualifiedId(id) + "span style=\"display: " +
                     (isopen ? "none" : "inline") + "\">");
      }
      for (int ix = 0; ix < children.size(); ix++) {
        lineno = writeNode((TopicTreeNode) children.get(ix), topline, writer,
                           level + 1, lineno, nextIndoc);
      }

      if (!children.isEmpty()) {
        writer.write("</span>");
      }
    }

    return lineno;
  }

  // --- Utilities

  protected TopicIF getTopic(String id) {
    return (TopicIF) topicmap.getObjectById(id);
  }

  protected String getId(TopicIF topic) {
    return topic.getObjectId();
  }

  protected String getQualifiedId(String id) {
    return name + "_" + id;
  }

  protected String list(Set nodes) {
    StringBuilder buf = new StringBuilder();
    Iterator it = nodes.iterator();
    while (it.hasNext()) {
      buf.append("," + getId((TopicIF) it.next()));
    }
    return buf.toString();
  }

  private String get(Map parameters, String name) {

    // the servlets 2.3 spec says the Map should be String -> String[],
    // but Oracle 9iAS has String -> String, so we need to code around
    // that

    Object value = parameters.get(name);

    if (value instanceof String) { // then your app server is broken
      return (String) value;
    }

    // else, we continue like normal
    String[] values = (String[]) value;
    if (values == null) {
      return null;
    }
    return values[0];
  }

  // NOTE: we *don't* count the root node (it's a false convenience node)
  private int countNodes(TopicTreeNode node) {
    List children = node.getChildren();
    int size = children.size();
    int count = 0;
    for (int ix = 0; ix < size; ix++) {
      count += countNodes((TopicTreeNode) children.get(ix));
    }
    return count + size;
  }

  public String toString(TopicIF topic) {
    try {
      if (topic == null) {
        return "null";
      } else {
        return Stringificator.toString(context, topic);
      }
    } catch (NavigatorRuntimeException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  // --- Extension interface

  /**
   * PUBLIC: This method renders the tree node, including its link, but
   * <em>not</em> the button in front of the node. Intended to be overridden
   * by applications wanting to control rendering of nodes in detail.
   */
  protected void renderNode(TopicTreeNode node, Writer out) throws IOException {
    TopicIF topic = node.getTopic();    
    if (topic != null) {
      out.write("<a href=\"" + makeNodeUrl(node) + "\"");
      if (nodeFrame != null) {
        out.write(" target=\"" + nodeFrame + "\"");
      }
      if (debug) {
        Object[] data = (Object[])node.getAttribute("data");
        if (data == null) {
          out.write(" title=\"*No data*\"");
        } else if (data.length == 1) {
          out.write(" title=\"" + data[0] + "\"");
        } else {
          out.write(" title=\"" + java.util.Arrays.asList(data) + "\"");
        }
      }
      out.write(">");
    }

    out.write(toString(topic));

    if (topic != null) {
      out.write("</a>");
    }
  }

  /**
   * PUBLIC: Renders the +/- button in front of the node. Intended to be
   * overridden.
   */
  protected void renderNodeButton(int topline, int level, int action,
                                  String id, Writer out) throws IOException {

    String image = "expand";
    if (action == CLOSE) {
      image = "collapse";
    }

    out.write("<a onclick=\"open_close_" + name + "('" + id + "')\">" + "<img border=0 src="
        + imageurl + "spacer.gif width=" + (level * 30) + " height=5>"
        + "<img border=0 id=" + getQualifiedId(id) + " src=" + imageurl + image + ".gif></a>");

  }

  /**
   * PUBLIC: Renders the back button at the top/bottom of the form.
   */
  protected void renderBackButton(Writer out, int topline) throws IOException {
    out.write("<a href=\"" + staticurl + (topline - windowSize)
        + "\" title='Show previous page'><img border=0 src=" + imageurl
        + "nav_prev.gif></a> ");
  }

  /**
   * PUBLIC: Renders the expand all button at the top/bottom of the form.
   */
  protected void renderExpandAllButton(Writer out, int topline)
      throws IOException {
    out.write("<a onclick=\"expand_all_" + name + "()\" title='Expand all nodes'><img border=0 src=" + imageurl + "expand_all.gif></a> ");
  }

  /**
   * PUBLIC: Renders the close all button at the top/bottom of the form.
   */
  protected void renderCloseAllButton(Writer out, int topline)
      throws IOException {
    out.write("<a onclick=\"close_all_" + name + "()\" title='Collapse all nodes'><img border=0 src=" + imageurl + "collapse_all.gif></a> ");
  }

  /**
   * PUBLIC: Renders the close all button at the top/bottom of the form.
   */
  protected void renderForwardButton(Writer out, int topline)
      throws IOException {
    out.write("<a href=\"" + staticurl + (topline + windowSize)
        + "\" title='Show next page'><img border=0 src=" + imageurl
        + "nav_next.gif></a>");
  }

  /**
   * PUBLIC: Called before rendering of the tree begins.
   */
  protected void startRender(Writer out) throws IOException { /* no-op */ }

  /**
   * PUBLIC: Called after the tree has been rendered.
   */
  protected void endRender(Writer out) throws IOException { /* no-op */ }

  /**
   * PUBLIC: Produces the URL to the given node.
   */
  protected String makeNodeUrl(TopicTreeNode node) {
    return nodepage + "id=" + getId(node.getTopic());
  }

}
