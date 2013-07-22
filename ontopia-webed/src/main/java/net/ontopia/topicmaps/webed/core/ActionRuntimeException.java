/*
 * #!
 * Ontopia Webed
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

package net.ontopia.topicmaps.webed.core;

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * PUBLIC: An exception that is thrown when something goes wrong while
 * an action is being executed. Causes form processing to stop and
 * the current transaction to be rolled back.
 */
public class ActionRuntimeException extends OntopiaRuntimeException {
  protected boolean isCritical = true;

  /**
   * PUBLIC: Creates an exception wrapping the Throwable.
   */
  public ActionRuntimeException(Throwable e) {
    super(e);
  }

  /**
   * PUBLIC: Creates an exception with a string message.
   */
  public ActionRuntimeException(String message) {
    super(message);
  }

  /**
   * PUBLIC: Creates an exception with a string message which wraps
   * the Throwable. The messages and stack traces of both exceptions
   * will be shown.
   */
  public ActionRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * PUBLIC: Creates an exception wrapping the Throwable.
   * @param isCritical Flag indicating whether this is a critical error.
   * @since 3.0
   */
  public ActionRuntimeException(Throwable e, boolean isCritical) {
    super(e);
    this.isCritical = isCritical;
  }

  /**
   * PUBLIC: Creates an exception with a string message.
   * @param isCritical Flag indicating whether this is a critical error.
   * @since 3.0
   */
  public ActionRuntimeException(String message, boolean isCritical) {
    super(message);
    this.isCritical = isCritical;
  }

  /**
   * PUBLIC: Creates an exception with a string message which wraps
   * the Throwable. The messages and stack traces of both exceptions
   * will be shown.
   * @param isCritical Flag indicating whether this is a critical error.
   * @since 3.0
   */
  public ActionRuntimeException(String message, Throwable cause,
                                boolean isCritical) {
    super(message, cause);
    this.isCritical = isCritical;
  }

  /**
   * PUBLIC: Returns the value of the critical property.
   * @since 3.0
   */
  public boolean getCritical() {
    return isCritical;
  }
  
  /**
   * PUBLIC: Sets the value of the critical property.
   * @since 3.0
   */
  public void setCritical(boolean isCritical) {
    this.isCritical = isCritical;
  }
  
}
