User Management in the OKS
==========================

Developer's Guide
-----------------

<p class="introduction">
This document introduces the support for managing users in the Ontopia Navigator Framework. It
describes the user management topic map vocabulary, the Ontopia JAAS module, and the user management
interface.
</p>

<span class="version">Ontopia 5.1 2010-06-09</p>

### Introduction ###

Ontopia has built-in support for storing user information in the topic map being used by the web
application. Applications that do not use authentication and applications which want to use
directory information stored elsewhere have no need for this functionality, but for those who really
do want to maintain user information in the topic map Ontopia provides some useful
functionality.

Ontopia has a topic map vocabulary for representing users, their user names, their passwords, user
groups, privileges, and the relationships between these. This vocabulary is used by a JAAS (Java
Authentication and Authorization Service) module that can be used to authenticate users against the
information in the topic map. There is also a ready-made Web Editor Framework application for
maintaining user information represented using this vocabulary.

### The user vocabulary ###

The model used by the user vocabulary is very simple. There are *users* , representing user
accounts. There are *roles*, which are basically user groups. And, finally, there are *privileges*,
which represent specific rights within the system. Below is a simple example of a topic map in this
vocabulary in LTM format.

````ltm
#PREFIX um @"http://psi.ontopia.net/userman/#"

[lmg : um:user = "Lars Marius Garshol"]
{lmg, um:username, [[larsga]]}
{lmg, um:password, [[hemmelig]]}
um:plays-role(lmg : um:user, admin : um:role)

[admin : um:role = "Administrator"]
um:has-privilege(admin : um:receiver, edit-users     : um:privilege)
um:has-privilege(admin : um:receiver, edit-ontology  : um:privilege)
um:has-privilege(admin : um:receiver, edit-instances : um:privilege)
````

The fragment above creates one user, with user name, display name, and password. It also says that
this user is an admin user, and that admin users can edit users, edit the ontology, and edit
instances. (The meaning of each privilege is defined by the application using the vocabulary; no
privileges are part of the vocabulary itself.)

