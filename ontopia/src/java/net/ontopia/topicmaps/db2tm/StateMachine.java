
package net.ontopia.topicmaps.db2tm;

/**
 * INTERNAL: Class used to decide what change to make after seeing a
 * sequence of changes.
 */
public class StateMachine {
  private int state;

  // 0: error (also unknown change)
  // 1: create
  // 2: update
  // 3: doesn't exist (WTF?)
  // 4: delete
  // 5: ignore (also start position)
  private static int[][] TRANSITIONS = {
    /* 0: error  */ {0, 0, 0, 0, 0, 0},
    /* 1: create */ {0, 0, 1, 0, 4, 1},
    /* 2: update */ {0, 2, 2, 0, 4, 2},
    /* 3: nothing*/ {0, 0, 0, 3, 0, 0},
    /* 4: delete */ {0, 2, 0, 0, 4, 4},
    /* 5: ignore */ {0, 1, 2, 0, 4, 5},
  };
  // note that other state machines (that is, transition tables) are possible
  // this one was created to suit Bergen Kommune, in the hope that it would
  // apply generally. it is, however, quite strict.

  public StateMachine() {
    state = ChangelogReaderIF.CHANGE_TYPE_IGNORE;
  }

  public void nextState(int changetype) {
    int oldstate = state;
    state = TRANSITIONS[state][changetype];
    if (state == ChangelogReaderIF.CHANGE_TYPE_UNKNOWN)
      throw new DB2TMException("Illegal sequence of change types: " + oldstate + "->" + changetype);
  }

  public int getChangeType() {
    return state; // states correspond exactly to change types
  }

  public void reset() {
    state = ChangelogReaderIF.CHANGE_TYPE_IGNORE; // back to start position
  }
}
