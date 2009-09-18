package net.ontopia.topicmaps.query.toma.tools;

import java.io.IOException;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.ParsedQueryIF;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicQueryProcessor;
import net.ontopia.topicmaps.query.toma.impl.utils.Stringifier;
import net.ontopia.topicmaps.utils.DuplicateSuppressionUtils;
import net.ontopia.topicmaps.utils.ImportExportUtils;

public class TomaQueryTest 
{

  public static void main(String[] args)
  {
    //String tmfile = "./toma/src/test/resources/query/opera.ltm";
    //String tmfile = "./toma/src/test/int-occs.ltm";
    String tmfile = "./toma/src/test/resources/query/full.ltm";
    
    try
    {
      TopicMapIF tm = ImportExportUtils.getReader(tmfile).read();
      DuplicateSuppressionUtils.removeDuplicates(tm);

      //String query = "select $t where $t.($$)<-$a(pupil-of)->(pupil).(person)<-$b(born-in)->(place) = i'milano';";
      //String query = "select $a.role where $a(defined-by)->($$) = i'ontopia';";
      String query = "select distinct substr($t.oc(specification)@english, 5, 1) where exists $t.oc@english;";

      QueryProcessorIF processor = new BasicQueryProcessor(tm);
      ParsedQueryIF pquery = processor.parse(query);

      System.out.println("Parsed query: \n" + pquery + "\n\n");

      long time = System.currentTimeMillis();
      QueryResultIF result = pquery.execute();
      System.out.println("Query time: " + (System.currentTimeMillis() - time));

      int rows = 0;

      for (int ix = 0; ix < result.getWidth(); ix++)
        System.out.print(result.getColumnName(ix) + " | ");
      System.out.println();
      System.out.println("----------------------------------------------------");
      
      while (result.next()) {
        for (int ix = 0; ix < result.getWidth(); ix++)
          System.out.print(Stringifier.toString(result.getValue(ix)) + " | ");
        System.out.println("");
        rows++;
      }

      System.out.println("Rows: " + rows);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InvalidQueryException e) {
      e.printStackTrace();
    }
  }
}
