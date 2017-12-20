pipeline {
  environment {
    project_name = "cessda-development"
    app_name = "cessda-cvmanager"
    env_name = "dev"
    feSvc_name = "${app_name}-service"
    image_tag = "eu.gcr.io/${project_name}/${app_name}:v${env.BUILD_NUMBER}"
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
          sh 'mvn clean install -U sonar:sonar -Dsonar.projectName=$JOB_NAME -Dsonar.host.url=${SONAR_HOST_URL} -Dsonar.login=${SONAR_AUTH_TOKEN} -DskipTests -Pdocker-compose'
          sleep 5
        }
      }
    }
    stage('Get Quality Gate Status') {
      steps {
        withSonarQubeEnv('cessda-sonar') {
          sh 'curl -su ${SONAR_AUTH_TOKEN}: ${SONAR_HOST_URL}api/qualitygates/project_status?analysisId="$(curl -su ${SONAR_AUTH_TOKEN}: ${SONAR_HOST_URL}api/ce/task?id="$(cat target/sonar/report-task.txt | awk -F "=" \'/ceTaskId=/{print $2}\')" | jq -r \'.task.analysisId\')" | jq -r \'.projectStatus.status\' > status'
        }
        script {
          STATUS = readFile('status')
          if ( STATUS.trim() == "ERROR") {
            error("Quality Gate not reached, please review the Sonar Report")
          } else if ( STATUS.trim() == "WARN") {
            error("Quality Gate not reached, please review the Sonar Report")
          } else {
            echo "Quality Gate reached, deployment will be processed, please wait"
          }
        }
      }
    }
    stage('Build Docker image') {
      steps {
        echo "Build Docker image"
        sh("docker build -t ${image_tag} . --no-cache")
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
