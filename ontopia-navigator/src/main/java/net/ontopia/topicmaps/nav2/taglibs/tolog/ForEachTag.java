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

package net.ontopia.topicmaps.nav2.taglibs.tolog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;

/**
 * INTERNAL:  
 * Tolog Tag for executing a query, iterating over each object in a result 
 * collection and creating new content for each iteration.
 * The column names in the query result are accessible as variables in the body
 * of the tag, and are bound to the query result row of that iteration.
 * The groupBy attribute can be used to indicate that only some of the
 * column names should be bound (yet), and that only lines which differ in those
 * columns should produce new output.
 * To access those columns, one can use another ForEachTag nested within this
 * one, which has no query attribute, but uses the result set produced here.
 * The nested ForEachTag can group by columns not yet grouped.
 * This can format a tree-result like the following (using a single query):
 *  Asia
 *    China
 *      Beijing
 *      Shanghai
 *    India
 *      Delhi
 *  Europe
 *    Sweden
 *      Gothenburg
 *
 */
public class ForEachTag extends BodyTagSupport {

  // members
  protected ForEachTag groupingAncestor; 
          // Used for nested grouping. 
  protected QueryWrapper queryWrapper;
          // Used for holding information common to several queries operating on
          // the same query.
  protected boolean groupColumns[];
          // Used to indicate which columns to group by.
  protected Set groupNames;
          // Holds the names to group by in this grouping ForEachTag.
  protected List orderBy;
          // Holds the names to order by in this grouping ForEachTag and
          // the ones nested within it.
  protected boolean hasValidated;
          // Indicates whether the leaf of this ForEachTag has been visited.
  protected int sequenceNumber;
  protected boolean neverEvaluatedBody;
          // Indicates that the body was never evaluated. Used to test for
          // insufficient grouping. Essentially, if the body is never evaluated,
          // one can never be absolutely sure if the query is sufficiently
          // grouped, since it depends on the contents of the body.
          
  protected static final String SEQUENCE_FIRST = "sequence-first";
  protected static final String SEQUENCE_NUMBER = "sequence-number";
  protected static final String SEQUENCE_LAST = "sequence-last";
  protected static final Collection FALSE = Collections.EMPTY_LIST;
  protected static final Collection TRUE = Collections
          .singletonList(Boolean.TRUE);
                  
  // tag attributes
  // (reminder: protected String query inherited from QueryExecutingTag)
  protected String query;
  protected String groupBy;
  protected String separator;
  
  /**
   * Process the start tag for this instance.
   * Depending on the input, chooses between evaluating an input query or
   * continuing iteration over a queryResult obtained in an ancestor ForEachTag.
   * Binds any varibles needed in the body.
   */
  @Override
  public int doStartTag() throws JspTagException {
    neverEvaluatedBody = false;
  
    // Get a queryWrapper either groupingAncestor or by creating one.
    if (query == null) {
      // This is a grouping within an ancestor ForEach - query tag.
      
      // Get the nearest ancestor ForEachTag
      groupingAncestor = (ForEachTag) findAncestorWithClass(this,
                                                            ForEachTag.class);
      
      // It's an error to have neither a query nor an ancestor.
      if (groupingAncestor == null)
        throw new JspTagException("<tolog:foreach> missing query attribute");
      
      // Share the queryWrapper in groupingAncestor;
      queryWrapper = groupingAncestor.getQueryWrapper();
      
      //! // The ancestor(s) must expect this tag as a nested tag.
      //! if (queryWrapper.fullyGrouped() && !queryWrapper.usedBy(this))
      //!   throw new JspTagException("<tolog:foreach> missing query attribute"
      //!           + " or grouping ancestor."
      //!           + " A grouping ancestor is another <tolog:foreach> tag"
      //!           + " that is nested around this <tolog:foreach> tag and"
      //!           + " groups by a subset of its columns.");
      
      // Allow this ForEachTag to take part in the processing in queryWrapper.
      queryWrapper.setUsedBy(this);  
        
      // Get the orderBy list without any variables grouped by in the ancestor.
      orderBy = new ArrayList(groupingAncestor.getOrderBy());
    } else { 
      // This is a self-contained ForEach - query tag.
      
      // Create a new queryWrapper for the current pageContext from the query.
      queryWrapper = new QueryWrapper(pageContext, query);
      
      orderBy = queryWrapper.parseQuery().getOrderBy();
      
      // Check if the result set is empty, and if so, skip the body.
      if (!queryWrapper.hasNext()) {
        processGroupBy();
        queryWrapper.getContextManager().pushScope();
        neverEvaluatedBody = true;
        return SKIP_BODY;
      }
      
      // Move to the first row of the result set
      queryWrapper.next();
    }
    
    // Establish new lexical scope for this loop
    queryWrapper.getContextManager().pushScope();
    
    // Compute which columns to group by, based on groupBy (optional)
    processGroupBy();
    
    // Move one step ahead in the query and bind all relevant variables.
    queryWrapper.bindVariables(groupColumns);

    sequenceNumber = 1;

    boolean isLast = !queryWrapper.hasNext() ||
      (groupingAncestor != null && (groupingAncestor.needsCurrentRow() || queryWrapper.isOnlyChild(groupingAncestor.groupColumns, groupColumns)));
    queryWrapper.getContextManager().setValue(SEQUENCE_FIRST, TRUE);
    queryWrapper.getContextManager().setValue(SEQUENCE_NUMBER, "1");
    queryWrapper.getContextManager().setValue(SEQUENCE_LAST, isLast ? TRUE : FALSE);
    
    return EVAL_BODY_BUFFERED;
  }
  
