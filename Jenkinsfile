pipeline {
  environment {
    project_name = "cessda-development"
    client = "cessda"
    app_name = "cvmanager-gui"
    env_name = "live"
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
          sh 'mvn clean install -U docker:build -DskipTests -Pdocker-compose'
          sleep 5
        }
      }
    }
    stage('Push Docker image') {
      steps {
	echo "Push Docker image"
        sh("gcloud docker -- push ${image_tag}")
        sh("gcloud container images add-tag ${image_tag} eu.gcr.io/${project_name}/${app_name}:${env.BRANCH_NAME}-latest")
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
