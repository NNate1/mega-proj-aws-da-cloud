#!/bin/bash

source config.sh

# Create load balancer and configure health check.
aws elb create-load-balancer \
	--load-balancer-name CNV-LoadBalancer \
	--listeners "Protocol=HTTP,LoadBalancerPort=80,InstanceProtocol=HTTP,InstancePort=8000" \
	--availability-zones us-east-1a
echo "Created load balancer"

aws elb configure-health-check \
	--load-balancer-name CNV-LoadBalancer \
	--health-check Target=HTTP:8000/test,Interval=30,UnhealthyThreshold=2,HealthyThreshold=10,Timeout=5
echo "Configured health check"

# Create launch configuration.
aws autoscaling create-launch-configuration \
	--launch-configuration-name CNV-LaunchConfiguration \
	--image-id $(cat image.id) \
	--instance-type t2.micro \
	--security-groups $AWS_SECURITY_GROUP_ID \
	--key-name $AWS_KEYPAIR_NAME \
	--instance-monitoring Enabled=true

echo "Created launch configuration"

# Create auto scaling group.
aws autoscaling create-auto-scaling-group \
	--auto-scaling-group-name CNV-AutoScalingGroup \
	--launch-configuration-name CNV-LaunchConfiguration \
	--load-balancer-names CNV-LoadBalancer \
	--availability-zones us-east-1a \
	--health-check-type ELB \
	--health-check-grace-period 60 \
	--min-size 1 \
	--max-size 2 \
	--desired-capacity 1

echo "Created auto scaling group"