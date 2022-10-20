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

package net.ontopia.utils.ontojsp;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TagSupport;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: A class that executes a jsp page from a given root node.
 */
public class JSPPageExecuter {

  // members
  protected PageContext pageContext;
  
  /**
   * Runs a JSP page (resp. function), represented by the specified
   * root node. It must get the corresponding page context object. The
   * <code>parentTag</code> is supposed to be null in a standalone
   * page (like a testcase) or given by the tag which calls this
   * function (inside the parent JSP).
   */
  public void run(PageContext page, TagSupport parentTag, JSPTreeNodeIF root)
    throws JspException, IOException {

    pageContext = page;    
    runTag(parentTag, root.makeClone());
  }

  // ---------------------------------------------------------
  // internal helper methods
  // ---------------------------------------------------------

  /**
   * Executes the children of this node.
   */
  protected void runTag(TagSupport parentTag, JSPTreeNodeIF node)
    throws JspException, IOException {

    //System.out.println("Running: " + node);
    
    List<JSPTreeNodeIF> children = node.getChildren();
    for (JSPTreeNodeIF curNode : children) {
      TagSupport curTag = curNode.getTag();

      // if content tag just put it out and proceed with next
      if (curTag == null) {
        pageContext.getOut().write(curNode.getContent());
        continue;
      }

      // initialize tag
      //! curTag.setParent(node.getTag());
      curTag.setParent(parentTag);
      curTag.setPageContext(pageContext);
      setAttributeValues(curNode, curTag);
      
      // run tag
      int startTagToken = curTag.doStartTag();
      if (startTagToken != TagSupport.SKIP_BODY) {

        if (startTagToken == BodyTagSupport.EVAL_BODY_BUFFERED) {
          // check if BodyTagSupport instance  
          BodyTagSupport btag = (BodyTagSupport) curTag;
          BodyContent body = pageContext.pushBody();
          body.clearBody(); // TOMCAT MADE ME DO IT :-(
          btag.setBodyContent(body);
          btag.doInitBody();

          loopTag(btag, curNode);

          // Release the body
          pageContext.popBody();
        } else if (startTagToken == BodyTagSupport.EVAL_BODY_INCLUDE) {
          loopTag(curTag, curNode);
        } else {
          throw new OntopiaRuntimeException("Internal error: unknown doStartTag token: " + startTagToken);
        }
        
      }
      // FIXME: Handle SKIP_PAGE;
      curTag.doEndTag();
      //tag.release(); FIXME: having this call here can't possibly be correct
    } // for i
  }

  private void loopTag(TagSupport tag, JSPTreeNodeIF curNode)
    throws JspException, IOException {
    // loop as long as tag says so
    int token;
    do {
      runTag(tag, curNode);
      token = tag.doAfterBody();
    } while (token == BodyTagSupport.EVAL_BODY_AGAIN);
    if (token != BodyTagSupport.SKIP_BODY) {
      throw new OntopiaRuntimeException("Internal error: unknown doAfterBody token: " + token);
    }
  }

  /**
   * Initializes a tag by setting its attribute values. This assumes
   * it already has its parent and pageContext.
   */
  private void setAttributeValues(JSPTreeNodeIF node, TagSupport tag)
    throws JspException {

    Map values = node.getAttributes();
    Class tagclass = tag.getClass();
    
    // Set attributes
    Iterator it = values.keySet().iterator();
    while (it.hasNext()) {
      String key = (String) it.next();
      String command = "set" + Character.toUpperCase(key.charAt(0)) +
        key.substring(1);
      Class[] strparam = { String.class };
      Class[] objparam = { Object.class };
      Object[] params = { values.get(key) };

      // get setter method
      Method setter = null;
      try {
        setter = tagclass.getMethod(command, strparam);
      } catch (NoSuchMethodException e) {
        try {
          setter = tagclass.getMethod(command, objparam);
        } catch (NoSuchMethodException e2) {
          throw new JspException(e2);
        }
      }

      // invoke setter method
      try {
        setter.invoke(tag, params);
      } catch (IllegalAccessException e) {
        throw new JspException(e);
      } catch (InvocationTargetException e) {
        Throwable cause = e.getCause();
        throw new JspException(cause.getMessage(), cause);
      }
    }
  }
}
