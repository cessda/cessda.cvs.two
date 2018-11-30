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
echo "Persistent disk 1: $P_DISK1"
echo "Persistent disk 2: $P_DISK2"


### Kubernetes configuration generation ###
sed "s/DEPLOYMENTNAME/$CLIENT-$PRODUCT-$MODULE-$ENVIRONMENT/g; s/NAMESPACE/$NAMESPACE/g; s/PVCNAME/$CLIENT-$PRODUCT-$P_DISK1-$ENVIRONMENT-pvc/g; s/PVCNAME/$CLIENT-$PRODUCT-$P_DISK2-$ENVIRONMENT-pvc/g; s/ENVIRONMENT/$ENVIRONMENT/g" ../k8s/template-$CLIENT-$PRODUCT-$MODULE-deployment.yaml > ../k8s/$CLIENT-$PRODUCT-$MODULE-$ENVIRONMENT-deployment.yaml
sed "s/SERVICENAME/$CLIENT-$PRODUCT-$MODULE-$ENVIRONMENT/g; s/NAMESPACE/$NAMESPACE/g" ../k8s/template-$CLIENT-$PRODUCT-$MODULE-service.yaml > ../k8s/$CLIENT-$PRODUCT-$MODULE-$ENVIRONMENT-service.yaml


# Kubctl credentials setup
gcloud container clusters get-credentials $CLIENT-$PRODUCT-$ENVIRONMENT-cc --zone=$ZONE > /dev/null 2>&1


### DDI Storage Setup ###

# Disk Creation (DDI)
if gcloud compute disks list 2> /dev/null | grep -E "$CLIENT-$PRODUCT-$P_DISK1-$ENVIRONMENT-disk.*READY" > /dev/null 2>&1;
  then
    echo "$P_DISK1 disk already exists"
  else
    gcloud compute disks create $CLIENT-$PRODUCT-$ENVIRONMENT-$P_DISK1-disk --size 10GB
    echo "$P_DISK1 disk created"
fi;

#Persistent Volume Creation (DDI)
if kubectl get pv -n $CLIENT-$PRODUCT-$P_DISK1-$ENVIRONMENT 2> /dev/null | grep -E "$CLIENT-$PRODUCT-$P_DISK1-$ENVIRONMENT-pv" > /dev/null 2>&1;
  then
    echo "$P_DISK1 Persistent Volume already exist"
  else
    sed "s/PVNAME/$CLIENT-$PRODUCT-$P_DISK1-$ENVIRONMENT-pv/g; s/NAMESPACE/$NAMESPACE/g; s/DISKNAME/$CLIENT-$PRODUCT-$ENVIRONMENT-$P_DISK1-disk/g" ../k8s/template-$CLIENT-$PRODUCT-$P_DISK1-pv.yaml > ../k8s/$CLIENT-$PRODUCT-$P_DISK1-$ENVIRONMENT-pv.yaml
    kubectl create -f ../k8s/$CLIENT-$PRODUCT-$P_DISK1-$ENVIRONMENT-pv.yaml
    rm -rf ../k8s/$CLIENT-$PRODUCT-$P_DISK1-$ENVIRONMENT-pv.yaml
    echo "$P_DISK1 Persistent Volume created"
fi;

#Persistent Volume Claim (DDI)
if kubectl get pvc -n $CLIENT-$PRODUCT-$P_DISK1-$ENVIRONMENT 2> /dev/null | grep -E "$CLIENT-$PRODUCT-$P_DISK1-$ENVIRONMENT-pvc" > /dev/null 2>&1;
  then
    echo "$P_DISK1 Persistent Volume Claim already exist"
  else
    sed "s/PVNAME/$CLIENT-$PRODUCT-$P_DISK1-$ENVIRONMENT-pv/g; s/NAMESPACE/$NAMESPACE/g; s/PVCNAME/$CLIENT-$PRODUCT-$P_DISK1-$ENVIRONMENT-pvc/g" ../k8s/template-$CLIENT-$PRODUCT-$P_DISK1-pvc.yaml > ../k8s/$CLIENT-$PRODUCT-$P_DISK1-$ENVIRONMENT-pvc.yaml
    kubectl create -f ../k8s/$CLIENT-$PRODUCT-$P_DISK1-$ENVIRONMENT-pvc.yaml
    rm -rf ../k8s/$CLIENT-$PRODUCT-$P_DISK1-$ENVIRONMENT-pvc.yaml
    echo "$P_DISK1 Persistent Volume Claim created"
fi;

### Split Storage Setup ###

