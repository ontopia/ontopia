
package net.ontopia.topicmaps.db2tm;

import java.util.Arrays;

import net.ontopia.utils.StringUtils;
import net.ontopia.utils.ObjectUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: This tuple reader wraps an underlying tuple reader, and
 * collapses a sequence of actions for the same key into a single
 * final action, using a state machine.
 *
 * Tuples are coming through ordered by key first, then by sequence.
 * We run the changes for each key through a state machine to
 * determine the final action for that key, then move on to the next
 * key.
 */
public class ChangelogReaderWrapper implements ChangelogReaderIF {
  private ChangelogReaderIF source;
  private StateMachine machine;
  private int[] keycols; // contains index in relation of each key column

  // used for tracking next tuple
  private String[] tuple;
  
  // used for tracking previous tuple
  private String prevorder; // ?
  private int prevchange;   // ?
  private String[] prevtuple;

  static Logger log = LoggerFactory.getLogger(ChangelogReaderWrapper.class.getName());
  
  public ChangelogReaderWrapper(ChangelogReaderIF source,
                                Relation relation,
                                String state_machine) {
    this.source = source;
    this.machine = new StateMachine(state_machine);

    String[] pkey = relation.getPrimaryKey();
    this.keycols = new int[pkey.length];
    for (int ix = 0; ix < pkey.length; ix++)
      keycols[ix] = relation.getColumnIndex(pkey[ix]);
  }
  
  public int getChangeType() {
    return machine.getChangeType();
  }

  public String getOrderValue() {
    return prevorder;
  }

  public String[] readNext() {
    // INVARIANT:
    //  (a) prevtuple is null, tuple is null, ready to start on first row
    //  (b) prevtuple holds previous passed-on row, tuple holds next
    //  (c) prevtuple holds previous passed-on row, tuple is null; that is,
    //      we've reached the end of the stream

    // it could be that we are finished (c), in which case, return null
    if (prevtuple != null && tuple == null)
      return null;
    
    // it could be that we haven't started yet (a), in which case,
    // kickstart things
    if (prevtuple == null && tuple == null)
      tuple = source.readNext();

    // get ready to process new key
    machine.reset();
    
    // now read new tuples until we find one belonging to a new key
    while (true) {
      if (log.isDebugEnabled())
        log.debug("State: " + machine.getChangeType() + " Tuple: (" +
                  (tuple == null ? "null" : StringUtils.join(tuple, "|")) + ")");
      
      // move one row forwards
      prevtuple = tuple;
      if (tuple != null) {
        try {
          machine.nextState(source.getChangeType());
        } catch (DB2TMException e) {
          throw new DB2TMException("Illegal state transition for tuple: " +
                                   Arrays.asList(tuple), e);
        }
        prevorder = source.getOrderValue();
      }
      
      tuple = source.readNext();

      // did we just move onto a new key?
      if (!equalsKey(prevtuple, tuple) ||
          (tuple == null && prevtuple == null))
        break;
    }

    // notice how we are now back to INVARIANT as stated above
    return prevtuple;
  }

  public void close() {
    source.close();
  }

  // INTERNAL HELPERS
  
  // this code is based on the assumption that the primary key is
  // always the first n values in the tuple, where n is the length
  // of the primary key. for now, the code does produce such tuples.
  private boolean equalsKey(String[] tuple1, String[] tuple2) {
    if ((tuple1 == null && tuple2 != null) ||
        (tuple1 != null && tuple2 == null))
      return false;
    if (tuple1 == null && tuple2 == null)
      return true;

    for (int ix = 0; ix < keycols.length; ix++)
      if (ObjectUtils.different(tuple1[keycols[ix]], tuple2[keycols[ix]]))
        return false;
    return true;
  }  
}
