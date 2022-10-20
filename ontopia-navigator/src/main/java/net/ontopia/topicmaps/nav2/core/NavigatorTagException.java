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

package net.ontopia.topicmaps.nav2.core;

import java.io.PrintStream;
import java.io.PrintWriter;
import javax.servlet.jsp.JspTagException;

/**
 * INTERNAL: base class for a generic problem occurring when
 * using the Navigator Tag Library. 
 * Please use a more appropiate, specific subclass.
 *
 * @see net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException
 * @see net.ontopia.topicmaps.nav2.core.NavigatorUserException
 * @see net.ontopia.topicmaps.nav2.core.NavigatorCompileException
 */
public class NavigatorTagException extends JspTagException {

  protected Throwable cause = null;

  /**
   * INTERNAL: constructor with empty error message.
   */
  public NavigatorTagException() {
    super();
  }
  
  /**
   * INTERNAL: constructor with a message.
   */
  public NavigatorTagException(String message) {
    super(message);
  }
  
  /**
   * @since 1.3.4
   */
  public NavigatorTagException(Throwable cause) {
    super();
    this.cause = cause;
  }
  
  /**
   * @since 1.3.4
   */
  public NavigatorTagException(String message, Throwable cause) {
    super(message);
    this.cause = cause;
  }

  /**
   * @since 1.3.4
   */
  @Override
  public Throwable getCause() {
    return cause;
  }
  
  @Override
  public String getMessage() {
    if (cause == null) {
      return super.getMessage();
    } else {
      return cause.toString();
    }
  }
  
  @Override
  public void printStackTrace() {
    super.printStackTrace();
    if (cause != null) {
      System.err.println("Caused by:");
      cause.printStackTrace();
    }
  }
  @Override
  public void printStackTrace(PrintStream ps) {
    super.printStackTrace(ps);
    if (cause != null) {
      ps.println("Caused by:");
      cause.printStackTrace(ps);
    }
  }
  @Override
  public void printStackTrace(PrintWriter pw) {
    super.printStackTrace(pw);
    if (cause != null) {
      pw.println("Caused by:");
      cause.printStackTrace(pw);
    }
  }
  
}
