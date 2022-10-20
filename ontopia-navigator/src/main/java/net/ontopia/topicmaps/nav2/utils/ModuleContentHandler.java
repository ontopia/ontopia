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

package net.ontopia.topicmaps.nav2.utils;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
import net.ontopia.topicmaps.nav2.core.FunctionIF;
import net.ontopia.topicmaps.nav2.impl.basic.Function;
import net.ontopia.utils.ontojsp.JSPContentHandler;
import net.ontopia.utils.ontojsp.JSPTreeNode;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * INTERNAL: A content handler for module specification files (root
 * element "module" which consists of an arbitrary number of
 * "function" elements. For each function a JSPTree will be built
 * containing the element structure of navigator tags.
 */
public class ModuleContentHandler extends JSPContentHandler {

  /** Map with function names as keys and FunctionIF objects as values */
  protected Map functions;
  protected String curFuncName;
  protected List curFuncParams;
  protected String curFuncRetVarName;
  
  // used to suppress content inside module element
  protected boolean characterCare;

  /**
   * Default constructor.
   */
  public ModuleContentHandler() {
    super();
    functions = new HashMap();
    characterCare = false;
  }

  @Override
  public void startElement(String uri, String lname, String qname,
                           Attributes atts) throws SAXException {
    if ("module".equals(qname)) {
      // root level element found
      // only allowed element herein is: <function>
    } else if ("function".equals(qname)) {
      // a new function definition starts
      root = new JSPTreeNode("ROOT", null);
      current = root;
      parents.clear();
      curFuncName = atts.getValue("name");
      curFuncRetVarName = atts.getValue("return");
      // get out all parameter names
      curFuncParams = new ArrayList();
      if (atts.getValue("params") != null) {
        StringTokenizer strtok = new StringTokenizer(atts.getValue("params"));
        while (strtok.hasMoreTokens()) {
          curFuncParams.add(strtok.nextToken());
        }
      }
      characterCare = true;
    } else {
      // all other elements are handled by daddy
      // modifiying (adding children) to current tree node (=function)
      super.startElement(uri, lname, qname, atts);
      characterCare = true;
    }
  }
  
  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    if (characterCare) {
      super.characters(ch, start, length);
    }
  }
  
  @Override
  public void endElement(String uri, String lname, String qname)
    throws SAXException {
    
    // characterCare = false;
    if (qname.equals("module")) {
      // root level element now ends
    } else if (qname.equals("function")) {
      // create a new function object, when definition finished
      FunctionIF function = new Function(null, curFuncName, root,
                                         curFuncParams, curFuncRetVarName);
      functions.put(curFuncName, function);
      characterCare = false;
    } else {
      // all other elements are handled by daddy
      super.endElement(uri, lname, qname);
    }
  }

  /**
   * Gets the functions as a map.
   *
   * @return java.util.Map - containing as key: function name, value:
   *         FunctionIF object.
   */
  public Map getFunctions() {
    return functions;
  }
  
}
