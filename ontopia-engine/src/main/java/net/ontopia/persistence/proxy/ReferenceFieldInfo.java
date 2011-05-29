
package net.ontopia.persistence.proxy;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: A field that references objects. A reference field is a
 * field that references the identity of instances of a descriptor
 * class. It is also known as a foreign key field.<p>
 *
 * A reference field is a composite of one or more fields that
 * together references the identity of an instance of a descriptor
 * class. The number of fields and the types of those fields must
 * match the identity fields of the referenced descriptor class.<p>
 */

public class ReferenceFieldInfo extends AbstractFieldInfo {

  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(ReferenceFieldInfo.class.getName());

  protected ClassInfoIF value_cinfo;
  protected Class value_class;
  protected FieldInfoIF identity_field;
  protected String[] value_columns;
  protected int column_count;
  
  public ReferenceFieldInfo(ClassInfoIF parent_cinfo, FieldDescriptor field, int index) {
    super(parent_cinfo, field, index);
    
    // Class information
    this.value_class = field.getValueClass();
    this.value_cinfo = parent_cinfo.getMapping().getClassInfo(value_class);
    
    // Field descriptor
    this.identity_field = value_cinfo.getIdentityFieldInfo();

    // Compute value columns
    this.value_columns = field.getColumns();
    this.column_count = (value_columns == null ? 0 : value_columns.length);
  }

  public ClassInfoIF getValueClassInfo() {
    return value_cinfo;
  }

  public int getColumnCount() {
    return column_count;
  }
  
  public boolean isIdentityField() {
    return true;
  }
  
  public String[] getValueColumns() {
    return value_columns;
  }

  /// --- FieldHandlerIF implementation

  /**
   * INTERNAL: Loads from its containing fields the identity of an
   * object.
   */
  public Object load(AccessRegistrarIF registrar, TicketIF ticket, ResultSet rs, int rsindex, boolean direct) throws SQLException {
    // Delegate loading to identity field handler
    return identity_field.load(registrar, ticket, rs, rsindex, direct);
  }
  
  /**
   * INTERNAL: Binds the identity keys to the containing fields.
   */
  public void bind(Object value, PreparedStatement stm, int stmt_index) throws SQLException {
    // Delegate binding to identity field handler
    identity_field.bind(value, stm, stmt_index);
  }

  public void retrieveFieldValues(Object value, List field_values) {
    identity_field.retrieveFieldValues(value, field_values);
  }
  
  public void retrieveSQLValues(Object value, List sql_values) {
    identity_field.retrieveSQLValues(value, sql_values);
  }

  public String toString() {
    return "<ReferenceFieldInfo " + field.getName() + ">";
  }
  
}





