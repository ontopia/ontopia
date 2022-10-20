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

package net.ontopia.topicmaps.nav2.taglibs.framework;

import java.text.NumberFormat;
import java.text.DecimalFormat;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Framework related tag for logging information about
 * memory and CPU time usage to log channel.
 */
public final class MemInfoTag extends TagSupport {

  // initialization of logging facility
  private static final Logger log = LoggerFactory.getLogger(MemInfoTag.class.getName());

  // constants
  private final static NumberFormat formatter =
    new DecimalFormat("###,###,###,###");

  // members
  private long startFreeMem;
  private long startTime;
  
  // tag attributes
  private String name = "";

  /**
   * Process the start tag for this instance.
   */
  @Override
  public int doStartTag() throws JspTagException {
    log.debug( "/// " + name + ": " + generateStartMemInfo() );
    return EVAL_BODY_INCLUDE;
  }

  /**
   * Process the end tag for this instance.
   */
  @Override
  public int doEndTag() throws JspTagException {
    log.debug( "\\\\\\ " + name + ": " + generateEndMemInfo() );
    return EVAL_PAGE;
  }

  
  // -----------------------------------------------------------------
  // set methods
  // -----------------------------------------------------------------

  /**
   * setting the tag attribute name: helps to give the output
   * a more understandable description.
   */
  public void setName(String name) {
    this.name = name;
  }

  // -----------------------------------------------------------
  // internal helper methods
  // -----------------------------------------------------------

  private String generateStartMemInfo() {
    long free = Runtime.getRuntime().freeMemory();
    long tot = Runtime.getRuntime().totalMemory();
    startTime = System.currentTimeMillis();
    startFreeMem = free;
    StringBuilder strBuf = new StringBuilder(32);
    strBuf.append( "Free Mem: " ).append( formatter.format(free) )
      .append( ", Allocated Mem: " ).append( formatter.format(tot) )
      .append(".");
    return strBuf.toString();
  }

  private String generateEndMemInfo() {
    long free = Runtime.getRuntime().freeMemory();
    long usedMem = startFreeMem - free;
    long endTime = System.currentTimeMillis();

    StringBuilder strBuf = new StringBuilder();
    if (usedMem > 0) {
      strBuf.append("Used Mem: " + formatter.format(usedMem));
    } else {
      strBuf.append("garbage was collected in the meantime!");
    }

    strBuf.append(" - required " + (endTime - startTime) + " ms.");
    return strBuf.toString();
  }
  
}
