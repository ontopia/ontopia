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

package net.ontopia.topicmaps.nav2.impl.basic;

import java.io.IOException;
import java.util.Collection;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.TagSupport;
import net.ontopia.topicmaps.nav2.core.ModuleIF;
import net.ontopia.utils.ontojsp.JSPPageExecuter;
import net.ontopia.utils.ontojsp.JSPTreeNodeIF;

/**
 * INTERNAL: A FunctionIF implementation used by the XML-based
 * ModuleIF reader code. The function evaluates the taglib tree node
 * in the context of the calling tag and the page context.
 *
 * @see net.ontopia.topicmaps.nav2.utils.ModuleReader
 */
public final class Function extends AbstractFunction {

  private ModuleIF module;
  private String name;
  private JSPTreeNodeIF rootNode;
  private Collection params;
  private String returnVariableName;
  
  /**
   * Default constructor.
   */
  public Function(String name, JSPTreeNodeIF rootNode, Collection params) {
    this(null, name, rootNode, params);
  }
  
  public Function(ModuleIF parentModule, String name,
                  JSPTreeNodeIF rootNode, Collection params) {
    this(parentModule, name, rootNode, params, null);
  }
    
  public Function(ModuleIF parentModule, String name,
                  JSPTreeNodeIF rootNode, Collection params,
                  String returnVariableName) {
    this.module = parentModule;
    this.name = name;
    this.rootNode = rootNode;
    this.params = params;
    this.returnVariableName = returnVariableName;
  }

  // -------------------------------------------------------
  // FunctionIF implementation
  // -------------------------------------------------------
  
  @Override
  public String getName() {
    return name;
  }

  @Override
  public Collection getParameters() {
    return params;
  }

  @Override
  public String getReturnVariableName() {
    return returnVariableName;
  }

  @Override
  public void call(PageContext pageContext, TagSupport callingTag)
    throws IOException, JspException {

    JSPPageExecuter exec = new JSPPageExecuter();
    exec.run(pageContext, callingTag, rootNode);
  }

  // --- deprecated methods
  
  @Override
  public JSPTreeNodeIF getRootNode() {
    return rootNode;
  }
  
  @Override
  public ModuleIF getModule() {
    return module;
  }

  // -------------------------------------------------------
  // overwrite Object implementation 
  // -------------------------------------------------------

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(64);
    sb.append("[Function: ")
      .append( name ).append(", params ")
      .append( params ).append( "]");    
    return sb.toString();
  }

}
