
function insertExample(exName) {

  // ===== ItalianOpera.ltm =================================================

  if (exName == "exPuccini") { // -------------------------------------------
    document.queryform.query.value =
      'using o for i"http://psi.ontopedia.net/"\n' +
      ' o:composed_by($OPERA  : o:Work, o:Puccini : o:Composer)?';

  } else if (exName == "exPucciniSorted") { // ------------------------------
    document.queryform.query.value =
      'using o for i"http://psi.ontopedia.net/"\n' +
      ' o:composed_by($OPERA  : o:Work, o:Puccini : o:Composer)\n' +
      'order by $OPERA?';

  } else if (exName == "exShakespeare") { // --------------------------------
    document.queryform.query.value =
      'using o for i"http://psi.ontopedia.net/"\n' +
      ' o:composed_by($OPERA : o:Work, $COMPOSER : o:Composer),\n' +
      ' o:based_on($OPERA : o:Result, $WORK : o:Source),\n' +
      ' o:written_by($WORK : o:Work, o:Shakespeare : o:Writer)\n' +
      'order by $COMPOSER?';

  } else if (exName == "exBornDied") { // -----------------------------------
    document.queryform.query.value =
      'select $PLACE, $PERSON from\n' +
      'i"http://psi.ontopedia.net/born_in"(\n' +
      '  $PERSON : i"http://psi.ontopedia.net/Person",\n' +
      '  $PLACE : i"http://psi.ontopedia.net/Place"\n' +
      '),\n' +
      'i"http://psi.ontopedia.net/died_in"(\n' +
      '  $PERSON : i"http://psi.ontopedia.net/Person",\n' +
      '  $PLACE : i"http://psi.ontopedia.net/Place"\n' +
      ')\n' +
      'order by $PLACE, $PERSON?\n' +
      '\n' +
      '/* The query is easier to read when prefixes are used: */\n' +
      '\n' +
      'using o for i"http://psi.ontopedia.net/"\n' +
      'select $PLACE, $PERSON from\n' +
      ' o:born_in( $PERSON : o:Person, $PLACE : o:Place ),\n' +
      ' o:died_in( $PERSON : o:Person, $PLACE : o:Place )\n' +
      'order by $PLACE, $PERSON?';

  } else if (exName == "exComposers") { // ----------------------------------
    document.queryform.query.value =
      'using o for i"http://psi.ontopedia.net/"\n' +
      'select $COMPOSER, count($OPERA) from\n' +
      ' o:composed_by($OPERA : o:Work, $COMPOSER : o:Composer)\n' +
      'order by $OPERA desc?';

  } else if (exName == "exMecca") { // --------------------------------------
    document.queryform.query.value =
      'using o for i"http://psi.ontopedia.net/"\n' +
      'select $CITY, count($OPERA) from\n' +
      ' instance-of($CITY, o:City),\n' +
      ' { o:premiere($OPERA : o:Work, $CITY : o:Place)\n' +
      ' |\n' +
      '   o:premiere($OPERA : o:Work, $THEATRE : o:Place),\n' +
      '   o:located_in($THEATRE : o:Containee, $CITY : o:Container)\n' +
      ' } order by $OPERA desc?';

  } else if (exName == "exTheatresByPremiere") { // -------------------------
    document.queryform.query.value =
      'using o for i"http://psi.ontopedia.net/"\n' +
      'select $THEATRE, count($OPERA) from\n' +
      ' instance-of($THEATRE, o:Theatre),\n' +
      ' o:premiere($OPERA : o:Work, $THEATRE : o:Place)\n' +
      'order by $OPERA desc?';

  } else if (exName == "exOperasByPremiereDate") { // -----------------------
    document.queryform.query.value =
      'using o for i"http://psi.ontopedia.net/"\n' +
      'select $OPERA, $PREMIERE-DATE from\n' +
      ' instance-of($OPERA, o:Opera),\n' +
      ' o:premiere_date($OPERA, $PREMIERE-DATE)\n' +
      'order by $PREMIERE-DATE desc\n' +
      'limit 20?';

  } else if (exName == "exEnglishTitles") { // ------------------------------
    document.queryform.query.value =
      'using o for i"http://psi.ontopedia.net/"\n' +
      'using lang for i"http://www.topicmaps.org/xtm/1.0/language.xtm#"\n' +
      'select $OPERA, $ENGLISH-TITLE from\n' +
      '  instance-of($OPERA, o:Opera),\n' +
      '  topic-name($OPERA, $NAME),\n' +
      '  value($NAME, $ENGLISH-TITLE),\n' +
      '  scope($NAME, lang:en)\n' +
      'order by $OPERA?';

  } else if (exName == "exSuicides") { // -----------------------------------
    document.queryform.query.value =
      '/* NB Result set is incomplete due to missing data */\n' +
      'using o for i"http://psi.ontopedia.net/"\n' +
      'select $WORK , $SUICIDE from\n' +
      '  o:appears_in($SUICIDE : o:Character, $WORK : o:Work),\n' +
      '  o:killed_by($SUICIDE : o:Victim, $SUICIDE : o:Perpetrator)\n' +
      'order by $WORK?';

  } else if (exName == "exSettingsByCountry") { // --------------------------
    document.queryform.query.value =
      "/* Define inference rule to capture nested located-in associations: */\n" +
      'using o for i"http://psi.ontopedia.net/"\n' +
      "ext-located-in($CONTAINEE, $CONTAINER) :-\n" +
      "{\n" +
      " o:located_in($CONTAINEE : o:Containee, $CONTAINER : o:Container) |\n" +
      " o:located_in($CONTAINEE : o:Containee, $MID : o:Container),\n" +
      " ext-located-in($MID, $CONTAINER)\n" +
      "}.\n\n" +
      "select $COUNTRY, count($OPERA) from\n" +
      " instance-of($COUNTRY, o:Country),\n" +
      " { o:takes_place_in($OPERA : o:Opera, $COUNTRY : o:Place) |\n" +
      "   o:takes_place_in($OPERA : o:Opera, $PLACE : o:Place),\n" +
      "   ext-located-in($PLACE, $COUNTRY) }\n" +
      "order by $OPERA desc?\n" ;

  } else if (exName == "exNaryArias") { // ----------------------------------
    document.queryform.query.value =
      'using o for i"http://psi.ontopedia.net/"\n' +
      "select $OPERA, $ARIA, count($CHARACTERS) from\n" +
      " o:part_of($ARIA : o:Part, $OPERA : o:Whole),\n" +
      " o:sung_by($CHARACTERS : o:Person, $ARIA : o:Aria),\n" +
      " o:sung_by($CHARACTER2 : o:Person, $ARIA : o:Aria),\n" +
      " $CHARACTERS /= $CHARACTER2\n" +
      "order by $CHARACTERS desc, $OPERA?";

  } else if (exName == "exInspiredBy") { // ---------------------------------
    document.queryform.query.value =
      "/* First define inference rule and then use it in query:*/\n\n" +
      'using o for i"http://psi.ontopedia.net/"\n' +
      "inspired-by($COMPOSER, $WRITER) :-\n" +
      ' o:composed_by($OPERA : o:Work, $COMPOSER : o:Composer),\n' +
      ' o:based_on($OPERA : o:Result, $WORK : o:Source),\n' +
      ' o:written_by($WORK : o:Work, $WRITER : o:Writer).\n\n' +
      "inspired-by(o:Giuseppe_Verdi, $WHO)\n" +
      "order by $WHO?\n\n" +
      "------------------------------------------------------------\n\n" +
      "Anything after ? is ignored by the query processor, so it's\n" +
      "safe to write stuff here... Try the following query and\n" +
      "compare it with the query 'Composers inspired by Shakespeare'.\n" +
      "(You'll have to copy the next two lines and use them to\n" +
      "replace the two line query above.)\n\n" +
      "inspired-by($WHO, o:Shakespeare)\n" +
      "order by $WHO?\n";

  } else if (exName == "exBibliography") { // -------------------------------
    document.queryform.query.value =
      'using o for i"http://psi.ontopedia.net/"\n' +
      "select $TOPIC, $BIBREF from\n" +
      "{\n" +
      "  o:bibref($TOPIC, $BIBREF)\n" +
      "|\n" +
      "  role-player($ROLE, $TOPIC),\n" +
      "  association-role($ASSOC, $ROLE),\n" +
      "  reifies($REIFIER, $ASSOC),\n" +
      "  o:bibref($REIFIER, $BIBREF)\n" +
      "}\n" +
      "order by $TOPIC desc, $BIBREF?\n\n" +
      "--------------------------------------------------------------\n\n" +
      "This query caters both for bibliographic references that are\n" +
      "attached directly to the topic and for those that are attached\n" +
      "to (reified) associations in which the topic plays a role.\n\n" +
      'For example, a book on the relationship between Tosca and Rome\n' +
      'will also be found under "Tosca", even though it is not an\n' +
      'occurrence of the topic "Tosca". (It is actually an occurrence\n' +
      'of the association between "Tosca" and "Rome".)\n';

  } else if (exName == "exRecordings") { // ---------------------------------
    document.queryform.query.value =
      'using o for i"http://psi.ontopedia.net/"\n' +
      'select $COMPOSER, $OPERA, $RECORDING from\n' +
      '  o:audio_recording($OPERA, $RECORDING),\n' +
      '  o:composed_by($OPERA : o:Work, $COMPOSER : o:Composer)\n' +
      'order by $COMPOSER, $OPERA, $RECORDING?\n';

  } else if (exName == "exNoDramatisPersonae") { // -------------------------
    document.queryform.query.value =
      'using o for i"http://psi.ontopedia.net/"\n' +
      'select $COMPOSER, $OPERA from\n' +
      ' instance-of($OPERA, o:Opera),\n' +
      ' o:composed_by($OPERA : o:Work, $COMPOSER : o:Composer),\n' +
      ' not( o:appears_in( $CHAR : o:Character, $OPERA : o:Work ) )\n' +
      'order by $COMPOSER, $OPERA?';

  } else if (exName == "exNoVoiceType") { // --------------------------
    document.queryform.query.value =
      'using o for i"http://psi.ontopedia.net/"\n' +
      'select $OPERA from\n' +
      ' o:appears_in($character : o:Character, $OPERA : o:Work),\n' +
      ' not(o:has_voice($character : o:Character, $voice-type : o:Voice_type))\n' +
      'order by $OPERA?';

  // ===== factbook.ltm ======================================================

  // it's all like the queries on the other topic maps

  // ===== i18n.ltm ==========================================================

  } else if (exName == "exTypeSummary") { // ---------------------------------
    document.queryform.query.value =
      "select $TYPE, count($SCRIPT), $CATEGORY from\n" +
      "subclass-of(script : superclass, $TYPE : subclass),\n" +
      "direct-instance-of($SCRIPT, $TYPE),\n" +
      "belongs-to($SCRIPT : containee, $CATEGORY : container)\n" +
      "order by $TYPE?";

  } else if (exName == "exFamilies") { // ------------------------------------
    document.queryform.query.value =
      "select $FAMILY, count($SCRIPT) from\n" +
      "instance-of($FAMILY, script-family),\n" +
      "belongs-to($SCRIPT : containee, $FAMILY : container)\n" +
      "order by $SCRIPT desc?";

  } else if (exName == "exScripts") { // -------------------------------------
    document.queryform.query.value =
      "select $SCRIPT, count($LANGUAGE) from\n" +
      "instance-of($LANGUAGE, language),\n" +
      "written-in($LANGUAGE : language, $SCRIPT : script)\n" +
      "order by $LANGUAGE desc?";

  // ===== jill.xtm ==========================================================

  } else if (exName == "exOccurrenceTopics") { // ----------------------------
    document.queryform.query.value =
      '/* This query finds every topic whose subject\n' +
      '   is an occurrence of some (other) topic */\n' +
      "select $URL, $LOCATOR-OF, $OCCURRENCE-OF from\n" +
      " subject-locator($LOCATOR-OF, $URL),\n" +
      " resource($OCC, $URL),\n" +
      " occurrence($OCCURRENCE-OF, $OCC)\n" +
      "order by $URL?\n";

  // ===== TapsaConcerts.xtm =================================================

  } else if (exName == "exBComposers") { // ----------------------------
    document.queryform.query.value =
      '/* tolog query that returns all composers whose names\n' +
      '   begin with the letter "B" along with their works */\n\n' +
      'import "http://psi.ontopia.net/tolog/string/" as s\n' +
      'using o for i"http://psi.ontopedia.net/"\n' +
      ' select $ComposerName, $WorkTitle from\n' +
      '  instance-of($Composer, o:Composer),\n' +
      '  o:composed_by($WorkTitle : o:Musical_work,\n' +
      '                $Composer : o:Composer),\n' +
      '  topic-name($Composer, $name),\n' +
      '  not(type($name, $type)),\n' +
      '  value($name, $ComposerName),\n' +
      '  s:starts-with($ComposerName, "B")\n' +
      '  order by $ComposerName, $WorkTitle?\n';

  } else if (exName == "exConcertsByDate") { // ----------------------------
    document.queryform.query.value =
      '/* list concerts (and other musical events) by date */\n\n' +
      'using dc for i"http://purl.org/dc/elements/1.1/"\n' +
      'using o for i"http://psi.ontopedia.net/"\n' +
      'select $DATE, $EVENT from\n' +
      'instance-of($EVENT, o:Musical_event),\n' +
      'dc:date($EVENT, $DATE)\n' +
      'order by $DATE desc?\n';

  // ===== xmltools-tm.xtm ===================================================

  } else if (exName == "exStdUsed") { // -------------------------------------
    document.queryform.query.value =
      "select $STD, count($PRODUCT) from\n" +
      "TMAT_StandardsUse($STD : TMAR_UsedIn, $PRODUCT : TMAR_UsedBy)\n" +
      "order by $PRODUCT desc?";
  }
}


