pipeline {
    agent any
    stages {
        stage('Source') {
            steps {
                git 'https://github.com/alex95mf/unir-cicd.git'
            }
        }
        stage('Build') {
            steps {
                echo 'Building stage!'
                sh 'make build'
            }
        }
        stage('Unit tests') {
            steps {
                sh 'make test-unit'
                archiveArtifacts artifacts: 'results/*.xml'
            }
        }
        stage('API tests') {
            steps {
                echo 'Running API tests!'
                sh 'make test-api'
                archiveArtifacts artifacts: 'results/*.xml'
            }
        }
        stage('E2E tests') {
            steps {
                echo 'Running E2E tests!'
                sh 'make test-e2e'
                archiveArtifacts artifacts: 'results/*.xml'
            }
        }
    }
    post {
        always {
            junit 'results/*_result.xml'
            junit 'results/*_result.xml'
            junit 'results/*_result.xml'
            cleanWs()
        }
        failure {
            script {
                def jobName = env.JOB_NAME
                def buildNumber = env.BUILD_NUMBER
                echo "Sending failure notification email for Job: ${jobName}, Build Number: ${buildNumber}"
                emailext body: "Error encontrado en el pipeline",
                         subject: "Error en el pipeline",
                         to: "alex95mf@gmail.com"
            }
        }
    }
}
