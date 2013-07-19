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

package net.ontopia.topicmaps.query.impl.utils;

import java.io.Writer;
import java.io.IOException;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.query.impl.basic.QueryTracer;

/**
 * INTERNAL: Used to get a trace from a specific query into a writer
 * for ease of display. <b>Warning:</b> this method is not
 * thread-safe; any other queries running simultaneously will pollute
 * the trace.
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
