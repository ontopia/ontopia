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

package net.ontopia.topicmaps.nav2.servlets;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.nav2.core.NavigatorPageIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import net.ontopia.topicmaps.nav2.utils.Stringificator;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.ParsedQueryIF;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.topicmaps.utils.TopicTreeNode;
import net.ontopia.utils.OntopiaRuntimeException;
import org.apache.commons.lang3.StringUtils;

/**
 * EXPERIMENTAL: This class is highly experimental. We recommend that
 * you not use this class. If you do, expect it to break in the next
 * version.
 *
 * @since 2.0
 * @deprecated Use the version in nav2.utils. This version will be
 * removed soon.
 */
@Deprecated
public class TreeWidget {
  protected static final int OPEN       = 1;
  protected static final int CLOSE      = 2;
  protected static final int EXPAND_ALL = 3;
  protected static final int CLOSE_ALL  = 4;

  protected static final int WINDOW_SIZE = 100;

  protected QueryProcessorIF processor;
  protected String nodepage;
  protected String staticurl;
  protected Set openNodes;
  protected String imageurl;
  protected int windowSize;

  private NavigatorPageIF context;
  private String name;
  private ParsedQueryIF query;
  private TopicMapIF topicmap;
  private Map map;
  private String topquery;
  private String ownpage;
  private String querystr; // string repr of query, parsed into 'query'
  private String nodeFrame;
  private boolean addAnchor;
  
  // --- External interface


  /**
   * Sets up the widget ready for use.
   * @param topicmap The topic map being displayed.
   * @param query A tolog query that given a node generates its
   * children. Use the %parent% parameter to reference the parent node
   * in the query. Make sure the query produces a 1-column result.
   * @param topquery A tolog query that generates the list of top
   * nodes.  Make sure the query produces a 1-column result.
   * @param ownpage The URI of the page the widget is on. The widget
   * will append request parameters in the form "a=b&c=d&e=f..."
   * @param nodepage The URI of of the page that shows the nodes.
   */
  public TreeWidget(TopicMapIF topicmap, String query, String topquery,
                    String ownpage, String nodepage)
    throws InvalidQueryException {
    this.topicmap = topicmap;
    this.topquery = topquery;
    this.ownpage = ownpage;
    this.nodepage = nodepage;
    this.querystr = query;
    this.name = "ONTOPIA-WIDGET-ATTRIBUTE";
    processor = QueryUtils.getQueryProcessor(topicmap);
    map = new HashMap();
    imageurl = "";
    addAnchor = true;
    windowSize = WINDOW_SIZE;
  }

  /**
   * EXPERIMENTAL: The name of the session key in which the set of
   * open nodes is stored.
   *
   * @since 2.0.4
   */
  public void setWidgetName(String name) {
    this.name = name;
  }

  /**
   * EXPERIMENTAL: The URL at which the graphics used by the widget
   * are found.
   *
   * @since 2.0.4
   */
  public void setImageUrl(String imageurl) {
    this.imageurl = imageurl;
  }

  /**
   * EXPERIMENTAL: FIXME.
   *
   * @since 2.0.4
   */
  public void setAddAnchor(boolean addAnchor) {
    this.addAnchor = addAnchor;
  }

  /**
   * EXPERIMENTAL: FIXME
   *
   * @since 2.0.4
   */
  public void setWindowSize(int windowSize) {
    this.windowSize = windowSize;
  }

  /**
   * EXPERIMENTAL: The name of the HTML frame in which to open links
   * to nodes.
   *
   * @since 2.0.3
   */
  public void setNodeFrame(String nodeFrame) {
    this.nodeFrame = nodeFrame;
  }
  
  /**
   * EXPERIMENTAL: We <b>really</b> don't recommend that you use this
   * method.
   */
  public void loadRules(Reader rulereader) throws IOException, InvalidQueryException {
    processor.load(rulereader);
  }

  /**
   * EXPERIMENTAL: Runs the widget, producing the output.
   */
  public void run(PageContext ctxt, Writer writer)
    throws IOException, InvalidQueryException, NavigatorRuntimeException {
    context = FrameworkUtils.getContextTag(ctxt);
    HttpServletRequest request = (HttpServletRequest) ctxt.getRequest();
    Map parameters = request.getParameterMap();
    
    // check that query has been parsed
    if (query == null)
      query = processor.parse(querystr);

    // get current node
    TopicIF current = null;
    if (parameters.containsKey("current"))
      current = getTopic(get(parameters, "current"));
    
    int action = getAction(parameters);
    if (action == EXPAND_ALL)
      openNodes = new UniversalSet();
    else if (action == CLOSE_ALL)
      openNodes = new HashSet();
    else {
      openNodes = getOpenNodes(request);
      if (action == OPEN)
        openNodes.add(current);
      else if (action == CLOSE)
        openNodes.remove(current);
    }

    int topline = 0;
    if (parameters.containsKey("topline")) {
      try {
        topline = Integer.parseInt(get(parameters, "topline"));
      }
      catch (NumberFormatException e) {
      }
    }
    
    try {      
      doQuery(topline, writer);
    } catch (InvalidQueryException e) {
      throw new net.ontopia.utils.OntopiaRuntimeException(e);
    }

    setOpenNodes(request, openNodes);
  }
  
