
// $Id: ForEachTag.java,v 1.47 2004/01/13 09:16:34 grove Exp $

package net.ontopia.topicmaps.nav2.taglibs.logic;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.BodyContent;

import net.ontopia.topicmaps.utils.TopicComparators;
import net.ontopia.topicmaps.utils.TopicStringifiers;
import net.ontopia.utils.StringifierIF;
import net.ontopia.topicmaps.nav2.utils.NavigatorUtils;
import net.ontopia.topicmaps.nav2.core.*;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import org.apache.commons.collections.comparators.ReverseComparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Logic Tag for iterating over each object in a collection,
 * creating new content for each iteration.
 */
public class ForEachTag extends BodyTagSupport {

  // initialization of logging facility
  private static Logger log = LoggerFactory.getLogger(ForEachTag.class.getName());

  // constants
  private static final int DEF_MAX_ITER = 100; // fallback-default-value
  private static final StringifierIF DEF_TOPIC_STRINGIFIER = TopicStringifiers
    .getDefaultStringifier();
  
  private static final Comparator DEF_TOPIC_COMPARATOR = TopicComparators
    .getCaseInsensitiveComparator(DEF_TOPIC_STRINGIFIER);

  private static final String DEF_ORDER_ASCENDING = "ascending";
  private static final String DEF_ORDER_DESCENDING = "descending";
  
  // members
  private ContextManagerIF ctxtMgr;
  private ContextTag contextTag;
  private Object[] items;            // collection we loop over
  private int index;                 // current index we are in the loop
  
  // tag attributes
  private String     collVariableName;
  private String     itemVariableName;
  private Comparator listComparator;
  private String     listComparatorClassName;
  private int        maxNumber;
  private int        startNumber;
  private String     separator;
  private boolean    sortItemsFlag;
  private String     sortOrder;
  private String     functionOnTruncate;

  /**
   * Default constructor.
   */
  public ForEachTag() {
    super();
    initializeValues();
  }
  
  /**
   * Process the start tag for this instance.
   */
  public int doStartTag() throws JspTagException {

    this.contextTag = FrameworkUtils.getContextTag(pageContext);
    this.ctxtMgr = contextTag.getContextManager();

    // get Collection to loop over
    Collection coll = null;
    if (collVariableName != null)
      coll = ctxtMgr.getValue(collVariableName);
    else
      coll = ctxtMgr.getDefaultValue();

    // if not maximum list length set by attribute get from configuration
    if (maxNumber <= 0)
      maxNumber = contextTag.getNavigatorConfiguration()
        .getProperty(NavigatorConfigurationIF.MAX_LIST_LENGTH,
                     NavigatorConfigurationIF.DEF_VAL_MAX_LIST_LENGTH);
    
    // establish new lexical scope for this loop
    ctxtMgr.pushScope();

    // do not proceed if no elements in collection at all
    if (coll.isEmpty()) {
      if (log.isInfoEnabled())
        log.info("Cannot loop over empty collection in '" +
                 (collVariableName != null ? collVariableName : "_default_") + "'");
      return SKIP_BODY;
    }

    // WARN: This might be problematic when collection is of unknown size.
    this.items = coll.toArray();
    // DEFAULT: do *not* sort elements in collection
    if (sortItemsFlag) {
      try {
        listComparator = getComparator();
        // TODO: enhance with more comparators?
        // TODO: Why fallback to another comparator if it is null?
        if (listComparator == null)
          listComparator = DEF_TOPIC_COMPARATOR;
        
        if (sortOrder != null && sortOrder.equals(DEF_ORDER_DESCENDING))
          listComparator = new ReverseComparator(listComparator);
        
        Arrays.sort( items, listComparator);
      } catch (Throwable t) {
        log.warn("Sort of List (variable '" + (collVariableName != null ?
                                         collVariableName : "_default_") +
                 "') had problems: " + t.getMessage());
      }
    }

    // set first element of collection in beginning
    if (startNumber >= items.length)
      startNumber = items.length - 1;
    index = startNumber;
    setVariableValues(items[index]);

    // proceed
    index = startNumber + 1;
    //log.debug("doStartTag, itemsSize: " + items.length + ", index: " + index);
    //log.debug("  coll: " + coll);
    //log.debug("    first item: " + items[0] + " type: " + items[0].getClass().getName());
    
    return EVAL_BODY_BUFFERED;
  }
  
  /**
   * Actions after some body has been evaluated.
   */
  public int doAfterBody() throws JspTagException {
    // put out the evaluated body
    BodyContent body = getBodyContent();
    JspWriter out = body.getEnclosingWriter();
    try {
      out.print(body.getString());
    } catch(IOException ioe) {
      throw new NavigatorRuntimeException("Error in ForEachTag.", ioe);
    }
    // Clear for next evaluation
    body.clearBody();

    //log.debug("doAfterBody, itemsSize: " + items.length + ", index: " + index);

    // test if we have to repeat the body 
    if (index < items.length
        && index < maxNumber) {

      // insert separating string
      if (separator != null) {
        try {
          out.print( separator );
        } catch(IOException ioe) {
          throw new NavigatorRuntimeException("Error in ForEachTag.", ioe);
        }
      }
      // set to next value in set
      setVariableValues(items[index]);
      index++;
      return EVAL_BODY_AGAIN;
    } else {
      return SKIP_BODY;
    }
    
  }
  
