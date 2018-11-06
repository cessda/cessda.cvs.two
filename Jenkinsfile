pipeline {
  environment {
    project_name = "cessda-development"
    client = "cessda"
    app_name = "cvmanager-gui"
    subdomain_name = "vocabularies-dev"
    feSvc_name = "${client}-${app_name}-service"
    image_tag = "eu.gcr.io/${project_name}/${app_name}:${env.BRANCH_NAME}-v${env.BUILD_NUMBER}"
  }

  agent any


  stages {
    stage('Check environment') {
      steps {
       echo "Check environment"
       echo "project_name = ${project_name}"
       echo "app_name = ${app_name}"
       echo "feSvc_name = ${feSvc_name}"
       echo "image_tag = ${image_tag}"
       }
    }
    stage('Prepare Application') {
      steps {
        dir('./infrastructure/gcp/') {
          sh("bash ${app_name}-registration.sh")
        }
      }
    }
    stage('Build Project and start Sonar scan') {
     steps {
        withSonarQubeEnv('cessda-sonar') {
          sh 'mvn clean install -U docker:build -DskipTests -Pdocker-compose}' // image tag specified in docker-compose.yaml
  //        sh 'mvn clean install sonar:sonar -Dsonar.projectName=$JOB_NAME -Dsonar.host.url=${SONAR_HOST_URL} -Dsonar.login=${SONAR_AUTH_TOKEN}'
          sleep 5
        }
      }
    stage('Check Requirements and Deployments') {
      steps {
        dir('./infrastructure/gcp/') {
          sh("bash ${app_name}-creation.sh")
        }
      }
    }
  }
}
