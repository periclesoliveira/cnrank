CNRank
==================
Ranking CNs, based on their probability of producing relevant answers to the user. This relevance is estimated based on the current state of the underlying
database using a probabilistic Bayesian model we have developed.


Setup before usage:
====================
\#Install the postgresql database

\#Create one database (e.g: from Coffman datasets available in dataset folder)

\#Populate the tables

\# download the postgresql-jdbc

\#Setup the CLASSPATH in your /etc/profile
 e.g(export CLASSPATH=/home/ubuntu/scngen/postgresql-9.3-1100.jdbc41.jar:/usr/lib/jvm/java-7-openjdk-amd64/lib/tools.jar:/usr/lib/jvm/java-7-openjdk-amd64/jre/lib/rt.jar:/usr/share/tomcat7/lib/servlet-api.jar:.)

\#Create the git Repository in your laptop or server:

git init

git clone https://github.com/periclesoliveira/cnrank

CNRank Usage:
===================
\# vi config.properties
  *this file contains the database name and username/password used to connect in this database. The last field is the hostname, in case of remote address put the dns name or IP Address used to access postgresql database.

\#javac CNRank.java

\#java CNRank

Example Usage
===================

periclesoliveira@ip-172-31-37-236:~/cnrank/src$ java CNRank
Successfully connected to the Database
Creating Term Index in Memory...
Time to index.......=13617

TermIndex size=225535
Input Keywords:denzel washington

