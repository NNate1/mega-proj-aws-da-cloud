#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"

#export PATH=<path to aws installation>:$PATH
export AWS_DEFAULT_REGION=us-east-1
export AWS_ACCOUNT_ID=
export AWS_ACCESS_KEY_ID=
export AWS_SECRET_ACCESS_KEY=
export AWS_EC2_SSH_KEYPAR_PATH=$DIR/CNV-key.pem
export AWS_SECURITY_GROUP=CNV-http+ssh
export AWS_SECURITY_GROUP_ID=
export AWS_KEYPAIR_NAME=CNV-key
