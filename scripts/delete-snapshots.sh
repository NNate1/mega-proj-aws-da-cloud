#!/bin/bash

# Get all snapshot IDs in the account
SNAPSHOT_IDS=$(aws ec2 describe-snapshots --owner-ids self --query 'Snapshots[*].SnapshotId' --output text)

# Loop through all snapshot IDs and delete them
for SNAPSHOT_ID in $SNAPSHOT_IDS
do
  echo "Deleting snapshot with ID $SNAPSHOT_ID"
  aws ec2 delete-snapshot --snapshot-id $SNAPSHOT_ID
done