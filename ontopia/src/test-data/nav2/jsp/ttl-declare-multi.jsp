<tolog:context topicmap="i18n.ltm">
List som creators and creations:

<tolog:declare>
  using i18n for i"http://psi.ontopia.net/i18n/#"
</tolog:declare>  

<tolog:declare>
  using iso for i"http://psi.ontopia.net/iso/15924.xtm#"
  
  made($MAKER, $MAKEE) :-
  created-by($MAKER : creator, $MAKEE : creation).
</tolog:declare>  

Tolkien created:
<tolog:foreach query="made(tolkien, $MAKEE) order by $MAKEE?"
  >  <tolog:out var="MAKEE"/>
</tolog:foreach>

The Cherokee Script was created by <tolog:out query="made($MAKER, iso:cher) order by $MAKER?"/> and was used to write the <tolog:out query="i18n:written-in(iso:cher : script, $LANGUAGE : language) order by $LANGUAGE?"/> language.

Creations of Wulfila:
<tolog:foreach query="made(wulfila, $MAKEE) order by $MAKEE?"
  >  <tolog:out var="MAKEE"/>
</tolog:foreach>

</tolog:context>
