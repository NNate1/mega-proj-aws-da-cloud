source config.sh

GROUP_NAME="CNV-AutoScalingGroup"
aws autoscaling put-scaling-policy \
  --auto-scaling-group-name $GROUP_NAME  \
  --policy-name IncreaseGroupSize \
  --policy-type StepScaling \
  --adjustment-type ChangeInCapacity \
  --metric-aggregation-type Average \
  --step-adjustments MetricIntervalLowerBound=0.0,ScalingAdjustment=+1 | jq -r .PolicyARN > PolicyARNScaleUp.id

aws autoscaling put-scaling-policy \
  --auto-scaling-group-name $GROUP_NAME  \
  --policy-name DecreasesGroupSize \
  --policy-type StepScaling \
  --adjustment-type ChangeInCapacity \
  --step-adjustments MetricIntervalUpperBound=0.0,ScalingAdjustment=-1 | jq -r .PolicyARN > PolicyARNScaleDown.id


aws cloudwatch put-metric-alarm \
  --alarm-name Step-Scaling-AlarmHigh-AddCapacity \
  --metric-name CPUUtilization \
  --namespace AWS/EC2 \
  --statistic Average \
  --period 60 \
  --evaluation-periods 1 \
  --threshold 50 \
  --comparison-operator GreaterThanOrEqualToThreshold \
  --dimensions "Name=AutoScalingGroupName,Value=$GROUP_NAME" \
  --alarm-actions $(cat PolicyARNScaleUp.id)

aws cloudwatch put-metric-alarm \
  --alarm-name Step-Scaling-AlarmLow-RemoveCapacity \
  --metric-name CPUUtilization \
  --namespace AWS/EC2 \
  --statistic Average \
  --period 60 \
  --evaluation-periods 2 \
  --threshold 25 \
  --comparison-operator LessThanOrEqualToThreshold \
  --dimensions "Name=AutoScalingGroupName,Value=$GROUP_NAME" \
  --alarm-actions $(cat PolicyARNScaleDown.id)