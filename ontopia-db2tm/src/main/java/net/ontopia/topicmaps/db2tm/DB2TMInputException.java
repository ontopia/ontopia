
package net.ontopia.topicmaps.db2tm;

import java.util.Arrays;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Thrown when data input errors are detected by DB2TM.</p>
 */
public class DB2TMInputException extends DB2TMException {

  public DB2TMInputException(String message, Throwable cause) {
    super(message, cause);
  }

  public DB2TMInputException(String message) {
    super(message);
  }
  
  public DB2TMInputException(String message, Relation relation, String[] tuple) {
    super(message + ", relation: '" + relation.getName() + "', tuple: " + Arrays.asList(tuple));
  }
  
  public DB2TMInputException(String message, Entity entity, String[] tuple) {
    super(message + ", relation: '" + entity.getRelation().getName() + "', tuple: " + Arrays.asList(tuple));
  }
  public DB2TMInputException(String message, Entity entity, String[] tuple, String value) {
    super(message + ": '" + value + "', relation: '" + entity.getRelation().getName() + "', tuple: " + Arrays.asList(tuple));
  }
  
}
