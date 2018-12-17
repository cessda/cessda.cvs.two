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
echo "Zone: $ZONE"
echo "Namespace: $NAMESPACE"

# Kubctl credentials setup
gcloud container clusters get-credentials $CLIENT-$PRODUCT-$ENVIRONMENT-cc --zone=$ZONE > /dev/null 2>&1

### Kubernetes configuration generation ###
sed "s/DEPLOYMENTNAME/$CLIENT-$PRODUCT-$MODULE-$ENVIRONMENT/g; s/NAMESPACE/$NAMESPACE/g; s/PVCNAME/$CLIENT-$PRODUCT-$P_DISK1-$ENVIRONMENT-pvc/g; s/PVCNAME/$CLIENT-$PRODUCT-$P_DISK2-$ENVIRONMENT-pvc/g; s/ENVIRONMENT/$ENVIRONMENT/g" ../k8s/template-$CLIENT-$PRODUCT-$MODULE-deployment.yaml > ../k8s/$CLIENT-$PRODUCT-$MODULE-$ENVIRONMENT-deployment.yaml
sed "s/SERVICENAME/$CLIENT-$PRODUCT-$MODULE-$ENVIRONMENT/g; s/NAMESPACE/$NAMESPACE/g" ../k8s/template-$CLIENT-$PRODUCT-$MODULE-service.yaml > ../k8s/$CLIENT-$PRODUCT-$MODULE-$ENVIRONMENT-service.yaml


# Kubctl credentials setup
gcloud container clusters get-credentials $CLIENT-$PRODUCT-$ENVIRONMENT-cc --zone=$ZONE > /dev/null 2>&1


### Kubernetes Cluster and Namespace setup

# Cluster Creation
# # # # # # # # # # # # # if gcloud container clusters list 2> /dev/null | grep -E "$CLIENT-$PRODUCT-$ENVIRONMENT-cc.*RUNNING" > /dev/null 2>&1;
# # # # # # # # # # # #   then
# # # # # # # # # # #     echo "Cluster already exists"
# # # # # # # # # #   else
# # # # # # # # #     gcloud container clusters create $CLIENT-$PRODUCT-$ENVIRONMENT-cc --network $NET --subnetwork $SUBNET --num-nodes 1 --machine-type n1-standard-1 --scopes "cloud-platform" --disk-size 50 --zone $ZONE --cluster-version=1.8.2-gke.0 --labels=env=$ENVIRONMENT,project=$PRODUCT
# # # # # # # #     echo "Cluster created"
# # # # # # # fi;

# Namespace Creation
# # # # # # if kubectl get ns $NAMESPACE > /dev/null 2>&1;
# # # # #   then
# # # #   else
# # #     kubectl create namespace $NAMESPACE
# #     echo "$NAMESPACE Namespace created"
# fi;



# Deployment
kubectl apply -f ../k8s/$CLIENT-$PRODUCT-$MODULE-$ENVIRONMENT-deployment.yaml
echo "Deployment created"


# Service
kubectl apply -f ../k8s/$CLIENT-$PRODUCT-$MODULE-$ENVIRONMENT-service.yaml
echo "$CLIENT-$PRODUCT-$MODULE-$ENVIRONMENT Service created"


### Clean Workspace
rm -rf ../k8s/$CLIENT-$PRODUCT-$MODULE-$ENVIRONMENT-deployment.yaml
rm -rf ../k8s/$CLIENT-$PRODUCT-$MODULE-$ENVIRONMENT-service.yaml
