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

package net.ontopia.topicmaps.nav2.portlets.pojos;

import java.util.List;
import java.util.ArrayList;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.RowMapperIF;
import net.ontopia.topicmaps.query.utils.QueryWrapper;

/**
 * PUBLIC: This component can create a two-level view of the top of a
 * tree, similar to the old Yahoo directory taxonomy that's still used
 * by dmoz. The structure is a list of rows where each row contains up
 * to n (configurable) top-level nodes, and each top-level node contains
 * the immediate children of the top-level node.
 *
 * <p>The object is independent of a specific topic map transaction,
 * and can thus be configured once and reused across transactions.
 */
public class YahooTree {
  private String topquery; // query to find top-level nodes
  private String query;    // query to find children
  private int columns;     // number of top-level nodes per row
  
  public YahooTree() {
    columns = 5;
  }

  public void setTopQuery(String topquery) {
    this.topquery = topquery;
  }

  public void setChildQuery(String query) {
    this.query = query;
  }

  public void setColumns(int columns) {
    this.columns = columns;
  }

  public int getColumns() {
    return columns;
  }

  public List<List<TreeNode>> makeModel(TopicMapIF topicmap) {
    // get flat list
    QueryWrapper qw = new QueryWrapper(topicmap);
    List<TreeNode> result = qw.queryForList(topquery, new NodeMapper(qw));

    // break flat list into rows with 'columns' nodes each
    List<List<TreeNode>> model = new ArrayList();
    for (int rowno = 0; rowno*columns < result.size(); rowno++) {
      int end = Math.min((rowno+1) * columns, result.size());
      model.add(result.subList(rowno*columns, end));
    }
    return model;
  }
  
  // ========================================================================

  public class TreeNode {
    private TopicIF topic;
    private QueryWrapper qw;

    public TreeNode(TopicIF topic, QueryWrapper qw) {
      this.topic = topic;
      this.qw = qw;
    }

    public TopicIF getTopic() {
      return topic;
    }

    public List<TopicIF> getChildren() {
      return qw.queryForList(query, qw.makeParams("self", topic));
    }
  }
  
  class NodeMapper implements RowMapperIF {
    private QueryWrapper qw;

    public NodeMapper(QueryWrapper qw) {
      this.qw = qw;
    }
    
    @Override
    public Object mapRow(QueryResultIF result, int row) {
      return new TreeNode((TopicIF) result.getValue(0), qw);
    }
    
  }
}