  private void doQuery(int topline, Writer writer)
    throws IOException, InvalidQueryException {
    
    TopicTreeNode tree = buildTree();
    writeHTML(tree, topline, writer);
  }

  // --- Logic

  private TopicTreeNode buildTree()
    throws InvalidQueryException {
    TopicTreeNode root = new TopicTreeNode(null);
    QueryResultIF result = processor.execute(topquery);
    while (result.next()) {
      TopicIF topic = (TopicIF) result.getValue(0);
      TopicTreeNode group = new TopicTreeNode(topic);
      group.setParent(root);
      group.setAttribute("id", getId(topic));
      
      if (openNodes.contains(topic)) {
        process(group);
        group.setAttribute("action", "close");
        openNodes.add(topic);
      } else {
        // check if there are children
        QueryResultIF leaves = getChildren(topic);
        if (leaves.next())
          group.setAttribute("action", "open");
        else
          group.setAttribute("action", null);
      }
    }
    return root;
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
      
      if (openNodes.contains(childtopic)) {
        process(child);
        if (!child.getChildren().isEmpty())
          child.setAttribute("action", "close");        
        openNodes.add(childtopic);
        
      } else {
        // this one is not open; need to check if it has children
        QueryResultIF leaves = getChildren(childtopic);
        if (leaves.next())
          child.setAttribute("action", "open");
        else
          child.setAttribute("action", null);
      }
    }
  }


  private void writeHTML(TopicTreeNode node, int topline, Writer writer) throws IOException {
    int nodes = countNodes(node);
    staticurl = ownpage + "topline=";    

    if (topline > 1)
      renderBackButton(writer, topline);
    renderExpandAllButton(writer, topline);
    renderCloseAllButton(writer, topline);
    if (topline + windowSize < nodes)
      renderForwardButton(writer, topline);
    writer.write("<br><br>\n");
    
    List children = node.getChildren();
    int lineno = 0;
    for (int ix = 0; ix < children.size(); ix++)
      lineno = writeNode((TopicTreeNode) children.get(ix), topline, writer, 0, lineno, false);

    writer.write("<br>");
    if (topline > 1)
      renderBackButton(writer, topline);
    renderExpandAllButton(writer, topline);
    renderCloseAllButton(writer, topline);
    if (topline + windowSize < nodes)
      renderForwardButton(writer, topline);

  }
  
  private int writeNode(TopicTreeNode node, int topline, Writer writer, int level, int lineno, boolean indoc) throws IOException {
    
    boolean nextIndoc = indoc;
    lineno++;
    if (lineno >= topline && lineno <= topline + windowSize) {
      String id = (String) node.getAttribute("id");
      String action = (String) node.getAttribute("action");

      if (action == null)
        writer.write("<img border=0 src=" + imageurl + "spacer.gif width=" + (level * 30) + " height=5>" +
                     "<img border=0 src=" + imageurl + "boxed.gif>");
      else
        renderNodeButton(topline, level, "open".equals(action) ? OPEN : CLOSE,
                         id, writer);

      writer.write("<a name=" + id + "></a>");

      renderNode(node, writer);
      
      writer.write("<br>\n");
    }

    if (lineno < topline + windowSize) {
      List children = node.getChildren();
      for (int ix = 0; ix < children.size(); ix++)
        lineno = writeNode((TopicTreeNode) children.get(ix), topline, writer, level+1, lineno, nextIndoc);
    }

    return lineno;
  }  

  // --- Helpers

  private int getAction(Map parameters) {
    String action = get(parameters, "todo");
    if (action == null)
      action = "close";

    switch (action) {
      case "open": return OPEN;
      case "close": return CLOSE;
      case "expandall": return EXPAND_ALL;
      case "closeall": return CLOSE_ALL;
      default: return -1;
    }
  }

  private Set getOpenNodes(HttpServletRequest request) {
    String opennodes = (String) request.getSession().getAttribute(name);
    if (opennodes == null)
      opennodes = "";
    
    return makeSet(StringUtils.split(opennodes, ","));
  }

  private void setOpenNodes(HttpServletRequest request, Set openNodes) {
    request.getSession().setAttribute(name, list(openNodes));
  }
  
  // --- Utilities
  
  private Set makeSet(String[] open) {
    Set nodes = new HashSet(open.length * 2);
    for (int ix = 0; ix < open.length; ix++) {
      if (open[ix].equals(""))
        continue;
      
      TMObjectIF object = topicmap.getObjectById(open[ix]);
      // if the ID list in the session is out of date, because the
      // topic map was reloaded or changed in the meantime, we may
      // get non-existent topics or non-topics back here. if this
      // happens we assume we have out-of-date info and stop
      if (object == null || !(object instanceof TopicIF))
        return new HashSet();
      
      nodes.add(object);
    }
    return nodes;
  }

  protected TopicIF getTopic(String id) {
    return (TopicIF) topicmap.getObjectById(id);
  }

  protected String getId(TopicIF topic) {
    return topic.getObjectId();
  }

  protected String list(Set nodes) {
    StringBuilder buf = new StringBuilder();
    Iterator it = nodes.iterator();
    while (it.hasNext()) 
      buf.append("," + getId((TopicIF) it.next()));
    return buf.toString();
  }

  private String get(Map parameters, String name) {
    String[] values = (String[]) parameters.get(name);
    if (values == null)
      return null;
    return values[0];
  }

  // NOTE: we *don't* count the root node (it's a false convenience node)
  private int countNodes(TopicTreeNode node) {
    List children = node.getChildren();
    int size = children.size();
    int count = 0;
    for (int ix = 0; ix < size; ix++)
      count += countNodes((TopicTreeNode) children.get(ix));
    return count + size;
  }

  public String toString(TopicIF topic) {
    try {
      return Stringificator.toString(context, topic);
    } catch (NavigatorRuntimeException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  // --- Extension interface

  /**
   * EXPERIMENTAL: This method renders the tree node, including its
   * link, but <em>not</em> the button in front of the node. Intended
   * to be overridden by applications wanting to control rendering of
   * nodes in detail.
   */
  protected void renderNode(TopicTreeNode node, Writer out) throws IOException {
    TopicIF topic = node.getTopic();
    if (topic != null) {
      out.write("<a href=\"" + nodepage + "id=" + getId(topic) + "\"");
      if (nodeFrame != null)
        out.write(" target=\"" + nodeFrame + "\"");
      out.write(">");
    }

    out.write(toString(topic));
      
    if (topic != null)
      out.write("</a>");
  }

  /**
   * EXPERIMENTAL: Renders the +/- button in front of the node.
   */
  protected void renderNodeButton(int topline, int level, int action, String id,
                               Writer out)
    throws IOException {

    String image = "expand";
    if (action == CLOSE)
      image = "collapse";
      
    out.write("<a href=\"" + staticurl + topline +
              "&todo=" + (action == OPEN ? "open" : "close") +
              "&current=" + id + (addAnchor ? ("#" + id) : "" ) + "\">" +
              "<img border=0 src=" + imageurl + "spacer.gif width=" + (level * 30) + " height=5>" +
              "<img border=0 src=" + imageurl + image + ".gif></a>");
    
  }

  /**
   * EXPERIMENTAL: Renders the back button at the top/bottom of the form.
   */
  protected void renderBackButton(Writer out, int topline) throws IOException {
    out.write("<a href=\"" + staticurl + (topline - windowSize) +
              "\" title='Show previous page'><img border=0 src=" + imageurl +
              "nav_prev.gif></a> ");
  }

  /**
   * EXPERIMENTAL: Renders the expand all button at the top/bottom of
   * the form.
   */
  protected void renderExpandAllButton(Writer out, int topline) throws IOException {
    out.write("<a href=\"" + ownpage + "topline=" + topline + "&todo=expandall\" title='Expand all nodes'><img border=0 src=" + imageurl + "expand_all.gif></a> ");
  }
  
  /**
   * EXPERIMENTAL: Renders the close all button at the top/bottom of
   * the form.
   */
  protected void renderCloseAllButton(Writer out, int topline) throws IOException {
    out.write("<a href=\"" + ownpage + "topline=0&todo=closeall\" title='Collapse all nodes'><img border=0 src=" + imageurl + "collapse_all.gif></a> ");
  }

  /**
   * EXPERIMENTAL: Renders the close all button at the top/bottom of
   * the form.
   */
  protected void renderForwardButton(Writer out, int topline)
    throws IOException {
    out.write("<a href=\"" + staticurl + (topline + windowSize) + "\" title='Show next page'><img border=0 src=" + imageurl + "nav_next.gif></a>");
  }
  

  // --- UniversalSet class

  // used when the "expand all" button is pressed. claims that all
  // topics are open, and since open topics are always added to the
  // set it also records which ones are open. this means that during
  // the tree traversal phase all nodes are included and added to the
  // set, so that when the staticurl is produced later all nodes are
  // included in the url. (this is necessary to ensure that the close
  // node buttons work as expected.)

  // this class is a hack, and therefore defined as an internal class.
  
  class UniversalSet extends HashSet {

    @Override
    public boolean contains(Object object) {
      return true;
    }
    
  }
}