For more information, see the [userman](http://psi.ontopia.net/userman/) PSI set.

### The JAAS module ###

JAAS is a standard Java API for authentication, which is implemented by modules providing
authentication services. JAAS modules can then be plugged into systems that need authentication,
like a web application. Ontopia provides a JAAS implementation that allows users to authenticate
themselves against user names and passwords stored in a topic map using the above vocabulary. The
most common usage for the JAAS module is to use it to provide authentication support in a servlet
web application.

To use module in a web application the following needs to be done:

*  Configure a shared repository in the web server. (See the Navigator Framework Configuration Guide
   for directions on this.)
*  Configure the JAAS module as a service inside the web server, and make it use the shared
   repository.
*  Configure the web application to use the JAAS module to control access to the application, or parts
   of it.

The first and second steps are dependent on the application server being used, and so we will only
document how to do it for Tomcat 5.0 in this guide. See the documentation for your application
server to learn how it is done there. Note that Tomcat versions earlier than 5.x do not support
JAAS.

#### Setting up the JAAS module ####

The TMLoginModule is a JAAS module that is configured through the `$TOMCAT_HOME/conf/jaas.config`
file. Normally it only takes a single option, `topicmap`, which specifies the id of the topic map in
which the user information can be found.

````
/** Login Configuration for the JAAS Sample Application **/

TM {
   net.ontopia.topicmaps.nav2.realm.TMLoginModule required 
     topicmap=mytopicmap.ltm;
}; 
````

The JAAS module also accepts the `repository` and `jndiname` options. If none of them are specified
the default repository will be used. Use the `repository` option if you need to refer to a specific
repository by id. Use the `jndiname` option if you need to refer to the repository stored in JNDI
under the given name.

In order for the application server to find the `jaas.config` configuration file one must make the
`java.security.auth.login.config` system property refer to the `jaas.config`. Refer to your
application server's documentation on how to set system properties. One way of doing this with
Tomcat is to set the value of the `JAVA_OPTS` environment variable to:

````
-Djava.security.auth.login.config=${ONTOPIA_HOME}/apache-tomcat/conf/jaas.config
````

Having done this, the following snippet needs to be added to `apache-tomcat/conf/server.xml` to set
up a Tomcat realm that uses the JAAS module.

````xml
<Realm className="org.apache.catalina.realm.JAASRealm" appName="TM"
   userClassNames="net.ontopia.topicmaps.nav2.realm.UserPrincipal"       
   roleClassNames="net.ontopia.topicmaps.nav2.realm.RolePrincipal"/>
````

##### Encrypted passwords #####

It's possible to store encrypted passwords (instead of plaintext passwords) in the topic map for
higher security. This protects the passwords against accidental exposure of topic map data, as well
as other attacks. This is configured with the `hashmethod` parameter to the JAAS module (set in
`jaas.config`). The supported values for this parameter are listed below.

|---|---|
| plaintext | No encryption. (This is the default.) | 
| base64 | Base64-encodes the passwords. This offers only very mild security. | 
| md5 | Produces an MD5 digest of the password, then base64-encodes it. Offers strong security, but means that it is impossible to reproduce the password from what's stored in the topic map. | 

> **Note**
> Note that the user management application only supports plaintext passwords.

#### Implementing authorization ####

The TMLoginModule exposes user-group membership and privileges as role principals. The default names
for these topics will be used as the role principal names. All authenticated users will also get the
implicit `user` role principal. Applications can check privileges and user-group membership through
the `HttpServletRequest.isUserInRole(String roleName)` method. The user name can be found through
the `HttpServletRequest.getRemoteUser()` method.

#### Configuring web.xml ####

At this point, the next step is to configure the `web.xml` of the web application to add the
necessary constraints. This is common to all application servers, and useful information on this can
be found in [Caucho's security
tutorial](http://www.caucho.com/resin-3.0/security/tutorial/security-basic/index.xtp), as well as
their [guide to the web.xml
elements](http://www.caucho.com/resin-3.0/config/webapp.xtp#Servlet-2.4).

Below is an example snippet from a `web.xml` file that sets up a very simple example of
authentication. It restricts access to everything in the `/app` directory in the application, and
requires the users to have the `user` role. (The JAAS module gives *every* user the `user` role, and
one role per user group. The name of the user group is the name of the role.)

````xml
  <security-constraint>
    <web-resource-collection>
      <web-resource-name>The application</web-resource-name>
      <url-pattern>/app/*</url-pattern>
    </web-resource-collection>
    <auth-constraint>
       <role-name>user</role-name>
    </auth-constraint>
  </security-constraint>

  <login-config>
    <auth-method>FORM</auth-method>
    <realm-name>TM</realm-name>
    
    <form-login-config>
        <form-login-page>/app/login.jsp</form-login-page>
        <form-error-page>/app/error.jsp</form-error-page>
    </form-login-config>
  </login-config>

  <security-role>
    <description>
      a user of the system
    </description>
    <role-name>user</role-name>
  </security-role>
````

Note that it may not be practical to use this mechanism to implement access control for the various
kinds of users. Instead, it may be easier to allow *all* users into the restricted area, and then to
implement the checking for whether the user has a particular privilege in application
logic.

#### Finding the user topic ####

Some applications store the user information inside the topic map used by the application, in order
to be able to attach further information to each user topic. In these cases, it's necessary to be
able to get hold of the user topic inside the web application. This can be done by calling the
`HttpServletRequest` object's `getRemoteUser` method, which returns the current user's user
name.

This can then be used as follows in a web application to get the current user topic:

````application/x-jsp
<tolog:set var="username"
  ><%= request.getRemoteUser() %></tolog:set>

<tolog:set var="user" query='
  using uman for i"http://psi.ontopia.net/userman/"
    uman:username($user, %username%)?
'/>
````

If you don't want to use Java code in the JSP file you can use the `out` tag from the core JSTL tag
library instead to get the user name.

### The administration interface ###

Ontopia comes with an application that can be used to administrate topic maps containing users,
roles, and privileges represented with the userman vocabulary. The application can be found in the
`$TOMCAT_HOME/webapps/accessctl` directory and is written using the Web Editor
Framework.

To access the application, start the Tomcat server and go to
[http://localhost:8080/accessctl/?tm=userman.ltm](http://localhost:8080/accessctl/?tm=userman.ltm).
This will open the example topic map `userman.ltm` in the application, which is useful for trying it
out. (Note that to access the application you need a user name and password; you can find this in
the userman.ltm topic map by looking at it in the Omnigator.)


