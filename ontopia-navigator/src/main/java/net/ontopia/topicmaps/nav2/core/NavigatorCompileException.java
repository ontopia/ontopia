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

/**
 * INTERNAL: Exception for a problem occurring when
 * using the Navigator Tag Library.
 * Used when attributes are not specified, given the
 * wrong values, tags have the wrong parents etc.
 */
public class NavigatorCompileException extends NavigatorTagException {

  /**
   * INTERNAL: constructor with empty error message.
   */
  public NavigatorCompileException() {
    super();
  }
  
  /**
   * INTERNAL: constructor with a message.
   */
  public NavigatorCompileException(String message) {
    super(message);
  }
  
  /**
   * @since 1.3.4
   */
  public NavigatorCompileException(Throwable cause) {
    super(cause);
  }
  
  /**
   * @since 1.3.4
   */
  public NavigatorCompileException(String message, Throwable cause) {
    super(message, cause);
  }
  
}





