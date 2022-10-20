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

import java.io.Reader;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

import net.ontopia.utils.OntopiaRuntimeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: 
 *
 * @since 4.0
 */

public class OnDemandValue {

  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(OnDemandValue.class.getName());

  protected IdentityIF identity;
  protected FieldInfoIF finfo;

  protected Object value;
  
  public OnDemandValue() {
  }

  public OnDemandValue(Object value) {
    this.value = value;
  }

  public boolean hasContext() {
    return (finfo != null);
  }
  
  public void setContext(IdentityIF identity, FieldInfoIF finfo) {
    this.identity = identity;
    this.finfo = finfo;
  }

  public Object getValue() {
      return value;
  }

  public Object getValue(TransactionIF txn) {
    try {
      RDBMSAccess access = (RDBMSAccess)txn.getStorageAccess();
      AccessRegistrarIF registrar = txn.getAccessRegistrar();
      // Get ticket
      TicketIF ticket = registrar.getTicket();

      FieldInfoIF identity_field = finfo.getParentClassInfo().getIdentityFieldInfo();
      String sql_load = SQLGenerator.getSelectStatement(finfo.getParentClassInfo().getMasterTable(),
                                                        new FieldInfoIF[] { finfo }, new FieldInfoIF[] { identity_field }, 0);

      boolean mustClose = false;
      
      // Prepare statement
      PreparedStatement stm = access.prepareStatement(sql_load);
      try {
      
        // Bind identity columns
        if (log.isDebugEnabled()) {
          log.debug("Binding object identity: " + identity);
        }
        identity_field.bind(identity, stm, 1);
      
        // Execute statement
        if (log.isDebugEnabled()) {
          log.debug("Executing: " + sql_load);
        }
        ResultSet rs = stm.executeQuery();
        try {
          // Exactly one row expected
          if (rs.next()) {
          
            // Load field value
            Object result = finfo.load(registrar, ticket, rs, 1, true);
            if (result == null) {
              mustClose = true;
              return null;
            } else {
              return new SQLReader((Reader)result, rs, stm);
            }
          } else {
            // No rows were found.
            throw new IdentityNotFoundException(identity);
          }
        } finally {
          // Close result set
          if (mustClose) {
            rs.close();
          }
        }
      } finally {
        //! if (close_stm && stm != null) stm.close();
        if (stm != null && mustClose) {
          stm.close();
        }
      }
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
}
