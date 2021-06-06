pipeline {
    agent any
     triggers {
        pollSCM "* * * * *"
     }
    environment {
        GITHUB = credentials('gitHubCredentials')

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
                                    app = docker.build("akshaygirpunje/sagan")
                                }
                            }
                }
                stage('Push Docker Image') {
                            steps {
                                echo '=== Pushing simple-java-maven-app Docker Image ==='
                                script {
                                    GIT_COMMIT_HASH = sh (script: "git log -n 1 --pretty=format:'%H'", returnStdout: true)
                                    SHORT_COMMIT = "${GIT_COMMIT_HASH[0..7]}"
                                    docker.withRegistry('https://registry.hub.docker.com', 'dockerHubCredentials') {
                                        app.push("$SHORT_COMMIT")
                                        app.push("latest")
                                    }
                                }
                            }
                }
                stage('Remove local images') {
                            steps {
                                echo '=== Delete the local docker images ==='
                                sh("docker rmi -f akshaygirpunje/sagan:latest || :")
                                sh("docker rmi -f akshaygirpunje/sagan:$SHORT_COMMIT || :")
                }
            }
    }
}
