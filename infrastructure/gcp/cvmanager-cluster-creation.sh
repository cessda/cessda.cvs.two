#!/bin/bash

#############################
#          CESSDA           #
#  Generic Cluster Setup    #
#############################


### Load Env Variable and set gcloud Region/Zone/Project
source ./gcp.config > /dev/null 2>&1

### some debug statements
echo "Client: $CLIENT"
echo "Product: $PRODUCT"
echo "Module: $MODULE"
echo "Environment: $ENVIRONMENT"


### Kubernetes configuration generation ###
sed "s/DEPLOYMENTNAME/$CLIENT-$PRODUCT-$MODULE-$ENVIRONMENT/g; s/NAMESPACE/$CLIENT-$PRODUCT-$ENVIRONMENT/g; s/PVCNAME/$CLIENT-$PRODUCT-$ENVIRONMENT-pvc/g; s/ENVIRONMENT/$ENVIRONMENT/g" ../k8s/template-$CLIENT-$PRODUCT-$MODULE-deployment.yaml > ../k8s/$CLIENT-$PRODUCT-$MODULE-$ENVIRONMENT-deployment.yaml
sed "s/SERVICENAME/$CLIENT-$PRODUCT-$MODULE-$ENVIRONMENT/g; s/NAMESPACE/$CLIENT-$PRODUCT-$ENVIRONMENT/g" ../k8s/template-$CLIENT-$PRODUCT-$MODULE-service.yaml > ../k8s/$CLIENT-$PRODUCT-$MODULE-$ENVIRONMENT-service.yaml


# Kubctl credentials setup
gcloud container clusters get-credentials $CLIENT-$PRODUCT-$ENVIRONMENT-cc --zone=$ZONE > /dev/null 2>&1


# Namespace Creation (not usually done here)
if kubectl get ns $CLIENT-$PRODUCT-$ENVIRONMENT > /dev/null 2>&1;
  then
    echo "Namespace already exists"
  else
    kubectl create namespace $CLIENT-$PRODUCT-$ENVIRONMENT
    echo "Namespace created"
fi;


### Kubernetes Cluster and Namespace setup

# Cluster Creation
if gcloud container clusters list 2> /dev/null | grep -E "$PRODUCT-$ENVIRONMENT-cc.*RUNNING" > /dev/null 2>&1;
  then
    echo "Cluster already exists"
  else
    gcloud container clusters create $PRODUCT-$ENVIRONMENT-cc --network $NET --subnetwork $SUBNET --num-nodes 1 --machine-type n1-standard-1 --scopes "cloud-platform" --disk-size 50 --zone $ZONE --cluster-version=1.8.2-gke.0 --labels=env=$ENVIRONMENT,project=$PRODUCT
    echo "Cluster created"
fi;


# Deployment
if kubectl get deployment $CLIENT-$PRODUCT-$MODULE-$ENVIRONMENT -n $CLIENT-$PRODUCT-$ENVIRONMENT > /dev/null 2>&1;
  then
    echo "Deployment already exists, it will be destroyed to perform the new deployment"
    kubectl delete deployment $CLIENT-$PRODUCT-$MODULE-$ENVIRONMENT -n $CLIENT-$PRODUCT-$ENVIRONMENT > /dev/null 2>&1
    kubectl create -f ../k8s/$CLIENT-$PRODUCT-$MODULE-$ENVIRONMENT-deployment.yaml
    echo "Deployment created"
  else
    kubectl create -f ../k8s/$CLIENT-$PRODUCT-$MODULE-$ENVIRONMENT-deployment.yaml
    echo "Deployment created"
fi;


# Service
if kubectl get service $CLIENT-$PRODUCT-$MODULE-$ENVIRONMENT -n $CLIENT-$PRODUCT-$ENVIRONMENT;
  then
    echo "Service already exists"
  else
    kubectl create -f ../k8s/$CLIENT-$PRODUCT-$MODULE-$ENVIRONMENT-service.yaml
    echo "Service created"
fi;


### Clean Workspace
rm -rf ../k8s/$CLIENT-$PRODUCT-$MODULE-$ENVIRONMENT-deployment.yaml
rm -rf ../k8s/$CLIENT-$PRODUCT-$MODULE-$ENVIRONMENT-service.yaml