  /**
    * Get the names to order by in this tag (to be used by nested tags for 
    * ordering
    */
  protected List getOrderBy() {
    return orderBy;
  }
  
  /**
    * Checks if this ForEachTag needs the current row of the query result.
    */
  public boolean needsCurrentRow() {
    return queryWrapper.relevantDifferences(groupColumns)
      || (groupingAncestor != null && groupingAncestor.needsCurrentRow());
  }
  
  /**
    * Finds out which columns to group by based on a space separated string.
    */
  protected void processGroupBy() throws JspTagException {
    groupColumns = new boolean[queryWrapper.getWidth()];
    groupNames = new TreeSet();
    if (groupBy == null) {
      // By default group by all columns.
      // Set all columns to true.
      for (int i = 0; i < groupColumns.length; i++) 
        groupColumns[i] = true;
      
    } else {
      // Initialize to group by no columns. 
      for (int i = 0; i < groupColumns.length; i++) 
        groupColumns[i] = false;
    
      StringTokenizer tok = new StringTokenizer(groupBy);
      
      // Must have at least one token in groupBy.
      if (!tok.hasMoreTokens()) 
        throw new JspTagException("<tolog:foreach> : got an empty groupBy"
                + " attribute."
                + "\nPlease group by at least one column"
                + " or leave the groupBy attribute out alltogether.\n");
      
      
      // Group by all columns mentioned in the groupBy String.
      while (tok.hasMoreTokens()) {
        String currentToken = tok.nextToken(" ");
        int currentIndex = queryWrapper.getIndex(currentToken);
        
        // If token is not recognised as any of the column names
        if (currentIndex == -1) 
          throw new JspTagException("<tolog:foreach> : The name"
                  + " \"" + currentToken + 
                  "\" mentioned in groupBy=\"" + groupBy + "\"," 
                  + " is not recognised as a column name in the query:"
                  + "\n\"" + queryWrapper.getQuery() + "\".\n");
                  
        groupColumns[currentIndex] = true;
        groupNames.add(currentToken);
      }
    }
    
    queryWrapper.updateTotalGroupBy(groupColumns);
    
    // Check that the columns in groupBy match those in orderBy.
    validateGroupByOrderBy();
  }
  
