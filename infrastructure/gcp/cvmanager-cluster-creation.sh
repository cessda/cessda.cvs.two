#!/bin/bash

#############################
#          CESSDA           #
#      Cluster Setup        #
#############################

# Julien Le Hericy
# CESSDA ERIC
# j.lehericy(at)cessda.eu


### Load Env Variable and set gcloud Region/Zone/Project
source ./gcp.config > /dev/null 2>&1

### Kubernetes Cluster and Namespace setup

# Cluster Creation
if gcloud container clusters list 2> /dev/null | grep -E "$PRODUCT-$ENVIRONMENT-cc.*RUNNING" > /dev/null 2>&1;
  then
    echo "Cluster already exists"
  else
    gcloud container clusters create $PRODUCT-$ENVIRONMENT-cc --network $NET --subnetwork $SUBNET --num-nodes 1 --machine-type n1-standard-1 --scopes "cloud-platform" --disk-size 50 --zone $ZONE --cluster-version=1.8.2-gke.0 --labels=env=$ENVIRONMENT,project=$PRODUCT
    echo "Cluster created"
fi;

# Kubctl credentials setup
gcloud container clusters get-credentials $PRODUCT-$ENVIRONMENT-cc --zone=$ZONE > /dev/null 2>&1

# Namespace Creation
if kubectl get ns $PRODUCT-$ENVIRONMENT > /dev/null 2>&1;
  then
    echo "Namespace already exists"
  else
    kubectl create namespace $PRODUCT-$ENVIRONMENT
    echo "Namespace created"
fi;

### Kubernetes configuration generation ###
sed "s/DEPLOYMENTNAME/$PRODUCT-$MODULE-$ENVIRONMENT/g; s/NAMESPACE/$PRODUCT-$ENVIRONMENT/g; s/ENVIRONMENT/$ENVIRONMENT/g" ../k8s/template-deployment.yaml > ../k8s/$PRODUCT-$MODULE-$ENVIRONMENT-deployment.yaml
sed "s/SERVICENAME/$PRODUCT-$MODULE-$ENVIRONMENT/g; s/NAMESPACE/$PRODUCT-$ENVIRONMENT/g" ../k8s/template-service.yaml > ../k8s/$PRODUCT-$MODULE-$ENVIRONMENT-service.yaml

### Kubernetes Deployment ###

# Deployment
if kubectl get deployment $PRODUCT-$MODULE-$ENVIRONMENT -n $PRODUCT-$ENVIRONMENT > /dev/null 2>&1;
  then
    echo "Deployment already exists, it will be destroyed to perform the new deployment"
    kubectl delete deployment $PRODUCT-$MODULE-$ENVIRONMENT -n $PRODUCT-$ENVIRONMENT > /dev/null 2>&1
    kubectl create -f ../k8s/$PRODUCT-$MODULE-$ENVIRONMENT-deployment.yaml > /dev/null 2>&1
    echo "Deployment created"
  else
    kubectl create -f ../k8s/$PRODUCT-$MODULE-$ENVIRONMENT-deployment.yaml > /dev/null 2>&1
    echo "Deployment created"
fi;

# Service
if kubectl get service $PRODUCT-$MODULE-$ENVIRONMENT -n $PRODUCT-$ENVIRONMENT > /dev/null 2>&1;
  then
    echo "Service already exists"
  else
    kubectl create -f ../k8s/$PRODUCT-$MODULE-$ENVIRONMENT-service.yaml > /dev/null 2>&1
    echo "Service created"
fi;

### Clean Workspace
rm -rf ../k8s/$PRODUCT-$MODULE-$ENVIRONMENT-deployment.yaml
rm -rf ../k8s/$PRODUCT-$MODULE-$ENVIRONMENT-service.yaml
