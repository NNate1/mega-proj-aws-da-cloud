#!/bin/bash

source config.sh

# Install java.
cmd="sudo yum update -y; sudo yum install java-1.8.0-openjdk-devel.x86_64 -y;"
ssh -o StrictHostKeyChecking=no -i $AWS_EC2_SSH_KEYPAR_PATH ec2-user@$(cat instance.dns) $cmd

# Install web server.
#scp -o StrictHostKeyChecking=no -i $AWS_EC2_SSH_KEYPAR_PATH $DIR/../res/WebServer.java ec2-user@$(cat instance.dns):

#scp -o StrictHostKeyChecking=no -i $AWS_EC2_SSH_KEYPAR_PATH $DIR/../java/WebServer.java ec2-user@$(cat instance.dns):
scp -o StrictHostKeyChecking=no -i $AWS_EC2_SSH_KEYPAR_PATH $JAR_PATH ec2-user@$(cat instance.dns):

# Build web server.
# cmd="javac WebServer.java"
# ssh -o StrictHostKeyChecking=no -i $AWS_EC2_SSH_KEYPAR_PATH ec2-user@$(cat instance.dns) $cmd 

# Setup web server to start on instance launch.
#cmd="echo \"java -cp /home/ec2-user WebServer\" | sudo tee -a /etc/rc.local; sudo chmod +x /etc/rc.local"

cmd="echo \"java -cp /home/ec2-user/$JAR_FILE -javaagent:/home/ec2-user/$JAR_FILE=ICount:pt.ulisboa.tecnico.cnv:output pt.ulisboa.tecnico.cnv.webserver.WebServer\" | sudo tee -a /etc/rc.local; sudo chmod +x /etc/rc.local"
ssh -o StrictHostKeyChecking=no -i $AWS_EC2_SSH_KEYPAR_PATH ec2-user@$(cat instance.dns) $cmd
