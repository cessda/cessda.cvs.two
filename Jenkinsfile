pipeline {
  environment {
    project_name = "cessda-development"
    app_name = "cessda.cvmanager"
    env_name = "dev"
    feSvc_name = "${app_name}-service"
    image_tag = "eu.gcr.io/${project_name}/cessda/${app_name}:latest"
    cluster = "cessda-cvmanager-dev-cc"
  }

  agent any

  stages {
    stage('Prepare Application') {
      steps {
        dir('./infrastructure/gcp/') {
          sh("bash cvmanager-gui-registration.sh")
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
        sh("gcloud container images add-tag ${image_tag} eu.gcr.io/${project_name}/${app_name}:latest")
      }
    }
    stage('Check Requirements and Deployments') {
      steps {
        dir('./infrastructure/gcp/') {
          sh("bash cvmanager-cluster-creation.sh")
        }
      }
    }
  }
}
