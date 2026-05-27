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

package net.ontopia.topicmaps.nav2.taglibs.output;

import java.io.IOException;
import java.util.Iterator;
import jakarta.servlet.jsp.JspTagException;
import jakarta.servlet.jsp.JspWriter;

/**
 * INTERNAL: Output-Producing Tag, which writes out the number
 * of objects in a collection.
 */
public class CountTag extends BaseOutputProducingTag {

  public CountTag() {
    // a number needs not to be escaped
    // we are also interested putting out 0 for empty collections
    super(false, false);
  }

  @Override
  public final void generateOutput(JspWriter out, Iterator iter)
    throws JspTagException, IOException {

    print2Writer( out, String.valueOf(getCollectionSize()) );
    
  }

}





