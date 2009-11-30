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
package net.ontopia.topicmaps.query.toma.impl.utils;

import java.io.Writer;
import java.io.IOException;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * PUBLIC: Simple tracer to output the query trace to a {@link Writer} object.
 * <p>
 * <b>Warning:</b> this method is not thread-safe; any other queries running
 * simultaneously will pollute the trace.
 * </p>
 */
public class SimpleQueryTracer extends QueryTracer.TracePrinter {
  private Writer out;

  public SimpleQueryTracer(Writer out) {
    this.out = out;
  }

  public boolean isEnabled() {
    return true;
  }

  public void output(String message) {
    try {
      out.write(message + "\n");
      out.flush();
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
}
