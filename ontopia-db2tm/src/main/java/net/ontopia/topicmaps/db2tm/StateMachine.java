
package net.ontopia.topicmaps.db2tm;

/**
 * INTERNAL: Class used to decide what change to make after seeing a
 * sequence of changes.
 */
public class StateMachine {
  private int state;
  private int[][] transitions;

  // 0: error (also unknown change)
  // 1: create
  // 2: update
  // 3: doesn't exist (WTF?)
  // 4: delete
  // 5: ignore (also start position)
  private static int[][] STRICT_MACHINE = {
    /* 0: error  */ {0, 0, 0, 0, 0, 0},
    /* 1: create */ {0, 0, 1, 0, 4, 1},
    /* 2: update */ {0, 2, 2, 0, 4, 2},
    /* 3: nothing*/ {0, 0, 0, 3, 0, 0},
    /* 4: delete */ {0, 2, 0, 0, 4, 4},
    /* 5: ignore */ {0, 1, 2, 0, 4, 5},
  };
  private static int[][] LENIENT_MACHINE = {
    /* 0: error  */ {0, 0, 0, 0, 0, 0},
    /* 1: create */ {0, 1, 1, 0, 4, 1},
    /* 2: update */ {0, 2, 2, 0, 4, 2},
    /* 3: nothing*/ {0, 0, 0, 3, 0, 0},
    /* 4: delete */ {0, 1, 1, 0, 4, 4},
    /* 5: ignore */ {0, 1, 2, 0, 4, 5},
  };

  public StateMachine() {
    this.state = ChangelogReaderIF.CHANGE_TYPE_IGNORE;
    this.transitions = STRICT_MACHINE;
  }

  public StateMachine(String machine_type) {
    this.state = ChangelogReaderIF.CHANGE_TYPE_IGNORE;
    if (machine_type != null && machine_type.equals("lenient"))
      this.transitions = LENIENT_MACHINE;
    else
      this.transitions = STRICT_MACHINE;
  }
  
  public void nextState(int changetype) {
    int oldstate = state;
    state = transitions[state][changetype];
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
