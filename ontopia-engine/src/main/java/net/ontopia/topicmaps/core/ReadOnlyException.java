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

package net.ontopia.topicmaps.core;

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * PUBLIC: Thrown when changes are attempted made on read-only objects.</p>
 */
public class ReadOnlyException extends OntopiaRuntimeException {

  public ReadOnlyException() {
    this("Read-only objects cannot be modified.");
  }

  public ReadOnlyException(Throwable cause) {
    super(cause);
  }

  public ReadOnlyException(String message) {
    super(message);
  }

  public ReadOnlyException(String message, Throwable cause) {
    super(message, cause);
  }
  
}
