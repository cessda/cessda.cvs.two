pipeline {
	environment {
        product_name = "cvs"
        module_name = "gui"
        IMAGE_TAG = "${docker_repo}/${product_name}-${module_name}:${env.BRANCH_NAME}-${env.BUILD_NUMBER}"
	}

    agent {
        label 'jnlp-himem'
    }

	stages {
        stage('Check environment') {
            steps {
                echo "Check environment"
                echo "product_name = ${product_name}"
                echo "module_name = ${module_name}"
                echo "image_tag = ${IMAGE_TAG}"
            }
        }
		stage('Build Project') {
			steps {
                withMaven {
                    sh 'mvn clean install -Pdocker-compose'					
				}
			}
		}
        stage('Run Sonar Scan') {
            steps {
                withSonarQubeEnv('cessda-sonar') {
                    withMaven {
                        sh 'mvn sonar:sonar -Pdocker-compose'
                    }
                }
            }
        }
        stage("Get Sonar Quality Gate") {
            steps {
                timeout(time: 1, unit: 'HOURS') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
        stage('Build and Push Docker Image') {
            steps {
                sh 'gcloud auth configure-docker'
                withMaven {
                    sh 'mvn docker:build docker:push -Pdocker-compose'
                }
                sh("gcloud container images add-tag ${IMAGE_TAG} ${docker_repo}/${product_name}-${module_name}:${env.BRANCH_NAME}-latest")
            }
        }
        stage('Check Requirements and Deployments') {
            steps {
                dir('./infrastructure/gcp/') {
                    build job: 'cessda.cvs.deploy/master', parameters: [string(name: 'gui_image_tag', value: "${IMAGE_TAG}"), string(name: 'module', value: 'gui')], wait: false
                }
            }
        }
	}
}
