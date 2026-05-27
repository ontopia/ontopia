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

package net.ontopia.topicmaps.nav2.taglibs.output;

import java.io.IOException;
import java.util.Iterator;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.JspTagException;
import net.ontopia.topicmaps.nav2.utils.Stringificator;

/**
 * INTERNAL: Output Producing Tag for selecting the name of an object
 * and writing it out.<p>
 * 
 * Note: Only puts out <b>first</b> item retrieved by iterator.
 */
public class NameTag extends BaseOutputProducingTag {

  // tag attributes
  protected String nameGrabberCN = null;
  protected String nameStringifierCN = null;
  protected String basenameScopeVarName = null;
  protected String variantScopeVarName = null;
  
  @Override
  public void generateOutput(JspWriter out, Iterator iter)
    throws JspTagException, IOException {
    
    Object elem = iter.next();
    print2Writer(out,
                 Stringificator.toString(contextTag, elem,
                                         nameGrabberCN, nameStringifierCN,
                                         basenameScopeVarName, variantScopeVarName));
  }
  
  // -----------------------------------------------------------------
  // set methods for tag attributes
  // -----------------------------------------------------------------

  public final void setGrabber(String classname) {
    this.nameGrabberCN = classname;
  }

  public final void setStringifier(String classname) {
    this.nameStringifierCN = classname;
  }
  
  public final void setBasenameScope(String scopeVarName) {
    this.basenameScopeVarName = scopeVarName;
  }
  
  public final void setVariantScope(String scopeVarName) {
    this.variantScopeVarName = scopeVarName;
  }

}
