pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                // Checkout the code from your Git repository
                checkout scm
            }
        }

        stage('Build JAR') {
            steps {
                // Build the project using Maven
                sh 'mvn clean package -DskipTests -T 3C'
            }
        }

        stage('Build Docker Image') {
            steps {
                // Build the Docker image
                script {
                    def dockerImage = docker.build('secureweather:latest')
                }
            }
        }

         stage('Deploy to Kubernetes') {
                    steps {
                        // Apply the Kubernetes YAML files
                        sh 'kubectl apply -f secureweather.deployment.yaml'
                        sh 'kubectl apply -f secureweather.service.yaml'
                    }
                }
            }
    }

    post {
        always {
            // Clean up workspace
            cleanWs()
        }
        success {
            // Notify team of success, perhaps through email or a chat service
            emailext body: 'Build succeeded: ${BUILD_URL}',
                     subject: 'Build succeeded for SecureWeather',
                     to: 'Bukhari.kibuka7@gmail.com'
            echo 'Build succeeded!'
        }
        failure {
            // Notify team of failure, perhaps through email or a chat service
            emailext body: 'Build failed: ${BUILD_URL}',
                     subject: 'Build failed for SecureWeather',
                     to: 'Bukhari.kibuka7@gmail.com'
            echo 'Build failed!'

            // Optionally, save artifacts or logs for debugging
            archiveArtifacts artifacts: '**/target/*.log', onlyIfSuccessful: false
        }
    }

}
