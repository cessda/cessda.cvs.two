# CESSDA Vocabulary Service
This repository contains all the source code and configuration needed to deploy the CESSDA Vocabulary Service application

##Project Structure

```bash
<ROOT>
├── infrastructure/		# Deployment scripts and configuration for gcp
├── src/				# Java application source code and configuration
├── target/				# Target build location
├── CONTRIBUTORS.md		# List of contributors to the project
├── Jenkinsfile			# Script used by Jenkins to deploy the application to gcp
├── pom.xml				# Maven build configuration file
├── README.md			# Instructions to build and deploy the application (this file)

```

## Technology Stack

Several frameworks and languages are used in this application.

| Framework/Technology                                 | Description                                              |
| ---------------------------------------------------- | -------------------------------------------------------- |
| Docker                                               | Container platform                                       |
| Kubernetes                                           | Container orchestrator                                   |
| Maven                                                | Build tool                                               |
| Java                                                 | Programming language and API                             |
| Vaadim                                               | Web application framework                                |
| Spring                                               | Web application framework                                |

## Naming

There is a unfortunate lack of alignment between product (tool/service) names and repository names, which can make the setting of variable values unclear.

Both the `Jenkins file` and `gcp.conf` contain variable declarations, but these variable names and values serve different purposes during the build and deployment process.



### Jenkins file

'fixed' variables (and typical values):

**project_name** = "cessda-development" (N.B. same value as PROJECT in gcp.config)



'changeable' variables:

**app_name** = "pasc-reverse"



### gcp.config
The gcp.config contains a number of variables, some of which are (more or less) fixed for all CESSDA projects,
other which need to be set per project, per module and per branch.

The following are stable for the time being, but all are changable if required, with NET enabling different deployment options within a specified GCP project,
and PROJECT and CLIENT allowing e.g. the possibility to host products for third parties within different GCP projects.



'fixed' variables (and typical values):

**REGION**=europe-west1

**ZONE**=europe-west1-b

**PROJECT**=cessda-development (N.B. same value as project_name in Jenkins file)

**NET**=jenkins-net

**CLIENT**=cessda



'changeable' variables:

**SUBNET** (different for dev, staging or production deployments)

**PRODUCT** (e.g. cvmanager)

**MODULE** (e.g. reverse)

**ENVIRONMENT** (e.g. dev)

Various files should be named in accordance with the values set for CLIENT, PRODUCT, MODULE

such as `template-<CLIENT>-<PRODUCT>-<MODULE>-deployment.yaml` (e.g. template-cessda-cvmanager-reverse-deployment.yaml)

and `template-<CLIENT>-<PRODUCT>-<MODULE>-service.yaml` (e.g. template-cessda-cvmanager-reverse-service.yaml)

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See the deployment section for instructions on how to deploy the project on a live system.

### Prerequisites

These are a list of dependencies that are required to build and run the CESSDA Vocabulary Service

- Java to run the application
- Maven to build the application
- Docker to store the application in containers for deployment
- Google Cloud SDK and permissions to access CESSDA cloud platform


### Installing

To create a local dev environment follow these instructions:

1. Clone the repository to your local workspace
2. Ensure that any required software and dependencies are installed
3. Build CESSDA Vocabulary Service with mvn clean install -U docker:build -Pdocker-compose
   (optional - use -DskipTests to speed up build)
4. Use `docker-compose up` in the directory `target/docker/generated` to start the application

The CESSDA Vocabulary Service should be running on `http://localhost:8080/`


## Deployment

Deployment in the live Google Cloud Environment requires a couple of considerations:

1. All deployments to the GCP Kubernetes engine are done via Jenkins
2. The configuration for the deployment is stored in infrastructure/gcp
3. Do not increase the replicas of the application. Because Vaadim keeps track of the session in the server increasing replicas will cause sessions to expire immediately, breaking the application.

Deployments should occur automatically once commits are made to this repository, if not go to https://cit.cessda.eu/job/cessda.cvmanager.beta/ and select the appropriate branch to build

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management

## Contributing

Please read [CESSDA Guideline for developers] (https://bitbucket.org/cessda/cessda.guidelines.cit/wiki/Developers) for details on our code of conduct, and the process for submitting pull requests to us.

## Authors

* **First Name - Last Name** - *Initial work*

You can find the list of all contributors [here](CONTRIBUTORS.md)
