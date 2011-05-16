<logic:context tmparam="tm" objparam="id" set="topic" settm="topicmap">

  <!-- instantiate class and register as a new function -->
  <logic:externalFunction name="helloWorld"
      fqcn="net.ontopia.topicmaps.nav2.impl.basic.HelloWorldFunction" />

  <!-- call this function -->
  <logic:call name="helloWorld" />
    
</logic:context>
