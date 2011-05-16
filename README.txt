
To build the current Ontopia distribution run
   $ mvn clean install -Pontopia-distribution-tomcat
from a terminal. The distribution can then be found in 
   ontopia-distribution-tomcat/target/ontopia-distribution-tomcat-X.Y.Z-SNAPSHOT/
where X, Y and Z are the current development version numbers.

Once you build the current Ontopia distribution you will probably 
want to play with the software.

Everything you want will be inside the distribution you just built;
the rest of the subversion checkout serves only to generate that
distribution. You can find the documentation within the distribution 
under the 'doc' directory.

If you're not already familiar with Ontopia, a good document to start
with is the install.html in the doc directory of the build you're
using.

If you're just starting out, try starting the Tomcat server as
described in section 4.3 of the install.html document, then in your
Web browser navigate to http://localhost:8080/ -- the web-based
applications listed there will give you plenty to do.
