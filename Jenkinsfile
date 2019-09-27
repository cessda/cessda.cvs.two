pipeline {
    options {
        ansiColor('xterm')
        buildDiscarder logRotator(artifactNumToKeepStr: '5', numToKeepStr: '10')
    }

	environment {
        product_name = "cvs"
        module_name = "gui"
        IMAGE_TAG = "${docker_repo}/${product_name}-${module_name}:${env.BRANCH_NAME}-${env.BUILD_NUMBER}"
	}

    agent {
        label 'jnlp-himem'
    }

	stages {
        // Building on master
		stage('Build Project') {
			steps {
                withMaven {
                    sh "mvn clean deploy -Pdocker-compose -DbuildNumber=${env.BUILD_NUMBER}"
				}
			}
            when { branch 'master' }
		}
        // Not running on master - test only (for PRs and integration branches)
		stage('Test Project') {
			steps {
                withMaven {
                    sh 'mvn clean test -Pdocker-compose'					
				}
			}
            when { not { branch 'master' } }
		}
        stage('Run Sonar Scan') {
            steps {
                withSonarQubeEnv('cessda-sonar') {
                    nodejs('node') {
                        withMaven {
                            sh "mvn sonar:sonar -Pdocker-compose -DbuildNumber=${env.BUILD_NUMBER}"
                        }
                    }
                }
            }
            when { branch 'master' }
        }
        stage("Get Sonar Quality Gate") {
            steps {
                timeout(time: 1, unit: 'HOURS') {
                    waitForQualityGate abortPipeline: false
                }
            }
            when { branch 'master' }
        }
        stage('Build and Push Docker Image') {
            steps {
                sh 'gcloud auth configure-docker'
                withMaven {
                    sh "mvn docker:build docker:push -Pdocker-compose -DbuildNumber=${env.BUILD_NUMBER} -Dimage_tag=${IMAGE_TAG}"
                }
                sh "gcloud container images add-tag ${IMAGE_TAG} ${docker_repo}/${product_name}-${module_name}:${env.BRANCH_NAME}-latest"
            }
            when { branch 'master' }
        }
        stage('Check Requirements and Deployments') {
            steps {
                dir('./infrastructure/gcp/') {
                    build job: 'cessda.cvs.deploy/master', parameters: [string(name: 'gui_image_tag', value: "${IMAGE_TAG}"), string(name: 'module', value: 'gui')], wait: false
                }
            }
            when { branch 'master' }
        }
	}
    post {
        failure {
            emailext body: '${DEFAULT_CONTENT}', subject: '${DEFAULT_SUBJECT}', to: 'Sigit.Nugraha@gesis.org, Claus-Peter.Klas@gesis.org'
        }
    }
}
