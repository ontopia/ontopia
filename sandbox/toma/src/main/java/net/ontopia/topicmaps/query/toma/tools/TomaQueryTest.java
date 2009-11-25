/**
 * Copyright (C) 2009 Space Applications Services
 *   <thomas.neidhart@spaceapplications.com>
 *
 * This file is part of the Ontopia project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ontopia.topicmaps.query.toma.tools;

import java.io.IOException;
import java.io.StringWriter;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.ParsedQueryIF;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicQueryProcessor;
import net.ontopia.topicmaps.query.toma.impl.utils.QueryTracer;
import net.ontopia.topicmaps.query.toma.impl.utils.SimpleQueryTracer;
import net.ontopia.topicmaps.query.toma.impl.utils.Stringifier;
import net.ontopia.topicmaps.utils.DuplicateSuppressionUtils;
import net.ontopia.topicmaps.utils.ImportExportUtils;

public class TomaQueryTest 
{

  public static void main(String[] args)
  {
    //String tmfile = "./toma/src/test/resources/query/ItalianOpera.ltm";
    //String tmfile = "./toma/src/test/int-occs.ltm";
    String tmfile = "./toma/src/test/resources/query/full.ltm";
    //String tmfile = "./toma/src/test/resources/query/hierarchies.ltm";
    //String tmfile = "./toma/src/test/resources/query/family.ltm";
    
    try
    {
      TopicMapIF tm = ImportExportUtils.getReader(tmfile).read();
      DuplicateSuppressionUtils.removeDuplicates(tm);

      //String query = "select $t where $t.(teacher)<-$a(pupil-of)->(pupil).(person)<-$b(born-in)->(place) = i'milano';";
      //String query = "select $t.id where milano.(place)<-(born-in)->(person).(pupil)<-(pupil-of)->(teacher) = $t;";

      //String query = "select $t where $t.(person)<-$b(born-in)->(place) = i'milano';";      
      //String query = "select $a.role where $a(defined-by)->($$) = i'ontopia';";
      //String query = "select $t, $a where $t.type(1) = i'opera' and $t.name ~ 'F' and exists $t.oc(article)[$a];";
      //String query = "select $a where $a.player = 'ltm';";      
      //String query = "select $t where $t.type IN (format, standard);";
      //String query = "select $M.NAME, $M.oc where $M.type = opera and $M.name ~ 'Cav' order by 1;";
      //String query = "select avg($t.oc(pages)), count($t) where $t.type(1) = standard and exists $t.oc(pages);";
      //String query = "select $t where ontopia.(project)<-$a(i'contributes-to')@year2009->(person) = $t;";
      //String query = "select $t where tn.($$)<-$a(contributes-to)->(project).($$)<-(implements)->(standard) = $t;";
      //String query = "select $t.oc(libretto)@web where exists $t.oc(libretto)@web;";
      //String query = "select $OPERA, $COMPOSER, $WORK where shakespeare.(writer)<-(written-by)->(work) = $WORK and $WORK.(source)<-(based-on)->(result) = $OPERA and $OPERA.(work)<-(composed-by)->(composer) = $COMPOSER order by 2;";
      //String query = "select $PERSON, $PROJECT where ltm-standard.(standard)<-(implements)->(project) = $PROJECT and $PROJECT.(project)<-(contributes-to)->(person) = $PERSON;";
      //String query = "select distinct $OPERA where $OPERA.type = opera and $OPERA.(work)<-(appears-in)->(character) = $CHAR and " + 
      //"$CHAR.(character)<-(has-voice)->(voice-type) = $TYPE " +
      //"order by 1;";
      //String query = "select $t where $t.name@short-name = 'Leoni';";
      //String query = "select $TOPIC, $TOPIC.oc(bibref).data where exists $TOPIC.oc(bibref) union select $TOPIC, $REIFIER.oc(bibref) where $a.player = $TOPIC and $reifier = $a.reifier and exists $reifier.oc(bibref) order by 1 desc, 2;";
      //String query = "select $p, count($c) where $p.(project)<-(contributes-to)->(person) = $c;";
      //String query = "select $p, $c where exists $p.(project)<-(contributes-to)->(person)[$c];";
      //String query = "select $t.name, $t.oc where exists $t order by 1;";
      //String query = "select $COMPOSER, count($OPERA) where exists $COMPOSER.(composer)<-(composed-by)->(work)[$OPERA] order by 2 desc;";
      String query = "select $topic.name($type)@$scope, $type, $scope where $topic.type = city;";
      
      QueryProcessorIF processor = new BasicQueryProcessor(tm);
      ParsedQueryIF pquery = processor.parse(query);

      System.out.println("Parsed query: \n" + pquery + "\n\n");

      StringWriter writer = new StringWriter();
      SimpleQueryTracer tracer = new SimpleQueryTracer(writer);
      QueryTracer.addListener(tracer);
      
      long time = System.currentTimeMillis();
      QueryResultIF result = pquery.execute();
      long elapsed = (System.currentTimeMillis() - time);
      int rows = 0;

//      QueryTracer.removeListener(tracer);
      
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

      System.out.println("Query time: " + elapsed + "ms");
      System.out.println("Rows: " + rows);
      
      //System.out.println("Trace: \n" + writer.toString());
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InvalidQueryException e) {
      e.printStackTrace();
    }
  }
}
