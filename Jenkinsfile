pipeline {
    agent any
     triggers {
        pollSCM "* * * * *"
     }
    environment {
        GITHUB = credentials('gitHubCredentials')
	registry = "akshaygirpunje/sagan-deploy"
        registryCredential = "dockerHubCredentials"
	dockerImage = " "

    }

    stages {
        stage('Checkout') {
            steps{
                checkout scm
            }
        }
        stage('Build') {
            steps {
                sh './gradlew build'
            }
        }

        stage('Build Docker Image') {

                            steps {
                                echo '=== Building sagan Docker Image ==='
                                script {
                                    app = docker.build registry + ":$BUILD_NUMBER"
                                }
                            }
                }
                stage('Push Docker Image') {
                            steps {
                                echo '=== Pushing simple-java-maven-app Docker Image ==='
                                script {
			          docker.withRegistry( '', registryCredential ) {
				       dockerImage.push()
				          }
					}
                            }
                }
                stage('Remove local images') {
                                             steps{
                                             sh "docker rmi $registry:$BUILD_NUMBER"
                                               }
									}
    }
}
