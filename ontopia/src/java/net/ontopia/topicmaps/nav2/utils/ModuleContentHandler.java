
// $Id: ModuleContentHandler.java,v 1.7 2003/07/28 10:07:34 larsga Exp $

package net.ontopia.topicmaps.nav2.utils;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.xml.Log4jSaxErrorHandler;
import net.ontopia.topicmaps.nav2.core.FunctionIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.impl.basic.Function;
import net.ontopia.utils.ontojsp.*;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import org.apache.log4j.Logger;

/**
 * INTERNAL: A content handler for module specification files (root
 * element "module" which consists of an arbitrary number of
 * "function" elements. For each function a JSPTree will be built
 * containing the element structure of navigator tags.
 */
public class ModuleContentHandler extends JSPContentHandler {

  // initialize logging facility
  static Logger log = Logger
    .getLogger(ModuleContentHandler.class.getName());

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

  public void startElement(String uri, String lname, String qname,
                           Attributes atts) throws SAXException {
    if (qname.equals("module")) {
      // root level element found
      // only allowed element herein is: <function>
    } else if (qname.equals("function")) {
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
        while (strtok.hasMoreTokens())
          curFuncParams.add(strtok.nextToken());
      }
      characterCare = true;
    } else {
      // all other elements are handled by daddy
      // modifiying (adding children) to current tree node (=function)
      super.startElement(uri, lname, qname, atts);
      characterCare = true;
    }
  }
  
  public void characters(char[] ch, int start, int length) throws SAXException {
    if (characterCare) 
      super.characters(ch, start, length);
  }
  
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
