
package net.ontopia.topicmaps.db2tm;

import net.ontopia.utils.ObjectUtils;

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
  private int keylength;

  // used for tracking next tuple
  private String[] tuple;
  
  // used for tracking previous tuple
  private String prevorder; // ?
  private int prevchange;   // ?
  private String[] prevtuple;

  public ChangelogReaderWrapper(ChangelogReaderIF source, int keylength) {
    this.source = source;
    this.keylength = keylength;
    this.machine = new StateMachine();
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
    
    // it could be that we haven't started yet (a), in which case, kickstart things
    if (prevtuple == null && tuple == null)
      tuple = source.readNext();

    // get ready to process new key
    machine.reset();
    
    // now read new tuples until we find one belonging to a new key
    while (true) {
      // move one row forwards
      prevtuple = tuple;
      if (tuple != null) {
        try {
          machine.nextState(source.getChangeType());
        } catch (DB2TMException e) {
          throw new DB2TMException("Illegal state transision for tuple: " + java.util.Arrays.asList(tuple), e);
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

    for (int ix = 0; ix < keylength; ix++)
      if (ObjectUtils.different(tuple1[ix], tuple2[ix]))
        return false;
    return true;
  }  
}
