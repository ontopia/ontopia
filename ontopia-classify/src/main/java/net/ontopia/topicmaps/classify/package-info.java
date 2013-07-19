/*
 * #!
 * Ontopia Classify
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */
/**
<p>To classify content, use the <tt>SimpleClassifier</tt> class.  Note
that most of the APIs are INTERNAL, and so may change at any time.</p>

<p>If you need more flexibility, it is possible to use the INTERNAL
APIs directly. Below is example code showing how to output a ranked
list of the terms found in a particular document.</p>

<pre>
    // load the topic map
    TopicMapIF topicmap = ImportExportUtils.getReader(args[0]).read();

    // create classifier
    TopicMapClassification tcl = new TopicMapClassification(topicmap);

    // read document
    ClassifiableContentIF cc = ClassifyUtils.getClassifiableContent(args[1]);

    // classify document
    tcl.classify(cc);

    // dump the ranked terms
    TermDatabase tdb = tcl.getTermDatabase();
    tdb.dump(50);
</pre>
*/

package net.ontopia.topicmaps.classify;
