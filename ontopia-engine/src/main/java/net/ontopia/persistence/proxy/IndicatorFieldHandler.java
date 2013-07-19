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

package net.ontopia.persistence.proxy;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import net.ontopia.utils.OntopiaRuntimeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: The indicator field handler implementation that is able
 * to...<p>
 */

public class IndicatorFieldHandler implements FieldHandlerIF {

  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(IndicatorFieldHandler.class.getName());

  protected ObjectRelationalMappingIF mapping;
  protected Map indicators;

  protected FieldHandlerIF common_handler;

  /**
   * INTERNAL:
   */
  public IndicatorFieldHandler(ObjectRelationalMappingIF mapping, Map indicators) {
    this.mapping = mapping;
    this.indicators = indicators;
  }
  
  public int getColumnCount() {
    if (common_handler == null)
      // Register common handler by pulling out first indicated class.
      registerCommonFieldHandler((Class)indicators.values().iterator().next());
    return 1 + common_handler.getColumnCount();
  }
  
  public boolean isIdentityField() {
    return true;
  }

  protected void registerCommonFieldHandler(Class indicated_klass) {
    // Register common identity field handler if not already set.
    if (common_handler != null) return;    
    common_handler = mapping.getClassInfo(indicated_klass).getIdentityFieldInfo();    
  }
  
  protected FieldHandlerIF getCommonFieldHandler() {
    return common_handler;
  }
  
  public Object load(AccessRegistrarIF registrar, TicketIF ticket, ResultSet rs, int rsindex, boolean direct) throws SQLException {
    // Load class indicator. Note that the indicator must be the first
    // column.
    Object indicator = rs.getObject(rsindex);
    
    // Get class info
    Class indicated_klass = (Class)indicators.get(indicator);
    if (indicated_klass == null)
      throw new OntopiaRuntimeException("Indicator '" + indicator + "' unknown.");
    
    FieldHandlerIF handler = mapping.getClassInfo(indicated_klass).getIdentityFieldInfo();    
    
    // Load identity object
    return handler.load(registrar, ticket, rs, rsindex + 1, direct);
  }
  
  public void bind(Object value, PreparedStatement stm, int stmt_index) throws SQLException {
    throw new UnsupportedOperationException("Indicator field handler cannot bind values.");
  }

  public void retrieveFieldValues(Object value, List field_values) {
    throw new UnsupportedOperationException("Indicator field handler cannot retrieve field values.");
  }

  public void retrieveSQLValues(Object value, List sql_values) {
    throw new UnsupportedOperationException("Indicator field handler cannot retrieve sql values.");
  }

  public String toString() {
    return "<IndicatorFieldHandler " + indicators.keySet() + ">";
  }
  
}





