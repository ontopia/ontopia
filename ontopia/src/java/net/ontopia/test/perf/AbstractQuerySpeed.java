
// $Id: AbstractQuerySpeed.java,v 1.3 2004/11/28 13:45:05 larsga Exp $

package net.ontopia.test.perf;

public abstract class AbstractQuerySpeed {
  protected static final String PATH = "/tmp/test-snapshot/";
  protected static final int TIMES = 1;
  
  public void run() {
    init();
    
    // queries on planet.xtm
    load("planet.xtm");
    time("no-name-topics",
         "topic($T), not(topic-name($T, $N))?");
    
    time("one-assoc",
         "btnt($A : broader, $B : narrower)?");

    time("one-instance",
         "instance-of($A, term)?");

    time("one-direct-instance",
         "direct-instance-of($A, term)?");
    
    time("simple-count",
         "select $A, count($B) " +
         "  from btnt($A : broader, $B : narrower)?");

    time("simple-project",
         "select $A " +
         "  from btnt($A : broader, $B : narrower)?");

    time("simple-count-order",
         "select $A, count($B) " +
         "  from btnt($A : broader, $B : narrower) "+
         "  order by $B desc, $A?");
    
    time("simple-join-not",
         "select $B from \n" +
         "  instance-of($B, term), \n" +
         "  not(btnt( $A : broader, $B : narrower ))?");

    // queries on bug662.xtm
    load("bug662.xtm");
    loadRules("crapo.tl");
    time("recursive-rule",
         "descendant-of($A, sign-type)?");
    
    // queries on xmlconf.xtm
    load("xmlconf.xtm");
    time("many-joins",
         "select $KEYWORD, count($PAPER) from\n" +
         "  tertiary-mention($KEYWORD : keyword, $PAPER : paper),\n" +
         "  not(primary-mention($KEYWORD : keyword, $PAPER2 : paper)),\n" +
         "  not(secondary-mention($KEYWORD : keyword, $PAPER3 : paper))\n" +
         "order by $PAPER desc?");

    // queries on acres-master.ltm
    load("acres-master.ltm");
    loadRules("acres-rules.tlg");
    time("acres-horror",
         "has-dx($T : dx-topic, $D : dx-document), " +
         "any-subclass-of(congenital, $T), " +
         "occurs-in-or-child($T, brain-rgn) " +
         //         "indicated-by-or-child($T, @id3) " +
         "order by $T?");
  }

  private void time(String id, String query) {
    long total = 0;

//     System.out.println("\n\n----- TIMING --------------------------------------------------");
    System.out.println("\nQuery ID: " + id);
//     System.out.println(query);
//     System.out.println("");
    
    for (int ix = 0; ix < TIMES; ix++) {
      
      long start = System.currentTimeMillis();
      execute(query);
      long time = System.currentTimeMillis();
      total += time - start;

    }

    float totalSecs = (float) total;
    System.out.println("Average query time in millisecs: " +
                       ((totalSecs / TIMES)));
  }

  protected abstract void init();
  protected abstract void execute(String query);
  protected abstract void load(String filename);
  protected abstract void loadRules(String filename);
  
}
