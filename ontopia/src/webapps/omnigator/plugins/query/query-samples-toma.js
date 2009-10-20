
// $Id: query-samples.js,v 1.24 2006/10/27 12:15:24 pepper Exp $

function insertExample(exName) {

  // ===== ItalianOpera.ltm =================================================

  if (exName == "exPuccini") { // -------------------------------------------
    document.queryform.query.value =
      'select $OPERA where \n' +
      '  puccini.(composer)<-(composed-by)->(work) = $OPERA;';

  } else if (exName == "exPucciniSorted") { // ------------------------------
    document.queryform.query.value =
      'select $OPERA where \n' +
      '  puccini.(composer)<-(composed-by)->(work) = $OPERA \n' +
      '  order by 1;';

  } else if (exName == "exShakespeare") { // --------------------------------
    document.queryform.query.value =
      'select $OPERA, $COMPOSER, $WORK where \n' +
      '  shakespeare.(writer)<-(written-by)->(work) = $WORK and \n' + 
      '  $WORK.(source)<-(based-on)->(result) = $OPERA and \n' + 
      '  $OPERA.(work)<-(composed-by)->(composer) = $COMPOSER \n' +
      '  order by 2;';

  } else if (exName == "exBornDied") { // -----------------------------------
    document.queryform.query.value =
      'select $PLACE, $PERSON where \n' +
      '  $PERSON.(person)<-(born-in)->(place) = $PLACE and \n' +
      '  $PERSON.(person)<-(died-in)->(place) = $PLACE \n' +
      '  order by 1, 2;';

  } else if (exName == "exComposers") { // ----------------------------------
    document.queryform.query.value = 
      '# not possible currently due to missing grouping in toma.';
      //'using o for i"http://psi.ontopedia.net/"\n' +
      //'select $COMPOSER, count($OPERA) from\n' +
      //' o:composed_by($OPERA : o:Work, $COMPOSER : o:Composer)\n' +
      //'order by $OPERA desc?';

  } else if (exName == "exMecca") { // --------------------------------------
    document.queryform.query.value = 
      '# not possible currently due to missing grouping in toma.';
      //'using o for i"http://psi.ontopedia.net/"\n' +
      //'select $CITY, count($OPERA) from\n' +
      //' instance-of($CITY, o:City),\n' +
      //' { o:premiere($OPERA : o:Work, $CITY : o:Place)\n' +
      //' |\n' +
      //'   o:premiere($OPERA : o:Work, $THEATRE : o:Place),\n' +
      //'   o:located_in($THEATRE : o:Containee, $CITY : o:Container)\n' +
      //' } order by $OPERA desc?';

  } else if (exName == "exTheatresByPremiere") { // -------------------------
    document.queryform.query.value =
      '# not possible currently due to missing grouping in toma.';
      //'using o for i"http://psi.ontopedia.net/"\n' +
      //'select $THEATRE, count($OPERA) from\n' +
      //' instance-of($THEATRE, o:Theatre),\n' +
      //' o:premiere($OPERA : o:Work, $THEATRE : o:Place)\n' +
      //'order by $OPERA desc?';

  } else if (exName == "exOperasByPremiereDate") { // -----------------------
    document.queryform.query.value =
      'select $OPERA, $OPERA.oc(premiere-date).data where \n' +
      '  $OPERA.type = opera \n' +
      '  order by 2 desc limit 20;';

  } else if (exName == "exEnglishTitles") { // ------------------------------
    document.queryform.query.value =
      '# can be further improved with square brackets \n' + 
      'select $OPERA, $OPERA.name@english where \n' +
      '  $OPERA.type = opera and \n' +
      '  exists $OPERA.name@english \n' + 
      '  order by 1;';

  } else if (exName == "exSuicides") { // -----------------------------------
    document.queryform.query.value =
      'select $WORK, $SUICIDE where \n' +
      '  $SUICIDE.(perpetrator)<-(killed-by)->(victim) = $VICTIM and \n' +
      '  $VICTIM = $SUICIDE and \n' +
      '  $SUICIDE.(character)<-(appears-in)->(work) = $WORK \n' +
      'order by 1;';
    
  } else if (exName == "exSettingsByCountry") { // --------------------------
    document.queryform.query.value =
      '# not possible currently due to missing grouping in toma.';
      //"/* Define inference rule to capture nested located-in associations: */\n" +
      //'using o for i"http://psi.ontopedia.net/"\n' +
      //"ext-located-in($CONTAINEE, $CONTAINER) :-\n" +
      //"{\n" +
      //" o:located_in($CONTAINEE : o:Containee, $CONTAINER : o:Container) |\n" +
      //" o:located_in($CONTAINEE : o:Containee, $MID : o:Container),\n" +
      //" ext-located-in($MID, $CONTAINER)\n" +
      //"}.\n\n" +
      //"select $COUNTRY, count($OPERA) from\n" +
      //" instance-of($COUNTRY, o:Country),\n" +
      //" { o:takes_place_in($OPERA : o:Opera, $COUNTRY : o:Place) |\n" +
      //"   o:takes_place_in($OPERA : o:Opera, $PLACE : o:Place),\n" +
      //"   ext-located-in($PLACE, $COUNTRY) }\n" +
      //"order by $OPERA desc?\n" ;

  } else if (exName == "exNaryArias") { // ----------------------------------
    document.queryform.query.value =
      '# not possible currently due to missing grouping in toma.';
      //'using o for i"http://psi.ontopedia.net/"\n' +
      //"select $OPERA, $ARIA, count($CHARACTERS) from\n" +
      //" o:part_of($ARIA : o:Part, $OPERA : o:Whole),\n" +
      //" o:sung_by($CHARACTERS : o:Person, $ARIA : o:Aria),\n" +
      //" o:sung_by($CHARACTER2 : o:Person, $ARIA : o:Aria),\n" +
      //" $CHARACTERS /= $CHARACTER2\n" +
      //"order by $CHARACTERS desc, $OPERA?";

  } else if (exName == "exInspiredBy") { // ---------------------------------
    document.queryform.query.value =
      'select distinct $COMPOSER, $WRITER where \n' +
      '  $COMPOSER = verdi and \n' +
      '  $COMPOSER.(composer)<-(composed-by)->(work) = $OPERA and \n' +
      '  $OPERA.(result)<-(based-on)->(source) = $WORK and \n' +
      '  $WORK.(work)<-(written-by)->(writer) = $WRITER \n' +
      'order by 2;';

  } else if (exName == "exBibliography") { // -------------------------------
    document.queryform.query.value =
      'select $TOPIC, $TOPIC.oc(bibref).data where \n' +
      '  exists $TOPIC.oc(bibref) \n' + 
      'union \n' +
      'select $TOPIC, $REIFIER.oc(bibref) where \n' + 
      '  $a.player = $TOPIC and \n' +
      '  $reifier = $a.reifier and \n' +
      '  exists $reifier.oc(bibref) \n' + 
      'order by 1 desc, 2;';    	

  } else if (exName == "exRecordings") { // ---------------------------------
    document.queryform.query.value =
      'select $COMPOSER, $OPERA, $RECORDING.data where \n' +
      '  $OPERA.(work)<-(composed-by)->(composer) = $COMPOSER and \n' +
      '  $RECORDING = $OPERA.oc(audio-recording) and \n' +
      '  exists $RECORDING \n' +
      '  order by 1, 2, 3;';
      
  } else if (exName == "exNoDramatisPersonae") { // -------------------------
    document.queryform.query.value =
      '# can be improved when square brackets work \n' + 
      'select $COMPOSER, $OPERA where \n' +
      '  $OPERA.type = opera and \n' +
      '  $OPERA.(work)<-(composed-by)->(composer) = $COMPOSER \n' +
      'except \n' +
      'select $COMPOSER, $OPERA where \n' +
      '  $OPERA.type = opera and \n' +
      '  $OPERA.(work)<-(composed-by)->(composer) = $COMPOSER and \n' +
      '  $OPERA.(work)<-(appears-in)->(character) = $CHAR \n' +
      'order by 1, 2;';
    	  
  } else if (exName == "exNoVoiceType") { // --------------------------
    document.queryform.query.value =
      'select $OPERA where \n' + 
      '  $OPERA.type = opera \n' +
      'except \n' +
      'select distinct $OPERA where \n' +
      '  $OPERA.(work)<-(appears-in)->(character) = $CHAR and \n' + 
      '  $CHAR.(character)<-(has-voice)->(voice-type) = $TYPE \n' +
      'order by 1;';
  }
}


