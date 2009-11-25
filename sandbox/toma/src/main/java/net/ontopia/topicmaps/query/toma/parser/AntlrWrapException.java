/**
 * Copyright (C) 2009 Space Applications Services
 *   <thomas.neidhart@spaceapplications.com>
 *
 * This file is part of the Ontopia project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ontopia.topicmaps.query.toma.parser;

import antlr.RecognitionException;

/**
 * INTERNAL: Exception used to wrap other exceptions so that they can
 * be thrown from inside ANTLR-generated code.
 */
@SuppressWarnings("serial")
public class AntlrWrapException extends RecognitionException 
{
  public Exception exception;

  public AntlrWrapException(Exception exception) {
    this.exception = exception;
  }

  public Exception getException() {
    return exception;
  }
}
