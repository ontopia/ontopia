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

import java.io.IOException;
import java.util.Iterator;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.JspTagException;

/**
 * INTERNAL: Implemented by a tag which produces
 * output from an input collection somehow.
 */
public interface OutputProducingTagIF {

  /**
   * INTERNAL: Generate information extracted from the input collection
   * provided access by specified iterator. This is expected to be
   * written to the <code>JspWriter</code> object.
   */
  void generateOutput(JspWriter out,
                             Iterator iterator)
    throws JspTagException, IOException;
  
}