  /**
   * Process the end tag.
   */
  public int doEndTag() throws JspException {
    // are there still items which have not been displayed yet?
    if (index >= maxNumber) {
      // name of function to call when list is truncated
      String functionName = null;
      if (functionOnTruncate != null)
        functionName = functionOnTruncate;
      else
        functionName = contextTag.getNavigatorConfiguration()
          .getProperty(NavigatorConfigurationIF.DEF_FUNC_ONTRUNCATE);

      // call specified function if list is truncated
      if (functionName != null && !functionName.equals("")) {
        // retrieve function object from central managed pool
        FunctionIF function = contextTag.getFunction(functionName);
        if (function != null) {
          // execute function
          if (log.isDebugEnabled())
            log.debug("execute function: " + function.toString());
          try {
            function.call(pageContext, this);
          } catch (IOException ioe) {
            throw new NavigatorRuntimeException("Error in ForEachTag: JspWriter not there.", ioe);
          }
        } else {
          log.debug("ForEachTag: no truncate function was specified.");
        }
        
      }
    }
    
    // establish old lexical scope, back to outside of the loop
    ctxtMgr.popScope();

    // reset members
    ctxtMgr = null;
    contextTag = null;
    items = null;
    // index = 0;
    
    return EVAL_PAGE;
  }

  /**
   * Resets the state of the Tag.
   */
  public void release() {
    // overwrite default behaviour
    // do not set parent to null!!!
  }
  
  // -----------------------------------------------------------------
  // Set methods for tag attributes
  // -----------------------------------------------------------------
  
  public void setName(String collectionName) {
    this.collVariableName = collectionName;
  }

  public void setSet(String itemName) {
    this.itemVariableName = itemName;
  }

  public void setComparator(String classname) {
    this.listComparatorClassName = classname;
    this.sortItemsFlag = (classname != null);
  }

  public void setOrder(String order) throws NavigatorRuntimeException {
    if (!order.equals(DEF_ORDER_DESCENDING) &&
        !order.equals(DEF_ORDER_ASCENDING))
      throw new NavigatorRuntimeException("Non-supported value ('" + order +
                                          "') given for attribute 'order' in element 'foreach'.");
    
    this.sortOrder = order;
    this.sortItemsFlag = (order != null);
  }

  /**
   * Sets the maximum number of displayed items.
   *
   * @param maxString try to convert to valid integer,
   * otherwise fallback to default value.
   * If Value is 0 or negative it is set to 1.
   */
  public void setMax(String maxString) {
    try {
      this.maxNumber = Integer.parseInt( maxString );
    } catch (NumberFormatException e) {
      this.maxNumber = 0;
      log.warn("Reset invalid max value '" + maxString + "' " +
               "to '" + maxNumber + "'.");
    }
  }

  /**
   * Sets the start number for the object in the collection at this index,
   * counting from 0. Defaults to 0. Useful for implementing paging
   * of long lists.
   *
   * @param startString try to convert to valid integer,
   * otherwise fallback to default value.
   * If Value is 0 or negative it is set to 1.
   */
  public void setStart(String startString) {
    try {
      this.startNumber = Integer.parseInt( startString );
    } catch (NumberFormatException e) {
      log.warn("Reset invalid start value to '0' was '" + startString + "'.");
      this.startNumber = 0;
    }
    if (this.startNumber < 0)
      this.startNumber = 0;
  }

  public void setSeparator(String separator) {
    this.separator = separator;
  }

  public void setFunctionOnTruncate(String functionName) {
    this.functionOnTruncate = functionName;
  }

  
  // -----------------------------------------------------------------
  // Helper methods
  // -----------------------------------------------------------------

  /**
   * INTERNAL: Sets variable values with help of the context manager.
   */
  private void setVariableValues(Object elem) {
    ctxtMgr.setDefaultValue( elem );
    if (itemVariableName != null)
      ctxtMgr.setValue(itemVariableName, elem);

    // set variables in current lexical scope
    // NB: make it 1-based!
    ctxtMgr.setValue(NavigatorApplicationIF.FOREACH_SEQ_INDEX_KEY,
                     new Integer(index+1));

    // figure out if this is the first item in the iteration
    if (index == startNumber)
      ctxtMgr.setValue(NavigatorApplicationIF.FOREACH_SEQ_FIRST_KEY,
                       Boolean.TRUE);
    else
      ctxtMgr.setValue(NavigatorApplicationIF.FOREACH_SEQ_FIRST_KEY,
                       Collections.EMPTY_LIST);

    // figure out if this is the last item in the iteration
    if (index < items.length-1
        && index < maxNumber-1)
      ctxtMgr.setValue(NavigatorApplicationIF.FOREACH_SEQ_LAST_KEY,
                       Collections.EMPTY_LIST);
    else
      ctxtMgr.setValue(NavigatorApplicationIF.FOREACH_SEQ_LAST_KEY,
                       Boolean.TRUE);
  }

  /**
   * INTERNAL: Retrieves the comparator class belonging to the
   * specified name.
   */
  private Comparator getComparator() throws NavigatorRuntimeException {
    Object obj = contextTag.getNavigatorApplication()
      .getInstanceOf(listComparatorClassName);
    if (obj != null && obj instanceof Comparator)
      return (Comparator) obj;
    else
      return null;
  }

  /**
   * INTERNAL: Initialises important member values.
   */
  private void initializeValues() {
    collVariableName = null;
    itemVariableName = null;
    listComparatorClassName = null;
    sortItemsFlag = false;
    sortOrder = null;
    maxNumber = -1;
    startNumber = 0;
    separator = null;
    functionOnTruncate = null;
  }
  
}
