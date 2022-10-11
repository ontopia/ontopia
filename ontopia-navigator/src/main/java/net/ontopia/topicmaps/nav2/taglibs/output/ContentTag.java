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
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.nav2.core.ContextManagerIF;
import net.ontopia.topicmaps.nav2.core.NavigatorConfigurationIF;
import net.ontopia.utils.CollectionUtils;
import net.ontopia.utils.StringUtils;

/**
 * INTERNAL: Output Producing Tag for writing out the content of an
 * occurrence or a string.
 * <p>Note: Only outputs first element of collection.
 */
public class ContentTag extends BaseOutputProducingTag implements Function<Object, String> {
  
  protected String strifyCN;
  
  @Override
  public final void generateOutput(JspWriter out, Iterator iter)
    throws JspTagException, IOException {

    Function<Object, String> strify = this;
    if (strifyCN != null) 
      strify = (Function<Object, String>)
        contextTag.getNavigatorApplication().getInstanceOf(strifyCN);
    
    Object elem = null;
    if (iter.hasNext()) 
      elem = iter.next();
      
    if (elem == null) {
      // try to get the variable from context and stringify it
      ContextManagerIF ctxtMgr = contextTag.getContextManager();
      Collection coll = ctxtMgr.getValue(variableName);
      if (coll != null) 
        elem = CollectionUtils.getFirstElement(coll);
    }

    // finally put out content
    out.print(strify.apply(elem));
  }

  /**
   * Tag attribute for setting the stringifier to be used when
   * producing the string from the selected object.
   * @since 2.0
   */
  public final void setStringifier(String strifyCN) {
    this.strifyCN = strifyCN;
  }

  // --- StringifierIF interface

  /**
   * PRIVATE: Included to implement StringifierIF interface.
   */
  @Override
  public String apply(Object object) {
    String content = null;

    String NULL_VALUE = null;
    String NULL_VALUE_ALT = null;
    String EMPTY_VALUE = null;
    String EMPTY_VALUE_ALT = null;
    
    // --- OccurrenceIF
    if (object instanceof OccurrenceIF) {
      OccurrenceIF occ = (OccurrenceIF) object;
      if (Objects.equals(DataTypes.TYPE_STRING, occ.getDataType()))
        content = occ.getValue();
      NULL_VALUE = NavigatorConfigurationIF.OCCURRENCE_NULLVALUE;
      NULL_VALUE_ALT = NavigatorConfigurationIF.DEFVAL_OCC_NULLVALUE;
      EMPTY_VALUE = NavigatorConfigurationIF.OCCURRENCE_EMPTYVALUE;
      EMPTY_VALUE_ALT = NavigatorConfigurationIF.DEFVAL_OCC_EMPTYVALUE;

    }

    // --- TopicNameIF && VariantNameIF
    else if (object instanceof TopicNameIF || object instanceof VariantNameIF) {
      if (object instanceof TopicNameIF)
        content = ((TopicNameIF) object).getValue();
      else
        content = ((VariantNameIF) object).getValue();

      NULL_VALUE = NavigatorConfigurationIF.NAMESTRING_NULLVALUE;
      EMPTY_VALUE = NavigatorConfigurationIF.NAMESTRING_EMPTYVALUE;
    }
    
    // --- otherwise call the standard toString method
    //     (this includes of course instances of String)
    else
      content = (object == null ? NULL_VALUE : object.toString());
    
    // --- check for null / ""
    if (content == null || content.equals("")) {
      NavigatorConfigurationIF navConf = contextTag.getNavigatorConfiguration();
      if (navConf != null) {
        if (content == null && NULL_VALUE != null)
          content = navConf.getProperty(NULL_VALUE, NULL_VALUE_ALT);
        else if (EMPTY_VALUE != null)
          content = navConf.getProperty(EMPTY_VALUE, EMPTY_VALUE_ALT);
      }
    }

    // --- output
    return StringUtils.escapeHTMLEntities(content);
  }
}
