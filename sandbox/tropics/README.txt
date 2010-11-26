TROPICS
=======
Tropics is a RESTful Topic Maps engine.


BUILDING
========
* 'ant clean'   : cleans the Tropics folder.
* 'ant compile' : builds the Tropics sources.
* 'ant test'    : tests the Tropics build (also runs 'compile' task).
* 'ant dist'    : creates a Tropics distribution for easy testing (also runs 
                  'compile' and 'test' tasks).


USAGE
=====
Tropics consists of three parts:
(1) The Tropics Server:
  (a) Run "java -jar tropics.jar" from the commandline in the dist-folder.
  

CHOOSING A BACKEND
==================
(1) File Backend: Topic Maps are stored from a ./topicmaps directory in the 
                  current working directory of Tropics.
(2) RDBMS Backend: Currently, only MySQL is tested as a RDBMS backend. 

  
SUPPORTED RESOURCES
===================
The following resources are currently supported:

(1) /topicmaps
      GET - Returns a topic map in XTM containing a topic for each topic map in
            the engine.

(2) /topicmaps/{topicMapId}
      GET - Returns a specific topic map as stored by the Topic Maps engine in 
            XTM.
      PUT - Update a topic map (by providing a complete topic map as XTM). Only
            supported with a database backend.
            
(3) /groups
      GET - Returns a topic map in XTM containing a topic for each topic map
            group in the engine. The "group.all" topic map group (which 
            contains each topic map in the engine) is always present.
            
(4) /groups/{groupId}
      GET - Returns a topic map in XTM representing the contents of the group
            specified by groupId.

(5) /topics
      GET - Returns the environments of the topics requested:
      params: "tms-include" - the topic map from which to get topics in the  
                              format "/topicmaps/{topicMapId}"
              "has-type"    - the topic type from which instances should be 
                              gotten.

(6) /topics/{topicId}
      GET - Returns the environment of the topic represented by {topicId}.
      params: "tms-include" - the topic map from which to get the topic in the  
                              format "/topicmaps/{topicMapId}"        

(7) /search
      GET - Performs a Tolog query on a specified topic map.
      params: "tms-include" - the topic map on which to perform the query 
                              in the format "/topicmaps/{topicMapId}"
              "query"       - the tolog query.

              
REMARKS
=======
(1) All query parameters need to be URLEncoded.
(2) The environment of a topic (as returned by a call to /topics/{topicId}) 
    consists of (TMSync is used behind the scenes):
  (a) The topic itself: all fields (including names, occurrences, etc.).
  (b) Its topic types, only subjectidentity fields.
  (c) The associations it plays a role in.
  (d) The association types and roles types of the associations it plays a role
      in, only subjectidentity fields.
(3) The 'ti'-parameter accepts two kinds of values:
  (a) /topicmaps/<topicmap-name> : <topicmap-name> is the name of a topic map  
                                   stored in the topic map engine. A list of 
                                   all topic maps can be found through example
                                   1 below.
  (b) /topicmaps/group.all : this value indicates a that all topic maps in the
                             topic maps engine should be taken into account 
                             when creating the representation for the resource.
                             It replaces 'ulisse.all' from the previous 
                             versions of tropics.                                   


EXAMPLES
========
Sources (especially test sources) have been included to show how the resources
can be accessed programmatically included how the results can be interpreted. 
Next to this, the URIs below can be a starting point.

(1) Get a list (a topic map actually) of all topic maps in tropics:
  http://localhost:8182/api/v1/topicmaps
  
(2) Get the ItalianOpera topic map as XTM:
  http://localhost:8182/api/v1/topicmaps/ItalianOpera

(3) Get all instances of the category topic type in all ulisse topic maps:
  http://localhost:8182/api/v1/topics?ti=/topicmaps/group.all&has-type=/topics/opera

(4) Get the Pucinni topic from the ItalianOpera topic map:
  http://localhost:8182/api/v1/topics/puccini?ti=/topicmaps/ItalianOpera  
  
(5) Find for all operas that contain a word starting with the letter 'g':
  http://localhost:8182/api/v1/search?ti=/topicmaps/group.all&query=using topics for s"http://localhost:8182/api/v1/topics/" select $OPERA_VALUE, $OPERA from instance-of($OPERA, topics:opera), topic-name($OPERA, $OPERA_NAME), value-like($OPERA_NAME, "g*"), value($OPERA_NAME, $OPERA_VALUE) order by $OPERA_VALUE?
