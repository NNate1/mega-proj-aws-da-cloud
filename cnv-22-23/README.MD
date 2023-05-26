# Setup
`mvn install`

# Run WebServer
`java -cp webserver/target/webserver-1.0.0-SNAPSHOT-jar-with-dependencies.jar -javaagent:webserver/target/webserver-1.0.0-SNAPSHOT-jar-with-dependencies.jar=ICount:pt.ulisboa.tecnico.cnv:output  pt.ulisboa.tecnico.cnv.webserver.WebServer`

# Deploy to aws
```
./create-image.sh
./launch-deployment.sh
./create-scaling-policy.sh
```

# Clean aws resources
```
./deregister-image.sh
./terminate-deployment.sh
 ./delete-snapshots.sh
```