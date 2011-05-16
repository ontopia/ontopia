
// $Id: OnDemandValue.java,v 1.2 2008/05/29 10:54:56 geir.gronmo Exp $

package net.ontopia.persistence.proxy;

import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
  static Logger log = LoggerFactory.getLogger(OnDemandValue.class.getName());

  protected IdentityIF identity;
  protected FieldInfoIF finfo;

  protected Object value;
  protected boolean released;
  
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
    if (released)
      throw new OntopiaRuntimeException("Cannot get value from released value.");
    else
      return value;
  }

  public void releaseValue() {
    if (value != null) {
      //! if (value instanceof Reader) {
      //!   try {
      //!     ((Reader)value).close();
      //!   } catch (IOException e) {
      //!     throw new OntopiaRuntimeException(e);
      //!   }
      //! }
    }
    released = true;
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
        if (log.isDebugEnabled())
          log.debug("Binding object identity: " + identity);
        identity_field.bind(identity, stm, 1);
      
        // Execute statement
        if (log.isDebugEnabled())
          log.debug("Executing: " + sql_load);
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
          if (mustClose) rs.close();
        }
      } finally {
        //! if (close_stm && stm != null) stm.close();
        if (stm != null && mustClose) stm.close();
      }
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
}
