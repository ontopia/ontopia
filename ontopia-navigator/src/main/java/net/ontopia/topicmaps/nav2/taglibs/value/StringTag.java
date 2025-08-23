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

package net.ontopia.topicmaps.nav2.taglibs.value;

import java.util.Collections;
import jakarta.servlet.jsp.JspTagException;
import jakarta.servlet.jsp.tagext.BodyTagSupport;
import jakarta.servlet.jsp.tagext.BodyContent;

import net.ontopia.topicmaps.nav2.core.ValueAcceptingTagIF;

/**
 * INTERNAL: Value Producing Tag for generating a string value.
 * (somewhat special because this Tag does not need to
 *  manipulate an input collection)
 */
public class StringTag extends BodyTagSupport {

  /**
   * Actions after some body has been evaluated.
   */
  @Override
  public int doAfterBody() throws JspTagException {

    // retrieve parent tag which accepts the result of this operation
    ValueAcceptingTagIF acceptingTag = (ValueAcceptingTagIF)
      findAncestorWithClass(this, ValueAcceptingTagIF.class);

    // get body content which should contain the string we want to set.
    BodyContent body = getBodyContent();
    String content = body.getString();

    // construct new collection which consists of one String entry
    // kick it over to the nearest accepting tag
    acceptingTag.accept( Collections.singleton(content) );

    // reset body contents
    body.clearBody();
    
    return SKIP_BODY;
  }

  /**
   * reset the state of the Tag.
   */
  @Override
  public void release() {
    // overwrite default behaviour
    // do not set parent to null!!!
  }
  
}