# Disk Creation (Split)
if gcloud compute disks list 2> /dev/null | grep -E "$CLIENT-$PRODUCT-$P_DISK2-$ENVIRONMENT-disk.*READY" > /dev/null 2>&1;
  then
    echo "$P_DISK2 disk already exists"
  else
    gcloud compute disks create $CLIENT-$PRODUCT-$P_DISK2-$ENVIRONMENT-disk --size 10GB
    echo "$P_DISK2 disk created"
fi;


#Persistent Volume Creation (Split)
if kubectl get pv -n $CLIENT-$PRODUCT-$P_DISK2-$ENVIRONMENT 2> /dev/null | grep -E "$CLIENT-$PRODUCT-$P_DISK2-$ENVIRONMENT-pv" > /dev/null 2>&1;
  then
    echo "$P_DISK2 Persistent Volume already exist"
  else
    sed "s/PVNAME/$CLIENT-$PRODUCT-$P_DISK2-$ENVIRONMENT-pv/g; s/NAMESPACE/$NAMESPACE/g; s/DISKNAME/$CLIENT-$PRODUCT-$P_DISK2-$ENVIRONMENT-disk/g" ../k8s/template-$CLIENT-$PRODUCT-$P_DISK2-pv.yaml > ../k8s/$CLIENT-$PRODUCT-$P_DISK2-$ENVIRONMENT-pv.yaml
    kubectl create -f ../k8s/$CLIENT-$PRODUCT-$P_DISK2-$ENVIRONMENT-pv.yaml
    rm -rf ../k8s/$CLIENT-$PRODUCT-$P_DISK2-$ENVIRONMENT-pv.yaml
    echo "$P_DISK2 Persistent Volume created"
fi;

#Persistent Volume Claim (Split)
if kubectl get pvc -n $CLIENT-$PRODUCT-$P_DISK2-$ENVIRONMENT 2> /dev/null | grep -E "$CLIENT-$PRODUCT-$P_DISK2-$ENVIRONMENT-pvc" > /dev/null 2>&1;
  then
    echo "$P_DISK2 Persistent Volume Claim already exist"
  else
    sed "s/PVNAME/$CLIENT-$PRODUCT-$P_DISK2-$ENVIRONMENT-pv/g; s/NAMESPACE/$NAMESPACE/g; s/PVCNAME/$CLIENT-$PRODUCT-$P_DISK2-$ENVIRONMENT-pvc/g" ../k8s/template-$CLIENT-$PRODUCT-$P_DISK2-pvc.yaml > ../k8s/$CLIENT-$PRODUCT-$P_DISK2-$ENVIRONMENT-pvc.yaml
    kubectl create -f ../k8s/$CLIENT-$PRODUCT-$P_DISK2-$ENVIRONMENT-pvc.yaml
    rm -rf ../k8s/$CLIENT-$PRODUCT-$P_DISK2-$ENVIRONMENT-pvc.yaml
    echo "$P_DISK2 Persistent Volume Claim created"
fi;


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
if kubectl get deployment $CLIENT-$PRODUCT-$MODULE-$ENVIRONMENT -n $NAMESPACE > /dev/null 2>&1;
  then
    echo "Deployment already exists in namespace $NAMESPACE, it will be destroyed to perform the new deployment"
    kubectl delete deployment $CLIENT-$PRODUCT-$MODULE-$ENVIRONMENT -n $NAMESPACE > /dev/null 2>&1
    kubectl create -f ../k8s/$CLIENT-$PRODUCT-$MODULE-$ENVIRONMENT-deployment.yaml
    echo "Deployment created"
  else
    echo "Deployment does not exist in namespace $NAMESPACE, it will be created"
    kubectl create -f ../k8s/$CLIENT-$PRODUCT-$MODULE-$ENVIRONMENT-deployment.yaml
    echo "Deployment created"
fi;


# Service
if kubectl get service $CLIENT-$PRODUCT-$MODULE-$ENVIRONMENT -n $NAMESPACE;
  then
    echo "$CLIENT-$PRODUCT-$MODULE-$ENVIRONMENT Service already exists"
  else
    kubectl create -f ../k8s/$CLIENT-$PRODUCT-$MODULE-$ENVIRONMENT-service.yaml
    echo "$CLIENT-$PRODUCT-$MODULE-$ENVIRONMENT Service created"
fi;


### Clean Workspace
rm -rf ../k8s/$CLIENT-$PRODUCT-$MODULE-$ENVIRONMENT-deployment.yaml
rm -rf ../k8s/$CLIENT-$PRODUCT-$MODULE-$ENVIRONMENT-service.yaml
