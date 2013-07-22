/*
 * #!
 * Ontopia TMRAP
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

package net.ontopia.topicmaps.utils.tmrap;

import net.ontopia.utils.OntopiaException;

/**
 * INTERNAL: Thrown to show that there was an error at the TMRAP level.
 *
 * @since 3.1
 */
public class TMRAPException extends OntopiaException {
  public TMRAPException(String msg) {
    super(msg);
  }
  public TMRAPException(Throwable cause) {
    super(cause);
  }
  public TMRAPException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
