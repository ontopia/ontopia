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
import net.ontopia.persistence.query.sql.SQLValueIF;

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: The indicator field handler implementation that is able
 * to...<p>
 */

public class IndicatorFieldHandler implements FieldHandlerIF {

  protected final ObjectRelationalMappingIF mapping;
  protected final Map<Object, Class<?>> indicators;

  protected FieldHandlerIF common_handler;

  /**
   * INTERNAL:
   */
  public IndicatorFieldHandler(ObjectRelationalMappingIF mapping, Map<Object, Class<?>> indicators) {
    this.mapping = mapping;
    this.indicators = indicators;
  }
  
  @Override
  public int getColumnCount() {
    if (common_handler == null) {
      // Register common handler by pulling out first indicated class.
      registerCommonFieldHandler(indicators.values().iterator().next());
    }
    return 1 + common_handler.getColumnCount();
  }
  
  @Override
  public boolean isIdentityField() {
    return true;
  }

  protected void registerCommonFieldHandler(Class<?> indicated_klass) {
    // Register common identity field handler if not already set.
    if (common_handler != null) {
      return;
    }    
    common_handler = mapping.getClassInfo(indicated_klass).getIdentityFieldInfo();    
  }
  
  protected FieldHandlerIF getCommonFieldHandler() {
    return common_handler;
  }
  
  @Override
  public Object load(AccessRegistrarIF registrar, TicketIF ticket, ResultSet rs, int rsindex, boolean direct) throws SQLException {
    // Load class indicator. Note that the indicator must be the first
    // column.
    Object indicator = rs.getObject(rsindex);
    
    // Get class info
    Class<?> indicated_klass = indicators.get(indicator);
    if (indicated_klass == null) {
      throw new OntopiaRuntimeException("Indicator '" + indicator + "' unknown.");
    }
    
    FieldHandlerIF handler = mapping.getClassInfo(indicated_klass).getIdentityFieldInfo();    
    
    // Load identity object
    return handler.load(registrar, ticket, rs, rsindex + 1, direct);
  }
  
  @Override
  public void bind(Object value, PreparedStatement stm, int stmt_index) throws SQLException {
    throw new UnsupportedOperationException("Indicator field handler cannot bind values.");
  }

  @Override
  public void retrieveFieldValues(Object value, List<Object> field_values) {
    throw new UnsupportedOperationException("Indicator field handler cannot retrieve field values.");
  }

  @Override
  public void retrieveSQLValues(Object value, List<SQLValueIF> sql_values) {
    throw new UnsupportedOperationException("Indicator field handler cannot retrieve sql values.");
  }

  @Override
  public String toString() {
    return "<IndicatorFieldHandler " + indicators.keySet() + ">";
  }
  
}





