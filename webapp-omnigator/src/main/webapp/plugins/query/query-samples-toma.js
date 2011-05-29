
function insertExample(exName) {

  // ===== ItalianOpera.ltm =================================================

  if (exName == "exPuccini") { // -------------------------------------------
    document.queryform.query.value =
      'select $OPERA where \n' +
      '  exists puccini.(composer)<-(composed-by)->(work)[$OPERA];';

  } else if (exName == "exPucciniSorted") { // ------------------------------
    document.queryform.query.value =
      'select $OPERA where \n' +
      '  exists puccini.(composer)<-(composed-by)->(work)[$OPERA] \n' +
      '  order by 1;';

  } else if (exName == "exShakespeare") { // --------------------------------
    document.queryform.query.value =
      'select $OPERA, $COMPOSER, $WORK where \n' +
      '  exists shakespeare.(writer)<-(written-by)->(work)[$WORK] \n' + 
      '  .(source)<-(based-on)->(result)[$OPERA] \n' + 
      '  .(work)<-(composed-by)->(composer)[$COMPOSER] \n' +
      'order by 2;';

  } else if (exName == "exBornDied") { // -----------------------------------
    document.queryform.query.value =
      'select $PLACE, $PERSON where \n' +
      '  exists $PERSON.(person)<-(born-in)->(place)[$PLACE] and \n' +
      '  $PERSON.(person)<-(died-in)->(place) = $PLACE \n' +
      '  order by 1, 2;';

  } else if (exName == "exComposers") { // ----------------------------------
    document.queryform.query.value = 
      'select $COMPOSER, count($OPERA) where \n' +
      '  exists $COMPOSER.(composer)<-(composed-by)->(work)[$OPERA] \n' +
      '  order by 2 desc;';

  } else if (exName == "exMecca") { // --------------------------------------
    document.queryform.query.value = 
      '# incomplete query\n' +
      'select $CITY, count($OPERA) where $CITY.type = city and \n' +
      '  exists $OPERA.(work)<-(premiere)->(place).\n' +
      '                (containee)<-(located-in)->(container)[$CITY] \n' + 
      'order by 2 desc;';

  } else if (exName == "exTheatresByPremiere") { // -------------------------
    document.queryform.query.value =
      'select $THEATRE, count($OPERA) where \n' +
      '  $THEATRE.type = theatre and \n' +
      '  exists $THEATRE.(place)<-(premiere)->(work)[$OPERA] \n' + 
      'order by 2 desc;';

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
      '  exists $SUICIDE.(perpetrator)<-(killed-by)->(victim)[$VICTIM] \n' +
      '  .(character)<-(appears-in)->(work)[$WORK] and \n' +
      '  $VICTIM = $SUICIDE \n' +
      'order by 1;';
    
  } else if (exName == "exSettingsByCountry") { // --------------------------
    document.queryform.query.value =
      '# not translated to toma yet.';
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
      'select $OPERA, $ARIA, count($CHARACTERS) where \n' +
      '  exists $OPERA.(whole)<-(part-of)->(part)[$ARIA] and \n' +
      '  exists $ARIA.(aria)<-(sung-by)->(person)[$CHARACTERS] and \n' +
      '  exists $ARIA.(aria)<-(sung-by)->(person)[$CHARACTER2] and \n' +
      '  $CHARACTERS != $CHARACTER2 \n' +
      'order by 3 desc, 1;';

  } else if (exName == "exInspiredBy") { // ---------------------------------
    document.queryform.query.value =
      'select distinct $COMPOSER, $WRITER where \n' +
      '  $COMPOSER = verdi and \n' +
      '  exists $COMPOSER.(composer)<-(composed-by)->(work)[$OPERA] \n' +
      '  .(result)<-(based-on)->(source)[$WORK] \n' +
      '  .(work)<-(written-by)->(writer)[$WRITER] \n' +
      'order by 2;';

  } else if (exName == "exBibliography") { // -------------------------------
    document.queryform.query.value =
      'select $TOPIC, $TOPIC.oc(bibref).data where \n' +
      '  exists $TOPIC.oc(bibref) \n' + 
      'union \n' +
      'select $TOPIC, $REIFIER.oc(bibref).data where \n' + 
      '  $a.player = $TOPIC and \n' +
      '  $reifier = $a.reifier and \n' +
      '  exists $reifier.oc(bibref) \n' + 
      'order by 1 desc, 2;';    	

  } else if (exName == "exRecordings") { // ---------------------------------
    document.queryform.query.value =
      'select $COMPOSER, $OPERA, $RECORDING.data where \n' +
      '  exists $OPERA.(work)<-(composed-by)->(composer)[$COMPOSER] \n' +
      '  and \n' +
      '  exists $OPERA.oc(audio-recording)[$RECORDING] \n' +
      '  order by 1, 2, 3;';
      
  } else if (exName == "exNoDramatisPersonae") { // -------------------------
    document.queryform.query.value =
      '# can be improved when square brackets work \n' + 
      'select $COMPOSER, $OPERA where \n' +
      '  exists opera.instance[$OPERA] \n' +
      '  .(work)<-(composed-by)->(composer)[$COMPOSER] \n' +
      'except \n' +
      'select $COMPOSER, $OPERA where \n' +
      '  exists $COMPOSER.(composer)<-(composed-by)->(work)[$OPERA] \n' +
      '  .(work)<-(appears-in)->(character)[$CHAR] \n' +
      'order by 1, 2;';
    	  
  } else if (exName == "exNoVoiceType") { // --------------------------
    document.queryform.query.value =
      'select $OPERA where \n' + 
      '  $OPERA.type = opera \n' +
      'except \n' +
      'select distinct $OPERA where \n' +
      '  exists $OPERA.(work)<-(appears-in)->(character)[$CHAR] and \n' + 
      '  exists $CHAR.(character)<-(has-voice)->(voice-type)[$TYPE] \n' +
      'order by 1;';
  }
}


