#!/bin/bash

### Load Env Variable and set gcloud Region/Zone/Project
source ./gcp.config > /dev/null 2>&1

# Kubctl credentials setup
gcloud container clusters get-credentials $CLIENT-$PRODUCT-$ENVIRONMENT-cc --zone=$ZONE > /dev/null 2>&1

### Kubernetes Deployment ###
if kubectl get deployment $CLIENT-$PRODUCT-mailrelay-$ENVIRONMENT -n $CLIENT-$PRODUCT-$ENVIRONMENT > /dev/null 2>&1;
  then
    echo "MailRelay component available, deployment will be processed"
    sed -i "s/MAILRELAY/$CLIENT-$PRODUCT-mailrelay-$ENVIRONMENT/g" ../../src/main/resources/application.yml
  else
    echo "MailRelay component not available, deployment's aborted"
    exit 1
fi;

