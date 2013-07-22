/*
 * #!
 * Ontopoly Editor
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
package ontopoly.utils;

import net.ontopia.utils.OntopiaRuntimeException;

public class NoSuchTopicException extends OntopiaRuntimeException {

  private static final long serialVersionUID = 1L;

  public NoSuchTopicException(String message) {
    super(message);
  }

  public NoSuchTopicException(Throwable cause) {
    super(cause);
  }

  public NoSuchTopicException(String message, Throwable cause) {
    super(message, cause);
  }

}