  /**
    * Validate the groupNames against the names in orderBy. 
    */
  protected void validateGroupByOrderBy() throws JspTagException {
    if (hasValidated)
      return;
    else
      hasValidated = true;
    
    while (!groupNames.isEmpty()) {
      // Every name in groupNames must occur (at least somewhere in orderBy.
      if (orderBy.isEmpty()) 
        throw new JspTagException("<tolog:foreach> : A column mentioned in"
                + " groupBy=\"" + groupBy + "\""
                + " did not occur in the \"order by\" part of the query:"
                + queryWrapper.getQuery() + "."
                + "\nPlease make sure the query result is ordered in the same"
                + " way as you wish to group it in the output.\n");
      
      String currentName = (String)orderBy.get(0);
      
      // The first few names in orderBy must be in groupBy, until groupBy is
      // empty.
      // i.e. the groupBy-s must follow the order of the "order by"-s
      if (!groupNames.contains(currentName))
        throw new JspTagException("A column mentioned in"
                + " groupBy=\"" + groupBy + "\""
                + " did not match the \"order by\" of the query:"
                + "\n\"" + queryWrapper.getQuery() + "\"."
                + "\nPlease make sure the query result is ordered in the same"
                + " way as you wish to group it in the output.");
      
      orderBy.remove(0);
      groupNames.remove(currentName);
    }
  }
  
  /** 
   * Actions after some body has been evaluated.
   */
  @Override
  public int doAfterBody() throws JspTagException {
    JspWriter jspWriter;

    try {
      // Get the writer to output the query result.
      jspWriter = getBodyContent().getEnclosingWriter();
      jspWriter.print( getBodyContent().getString() );
    } catch (IOException e) {
      throw new NavigatorRuntimeException("Error in ForEachTag.", e);
    }
      
    // If current row is relevant to the grouping ancestor.
    if (groupingAncestor != null && groupingAncestor.needsCurrentRow())
      return SKIP_BODY;  
    
    // If the end of the query result has been reached.
    if (!queryWrapper.hasNext())
      return SKIP_BODY;
    
    // Move to next row in query result.  
    queryWrapper.next();
    queryWrapper.bindVariables(groupColumns);

    sequenceNumber++;
      
    // insert separating string (if any)
    if (separator != null) {
      try {  
        jspWriter.print( separator );
      } catch(IOException ioe) {
        throw new NavigatorRuntimeException("Error in ForEachTag.", ioe);
      }
    }

    boolean isLast = !queryWrapper.hasNext() ||
      (groupingAncestor != null && (groupingAncestor.needsCurrentRow() || queryWrapper.isOnlyChild(groupingAncestor.groupColumns, groupColumns)));
    queryWrapper.getContextManager().setValue(SEQUENCE_FIRST, FALSE);
    queryWrapper.getContextManager().setValue(SEQUENCE_NUMBER, Integer.toString(sequenceNumber));
    queryWrapper.getContextManager().setValue(SEQUENCE_LAST, isLast ? TRUE : FALSE);
    
    // Prepare for next iteration.
    getBodyContent().clearBody();
    return EVAL_BODY_AGAIN;
  }
  
  protected QueryWrapper getQueryWrapper() {
    return queryWrapper;
  }
  
  /**
   * Process the end tag.
   */
  @Override
  public int doEndTag() throws JspException {
    if (!(queryWrapper.fullyGrouped() || neverEvaluatedBody))
      throw new JspTagException("<tolog:foreach> - tag insufficiently grouped"
              + " or missing grouping descendant."
                + "\nA grouping descendant is another <tolog:foreach> tag"
                + " that is nested within this <tolog:foreach> tag and"
                + " groups its result further."
                + "\nA <tolog:foreach> tag is insufficiently grouped if it"
                + " has no grouping descendant and uses the \"groupBy\""
                + " attribute to group by some but not all its columns.\n");
  
    // establish old lexical scope, back to outside of the loop
    queryWrapper.getContextManager().popScope();
    reset();
    return super.doEndTag();
  }

  /**
   * Reset state so we're ready to run again.
   */
  private void reset() {
    // reset member variables
    groupingAncestor = null; 
    queryWrapper = null;
    groupColumns = null;
    groupNames = null;
    orderBy = null;
    hasValidated = false;
    sequenceNumber = 0;
    neverEvaluatedBody = false;
  }

  /**
   * Resets the state of the Tag.
   */
  @Override
  public void release() {
    // do *not* reset tag attributes here, as that will cause problems
    // with tag pooling
    super.release();
  }
  
  // -----------------------------------------------------------------
  // Set methods for tag attributes
  // -----------------------------------------------------------------
  
  public void setSeparator(String separator) {
    this.separator = separator;
  }

  public void setGroupBy(String groupBy) {
    this.groupBy = groupBy;
  }

  public void setQuery(String query) {
    this.query = query;
  }

}
