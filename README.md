CMD2RDF
=======

CMD2RDF is a CLARIN-NL project to make the CMD record harvested by
CLARIN available as RDF.

## Getting started

### Configure the app
There are two important files for the configuration:
* /path/to/CMD2RDF/batch/src/main/resources/cmd2rdf.xml
* /path/to/CMD2RDF/lda/src/main/webapp/specs/cmd2rdf-lda.ttl

The file `cmd2rdf-lda.ttl` is used to connect the web application `cmd2rdf-lda` to `cmd2rdf`.
This file has to be changed whenweb applications will be running on `localhost:8080`.
All the references of `192.168.99.100:8080` should be changed to `localhost:8080`. 

The file `/path/to/CMD2RDF/webapps/src/main/webapp/WEB-INF/web.xml` contains a path to the `cmd2rdf.xml`-file

### Starting the web app
This guide expects that the code is already build. 

1. Make sure Virtuoso is running on its default uri: localhost:8090
You can start the it with docker see: https://hub.docker.com/r/openlink/virtuoso-opensource-7  

1.  Deploy the wars of the subprojects `webapps` and `lda` in Apache Tomcat.
Tomcat 6 & 7 will work with the application.

1. Open the app `cmdi2rdf`: http://localhost:8080/cmd2rdf (when Tomcat runs or port 8080 of localhost)


  


