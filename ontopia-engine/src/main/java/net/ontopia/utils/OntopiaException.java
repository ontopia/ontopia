/*
 * #!
 * Ontopia Engine
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

package net.ontopia.utils;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * PUBLIC: An exception class that can be used to wrap other
 * exceptions with. This is the most generic exception used.</p>
 */

public class OntopiaException extends Exception {

  protected Throwable cause = null;

  public OntopiaException(String message) {
    super(message);
  }
  
  public OntopiaException(Throwable cause) {
    super();
    this.cause = cause;
  }

  /**
   * @since 1.3
   */
  public OntopiaException(String message, Throwable cause) {
    super(message);
    this.cause = cause;
  }

  /**
   * @since 1.3
   */
  @Override
  public Throwable getCause() {
    return cause;
  }

  /**
   * @since 1.3.2
   */
  @Override
  public String getMessage() {
    String message = super.getMessage();
    if (message != null) {
      return message;
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




