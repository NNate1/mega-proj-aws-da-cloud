## Projeto de CNV na Cláudia

`mvn install`

# jar separados
`java -cp webserver/target/webserver-1.0.0-SNAPSHOT-jar-with-dependencies.jar -javaagent:../../lab-javassist/target/JavassistWrapper-1.0-jar-with-dependencies.jar=ICount:pt.ulisboa.tecnico.cnv:output  pt.ulisboa.tecnico.cnv.webserver.WebServer`

# jar junto
`java -cp webserver/target/webserver-1.0.0-SNAPSHOT-jar-with-dependencies.jar -javaagent:webserver/target/webserver-1.0.0-SNAPSHOT-jar-with-dependencies.jar=ICount:pt.ulisboa.tecnico.cnv:output  pt.ulisboa.tecnico.cnv.webserver.WebServer`