
function insertExample(exName) {

  // ===== opera.hytm ========================================================

  if (exName == "exPuccini") { // --------------------------------------------
    document.queryform.query.value =
      "composed-by($OPERA : work, puccini : composer)?";

  } else if (exName == "exPucciniSorted") { // -------------------------------
    document.queryform.query.value =
      "composed-by($OPERA : work, puccini : composer)\n" +
      "order by $OPERA?";

  } else if (exName == "exShakespeare") { // ---------------------------------
    document.queryform.query.value =
      "composed-by($OPERA : work, $COMPOSER : composer),\n" +
      "based-on($OPERA : result, $WORK : source),\n" +
      "written-by($WORK : work, shakespeare : writer)?";

  } else if (exName == "exBornDied") { // ------------------------------------
    document.queryform.query.value =
      'select $PLACE, $PERSON from\n' +
      " born-in($PERSON : person, $PLACE : place),\n" +
      " died-in($PERSON : person, $PLACE : place)\n" +
      'order by $PLACE, $PERSON?';

  } else if (exName == "exComposers") { // -----------------------------------
    document.queryform.query.value =
      "select $COMPOSER, count($OPERA) from\n" +
      " composed-by($OPERA : work, $COMPOSER : composer)\n" +
      "order by $OPERA desc?";

  } else if (exName == "exMecca") { // ---------------------------------------
    document.queryform.query.value =
      "select $CITY, count($OPERA) from\n" +
      "instance-of($CITY, city),\n" +
      "{ premiere($OPERA : work, $CITY : place) |\n" +
      "  premiere($OPERA : work, $THEATRE : place),\n" +
      "  located-in($THEATRE : containee, $CITY : container) }\n" +
      "order by $OPERA desc?";

  } else if (exName == "exOperasByPremiereDate") { // ------------------------
    document.queryform.query.value =
      "select $OPERA, $PREMIERE-DATE from\n" +
      " instance-of($OPERA, opera),\n" +
      " premiere-date($OPERA, $PREMIERE-DATE)\n" +
      "order by $PREMIERE-DATE desc\n" +
      "limit 20?";

  } else if (exName == "exEnglishTitles") { // -------------------------------
    document.queryform.query.value =
      "select $OPERA, $ENGLISH-TITLE from\n" +
      " instance-of($OPERA, opera),\n" +
      " topic-name($OPERA, $NAME),\n" +
      " value($NAME, $ENGLISH-TITLE),\n" +
      " scope($NAME, english)?";

  } else if (exName == "exSuicides") { // ------------------------------------
    document.queryform.query.value =
      '/* NB Result set is incomplete due to missing data */\n' +
      'killed-by($SUICIDE : victim, $SUICIDE : perpetrator)\n' +
      'order by $SUICIDE?';

  } else if (exName == "exNoDramatisPersonae") { // --------------------------
    document.queryform.query.value =
      'select $COMPOSER, $OPERA from\n' +
      ' instance-of($OPERA, opera),\n' +
      ' composed-by($OPERA : work, $COMPOSER : composer),\n' +
      ' not( appears-in( $CHARACTER : character, $OPERA : work ) )\n' +
      'order by $COMPOSER, $OPERA?';

  } else if (exName == "exSettingsByCountry") { // ---------------------------
    document.queryform.query.value =
      "/* Define inference rule to capture nested located-in associations: */\n" +
      "ext-located-in($CONTAINEE, $CONTAINER) :-\n" +
      "{\n" +
      " located-in($CONTAINEE : containee, $CONTAINER : container) |\n" +
      " located-in($CONTAINEE : containee, $MID : container),\n" +
      " ext-located-in($MID, $CONTAINER)\n" +
      "}.\n\n" +
      "select $COUNTRY, count($OPERA) from\n" +
      " instance-of($COUNTRY, country),\n" +
      " { takes-place-in($OPERA : opera, $COUNTRY : place) |\n" +
      "   takes-place-in($OPERA : opera, $PLACE : place),\n" +
      "   ext-located-in($PLACE, $COUNTRY) }\n" +
      "order by $OPERA desc?\n" ;

  } else if (exName == "exNaryArias") { // -----------------------------------
    document.queryform.query.value =
      "select $OPERA, $ARIA, count($CHARACTERS) from\n" +
      " part-of($ARIA : part, $OPERA : whole),\n" +
      " sung-by($CHARACTERS : person, $ARIA : aria),\n" +
      " sung-by($CHARACTER2 : person, $ARIA : aria),\n" +
      " $CHARACTERS /= $CHARACTER2\n" +
      "order by $CHARACTERS desc, $OPERA?";

  } else if (exName == "exInspiredBy") { // ----------------------------------
    document.queryform.query.value =
      "/* First define inference rule and then use it in query: */\n\n" +
      "inspired-by($COMPOSER, $WRITER) :-\n" +
      " composed-by($OPERA : work, $COMPOSER : composer),\n" +
      " based-on($OPERA : result, $WORK : source),\n" +
      " written-by($WORK : work, $WRITER : writer).\n\n" +
      "inspired-by(verdi, $WHO)\n" +
      "order by $WHO?\n\n" +
      "------------------------------------------------------------\n\n" +
      "Anything after ? is ignored by the query processor, so it's\n" +
      "safe to write stuff here... Try the following query and\n" +
      "compare it with the query 'Composers inspired by Shakespeare'.\n" +
      "(You'll have to copy the next two lines and use them to\n" +
      "replace the two line query above.)\n\n" +
      "inspired-by($WHO, shakespeare)\n" +
      "order by $WHO?\n\n";

  } else if (exName == "exBibliography") { // --------------------------------
    document.queryform.query.value =
      'using op for i"http://psi.ontopia.net/opera/"\n' +
      "select $TOPIC, $BIBREF from\n" +
      "{\n" +
      "  op:bibref($TOPIC, $BIBREF)\n" +
      "|\n" +
      "  role-player($ROLE, $TOPIC),\n" +
      "  association-role($ASSOC, $ROLE),\n" +
      "  reifies($REIFIER, $ASSOC),\n" +
      "  op:bibref($REIFIER, $BIBREF)\n" +
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

  } else if (exName == "exRecordings") { // ----------------------------------
    document.queryform.query.value =
      'select $COMPOSER, $OPERA, $RECORDING from\n' +
      '  audio-recording($OPERA, $RECORDING),\n' +
      '  composed-by($OPERA : work, $COMPOSER : composer)\n' +
      'order by $COMPOSER, $OPERA, $RECORDING?\n';

  // ===== opera.ltm ========================================================

  } else if (exName == "exPuccini3") { // --------------------------------------------
    document.queryform.query.value =
      "composed-by($OPERA : work, puccini : composer)?";

  } else if (exName == "exPucciniSorted3") { // -------------------------------
    document.queryform.query.value =
      "composed-by($OPERA : work, puccini : composer)\n" +
      "order by $OPERA?";

  } else if (exName == "exShakespeare3") { // ---------------------------------
    document.queryform.query.value =
      "composed-by($OPERA : work, $COMPOSER : composer),\n" +
      "based-on($OPERA : result, $WORK : source),\n" +
      "written-by($WORK : work, shakespeare : writer)?";

  } else if (exName == "exBornDied3") { // ------------------------------------
    document.queryform.query.value =
      'select $PLACE, $PERSON from\n' +
      " born-in($PERSON : person, $PLACE : place),\n" +
      " died-in($PERSON : person, $PLACE : place)\n" +
      'order by $PLACE, $PERSON?';

  } else if (exName == "exComposers3") { // -----------------------------------
    document.queryform.query.value =
      "select $COMPOSER, count($OPERA) from\n" +
      " composed-by($OPERA : work, $COMPOSER : composer)\n" +
      "order by $OPERA desc?";

  } else if (exName == "exMecca3") { // ---------------------------------------
    document.queryform.query.value =
      "select $CITY, count($OPERA) from\n" +
      "instance-of($CITY, city),\n" +
      "{ premiere($OPERA : work, $CITY : place) |\n" +
      "  premiere($OPERA : work, $THEATRE : place),\n" +
      "  located-in($THEATRE : containee, $CITY : container) }\n" +
      "order by $OPERA desc?";

  } else if (exName == "exOperasByPremiereDate3") { // ------------------------
    document.queryform.query.value =
      "select $OPERA, $PREMIERE-DATE from\n" +
      " instance-of($OPERA, opera),\n" +
      " premiere-date($OPERA, $PREMIERE-DATE)\n" +
      "order by $PREMIERE-DATE desc\n" +
      "limit 20?";

  } else if (exName == "exEnglishTitles3") { // -------------------------------
    document.queryform.query.value =
      "select $OPERA, $ENGLISH-TITLE from\n" +
      " instance-of($OPERA, opera),\n" +
      " topic-name($OPERA, $NAME),\n" +
      " value($NAME, $ENGLISH-TITLE),\n" +
      " scope($NAME, english)?";

  } else if (exName == "exSuicides3") { // ------------------------------------
    document.queryform.query.value =
      '/* NB Result set is incomplete due to missing data */\n' +
      'killed-by($SUICIDE : victim, $SUICIDE : perpetrator)\n' +
      'order by $SUICIDE?';

  } else if (exName == "exSettingsByCountry3") { // ---------------------------
    document.queryform.query.value =
      "/* Define inference rule to capture nested located-in associations: */\n" +
      "ext-located-in($CONTAINEE, $CONTAINER) :-\n" +
      "{\n" +
      " located-in($CONTAINEE : containee, $CONTAINER : container) |\n" +
      " located-in($CONTAINEE : containee, $MID : container),\n" +
      " ext-located-in($MID, $CONTAINER)\n" +
      "}.\n\n" +
      "select $COUNTRY, count($OPERA) from\n" +
      " instance-of($COUNTRY, country),\n" +
      " { takes-place-in($OPERA : opera, $COUNTRY : place) |\n" +
      "   takes-place-in($OPERA : opera, $PLACE : place),\n" +
      "   ext-located-in($PLACE, $COUNTRY) }\n" +
      "order by $OPERA desc?\n" ;

  } else if (exName == "exNaryArias3") { // -----------------------------------
    document.queryform.query.value =
      "select $OPERA, $ARIA, count($CHARACTERS) from\n" +
      " part-of($ARIA : part, $OPERA : whole),\n" +
      " sung-by($CHARACTERS : person, $ARIA : aria),\n" +
      " sung-by($CHARACTER2 : person, $ARIA : aria),\n" +
      " $CHARACTERS /= $CHARACTER2\n" +
      "order by $CHARACTERS desc, $OPERA?";

  } else if (exName == "exInspiredBy3") { // ----------------------------------
    document.queryform.query.value =
      "/* First define inference rule and then use it in query: */\n\n" +
      "inspired-by($COMPOSER, $WRITER) :-\n" +
      " composed-by($OPERA : work, $COMPOSER : composer),\n" +
      " based-on($OPERA : result, $WORK : source),\n" +
      " written-by($WORK : work, $WRITER : writer).\n\n" +
      "inspired-by(verdi, $WHO)\n" +
      "order by $WHO?\n\n" +
      "------------------------------------------------------------\n\n" +
      "Anything after ? is ignored by the query processor, so it's\n" +
      "safe to write stuff here... Try the following query and\n" +
      "compare it with the query 'Composers inspired by Shakespeare'.\n" +
      "(You'll have to copy the next two lines and use them to\n" +
      "replace the two line query above.)\n\n" +
      "inspired-by($WHO, shakespeare)\n" +
      "order by $WHO?\n\n";

  } else if (exName == "exBibliography3") { // --------------------------------
    document.queryform.query.value =
      'using op for i"http://psi.ontopia.net/opera/"\n' +
      "select $TOPIC, $BIBREF from\n" +
      "{\n" +
      "  op:bibref($TOPIC, $BIBREF)\n" +
      "|\n" +
      "  role-player($ROLE, $TOPIC),\n" +
      "  association-role($ASSOC, $ROLE),\n" +
      "  reifies($REIFIER, $ASSOC),\n" +
      "  op:bibref($REIFIER, $BIBREF)\n" +
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

  } else if (exName == "exRecordings3") { // ----------------------------------
    document.queryform.query.value =
      'select $COMPOSER, $OPERA, $RECORDING from\n' +
      '  audio-recording($OPERA, $RECORDING),\n' +
      '  composed-by($OPERA : work, $COMPOSER : composer)\n' +
      'order by $COMPOSER, $OPERA, $RECORDING?\n';

  } else if (exName == "exNoDramatisPersonae3") { // --------------------------
    document.queryform.query.value =
      'select $COMPOSER, $OPERA from\n' +
      ' instance-of($OPERA, opera),\n' +
      ' composed-by($OPERA : work, $COMPOSER : composer),\n' +
      ' not( appears-in( $CHARACTER : character, $OPERA : work ) )\n' +
      'order by $COMPOSER, $OPERA?';

  } else if (exName == "exNoVoiceType3") { // --------------------------
    document.queryform.query.value =
      'select $OPERA from\n' +
      ' appears-in($character : character, $OPERA : work),\n' +
      ' not(has-voice($character : character, $voice-type : voice-type))\n' +
      'order by $OPERA?';

  // ===== opera.xtm =========================================================

  } else if (exName == "exPuccini2") { // ------------------------------------
    document.queryform.query.value =
      'using op for i"http://psi.ontopia.net/music/"\n' +
      'using lit for i"http://psi.ontopia.net/literature/"\n' +
      ' op:composed-by($OPERA  : lit:work, puccini : op:composer)?';

  } else if (exName == "exPucciniSorted2") { // ------------------------------
    document.queryform.query.value =
      'using op for i"http://psi.ontopia.net/music/"\n' +
      'using lit for i"http://psi.ontopia.net/literature/"\n' +
      ' op:composed-by($OPERA  : lit:work, puccini : op:composer)\n' +
      'order by $OPERA?';

  } else if (exName == "exShakespeare2") { // --------------------------------
    document.queryform.query.value =
      'using m for i"http://psi.ontopia.net/music/"\n' +
      'using op for i"http://psi.ontopia.net/opera/"\n' +
      'using lit for i"http://psi.ontopia.net/literature/"\n' +
      ' m:composed-by($OPERA : lit:work, $COMPOSER : m:composer),\n' +
      ' op:based-on($OPERA : op:result, $WORK : op:source),\n' +
      ' lit:written-by($WORK : lit:work, shakespeare : lit:writer)?';

  } else if (exName == "exBornDied2") { // -----------------------------------
    document.queryform.query.value =
      'select $PLACE, $PERSON from\n' +
      'i"http://psi.ontopia.net/biography/born-in"(\n' +
      '  $PERSON : i"http://psi.ontopia.net/person",\n' +
      '  $PLACE : i"http://psi.ontopia.net/geography/place"\n' +
      '),\n' +
      'i"http://psi.ontopia.net/biography/died-in"(\n' +
      '  $PERSON : i"http://psi.ontopia.net/person",\n' +
      '  $PLACE : i"http://psi.ontopia.net/geography/place"\n' +
      ')\n' +
      'order by $PLACE, $PERSON?\n' +
      '\n' +
      '/* Query easier to read when prefixes are used: */\n' +
      'using ont for i"http://psi.ontopia.net/"\n' +
      'using bio for i"http://psi.ontopia.net/biography/"\n' +
      'using geo for i"http://psi.ontopia.net/geography/"\n' +
      'select $PLACE, $PERSON from\n' +
      'bio:born-in( $PERSON : ont:person, $PLACE : geo:place ),\n' +
      'bio:died-in( $PERSON : ont:person, $PLACE : geo:place )\n' +
      'order by $PLACE, $PERSON?';

  } else if (exName == "exComposers2") { // ----------------------------------
    document.queryform.query.value =
      'using m for i"http://psi.ontopia.net/music/"\n' +
      'using l for i"http://psi.ontopia.net/literature/"\n' +
      'select $COMPOSER, count($OPERA) from\n' +
      ' m:composed-by($OPERA : l:work, $COMPOSER : m:composer)\n' +
      'order by $OPERA desc?';

  } else if (exName == "exMecca2") { // --------------------------------------
    document.queryform.query.value =
      'using m for i"http://psi.ontopia.net/music/"\n' +
      'using l for i"http://psi.ontopia.net/literature/"\n' +
      'using op for i"http://psi.ontopia.net/opera/"\n' +
      'using geo for i"http://psi.ontopia.net/geography/"\n' +
      'select $CITY, count($OPERA) from\n' +
      ' instance-of($CITY, geo:city),\n' +
      ' { op:premiere($OPERA : l:work, $CITY : geo:place)\n' +
      ' |\n' +
      '   op:premiere($OPERA : l:work, $THEATRE : geo:place),\n' +
      '   geo:located-in($THEATRE : geo:containee, $CITY : geo:container)\n' +
      ' } order by $OPERA desc?';

  } else if (exName == "exOperasByPremiereDate2") { // -----------------------
    document.queryform.query.value =
      'select $OPERA, $PREMIERE-DATE from\n' +
      ' instance-of($OPERA, i"http://psi.ontopia.net/music/opera"),\n' +
      ' i"http://psi.ontopia.net/opera/premiere-date"($OPERA, $PREMIERE-DATE)\n' +
      'order by $PREMIERE-DATE desc\n' +
      'limit 20?';

  } else if (exName == "exEnglishTitles2") { // ------------------------------
    document.queryform.query.value =
      'select $OPERA, $ENGLISH-TITLE from\n' +
      '  instance-of($OPERA, i"http://psi.ontopia.net/music/opera"),\n' +
      '  topic-name($OPERA, $NAME),\n' +
      '  value($NAME, $ENGLISH-TITLE),\n' +
      '  scope($NAME, i"http://www.topicmaps.org/xtm/1.0/language.xtm#en")?';

  } else if (exName == "exSuicides2") { // -----------------------------------
    document.queryform.query.value =
      '/* NB Result set is incomplete due to missing data */\n' +
      'using o for s"opera-template.xtmp#"\n' +
      'select $WORK , $SUICIDE from\n' +
      '  o:appears-in($SUICIDE : o:character, $WORK : o:work),\n' +
      '  o:killed-by($SUICIDE : o:victim, $SUICIDE : o:perpetrator)\n' +
      'order by $WORK?';

  } else if (exName == "exNoDramatisPersonae2") { // -------------------------
    document.queryform.query.value =
      'using lit for i"http://psi.ontopia.net/literature/"\n' +
      'using mus for i"http://psi.ontopia.net/music/"\n' +
      'select $COMPOSER, $OPERA from\n' +
      ' instance-of($OPERA, mus:opera),\n' +
      ' mus:composed-by($OPERA : lit:work, $COMPOSER : mus:composer),\n' +
      ' not( lit:appears-in( $CHAR : lit:character, $OPERA : lit:work ) )\n' +
      'order by $COMPOSER, $OPERA?';

  } else if (exName == "exSettingsByCountry2") { // --------------------------
    document.queryform.query.value =
      "/* Define inference rule to capture nested located-in associations: */\n" +
      'using g for i"http://psi.ontopia.net/geography/"\n' +
      'using m for i"http://psi.ontopia.net/music/"\n' +
      'using o for i"http://psi.ontopia.net/opera/"\n' +
      "ext-located-in($CONTAINEE, $CONTAINER) :-\n" +
      "{\n" +
      " g:located-in($CONTAINEE : g:containee, $CONTAINER : g:container) |\n" +
      " g:located-in($CONTAINEE : g:containee, $MID : g:container),\n" +
      " ext-located-in($MID, $CONTAINER)\n" +
      "}.\n\n" +
      "select $COUNTRY, count($OPERA) from\n" +
      " instance-of($COUNTRY, g:country),\n" +
      " { o:takes-place-in($OPERA : m:opera, $COUNTRY : g:place) |\n" +
      "   o:takes-place-in($OPERA : m:opera, $PLACE : g:place),\n" +
      "   ext-located-in($PLACE, $COUNTRY) }\n" +
      "order by $OPERA desc?\n" ;

  } else if (exName == "exNaryArias2") { // ----------------------------------
    document.queryform.query.value =
      'using mus for i"http://psi.ontopia.net/music/"\n' +
      'using op for i"http://psi.ontopia.net/opera/"\n' +
      'using ont for i"http://psi.ontopia.net/"\n' +
      "select $OPERA, $ARIA, count($CHARACTERS) from\n" +
      " op:part-of($ARIA : op:part, $OPERA : op:whole),\n" +
      " mus:sung-by($CHARACTERS : ont:person, $ARIA : mus:aria),\n" +
      " mus:sung-by($CHARACTER2 : ont:person, $ARIA : mus:aria),\n" +
      " $CHARACTERS /= $CHARACTER2\n" +
      "order by $CHARACTERS desc, $OPERA?";

  } else if (exName == "exInspiredBy2") { // ---------------------------------
    document.queryform.query.value =
      "/* First define inference rule and then use it in query:*/\n\n" +
      'using m for i"http://psi.ontopia.net/music/"\n' +
      'using op for i"http://psi.ontopia.net/opera/"\n' +
      'using lit for i"http://psi.ontopia.net/literature/"\n\n' +
      "inspired-by($COMPOSER, $WRITER) :-\n" +
      ' m:composed-by($OPERA : lit:work, $COMPOSER : m:composer),\n' +
      ' op:based-on($OPERA : op:result, $WORK : op:source),\n' +
      ' lit:written-by($WORK : lit:work, $WRITER : lit:writer).\n\n' +
      "inspired-by(verdi, $WHO)\n" +
      "order by $WHO?\n\n" +
      "------------------------------------------------------------\n\n" +
      "Anything after ? is ignored by the query processor, so it's\n" +
      "safe to write stuff here... Try the following query and\n" +
      "compare it with the query 'Composers inspired by Shakespeare'.\n" +
      "(You'll have to copy the next two lines and use them to\n" +
      "replace the two line query above.)\n\n" +
      "inspired-by($WHO, shakespeare)\n" +
      "order by $WHO?\n";

  } else if (exName == "exBibliography2") { // -------------------------------
    document.queryform.query.value =
      'using op for i"http://psi.ontopia.net/opera/"\n' +
      "select $TOPIC, $BIBREF from\n" +
      "{\n" +
      "  op:bibref($TOPIC, $BIBREF)\n" +
      "|\n" +
      "  role-player($ROLE, $TOPIC),\n" +
      "  association-role($ASSOC, $ROLE),\n" +
      "  reifies($REIFIER, $ASSOC),\n" +
      "  op:bibref($REIFIER, $BIBREF)\n" +
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

  } else if (exName == "exRecordings2") { // ---------------------------------
    document.queryform.query.value =
      'using music for i"http://psi.ontopia.net/music/"\n' +
      'using lit for i"http://psi.ontopia.net/literature/"\n' +
      'select $COMPOSER, $OPERA, $RECORDING from\n' +
      '  music:audio-recording($OPERA, $RECORDING),\n' +
      '  music:composed-by($OPERA : lit:work, $COMPOSER : music:composer)\n' +
      'order by $COMPOSER, $OPERA, $RECORDING?\n';

  // ===== factbook.hytm =====================================================

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
      'using m for i"http://www.kanzaki.com/ns/music#"\n' +
      ' select $ComposerName, $WorkTitle from\n' +
      '  instance-of($Composer, m:Composer),\n' +
      '  m:composer($WorkTitle : m:Oeuvre,\n' +
      '             $Composer : m:Composer),\n' +
      '  topic-name($Composer, $name),\n' +
      '  not(type($name, $type)),\n' +
      '  value($name, $ComposerName),\n' +
      '  s:starts-with($ComposerName, "B")\n' +
      '  order by $ComposerName, $WorkTitle?\n';

  } else if (exName == "exNoSortName") { // ----------------------------
    document.queryform.query.value =
      '/* list composers whose fullname does not have\n' +
      '   a sort variant */\n\n' +
      'using b for i"http://psi.ontopia.net/basename/#"\n' +
      'using m for i"http://www.kanzaki.com/ns/music#"\n' +
      'select $COMPOSER from\n' +
      '  instance-of($COMPOSER,m:Composer),\n' +
      '  topic-name($COMPOSER, $N),\n' +
      '  type($N, b:full-name),\n' +
      '    not(variant($N, $V))?\n';

  } else if (exName == "exConcertsByDate") { // ----------------------------
    document.queryform.query.value =
      '/* list concerts (and other musical events) by date */\n\n' +
      'using dc for i"http://purl.org/dc/elements/1.1/"\n' +
      'using m for i"http://www.kanzaki.com/ns/music#"\n' +
      'select $DATE, $EVENT from\n' +
      'instance-of($EVENT, m:Musical_Event),\n' +
      'dc:Date($EVENT, $DATE)\n' +
      'order by $DATE desc?\n';

  // ===== xmltools-tm.xtm ===================================================

  } else if (exName == "exStdUsed") { // -------------------------------------
    document.queryform.query.value =
      "select $STD, count($PRODUCT) from\n" +
      "TMAT_StandardsUse($STD : TMAR_UsedIn, $PRODUCT : TMAR_UsedBy)\n" +
      "order by $PRODUCT desc?";
  }
}


