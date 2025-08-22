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

package net.ontopia.topicmaps.nav2.impl.basic;

import java.io.IOException;
import java.util.Collection;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.TagSupport;

/**
 * INTERNAL: Implementation of FunctionIF interface for testing
 * purposes. This class may also be used to demonstrate also the
 * external function mechansimn.
 *
 * @see net.ontopia.topicmaps.nav2.taglibs.logic.ExternalFunctionTag
 */
public class HelloWorldFunction extends AbstractFunction {
  
  @Override
  public Collection execute(PageContext pageContext, TagSupport callingTag)
    throws IOException, JspException {
    pageContext.getOut().print("Hello World!\n");
    return null;
  }
  
}
